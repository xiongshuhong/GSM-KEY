package com.irenebond.gsmmkey;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.irenebond.gsmmkey.base.NetBaseActivity;

public class MenuActivity extends NetBaseActivity implements OnItemClickListener{

	public final static String PREFERENCES_NAME="com.android.gsmgate.MenuActivity1";
	public final static String WORKING_MODE = "com.android.gsmgate.workingmode";
	public final static String ALARM_1_ON_OFF="com.android.gsmgate.alarm1.on.or.off";
	public final static String ALARM_1_SMS_PHONE="com.android.gsmgate.alarm1.sms.or.phone";
	public final static String ALARM_1_TIME="com.android.gsmgate.alarm1.time";
	public final static String ALARM_2_ON_OFF="com.android.gsmgate.alarm2.on.or.off";
	public final static String ALARM_2_SMS_PHONE="com.android.gsmgate.alarm2.sms.or.phone";
	public final static String ALARM_2_TIME="com.android.gsmgate.alarm2.time";
	public final static String SET_GOT_BASE_TIMER="com.android.gsmate.set.got.base.timer";
	public final static String SET_RELAY_CLOSE_TIME="com.android.gsmate.set.relay.close.time";
	public final static String SET_RELAY_INITIAL_STATUS_SETTING="com.android.gsmate.set.relay.initial.status.setting";
	public final static String ENABLE_ACM_MODE = "EnableACMMode";
	public final static String ENABLE_REPORT_MODE = "EnableReportMode";
	public final static String ENABLE_REPLY_MODE = "ENABLE_REPLY_MODE";
	public final static String SET_AUTHORIZED_NO="com.android.gsmate.set.authorized.no";
	public final static String SET_SERIAL_NO="com.android.gsmate.set.serial.no";
	public final static String SET_ALARM_SERIAL_NO="com.android.gsmate.set.alarm.serial.no";
	public final static String SYSTEMSETTINGS_SMS="com.android.gsmate.systemsettings.sms";
	public final static String AUTHORIZEDNOSYSTEM_SMS="com.android.gsmate.systemsettings.sms";
	public final static String DEVICES_LIST = "devicesList";
	public final static String SMS_DEVICES_LIST = "SMSdevicesList";
	public final static String LOGIN_IP = "longinIP";
	public final static String LOGIN_PORT = "longinPort";
	public final static String LOGIN_NAME = "longinName";
	public final static String LOGIN_PASSWD = "longinPasswd";

