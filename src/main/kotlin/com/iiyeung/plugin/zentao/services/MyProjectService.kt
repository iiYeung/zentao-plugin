package com.iiyeung.plugin.zentao.services

import com.iiyeung.plugin.zentao.ZentaoBundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    init {
        thisLogger().info(ZentaoBundle.message("projectService", project.name))
    }

    fun getRandomNumber() = (1..100).random()
}
