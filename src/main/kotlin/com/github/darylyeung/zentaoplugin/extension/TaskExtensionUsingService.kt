package com.github.darylyeung.zentaoplugin.extension

import com.github.darylyeung.zentaoplugin.extension.zentao.ZentaoRepositoryType
import com.intellij.openapi.extensions.ExtensionPointName

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-19 14:40:06
 */
class TaskExtensionUsingService {
    companion object {
        private val EP_NAME = ExtensionPointName<Any>("com.intellij.tasks.taskRepositoryType")
    }

    fun userExtensionPoint() {
        EP_NAME.extensionList.plus(ZentaoRepositoryType())
    }
}