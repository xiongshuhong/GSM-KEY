package com.irenebond.gsmmkey.network.packet;

import com.irenebond.gsmmkey.network.packet.base.InPacket;

/**
 * Created by Irene on 2016/3/18.
 */
public class RegisterInPacket extends InPacket {
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
