package com.iiyeung.plugin.zentao.extension.zentao.model

import kotlinx.serialization.Serializable
import java.util.Date

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-11-10 13:51:40
 */
@Serializable
data class ZentaoProject(
    var id: Int,
    val name: String,
    val code: String,
    val model: String,
    val budget: Int,
    val budgetUnit: String,
    val parent: Int,
    val begin: String,
    val end: String,
    val status: String,
    val openedBy: String,
    val openedDate: Date,
    val PM: String,
    val progress: Int,
    )