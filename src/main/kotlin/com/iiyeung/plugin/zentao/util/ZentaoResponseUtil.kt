package com.iiyeung.plugin.zentao.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.iiyeung.plugin.zentao.ZentaoBundle
import com.iiyeung.plugin.zentao.common.RequestFailedException
import com.iiyeung.plugin.zentao.common.UnauthorizedException
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.Producer
import org.apache.commons.httpclient.HttpStatus
import org.apache.http.HttpResponse
import org.apache.http.client.ResponseHandler
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.jetbrains.annotations.ApiStatus
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.function.Function
import java.util.function.IntPredicate

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-11-10 16:41:19
 */
object ZentaoResponseUtil {
    val LOG: Logger = Logger.getInstance(ZentaoResponseUtil::class.java)

    val DEFAULT_CHARSET: Charset = StandardCharsets.UTF_8

    private const val ERROR_PREFIX: String = "zentao-plugin: "

    fun withErrorPrefix(message: String?): String {
        val msg = (message ?: "").trim()
        return if (msg.startsWith(ERROR_PREFIX)) msg else ERROR_PREFIX + msg
    }

    @Throws(IOException::class)
    fun getResponseContentAsReader(response: HttpResponse): Reader {
        // Respect Content-Type charset; fall back to UTF-8
        val entity = response.entity
        val ct = ContentType.getOrDefault(entity)
        val charset = ct.charset ?: DEFAULT_CHARSET
        return InputStreamReader(entity.content, charset)
    }

