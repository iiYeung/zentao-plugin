package com.iiyeung.plugin.zentao.extension.zentao

import com.iiyeung.plugin.zentao.extension.icon.TasksCoreIcons
import com.intellij.openapi.project.Project
import com.intellij.tasks.TaskRepository
import com.intellij.tasks.config.TaskRepositoryEditor
import com.intellij.tasks.impl.BaseRepositoryType
import com.intellij.util.Consumer
import javax.swing.Icon

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-01-18 23:53:53
 */
class ZentaoRepositoryType : BaseRepositoryType<ZentaoRepository>() {
    override fun getName(): String {
        return "Zentao"
    }

    override fun getIcon(): Icon {
        return TasksCoreIcons.zentao
    }

    override fun createRepository(): TaskRepository {
        return ZentaoRepository(this)
    }

    override fun getRepositoryClass(): Class<ZentaoRepository> {
        return ZentaoRepository::class.java
    }

    override fun createEditor(
        repository: ZentaoRepository,
        project: Project?,
        changeListener: Consumer<in ZentaoRepository>?
    ): TaskRepositoryEditor {
        return ZentaoRepositoryEditor(project, repository, changeListener)
    }
}