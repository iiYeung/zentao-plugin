package com.iiyeung.plugin.zentao.toolWindow

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().info("zentao plugin tool window factory")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        TODO("Not yet implemented")
    }

    override fun shouldBeAvailable(project: Project) = true
}