    @Throws(IOException::class)
    fun getResponseContentAsString(response: HttpResponse): String {
        return EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET)
    }

    fun messageForStatusCode(statusCode: Int): String {
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            return ZentaoBundle.message("failure.login")
        } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
            return ZentaoBundle.message("failure.permissions")
        }
        return ZentaoBundle.message("failure.http.error", statusCode, HttpStatus.getStatusText(statusCode))
    }

    @ApiStatus.Internal
    class JsonResponseHandlerBuilder private constructor(gson: Gson) {
        private val myGson: Gson
        var mySuccessChecker = IntPredicate { code: Int -> code / 100 == 2 }
        var myIgnoreChecker = IntPredicate { code: Int -> false }
        var myErrorExtractor: Function<HttpResponse?, out RequestFailedException?>? = null

        init {
            myGson = gson
        }

        fun successCode(predicate: IntPredicate): JsonResponseHandlerBuilder {
            mySuccessChecker = predicate
            return this
        }

        fun ignoredCode(predicate: IntPredicate): JsonResponseHandlerBuilder {
            myIgnoreChecker = predicate
            return this
        }

        fun errorHandler(handler: Function<HttpResponse?, out RequestFailedException?>): JsonResponseHandlerBuilder {
            myErrorExtractor = handler
            return this
        }

        fun <T> toSingleObject(cls: Class<T?>): ResponseHandler<T?> {
            return GsonResponseHandler<T?>(
                this,
                Function { s: String? -> myGson.fromJson<T?>(s, cls) },
                Function { r: Reader? -> myGson.fromJson<T?>(r, cls) },
                Producer { null })
        }

        fun <T> toMultipleObjects(typeToken: TypeToken<MutableList<T?>?>): ResponseHandler<MutableList<T?>?> {
            return GsonResponseHandler<MutableList<T?>?>(
                this,
                Function { s: String? -> myGson.fromJson<MutableList<T?>?>(s, typeToken.getType()) },
                Function { r: Reader? -> myGson.fromJson<MutableList<T?>?>(r, typeToken.getType()) },
                Producer { mutableListOf() })
        }

        fun toNothing(): ResponseHandler<Void?> {
            return GsonResponseHandler<Void?>(
                this,
                Function { s: String? -> null },
                Function { r: Reader? -> null },
                Producer { null })
        }

        companion object {
            fun fromGson(gson: Gson): JsonResponseHandlerBuilder {
                return JsonResponseHandlerBuilder(gson)
            }
        }
    }

    open class GsonResponseHandler<T>(
        builder: JsonResponseHandlerBuilder,
        fromString: Function<in String?, out T?>,
        fromReader: Function<in Reader?, out T?>,
        fallbackValue: Producer<out T?>
    ) : ResponseHandler<T?> {
        private val myBuilder: JsonResponseHandlerBuilder
        private val myFromString: Function<in String?, out T?>
        private val myFromReader: Function<in Reader?, out T?>
        private val myFallbackValue: Producer<out T?>

        init {
            myBuilder = builder
            myFromString = fromString
            myFromReader = fromReader
            myFallbackValue = fallbackValue
        }

        @Throws(IOException::class)
        override fun handleResponse(response: HttpResponse): T? {
            val statusCode = response.getStatusLine().getStatusCode()
            if (!myBuilder.mySuccessChecker.test(statusCode)) {
                // Special handling for 401 Unauthorized: do not notify user, log only and throw
                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    val content: String = runCatching { getResponseContentAsString(response) }.getOrDefault("")
                    val message = extractErrorMessage(content, messageForStatusCode(statusCode))
                    // Only log for 401
                    LOG.warn(withErrorPrefix(message))
                    throw UnauthorizedException(message)
                }
                if (myBuilder.myIgnoreChecker.test(statusCode)) {
                    return myFallbackValue.produce()
                }
                if (myBuilder.myErrorExtractor != null) {
                    val exception: RequestFailedException? = myBuilder.myErrorExtractor!!.apply(response)
                    if (exception != null) {
                        throw exception
                    }
                }
                val content: String = runCatching { getResponseContentAsString(response) }.getOrDefault("")
                val message = extractErrorMessage(content, messageForStatusCode(statusCode))
                notifyError(message)
                throw RequestFailedException(message)
            }
            try {
                if (LOG.isDebugEnabled()) {
                    val content: String = getResponseContentAsString(response)
                    prettyFormatJsonToLog(LOG, content)
                    return myFromString.apply(content)
                } else {
                    return myFromReader.apply(getResponseContentAsReader(response))
                }
            } catch (e: JsonSyntaxException) {
                LOG.warn("Malformed server response", e)
                return myFallbackValue.produce()
            }
        }
    }

    private fun notifyError(message: String) {
        try {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Zentao Notifications")
                .createNotification(withErrorPrefix(message), NotificationType.ERROR)
                .notify(null)
        } catch (t: Throwable) {
            // Fallback to log if notifications infra is unavailable
            LOG.warn("Failed to show notification: ${withErrorPrefix(message)}", t)
        }
    }

    private fun extractErrorMessage(raw: String, defaultMessage: String?): String {
        if (raw.isBlank()) return defaultMessage ?: ""
        // Try to parse JSON and extract common error fields
        return try {
            val gson = Gson()
            val element = gson.fromJson(raw, JsonElement::class.java)
            when {
                element.isJsonPrimitive && element.asJsonPrimitive.isString -> element.asString
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    val keys = listOf("error", "message", "msg", "detail")
                    for (k in keys) {
                        if (obj.has(k) && obj.get(k).isJsonPrimitive) {
                            return obj.get(k).asString
                        }
                    }
                    // Handle { errors: [...] } or { errors: { message: "..." } }
                    if (obj.has("errors")) {
                        val errors = obj.get("errors")
                        if (errors.isJsonArray && errors.asJsonArray.size() > 0) {
                            val first = errors.asJsonArray[0]
                            if (first.isJsonPrimitive) return first.asString
                            if (first.isJsonObject) {
                                val fObj = first.asJsonObject
                                if (fObj.has("message") && fObj.get("message").isJsonPrimitive) {
                                    return fObj.get("message").asString
                                }
                            }
                        } else if (errors.isJsonObject) {
                            val eObj = errors.asJsonObject
                            if (eObj.has("message") && eObj.get("message").isJsonPrimitive) {
                                return eObj.get("message").asString
                            }
                        }
                    }
                    defaultMessage ?: raw
                }
                else -> defaultMessage ?: raw
            }
        } catch (_: Throwable) {
            // Not JSON or parse failed; return raw string if readable
            if (raw.length <= 1024) raw else (defaultMessage ?: "")
        }
    }

    class GsonSingleObjectDeserializer<T>(
        gson: Gson, cls: Class<T>, ignoreNotFound: Boolean = false
    ) : GsonResponseHandler<T>(
        JsonResponseHandlerBuilder.fromGson(gson)
        .ignoredCode { code -> ignoreNotFound && code == HttpStatus.SC_NOT_FOUND },
        { gson.fromJson(it, cls) },
        { gson.fromJson(it, cls) },
        { null })

    fun prettyFormatJsonToLog(logger: Logger, json: String) {
        if (logger.isDebugEnabled()) {
            try {
                val gson = GsonBuilder().setPrettyPrinting().create()
                logger.debug("\n" + gson.toJson(gson.fromJson<JsonElement?>(json, JsonElement::class.java)))
            } catch (_: JsonSyntaxException) {
                logger.debug("Malformed JSON\n" + json)
            }
        }
    }
}
