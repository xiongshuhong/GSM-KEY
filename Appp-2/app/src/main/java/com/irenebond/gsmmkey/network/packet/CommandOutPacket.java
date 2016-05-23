package com.irenebond.gsmmkey.network.packet;

import com.irenebond.gsmmkey.network.packet.base.OutPacket;

/**
 * Created by Irene on 2016/3/7.
 */
public class CommandOutPacket extends OutPacket{
//    {"cmd":"101","sendto":"123233645623","info":"#PWD123456#OUT1=ON","id":1}
    private String cmd;
    private String sendto;
    private String info;
    private int id;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = String.valueOf(cmd);
    }

    public String getSendto() {
        return sendto;
    }

    public void setSendto(String sendto) {
        this.sendto = sendto;
    }

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
