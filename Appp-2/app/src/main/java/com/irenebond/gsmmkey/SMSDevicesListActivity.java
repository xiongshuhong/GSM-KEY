package com.irenebond.gsmmkey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.irenebond.gsmmkey.adapter.SMSDevicesListAdapter;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.data.GPRSDevicesListBean;
import com.irenebond.gsmmkey.network.SocketThreadManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import irene.com.framework.util.LogUtil;

/**
 * Created by Irene on 2016/1/26.
 */
public class SMSDevicesListActivity extends NetBaseActivity implements View.OnClickListener{

    private ListView gv_devices_list;
    private SMSDevicesListAdapter mSMSDevicesListAdapter;
    private ArrayList<GPRSDevicesListBean> mDatas = new ArrayList<GPRSDevicesListBean>();
    private LinearLayout ll_register_to_device;

    @Override
    public void installViews() {
        super.installViews();
        setContentView(R.layout.activity_sms_devices_list);
        mTitleBar.setTitleOnly("SMS Devices List");
        mTitleBar.setRightDrawable(R.drawable.ic_add);
        mTitleBar.setRightBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrEditDialogue(false,0);
            }
        });
        gv_devices_list = (ListView) findViewById(R.id.gv_devices_list);
        ll_register_to_device = (LinearLayout) findViewById(R.id.ll_register_to_device);
        ll_register_to_device.setOnClickListener(this);
        mSMSDevicesListAdapter = new SMSDevicesListAdapter(mActivity);
        gv_devices_list.setAdapter(mSMSDevicesListAdapter);
        System.out.println("IreneBondd: " + getSMSData());
        loadData(getSMSData());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void addOrEditDialogue(final boolean isEdit, final int position){

        View contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.dlg_add_devices, null);
        final Dialog mDialog = DialogManager.showCenterAlert(mActivity, contentView);
        final TextView tv_sure = (TextView) contentView.findViewById(R.id.tv_sure);
        final TextView tv_cancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        final EditText et_device_name = (EditText) contentView.findViewById(R.id.et_device_name);
        final EditText et_sim_no = (EditText) contentView.findViewById(R.id.et_sim_no);
        final EditText et_device_type = (EditText) contentView.findViewById(R.id.et_device_type);
        final EditText et_password = (EditText) contentView.findViewById(R.id.et_password);
        if(isEdit){
            GPRSDevicesListBean mGPRSDevicesListBean = mDatas.get(position);
            et_device_name.setText(mGPRSDevicesListBean.getDeviceName());
            et_sim_no.setText(mGPRSDevicesListBean.getDeviceNo());
            et_device_type.setText(mGPRSDevicesListBean.getDeviceType());
            et_password.setText(mGPRSDevicesListBean.getPassword());
        }
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = et_device_name.getText().toString();
                String password = et_password.getText().toString();
                String deviceType = et_device_type.getText().toString();
                String deviceNo = et_sim_no.getText().toString();

                if (deviceName.length() == 0) {
                    Toast.makeText(mActivity, "Input Device Name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (deviceType.length() == 0) {
                    Toast.makeText(mActivity, "Input Device Type", Toast.LENGTH_SHORT).show();
                    return;
                } else if (deviceNo.length() == 0) {
                    Toast.makeText(mActivity, "Input SIM No", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.length() == 0) {
                    Toast.makeText(mActivity, "Input Password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!(deviceType.equals("000001") || deviceType.equals("000002"))) {
                    Toast.makeText(mActivity, "Device Type Must 000001 or 000002", Toast.LENGTH_SHORT).show();
                    return;
                }else if(password.length() < 6){
                    Toast.makeText(mActivity, "Only Enter 6 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isEdit){
                    GPRSDevicesListBean mGPRSDevicesListBean = mDatas.get(position);
                    mGPRSDevicesListBean.setDeviceName(deviceName);
                    mGPRSDevicesListBean.setPassword(password);
                    mGPRSDevicesListBean.setDeviceNo(deviceNo);
                    mGPRSDevicesListBean.setDeviceType(deviceType);
                }else {
                    GPRSDevicesListBean mGPRSDevicesListBean = new GPRSDevicesListBean();
                    mGPRSDevicesListBean.setDeviceName(deviceName);
                    mGPRSDevicesListBean.setPassword(password);
                    mGPRSDevicesListBean.setDeviceNo(deviceNo);
                    mGPRSDevicesListBean.setDeviceType(deviceType);
                    mDatas.add(mGPRSDevicesListBean);
                }
                setDeviceList();
                mSMSDevicesListAdapter.setData(mDatas);
                mDialog.dismiss();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    private void setDeviceList(){

        String smsContent = JSON.toJSONString(mDatas, SerializerFeature.WriteMapNullValue);
        setDevices(smsContent);
    }

    private void setDevices(String value) {

        SharedPreferences.Editor editor = GateApplication.getInstance().getSpecialSharedPreferences(false).edit();
        editor.putString(MenuActivity.SMS_DEVICES_LIST, value);
        editor.commit();
    }

    private void loadData(String content){
        try {
            JSONArray mJSONArray = new JSONArray(content);
            LogUtil.println("IreneBond content： " + mJSONArray.length());
            for(int index = 0;index < mJSONArray.length();index++){
                LogUtil.println("IreneBond data: " + mJSONArray.get(index));
            }
            for(int index = 0;index < mJSONArray.length();index++){
                LogUtil.println("IreneBond data: " + mJSONArray.get(index));
                GPRSDevicesListBean lGPRSDevicesListBean = getPacket(mJSONArray.get(index).toString(),GPRSDevicesListBean.class);
                mDatas.add(lGPRSDevicesListBean);
            }
            mSMSDevicesListAdapter.setData(mDatas);
        }catch(JSONException e){

        }
    }

    private String getSMSData(){
        return GateApplication.getInstance().getSpecialSharedPreferences(false).getString(MenuActivity.SMS_DEVICES_LIST, "");
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ll_register_to_device){
            addOrEditDialogue(false,0);
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void deleteData(final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Data Now?");
        builder.setTitle("Delete");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatas.remove(position);
                setDeviceList();
                mSMSDevicesListAdapter.setData(mDatas);
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

    public void editData(int position){
        addOrEditDialogue(true,position);
    }
}
