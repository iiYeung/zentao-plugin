package com.iiyeung.plugin.zentao.common

import com.iiyeung.plugin.zentao.ZentaoBundle
import com.intellij.tasks.TaskRepository

/**
 * Author: Yeung
 * Converted to Kotlin
 */
class RequestFailedException : RuntimeException {

    val repository: TaskRepository?

    constructor(repository: TaskRepository, message: String) : super(message) {
        this.repository = repository
    }

    constructor(message: String) : super(message) {
        this.repository = null
    }

    constructor(message: String, cause: Throwable) : super(message, cause) {
        this.repository = null
    }

    constructor(cause: Throwable) : super(cause) {
        this.repository = null
    }

    companion object {
        @JvmStatic
        fun forStatusCode(code: Int, message: String): RequestFailedException {
            return RequestFailedException(ZentaoBundle.message("failure.http.error", code, message))
        }

        @JvmStatic
        fun forServerMessage(message: String): RequestFailedException {
            return RequestFailedException(ZentaoBundle.message("failure.server.message", message))
        }
    }
}
