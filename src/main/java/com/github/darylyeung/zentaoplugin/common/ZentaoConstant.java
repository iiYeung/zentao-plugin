package com.github.darylyeung.zentaoplugin.common;

import lombok.Getter;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-14 18:10:57
 */
public enum ZentaoConstant {

    TOKEN("token", "token");
    private final String code;
    private final String desc;

    ZentaoConstant(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
