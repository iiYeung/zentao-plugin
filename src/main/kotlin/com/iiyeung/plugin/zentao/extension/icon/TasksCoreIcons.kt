package com.iiyeung.plugin.zentao.extension.icon

import com.intellij.ui.IconManager
import javax.swing.Icon

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-18 22:07:51
 */
object TasksCoreIcons {
    private fun load(path: String, cacheKey: Int, flags: Int): Icon {
        return IconManager.getInstance()
            .loadRasterizedIcon(path, TasksCoreIcons::class.java.classLoader, cacheKey, flags)
    }

    /** 16x16 */
    val zentao: Icon = load("icons/zentao.svg", -995420501, 0)
}