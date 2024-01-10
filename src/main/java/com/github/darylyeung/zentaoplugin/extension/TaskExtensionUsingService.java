package com.github.darylyeung.zentaoplugin.extension;

import com.github.darylyeung.zentaoplugin.extension.zentao.ZentaoRepositoryType;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.tasks.TaskRepositoryType;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-10 22:58:14
 */
public class TaskExtensionUsingService {
    private static final ExtensionPointName<TaskRepositoryType> EP_NAME = ExtensionPointName.create("com.intellij.tasks.repositoryType");

    public void userExtensionPoint() {
        EP_NAME.getExtensionList().add(new ZentaoRepositoryType());
    }
}
