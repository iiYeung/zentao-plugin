package com.github.darylyeung.zentaoplugin.extension.zentao;

import com.github.darylyeung.zentaoplugin.extension.icons.TasksCoreIcons;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.impl.BaseRepositoryType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-10 23:06:19
 */
public class ZentaoRepositoryType extends BaseRepositoryType<ZentaoRepository> {
    @Override
    public @NotNull String getName() {
        return "Zentao";
    }

    @Override
    public @NotNull Icon getIcon() {
        return TasksCoreIcons.ZenTao;
    }

    @Override
    public @NotNull TaskRepository createRepository() {
        return new ZentaoRepository(this);
    }

    @Override
    public Class<ZentaoRepository> getRepositoryClass() {
        return ZentaoRepository.class;
    }
}
