package com.irenebond.gsmmkey.bean;

import irene.com.framework.common.BaseBean;

/**
 * Created by Irene on 2016/1/29.
 */
public class LoginBean extends BaseBean{

    private String cmd;
    private String username;
    private String password;
    private int id;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
