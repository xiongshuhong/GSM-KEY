package com.irenebond.gsmmkey.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.irenebond.gsmmkey.GateApplication;
import com.irenebond.gsmmkey.MenuActivity;
import com.irenebond.gsmmkey.R;
import com.irenebond.gsmmkey.MainActivity;
import com.irenebond.gsmmkey.SMSDevicesListActivity;
import com.irenebond.gsmmkey.data.GPRSDevicesListBean;

import java.util.ArrayList;


/**
 * Created by Irene on 2016/1/26.
 */
public class SMSDevicesListAdapter extends BaseAdapter{

    private ArrayList<GPRSDevicesListBean> mDatas = new ArrayList<GPRSDevicesListBean>();
    private Activity mActivity;

    public SMSDevicesListAdapter(Activity mActivity){
        this.mActivity = mActivity;
    }

    public void setData(ArrayList<GPRSDevicesListBean> mDatas){
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public void clearData(){
        mDatas = new ArrayList<GPRSDevicesListBean>();
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
        setContent(convertView,position);
        return convertView;
    }

    private void setContent(View convertView,final int position){

        ViewHolder mViewHolder = (ViewHolder)convertView.getTag();
        final GPRSDevicesListBean mGPRSDevicesListBean = mDatas.get(position);
        if(mGPRSDevicesListBean.getDeviceType().equals("000001")) {
            mViewHolder.iv_src.setImageResource(R.drawable.gate_200);
        }else if(mGPRSDevicesListBean.getDeviceType().equals("000002")) {
            mViewHolder.iv_src.setImageResource(R.drawable.gate_2000);
        }
        String id = "ID " + mGPRSDevicesListBean.getDeviceNo();
        mViewHolder.tv_id_no.setText(id);
        mViewHolder.tv_name.setText(mGPRSDevicesListBean.getDeviceName());
        mViewHolder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MainActivity.class);
                GateApplication.getInstance().mGPRSDevicesListBean = mDatas.get(position);
                GateApplication.getInstance().mDatas = mDatas;
                GateApplication.getInstance().position = position;
                mActivity.startActivity(intent);
            }
        });
        mViewHolder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SMSDevicesListActivity)mActivity).editData(position);
            }
        });
        mViewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SMSDevicesListActivity)mActivity).deleteData(position);
            }
        });
    }

    private View init(View convertView){
        if (null == convertView) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_sms_devices_list_adapter, null);
            ViewHolder mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        return convertView;
    }

    private class ViewHolder{

        public TextView tv_edit;
        public TextView tv_delete;
        public TextView tv_id_no;
        public TextView tv_name;
        public ImageView iv_src;
        public RelativeLayout ll_main;
        public ViewHolder(View view){
            tv_edit = (TextView) view.findViewById(R.id.tv_edit);
            tv_delete = (TextView) view.findViewById(R.id.tv_delete);
            tv_id_no = (TextView) view.findViewById(R.id.tv_id_no);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            iv_src = (ImageView) view.findViewById(R.id.iv_src);
            ll_main = (RelativeLayout) view.findViewById(R.id.ll_main);
        }
    }

}
