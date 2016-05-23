

package com.irenebond.gsmmkey;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.OutPackUtil;
import com.irenebond.gsmmkey.network.packet.CommandInPacket;
import com.irenebond.gsmmkey.network.packet.CommandOutPacket;

import java.util.ArrayList;
import java.util.HashMap;

import irene.com.framework.util.LogUtil;

public class AlarmSettingActivity extends NetBaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView=null;
    private TextView tv_last_content=null;
    private SimpleAdapter simpleAdapter;
    private  final String mColumns[]={"txtName","txtInfo"};
    private  final int mViewIds[]={
            R.id.txtView_name,
            R.id.txtView_info,
    };

    private final int SMS_MODE[]={
            R.string.alarm_working_mode,
            R.string.set_alarm_sms_text,
            R.string.get_alarm_sms_text,
            R.string.status_check
    };

    private final int SMS_MODE_HINT[]={
            R.string.alarm_working_mode_hint,
            R.string.set_alarm_sms_text_hint,
            R.string.get_alarm_sms_text_hint,
            R.string.status_check_hint
    };
    private ArrayList<HashMap<String,Object>> listItem;
    private static final int ALARM_WORKING_MODE = 0;
    private static final int SET_ALARM_SMS_TEXT = 1;
    private static final int GET_ALARM_SMS_TEXT = 2;
    private static final int STATUS_CHECK = 3;

    private SMSReceiver mSMSReceiver=null;

    @Override
    public void installViews() {
        super.installViews();
        setContentView(R.layout.activity_system_setting);
        mTitleBar.setLeftBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        mTitleBar.setTitle(R.string.alarm_setting_title);
        listView=(ListView)findViewById(R.id.system_settings_list);
        listItem=new ArrayList<HashMap<String,Object>>();
        simpleAdapter=new SimpleAdapter(this,listItem,
                R.layout.item_system_settings_list,
                mColumns,
                mViewIds
        );
        listView.setOnItemClickListener(this);
        listView.setAdapter(simpleAdapter);
        addListItem();

        tv_last_content=(TextView)findViewById(R.id.tv_last_content);

        mSMSReceiver=new SMSReceiver();
        IntentFilter intentFilter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        this.registerReceiver(mSMSReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // LogUtil.println("onDetachedFromWindow");
        if(mSMSReceiver!=null)
            this.unregisterReceiver(mSMSReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLastContent();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setLastContent(){

        String lastContent = getPreferenceString(com.irenebond.gsmmkey.MenuActivity.SYSTEMSETTINGS_SMS);
        if(lastContent.length() == 0)
            tv_last_content.setVisibility(View.INVISIBLE);
        else{
            tv_last_content.setVisibility(View.VISIBLE);
            tv_last_content.setText(lastContent);
        }
    }

    private void addListItem() {
        // TODO Auto-generated method stub
        HashMap<String,Object> map;
        Resources res=this.getResources();
        int[] workMode;
        int[] workModeHint;
        workMode = SMS_MODE;
        workModeHint = SMS_MODE_HINT;
        for(int i=0;i<workMode.length;i++){
            map=new HashMap<String,Object>();
            map.put(mColumns[0], res.getString(workMode[i]));
            map.put(mColumns[1], res.getString(workModeHint[i]));
            listItem.add(map);
        }
        simpleAdapter.notifyDataSetChanged();
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        onHandleSMSMode(position);
    }


    private void onHandleSMSMode(int position){
        switch(position){
            case ALARM_WORKING_MODE:
                onAlarmWorkingModeClick();
                break;
            case SET_ALARM_SMS_TEXT:
                onSetAlarmSMSClick();
                break;
            case GET_ALARM_SMS_TEXT:
                onGetAlarmSMSTextClick();
                break;
            case STATUS_CHECK:
                onStatusCheckClick();
                break;
            default:
                break;
        }
    }

    private void onStatusCheckClick(){
        Builder builder = new Builder(this);
        builder.setMessage(R.string.is_check_status);
        builder.setTitle(R.string.status_check);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sms="#PWD"+getPassword()+"#STATUS?";
                LogUtil.println("sms: " + sms);
                if(com.irenebond.gsmmkey.GateApplication.getInstance().isSMS)
                    sendSms(sms);
                else{
                    CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_SYSTEM_STATUS_DATA_CHECK, sms);
                    OutPackUtil.sendMessage(mCommandOutPacket);
                }
                closeTheDialog(dialog);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeTheDialog(dialog);
            }
        });
        builder.create().show();
    }

    private void onGetAlarmSMSTextClick(){

        Builder builder = new Builder(this);
        builder.setMessage(R.string.is_get_alarm_sms_text);
        builder.setTitle(R.string.get_alarm_sms_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String sms = "#PWD" + getPassword() + "#UDI?";
                String hint = getStringFromId(R.string.send_sms_successfully);
                LogUtil.println("sms: " + sms);
                if(com.irenebond.gsmmkey.GateApplication.getInstance().isSMS)
                    sendSms(sms);
                else{
                    CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_ALARM_TEXT_INQUIRY, sms);
                    OutPackUtil.sendMessage(mCommandOutPacket);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeTheDialog(dialog);
            }
        });
        builder.create().show();
    }

    private void onAlarmWorkingModeClick(){
        LayoutInflater inflater=this.getLayoutInflater();
        final View layout=inflater.inflate(R.layout.alarm_setting, (ViewGroup)findViewById(R.id.linearLayout_alarmSetting));
        final EditText edtTime1=(EditText)layout.findViewById(R.id.edtTime_1);
        final EditText edtTime2=(EditText)layout.findViewById(R.id.edtTime_2);
        final RadioGroup radioGroupONOFF=(RadioGroup)layout.findViewById(R.id.radioGroup_onoff);
        final RadioGroup radioGroupSMSPhone=(RadioGroup)layout.findViewById(R.id.radioGroup_smsphone);
        final RadioGroup radioGroupONOFF2=(RadioGroup)layout.findViewById(R.id.radioGroup_onoff2);
        final RadioGroup radioGroupSMSPhone2=(RadioGroup)layout.findViewById(R.id.radioGroup_smsphone2);
        // LogUtil.println("alarmWorkingMode: ");
        radioGroupONOFF.check(getAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_1_ON_OFF,R.id.radionBtn_off));
        radioGroupSMSPhone.check(getAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_1_SMS_PHONE,R.id.radionBtn_phone));
        edtTime1.setText(getAlarmTime(com.irenebond.gsmmkey.MenuActivity.ALARM_1_TIME));

        radioGroupONOFF2.check(getAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_2_ON_OFF, R.id.radionBtn_off2));
        radioGroupSMSPhone2.check(getAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_2_SMS_PHONE, R.id.radionBtn_phone2));
        edtTime2.setText(getAlarmTime(com.irenebond.gsmmkey.MenuActivity.ALARM_2_TIME));

        new Builder(mActivity).setTitle(R.string.alarm_setting)
                .setView(layout).setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String time1 = edtTime1.getText().toString().trim();
                String time2 = edtTime2.getText().toString().trim();
                if (time1.length() == 0) {
                    keepTheDialog(dialog, R.string.enter_alarm1_time);
                } else if (time2.length() == 0) {
                    keepTheDialog(dialog, R.string.enter_alarm2_time);
                } else {
                    if (getPhoneNo().length() != 0) {
                        setAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_1_ON_OFF, radioGroupONOFF);
                        setAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_1_SMS_PHONE, radioGroupSMSPhone);
                        setAlarmTime(com.irenebond.gsmmkey.MenuActivity.ALARM_1_TIME, edtTime1);

                        setAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_2_ON_OFF, radioGroupONOFF2);
                        setAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_2_SMS_PHONE, radioGroupSMSPhone2);
                        setAlarmTime(com.irenebond.gsmmkey.MenuActivity.ALARM_2_TIME, edtTime2);
                    }
                    String sms = "#PWD" + getPassword() + "#ALARM-IN1=";
                    if (getAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_1_ON_OFF, R.id.radionBtn_off) == R.id.radionBtn_off)
                        sms += "OFF:";
                    else
                        sms += "ON:";
                    if (getAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_1_SMS_PHONE, R.id.radionBtn_phone) == R.id.radionBtn_phone)
                        sms += "PHONE:";
                    else
                        sms += "SMS:";
                    sms += getAlarmTime(com.irenebond.gsmmkey.MenuActivity.ALARM_1_TIME) + ",ALARM-IN2=";

                    if (getAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_2_ON_OFF, R.id.radionBtn_off2) == R.id.radionBtn_off2)
                        sms += "OFF:";
                    else
                        sms += "ON:";
                    if (getAlarmSetting(com.irenebond.gsmmkey.MenuActivity.ALARM_2_SMS_PHONE, R.id.radionBtn_phone2) == R.id.radionBtn_phone2)
                        sms += "PHONE:";
                    else
                        sms += "SMS:";
                    sms += getAlarmTime(com.irenebond.gsmmkey.MenuActivity.ALARM_2_TIME);

                    LogUtil.println("sms: " + sms);
                    if (com.irenebond.gsmmkey.GateApplication.getInstance().isSMS)
                        sendSms(sms);
                    else {
                        CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_ALARM_WORKING_MODE, sms);
                        OutPackUtil.sendMessage(mCommandOutPacket);
                    }
                    closeTheDialog(dialog);
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                closeTheDialog(dialog);
            }
        }).show();
    }

    private CommandOutPacket getCommandOutPacket(int cmd,String info){
        showCustomLoadingView();
        CommandOutPacket mCommandOutPacket = new CommandOutPacket();
        mCommandOutPacket.setId(1);
        mCommandOutPacket.setCmd(cmd);
        mCommandOutPacket.setInfo(info);
        mCommandOutPacket.setSendto(com.irenebond.gsmmkey.GateApplication.getInstance().mGPRSDevicesListBean.getDeviceNo());
        return mCommandOutPacket;
    }

    private int getAlarmSetting(String key,int radioGroupONOFF){
        return com.irenebond.gsmmkey.GateApplication.getInstance().getSpecialSharedPreferences().getInt(key,radioGroupONOFF);
    }
    private void setAlarmSetting(String key,RadioGroup radioGroupONOFF) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor= com.irenebond.gsmmkey.GateApplication.getInstance().getSpecialSharedPreferences().edit();
        editor.putInt(key, radioGroupONOFF.getCheckedRadioButtonId());
        editor.commit();

    }

    private void setAlarmTime(String key,EditText edtTime) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor= com.irenebond.gsmmkey.GateApplication.getInstance().getSpecialSharedPreferences().edit();
        editor.putString(key, edtTime.getText().toString().trim());
        editor.commit();

    }
    private String getAlarmTime(String key){
        return com.irenebond.gsmmkey.GateApplication.getInstance().getSpecialSharedPreferences().getString(key,"");
    }

    private void onSetAlarmSMSClick(){
        LayoutInflater inflater=this.getLayoutInflater();
        final View layout=inflater.inflate(R.layout.alarm_message_setting, (ViewGroup)findViewById(R.id.linearLayoutAlarmSMSSetting));
        final EditText edtText=(EditText)layout.findViewById(R.id.edtAlarm1);
        final EditText edtText2=(EditText)layout.findViewById(R.id.edtAlarm2);
        new Builder(mActivity).setTitle(R.string.set_alarm_sms_text)
                .setView(layout).setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String smsContent = edtText.getText().toString().trim();
                String smsContent2 = edtText2.getText().toString().trim();
                if (smsContent.length() == 0 || smsContent2.length() == 0) {
                    keepTheDialog(dialog, R.string.please_input_alarm_hint);
                } else {
                    String sms = "#PWD" + getPassword() + "#UDI1:" + smsContent
                            + ",UDI2:" + smsContent2;
                    LogUtil.println("sms: " + sms);
                    if (com.irenebond.gsmmkey.GateApplication.getInstance().isSMS)
                        sendSms(sms);
                    else {
                        CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_SET_ALARM_SMS_TEXT, sms);
                        OutPackUtil.sendMessage(mCommandOutPacket);
                    }
                    closeTheDialog(dialog);
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeTheDialog(dialog);
            }
        }).show();
    }

    private String getStringFromId(int text){
        Resources res=this.getResources();
        return res.getString(text);
    }
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what){
                case 1:
                    com.irenebond.gsmmkey.MenuActivity.instance.updateSMSContent(msg.getData(),tv_last_content);
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private void showSMSContent(Bundle bundle){
        Message msg=handler.obtainMessage();
        msg.what=1;
        msg.setData(bundle);
        msg.sendToTarget();
    }
    private class SMSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras()!=null)
                showSMSContent(intent.getExtras());
        }
    }

    @Override
    public void notifyTcpPacketArrived(int mCommand, final String mPacket) {
        super.notifyTcpPacketArrived(mCommand, mPacket);
        switch (mCommand){
            case Config.COMMAND_ALARM_WORKING_MODE:
            case Config.COMMAND_SET_ALARM_SMS_TEXT:
            case Config.COMMAND_ALARM_TEXT_INQUIRY:
            case Config.COMMAND_SYSTEM_STATUS_DATA_CHECK:
                showCustomNormalView();
                CommandInPacket mCommandInPacket = JSON.parseObject(mPacket, CommandInPacket.class);
//                Toast.makeText(mActivity, mCommandInPacket.getInfo(), Toast.LENGTH_SHORT).show();

                String info = mCommandInPacket.getInfo();
                if(info.equals("send success"))
                    return;
                setPreferenceString(com.irenebond.gsmmkey.MenuActivity.AUTHORIZEDNOSYSTEM_SMS,getCurrentTime() + info);
                setLastContent();
                Toast.makeText(mActivity, mCommandInPacket.getInfo(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
