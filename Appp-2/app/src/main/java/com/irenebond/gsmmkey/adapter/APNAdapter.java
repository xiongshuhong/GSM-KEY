package com.irenebond.gsmmkey.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irenebond.gsmmkey.R;
import com.irenebond.gsmmkey.bean.APNInfoBean;
import java.util.ArrayList;

/**
 * Created by Irene on 2016/4/28.
 */
public class APNAdapter extends BaseAdapter {

    private ArrayList<APNInfoBean> mDatas = new ArrayList<APNInfoBean>();
    private Activity mActivity;

    public APNAdapter(Activity mActivity){
        this.mActivity = mActivity;
    }

    public void setData(ArrayList<APNInfoBean> mDatas){
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public ArrayList<APNInfoBean> getData(){

        if(mDatas == null)
            mDatas = new ArrayList<APNInfoBean>();
        return mDatas;
    }

    public void clearData(){
        mDatas = new ArrayList<APNInfoBean>();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = init(convertView);
        setContent(convertView, position);
        return convertView;
    }

    private void setContent(View convertView, final int position){

        ViewHolder mViewHolder = (ViewHolder)convertView.getTag();
        final APNInfoBean mGPRSDevicesListBean = mDatas.get(position);
        mViewHolder.tv_apn_name.setText(mGPRSDevicesListBean.getApn());
    }

    private View init(View convertView){
        if (null == convertView) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_apn, null);
            ViewHolder mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        return convertView;
    }

    private class ViewHolder {
        public LinearLayout ll_main;
        public TextView tv_apn_name;

        public ViewHolder(View view) {
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
            tv_apn_name = (TextView) view.findViewById(R.id.tv_apn_name);
        }
    }
}
