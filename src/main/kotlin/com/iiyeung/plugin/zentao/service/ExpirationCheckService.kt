package com.iiyeung.plugin.zentao.service

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 插件有效期检查服务
 * 用于 Preview 版本的时间限制
 */
@Service(Service.Level.APP)
class ExpirationCheckService {

    companion object {
        // 设置过期日期 (格式: yyyy-MM-dd)
        private const val EXPIRATION_DATE = "2026-01-27"

        // 提前警告天数
        private const val WARNING_DAYS = 3

        // 插件 ID
        private const val PLUGIN_ID = "com.github.darylyeung.zentaoplugin"

        fun getInstance(): ExpirationCheckService {
            return ApplicationManager.getApplication().getService(ExpirationCheckService::class.java)
        }
    }

    private val logger = thisLogger()
    private val expirationDate: LocalDate = LocalDate.parse(EXPIRATION_DATE, DateTimeFormatter.ISO_LOCAL_DATE)

    @Volatile
    private var isExpired: Boolean = false

    @Volatile
    private var hasNotified: Boolean = false

    @Volatile
    private var isPreviewVersion: Boolean = false

    init {
        checkIfPreviewVersion()
        if (isPreviewVersion) {
            checkExpiration()
        } else {
            logger.info("Non-preview version detected, expiration check disabled")
        }
    }

    /**
     * 检查是否为 Preview 版本
     */
    private fun checkIfPreviewVersion() {
        try {
            val plugin = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
            val version = plugin?.version ?: ""
            isPreviewVersion = version.contains("preview", ignoreCase = true)
            logger.info("Plugin version: $version, isPreviewVersion: $isPreviewVersion")
        } catch (e: Exception) {
            logger.error("Failed to check plugin version", e)
            // 默认不启用过期检查
            isPreviewVersion = false
        }
    }

    /**
     * 检查插件是否过期
     */
    private fun checkExpiration() {
        val today = LocalDate.now()
        isExpired = today.isAfter(expirationDate)

        if (isExpired) {
            logger.warn("Zentao Plugin Preview version has expired on $EXPIRATION_DATE")
        } else {
            val daysRemaining = ChronoUnit.DAYS.between(today, expirationDate)
            logger.info("Zentao Plugin Preview version will expire in $daysRemaining days (on $EXPIRATION_DATE)")
        }
    }

    /**
     * 判断插件是否已过期
     * 非 Preview 版本始终返回 false
     */
    fun isPluginExpired(): Boolean {
        return isPreviewVersion && isExpired
    }

    /**
     * 获取剩余天数
     */
    fun getDaysRemaining(): Long {
        val today = LocalDate.now()
        return if (today.isAfter(expirationDate)) {
            0
        } else {
            ChronoUnit.DAYS.between(today, expirationDate)
        }
    }

    /**
     * 是否需要显示警告
     */
    fun shouldShowWarning(): Boolean {
        return getDaysRemaining() in 1..WARNING_DAYS
    }

    /**
     * 显示过期通知
     */
    fun showExpirationNotification(project: Project?) {
        if (hasNotified) {
            return
        }

        hasNotified = true

        val notification = if (isExpired) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Zentao Notifications")
                .createNotification(
                    "Zentao Plugin Preview 已过期",
                    "此 Preview 版本已于 $EXPIRATION_DATE 过期，插件功能已被禁用。\n" +
                            "请联系开发者获取最新版本。",
                    NotificationType.ERROR
                )
        } else {
            val daysRemaining = getDaysRemaining()
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Zentao Notifications")
                .createNotification(
                    "Zentao Plugin Preview 即将过期",
                    "此 Preview 版本将在 $daysRemaining 天后过期 (过期日期: $EXPIRATION_DATE)。\n" +
                            "请及时联系开发者获取更新版本。",
                    NotificationType.WARNING
                )
        }

        notification.notify(project)
    }

    /**
     * 验证功能是否可用
     * 如果插件已过期，返回 false 并显示通知
     */
    fun validateAndNotify(project: Project?): Boolean {
        if (isExpired) {
            showExpirationNotification(project)
            return false
        }

        if (shouldShowWarning() && !hasNotified) {
            showExpirationNotification(project)
        }

        return true
    }
}