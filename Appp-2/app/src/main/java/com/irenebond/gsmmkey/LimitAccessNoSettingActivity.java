package com.irenebond.gsmmkey;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Calendar;
import java.util.HashMap;

import irene.com.framework.util.LogUtil;

/**
 * Created by Irene on 2016/4/5.
 */
public class LimitAccessNoSettingActivity extends NetBaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView=null;
    private SimpleAdapter simpleAdapter;
    private TextView tv_last_content;
    private  final String mColumns[]={"txtName","txtInfo"};
    private  final int mViewIds[]={
            R.id.txtView_name,
            R.id.txtView_info,
    };
    private ArrayList<HashMap<String,Object>> listItem;
    private SMSReceiver mSMSReceiver;
    public static final int WEEKLY_ACCESS_NUMBER = 0;
    public static final int DAILY_ACCESS_NUMBER = 1;

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
        mTitleBar.setTitle(R.string.limit_access_no_settings);
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

    private void addListItem() {
        // TODO Auto-generated method stub
        HashMap<String,Object> map=new HashMap<String,Object>();
        Resources res=this.getResources();
        map.put(mColumns[0],res.getString(R.string.weekly_access_number));
        map.put(mColumns[1],res.getString(R.string.pwd_weekly_access_number));
        listItem.add(map);


        map=new HashMap<String,Object>();
        map.put(mColumns[0],res.getString(R.string.daily_access_number));
        map.put(mColumns[1],res.getString(R.string.pwd_daily_access_number));
        listItem.add(map);

        simpleAdapter.notifyDataSetChanged();
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        switch(arg2){
            case WEEKLY_ACCESS_NUMBER:
                onWeeklyAccessNumber();
                break;
            case DAILY_ACCESS_NUMBER:
                onDailyAccessNumber();
                break;
            default:
                break;
        }
    }

    boolean isSelected[];
    private void onWeeklyAccessNumber() {
        LayoutInflater inflater=this.getLayoutInflater();
        final View layout=inflater.inflate(R.layout.dlg_limit_access_no_setting, (ViewGroup) findViewById(R.id.set_authorized));
        isSelected = new boolean[7];
        isSelected[0] = false;
        isSelected[1] = false;
        isSelected[2] = false;
        isSelected[3] = false;
        isSelected[4] = false;
        isSelected[5] = false;
        isSelected[6] = false;

        final int color_normal_bg = getResources().getColor(R.color.color_ccc);
        final int color_select_bg = getResources().getColor(R.color.color_1abc9c);
        final int textColorNormal = getResources().getColor(R.color.color_666);
        final int textColorSelect = getResources().getColor(R.color.color_1abc9c);
        final EditText et_serial_no = (EditText) layout.findViewById(R.id.et_serial_no);
        if(number == 2000)
            et_serial_no.setHint("Input Serial No.(01 to 200)");

        final EditText et_shl_no = (EditText) layout.findViewById(R.id.et_shl_no);

        final EditText et_start_time_h = (EditText) layout.findViewById(R.id.et_start_time_h);
        final EditText et_start_time_min = (EditText) layout.findViewById(R.id.et_start_time_min);

        final EditText et_end_time_h = (EditText) layout.findViewById(R.id.et_end_time_h);
        final EditText et_end_time_min = (EditText) layout.findViewById(R.id.et_end_time_min);

        final TextView tv_mon = (TextView) layout.findViewById(R.id.tv_mon);
        final TextView tv_tues = (TextView) layout.findViewById(R.id.tv_tues);
        final TextView tv_wed = (TextView) layout.findViewById(R.id.tv_wed);
        final TextView tv_thur = (TextView) layout.findViewById(R.id.tv_thur);
        final TextView tv_fri = (TextView) layout.findViewById(R.id.tv_fri);
        final TextView tv_sat = (TextView) layout.findViewById(R.id.tv_sat);
        final TextView tv_sun = (TextView) layout.findViewById(R.id.tv_sun);

        final View view_1 = (View) layout.findViewById(R.id.view_1);
        final View view_2 = (View) layout.findViewById(R.id.view_2);
        final View view_3 = (View) layout.findViewById(R.id.view_3);
        final View view_4 = (View) layout.findViewById(R.id.view_4);
        final View view_5 = (View) layout.findViewById(R.id.view_5);
        final View view_6 = (View) layout.findViewById(R.id.view_6);

        final LinearLayout ll_tues = (LinearLayout) layout.findViewById(R.id.ll_tues);
        final LinearLayout ll_wed = (LinearLayout) layout.findViewById(R.id.ll_wed);
        final LinearLayout ll_thur = (LinearLayout) layout.findViewById(R.id.ll_thur);
        final LinearLayout ll_fri = (LinearLayout) layout.findViewById(R.id.ll_fri);
        final LinearLayout ll_sat = (LinearLayout) layout.findViewById(R.id.ll_sat);

        tv_mon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isSelected[0] = !isSelected[0];
                if(isSelected[0]){
                    tv_mon.setTextColor(textColorSelect);
                    tv_mon.setBackgroundResource(R.drawable.shape_around_bg_left_color_1abc9c);
                    view_1.setBackgroundColor(textColorSelect);
                } else {
                    tv_mon.setBackgroundResource(R.drawable.shape_around_bg_left_color_ccc);
                    tv_mon.setTextColor(textColorNormal);
                    if(!isSelected[1]){
                        view_1.setBackgroundColor(textColorNormal);
                    }
                }
            }
        });
        ll_tues.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                isSelected[1] = !isSelected[1];
                if (isSelected[1]) {
                    ll_tues.setBackgroundColor(color_select_bg);
                    tv_tues.setTextColor(textColorSelect);
                    view_2.setBackgroundColor(textColorSelect);
                    view_1.setBackgroundColor(textColorSelect);
                } else {
                    ll_tues.setBackgroundColor(color_normal_bg);
                    tv_tues.setTextColor(textColorNormal);
                    if(!isSelected[2]){
                        view_2.setBackgroundColor(textColorNormal);
                    }
                    if(!isSelected[0]){
                        view_1.setBackgroundColor(textColorNormal);
                    }
                }
            }
        });
        ll_wed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isSelected[2] = !isSelected[2];
                if (isSelected[2]) {
                    ll_wed.setBackgroundColor(color_select_bg);
                    tv_wed.setTextColor(textColorSelect);
                    view_3.setBackgroundColor(textColorSelect);
                    view_2.setBackgroundColor(textColorSelect);
                } else {
                    ll_wed.setBackgroundColor(color_normal_bg);
                    tv_wed.setTextColor(textColorNormal);
                    if(!isSelected[3]){
                        view_3.setBackgroundColor(textColorNormal);
                    }
                    if(!isSelected[1]){
                        view_2.setBackgroundColor(textColorNormal);
                    }
                }
            }
        });
        ll_thur.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isSelected[3] = !isSelected[3];
                if (isSelected[3]) {
                    ll_thur.setBackgroundColor(color_select_bg);
                    tv_thur.setTextColor(textColorSelect);
                    view_3.setBackgroundColor(textColorSelect);
                    view_4.setBackgroundColor(textColorSelect);
                } else {
                    ll_thur.setBackgroundColor(color_normal_bg);
                    tv_thur.setTextColor(textColorNormal);
                    if(!isSelected[4]){
                        view_4.setBackgroundColor(textColorNormal);
                    }
                    if(!isSelected[2]){
                        view_3.setBackgroundColor(textColorNormal);
                    }
                }
            }
        });
        ll_fri.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isSelected[4] = !isSelected[4];
                if (isSelected[4]) {
                    ll_fri.setBackgroundColor(color_select_bg);
                    tv_fri.setTextColor(textColorSelect);
                    view_4.setBackgroundColor(textColorSelect);
                    view_5.setBackgroundColor(textColorSelect);
                } else {
                    ll_fri.setBackgroundColor(color_normal_bg);
                    tv_fri.setTextColor(textColorNormal);
                    if(!isSelected[5]){
                        view_5.setBackgroundColor(textColorNormal);
                    }
                    if(!isSelected[3]){
                        view_4.setBackgroundColor(textColorNormal);
                    }
                }
            }
        });
        ll_sat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isSelected[5] = !isSelected[5];
                if (isSelected[5]) {
                    ll_sat.setBackgroundColor(color_select_bg);
                    tv_sat.setTextColor(textColorSelect);
                    view_5.setBackgroundColor(textColorSelect);
                    view_6.setBackgroundColor(textColorSelect);
                } else {
                    ll_sat.setBackgroundColor(color_normal_bg);
                    tv_sat.setTextColor(textColorNormal);
                    if(!isSelected[6]){
                        view_6.setBackgroundColor(textColorNormal);
                    }
                    if(!isSelected[4]){
                        view_5.setBackgroundColor(textColorNormal);
                    }
                }
            }
        });
        tv_sun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isSelected[6] = !isSelected[6];
                if (isSelected[6]) {
                    tv_sun.setTextColor(textColorSelect);
                    tv_sun.setBackgroundResource(R.drawable.shape_around_bg_right_color_1abc9c);
                    view_6.setBackgroundColor(textColorSelect);
                } else {
                    tv_sun.setTextColor(textColorNormal);
                    tv_sun.setBackgroundResource(R.drawable.shape_around_bg_right_color_ccc);
                    if(!isSelected[5]){
                        view_6.setBackgroundColor(textColorNormal);
                    }
                }
            }
        });
        new AlertDialog.Builder(LimitAccessNoSettingActivity.this).setTitle("Weekly Limit Access NO. Setting")
                .setView(layout).setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String serialNo = et_serial_no.getText().toString();
                String shlNo = et_shl_no.getText().toString();
                String startTimeH = et_start_time_h.getText().toString();
                String startTimeMin = et_start_time_min.getText().toString();
                String endTimeH = et_end_time_h.getText().toString();
                String endTimeMin = et_end_time_min.getText().toString();
                int maxNumber = 50;
                if(number == 2000)
                    maxNumber = 200;
                if (serialNo.length() == 0) {
                    keepTheDialog(dialog, et_serial_no.getHint().toString());
                } else if (Integer.parseInt(serialNo) > maxNumber) {
                    keepTheDialog(dialog, "More than " + maxNumber);
                } else if (shlNo.length() == 0) {
                    keepTheDialog(dialog, "Input SHL NO");
                } else if (startTimeH.length() == 0) {
                    keepTheDialog(dialog, "Please input full setting information of Hour data");
                } else if (Integer.parseInt(startTimeH) > 23) {
                    keepTheDialog(dialog, "More than 23");
                } else if (startTimeMin.length() == 0) {
                    keepTheDialog(dialog, "Please input full setting information of Minute data");
                } else if (Integer.parseInt(startTimeMin) > 59) {
                    keepTheDialog(dialog, "More than 59");
                } else if (endTimeH.length() == 0) {
                    keepTheDialog(dialog, "Please input full setting information of Hour data");
                } else if (Integer.parseInt(endTimeH) > 23) {
                    keepTheDialog(dialog, "More than 23");
                } else if (endTimeMin.length() == 0) {
                    keepTheDialog(dialog, "Please input full setting information of Minute data");
                } else if (Integer.parseInt(endTimeMin) > 59) {
                    keepTheDialog(dialog, "More than 59");
                } else if (Integer.parseInt(startTimeH) > Integer.parseInt(endTimeH) || (Integer.parseInt(startTimeH) == Integer.parseInt(endTimeH) && Integer.parseInt(startTimeMin) > Integer.parseInt(endTimeMin))) {
                    keepTheDialog(dialog, "Start time must be earlier than End time");
                } else if (!isSelected(isSelected)) {
                    keepTheDialog(dialog, "please input the proper weekly day");
                } else {
                    if (serialNo.length() == 1) {
                        serialNo = "00" + serialNo;
                    } else if (serialNo.length() == 2) {
                        serialNo = "0" + serialNo;
                    }
                    String day = "";
                    if (isSelected[0])
                        day += "1";
                    if (isSelected[1])
                        day += "2";
                    if (isSelected[2])
                        day += "3";
                    if (isSelected[3])
                        day += "4";
                    if (isSelected[4])
                        day += "5";
                    if (isSelected[5])
                        day += "6";
                    if (isSelected[6])
                        day += "7";
//                    #PWD123456#SHL001=13564121668:1357(8:32-23:59)
                    String sms = "#PWD" + getPassword() + "#SHL" + serialNo + "=" + shlNo + ":" + day + "(" + startTimeH + ":" + startTimeMin + "-" + endTimeH + ":" + endTimeMin + ")";
                    LogUtil.println("sms: " + sms);
                    if (GateApplication.getInstance().isSMS)
                        sendSms(sms);
                    else {
                        CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.WEEKLY_ACCESS_NO, sms);
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

    private boolean isSelected(boolean[] isSelected){
        for(int index = 0;index < isSelected.length;index++){
            if(isSelected[index])
                return true;
        }
        return false;
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

    private int getDayOfYearAndMonth(String year,String month){

        Calendar time=Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, Integer.parseInt(year)); //year 为 int
        time.set(Calendar.MONTH, Integer.parseInt(month) - 1); //month 为int
        return time.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    private void onDailyAccessNumber() {
        LayoutInflater inflater=this.getLayoutInflater();
        final View layout=inflater.inflate(R.layout.dlg_limit_access_day_no_setting, (ViewGroup) findViewById(R.id.set_authorized));
        final EditText et_serial_no = (EditText) layout.findViewById(R.id.et_serial_no);
        if(number == 2000)
            et_serial_no.setHint("Input Serial No.(01 to 200)");
        final EditText et_shl_no = (EditText) layout.findViewById(R.id.et_shl_no);

        final EditText et_start_time_h = (EditText) layout.findViewById(R.id.et_start_time_h);
        final EditText et_start_time_min = (EditText) layout.findViewById(R.id.et_start_time_min);

        final EditText et_end_time_h = (EditText) layout.findViewById(R.id.et_end_time_h);
        final EditText et_end_time_min = (EditText) layout.findViewById(R.id.et_end_time_min);

        final EditText et_start_day_year = (EditText) layout.findViewById(R.id.et_start_day_year);
        final EditText et_start_day_month = (EditText) layout.findViewById(R.id.et_start_day_month);
        final EditText et_start_day_day = (EditText) layout.findViewById(R.id.et_start_day_day);

        final EditText et_end_day_year = (EditText) layout.findViewById(R.id.et_end_day_year);
        final EditText et_end_day_month = (EditText) layout.findViewById(R.id.et_end_day_month);
        final EditText et_end_day_day = (EditText) layout.findViewById(R.id.et_end_day_day);

        new AlertDialog.Builder(LimitAccessNoSettingActivity.this).setTitle("Daily Limit Access NO. Setting")
                .setView(layout).setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String serialNo = et_serial_no.getText().toString();
                String shlNo = et_shl_no.getText().toString();
                String startTimeH = et_start_time_h.getText().toString();
                String startTimeMin = et_start_time_min.getText().toString();
                String endTimeH = et_end_time_h.getText().toString();
                String endTimeMin = et_end_time_min.getText().toString();

                String startYear = et_start_day_year.getText().toString();
                String startMonth = et_start_day_month.getText().toString();
                String startDay = et_start_day_day.getText().toString();

                String endYear = et_end_day_year.getText().toString();
                String endMonth = et_end_day_month.getText().toString();
                String endDay = et_end_day_day.getText().toString();
                int maxNumber = 50;
                if(number == 2000)
                    maxNumber = 200;
                if (serialNo.length() == 0) {
                    keepTheDialog(dialog,et_serial_no.getHint().toString());
                } else if (Integer.parseInt(serialNo) > maxNumber) {
                    keepTheDialog(dialog, "More than " + maxNumber);
                } else if (shlNo.length() == 0) {
                    keepTheDialog(dialog, "Input SHL NO");
                } else if (startTimeH.length() == 0) {
                    keepTheDialog(dialog, "Please input full setting information of Hour data");
                } else if (Integer.parseInt(startTimeH) > 23) {
                    keepTheDialog(dialog, "More than 23");
                } else if (startTimeMin.length() == 0) {
                    keepTheDialog(dialog, "Please input full setting information of Minute data");
                } else if (Integer.parseInt(startTimeMin) > 59) {
                    keepTheDialog(dialog, "More than 59");
                } else if (endTimeH.length() == 0) {
                    keepTheDialog(dialog, "Please input full setting information of Hour data");
                } else if (Integer.parseInt(endTimeH) > 23) {
                    keepTheDialog(dialog, "More than 23");
                } else if (endTimeMin.length() == 0) {
                    keepTheDialog(dialog, "Please input full setting information of Minute data");
                }else if (Integer.parseInt(endTimeMin) > 59) {
                    keepTheDialog(dialog, "More than 59");
                } else if (Integer.parseInt(startTimeH) > Integer.parseInt(endTimeH) || (Integer.parseInt(startTimeH) == Integer.parseInt(endTimeH) && Integer.parseInt(startTimeMin) > Integer.parseInt(endTimeMin))) {
                    keepTheDialog(dialog, "Start time must be earlier than End time");
                }else if (Integer.parseInt(startMonth) > 12 || Integer.parseInt(endMonth) > 12) {
                    keepTheDialog(dialog, "More than 12");
                }else if(Integer.parseInt(startYear) > Integer.parseInt(endYear)
                        || (Integer.parseInt(startYear) == Integer.parseInt(endYear) && Integer.parseInt(startMonth) > Integer.parseInt(endMonth))
                        || (Integer.parseInt(startYear) == Integer.parseInt(endYear) && Integer.parseInt(startMonth) == Integer.parseInt(endMonth) && Integer.parseInt(startDay) == Integer.parseInt(endDay))){
                    keepTheDialog(dialog, "Start day must be earlier than End day");
                }else if(startYear.length() == 0 || endYear.length() == 0){
                    keepTheDialog(dialog, "Please input full setting information of Year data");
                }else if(startMonth.length() == 0 || endMonth.length() == 0){
                    keepTheDialog(dialog, "Please input full setting information of Month data");
                }else if(startDay.length() == 0 || endDay.length() == 0){
                    keepTheDialog(dialog, "Please input full setting information of Day data");
                }else {
                    if(Integer.parseInt(startDay) > getDayOfYearAndMonth(startYear,startMonth)){
                        keepTheDialog(dialog, "More than " + getDayOfYearAndMonth(startYear,startMonth));
                        return;
                    }else if(Integer.parseInt(endDay) > getDayOfYearAndMonth(endYear,endMonth)){
                        keepTheDialog(dialog, "More than " + getDayOfYearAndMonth(endYear,endMonth));
                        return;
                    }

                    if (serialNo.length() == 1) {
                        serialNo = "00" + serialNo;
                    } else if (serialNo.length() == 2) {
                        serialNo = "0" + serialNo;
                    }

                    startYear = getRightData(startYear);
                    startMonth = getRightData(startMonth);
                    startDay = getRightData(startDay);

                    endYear = getRightData(endYear);
                    endMonth = getRightData(endMonth);
                    endDay = getRightData(endDay);
//                    指令如下：#PWD123456#PHL001=13564121668:100324-101012(8:32-18:26)
                    String sms = "#PWD" + getPassword() + "#PHL" + serialNo + "=" + shlNo + ":" + startYear + startMonth + startDay + "-" + endYear + endMonth + endDay + "(" + startTimeH + ":" + startTimeMin + "-" + endTimeH + ":" + endTimeMin + ")";
                    LogUtil.println("sms: " + sms);
                    if (GateApplication.getInstance().isSMS)
                        sendSms(sms);
                    else {
                        CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.DAILY_ACCESS_NO, sms);
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

    private String getRightData(String mContent){

        if(mContent.length() == 1)
            mContent = "0" + mContent;
        return mContent;
    }
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what){
                case 1:
                    MenuActivity.instance.updateSMSContent(msg.getData(),tv_last_content);
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
            // TODO Auto-generated method stub
            if(intent.getExtras()!=null)
                showSMSContent(intent.getExtras());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // // LogUtil.println("onDestroy");
        if(mSMSReceiver!=null)
            this.unregisterReceiver(mSMSReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLastContent();
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

    @Override
    public void notifyTcpPacketArrived(int mCommand, final String mPacket) {
        super.notifyTcpPacketArrived(mCommand, mPacket);
        switch (mCommand){
            case Config.WEEKLY_ACCESS_NO:
            case Config.DAILY_ACCESS_NO:
                showCustomNormalView();
                CommandInPacket mCommandInPacket = JSON.parseObject(mPacket, CommandInPacket.class);
                String info = mCommandInPacket.getInfo();
                if(info.equals("send success"))
                    return;
                setPreferenceString(MenuActivity.AUTHORIZEDNOSYSTEM_SMS,getCurrentTime() + info);
                setLastContent();
                Toast.makeText(mActivity, mCommandInPacket.getInfo(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

}
