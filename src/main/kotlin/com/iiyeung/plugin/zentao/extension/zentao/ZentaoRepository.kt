package com.iiyeung.plugin.zentao.extension.zentao

import com.google.gson.Gson
import com.iiyeung.plugin.zentao.common.ApiConfig
import com.iiyeung.plugin.zentao.common.Constants
import com.iiyeung.plugin.zentao.common.UnauthorizedException
import com.iiyeung.plugin.zentao.extension.zentao.model.*
import com.iiyeung.plugin.zentao.util.ZentaoResponseUtil
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.tasks.Task
import com.intellij.tasks.impl.BaseRepository
import com.intellij.tasks.impl.httpclient.NewBaseRepositoryImpl
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.xmlb.annotations.Tag
import org.apache.http.HttpRequestInterceptor
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-01-18 22:32:15
 */
@Tag("Zentao")
class ZentaoRepository : NewBaseRepositoryImpl {
    private var myCurrentProduct: ZentaoProduct? = null
    private var myProducts: List<ZentaoProduct>? = null

    // Prevent infinite recursion while generating token
    @Volatile
    private var isGeneratingToken = false

    // Backoff control for consecutive bad credentials: avoid repeatedly requesting with the same wrong username/password
    @Volatile
    private var lastFailedCredentialSignature: String? = null

    @Volatile
    private var lastFailedAtMillis: Long = 0L

    // Backoff duration (do not request token again for the same invalid credentials within this period)
    private val invalidCredentialCooldownMillis: Long = TimeUnit.MINUTES.toMillis(1)

    constructor() : super()

    constructor(other: ZentaoRepository) : super(other) {
        myCurrentProduct = other.myCurrentProduct
    }

    constructor(type: ZentaoRepositoryType) : super(type)

    // ========== Token management ==========

    /**
     * Build credential attributes for the Token
     */
    private fun getTokenCredentialAttributes(): CredentialAttributes {
        return CredentialAttributes(
            "ZentaoToken_${url}_${myUsername}",
            myUsername ?: "unknown"
        )
    }

    /**
     * Get token from secure storage
     */
    private fun getStoredToken(): String? {
        // Access PasswordSafe directly; avoid unnecessary thread hop and blocking .get()
        val credentials = PasswordSafe.instance.get(getTokenCredentialAttributes())
        return credentials?.getPasswordAsString()
    }

    /**
     * Store token into secure storage
     */
    private fun storeToken(token: String) {
        // Execute in pooled thread; no need to bounce through EDT
        ApplicationManager.getApplication().executeOnPooledThread {
            val credentials = Credentials(myUsername, token)
            PasswordSafe.instance.set(getTokenCredentialAttributes(), credentials)
        }
    }

    /**
     * Clear stored token
     */
    private fun clearStoredToken() {
        // Execute in pooled thread; avoid scheduling via EDT
        ApplicationManager.getApplication().executeOnPooledThread {
            PasswordSafe.instance.set(getTokenCredentialAttributes(), null)
        }
    }

    /**
     * Get the current valid token
     */
    fun getCurrentToken(): String? {
        return getStoredToken()
    }

    /**
     * Generate and persist a new token
     */
    fun generateAndStoreToken(force: Boolean = false): String? {
        // Prevent recursive invocation
        if (isGeneratingToken) {
            thisLogger().warn("Token generation already in progress, skipping recursive call")
            return null
        }

        val currentSig = credentialSignature()
        val now = System.currentTimeMillis()
        if (!force) {
            if (lastFailedCredentialSignature == currentSig &&
                now - lastFailedAtMillis < invalidCredentialCooldownMillis
            ) {
                thisLogger().warn("Skip token generation due to recent failed credentials; wait for cooldown or change credentials")
                return null
            }
        }

        return try {
            isGeneratingToken = true
            val token = ProgressManager.getInstance().runProcessWithProgressSynchronously<String?, Exception>(
                {
                    fetchTokenFromServer()
                },
                "Generating token",
                false,
                null
            )

            if (token != null) {
                storeToken(token)
                // Clear failure markers after success
                lastFailedCredentialSignature = null
                lastFailedAtMillis = 0L
            }
            token
        } catch (e: Exception) {
            thisLogger().warn("Failed to generate token", e)
            // On error, clear cached token (including persistent storage) to avoid using an invalid/expired token
            try {
                clearStoredToken()
                thisLogger().warn(ZentaoResponseUtil.withErrorPrefix("Token request failed, cleared cached token"))
            } catch (t: Throwable) {
                thisLogger().warn("Failed to clear cached token after token request failure", t)
            }
            // Record as failed credentials and enter backoff window
            lastFailedCredentialSignature = currentSig
            lastFailedAtMillis = System.currentTimeMillis()
            // Rethrow the original exception so the caller can get the exact API error message
            throw e
        } finally {
            isGeneratingToken = false
        }
    }

