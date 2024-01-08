package com.github.darylyeung.zentaoplugin.toolWindow

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().info("this is my first plugin")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        //  create login panel
        val loginPanel = LoginPanel().createPanel(toolWindow)
        //  add login panel to tool window
        val content = ContentFactory.getInstance().createContent(loginPanel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
