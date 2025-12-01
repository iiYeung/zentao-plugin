package com.iiyeung.plugin.zentao.extension.zentao

import com.google.gson.Gson
import com.iiyeung.plugin.zentao.common.ApiConfig
import com.iiyeung.plugin.zentao.common.Constants
import com.iiyeung.plugin.zentao.extension.zentao.model.*
import com.iiyeung.plugin.zentao.util.ZentaoResponseUtil
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
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

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-01-18 22:32:15
 */
@Tag("Zentao")
class ZentaoRepository : NewBaseRepositoryImpl {
    private var myCurrentProduct: ZentaoProduct? = null
    private var myProducts: List<ZentaoProduct>? = null

    // 用于防止token生成时的无限递归
    @Volatile
    private var isGeneratingToken = false

    constructor() : super()

    constructor(other: ZentaoRepository) : super(other) {
        myCurrentProduct = other.myCurrentProduct
    }

    constructor(type: ZentaoRepositoryType) : super(type)

    // ========== Token管理相关方法 ==========

    /**
     * 获取Token的凭据属性
     */
    private fun getTokenCredentialAttributes(): CredentialAttributes {
        return CredentialAttributes(
            "ZentaoToken_${url}_${myUsername}",
            myUsername ?: "unknown"
        )
    }

    /**
     * 从安全存储中获取Token
     */
    private fun getStoredToken(): String? {
        // Access PasswordSafe directly; avoid unnecessary thread hop and blocking .get()
        val credentials = PasswordSafe.instance.get(getTokenCredentialAttributes())
        return credentials?.getPasswordAsString()
    }

    /**
     * 将Token存储到安全存储中
     */
    private fun storeToken(token: String) {
        // Execute in pooled thread; no need to bounce through EDT
        ApplicationManager.getApplication().executeOnPooledThread {
            val credentials = Credentials(myUsername, token)
            PasswordSafe.instance.set(getTokenCredentialAttributes(), credentials)
        }
    }

    /**
     * 清除存储的Token
     */
    private fun clearStoredToken() {
        // Execute in pooled thread; avoid scheduling via EDT
        ApplicationManager.getApplication().executeOnPooledThread {
            PasswordSafe.instance.set(getTokenCredentialAttributes(), null)
        }
    }

    /**
     * 获取当前有效的Token
     */
    fun getCurrentToken(): String? {
        return getStoredToken()
    }

    /**
     * 生成并存储新Token
     */
    fun generateAndStoreToken(): String? {
        // 防止递归调用
        if (isGeneratingToken) {
            thisLogger().warn("Token generation already in progress, skipping recursive call")
            return null
        }

        return try {
            isGeneratingToken = true
            val token = ProgressManager.getInstance().runProcessWithProgressSynchronously<String?, Exception>(
                {
                    fetchTokenFromServer()
                },
                "正在生成 Token",
                false,
                null
            )

            if (token != null) {
                storeToken(token)
            }
            token
        } catch (e: Exception) {
            thisLogger().warn("Failed to generate token", e)
            null
        } finally {
            isGeneratingToken = false
        }
    }

    /**
     * 从服务器获取Token
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
     * 刷新Token
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

    // ========== HTTP请求拦截器 ==========

    override fun createRequestInterceptor(): HttpRequestInterceptor {
        return HttpRequestInterceptor { request, _ ->
            // 如果正在生成token，跳过添加token header以避免递归
            if (isGeneratingToken) {
                return@HttpRequestInterceptor
            }

            val token = getCurrentToken()
            if (token != null) {
                request.addHeader(Constants.TOKEN_KEY, token)
            } else {
                // 只有在不是token生成过程中才尝试生成新token
                val newToken = generateAndStoreToken()
                if (newToken != null) {
                    request.addHeader(Constants.TOKEN_KEY, newToken)
                }
            }
        }
    }

    // ========== 连接测试 ==========

    override fun createCancellableConnection(): CancellableConnection {
        return object : CancellableConnection() {
            @Throws(Exception::class)
            override fun doTest() {
                try {
                    getUser()
                } catch (e: Exception) {
                    // Token可能已过期，尝试刷新
                    if (refreshTokenIfNeeded()) {
                        getUser()
                    } else {
                        throw e
                    }
                }
            }

            override fun cancel() {
                // 实现取消逻辑
            }
        }
    }

    // ========== API调用方法（带重试机制） ==========

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
            bugs.addAll(getBugsWithRetry(getRestApiUrl(ApiConfig.Endpoints.PRODUCTS, myCurrentProduct?.id, ApiConfig.Endpoints.BUGS)))
        } else {
            myProducts?.forEach { product ->
                bugs.addAll(getBugsWithRetry(getRestApiUrl(ApiConfig.Endpoints.PRODUCTS, product.id, ApiConfig.Endpoints.BUGS)))
            }
        }
        return bugs
    }

    private fun getBugsWithRetry(url: String): List<ZentaoBug> {
        val urlBuild = URIBuilder(url).addParameter("limit", "1000").build()
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(Gson(), ZentaoBugPage::class.java)

        return try {
            val page: ZentaoBugPage? = httpClient.execute(HttpGet(urlBuild), handler)
            extractBugsFromPage(page)
        } catch (e: Exception) {
            thisLogger().info("Fetch bugs failed, attempting token refresh. Error: $e")
            if (refreshTokenIfNeeded()) {
                val page: ZentaoBugPage? = httpClient.execute(HttpGet(urlBuild), handler)
                extractBugsFromPage(page)
            } else {
                emptyList()
            }
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
        return try {
            ensureProjectsDiscovered()
            myProducts.orEmpty()
        } catch (_: Exception) {
            emptyList()
        }
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
            thisLogger().info("Fetch products failed, attempting token refresh. Error: $e")
            if (refreshTokenIfNeeded()) {
                val page: ZentaoProductPage? = httpClient.execute(HttpGet(urlBuild), handler)
                extractProductsFromPage(page)
            } else {
                emptyList()
            }
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