package com.github.darylyeung.zentaoplugin.toolWindow

import com.github.darylyeung.zentaoplugin.common.Constant
import com.intellij.ide.util.PropertiesComponent
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
        if (PropertiesComponent.getInstance().isValueSet(Constant.TOKEN_KEY.value)) {
            thisLogger().info("token is set")
            //  login success, show product list
            val listPanel = ProductListPanel().createListPanel(toolWindow)
            val content = ContentFactory.getInstance().createContent(listPanel, null, false)
            toolWindow.contentManager.addContent(content)
            return
        }
        //  create login panel
        val loginPanel = LoginPanel().createLoginPanel(toolWindow)
        //  add login panel to tool window
        val content = ContentFactory.getInstance().createContent(loginPanel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
