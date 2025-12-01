package com.iiyeung.plugin.zentao.common

import com.iiyeung.plugin.zentao.util.ZentaoResponseUtil

/**
 * Thrown when server responds with 401 Unauthorized.
 * Used to suppress user-facing notifications and only log the event.
 */
class UnauthorizedException : RuntimeException {
    constructor(message: String) : super(ZentaoResponseUtil.withErrorPrefix(message))
    constructor(message: String, cause: Throwable) : super(ZentaoResponseUtil.withErrorPrefix(message), cause)
    constructor(cause: Throwable) : super(cause)
}
