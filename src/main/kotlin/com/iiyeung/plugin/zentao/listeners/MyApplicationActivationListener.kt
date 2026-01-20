package com.iiyeung.plugin.zentao.listeners

import com.iiyeung.plugin.zentao.service.ExpirationCheckService
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.wm.IdeFrame

internal class MyApplicationActivationListener : ApplicationActivationListener {

    private var hasCheckedOnce = false

    override fun applicationActivated(ideFrame: IdeFrame) {
        thisLogger().info("zentao plugin activated")

        // 只在首次激活时检查一次
        if (!hasCheckedOnce) {
            hasCheckedOnce = true
            checkPluginExpiration(ideFrame)
        }
    }

    private fun checkPluginExpiration(ideFrame: IdeFrame) {
        val expirationService = ExpirationCheckService.getInstance()
        val project = ideFrame.project

        // 显示过期或警告通知
        if (expirationService.isPluginExpired() || expirationService.shouldShowWarning()) {
            expirationService.showExpirationNotification(project)
        }
    }
}
