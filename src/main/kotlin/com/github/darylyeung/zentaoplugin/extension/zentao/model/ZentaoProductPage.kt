package com.github.darylyeung.zentaoplugin.extension.zentao.model

import kotlinx.serialization.Serializable

/**
 * @author Yeung
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
