package com.irenebond.gsmmkey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.irenebond.gsmmkey.MainActivity;
import com.irenebond.gsmmkey.R;

public class MainAdapter extends BaseAdapter implements ListAdapter {

	private Activity mActivity = null;
	private int mPicutureIDs[] = {
			R.drawable.home_message_but_selector,
			R.drawable.home_phone_but_selector,
			R.drawable.home_switch1_on_selector,
			R.drawable.home_switch1_off_selector,
			R.drawable.home_switch2_on_selector,
			R.drawable.home_switch2_off_selector,
			R.drawable.home_btn1_but_selector,
			R.drawable.home_btn2_but_selector
	};

	public MainAdapter(Activity mActivity) {
		this.mActivity = mActivity;
	}
	@Override
	public int getCount() {
		return (mPicutureIDs.length);
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = init(convertView);
		setContent(convertView,position);
		return convertView;
	}

	private void setContent(View convertView,int position){

		ViewHolder mViewHolder = (ViewHolder)convertView.getTag();
		mViewHolder.iv_src.setImageResource(mPicutureIDs[position]);
	}

	private View init(View convertView){
		if (null == convertView) {
			convertView = mActivity.getLayoutInflater().inflate(R.layout.item_main, null);
			ViewHolder mViewHolder = new ViewHolder(convertView);
			convertView.setTag(mViewHolder);
		}
		return convertView;
	}

	private class ViewHolder{

		public ImageView iv_src;
		public LinearLayout ll_main;
		public ViewHolder(View view){
			iv_src = (ImageView) view.findViewById(R.id.iv_src);
			ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
		}
	}
};
