package com.irenebond.gsmmkey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.bean.IPPortBean;
import com.irenebond.gsmmkey.bean.LoginBean;
import com.irenebond.gsmmkey.bean.UserBean;
import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.NetworkUtil;
import com.irenebond.gsmmkey.network.OutPackUtil;
import com.irenebond.gsmmkey.network.SocketThreadManager;
import com.irenebond.gsmmkey.network.TCPClient;
import com.irenebond.gsmmkey.network.packet.LoginInPacket;

import irene.com.framework.util.LogUtil;

/**
 * Created by Irene on 2016/1/25.
 */
public class LoginActivity extends NetBaseActivity implements View.OnClickListener {

    private TextView tv_register;
    private Button btn_login;
    private LinearLayout ll_server_info,ll_more;
    private ImageView iv_arrow;
    private EditText et_user_name,et_password;
    private EditText et_ip,et_server_port;
    private static final int REQUEST_REGIESTER = 0x11;
    private static final String LOGIN_SUCCESS = "login success";
    @Override
    public void installViews() {
        super.installViews();
        setContentView(R.layout.activity_login);
        mTitleBar.setTitleOnly(R.string.login);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        tv_register = (TextView) findViewById(R.id.tv_register);
        String content = "<u>Register</u>";
        tv_register.setText(Html.fromHtml(content));
        tv_register.setOnClickListener(this);

        ll_server_info = (LinearLayout) findViewById(R.id.ll_server_info);
        ll_more = (LinearLayout) findViewById(R.id.ll_more);
        ll_more.setOnClickListener(this);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);

        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_password = (EditText) findViewById(R.id.et_password);
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_server_port = (EditText) findViewById(R.id.et_server_port);

        if(GateApplication.getInstance().mUserBean != null){
            et_user_name.setText(GateApplication.getInstance().mUserBean.getUsername());
            et_password.setText(GateApplication.getInstance().mUserBean.getPassword());
            et_ip.setText(GateApplication.getInstance().mUserBean.getServerIP());
            et_server_port.setText(GateApplication.getInstance().mUserBean.getServerPort() + "");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_register:
                onRegisterClick();
                break;
            case R.id.btn_login:
                onLoginClick();
                break;
            case R.id.ll_more:
                onMoreClick();
                break;
            default:
                break;
        }
    }

    private void onMoreClick(){
        if(ll_server_info.getVisibility() == View.GONE) {
            iv_arrow.setImageResource(R.drawable.ic_arrow_up);
            ll_server_info.setVisibility(View.VISIBLE);
        }else {
            iv_arrow.setImageResource(R.drawable.ic_arrow_down);
            ll_server_info.setVisibility(View.GONE);
        }
    }

    private void onLoginClick(){

        String userName = et_user_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String serverIP = et_ip.getText().toString().trim();
        String serverPort = et_server_port.getText().toString().trim();
        if(userName.length() ==0){
            Toast.makeText(mActivity,"Enter User Name!",Toast.LENGTH_SHORT).show();
        }else if(password.length() == 0){
            Toast.makeText(mActivity,"Enter Password!",Toast.LENGTH_SHORT).show();
        }else if(serverIP.length() == 0){
            Toast.makeText(mActivity,"Enter Server IP!",Toast.LENGTH_SHORT).show();
        }else if(serverPort.length() == 0){
            Toast.makeText(mActivity,"Enter Server Port!",Toast.LENGTH_SHORT).show();
        }else{
            GateApplication.getInstance().isRegister = false;
            if(NetworkUtil.isNetworkConnected(mActivity)) {
                UserBean mUserBean = new UserBean();
                mUserBean.setUsername(userName);
                mUserBean.setPassword(password);
                mUserBean.setServerIP(serverIP);
                mUserBean.setServerPort(serverPort);
                GateApplication.getInstance().mUserBean = mUserBean;
                showCustomLoadingView();
                if(TCPClient.init()){
                    OutPackUtil.sendMessage(getLoginPacket());
                }
            }else{
                Toast.makeText(mActivity,"Unable to connect to the network!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onRegisterClick(){

        String serverIP = et_ip.getText().toString().trim();
        String serverPort = et_server_port.getText().toString().trim();
        if(serverIP.length() == 0){
            Toast.makeText(mActivity,"Enter Server IP!",Toast.LENGTH_SHORT).show();
        }else if(serverPort.length() == 0){
            Toast.makeText(mActivity,"Enter Server Port!",Toast.LENGTH_SHORT).show();
        }else{
            IPPortBean mIPPortBean = new IPPortBean();
            mIPPortBean.setIp(serverIP);
            mIPPortBean.setPort(serverPort);
            Intent mIntent = new Intent(this,RegisterActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("mIPPortBean",mIPPortBean);
            mIntent.putExtras(mBundle);
            startActivityForResult(mIntent,REQUEST_REGIESTER);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void notifyTcpPacketArrived(int mCommand, String mPacket) {
        super.notifyTcpPacketArrived(mCommand, mPacket);
        switch (mCommand){
            case Config.COMMAND_CONNET_SUCCESS:
                break;
            case Config.COMMAND_LOGIN:
                showCustomNormalView();
                onLogin(mPacket);
                LogUtil.println("IreneBond：登录1: " + mPacket);
                break;
            case Config.COMMAND_CONNET_FAIL:
                showCustomNormalView();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Network Connect Fail!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private void onLogin(String mPacket){
        LogUtil.println("IreneBond 2： " + mPacket);
        try {
            LoginInPacket mLoginInPacket = getPacket(mPacket, LoginInPacket.class);
            LogUtil.println("IreneBond 数据： " + mLoginInPacket.getDevices());
            if (mLoginInPacket.getInfo().equals(LOGIN_SUCCESS)) {
                GateApplication.getInstance().isLogin = false;
                saveLoginData();
                GateApplication.getInstance().isLoginSuccess = true;
                Intent mIntent = new Intent(this, GPRSDevicesListActivity.class);
                mIntent.putExtra("devicesList", mLoginInPacket.getDevices());
                startActivity(mIntent);
                finish();
            } else {
                Toast.makeText(mActivity, mLoginInPacket.getInfo(), Toast.LENGTH_SHORT).show();
            }
        }catch (com.alibaba.fastjson.JSONException e){
            Toast.makeText(mActivity, "Login Fail", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLoginData(){

        String userName = et_user_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String serverIP = et_ip.getText().toString().trim();
        String serverPort = et_server_port.getText().toString().trim();

        SharedPreferences preferences = this.getSharedPreferences(MenuActivity.PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MenuActivity.LOGIN_IP, serverIP);
        editor.putString(MenuActivity.LOGIN_PORT, serverPort);
        editor.putString(MenuActivity.LOGIN_NAME, userName);
        editor.putString(MenuActivity.LOGIN_PASSWD, password);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_REGIESTER){
                LoginBean mLoginBean = (LoginBean)data.getSerializableExtra("mLoginBean");
                et_user_name.setText(mLoginBean.getUsername());
                et_password.setText(mLoginBean.getPassword());
                onLoginClick();
            }
        }
    }
}
