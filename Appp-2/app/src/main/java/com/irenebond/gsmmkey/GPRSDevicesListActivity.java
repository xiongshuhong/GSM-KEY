package com.irenebond.gsmmkey;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.app.AlertDialog.Builder;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.irenebond.gsmmkey.adapter.GPRSDevicesListAdapter;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.bean.ReadAPNInfoBean;
import com.irenebond.gsmmkey.data.GPRSDevicesListBean;
import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.OutPackUtil;
import com.irenebond.gsmmkey.network.SocketThreadManager;
import com.irenebond.gsmmkey.network.packet.CommandOutPacket;
import com.irenebond.gsmmkey.network.packet.GetDeviceNameInPacket;
import com.irenebond.gsmmkey.network.packet.LoginInPacket;

import java.util.ArrayList;
import irene.com.framework.util.LogUtil;

/**
 * Created by Irene on 2016/1/26.
 */
public class GPRSDevicesListActivity extends NetBaseActivity implements View.OnClickListener{

    private GridView gv_devices_list;
    private LinearLayout ll_register_to_device;
    private GPRSDevicesListAdapter mGPRSDevicesListAdapter;
    private ArrayList<GPRSDevicesListBean> mAllDevices = new ArrayList<GPRSDevicesListBean>();
    private ArrayList<GPRSDevicesListBean> mOnLineDevices = new ArrayList<GPRSDevicesListBean>();
    private boolean isAll = false;
    public static GPRSDevicesListActivity instance = null;
    String content = "{\"data\": [{ \"name\": \"NH\", \"list\": [ {\"apn\": \"web.vodafone.nl#vodafone#vodafone\"},{ \"apn\": \"movistar.es#MOVISTAR#MOVISTAR\" },{\"apn\": \"imovil.entelpcs.cl#entelpcs#entelpcs\" } ] },{ \"name\": \"ESP\", \"list\": [{\"apn\": \"gprs.pepephone.com##\"}] },{\"name\": \"CH\",\"list\": [ {\"apn\": \"bam.clarochile.cl#clarochile#clarochile\"} ] } ]}";
    @Override
    public void installViews() {
        super.installViews();
        setContentView(R.layout.activity_devices_list);
        ReadAPNInfoBean mReadAPNInfoBean = getPacket(content, ReadAPNInfoBean.class);
        LogUtil.println("IreneBond size: " + mReadAPNInfoBean.getData().size());
        instance = this;
        mTitleBar.setTitleOnly("GPRS Devices List");
        mTitleBar.setRightContent("OnLine");
        mTitleBar.setRightBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAll = !isAll;
                updateDevices();
            }
        });
        ll_register_to_device = (LinearLayout) findViewById(R.id.ll_register_to_device);
        ll_register_to_device.setOnClickListener(this);
        gv_devices_list = (GridView) findViewById(R.id.gv_devices_list);
        mGPRSDevicesListAdapter = new GPRSDevicesListAdapter(mActivity);
        gv_devices_list.setAdapter(mGPRSDevicesListAdapter);
        initData();
        getDeviceName();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getDeviceName(){
        for(int index = 0;index < mAllDevices.size();index++){
            GPRSDevicesListBean mGPRSDevicesListBean = mAllDevices.get(index);
            if(mGPRSDevicesListBean.getDeviceStatus().equals("ON")){
                CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_GET_DEVICE_NAME, "#PWD123456#DEVICE?",mGPRSDevicesListBean.getDeviceNo());
                LogUtil.println("IreneBond mCommandOutPacket: " + SocketThreadManager.getInstance());
                OutPackUtil.sendMessage(mCommandOutPacket);
            }
        }
    }

    private CommandOutPacket getCommandOutPacket(int cmd,String info,String device){
        CommandOutPacket mCommandOutPacket = new CommandOutPacket();
        mCommandOutPacket.setId(1);
        mCommandOutPacket.setCmd(cmd);
        mCommandOutPacket.setInfo(info);
        mCommandOutPacket.setSendto(device);
        return mCommandOutPacket;
    }

    private void initData(){
        Intent mIntent = getIntent();
        String devicesList = mIntent.getStringExtra("devicesList");
        if(devicesList == null || devicesList.length() == 0){
            setDevices("");
        }
        while(devicesList.length() >0){
            String mContent = "";
            if(devicesList.contains(",")) {
                mContent = devicesList.substring(0, devicesList.indexOf(","));
                devicesList = devicesList.substring(devicesList.indexOf(",") + 1);
            }else {
                mContent = devicesList;
                devicesList = "";
            }
            addData(mContent);
            LogUtil.println("IreneBond mContent: " + mContent);
            LogUtil.println("IreneBond deviceList：" + devicesList);
        }
        updateDevices();
    }

    private void addData(String mContent){
        GPRSDevicesListBean mGPRSDevicesListBean = new GPRSDevicesListBean();
        String mDeviceNo = mContent.substring(0, mContent.indexOf("-"));
        mContent = mContent.substring(mContent.indexOf("-") + 1);
        String mDeviceType = mContent.substring(0, mContent.indexOf("-"));
        if(mDeviceType.equals("000001") || mDeviceType.equals("000002")){//只处理000001和000002
            mContent = mContent.substring(mContent.indexOf("-") + 1);
            String deviceStatus = mContent;
            LogUtil.println("IreneBond deviceType: " + mDeviceNo + " deviceNo: " + mDeviceType + " deviceStatus: " + deviceStatus);
            mGPRSDevicesListBean.setDeviceNo(mDeviceNo);
            mGPRSDevicesListBean.setDeviceType(mDeviceType);
            mGPRSDevicesListBean.setDeviceStatus(deviceStatus);
            mAllDevices.add(mGPRSDevicesListBean);
        }
        setDeviceList();
    }

    private void setDeviceList(){

        String content = JSON.toJSONString(mAllDevices, SerializerFeature.WriteMapNullValue);
        setDevices(content);
    }

    private void setDevices(String value) {

        SharedPreferences.Editor editor = GateApplication.getInstance().getSpecialSharedPreferences(false).edit();
        editor.putString(MenuActivity.DEVICES_LIST, value);
        editor.commit();
    }

    private String getData(){
        return GateApplication.getInstance().getSpecialSharedPreferences(false).getString(MenuActivity.DEVICES_LIST, "");
    }

    @Override
    public void notifyTcpPacketArrived(int mCommand, String mPacket) {
        super.notifyTcpPacketArrived(mCommand, mPacket);
        switch (mCommand){
            case Config.COMMAND_GET_DEVICE_NAME:
//                GetDeviceNameInPacket mGetDeviceNameInPacket = getPacket(mPacket,GetDeviceNameInPacket.class);
//                LogUtil.println("IreneBond 数据： " + mGetDeviceNameInPacket.getInfo());
//                setDeviceName(mGetDeviceNameInPacket);
                break;
        }
    }

    public void setDeviceName(String mPacket){
        GetDeviceNameInPacket mGetDeviceNameInPacket = getPacket(mPacket,GetDeviceNameInPacket.class);
        LogUtil.println("IreneBond 数据： " + mGetDeviceNameInPacket.getInfo());
        setDeviceName(mGetDeviceNameInPacket);
    }

    private void setDeviceName(GetDeviceNameInPacket mGetDeviceNameInPacket){
        for(int index = 0;index < mAllDevices.size();index++){
            GPRSDevicesListBean mGPRSDevicesListBean = mAllDevices.get(index);
            if(mGPRSDevicesListBean.getDeviceNo().equals(mGetDeviceNameInPacket.getDeviceId())){
                mGPRSDevicesListBean.setDeviceName(mGetDeviceNameInPacket.getInfo());
                break;
            }
        }
        setDeviceList();
        updateDevices();
    }

    private void updateDevices(){
        if (isAll) {
            onAllDevices();
            mTitleBar.setRightContent("All");
        }
        else {
            setOnLineDevices();
            mTitleBar.setRightContent("OnLine");
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ll_register_to_device){
            Intent mIntent = new Intent(mActivity,GprsSettingActivity.class);
            mIntent.putExtra("fromSMS",true);
            startActivity(mIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDevices();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            exitProgram();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitProgram() {
        // TODO Auto-generated method stub
        Builder builder = new Builder(this);
        builder.setMessage(R.string.is_exit_the_program);
        builder.setTitle(R.string.exit_the_program);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SocketThreadManager.getInstance().releaseInstance();
                System.exit(0);
            }
        });
        builder.show();
    }

    private void setOnLineDevices(){

        mOnLineDevices.clear();
        for(int index = 0;index < mAllDevices.size();index++){
            GPRSDevicesListBean mGPRSDevicesListBean = mAllDevices.get(index);
            if(mGPRSDevicesListBean.getDeviceStatus().equals("ON")){
                mOnLineDevices.add(mGPRSDevicesListBean);
            }
        }
        mGPRSDevicesListAdapter.setData(mOnLineDevices);
    }

    private void onAllDevices(){
        mGPRSDevicesListAdapter.setData(mAllDevices);
    }
}
