package com.irenebond.gsmmkey;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.irenebond.gsmmkey.bean.UserBean;
import com.irenebond.gsmmkey.data.GPRSDevicesListBean;

import java.util.ArrayList;

import irene.com.framework.exception.BaseUncaughtExceptionHandler;
import irene.com.framework.util.LogUtil;


/**
 * Created by Irene on 2016/1/26.
 */
public class GateApplication extends Application{

    private static GateApplication mGateApplication;
    public static boolean isSMS = true;
    public static GateApplication  getInstance(){
        return mGateApplication;
    }
    public static GPRSDevicesListBean mGPRSDevicesListBean;
    public static ArrayList<GPRSDevicesListBean> mDatas;
    public static int position;

    public static Activity mLastActivity = null;
    public static boolean isLoginSuccess = false;
    public static boolean isRegister = false;
    public static UserBean mUserBean;
    public static boolean isLogin = false;
    public String getPreferencesName(boolean isLogin){
        if(isLogin)
            return "device" + mGPRSDevicesListBean.getDeviceNo() + mGPRSDevicesListBean.getDeviceName();
        else
            return "device" + "000122";
    }


    public SharedPreferences getSpecialSharedPreferences(){

        String name = getPreferencesName(true);
        SharedPreferences mSharedPreferences = getSharedPreferences(name, Activity.MODE_PRIVATE);
        return mSharedPreferences;
    }

    public SharedPreferences getSpecialSharedPreferences(boolean isLogin){

        String name = getPreferencesName(false);
        SharedPreferences mSharedPreferences = getSharedPreferences(name, Activity.MODE_PRIVATE);
        return mSharedPreferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BaseUncaughtExceptionHandler.getInstance().init();
        mGateApplication = this;
        isLogin = false;
        isRegister = false;
        loadData();
    }

    private void loadData(){
        LogUtil.println("IreneBond 加载数据");
        SharedPreferences preferences = this.getSharedPreferences(MenuActivity.PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String userName = preferences.getString(MenuActivity.LOGIN_NAME, "");
        String password = preferences.getString(MenuActivity.LOGIN_PASSWD, "");
        String serverIP = preferences.getString(MenuActivity.LOGIN_IP, "");
        String serverPort = preferences.getString(MenuActivity.LOGIN_PORT, "");
        LogUtil.println("IreneBond 加载数据 ： " + password);
        if(password.length() > 0) {
            UserBean mUserBean = new UserBean();
            mUserBean.setUsername(userName);
            mUserBean.setPassword(password);
            mUserBean.setServerIP(serverIP);
            mUserBean.setServerPort(serverPort);
            GateApplication.getInstance().mUserBean = mUserBean;
        }else{
            GateApplication.getInstance().mUserBean = null;
        }
    }
}
