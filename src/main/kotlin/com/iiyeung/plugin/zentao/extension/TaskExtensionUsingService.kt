package com.iiyeung.plugin.zentao.extension

import com.iiyeung.plugin.zentao.extension.zentao.ZentaoRepositoryType
import com.intellij.openapi.extensions.ExtensionPointName

/**
 * @author iiYeung
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