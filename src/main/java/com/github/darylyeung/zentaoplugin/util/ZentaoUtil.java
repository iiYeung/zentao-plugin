package com.github.darylyeung.zentaoplugin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.tasks.impl.gson.TaskGsonUtil;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-14 12:33:01
 */
public class ZentaoUtil {

    public static final Gson GSON = buildGson();

    private static Gson buildGson() {
        final GsonBuilder gson = TaskGsonUtil.createDefaultBuilder();
        return gson.create();
    }
}
