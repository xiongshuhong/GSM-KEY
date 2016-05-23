package com.irenebond.gsmmkey.network.packet;

import com.irenebond.gsmmkey.network.packet.base.InPacket;

/**
 * Created by Irene on 2016/3/18.
 */
public class GetDeviceNameInPacket extends InPacket {
    private String deviceId;
    private String info;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getInfo() {
        if(info.contains("DEVICE NAME IS "))
            info = info.replace("DEVICE NAME IS ","");
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
