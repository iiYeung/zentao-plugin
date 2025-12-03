package com.iiyeung.plugin.zentao.common

/**
 * Centralized API configuration constants: paths, headers, content types, and endpoints.
 * Keep sensitive values out of logs; do not include tokens here.
 */
object ApiConfig {
    object Paths {
        const val REST_PREFIX: String = "/api.php/v1"
    }

    object Headers {
        const val CONTENT_TYPE: String = "Content-Type"
    }

    object ContentTypes {
        const val JSON_UTF8: String = "application/json; charset=utf-8"
    }

    object Endpoints {
        const val TOKENS: String = "tokens"
        const val PRODUCTS: String = "products"
        const val BUGS: String = "bugs"
        const val USER: String = "user"
    }
}