package com.irenebond.gsmmkey.network.packet;

import com.irenebond.gsmmkey.network.packet.base.InPacket;

/**
 * Created by Irene on 2016/3/3.
 */
public class LoginInPacket extends InPacket{
    private String info;
    private String devices;
    private int id;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDevices() {
        return devices;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //    {"cmd":"100","info":"login success","devices":"012497008039244-000002-ON,861516000591456-000043-OFF,012497008042727-000002-OFF,1075021997200-000401-OFF","id":2}
}
