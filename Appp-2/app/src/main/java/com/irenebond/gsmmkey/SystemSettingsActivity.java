package com.irenebond.gsmmkey;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.irenebond.gsmmkey.network.SocketThreadManager;
import com.irenebond.gsmmkey.network.packet.CommandInPacket;
import com.irenebond.gsmmkey.network.packet.CommandOutPacket;

import irene.com.framework.util.LogUtil;

public class SystemSettingsActivity extends NetBaseActivity implements OnItemClickListener{

	private ListView listView=null;
	private TextView tv_last_content=null;
	private SimpleAdapter simpleAdapter;
	private  final String mColumns[]={"txtName","txtInfo"};
	private  final int mViewIds[]={
			R.id.txtView_name,
			R.id.txtView_info,
	};

	private final int SMS_MODE[]={
			R.string.enable_acm_mode,
			R.string.enable_report_mode,
			R.string.enable_reply_mode,
			R.string.set_working_mode,
			R.string.set_the_got_time,
			R.string.setup_the_customized_command,
			R.string.status_check,
			R.string.check_gsm_signal
	};

	private final int SMS_MODE_HINT[]={
			R.string.enable_acm_mode_hint,
			R.string.enable_report_mode_hint,
			R.string.enable_reply_mode_hint,
			R.string.set_working_mode_hint,
			R.string.set_the_got_time_hint,
			R.string.none,
			R.string.status_check_hint,
			R.string.check_gsm_signal_hint,
	};
	private  ArrayList<HashMap<String,Object>> listItem;
	private static final int ENABLE_ACM_MODE = 0;
	private static final int ENABLE_REPORT_MODE = 1;
	private static final int ENABLE_REPLY_MODE = 2;
	private static final int SET_WORKING_MODE = 3;
	private static final int SET_THE_GOT_TIME=4;
	private static final int SETUP_THE_CUSTOMIZED_COMMAND = 5;
	private static final int STATUS_CHECK=6;
	private static final int CHECK_GSM_SIGNAL=7;

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
		mTitleBar.setTitle(R.string.system_settings);
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
		LogUtil.println("IreneBond position: " + position);
		switch(position){
			case SET_WORKING_MODE :
				setWorkingMode();
				break;
			case CHECK_GSM_SIGNAL :
				checkGSMSignal();
				break;
			case SET_THE_GOT_TIME :
				setTheGotTime();
				break;
			case STATUS_CHECK :
				statusCheck();
				break;
			case ENABLE_ACM_MODE :
				onEnableACMModeClick();
				break;
			case ENABLE_REPORT_MODE :
				onEnableReportModeClick();
				break;
			case ENABLE_REPLY_MODE:
				onEnableReplyModeClick();
				break;
			case SETUP_THE_CUSTOMIZED_COMMAND :
				onSetupCustomizedCommandClick();
				break;
			default:
				break;
		}
	}


	private void onEnableReplyModeClick(){

		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.dlg_relay_initial_status_setting, (ViewGroup)findViewById(R.id.linearLayout_RelayInitialStatus));
		final RadioGroup radioGroup=(RadioGroup)layout.findViewById(R.id.radioGroupRelayInitialStatus);
		radioGroup.check(getRadioButtonStatus(MenuActivity.ENABLE_REPLY_MODE));
		new Builder(SystemSettingsActivity.this).setTitle(R.string.enable_reply_mode)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (getPhoneNo().length() != 0)
					setRadioButtonStatus(radioGroup, MenuActivity.ENABLE_REPLY_MODE);
				String sms = new String();
				if (radioGroup.getCheckedRadioButtonId() == R.id.radioBtnRelayInitialStatus_ON) {
					sms = "#PWD" + getPassword() + "#REPLY=ON";
				} else {
					sms = "#PWD" + getPassword() + "#REPLY=OFF";
				}
				LogUtil.println("sms: " + sms);
				if (GateApplication.getInstance().isSMS)
					sendSms(sms);
				else {
						CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_ENABLE_REPLY_MODE, sms);
						OutPackUtil.sendMessage(mCommandOutPacket);
				}
				closeTheDialog(dialog);
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				closeTheDialog(dialog);
			}
		}).show();
	}

	private void onEnableReportModeClick(){

		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.dlg_relay_initial_status_setting, (ViewGroup)findViewById(R.id.linearLayout_RelayInitialStatus));
		final RadioGroup radioGroup=(RadioGroup)layout.findViewById(R.id.radioGroupRelayInitialStatus);
		radioGroup.check(getRadioButtonStatus(MenuActivity.ENABLE_REPORT_MODE));
		new Builder(SystemSettingsActivity.this).setTitle(R.string.enable_report_mode)
				.setView(layout).setPositiveButton(R.string.send,new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(getPhoneNo().length()!=0)
					setRadioButtonStatus(radioGroup, MenuActivity.ENABLE_REPORT_MODE);
				String sms=new String();
				if(radioGroup.getCheckedRadioButtonId()==R.id.radioBtnRelayInitialStatus_ON){
					sms="#PWD"+getPassword()+"#REPORT=ON";
				}else{
					sms="#PWD"+getPassword()+"#REPORT=OFF";
				}
				LogUtil.println("sms: " + sms);
				if (GateApplication.getInstance().isSMS)
					sendSms(sms);
				else {
					CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_ENABLE_REPORT_MODE, sms);
					OutPackUtil.sendMessage(mCommandOutPacket);
				}
				closeTheDialog(dialog);
			}
		}).setNegativeButton(R.string.cancel,new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				closeTheDialog(dialog);
			}
		}).show();
	}

	private void onEnableACMModeClick(){
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.dlg_relay_initial_status_setting, (ViewGroup)findViewById(R.id.linearLayout_RelayInitialStatus));
		final RadioGroup radioGroup=(RadioGroup)layout.findViewById(R.id.radioGroupRelayInitialStatus);
		radioGroup.check(getRadioButtonStatus(MenuActivity.ENABLE_ACM_MODE));
		new Builder(SystemSettingsActivity.this).setTitle(R.string.enable_acm_mode)
				.setView(layout).setPositiveButton(R.string.send,new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(getPhoneNo().length()!=0)
					setRadioButtonStatus(radioGroup, MenuActivity.ENABLE_ACM_MODE);
				String sms=new String();
				if(radioGroup.getCheckedRadioButtonId()==R.id.radioBtnRelayInitialStatus_ON){
					sms="#PWD"+getPassword()+"#ACM=ON";
				}else{
					sms="#PWD"+getPassword()+"#ACM=OFF";
				}
				LogUtil.println("sms: " + sms);
				if (GateApplication.getInstance().isSMS)
					sendSms(sms);
				else {
					CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_ENABLE_ACM_MODE, sms);
					OutPackUtil.sendMessage(mCommandOutPacket);
				}
				closeTheDialog(dialog);
			}
		}).setNegativeButton(R.string.cancel,new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				closeTheDialog(dialog);
			}
		}).show();
	}

	private void onSetupCustomizedCommandClick(){
		Intent mIntent = new Intent(mActivity,SetCustomizedCommandActivity.class);
		startActivity(mIntent);
	}

	private void setWorkingMode() {
		// TODO Auto-generated method stub
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.dlg_working_mode, (ViewGroup) findViewById(R.id.linearLayout_workingMode));
		final RadioGroup radioGroup=(RadioGroup)layout.findViewById(R.id.radioGroupWorkingMode);
		radioGroup.check(getWorkMode());
		new Builder(SystemSettingsActivity.this).setTitle(R.string.set_working_mode)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (getPhoneNo().length() != 0)
					setWorkMode(radioGroup);
				String sms = new String();
				if (radioGroup.getCheckedRadioButtonId() == R.id.radioBtn_mode1) {
					sms = "#PWD" + getPassword() + "#MODE1";
				} else {
					sms = "#PWD" + getPassword() + "#MODE0";
				}
				LogUtil.println("sms: " + sms);
				if (GateApplication.getInstance().isSMS)
					sendSms(sms);
				else {
					CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_SET_WORKING_MODE, sms);
					OutPackUtil.sendMessage(mCommandOutPacket);
				}
				closeTheDialog(dialog);
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				closeTheDialog(dialog);
			}
		}).show();
	}
	private void setWorkMode(RadioGroup radioGroup) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(MenuActivity.WORKING_MODE, radioGroup.getCheckedRadioButtonId());
		editor.commit();

	}
	private int getWorkMode(){
		int mode=GateApplication.getInstance().getSpecialSharedPreferences().getInt(MenuActivity.WORKING_MODE, R.id.radioBtn_mode0);
		if(mode==R.id.radioBtn_mode1)
			return mode;
		else
			return R.id.radioBtn_mode0;

	}

	private void checkGSMSignal() {
		// TODO Auto-generated method stub
		Builder builder = new Builder(this);
		builder.setMessage(R.string.is_check_gsm_signal);
		builder.setTitle(R.string.check_gsm_signal);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String sms="#PWD"+getPassword()+"#CSQ?";
				LogUtil.println("sms: " + sms);
				if (GateApplication.getInstance().isSMS)
					sendSms(sms);
				else {
					CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_CHECK_GSM_SIGNAL, sms);
					OutPackUtil.sendMessage(mCommandOutPacket);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				closeTheDialog(dialog);
			}
		});
		builder.create().show();
	}
	private void setTheGotTime() {
		// TODO Auto-generated method stub
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.dlg_got_timer_setting, (ViewGroup)findViewById(R.id.linearlayout_setgottime));
		final EditText edtText=(EditText)layout.findViewById(R.id.enter_serial_no);
		final TextView txtView=(TextView)layout.findViewById(R.id.txtView_time);
		txtView.setText(R.string.relay_close_time);
		if(getRelayCloseTime().length()==0)
			edtText.setHint(getStringId());
		else
			edtText.setText(getRelayCloseTime());
		final RadioGroup radioGroup=(RadioGroup)layout.findViewById(R.id.radioGroupSetGotTime);
		radioGroup.check(getGotTimeBaseTimer());
		new Builder(SystemSettingsActivity.this).setTitle(R.string.set_the_got_time)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String time = edtText.getText().toString().trim();
				if (edtText.getText().length() == 0) {
					keepTheDialog(dialog, R.string.please_input_relay_close_time);
				} else if (Integer.parseInt(time) < 1) {
					keepTheDialog(dialog, R.string.number_too_small);
					edtText.setText("");
					edtText.setHint(getStringId());
					setRelayCloseTime("");
				} else if (Integer.parseInt(time) >= 65530) {
					keepTheDialog(dialog, R.string.number_too_big);
					edtText.setText("");
					edtText.setHint(getStringId());
					setRelayCloseTime("");
				} else {
					if (getPhoneNo().length() != 0)
						setRelayCloseTime(time);
//					#PWD123456#GOT5000:MILLISECOND
//					#PWD123456#GOT500:SECOND
//					#PWD123456#GOT500:MINUT
					String sms = "#PWD" + getPassword() + "#GOT" + time + ":" + getRadioButtonString(radioGroup.getCheckedRadioButtonId());
					if (getPhoneNo().length() != 0)
						setGotTimeBaseTimer(radioGroup);
					LogUtil.println("sms: " + sms);
					if (GateApplication.getInstance().isSMS)
						sendSms(sms);
					else {
						CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_GOT_TIMER_SETTING, sms);
						OutPackUtil.sendMessage(mCommandOutPacket);
					}
					closeTheDialog(dialog);
				}
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				closeTheDialog(dialog);
			}
		}).show();;
	}
	private String getRadioButtonString(int checkId){
		if(checkId==R.id.radioBtn_mil){
			return "MILLISECOND";
		}else if(checkId==R.id.radioBtn_sec){
			return "SECOND";
		}else{
			return "MINUTE";
		}
	}
	private void setGotTimeBaseTimer(RadioGroup radioGroup) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(MenuActivity.SET_GOT_BASE_TIMER, radioGroup.getCheckedRadioButtonId());
		editor.commit();

	}
	private int getGotTimeBaseTimer(){
		int radioBtn=GateApplication.getInstance().getSpecialSharedPreferences().getInt(MenuActivity.SET_GOT_BASE_TIMER, R.id.radioBtn_mil);
		if(radioBtn==R.id.radioBtn_min||radioBtn==R.id.radioBtn_sec)
			return radioBtn;
		else
			return R.id.radioBtn_mil;
	}
	private void setRelayCloseTime(String time){
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putString(MenuActivity.SET_RELAY_CLOSE_TIME, time);
		editor.commit();
	}
	private String getRelayCloseTime(){
		String time=GateApplication.getInstance().getSpecialSharedPreferences().getString(MenuActivity.SET_RELAY_CLOSE_TIME, "");
		// LogUtil.println("time: "+time);
		return time;
	}
	private int getStringId() {
		// TODO Auto-generated method stub
		if(getGotTimeBaseTimer()==R.id.radioBtn_mil)
			return R.string.pleaseinput_setgottime_mil;
		else if(getGotTimeBaseTimer()==R.id.radioBtn_min)
			return R.string.pleaseinput_setgottime_min;
		else
			return R.string.pleaseinput_setgottime_sec;
	}

	private void setRadioButtonStatus(RadioGroup radioGroup,String key) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(key, radioGroup.getCheckedRadioButtonId());
		editor.commit();

	}

	private int getRadioButtonStatus(String key,int defaultValue){
		int mode=GateApplication.getInstance().getSpecialSharedPreferences().getInt(MenuActivity.SET_RELAY_INITIAL_STATUS_SETTING, defaultValue);
		if((mode==R.id.radioBtnRelayInitialStatus_OFF))
			return mode;
		else
			return R.id.radioBtnRelayInitialStatus_ON;
	}

	private int getRadioButtonStatus(String key){
		int mode=GateApplication.getInstance().getSpecialSharedPreferences().getInt(key, R.id.radioBtnRelayInitialStatus_OFF);
		if((mode==R.id.radioBtnRelayInitialStatus_OFF))
			return mode;
		else
			return R.id.radioBtnRelayInitialStatus_ON;
	}
	private void statusCheck() {
		// TODO Auto-generated method stub
		Builder builder = new Builder(this);
		builder.setMessage(R.string.is_check_status);
		builder.setTitle(R.string.status_check);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String sms="#PWD"+getPassword()+"#STATUS?";
				LogUtil.println("sms: " + sms);
				if (GateApplication.getInstance().isSMS)
					sendSms(sms);
				else {
					CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_ON_SYSTEM_STATUS_DATA_CHECK, sms);
					OutPackUtil.sendMessage(mCommandOutPacket);
				}
				closeTheDialog(dialog);
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				closeTheDialog(dialog);
			}
		});
		builder.create().show();
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
				case 1:
					MenuActivity.instance.updateSMSContent(msg.getData(), tv_last_content);
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
			case Config.COMMAND_ENABLE_ACM_MODE:
			case Config.COMMAND_ENABLE_REPORT_MODE:
			case Config.COMMAND_ENABLE_REPLY_MODE:
			case Config.COMMAND_SET_WORKING_MODE:
			case Config.COMMAND_GOT_TIMER_SETTING:
			case Config.COMMAND_ON_SYSTEM_STATUS_DATA_CHECK:
			case Config.COMMAND_CHECK_GSM_SIGNAL:
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