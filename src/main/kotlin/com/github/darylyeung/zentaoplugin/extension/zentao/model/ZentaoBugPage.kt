package com.github.darylyeung.zentaoplugin.extension.zentao.model

import kotlinx.serialization.Serializable

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-18 22:36:48
 */
@Serializable
data class ZentaoBugPage(
    val page:Int,
    val total:Int,
    val limit:Int,
    val bugs:List<ZentaoBug>
)
