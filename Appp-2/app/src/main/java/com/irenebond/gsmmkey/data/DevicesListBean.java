package com.irenebond.gsmmkey.data;

import java.util.ArrayList;

import irene.com.framework.common.BaseBean;

/**
 * Created by Irene on 2016/3/21.
 */
public class DevicesListBean extends BaseBean{
    private ArrayList<GPRSDevicesListBean> mAllDevices;

    public ArrayList<GPRSDevicesListBean> getmAllDevices() {
        return mAllDevices;
    }

    public void setmAllDevices(ArrayList<GPRSDevicesListBean> mAllDevices) {
        this.mAllDevices = mAllDevices;
    }
}
