package com.irenebond.gsmmkey.network.packet;

import com.irenebond.gsmmkey.network.packet.base.OutPacket;

/**
 * Created by Irene on 2016/3/3.
 */
public class LoginOutPacket extends OutPacket{
    private String cmd;
    private String userName;
    private String password;
    private int id;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {

        this.cmd = String.valueOf(cmd);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
