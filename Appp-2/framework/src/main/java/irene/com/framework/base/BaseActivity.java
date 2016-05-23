/**
 * All rights Reserved, Copyright (C) HAOWU LIMITED 2011-2014
 * FileName: BaseActionBarActivity.java
 * Version:  $Revision$
 * Modify record:
 * NO. |		Date		|		Name		|		Content
 * 1   |	2014年9月5日		|	huangzs	|	original version
 */
package irene.com.framework.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import irene.com.framework.R;
import irene.com.framework.observer.BaseActivityObserver;
import irene.com.framework.util.LogUtil;
import irene.com.framework.view.TitleBar;

/**
 * Created by Irene on 2015/8/18.
 */

public class BaseActivity extends Activity implements BaseActivityObserver {

    // 显示内容的view
    protected View loadingView, failView, emptyView;
    // 请求失败后的文字提醒
    protected TextView tv_fail;
    protected LinearLayout ll_normalView;
    protected TitleBar mTitleBar;
    protected Activity mActivity;

    private ViewGroup root;
    private View customLoadingView;
    public String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    protected SMSBroadcastReceiver mSMSBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        initBaseData();
        installViews();
        registerEvents();
    }

    protected void registerSMSSendBroadCastReceiver(){
        mSMSBroadcastReceiver = new SMSBroadcastReceiver();
        IntentFilter sendFilter = new IntentFilter(SMS_SEND_ACTIOIN);
        registerReceiver(mSMSBroadcastReceiver, sendFilter);
    }

    protected void unRegisterSMSSendBroadCastReceiver(){
        if(mSMSBroadcastReceiver != null)
            unregisterReceiver(mSMSBroadcastReceiver);
    }

    private void initBaseData() {
        super.setContentView(R.layout.base_activity);
        ll_normalView = (LinearLayout) findViewById(R.id.ll_normalView);
        loadingView = findViewById(R.id.loadingView);
        failView = findViewById(R.id.failView);
        emptyView = findViewById(R.id.emptyView);
        tv_fail = (TextView) findViewById(R.id.tv_fail);
        mTitleBar = (TitleBar) findViewById(R.id.tb_title);
        failView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reload();
            }
        });
        mTitleBar.setLeftBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void installViews() {

    }

    @Override
    public void registerEvents() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void setContentView(int layoutResID) {
        ll_normalView.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(layoutResID, ll_normalView);
    }

    @Override
    public void showLoadingView() {
        if (loadingView.getVisibility() == View.GONE) {
            ll_normalView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            failView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            ImageView iv_loading = (ImageView) loadingView.findViewById(R.id.iv_loading);
            AnimationDrawable animationDrawable = (AnimationDrawable) iv_loading
                    .getBackground();
            animationDrawable.start();
        }
    }

    @Override
    public void showNormalView() {
        if (ll_normalView.getVisibility() == View.GONE) {
            ll_normalView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            failView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmptyView() {
        if (emptyView.getVisibility() == View.GONE) {
            ll_normalView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            failView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showReloadView(String failStr) {
        if (failView.getVisibility() == View.GONE) {
            ll_normalView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            failView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        tv_fail.setText(failStr + ",点击重新加载");
    }

    public void showCustomLoadingView(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                root = (ViewGroup)getRootView();
                loadingView =  getLayoutInflater().inflate(R.layout.loading, root, false);
                root.addView(loadingView);
            }
        });
    }

    public void showCustomNormalView(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(root != null) {
                    root.removeView(loadingView);
                }
            }
        });
    }

    private View getRootView()
    {
        //return findViewById(android.R.id.content).getRootView();
        return ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
    }

    public class SMSBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    LogUtil.println("IreneBond send Success");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "SMS sends successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                default:
                    LogUtil.println("IreneBond send fail");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity,"SMS sends fail",Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }
    }

}
