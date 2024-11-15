package com.iiyeung.plugin.zentao.extension.zentao.model

import kotlinx.serialization.Serializable

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-01-18 22:42:29
 */
@Serializable
data class ZentaoProductPage(
    val page: Int,
    val total: Int,
    val limit: Int,
    val products: List<ZentaoProduct>
)
