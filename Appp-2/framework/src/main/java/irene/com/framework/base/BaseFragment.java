/**
 * All rights Reserved, Copyright (C) HAOWU LIMITED 2011-2014
 * FileName: BaseFragment.java
 * Version:  $Revision$
 * Modify record:
 * NO. |		Date		|		Name		|		Content
 * 1   |	2014年9月6日		|	huangzs	|	original version
 */
package irene.com.framework.base;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import irene.com.framework.R;
import irene.com.framework.observer.BaseFragmentObserver;
import irene.com.framework.view.TitleBar;

/**
 * Created by Irene on 2015/8/18.
 */

public abstract class BaseFragment extends Fragment implements OnClickListener,
        BaseFragmentObserver{

	// 显示内容的view
	protected View loadingView, failView, emptyView;
	// 请求失败后的文字提醒
	protected TextView tv_fail;
	protected LinearLayout ll_normalView;
	protected TitleBar mTitleBar;
	protected View rootView;
	protected LayoutInflater mInflater;
	protected Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		rootView = inflater.inflate(R.layout.base_activity, null);
		mActivity = getActivity();
		initBaseData();
		installViews();
		registerEvents();
		return rootView;
	}

    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }

	/**
	 * 
	 * Method name: initBaseData <BR>
	 * Description:加载基础数据 <BR>
	 * Remark: <BR>
	 * void<BR>
	 */
	private void initBaseData() {
		ll_normalView = (LinearLayout) rootView.findViewById(R.id.ll_normalView);
		loadingView = rootView.findViewById(R.id.loadingView);
		failView = rootView.findViewById(R.id.failView);
		emptyView = rootView.findViewById(R.id.emptyView);
		tv_fail = (TextView) rootView.findViewById(R.id.tv_fail);
        tv_fail.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                reload();
            }
        });
		mTitleBar = (TitleBar) rootView.findViewById(R.id.tb_title);
		failView.setOnClickListener(this);
		mTitleBar.setLeftBtnListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});
	}

    @Override
    public void onDestroy() {
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

	@Override
	public void setContentView(int resourceId) {
		ll_normalView.removeAllViews();
		mInflater.inflate(resourceId, ll_normalView);
	}
	
	public View findViewById(int resourceId){
		if (resourceId < 0) {
			return null;
		}
		return rootView.findViewById(resourceId);

	}
}
