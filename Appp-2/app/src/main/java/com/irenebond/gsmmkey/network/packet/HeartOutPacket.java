package com.irenebond.gsmmkey.network.packet;

import com.irenebond.gsmmkey.GateApplication;
import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.packet.base.OutPacket;

/**
 * Created by Irene on 2016/3/4.
 */
public class HeartOutPacket extends OutPacket{
    private String cmd = String.valueOf(Config.COMMAND_HEART);
    private String deviceId = GateApplication.getInstance().mGPRSDevicesListBean.getDeviceNo();
    private String end = "0";
}
