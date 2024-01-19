package com.github.darylyeung.zentaoplugin.extension.zentao.model

import kotlinx.serialization.Serializable

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-18 22:38:20
 */
@Serializable
data class ZentaoProduct(
    var id: Int,
    val program: Int,
    val name: String,
    val code: String,
    val shadow: Int,
    val bind: String,
    val line: Int,
    val type: String,
    val status: String,
    val subStatus: String,
    val desc: String,
    val PO: ZentaoUserInfo,
    val QD: ZentaoUserInfo,
    val RD: ZentaoUserInfo,
    val feedback: String,
    val ticket: String,
    val acl: String,
//    val whitelist: List<Any>,
    val reviewer: String,
    val createdBy: ZentaoUserInfo,
    val createdDate: String,
    val createdVersion: String,
    val order: Int,
    val vision: String,
    val deleted: String,
    val lineName: String,
    val programName: String,
    val programPM: String,
//    val stories: Map<String, Any>,
//    val requirements: Map<String, Any>,
    val plans: Int,
    val releases: Int,
    val bugs: Int,
    val unResolved: Int,
    val closedBugs: Int,
    val fixedBugs: Int,
    val thisWeekBugs: Int,
    val assignToNull: Int,
    val progress: Int,
)