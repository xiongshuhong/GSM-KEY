package com.irenebond.gsmmkey.data;

import com.irenebond.gsmmkey.R;

import irene.com.framework.common.BaseBean;

/**
 * Created by Irene on 2016/1/26.
 */
public class GPRSDevicesListBean extends BaseBean{

    private String deviceNo;
    private String deviceType;
    private String deviceStatus;
    private String deviceName;
    private String password;

    public int getNumber() {
        if(getDeviceType().equals("000001")) {
            return 200;
        }else if(getDeviceType().equals("000002")) {
            return 2000;
        }else
            return 0;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
