package com.irenebond.gsmmkey.network.packet;

import com.irenebond.gsmmkey.network.packet.base.InPacket;

/**
 * Created by Irene on 2016/3/7.
 */
public class CommandInPacket extends InPacket {

    private String info;
    private int id;
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
