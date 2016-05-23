package com.irenebond.gsmmkey;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.irenebond.gsmmkey.adapter.MainAdapter;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.OutPackUtil;
import com.irenebond.gsmmkey.network.SocketThreadManager;
import com.irenebond.gsmmkey.network.packet.CommandInPacket;
import com.irenebond.gsmmkey.network.packet.CommandOutPacket;
import com.irenebond.gsmmkey.network.packet.base.InPacket;

import irene.com.framework.util.LogUtil;


public class MainActivity extends NetBaseActivity implements OnClickListener{

	private static final String CALL_ACTION="android.intent.action.CALL";
	private MainAdapter mAdapter=null;
	private Button btn_setting=null;
	private Button btn_exit=null;
	private TextView txtView=null;
	private SMSReceiver mSMSReceiver;

	@Override
	public void installViews() {
		super.installViews();
		setContentView(R.layout.activity_main);
		mAdapter=new MainAdapter(this);
		GridView gridView=(GridView)findViewById(R.id.my_grid);
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				switch(arg2){
					case 0:
						onCommonSend("RLY-TEXT1", Config.COMMAND_MAIN_1,"Enter RLY-TEXT1 Content or Check OUT1 Customized Command Text");
						break;
					case 1:
						onCommonSend("RLY-TEXT2", Config.COMMAND_MAIN_2,"Enter RLY-TEXT2 Content or Check OUT2 Customized Command Text");
						break;
					case 2:
						setRelayInitialStatusSetting(true, R.string.relay_inital_set_on, "OUT1",R.string.relay_initial_status, Config.COMMAND_MAIN_3);
						break;
					case 3:
						setRelayInitialStatusSetting(false, R.string.relay_inital_set_off, "OUT1",R.string.relay_initial_status, Config.COMMAND_MAIN_4);
						break;
					case 4:
						setRelayInitialStatusSetting(true,R.string.relay_inital_set_on,"OUT2",R.string.relay2_initial_status, Config.COMMAND_MAIN_5);
						break;
					case 5:
						setRelayInitialStatusSetting(false,R.string.relay_inital_set_off,"OUT2",R.string.relay2_initial_status, Config.COMMAND_MAIN_6);
						break;
					case 6:
						callAction();
						break;
					case 7:
						showHelp();
						break;
					default:
						break;
				}
			}

		});
		btn_setting=(Button)findViewById(R.id.home_setting);
		btn_setting.setOnClickListener(this);
		btn_exit=(Button)findViewById(R.id.home_exit);
		btn_exit.setOnClickListener(this);
		txtView=(TextView)findViewById(R.id.bind_num);
		mSMSReceiver=new SMSReceiver();
		IntentFilter intentFilter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(Integer.MAX_VALUE);
		this.registerReceiver(mSMSReceiver, intentFilter);
	}

	private void setRelayInitialStatusSetting(final boolean isStart,int message,final String sendContent,int title,final int cmd){
		Builder builder = new Builder(this);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				setRelayInitialStatusSetting(isStart);
				// LogUtil.println("isStart: "+isStart);
				String sms=new String();
				if(getRelayInitialStatus()==R.id.radioBtnRelayInitialStatus_ON){
//					sms="#PWD"+getPassword()+"#OUT1=ON";
					sms="#PWD"+getPassword()+"#" + sendContent + "=ON";
				}else{
					sms="#PWD"+getPassword()+"#" + sendContent + "=OFF";
				}
				LogUtil.println("sms: " + sms);
				if(!GateApplication.getInstance().isSMS){
					CommandOutPacket mCommandOutPacket = getCommandOutPacket(cmd, sms);
					OutPackUtil.sendMessage(mCommandOutPacket);
					return;
				}else
					sendSms(sms);
			}

		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}

		});
		builder.show();
	}
	private int getRelayInitialStatus(){
		
		int mode=GateApplication.getInstance().getSpecialSharedPreferences().getInt(MenuActivity.SET_RELAY_INITIAL_STATUS_SETTING, R.id.radioBtnRelayInitialStatus_OFF);
		if((mode==R.id.radioBtnRelayInitialStatus_OFF))
			return mode;
		else
			return R.id.radioBtnRelayInitialStatus_ON;
	}
	private void setRelayInitialStatusSetting(final boolean isStart){
		int radioId;
		if(isStart)
			radioId=R.id.radioBtnRelayInitialStatus_ON;
		else
			radioId=R.id.radioBtnRelayInitialStatus_OFF;
		setRelayInitialStatus(radioId);
	}
	private void setRelayInitialStatus(int radioId) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(MenuActivity.SET_RELAY_INITIAL_STATUS_SETTING, radioId);
		editor.commit();

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getBindNumber();
	}
	private void getBindNumber() {
		// TODO Auto-generated method stub
		Resources res=this.getResources();
		String text = getPhoneNo();
		// LogUtil.println("getBindNumber: "+text);
		if(text.length()==0)
			txtView.setText(res.getString(R.string.not_set_access_control_no));
		else
			txtView.setText(res.getString(R.string.access_control_no)+text);
		//txtView.append(GateApplication.getInstance().getSpecialSharedPreferences().getString(MenuActivity.ACCESSCONTROLNO,""));
	}
	private String getStringFromId(int text){
		Resources res=this.getResources();
		return res.getString(text);
	}
	private void callAction() {
		// TODO Auto-generated method stub
		Builder builder = new Builder(this);
		builder.setMessage(R.string.call_key);
		builder.setTitle(R.string.call_key_title);
		builder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(getPhoneNo().length()!=0){
					Intent intent=new Intent(CALL_ACTION,Uri.parse("tel:"+getPhoneNo()));
					MainActivity.this.startActivity(intent);
				}else{
					Toast.makeText(MainActivity.this, R.string.not_set_access_control_no, Toast.LENGTH_SHORT).show();
				}
			}

		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}

		});
		builder.show();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.home_setting:
				Intent intent=new Intent(this,MenuActivity.class);
				this.startActivity(intent);
				break;
			case R.id.home_exit:
				exitProgram();
				break;
			default:
				break;
		}
	}
	private void exitProgram() {
		// TODO Auto-generated method stub
//		Builder builder = new Builder(this);
//		builder.setMessage(R.string.is_exit_the_program);
//		builder.setTitle(R.string.exit_the_program);
//		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//			}
//		});
//		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				System.exit(0);
//			}
//		});
//		builder.show();
		mActivity.finish();
	}
	private void showHelp() {
		Intent mIntent = new Intent(mActivity,ReviewUrlActivity.class);
		startActivity(mIntent);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			exitProgram();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// LogUtil.println("onDestroy");
		if(mSMSReceiver!=null)
			this.unregisterReceiver(mSMSReceiver);
		super.onDestroy();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
				case 1:
					updateSMSContent(msg.getData());
					break;
			}
			super.handleMessage(msg);
		}

	};
	private void updateSMSContent(Bundle bundle){
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
				return;
			sms.append(getCurrentTime());
			smsContent.append(currentMessage.getDisplayMessageBody());
			sms.append(currentMessage.getDisplayMessageBody());
		}
		getSystemSettings(smsContent.toString());
		setSMSContent(sms.toString());
	}
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
	private void setSMSContent(String sms){
		SharedPreferences.Editor editor = GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putString(MenuActivity.AUTHORIZEDNOSYSTEM_SMS, sms);
		editor.commit();
	}
	private void setAlarmSetting(String key,int radioButton_id) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(key, radioButton_id);
		editor.commit();

	}
	private void setAlarmTime(String key,String time) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putString(key, time);
		editor.commit();

	}
	private void getSystemSettings(String sms) {
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
	private void setRelayCloseTime(String time){
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putString(MenuActivity.SET_RELAY_CLOSE_TIME, time);
		editor.commit();
	}
	private void setSerialNo(String serial){
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		int serialNo=Integer.parseInt(serial);
		// LogUtil.println("serial: "+serial);
		editor.putInt(MenuActivity.SET_SERIAL_NO, serialNo);
		editor.commit();
	}

	private void onCommonSend(String key,int cmd,String hint){
		String content = getTextContent(key);
		if(content.length() == 0){
			Toast.makeText(mActivity,hint,Toast.LENGTH_SHORT).show();
			return;
		}
//		#PWD123456#RLY-TEXT1#XXXX
		final String sms = content;
		if(!GateApplication.getInstance().isSMS){
			CommandOutPacket mCommandOutPacket = getCommandOutPacket(cmd, sms);
			OutPackUtil.sendMessage(mCommandOutPacket);
			return;
		}

		Builder builder = new Builder(this);
		builder.setMessage("Send the SMS :" + sms);
		builder.setTitle("SMS SEND CONFIRMATION");
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendSms(sms);
				closeTheDialog(dialog);
			}

		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				closeTheDialog(dialog);
			}

		});
		builder.show();
	}


	private String getTextContent(String key){
		return GateApplication.getInstance().getSpecialSharedPreferences().getString(key, "");
	}

	@Override
	protected void onStop() {
		super.onStop();
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
			case Config.COMMAND_MAIN_1:
			case Config.COMMAND_MAIN_2:
			case Config.COMMAND_MAIN_3:
			case Config.COMMAND_MAIN_4:
			case Config.COMMAND_MAIN_5:
			case Config.COMMAND_MAIN_6:
				showCustomNormalView();
				CommandInPacket mCommandInPacket = JSON.parseObject(mPacket, CommandInPacket.class);
				String info = mCommandInPacket.getInfo();
				if(info.equals("send success"))
					return;
				Toast.makeText(mActivity, mCommandInPacket.getInfo(), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
		}
	}

}
