
package com.irenebond.gsmmkey;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog.Builder;

import com.alibaba.fastjson.JSON;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.OutPackUtil;
import com.irenebond.gsmmkey.network.SocketThreadManager;
import com.irenebond.gsmmkey.network.packet.CommandInPacket;
import com.irenebond.gsmmkey.network.packet.CommandOutPacket;

import irene.com.framework.util.LogUtil;

public class SetCustomizedCommandActivity extends NetBaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView=null;
    private TextView tv_last_content=null;
    private SimpleAdapter simpleAdapter;
    private  final String mColumns[]={"txtName","txtInfo"};
    private  final int mViewIds[]={
            R.id.txtView_name,
            R.id.txtView_info,
    };

    private final int ITEMS[]={
            R.string.setup_customized_open1_text,
            R.string.setup_customized_close1_text,
            R.string.setup_customized_pulse_control1_text,
            R.string.setup_customized_open2_text,
            R.string.setup_customized_close2_text,
            R.string.setup_customized_pluse_control2_text,
            R.string.check_customized_command_text,
            R.string.check_customized_command2_text
    };

    private final int ITMES_HINT[]={
            R.string.setup_customized_open1_text_hint,
            R.string.setup_customized_close1_text_hint,
            R.string.setup_customized_pulse_control1_text_hint,
            R.string.setup_customized_open2_text_hint,
            R.string.setup_customized_close_text2_hint,
            R.string.setup_customized_pluse_control2_text_hint,
            R.string.check_customized_command_text_hint,
            R.string.check_customized_command2_text_hint
    };
    private  ArrayList<HashMap<String,Object>> listItem;

    private static final int SETUP_CUSTOMIZED_OPEN1_TEXT = 0;
    private static final int SETUP_CUSTOMIZED_CLOSE1_TEXT=1;
    private static final int SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT=2;
    private static final int SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT=3;
    private static final int SETUP_CUSTOMIZED_OPEN2_TEXT = 4;
    private static final int SETUP_CUSTOMIZED_CLOSE2_TEXT=5;
    private static final int SETUP_CUSTOMIZED_COMMAND_TEXT=6;
    private static final int SETUP_CUSTOMIZED_COMMAND2_TEXT=7;

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
        mTitleBar.setTitle("Setup the customized command");
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

        tv_last_content = (TextView)findViewById(R.id.tv_last_content);
        mSMSReceiver=new SMSReceiver();
        IntentFilter intentFilter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        this.registerReceiver(mSMSReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // System.out.println("onDetachedFromWindow");
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

        String lastContent = getPreferenceString(MenuActivity.SYSTEMSETTINGS_SMS);
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
        for(int i=0;i<ITEMS.length;i++){
            map=new HashMap<String,Object>();
            map.put(mColumns[0], res.getString(ITEMS[i]));
            map.put(mColumns[1], res.getString(ITMES_HINT[i]));
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
            case SETUP_CUSTOMIZED_OPEN1_TEXT :
                onCommonSend(R.string.setup_customized_open1_text,"Open1 Text","OPEN1-TEXT", Config.COMMAND_SETUP_CUSTOMIZED_OPEN1_TEXT);
                break;
            case SETUP_CUSTOMIZED_CLOSE1_TEXT :
                onCommonSend(R.string.setup_customized_close1_text,"Close1 Text","CLOSE1-TEXT", Config.COMMAND_SETUP_CUSTOMIZED_CLOSE1_TEXT);
                break;
            case SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT :
                onCommonSend(R.string.setup_customized_pulse_control1_text,"Pulse1 Control Text","RLY-TEXT1", Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT);
                break;
            case SETUP_CUSTOMIZED_COMMAND_TEXT :
                statusCheck("Check OUT1 Customized Command Text", "Check OUT1 Customized Command Text Now?", "OUT1-TEXT", Config.COMMAND_SETUP_CUSTOMIZED_COMMAND_TEXT);
                break;
            case SETUP_CUSTOMIZED_OPEN2_TEXT :
                onCommonSend(R.string.setup_customized_open2_text,"Open2 Text","OPEN2-TEXT", Config.COMMAND_SETUP_CUSTOMIZED_OPEN2_TEXT);
                break;
            case SETUP_CUSTOMIZED_CLOSE2_TEXT :
                onCommonSend(R.string.setup_customized_close2_text,"Close2 Text","CLOSE2-TEXT", Config.COMMAND_SETUP_CUSTOMIZED_CLOSE2_TEXT);
                break;
            case SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT :
                onCommonSend(R.string.setup_customized_pluse_control2_text,"Pulse2 Control Text","RLY-TEXT2", Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT);
                break;
            case SETUP_CUSTOMIZED_COMMAND2_TEXT :
                statusCheck("Check OUT2 Customized Command Text","Check OUT2 Customized Command Text Now?","OUT2-TEXT",Config.COMMAND_SETUP_CUSTOMIZED_COMMAND2_TEXT);
                break;
            default:
                break;
        }
    }

    private void statusCheck(String title,String question,final String hint,final int command) {
        // TODO Auto-generated method stub
        Builder builder = new Builder(this);
        builder.setMessage(question);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sms="#PWD"+getPassword()+"#" + hint + "?";
                LogUtil.println("sms: " + sms);
                if (GateApplication.getInstance().isSMS)
                    sendSms(sms);
                else {
                    CommandOutPacket mCommandOutPacket = getCommandOutPacket(command, sms);
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

    private void onCommonSend(int title,String hint,final String sendContent,final int command){

        LayoutInflater inflater=this.getLayoutInflater();
        final View layout=inflater.inflate(R.layout.dlg_set_customized_command, (ViewGroup)findViewById(R.id.ll_main));
        final EditText et_content=(EditText)layout.findViewById(R.id.et_content);
        et_content.setText(getTextContent(sendContent));
        final TextView tv_hint=(TextView)layout.findViewById(R.id.tv_hint);
        tv_hint.setText(hint);
        new Builder(mActivity).setTitle(title)
                .setView(layout).setPositiveButton(R.string.send,new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String content = et_content.getText().toString().trim();
                setTextContent(sendContent,content);
                String sms="#PWD"+getPassword()+"#"+sendContent + "#" + content;
                String hint=getStringFromId(R.string.send_sms_successfully);
                LogUtil.println("sms: " + sms);
                if (GateApplication.getInstance().isSMS)
                    sendSms(sms);
                else {
                    CommandOutPacket mCommandOutPacket = getCommandOutPacket(command, sms);
                    OutPackUtil.sendMessage(mCommandOutPacket);
                }

                closeTheDialog(dialog);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                closeTheDialog(dialog);
            }
        }).show();
    }


    private String getTextContent(String key){
        return GateApplication.getInstance().getSpecialSharedPreferences().getString(key, "");
    }
    private void setTextContent(String key,String content) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
        editor.putString(key, content);
        editor.commit();
        System.out.println("IreneBond 存储key: " + key + " content:" + content);
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
                    String content = MenuActivity.instance.updateSMSContent(msg.getData(), tv_last_content);
                    if(content.contains("OPEN1-TEXT")){
                        setContent1(content);
                    }else if(content.contains("OPEN2-TEXT")){
                        setContent2(content);
                    }
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
    private class SMSReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getExtras()!=null)
                showSMSContent(intent.getExtras());
        }

    }

    private CommandOutPacket getCommandOutPacket(int cmd,String info){
        showCustomLoadingView();
        CommandOutPacket mCommandOutPacket = new CommandOutPacket();
        mCommandOutPacket.setId(1);
        mCommandOutPacket.setCmd(cmd);
        mCommandOutPacket.setInfo(info);
        mCommandOutPacket.setSendto(GateApplication.getInstance().mGPRSDevicesListBean.getDeviceNo());
        return mCommandOutPacket;
    }

    @Override
    public void notifyTcpPacketArrived(int mCommand, final String mPacket) {
        super.notifyTcpPacketArrived(mCommand, mPacket);
        switch (mCommand){
            case Config.COMMAND_SETUP_CUSTOMIZED_OPEN1_TEXT:
            case Config.COMMAND_SETUP_CUSTOMIZED_CLOSE1_TEXT:
            case Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT:
            case Config.COMMAND_SETUP_CUSTOMIZED_COMMAND_TEXT:
            case Config.COMMAND_SETUP_CUSTOMIZED_OPEN2_TEXT:
            case Config.COMMAND_SETUP_CUSTOMIZED_CLOSE2_TEXT:
            case Config.COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT:
            case Config.COMMAND_SETUP_CUSTOMIZED_COMMAND2_TEXT:
                showCustomNormalView();
                CommandInPacket mCommandInPacket = JSON.parseObject(mPacket, CommandInPacket.class);
                String info = mCommandInPacket.getInfo();
                if(info.equals("send success"))
                    return;
                setPreferenceString(MenuActivity.AUTHORIZEDNOSYSTEM_SMS,getCurrentTime() + info);
                setLastContent();
                Toast.makeText(mActivity, mCommandInPacket.getInfo(), Toast.LENGTH_SHORT).show();
                if(mCommand == Config.COMMAND_SETUP_CUSTOMIZED_COMMAND2_TEXT){
                    setContent2(info);
                }else if(mCommand == Config.COMMAND_SETUP_CUSTOMIZED_COMMAND_TEXT){
                    setContent1(info);
                }
                break;
            default:
                break;
        }
    }

    private void setContent2(String content){
        String openText = content.substring("OPEN2-TEXT=".length(),content.indexOf(","));
        content = content.substring(content.indexOf(",") + 1,content.length());
        String closeText = content.substring("CLOSE2-TEXT=".length(),content.indexOf(","));
        content = content.substring(content.indexOf(",") + 1,content.length());
        String trigText = content.substring("TRIG2-TEXT=".length(),content.length());
        setTextContent("OPEN2-TEXT", openText);
        setTextContent("CLOSE2-TEXT", closeText);
        setTextContent("RLY-TEXT2", trigText);
    }

    private void setContent1(String content){
        String openText = content.substring("OPEN1-TEXT=".length(),content.indexOf(","));
        content = content.substring(content.indexOf(",") + 1,content.length());
        String closeText = content.substring("CLOSE1-TEXT=".length(),content.indexOf(","));
        content = content.substring(content.indexOf(",") + 1,content.length());
        String trigText = content.substring("TRIG1-TEXT=".length(),content.length());
        LogUtil.println("IreneBond openText: " + openText + " closeText: " + closeText + " trigText: " + trigText);
        setTextContent("OPEN1-TEXT", openText);
        setTextContent("CLOSE1-TEXT", closeText);
        setTextContent("RLY-TEXT1", trigText);
    }

    @Override
    public void notifySMSPacketArrived(String mContent) {
        super.notifySMSPacketArrived(mContent);
        if(mContent.contains("OPEN1-TEXT")){
            setContent1(mContent);
        }else if(mContent.contains("OPEN2-TEXT")){
            setContent2(mContent);
        }
    }
}