    /**
     * Fetch token from server
     */
    @Throws(Exception::class)
    private fun fetchTokenFromServer(): String? {
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(Gson(), ZentaoToken::class.java)
        val httpPost = HttpPost(URIBuilder(getRestApiUrl(ApiConfig.Endpoints.TOKENS)).build())
        httpPost.addHeader(ApiConfig.Headers.CONTENT_TYPE, ApiConfig.ContentTypes.JSON_UTF8)
        httpPost.entity = StringEntity(
            Gson().toJson(ZentaoLogin(myUsername, myPassword)),
            ContentType.APPLICATION_JSON
        )

        val result: ZentaoToken? = httpClient.execute(httpPost, handler)
        thisLogger().info("Fetch token successful.")
        return result?.token
    }

    /**
     * Generate a non-plain signature based on current username/password to detect credential changes
     */
    private fun credentialSignature(): String {
        val user = myUsername ?: ""
        val pwd = myPassword ?: ""
        val input = "$user:$pwd"
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
            bytes.joinToString(separator = "") { b -> "%02x".format(b) }
        } catch (t: Throwable) {
            // Fall back to plain concatenation (memory only), do not log
            input
        }
    }

    /**
     * Refresh token
     */
    private fun refreshTokenIfNeeded(): Boolean {
        return try {
            clearStoredToken()
            val newToken = generateAndStoreToken()
            newToken != null
        } catch (e: Exception) {
            thisLogger().warn("Failed to refresh token", e)
            false
        }
    }

    // ========== HTTP request interceptor ==========

    override fun createRequestInterceptor(): HttpRequestInterceptor {
        return HttpRequestInterceptor { request, _ ->
            // If generating token, skip adding token header to avoid recursion
            if (isGeneratingToken) {
                return@HttpRequestInterceptor
            }

            val token = getCurrentToken()
            if (token != null) {
                request.addHeader(Constants.TOKEN_KEY, token)
            } else {
                // Only try to generate a new token if we are not in the generation process
                val newToken = generateAndStoreToken()
                if (newToken != null) {
                    request.addHeader(Constants.TOKEN_KEY, newToken)
                }
            }
        }
    }

    // ========== Connection test ==========

    override fun createCancellableConnection(): CancellableConnection {
        return object : CancellableConnection() {
            @Throws(Exception::class)
            override fun doTest() {
                try {
                    // During connection test, always generate a fresh token from current username/password
                    // to avoid using outdated cached token
                    clearStoredToken()
                    val newToken = generateAndStoreToken(force = true)

                    // If token cannot be generated from current input, fail immediately (keep server-side error message)
                    if (newToken.isNullOrBlank()) {
                        throw IllegalStateException(ZentaoResponseUtil.withErrorPrefix("Failed to generate token with current credentials"))
                    }
                    // Use the newly generated token to validate user info, no retries
                    getUser()
                } catch (e: Exception) {
                    // Connection test: do not show notification; throw a single prefixed IllegalStateException
                    val message = ZentaoResponseUtil.withErrorPrefix(e.message ?: e.toString())
                    // For 401 still log only (no notification)
                    if (e is UnauthorizedException) {
                        thisLogger().warn(message)
                    }
                    throw IllegalStateException(message)
                }
            }

            override fun cancel() {
                // Implement cancel logic
            }
        }
    }

    // ========== API methods (no retry mechanism) ==========

    override fun findTask(s: String): Task? {
        return null
    }

    override fun clone(): BaseRepository {
        return ZentaoRepository(this)
    }

    @Throws(Exception::class)
    override fun getIssues(query: String?, offset: Int, limit: Int, withClosed: Boolean): Array<out ZentaoTask> {
        val bugs = fetchBugs()
        return ContainerUtil.map2Array(bugs, ZentaoTask::class.java) { issue -> ZentaoTask(this, issue) }
    }

    @Throws(Exception::class)
    private fun fetchBugs(): List<ZentaoBug> {
        val bugs = mutableListOf<ZentaoBug>()
        ensureProjectsDiscovered()
        if (myCurrentProduct != null) {
            bugs.addAll(getBugs(getRestApiUrl(ApiConfig.Endpoints.PRODUCTS, myCurrentProduct?.id, ApiConfig.Endpoints.BUGS)))
        } else {
            myProducts?.forEach { product ->
                bugs.addAll(getBugs(getRestApiUrl(ApiConfig.Endpoints.PRODUCTS, product.id, ApiConfig.Endpoints.BUGS)))
            }
        }
        return bugs
    }

    private fun getBugs(url: String): List<ZentaoBug> {
        val urlBuild = URIBuilder(url).addParameter("limit", "1000").build()
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(Gson(), ZentaoBugPage::class.java)

        return try {
            val page: ZentaoBugPage? = httpClient.execute(HttpGet(urlBuild), handler)
            extractBugsFromPage(page)
        } catch (e: Exception) {
            // No retry; do not notify for 401 (log only); notify and rethrow for other errors
            if (e is UnauthorizedException) {
                thisLogger().warn(ZentaoResponseUtil.withErrorPrefix(e.message ?: e.toString()))
            } else {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Zentao Notifications")
                    .createNotification(ZentaoResponseUtil.withErrorPrefix(e.message ?: e.toString()), NotificationType.ERROR)
                    .notify(null)
            }
            throw e
        }
    }

    private fun extractBugsFromPage(page: ZentaoBugPage?): List<ZentaoBug> {
        return if (page == null || page.bugs.isEmpty()) {
            emptyList()
        } else {
            page.bugs.filter { i -> i.assignedTo.account == myUsername }.toList()
        }
    }

    fun getProducts(): List<ZentaoProduct> {
        // Do not swallow exceptions: ensure callers receive failures
        ensureProjectsDiscovered()
        return myProducts.orEmpty()
    }

    @Throws(Exception::class)
    private fun ensureProjectsDiscovered() {
        if (myProducts == null) {
            myProducts = fetchProducts()
        }
    }

    override fun getRestApiPathPrefix(): String {
        return ApiConfig.Paths.REST_PREFIX
    }

    @Throws(Exception::class)
    fun fetchProducts(): List<ZentaoProduct> {
        val urlBuild = URIBuilder(getRestApiUrl(ApiConfig.Endpoints.PRODUCTS)).addParameter("limit", "1000").build()
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(gson = Gson(), ZentaoProductPage::class.java)

        return try {
            val page: ZentaoProductPage? = httpClient.execute(HttpGet(urlBuild), handler)
            extractProductsFromPage(page)
        } catch (e: Exception) {
            // No retry; do not notify for 401 (log only); notify and rethrow for other errors
            if (e is UnauthorizedException) {
                thisLogger().warn(ZentaoResponseUtil.withErrorPrefix(e.message ?: e.toString()))
            } else {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Zentao Notifications")
                    .createNotification(ZentaoResponseUtil.withErrorPrefix(e.message ?: e.toString()), NotificationType.ERROR)
                    .notify(null)
            }
            throw e
        }
    }

    private fun extractProductsFromPage(page: ZentaoProductPage?): List<ZentaoProduct> {
        thisLogger().info("Fetch products successful. Response: $page")
        return if (page == null || page.products.isEmpty()) {
            emptyList()
        } else {
            page.products
        }
    }

    fun getCurrentProduct(): ZentaoProduct? {
        return myCurrentProduct
    }

    fun getUser() {
        val urlBuild = URIBuilder(getRestApiUrl(ApiConfig.Endpoints.USER)).build()
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(Gson(), ZentaoUserDetail::class.java)

        try {
            val user = httpClient.execute(HttpGet(urlBuild), handler)
            thisLogger().info("Fetch user successful. Response: $user")
        } catch (e: Exception) {
            thisLogger().warn("Failed to fetch user", e)
            throw e
        }
    }

}