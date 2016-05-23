package com.irenebond.gsmmkey.network.packet;

import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.packet.base.OutPacket;

/**
 * Created by Irene on 2016/3/18.
 */
public class RegisterOutPacket extends OutPacket{
    private String cmd = String.valueOf(Config.COMMAND_REGISTER);
    private String userName;
    private String password;
    private int id = 1;
    private String Email;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
