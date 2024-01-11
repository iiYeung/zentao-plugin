package com.github.darylyeung.zentaoplugin.extension.zentao;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.config.BaseRepositoryEditor;
import com.intellij.tasks.impl.BaseRepository;
import com.intellij.util.Consumer;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-11 22:13:09
 */
public class ZentaoRepositoryEditor extends BaseRepositoryEditor<ZentaoRepository> {
    public ZentaoRepositoryEditor(Project project, ZentaoRepository repository, Consumer<? super ZentaoRepository> changeListener) {
        super(project, repository, changeListener);
    }
}