	private ListView listView=null;
	private SimpleAdapter simpleAdapter;
	private final String mColumns[]={"imgView","textView"};
	private final int mDrawable[]={R.drawable.icon2,R.drawable.icon2};
	private final int mStringIds[]={
			R.string.authorized_settings,
			R.string.limit_access_no_settings,
			R.string.alarm_setting_title,
			R.string.system_settings,
			R.string.set_new_password,
			R.string.reset_password_2
	};
	private final int mWifiStringIds[]={
			R.string.authorized_settings,
			R.string.limit_access_no_settings,
			R.string.alarm_setting_title,
			R.string.system_settings,
			R.string.reset_password
	};
	private final int mViewIds[]={
			R.id.menu_img,
			R.id.menu_name
	};
	private TextView tipView=null;
	private ArrayList<HashMap<String,Object>> listItem;
	public static MenuActivity instance;
	@Override
	public void installViews() {
		super.installViews();
		setContentView(R.layout.activity_menu);
		instance = this;
		mTitleBar.setLeftBtnListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});
		mTitleBar.setTitle(R.string.menu_settings);
		listView=(ListView)findViewById(R.id.list);
		listItem=new ArrayList<HashMap<String,Object>>();
		simpleAdapter=new SimpleAdapter(this,listItem,
				R.layout.item_menu_list,
				mColumns,
				mViewIds
		);
		listView.setOnItemClickListener(this);
		listView.setAdapter(simpleAdapter);
		addListItem();
		tipView=(TextView)findViewById(R.id.tv_last_content);
		try{
			if(getSMSContent(GateApplication.getInstance().getSpecialSharedPreferences()).length()==0)
				tipView.setVisibility(View.INVISIBLE);
			else{
				tipView.setVisibility(View.VISIBLE);
				tipView.setText(getSMSContent(GateApplication.getInstance().getSpecialSharedPreferences()));
			}
		}
		catch(Exception e){

		}
	}

	public static String getSMSContent(SharedPreferences preferences){

		String sms = preferences.getString(MenuActivity.SYSTEMSETTINGS_SMS,"");
		// LogUtil.println("getSMSContent(): "+sms);
		return sms;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private class SMSReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getExtras()!=null)
				showSMSContent(intent.getExtras());
		}

	}
	private void showSMSContent(Bundle bundle){
		Message msg=handler.obtainMessage();
		msg.what=1;
		msg.setData(bundle);
		msg.sendToTarget();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
				case 1:
					updateSMSContent(msg.getData(),tipView);
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
			String phone=currentMessage.getDisplayOriginatingAddress();
			if(!phone.contains(getPhoneNo()))
				return "";
			sms.append(getCurrentTime());
			smsContent.append(currentMessage.getDisplayMessageBody());
			sms.append(currentMessage.getDisplayMessageBody());
		}

		getSystemSettings(smsContent.toString());
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
	public static void setAlarmSetting(String key,int radioButton_id) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor = GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(key, radioButton_id);
		editor.commit();

	}
	public static void setAlarmTime(String key,String time) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putString(key, time);
		editor.commit();

	}
	public static void setRelayCloseTime(String time){
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putString(MenuActivity.SET_RELAY_CLOSE_TIME, time);
		editor.commit();
	}
	public static void setSerialNo(String serial){
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		int serialNo=Integer.parseInt(serial);
		// LogUtil.println("serial: "+serial);
		editor.putInt(MenuActivity.SET_SERIAL_NO, serialNo);
		editor.commit();
	}
	public static void setRelayInitialStatus(int radioId) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(MenuActivity.SET_RELAY_INITIAL_STATUS_SETTING, radioId);
		editor.commit();

	}
	public static void getSystemSettings(String sms) {
		// TODO Auto-generated method stub
		//	String sms2="ALARM1=ON:PHONE:3223,ALARM2=ON:PHONE:5632,GOT=3463,WHL=02,ALARM1=H,ALARM2=H,BCPW=34,CSQ=20,RELAY=ON";
		//	String sms3="ALARM1=ON:SMS:0700,ALARM2=ON:PHONE:1245 OKAY";
		//	String sms="Error Old Password";
		boolean is=false;
		if(sms.substring("ALARM1=".length()).startsWith("ON")){
			// LogUtil.println("ON");
			sms=sms.substring("ALARM1=ON:".length());
			// LogUtil.println("sms: "+sms);
			setAlarmSetting(MenuActivity.ALARM_1_ON_OFF,R.id.radionBtn_on);
			is=true;
		}else if(sms.substring("ALARM1=".length()).startsWith("OFF")){
			// LogUtil.println("OFF");
			sms=sms.substring("ALARM1=OFF:".length());
			// LogUtil.println("sms: "+sms);
			setAlarmSetting(MenuActivity.ALARM_1_ON_OFF,R.id.radionBtn_off);
			is=true;
		}
		// LogUtil.println("getSystemSettings: ");
		if(!is)
			return;
		if(sms.startsWith("SMS:")){
			String time=sms.substring("SMS:".length(), sms.indexOf(','));
			// LogUtil.println("time: "+time);
			setAlarmTime(MenuActivity.ALARM_1_TIME,time);
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
			setAlarmSetting(MenuActivity.ALARM_1_SMS_PHONE,R.id.radionBtn_sms);

		}else if(sms.startsWith("PHONE:")){
			String time=sms.substring("PHONE:".length(), sms.indexOf(','));
			// LogUtil.println("time: "+time);
			setAlarmTime(MenuActivity.ALARM_1_TIME,time);
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
			setAlarmSetting(MenuActivity.ALARM_1_SMS_PHONE,R.id.radionBtn_phone);
		}

		if(sms.substring("ALARM2=".length()).startsWith("ON")){
			// LogUtil.println("ON");
			sms=sms.substring("ALARM2=ON:".length());
			// LogUtil.println("sms: "+sms);
			setAlarmSetting(MenuActivity.ALARM_2_ON_OFF,R.id.radionBtn_on2);
		}else if(sms.substring("ALARM2=".length()).startsWith("OFF")){
			// LogUtil.println("OFF");
			sms=sms.substring("ALARM2=OFF:".length());
			// LogUtil.println("sms: "+sms);
			setAlarmSetting(MenuActivity.ALARM_2_ON_OFF,R.id.radionBtn_off2);
		}
		if(sms.startsWith("SMS:")){
			String time;
			if(sms.endsWith("ON")||sms.endsWith("OFF"))
				time=sms.substring("SMS:".length(), sms.indexOf(','));
			else
				time=sms.substring("SMS:".length(), sms.indexOf(' '));
			// LogUtil.println("time: "+time);
			setAlarmTime(MenuActivity.ALARM_2_TIME,time);
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
			setAlarmSetting(MenuActivity.ALARM_2_SMS_PHONE,R.id.radionBtn_sms2);

		}else if(sms.startsWith("PHONE:")){
			String time;
			if(sms.endsWith("ON")||sms.endsWith("OFF"))
				time=sms.substring("PHONE:".length(), sms.indexOf(','));
			else
				time=sms.substring("PHONE:".length(), sms.indexOf(' '));

			// LogUtil.println("time: "+time);
			setAlarmTime(MenuActivity.ALARM_2_TIME,time);
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
			setAlarmSetting(MenuActivity.ALARM_2_SMS_PHONE,R.id.radionBtn_phone2);
		}
		if(sms.startsWith("GOT=")){
			String time=sms.substring("GOT=".length(), sms.indexOf(','));
			// LogUtil.println("time: "+time);
			setRelayCloseTime(time);
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
		}

		if(sms.startsWith("WHL=")){
			String time=sms.substring("WHL=".length(), sms.indexOf(','));
			// LogUtil.println("WHL: "+time);
			setSerialNo(time);
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
		}

		if(sms.startsWith("ALARM1=")){
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
		}


		if(sms.startsWith("ALARM2=")){
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
		}

		if(sms.startsWith("BCPW=")){
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
		}


		if(sms.startsWith("CSQ=")){
			sms=sms.substring(sms.indexOf(',')+1);
			// LogUtil.println("sms: "+sms);
		}

		if(sms.startsWith("RELAY=")){
			String time=sms.substring("RELAY=".length());
			// LogUtil.println("time: "+time);
			if(time.contains("ON")){
				setRelayInitialStatus(R.id.radioBtnRelayInitialStatus_ON);
			}else if(time.contains("OFF")){
				setRelayInitialStatus(R.id.radioBtnRelayInitialStatus_OFF);
			}

		}

	}

	private void addListItem() {
		// TODO Auto-generated method stub
		HashMap<String,Object> map;
		Resources res=this.getResources();
		int[] mResourceID;
		if(GateApplication.getInstance().isSMS)
			mResourceID = mStringIds;
		else
			mResourceID = mWifiStringIds;
		for(int i=0;i<mResourceID.length;i++){
			map=new HashMap<String,Object>();
			map.put(mColumns[0],mDrawable[(i+1)%2]);
			map.put(mColumns[1],res.getString(mResourceID[i]));
			listItem.add(map);
		}
		simpleAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(GateApplication.getInstance().isSMS){
			onSMSMode(position);
		}else{
			onWIFIMODE(position);
		}

	}

	private void onSMSMode(int position){
		switch(position){
			case 0:
				authorizedNoSettings();
				break;
			case 1:
				onLimitAccessNoSettings();
				break;
			case 2:
				onAlarmSettingClick();
				break;
			case 3:
				systemSettings();
				break;
			case 4:
				setNewPassword();
				break;
			case 5:
				resetPassword();
			default:
				break;
		}
	}

	private void onWIFIMODE(int postion){
		switch(postion){
			case 0:
				authorizedNoSettings();
				break;
			case 1:
				onLimitAccessNoSettings();
				break;
			case 2:
				onAlarmSettingClick();
				break;
			case 3:
				systemSettings();
				break;
			case 4:
				resetPassword();
				break;
			default:
				break;
		}
	}
	private void onLimitAccessNoSettings(){
		if(!isSetAccessControlNo()){
			Toast.makeText(this, R.string.not_set_access_control_no, Toast.LENGTH_SHORT).show();
		}else {
			Intent mIntent = new Intent(mActivity, LimitAccessNoSettingActivity.class);
			startActivity(mIntent);
		}
	}
	private void onAlarmSettingClick(){
		Intent mIntent = new Intent(mActivity,AlarmSettingActivity.class);
		startActivity(mIntent);
	}
	private void onGprsSettingClick(){
		Intent mIntent = new Intent(mActivity,GprsSettingActivity.class);
		startActivity(mIntent);
	}

	private void resetPassword() {
		// TODO Auto-generated method stub
		if(!isSetAccessControlNo()){
			Toast.makeText(this, R.string.not_set_access_control_no, Toast.LENGTH_SHORT).show();
		}else{
			LayoutInflater inflater=this.getLayoutInflater();
			final View layout=inflater.inflate(R.layout.dlg_set_access_control, (ViewGroup)findViewById(R.id.set_access_control));
			final EditText edtText=(EditText)layout.findViewById(R.id.edt_access_control);
			edtText.setHint("Enter factory default password");
			new AlertDialog.Builder(MenuActivity.this).setTitle("Reset password to factory setting")
					.setView(layout).setCancelable(false).setPositiveButton(R.string.save, new DialogInterface
					.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(edtText.getText().length()==0){
						keepTheDialog(dialog,"Enter factory default password");
					}else if(edtText.getText().length() != 6){
						keepTheDialog(dialog,"Only Enter 6 digits");
					}else{
						String hint=getStringFromId(R.string.reset_password_successful);
						Toast.makeText(MenuActivity.this,hint, Toast.LENGTH_LONG).show();
						GateApplication.getInstance().mDatas.get(GateApplication.getInstance().position).setPassword(edtText.getText().toString().trim());
						setDeviceList();
						//		clearAllData();
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
	}
	private void systemSettings() {
		// TODO Auto-generated method stub
		if(!isSetAccessControlNo()){
			Toast.makeText(this, R.string.not_set_access_control_no, Toast.LENGTH_SHORT).show();
		}else{
			Intent intent=new Intent(this,SystemSettingsActivity.class);
			this.startActivity(intent);
		}
	}

	private void authorizedNoSettings() {
		// TODO Auto-generated method stub
		if(!isSetAccessControlNo()){
			Toast.makeText(this, R.string.not_set_access_control_no, Toast.LENGTH_SHORT).show();
		}else{
			Intent intent=new Intent(this,AuthorizedNoSettingActivity.class);
			this.startActivity(intent);
		}
	}

	private void setNewPassword() {
		// TODO Auto-generated method stub
		if(!isSetAccessControlNo()){
			Toast.makeText(this, R.string.not_set_access_control_no, Toast.LENGTH_SHORT).show();
		}else{
			LayoutInflater inflater=this.getLayoutInflater();
			final View layout=inflater.inflate(R.layout.dlg_edit_pwd, (ViewGroup)findViewById(R.id.edit_pwd));
			final EditText oldPassword=(EditText)layout.findViewById(R.id.old_pwd_edit);
			final EditText newPassword=(EditText)layout.findViewById(R.id.new_pwd_edit);
			final EditText renewPassword=(EditText)layout.findViewById(R.id.re_pwd_edit);
			new AlertDialog.Builder(MenuActivity.this).setTitle(R.string.set_new_password)
					.setView(layout).setPositiveButton(R.string.save, new DialogInterface
					.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(oldPassword.getText().length()==0){
						keepTheDialog(dialog,R.string.please_enter_password);
					}else if(newPassword.getText().length()==0){
						keepTheDialog(dialog,R.string.please_enter_new_password);
					}else if(renewPassword.getText().length()==0){
						keepTheDialog(dialog,R.string.please_confirm_new_password);
					}else if(oldPassword.getText().length() < 6 || newPassword.getText().length() < 6 || renewPassword.getText().length() < 6){
						keepTheDialog(dialog,"Only Enter 6 digits");
					}else{
						if(!oldPassword.getText().toString().trim().equals(getPassword())){
							keepTheDialog(dialog,R.string.please_enter_correct_password);
						}else if(!newPassword.getText().toString().trim().equals(renewPassword.getText().toString().trim())){
							keepTheDialog(dialog,R.string.new_passwords_not_match);
						}else if(oldPassword.getText().toString().trim().equals(newPassword.getText().toString().trim())){
							keepTheDialog(dialog,R.string.new_passwords_match);
						}
						else{
							GateApplication.getInstance().mDatas.get(GateApplication.getInstance().position).setPassword(newPassword.getText().toString().trim());
							setDeviceList();
							String sms="#PWD"+oldPassword.getText().toString().trim()+
									"#CAP"+newPassword.getText().toString().trim()+"#CAP"+newPassword.getText().toString().trim();
							// LogUtil.println("sms: "+sms);
							sendSms(sms);
							closeTheDialog(dialog);
						}
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
	}
	private void setDeviceList(){

		String smsContent = JSON.toJSONString(GateApplication.getInstance().mDatas, SerializerFeature.WriteMapNullValue);
		setDevices(smsContent);
	}

	private void setDevices(String value) {

		SharedPreferences.Editor editor = GateApplication.getInstance().getSpecialSharedPreferences(false).edit();
		editor.putString(MenuActivity.SMS_DEVICES_LIST, value);
		editor.commit();
	}

	private String getStringFromId(int text){
		Resources res=this.getResources();
		return res.getString(text);
	}
	private boolean isSetAccessControlNo() {

		String text = GateApplication.getInstance().mGPRSDevicesListBean.getDeviceNo();
		// LogUtil.println("isSetAccessControlNo: "+text);
		if(text.length()==0)
			return false;
		else
			return true;
	}

}