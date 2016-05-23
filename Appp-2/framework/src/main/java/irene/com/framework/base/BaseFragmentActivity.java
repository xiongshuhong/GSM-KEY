/**
 * All rights Reserved, Copyright (C) HAOWU LIMITED 2011-2014
 * FileName: BaseFragmentActivity.java
 * Version:  $Revision$
 * Modify record:
 * NO. |		Date		|		Name		|		Content
 * 1   |	2014年9月5日		|	huangzs	|	original version
 */
package irene.com.framework.base;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import irene.com.framework.R;
import irene.com.framework.observer.BaseActivityObserver;
import irene.com.framework.view.TitleBar;

/**
 * Created by Irene on 2015/8/18.
 */

public class BaseFragmentActivity extends FragmentActivity implements BaseActivityObserver {

	// 显示内容的view
	protected View loadingView, failView, emptyView;
	// 请求失败后的文字提醒
	protected TextView tv_fail;
	protected LinearLayout ll_normalView;
	protected TitleBar mTitleBar;
	protected LayoutInflater mInflater;
	protected Activity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		initBaseData();
		installViews();
		registerEvents();
	}
	private void initBaseData() {
		super.setContentView(R.layout.base_activity);
		ll_normalView = (LinearLayout) findViewById(R.id.ll_normalView);
		loadingView = findViewById(R.id.loadingView);
		failView = findViewById(R.id.failView);
		emptyView = findViewById(R.id.emptyView);
		tv_fail = (TextView) findViewById(R.id.tv_fail);
        tv_fail.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                reload();
            }
        });
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mTitleBar.setLeftBtnListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext.finish();
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

}
