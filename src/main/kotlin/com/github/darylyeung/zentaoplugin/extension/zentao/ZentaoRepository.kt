package com.github.darylyeung.zentaoplugin.extension.zentao

import com.github.darylyeung.zentaoplugin.common.Constant
import com.github.darylyeung.zentaoplugin.extension.zentao.model.*
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.tasks.Task
import com.intellij.tasks.impl.BaseRepository
import com.intellij.tasks.impl.httpclient.NewBaseRepositoryImpl
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.xmlb.annotations.Tag
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.http.HttpRequestInterceptor
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * @author Yeung
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
        ensureProjectsDiscovered()
        if (myCurrentProduct != null) {
            return getBugs(getRestApiUrl("products", myCurrentProduct?.id, "bugs"))
        } else {
            val bugs = mutableListOf<ZentaoBug>()
            myProducts?.forEach { product ->
                bugs.plus(getBugs(getRestApiUrl("products", product.id, "bugs")))
            }
            return bugs
        }
    }


    private fun getBugs(url: String): List<ZentaoBug> {
        val response = OkHttpClient().newCall(
            Request.Builder().url(url)
                .header(Constant.TOKEN_KEY.value, token.toString()).build()
        ).execute()
        if (!response.isSuccessful) {
            return emptyList()
        }
        val result = response.body?.string().toString()
        thisLogger().info("fetch bug Successful. Response: $result")
        val page = json.decodeFromString<ZentaoBugPage>(result)
        return if (page.bugs.isEmpty()) {
            emptyList()
        } else {
            page.bugs.filter { i -> i.assignedTo.account == myUsername }.toList()
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

        val response = OkHttpClient().newCall(
            Request.Builder()
                .url(getRestApiUrl("products"))
                .header(Constant.TOKEN_KEY.value, token.toString()).build()
        ).execute()
        if (!response.isSuccessful) {
            return emptyList()
        }
        val result = response.body?.string().toString()
        thisLogger().info("fetch product Successful. Response: $result")
        val page = json.decodeFromString<ZentaoProductPage>(result)
        return if (page.products.isEmpty()) {
            emptyList()
        } else {
            page.products
        }
    }

    @Throws(Exception::class)
    fun generateToken() {
        val paramJson = "{\"account\":\"$myUsername\",\"password\":\"$myPassword\"}"
        val body = paramJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(getRestApiUrl("tokens"))
            .header("Content-Type", "application/json; charset=utf-8")
            .post(body).build()
        val response = OkHttpClient().newCall(request).execute()
        if (!response.isSuccessful) {
            return
        }
        val result = response.body?.string().toString()
        thisLogger().info("fetch token Successful. Response: $result")
        if (result.isEmpty()) {
            return
        }
        val token = json.decodeFromString<ZentaoToken>(result)
        setToken(token.token)
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
}