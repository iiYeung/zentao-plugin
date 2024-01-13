package com.github.darylyeung.zentaoplugin.extension.zentao.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-11 22:23:15
 */
public class ZentaoUserInfo {
    private int id;
    private String account;
    private String avatar;
    private String realname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
}