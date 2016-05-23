package com.irenebond.gsmmkey.bean;

import java.util.ArrayList;

import irene.com.framework.common.BaseBean;

/**
 * Created by Irene on 2016/4/13.
 */
public class APNInfoListBean extends BaseBean{
    private String name;
    private ArrayList<APNInfoBean> list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<APNInfoBean> getList() {
        if(list == null)
            list = new ArrayList<APNInfoBean>();
        return list;
    }

    public void setList(ArrayList<APNInfoBean> list) {
        this.list = list;
    }
}
