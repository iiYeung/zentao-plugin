package com.iiyeung.plugin.zentao.extension.zentao.model

import kotlinx.serialization.Serializable

/**
 * @author iiYeung
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
