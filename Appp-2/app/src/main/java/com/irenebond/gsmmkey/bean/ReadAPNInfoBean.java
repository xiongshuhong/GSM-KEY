package com.irenebond.gsmmkey.bean;

import java.util.ArrayList;

import irene.com.framework.common.BaseBean;

/**
 * Created by Irene on 2016/4/13.
 */
public class ReadAPNInfoBean extends BaseBean{
    private ArrayList<APNInfoListBean> data;

    public ArrayList<APNInfoListBean> getData() {
        return data;
    }

    public void setData(ArrayList<APNInfoListBean> data) {
        this.data = data;
    }
}
