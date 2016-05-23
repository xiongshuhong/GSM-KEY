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

public class AuthorizedNoSettingActivity extends NetBaseActivity implements OnItemClickListener{

	private ListView listView=null;
	private SimpleAdapter simpleAdapter;
	private TextView tv_last_content;
	private  final String mColumns[]={"txtName","txtInfo"};
	private  final int mViewIds[]={
			R.id.txtView_name,
			R.id.txtView_info,
	};
	private  ArrayList<HashMap<String,Object>> listItem;
	private SMSReceiver mSMSReceiver;
	public static final int SET_AUTHORIZED_NO = 0;
	public static final int INQUIRY_AUTHORIZED_NO=1;
	public static final int REMOVE_AUTHORIZED_NO=2;
	public static final int SET_ALARM_NO = 3;
	public static final int INQUIRY_ALARM_NO=4;
	public static final int REMOVE_ALARM_NO=5;
	public static final int INQUIRY_A_NUMBER = 6;

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
		mTitleBar.setTitle(R.string.authorized_settings);
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
		map.put(mColumns[0],res.getString(R.string.set_authorized));
		map.put(mColumns[1],res.getString(R.string.pwd_tel));
		listItem.add(map);


		map=new HashMap<String,Object>();
		map.put(mColumns[0],res.getString(R.string.inquiry_authorized));
		map.put(mColumns[1],res.getString(R.string.pwd_tel_serial_no));
		listItem.add(map);

		map=new HashMap<String,Object>();
		map.put(mColumns[0],res.getString(R.string.remove_authorized_no));
		map.put(mColumns[1],res.getString(R.string.pwd_tel_serial_));
		listItem.add(map);

		map=new HashMap<String,Object>();
		map.put(mColumns[0],res.getString(R.string.set_alarm));
		map.put(mColumns[1],res.getString(R.string.pwd_alarm));
		listItem.add(map);

		map=new HashMap<String,Object>();
		map.put(mColumns[0],res.getString(R.string.inquiry_alarm));
		map.put(mColumns[1],res.getString(R.string.pwd_alarm_serial_no));
		listItem.add(map);

		map=new HashMap<String,Object>();
		map.put(mColumns[0],res.getString(R.string.remove_alarm_no));
		map.put(mColumns[1],res.getString(R.string.pwd_alarm_serial_));
		listItem.add(map);

		map=new HashMap<String,Object>();
		map.put(mColumns[0],res.getString(R.string.inquery_a_number));
		map.put(mColumns[1],res.getString(R.string.inquery_a_number_hint));
		listItem.add(map);

