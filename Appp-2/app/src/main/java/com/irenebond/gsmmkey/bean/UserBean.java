package com.irenebond.gsmmkey.bean;

import irene.com.framework.common.BaseBean;

/**
 * Created by Irene on 2016/3/24.
 */
public class UserBean extends BaseBean{
    private String username;
    private String password;
    private String serverIP;
    private String serverPort;

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

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getServerPort() {
        if(serverPort == null)
            return 0;
        else
            return Integer.parseInt(serverPort);
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
