package com.iiyeung.plugin.zentao.common

import com.iiyeung.plugin.zentao.ZentaoBundle
import com.iiyeung.plugin.zentao.util.ZentaoResponseUtil
import com.intellij.tasks.TaskRepository

/**
 * Author: Yeung
 * Converted to Kotlin
 */
class RequestFailedException : RuntimeException {

    val repository: TaskRepository?

    constructor(repository: TaskRepository, message: String) : super(
        if (message.isBlank()) message else ZentaoResponseUtil.withErrorPrefix(message)
    ) {
        this.repository = repository
    }

    constructor(message: String) : super(
        if (message.isBlank()) message else ZentaoResponseUtil.withErrorPrefix(message)
    ) {
        this.repository = null
    }

    constructor(message: String, cause: Throwable) : super(
        if (message.isBlank()) message else ZentaoResponseUtil.withErrorPrefix(message), cause
    ) {
        this.repository = null
    }

    constructor(cause: Throwable) : super(cause) {
        this.repository = null
    }

    companion object {
        @JvmStatic
        fun forStatusCode(code: Int, message: String): RequestFailedException {
            val msg = ZentaoBundle.message("failure.http.error", code, message)
            return RequestFailedException(
                if (msg.isBlank()) msg else ZentaoResponseUtil.withErrorPrefix(msg)
            )
        }

        @JvmStatic
        fun forServerMessage(message: String): RequestFailedException {
            val msg = ZentaoBundle.message("failure.server.message", message)
            return RequestFailedException(
                if (msg.isBlank()) msg else ZentaoResponseUtil.withErrorPrefix(msg)
            )
        }
    }
}
