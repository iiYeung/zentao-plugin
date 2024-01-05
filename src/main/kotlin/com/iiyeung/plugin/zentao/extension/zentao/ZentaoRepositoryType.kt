package com.iiyeung.plugin.zentao.extension.zentao

import com.iiyeung.plugin.zentao.extension.icon.TasksCoreIcons
import com.intellij.tasks.TaskRepository
import com.intellij.tasks.impl.BaseRepositoryType
import javax.swing.Icon

/**
 * @author Yeung
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
}