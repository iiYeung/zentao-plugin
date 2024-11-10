package com.iiyeung.plugin.zentao.extension.zentao.model

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-11-10 15:21:19
 */
data class ZentaoUserDetail(
    val id: Int,
    val type: String,
    val dept: Int,
    val account: String,
    val realname: String,
    val nickname: String,
    val avatar: String,
    val birthday: String,
    val gender: String,
    val mobile: String,
    val phone: String,
    val weixin: String,
    val address: String,
    val join: String,
    val admin: Boolean
)
