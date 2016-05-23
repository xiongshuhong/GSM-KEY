package com.irenebond.gsmmkey;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import irene.com.framework.base.BaseActivity;

public class WelcomeActivity extends BaseActivity {

	private ProgressThread mProgressThread=null;
	private boolean isStart=false;

	@Override
	public void installViews() {
		setContentView(R.layout.activity_welcome);
		runCtycle();
	}

	private void runSMSModeActivity(){
		Intent intent=new Intent(mActivity,SMSDevicesListActivity.class);
		startActivity(intent);
		finish();
	}

	private void runCtycle() {
		// TODO Auto-generated method stub
		if(mProgressThread==null){
			mProgressThread=new ProgressThread(mHandler);
			isStart=false;
			mProgressThread.setState(ProgressThread.STATE_RUNNING);
			mProgressThread.start();
		}
	}
	final Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int curval=msg.getData().getInt("CUR_VAL");
			if(curval>100){
				mProgressThread.setState(ProgressThread.STATE_DONE);
				// System.out.println("curval: "+curval);
				runOtherActivity();
				return;
			}
			super.handleMessage(msg);
		}

	};
	public void runOtherActivity(){
		if(!isStart){
			this.finish();
			isStart=true;
			Intent intent=new Intent(this,SMSDevicesListActivity.class);
			this.startActivity(intent);
		}
	}
	class ProgressThread extends Thread{
		public final static int STATE_DONE=0;
		public final static int STATE_RUNNING=1;
		private Handler mHandler=null;
		private int mState=0;
		private int mCurval=0;
		public final static int MAX_VAL=100;
		ProgressThread(Handler handler){
			mHandler=handler;
		}
		public void setState(int state){
			mState=state;
		}
		public void run(){
			mState=STATE_RUNNING;
			while(mState==STATE_RUNNING){
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Message msg=mHandler.obtainMessage();
				Bundle b=new Bundle();
				b.putInt("CUR_VAL", mCurval);
				msg.setData(b);
				mHandler.sendMessage(msg);
				mCurval++;
			}
		}
	}
}