		simpleAdapter.notifyDataSetChanged();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		switch(arg2){
			case SET_AUTHORIZED_NO:
				setAuthorizedNo();
				break;
			case INQUIRY_AUTHORIZED_NO:
				inquiryAuthorizedNo();
				break;
			case REMOVE_AUTHORIZED_NO:
				removeAuthorizedNo();
				break;
			case SET_ALARM_NO:
				setAlarmNo();
				break;
			case INQUIRY_ALARM_NO:
				inquiryNo();
				break;
			case REMOVE_ALARM_NO:
				removeAlarmNo();
				break;
			case INQUIRY_A_NUMBER:
				onInquiryANumberClick();
				break;
			default:
				break;
		}
	}

	private void onInquiryANumberClick(){
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.authorizedno, (ViewGroup) findViewById(R.id.authorized));
		final EditText edtText=(EditText)layout.findViewById(R.id.enter_serial_no);
		TextView txtView_time = (TextView) layout.findViewById(R.id.txtView_time);
		txtView_time.setText("Phone Number");
		edtText.setHint("Enter Phone Number");
		new Builder(AuthorizedNoSettingActivity.this).setTitle(R.string.please_fill_the_phone_number)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int length = edtText.getText().toString().trim().length();
				String mString = edtText.getText().toString().trim();
				if (length == 0) {
					keepTheDialog(dialog, R.string.please_fill_the_phone_number);
				}else {
					String sms = "#PWD" + getPassword() + "#QUERY=" + mString;
					LogUtil.println("sms: " + sms);
					if (GateApplication.getInstance().isSMS)
						sendSms(sms);
					else {
						CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_INQUIRY_A_NO, sms);
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
		}).show();
	}
	private void setAlarmNo() {
		// TODO Auto-generated method stub
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.dlg_set_authorized_no, (ViewGroup)findViewById(R.id.set_authorized));
		final EditText edtText=(EditText)layout.findViewById(R.id.edt_enter_authorized_no);
		final EditText edtText2=(EditText)layout.findViewById(R.id.enter_serial_no);
		final TextView txtView=(TextView)layout.findViewById(R.id.textView_authorized_no);
		final TextView txtView2=(TextView)layout.findViewById(R.id.textView_enter_serial_no);
		txtView.setText(R.string.alarm_no);
		edtText.setHint(R.string.enter_alarm_no);
		txtView2.setText(R.string.alarm_serial_no);
		edtText2.setHint(R.string.alarm_serial_no_hint);
		new Builder(AuthorizedNoSettingActivity.this).setTitle(R.string.please_fill_all)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Toast.makeText(AuthorizedNoSystem.this,edtText2.getText().toString(), Toast.LENGTH_LONG).show();
				if (edtText.getText().toString().trim().length() == 0) {
					keepTheDialog(dialog, R.string.please_enter_alarm_no);
				} else if (edtText2.getText().toString().trim().length() == 0) {
					keepTheDialog(dialog, R.string.please_enter_alarm_serial_no);
				} else {
					int serial = Integer.parseInt(edtText2.getText().toString().trim());
					if (serial < 1) {
						keepTheDialog(dialog, R.string.alarm_no_low);
						edtText2.setText("");
					} else if (serial > 5) {
						keepTheDialog(dialog, R.string.alarm_no_big);
						edtText2.setText("");
					} else {
						String mString = edtText2.getText().toString().trim();
						String mSerial;
						if (mString.length() > 2)
							mSerial = mString.substring(mString.length() - 2, mString.length());
						else if (mString.length() == 2)
							mSerial = mString;
						else
							mSerial = "0" + mString;
						setAlarmSerialNo(serial);
						//String sms="#PWD"+getPassword()+"#WHL001="+edtText2.getText().toString().trim();
						String sms = "#PWD" + getPassword() + "#ALARM" + mSerial + "=" + edtText.getText().toString().trim();
						String hint = getStringFromId(R.string.send_sms_successfully);
						LogUtil.println("sms: " + sms);
						if (GateApplication.getInstance().isSMS)
							sendSms(sms);
						else {
							CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_SET_ALARM_NO, sms);
							OutPackUtil.sendMessage(mCommandOutPacket);
						}
						closeTheDialog(dialog);
					}
				}
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				closeTheDialog(dialog);
			}
		}).show();
	}
	private void setAlarmSerialNo(int serialNo){
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(MenuActivity.SET_ALARM_SERIAL_NO, serialNo);
		editor.commit();
	}
	private void setAuthorizedNo(String text){
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putString(MenuActivity.SET_AUTHORIZED_NO, text);
		editor.commit();
	}
	private String getAuthorizedNo(){
		String text=GateApplication.getInstance().getSpecialSharedPreferences().getString(MenuActivity.SET_AUTHORIZED_NO, "");
		//	// LogUtil.println("Authorized No: "+text);
		return text;
	}
	private void inquiryNo() {
		// TODO Auto-generated method stub

		Builder builder = new Builder(this);
		builder.setMessage(R.string.is_inquiry_alarm);
		builder.setTitle(R.string.inquiry_alarm);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String sms = "#PWD" + getPassword() + "#ALARM=ALL?";
				LogUtil.println("sms: " + sms);
				if (GateApplication.getInstance().isSMS)
					sendSms(sms);
				else {
					CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_INQUIRY_ALARM_NO, sms);
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

	private void removeAlarmNo() {
		// TODO Auto-generated method stub
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.authorizedno, (ViewGroup)findViewById(R.id.authorized));
		final EditText edtText=(EditText)layout.findViewById(R.id.enter_serial_no);
		final TextView txtView=(TextView)layout.findViewById(R.id.txtView_time);
		txtView.setText(R.string.alarm_no);
		edtText.setHint("Enter Serial No. (01 to 05)");
		new Builder(AuthorizedNoSettingActivity.this).setTitle(R.string.please_fill_all)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String mString = edtText.getText().toString().trim();
				//int serial=Integer.parseInt(mString);
				if (edtText.getText().length() == 0) {
					keepTheDialog(dialog, "Enter Serial No. (01 to 05)");
				} else if (Integer.parseInt(mString) < 1) {
					keepTheDialog(dialog, R.string.alarm_no_low);
					edtText.setText("");
				} else if (Integer.parseInt(mString) > 5) {
					keepTheDialog(dialog, R.string.alarm_no_big);
					edtText.setText("");
				} else {
					String mSerial;
					if (mString.length() > 2)
						mSerial = mString.substring(mString.length() - 2, mString.length());
					else if (mString.length() == 2)
						mSerial = mString;
					else
						mSerial = "0" + mString;

					String sms = "#PWD" + getPassword() + "#ALARM" + mSerial + "=0000000";
					LogUtil.println("sms: " + sms);
					if (GateApplication.getInstance().isSMS)
						sendSms(sms);
					else {
						CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_REMOVE_ALARM_NO, sms);
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
	private void setAuthorizedNo() {
		// TODO Auto-generated method stub
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.dlg_set_authorized_no, (ViewGroup)findViewById(R.id.set_authorized));
		final EditText edtText=(EditText)layout.findViewById(R.id.edt_enter_authorized_no);
		final EditText edtText2=(EditText)layout.findViewById(R.id.enter_serial_no);
		if(number == 2000)
			edtText2.setHint(R.string.enter_serial_no_2000);
		else
			edtText2.setHint(R.string.enter_serial_no_200);
		if(getSerialNo().length()!=0){
			edtText2.setText(getSerialNo());
		}else
			edtText2.setText("");
		if(getAuthorizedNo().length()!=0){
			edtText.setText(getAuthorizedNo());
		}else
			edtText.setText("");
		new Builder(AuthorizedNoSettingActivity.this).setTitle(R.string.please_fill_all)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Toast.makeText(AuthorizedNoSystem.this,edtText2.getText().toString(), Toast.LENGTH_LONG).show();
				int length = edtText2.getText().toString().trim().length();
				if (edtText.getText().length() == 0) {
					keepTheDialog(dialog, R.string.please_enter_the_authorized_no);
				} else if (length == 0) {
					keepTheDialog(dialog, R.string.please_enter_seial_no);
				} else {
					int serial = Integer.parseInt(edtText2.getText().toString().trim());
					if (serial < 1) {
						keepTheDialog(dialog, R.string.serial_no_low);
						edtText2.setText("");
					} else if (serial > number) {
						if(number == 2000)
							keepTheDialog(dialog, R.string.more_than_2000);
						else
							keepTheDialog(dialog, R.string.more_than_200);
						edtText2.setText("");
					} else {
						String mString = edtText2.getText().toString().trim();
						if (getPhoneNo().length() != 0) {
							setSerialNo(serial);
							setAuthorizedNo(edtText.getText().toString().trim());
						}
						String sms = "#PWD" + getPassword() + "#WHL" + mString + "=" + edtText.getText().toString().trim();
						LogUtil.println("IreneBond sms: " + sms);
						if (GateApplication.getInstance().isSMS)
							sendSms(sms);
						else {
							CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_SET_AUTHORIZED_NO, sms);
							OutPackUtil.sendMessage(mCommandOutPacket);
						}
						closeTheDialog(dialog);
					}
				}
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {

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
		mCommandOutPacket.setSendto(GateApplication.getInstance().mGPRSDevicesListBean.getDeviceNo());
		return mCommandOutPacket;
	}

	private void setSerialNo(int serialNo){
		SharedPreferences.Editor editor=GateApplication.getInstance().getSpecialSharedPreferences().edit();
		editor.putInt(MenuActivity.SET_SERIAL_NO, serialNo);
		editor.commit();
	}
	private String getSerialNo(){
		int text=GateApplication.getInstance().getSpecialSharedPreferences().getInt(MenuActivity.SET_SERIAL_NO, 0);
		String mSerial;
		if (text == 0)
			mSerial="";
		else if(text<10)
			mSerial="00"+text;
		else if(text<100)
			mSerial="0"+text;
		else
			mSerial=String.valueOf(text);
		return mSerial;
	}

	private String getStringFromId(int text){
		Resources res=this.getResources();
		return res.getString(text);
	}
	private void inquiryAuthorizedNo() {
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.authorizedno, (ViewGroup) findViewById(R.id.authorized));
		final EditText edtText=(EditText)layout.findViewById(R.id.enter_serial_no);
		if(number == 2000)
			edtText.setHint(R.string.enter_serial_no_2000);
		else
			edtText.setHint(R.string.enter_serial_no_200);

		new Builder(AuthorizedNoSettingActivity.this).setTitle(R.string.please_fill_all)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int length = edtText.getText().toString().trim().length();
				String mString = edtText.getText().toString().trim();
				int serial = 0;
				if (length == 0) {
					keepTheDialog(dialog, R.string.please_enter_seial_no);
				} else {
					serial = Integer.parseInt(mString);
					if (serial < 1) {
						keepTheDialog(dialog, R.string.serial_no_low);
						edtText.setText("");
					} else if (serial > number) {
						if(number == 2000)
							keepTheDialog(dialog, R.string.more_than_2000);
						else
							keepTheDialog(dialog, R.string.more_than_200);
						edtText.setText("");
					} else {
						//	String sms="#PWD"+getPassword()+"#WHL"+getSerialNo()+"?";
						String sms = "#PWD" + getPassword() + "#WHL" + mString + "?";
						LogUtil.println("sms: " + sms);
						if (GateApplication.getInstance().isSMS)
							sendSms(sms);
						else {
							CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_INQUIRY_AUTHORIZED_NO, sms);
							OutPackUtil.sendMessage(mCommandOutPacket);
						}
						closeTheDialog(dialog);
					}
				}
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				closeTheDialog(dialog);
			}
		}).show();

	}
	private void removeAuthorizedNo() {
		// TODO Auto-generated method stub
		LayoutInflater inflater=this.getLayoutInflater();
		final View layout=inflater.inflate(R.layout.authorizedno, (ViewGroup)findViewById(R.id.authorized));
		final EditText edtText=(EditText)layout.findViewById(R.id.enter_serial_no);
		if(number == 2000)
			edtText.setHint(R.string.enter_serial_no_2000);
		else
			edtText.setHint(R.string.enter_serial_no_200);
		new Builder(AuthorizedNoSettingActivity.this).setTitle(R.string.please_fill_all)
				.setView(layout).setPositiveButton(R.string.send, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String mString = edtText.getText().toString().trim();
				int serial=0;
				if(edtText.getText().toString().trim().length()==0){
					keepTheDialog(dialog,R.string.please_enter_seial_no);
				}else {
					serial=Integer.parseInt(mString);
					if(serial<1){
						keepTheDialog(dialog,R.string.serial_no_low);
						edtText.setText("");
					}else if(serial>number){
						if(number == 2000)
							keepTheDialog(dialog,R.string.more_than_2000);
						else
							keepTheDialog(dialog, R.string.more_than_200);
						edtText.setText("");
					}else{
						String sms="#PWD"+getPassword()+"#WHL"+mString+"=0000000";
						LogUtil.println("sms: " + sms);
						if(GateApplication.getInstance().isSMS)
							sendSms(sms);
						else{
							CommandOutPacket mCommandOutPacket = getCommandOutPacket(Config.COMMAND_REMOVE_AUTHORIZED_NO, sms);
							OutPackUtil.sendMessage(mCommandOutPacket);
						}
						closeTheDialog(dialog);
					}
				}
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {

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
	private class SMSReceiver extends BroadcastReceiver{

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
			case Config.COMMAND_SET_AUTHORIZED_NO:
			case Config.COMMAND_INQUIRY_AUTHORIZED_NO:
			case Config.COMMAND_REMOVE_AUTHORIZED_NO:
			case Config.COMMAND_SET_ALARM_NO:
			case Config.COMMAND_INQUIRY_ALARM_NO:
			case Config.COMMAND_REMOVE_ALARM_NO:
			case Config.COMMAND_INQUIRY_A_NO:
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