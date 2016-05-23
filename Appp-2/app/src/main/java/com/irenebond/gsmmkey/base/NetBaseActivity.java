package com.irenebond.gsmmkey.base;

import com.alibaba.fastjson.JSON;
import com.irenebond.gsmmkey.GPRSDevicesListActivity;
import com.irenebond.gsmmkey.GateApplication;
import com.irenebond.gsmmkey.MenuActivity;
import com.irenebond.gsmmkey.R;
import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.OutPackUtil;
import com.irenebond.gsmmkey.network.SocketThreadManager;
import com.irenebond.gsmmkey.network.observer.PacketObserver;
import com.irenebond.gsmmkey.network.packet.LoginInPacket;
import com.irenebond.gsmmkey.network.packet.LoginOutPacket;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import irene.com.framework.base.BaseActivity;
import irene.com.framework.util.LogUtil;


/**
 * Created by Irene on 2016/1/29.
 */
public class NetBaseActivity extends BaseActivity implements PacketObserver,SMSObserver{
    private String COMMAND = "\"cmd\":\"%d\"";
    protected SMSDataObserver mSMSDataObserver;
    protected int number;
    @Override
    public void installViews() {
        super.installViews();
        if(GateApplication.getInstance().mGPRSDevicesListBean != null) {
            if(GateApplication.getInstance().mGPRSDevicesListBean.getDeviceType().contains("02"))
                number = 2000;
            else
                number = 200;
        }
        initSMS();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GateApplication.getInstance().isSMS) {
            registerSMSSendBroadCastReceiver();
            registerSMS();
        }
        GateApplication.getInstance().mLastActivity = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(GateApplication.getInstance().isSMS) {
            unRegisterSMSSendBroadCastReceiver();
            unRegisterSMS();
        }
    }

    @Override
    public void notifyTcpPacketArrived(String mContent) {
        LogUtil.println("IreneBond 接收到数据notifyTcpPacketArrived：" + mContent);
        if(mContent !=null){
            LogUtil.println("IreneBond 截取前：" + mContent);
            if(mContent.indexOf("}") != mContent.lastIndexOf("}")) {
                mContent = mContent.substring(0, mContent.indexOf("}") + 1);
            }
            LogUtil.println("IreneBond 截取后：" + mContent);
            int command = getCommand(mContent);
            LogUtil.println("IreneBond 解析指令：" + command);
            switch (command){
                case Config.COMMAND_SET_AUTHORIZED_NO:
                case Config.COMMAND_INQUIRY_AUTHORIZED_NO:
                case Config.COMMAND_REMOVE_AUTHORIZED_NO:
                case Config.COMMAND_SET_ALARM_NO:
                case Config.COMMAND_INQUIRY_ALARM_NO:
                case Config.COMMAND_REMOVE_ALARM_NO:
                case Config.COMMAND_INQUIRY_A_NO:

                case Config.COMMAND_ALARM_WORKING_MODE:
                case Config.COMMAND_SET_ALARM_SMS_TEXT:
                case Config.COMMAND_ALARM_TEXT_INQUIRY:
                case Config.COMMAND_SYSTEM_STATUS_DATA_CHECK:

                case Config.COMMAND_ENABLE_ACM_MODE:
                case Config.COMMAND_ENABLE_REPORT_MODE:
                case Config.COMMAND_ENABLE_REPLY_MODE:
                case Config.COMMAND_SET_WORKING_MODE:
                case Config.COMMAND_GOT_TIMER_SETTING:
                case Config.COMMAND_ON_SYSTEM_STATUS_DATA_CHECK:
                case Config.COMMAND_CHECK_GSM_SIGNAL:

                case Config.COMMAND_SETUP_CUSTOMIZED_OPEN1_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_CLOSE1_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_COMMAND_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_OPEN2_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_CLOSE2_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_COMMAND2_TEXT:

                case Config.COMMAND_MAIN_1:
                case Config.COMMAND_MAIN_2:
                case Config.COMMAND_MAIN_3:
                case Config.COMMAND_MAIN_4:
                case Config.COMMAND_MAIN_5:
                case Config.COMMAND_MAIN_6:

                case Config.COMMAND_GET_DEVICE_NAME:
                case Config.COMMAND_REGISTER:

                case Config.COMMAND_LOGIN:
                case Config.COMMAND_ERROR:

                case Config.WEEKLY_ACCESS_NO:
                case Config.DAILY_ACCESS_NO:
                    sendMessage(command,mContent);
                    break;
                default:
                    break;
            }
        }
    }

    private void sendMessage(int command,String mContent){
        Message mMessage = mHandler.obtainMessage();
        mMessage.what = command;
        mMessage.obj = mContent;
        mHandler.sendMessage(mMessage);
    }

    private int getCommand(String mContent){

        if(isContainsData(mContent, Config.COMMAND_SET_AUTHORIZED_NO))
            return Config.COMMAND_SET_AUTHORIZED_NO;
        else if(isContainsData(mContent, Config.COMMAND_INQUIRY_AUTHORIZED_NO))
            return Config.COMMAND_INQUIRY_AUTHORIZED_NO;
        else if(isContainsData(mContent, Config.COMMAND_REMOVE_AUTHORIZED_NO))
            return Config.COMMAND_REMOVE_AUTHORIZED_NO;
        else if(isContainsData(mContent, Config.COMMAND_SET_ALARM_NO))
            return Config.COMMAND_SET_ALARM_NO;
        else if(isContainsData(mContent, Config.COMMAND_INQUIRY_ALARM_NO))
            return Config.COMMAND_INQUIRY_ALARM_NO;
        else if(isContainsData(mContent, Config.COMMAND_REMOVE_ALARM_NO))
            return Config.COMMAND_REMOVE_ALARM_NO;
        else if(isContainsData(mContent, Config.COMMAND_INQUIRY_A_NO))
            return Config.COMMAND_INQUIRY_A_NO;
        else if(isContainsData(mContent, Config.COMMAND_LOGIN))
            return Config.COMMAND_LOGIN;
        else if(isContainsData(mContent, Config.COMMAND_ALARM_WORKING_MODE))
            return Config.COMMAND_ALARM_WORKING_MODE;
        else if(isContainsData(mContent, Config.COMMAND_SET_ALARM_SMS_TEXT))
            return Config.COMMAND_SET_ALARM_SMS_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_ALARM_TEXT_INQUIRY))
            return Config.COMMAND_ALARM_TEXT_INQUIRY;
        else if(isContainsData(mContent, Config.COMMAND_SYSTEM_STATUS_DATA_CHECK))
            return Config.COMMAND_SYSTEM_STATUS_DATA_CHECK;
        else if(isContainsData(mContent, Config.COMMAND_ENABLE_ACM_MODE))
            return Config.COMMAND_ENABLE_ACM_MODE;
        else if(isContainsData(mContent, Config.COMMAND_ENABLE_REPORT_MODE))
            return Config.COMMAND_ENABLE_REPORT_MODE;
        else if(isContainsData(mContent, Config.COMMAND_ENABLE_REPLY_MODE))
            return Config.COMMAND_ENABLE_REPLY_MODE;
        else if(isContainsData(mContent, Config.COMMAND_SET_WORKING_MODE))
            return Config.COMMAND_SET_WORKING_MODE;
        else if(isContainsData(mContent, Config.COMMAND_GOT_TIMER_SETTING))
            return Config.COMMAND_GOT_TIMER_SETTING;
        else if(isContainsData(mContent, Config.COMMAND_ON_SYSTEM_STATUS_DATA_CHECK))
            return Config.COMMAND_ON_SYSTEM_STATUS_DATA_CHECK;
        else if(isContainsData(mContent, Config.COMMAND_CHECK_GSM_SIGNAL))
            return Config.COMMAND_CHECK_GSM_SIGNAL;

        else if(isContainsData(mContent, Config.COMMAND_SETUP_CUSTOMIZED_OPEN1_TEXT))
            return Config.COMMAND_SETUP_CUSTOMIZED_OPEN1_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_SETUP_CUSTOMIZED_CLOSE1_TEXT))
            return Config.COMMAND_SETUP_CUSTOMIZED_CLOSE1_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT))
            return Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_SETUP_CUSTOMIZED_COMMAND_TEXT))
            return Config.COMMAND_SETUP_CUSTOMIZED_COMMAND_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_SETUP_CUSTOMIZED_OPEN2_TEXT))
            return Config.COMMAND_SETUP_CUSTOMIZED_OPEN2_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_SETUP_CUSTOMIZED_CLOSE2_TEXT))
            return Config.COMMAND_SETUP_CUSTOMIZED_CLOSE2_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT))
            return Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_SETUP_CUSTOMIZED_COMMAND2_TEXT))
            return Config.COMMAND_SETUP_CUSTOMIZED_COMMAND2_TEXT;
        else if(isContainsData(mContent, Config.COMMAND_GET_DEVICE_NAME))
            return Config.COMMAND_GET_DEVICE_NAME;
        else if(isContainsData(mContent, Config.COMMAND_MAIN_1))
            return Config.COMMAND_MAIN_1;
        else if(isContainsData(mContent, Config.COMMAND_MAIN_2))
            return Config.COMMAND_MAIN_2;
        else if(isContainsData(mContent, Config.COMMAND_MAIN_3))
            return Config.COMMAND_MAIN_3;
        else if(isContainsData(mContent, Config.COMMAND_MAIN_4))
            return Config.COMMAND_MAIN_4;
        else if(isContainsData(mContent, Config.COMMAND_MAIN_5))
            return Config.COMMAND_MAIN_5;
        else if(isContainsData(mContent, Config.COMMAND_MAIN_6))
            return Config.COMMAND_MAIN_6;
        else if(isContainsData(mContent, Config.COMMAND_REGISTER))
            return Config.COMMAND_REGISTER;
        else if(isContainsData(mContent, Config.WEEKLY_ACCESS_NO))
            return Config.WEEKLY_ACCESS_NO;
        else if(isContainsData(mContent, Config.DAILY_ACCESS_NO))
            return Config.DAILY_ACCESS_NO;
        else
            return Config.COMMAND_ERROR;
    }

    private boolean isContainsData(String mContent,int data){
        if(mContent.contains(String.format(COMMAND,data)) || mContent.contains(String.format(COMMAND,data + 1)))
            return true;
        else
            return false;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Config.COMMAND_SET_AUTHORIZED_NO:
                case Config.COMMAND_INQUIRY_AUTHORIZED_NO:
                case Config.COMMAND_REMOVE_AUTHORIZED_NO:
                case Config.COMMAND_SET_ALARM_NO:
                case Config.COMMAND_INQUIRY_ALARM_NO:
                case Config.COMMAND_REMOVE_ALARM_NO:
                case Config.COMMAND_INQUIRY_A_NO:

                case Config.COMMAND_ALARM_WORKING_MODE:
                case Config.COMMAND_SET_ALARM_SMS_TEXT:
                case Config.COMMAND_ALARM_TEXT_INQUIRY:
                case Config.COMMAND_SYSTEM_STATUS_DATA_CHECK:

                case Config.COMMAND_ENABLE_ACM_MODE:
                case Config.COMMAND_ENABLE_REPORT_MODE:
                case Config.COMMAND_ENABLE_REPLY_MODE:
                case Config.COMMAND_SET_WORKING_MODE:
                case Config.COMMAND_GOT_TIMER_SETTING:
                case Config.COMMAND_ON_SYSTEM_STATUS_DATA_CHECK:
                case Config.COMMAND_CHECK_GSM_SIGNAL:

                case Config.COMMAND_SETUP_CUSTOMIZED_OPEN1_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_CLOSE1_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_COMMAND_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_OPEN2_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_CLOSE2_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT:
                case Config.COMMAND_SETUP_CUSTOMIZED_COMMAND2_TEXT:

                case Config.COMMAND_MAIN_1:
                case Config.COMMAND_MAIN_2:
                case Config.COMMAND_MAIN_3:
                case Config.COMMAND_MAIN_4:
                case Config.COMMAND_MAIN_5:
                case Config.COMMAND_MAIN_6:
                case Config.COMMAND_GET_DEVICE_NAME:
                case Config.COMMAND_LOGIN:
                case Config.COMMAND_REGISTER:

                case Config.WEEKLY_ACCESS_NO:
                case Config.DAILY_ACCESS_NO:
                    notifyTcpPacketArrived(msg.what,(String)(msg.obj));
                    break;
                default:
                    break;
            }
        }
    };

    protected <T> T getPacket(String content,Class<T> clazz){
        return JSON.parseObject(content, clazz);
    }
    @Override
    public void notifyTcpPacketArrived(int mCommand, final String mPacket) {
        if(mCommand == Config.COMMAND_LOGIN && GateApplication.getInstance().isLoginSuccess){//重发最新的
            LoginInPacket mLoginInPacket = getPacket(mPacket, LoginInPacket.class);
            if(mLoginInPacket.getInfo().equals("login success")) {
                GateApplication.getInstance().isLogin = false;
                OutPackUtil.sendAuto();
            }else {
                if(mLoginInPacket.getInfo().equals("this user is online"))
                    GateApplication.getInstance().isLogin = true;
                else
                    GateApplication.getInstance().isLogin = false;
                ((NetBaseActivity) (GateApplication.getInstance().mLastActivity)).notifyTcpPacketArrived(Config.COMMAND_ERROR, mLoginInPacket.getInfo());
            }
        }
        if(mCommand == Config.COMMAND_CONNET_SUCCESS){
            LogUtil.println("IreneBond 链接成功： " + SocketThreadManager.getInstance());
            if(SocketThreadManager.getInstance() == null)
                SocketThreadManager.init();
            else if(!GateApplication.getInstance().isRegister)
                OutPackUtil.sendMessage(getLoginPacket());
        }
        if(mCommand == Config.COMMAND_ERROR){
            showCustomNormalView();
            if(mPacket.contains("\"cmd\":\"003\""))
                return;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, mPacket, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(mCommand == Config.COMMAND_GET_DEVICE_NAME){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GPRSDevicesListActivity.instance.setDeviceName(mPacket);
                }
            });
        }
    }

    protected LoginOutPacket getLoginPacket(){

        LoginOutPacket mLoginOutPacket = new LoginOutPacket();
        mLoginOutPacket.setCmd(Config.COMMAND_LOGIN);
        mLoginOutPacket.setUserName(GateApplication.getInstance().mUserBean.getUsername());
        mLoginOutPacket.setPassword(GateApplication.getInstance().mUserBean.getPassword());
        mLoginOutPacket.setId(2);
        return mLoginOutPacket;
    }

    protected void sendSms(String smsContent){
        String str_num =getPhoneNo();
        if(str_num.length()!=0){
            // System.out.println("phone No: "+str_num);
//            String str_content =smsContent;
//            SmsManager sms_manager = SmsManager.getDefault();
//            ArrayList<String> texts = sms_manager.divideMessage(str_content);
//            Intent sentIntent = new Intent(SMS_SEND_ACTIOIN);
//            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
//            for(String text : texts){
//                sms_manager.sendTextMessage(str_num, null, text, sentPI, null);
//            }
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+str_num));
            intent.putExtra("sms_body", smsContent);
            startActivity(intent);
        }else{
            Toast.makeText(mActivity, R.string.not_set_access_control_no, Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendSms(String smsContent,String phoneNo){
        String str_num = phoneNo;
        if(str_num.length()!=0){
            // System.out.println("phone No: "+str_num);
//            String str_content =smsContent;
//            SmsManager sms_manager = SmsManager.getDefault();
//            ArrayList<String> texts = sms_manager.divideMessage(str_content);
//            Intent sentIntent = new Intent(SMS_SEND_ACTIOIN);
//            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
//            for(String text : texts){
//                sms_manager.sendTextMessage(str_num, null, text, sentPI, null);
//            }
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+str_num));
            intent.putExtra("sms_body", smsContent);
            startActivity(intent);
        }else{
            Toast.makeText(mActivity, R.string.not_set_access_control_no, Toast.LENGTH_SHORT).show();
        }
    }

    public String getPreferenceString(String key){
        return getPreferenceString(key,"");
    }

    public void setPreferenceString(String key,String value){
        SharedPreferences.Editor  mEditor = GateApplication.getInstance().getSpecialSharedPreferences().edit();
        mEditor.putString(key,value);
        mEditor.commit();
    }

    public String getPreferenceString(String key,String defaultValue){
        String content = GateApplication.getInstance().getSpecialSharedPreferences().getString(key, defaultValue);
        return content;
    }

    public String getPhoneNo(){

        if(GateApplication.getInstance().mGPRSDevicesListBean == null)
            return "";
        else
            return GateApplication.getInstance().mGPRSDevicesListBean.getDeviceNo();
    }

    public String getPassword(){
        if(GateApplication.getInstance().mGPRSDevicesListBean == null)
            return "123456";
        else
            return GateApplication.getInstance().mGPRSDevicesListBean.getPassword();
    }

    protected void keepTheDialog(DialogInterface dialog,int hint){

        Toast.makeText(mActivity, hint, Toast.LENGTH_SHORT).show();
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    protected void keepTheDialog(DialogInterface dialog,String hint){

        Toast.makeText(mActivity, hint, Toast.LENGTH_SHORT).show();
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void closeTheDialog(DialogInterface dialog){
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSMS(){
        if(GateApplication.getInstance().isSMS)
            mSMSDataObserver = new SMSDataObserver(new Handler());
    }

    private void registerSMS(){
        if(GateApplication.getInstance().isSMS)
            mActivity.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mSMSDataObserver);
    }

    private void unRegisterSMS(){
        if(GateApplication.getInstance().isSMS)
            mActivity.getContentResolver().unregisterContentObserver(mSMSDataObserver);
    }

    class SMSDataObserver extends ContentObserver {

        public SMSDataObserver(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Cursor cursor = null;
            try {
                cursor = mActivity.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, "date desc");
                if (cursor.moveToFirst()) {
                    String message = cursor.getString(cursor.getColumnIndex("body"));
                    LogUtil.println("IreneBond 短信内容：" + message);
                    notifySMSPacketArrived(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    @Override
    public void notifySMSPacketArrived(String mContent) {

    }

    public String getCurrentTime(){
        Date dt = new Date();
        String curDate = (String) DateFormat.format("yyyy-M-d", dt);
        Time t=new Time();
        t.setToNow();
        if(t.hour<10)
            curDate+=" 0"+String.valueOf(t.hour)+":";
        else
            curDate+=" "+String.valueOf(t.hour)+":";

        if(t.minute<10)
            curDate+="0"+String.valueOf(t.minute)+":";
        else
            curDate+=""+String.valueOf(t.minute)+":";

        if(t.second<10)
            curDate+="0"+String.valueOf(t.second);
        else
            curDate+=""+String.valueOf(t.second);

        return curDate;
    }
}
