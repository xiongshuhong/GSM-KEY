

package com.irenebond.gsmmkey;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
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
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.irenebond.gsmmkey.adapter.APNAdapter;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.haroldreyes.androidnetworking.WebServiceRequest;
import com.haroldreyes.androidnetworking.WebServiceRequest.HttpGET;
import com.irenebond.gsmmkey.bean.APNInfoBean;
import com.irenebond.gsmmkey.bean.APNInfoListBean;
import com.irenebond.gsmmkey.bean.ReadAPNInfoBean;

import irene.com.framework.util.LogUtil;


public class GprsSettingActivity extends NetBaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView=null;
    private TextView tipView=null;
    private SimpleAdapter simpleAdapter;
    private  final String mColumns[]={"txtName","txtInfo"};
    private  final int mViewIds[]={
            R.id.txtView_name,
            R.id.txtView_info,
    };

    private final int SMS_MODE[]={
            R.string.setup_apn,
            R.string.register_to_server
    };

    private final int SMS_MODE_HINT[]={
            R.string.setup_apn_hint,
            R.string.register_to_server_hint
    };
    private ArrayList<HashMap<String,Object>> listItem;
    private static final int SETUP_APN = 0;
    private static final int REGISTER_TO_SERVER = 1;

    private SMSReceiver mSMSReceiver=null;
    private boolean fromSMS = false;

    private HttpGET httpGet;
    @Override
    public void installViews() {
        super.installViews();
        setContentView(R.layout.activity_system_setting);
        fromSMS = getIntent().getBooleanExtra("fromSMS",false);
        mTitleBar.setLeftBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        mTitleBar.setTitle(R.string.gprs_setting);
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

        tipView=(TextView)findViewById(R.id.tv_last_content);
        if(fromSMS || MenuActivity.getSMSContent(GateApplication.getInstance().getSpecialSharedPreferences()).length()==0)
            tipView.setVisibility(View.INVISIBLE);
        else{
            tipView.setVisibility(View.VISIBLE);
            tipView.setText(MenuActivity.getSMSContent(GateApplication.getInstance().getSpecialSharedPreferences()));
        }

        mSMSReceiver=new SMSReceiver();
        IntentFilter intentFilter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        this.registerReceiver(mSMSReceiver, intentFilter);
        getVPN();
    }

    private void getVPN(){
        System.out.println("IreneBond 网络请求");
        String url1 = "http://www.kiosk-china.com/kiosk-ad/apn.txt";
        httpGet = new HttpGET(); //Creating a new instance of the request.
        httpGet.setUrl(url1); //Setting the URL
        httpGet.setCallback(callBack); //Setting the Callback
        httpGet.execute(); //Executing the request
    }

    private ReadAPNInfoBean mReadAPNInfoBean;

    private WebServiceRequest.Callback callBack = new WebServiceRequest.Callback() {
        @Override
        public void onResult(int responseCode, String responseMessage, Exception exception) {
            if(responseCode == 200 && exception == null){
                responseMessage = responseMessage.replace(" ","").replace("\n","").trim();
                mReadAPNInfoBean = getPacket(responseMessage, ReadAPNInfoBean.class);
                System.out.println("IreneBond responseMessage: " + responseMessage);
            }
        }
    };

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
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            case SETUP_APN:
                onSetupAPNClick(null,null);
                break;
            case REGISTER_TO_SERVER:
                onRegisterToServerClick();
                break;
            default:
                break;
        }
    }

    boolean isReadNetwork = false;
    private void onSetupAPNClick(String hint,String vpn){
        getVPN();
        LayoutInflater inflater=this.getLayoutInflater();
        final View layout=inflater.inflate(R.layout.dlg_setup_apn, (ViewGroup) findViewById(R.id.ll_main));
        TextView tv_read_default = (TextView) layout.findViewById(R.id.tv_read_default);
        final EditText et_apn =(EditText)layout.findViewById(R.id.et_apn);
        final EditText et_user_name = (EditText) layout.findViewById(R.id.et_user_name);
        final EditText et_password = (EditText) layout.findViewById(R.id.et_password);
        final EditText et_device_number = (EditText) layout.findViewById(R.id.et_device_number);
        final EditText et_read_default = (EditText) layout.findViewById(R.id.et_read_default);
        if(hint != null)
            et_read_default.setText(hint);
        if(vpn != null) {
//            web.vodafone.nl#vodafone#vodafone
            String apn = vpn.substring(0,vpn.indexOf("#"));
            et_apn.setText(apn);
            vpn = vpn.substring(vpn.indexOf("#") + 1);
            String useName = vpn.substring(0,vpn.indexOf("#"));
            et_user_name.setText(useName);
            vpn = vpn.substring(vpn.indexOf("#") + 1);
            String passwd = vpn;
            et_password.setText(passwd);
            isReadNetwork = true;
        }
        final AlertDialog mAlertDialog = new Builder(mActivity).setTitle(R.string.setup_apn)
                .setView(layout).setPositiveButton(R.string.send,new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String apn = et_apn.getText().toString().trim();
                        String userName = et_user_name.getText().toString().trim();
                        String password = et_password.getText().toString().trim();
                        String deviceNumber = et_device_number.getText().toString().trim();
                        if(deviceNumber.length() == 0){
                            keepTheDialog(dialog, "Input Device Number");
                            return;
                        }
                        if(!isReadNetwork) {
                            if (apn.length() == 0) {
                                keepTheDialog(dialog, "Input APN");
                                return;
                            } else if (userName.length() == 0) {
                                keepTheDialog(dialog, "Input User Name");
                                return;
                            } else if (password.length() == 0) {
                                keepTheDialog(dialog, "Input Password");
                                return;
                            }
                        }
                        String sms ="#PWD" + "123456";
                        sms = sms + "#APN:" + apn + "#" + userName + "#" + password;
                        LogUtil.println("sms: " + sms);
                        sendSms(sms,deviceNumber);
                        closeTheDialog(dialog);
                    }
                }).setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        closeTheDialog(dialog);
                    }
                }).show();

        tv_read_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVPN();
                if(mReadAPNInfoBean == null){
                    Toast.makeText(mActivity,"NOT Available",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_read_default.getText().toString().length() > 0) {
                    if(getInfo(et_read_default.getText().toString()).size() > 0) {
                        onSelectAPNClick(et_read_default.getText().toString());
                        mAlertDialog.dismiss();
                    }else{
                        Toast.makeText(mActivity,"Not Exist",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void onSelectAPNClick(final String hint){

        mAPNAdapter = new APNAdapter(mActivity);
        LayoutInflater inflater=this.getLayoutInflater();
        final View layout=inflater.inflate(R.layout.dlg_select_apn, (ViewGroup) findViewById(R.id.ll_main));
        ListView lv_vpn = (ListView) layout.findViewById(R.id.lv_vpn);
        lv_vpn.setAdapter(mAPNAdapter);
        mAPNAdapter.setData(getInfo(hint));
        final AlertDialog mAlertDialog = new Builder(mActivity).setTitle(R.string.select_apn)
                .setView(layout).show();
        lv_vpn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAlertDialog.dismiss();
                onSetupAPNClick(hint,mAPNAdapter.getData().get(position).getApn());
            }
        });
    }
    private APNAdapter mAPNAdapter;

    private ArrayList<APNInfoBean> getInfo(String mHint){
        if(mReadAPNInfoBean == null)
            return new ArrayList<APNInfoBean>();
        for(int index = 0;index < mReadAPNInfoBean.getData().size();index++){
            APNInfoListBean mAPNInfoListBean = mReadAPNInfoBean.getData().get(index);
            if(mAPNInfoListBean.getName().contains(mHint))
                return mAPNInfoListBean.getList();
        }
        return new ArrayList<APNInfoBean>();
    }


    private void onRegisterToServerClick(){
        LayoutInflater inflater=this.getLayoutInflater();
        final View layout=inflater.inflate(R.layout.dlg_register_to_server, (ViewGroup) findViewById(R.id.ll_main));
        final RadioGroup rg_main = (RadioGroup) layout.findViewById(R.id.rg_main);
        final EditText et_user_name = (EditText) layout.findViewById(R.id.et_user_name);
        final EditText et_password = (EditText) layout.findViewById(R.id.et_password);
        final EditText et_server_ip =(EditText)layout.findViewById(R.id.et_server_ip);
        final EditText et_server_port =(EditText)layout.findViewById(R.id.et_server_port);
        final EditText et_device_number = (EditText) layout.findViewById(R.id.et_device_number);

        new Builder(mActivity).setTitle(R.string.register_to_server)
                .setView(layout).setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //#PWD123456#GPRS=ON:abcde:782734:121.43.145.86:9041
                String userName = et_user_name.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String serverIP = et_server_ip.getText().toString().trim();
                String serverPort = et_server_port.getText().toString().trim();
                String deviceNumber = et_device_number.getText().toString().trim();
                boolean isShowOther = false;
                if(deviceNumber.length() == 0){
                    keepTheDialog(dialog, "Input Device Number");
                    return;
                }else if(password.length() == 0){
                    keepTheDialog(dialog, "Input Password");
                    return;
                }
                String sms = "#PWD" + password;
                if (rg_main.getCheckedRadioButtonId() == R.id.rb_on) {
                    sms = sms + "#GPRS=ON";
                    isShowOther = true;
                } else {
                    sms = sms + "#GPRS=OFF";
                    isShowOther = false;
                }

                if (isShowOther) {
                    if(userName.length() == 0){
                        Toast.makeText(mActivity,"Input User Name",Toast.LENGTH_SHORT).show();
                        return;
                    }else if(serverIP.length() == 0){
                        Toast.makeText(mActivity,"Input Server IP",Toast.LENGTH_SHORT).show();
                        return;
                    }else if(serverPort.length() == 0){
                        Toast.makeText(mActivity,"Input Server PORT",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sms = sms + ":" + userName + ":" + password + ":" + serverIP + ":" + serverPort;
                }
                LogUtil.println("sms: "+sms);
                sendSms(sms,deviceNumber);
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


    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what){
                case 1:
                    if(fromSMS)
                        if(MenuActivity.instance != null)
                            MenuActivity.instance.updateSMSContent(msg.getData(),tipView);
                    break;
            }
            super.handleMessage(msg);
        }

    };

    public String updateSMSContent(Bundle bundle,TextView txtView){
        Object[] myOBJpdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
        StringBuilder sms=new StringBuilder();
        StringBuilder smsContent=new StringBuilder();
        for (int i = 0; i<myOBJpdus.length; i++)
            messages[i] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[i]);
        for (SmsMessage currentMessage : messages)
        {
            sms.append(getCurrentTime());
            smsContent.append(currentMessage.getDisplayMessageBody());
            sms.append(currentMessage.getDisplayMessageBody());
        }
        txtView.setVisibility(View.VISIBLE);
        txtView.setText(sms);
        setSMSContent(sms.toString());
        return sms.toString();
    }

    private static void setSMSContent(String sms){
        SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
        editor.putString(MenuActivity.AUTHORIZEDNOSYSTEM_SMS, sms);
        editor.commit();
    }

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
}
