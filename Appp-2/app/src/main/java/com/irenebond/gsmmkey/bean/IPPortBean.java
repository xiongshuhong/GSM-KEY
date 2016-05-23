package com.irenebond.gsmmkey.bean;

import irene.com.framework.common.BaseBean;

/**
 * Created by Irene on 2016/3/18.
 */
public class IPPortBean extends BaseBean {
    private String ip;
    private String port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
