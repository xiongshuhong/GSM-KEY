package com.irenebond.gsmmkey.network.packet.base;

/**
 * Created by Irene on 2016/3/3.
 */
public abstract class InPacket {
    protected String cmd;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {

        this.cmd = String.valueOf(cmd);
    }
}
