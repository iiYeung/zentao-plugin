package com.github.darylyeung.zentaoplugin.extension.zentao;

import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepositoryType;
import com.intellij.tasks.impl.BaseRepository;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-10 23:02:18
 */
@Tag("zentao")
public class ZentaoRepository extends BaseRepository {

    @Override
    public @Nullable Task findTask(@NotNull String s) throws Exception {
        return null;
    }

    public ZentaoRepository(TaskRepositoryType type) {
        super(type);
    }

    @Override
    public @NotNull BaseRepository clone() {
        return null;
    }
}
