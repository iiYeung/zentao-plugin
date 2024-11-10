package com.iiyeung.plugin.zentao.extension.zentao

import com.google.gson.Gson
import com.iiyeung.plugin.zentao.common.Constant
import com.iiyeung.plugin.zentao.extension.zentao.model.*
import com.iiyeung.plugin.zentao.util.ZentaoResponseUtil
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.tasks.Task
import com.intellij.tasks.impl.BaseRepository
import com.intellij.tasks.impl.httpclient.NewBaseRepositoryImpl
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.xmlb.annotations.Tag
import kotlinx.serialization.json.Json
import org.apache.http.HttpRequestInterceptor
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-01-18 22:32:15
 */
@Tag("Zentao")
class ZentaoRepository : NewBaseRepositoryImpl {
    private var token: String? = null
    private var myCurrentProduct: ZentaoProduct? = null
    private var myProducts: List<ZentaoProduct>? = null

    constructor() : super()

    constructor(other: ZentaoRepository) : super(other) {
        myCurrentProduct = other.myCurrentProduct
    }

    constructor(type: ZentaoRepositoryType) : super(type)

    override fun findTask(s: String): Task? {
        return null
    }

    override fun clone(): BaseRepository {
        return ZentaoRepository(this)
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Throws(Exception::class)
    override fun getIssues(query: String?, offset: Int, limit: Int, withClosed: Boolean): Array<out ZentaoTask> {
        val bugs = fetchBugs((offset / limit) + 1, limit, !withClosed)
        return ContainerUtil.map2Array(bugs, ZentaoTask::class.java) { issue -> ZentaoTask(this, issue) }
    }

    @Throws(Exception::class)
    private fun fetchBugs(pageNumber: Int, pageSize: Int, openedOnly: Boolean): List<ZentaoBug> {
        val bugs = mutableListOf<ZentaoBug>()
        ensureProjectsDiscovered()
        if (myCurrentProduct != null) {
            bugs.addAll(getBugs(getRestApiUrl("products", myCurrentProduct?.id, "bugs")))
        } else {
            myProducts?.forEach { product ->
                bugs.addAll(getBugs(getRestApiUrl("products", product.id, "bugs")))
            }
        }
        return bugs
    }


    private fun getBugs(url: String): List<ZentaoBug> {
        val urlBuild = URIBuilder(url).addParameter("limit", "1000").build()
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(Gson(), ZentaoBugPage::class.java)
        var page: ZentaoBugPage?
        try {
            page = httpClient.execute(HttpGet(urlBuild), handler)
        } catch (e: Exception) {
            thisLogger().info("fetch product error. Response: $e")
            fetchToken()
            page = httpClient.execute(HttpGet(urlBuild), handler)
        }
        return if (page == null || page.bugs.isEmpty()) {
            emptyList()
        } else {
            page.bugs.filter { i -> i.assignedTo?.account == myUsername }.toList()
        }
    }

    @NotNull
    fun getProducts(): List<ZentaoProduct> {
        return try {
            ensureProjectsDiscovered()
            myProducts.orEmpty()
        } catch (ignored: Exception) {
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
        return "/api.php/v1"
    }

    @Nullable
    override fun createRequestInterceptor(): HttpRequestInterceptor? {
        return if (token == null) {
            null
        } else HttpRequestInterceptor { request, _ -> request.addHeader(Constant.TOKEN_KEY.value, token) }
    }

    @NotNull
    @Throws(Exception::class)
    fun fetchProducts(): List<ZentaoProduct> {
        generateToken()

        val urlBuild = URIBuilder(getRestApiUrl("products")).addParameter("limit", "1000").build()
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(gson = Gson(), ZentaoProductPage::class.java)
        var page: ZentaoProductPage?
        try {
            page = httpClient.execute(HttpGet(urlBuild), handler)
        } catch (e: Exception) {
            thisLogger().info("fetch product error. Response: $e")
            fetchToken()
            page = httpClient.execute(HttpGet(urlBuild), handler)
        }
        thisLogger().info("fetch product Successful. Response: $page")
        return if (page == null || page.products.isEmpty()) {
            emptyList()
        } else {
            page.products
        }
    }

    @Throws(Exception::class)
    fun generateToken() {
        if (token != null) {
            return
        }

        fetchToken()
    }

    @Throws(Exception::class)
    private fun fetchToken() {
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(Gson(), ZentaoToken::class.java)
        val httpPost = HttpPost(URIBuilder(getRestApiUrl("tokens")).build())
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8")
        httpPost.entity =
            StringEntity(Gson().toJson(ZentaoLogin(myUsername, myPassword)), ContentType.APPLICATION_JSON)
        var result: ZentaoToken? = null
        result = httpClient.execute(httpPost, handler)
        thisLogger().info("fetch token Successful. Response: $result")
        setToken(result?.token)
    }

    fun getCurrentProduct(): ZentaoProduct? {
        return myCurrentProduct
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    override fun createCancellableConnection(): CancellableConnection? {
        return object : CancellableConnection() {
            @Throws(Exception::class)
            override fun doTest() {
                getUser()
            }

            override fun cancel() {
                TODO("Not yet implemented")
            }
        }
    }

    fun getUser() {
        fetchToken()
        val urlBuild = URIBuilder(getRestApiUrl("user")).build()
        val handler = ZentaoResponseUtil.GsonSingleObjectDeserializer(Gson(), ZentaoUserDetail::class.java)
        var user = httpClient.execute(HttpGet(urlBuild), handler)
        thisLogger().info("fetch user Successful. Response: $user")
    }
}