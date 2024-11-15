package com.iiyeung.plugin.zentao.extension.zentao.model

import kotlinx.serialization.Serializable

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-01-18 22:39:48
 */
@Serializable
data class ZentaoUserInfo(
    val id: Int,
    val account: String,
    val avatar: String,
    val realname: String
)
