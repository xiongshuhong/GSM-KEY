package com.irenebond.gsmmkey;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.bean.IPPortBean;
import com.irenebond.gsmmkey.bean.LoginBean;
import com.irenebond.gsmmkey.bean.UserBean;
import com.irenebond.gsmmkey.network.Config;
import com.irenebond.gsmmkey.network.OutPackUtil;
import com.irenebond.gsmmkey.network.SocketThreadManager;
import com.irenebond.gsmmkey.network.TCPClient;
import com.irenebond.gsmmkey.network.packet.RegisterInPacket;

import irene.com.framework.util.LogUtil;


/**
 * Created by Irene on 2016/1/25.
 */
public class RegisterActivity extends NetBaseActivity implements View.OnClickListener {

    private EditText et_user_name,et_password;
    private EditText et_confirm_passwd;
    private EditText et_email;
    private TextView tv_ip,tv_port;
    private Button btn_register;
    @Override
    public void installViews() {
        setContentView(R.layout.activity_register);
        mTitleBar.setTitle("Register");
        mTitleBar.setLeftBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        IPPortBean mIPPortBean = (IPPortBean)getIntent().getExtras().getSerializable("mIPPortBean");
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confirm_passwd = (EditText) findViewById(R.id.et_confirm_passwd);
        et_email = (EditText) findViewById(R.id.et_email);
        tv_ip = (TextView) findViewById(R.id.tv_ip);
        tv_ip.setText(mIPPortBean.getIp());
        tv_port = (TextView) findViewById(R.id.tv_port);
        tv_port.setText(mIPPortBean.getPort());
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:
                onRegisterClick();
                break;
        }
    }

    private void onRegisterClick(){
        String userName = et_user_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirmPasswd = et_confirm_passwd.getText().toString().trim();
        String email = et_email.getText().toString().trim();

        if(userName.length() ==0){
            Toast.makeText(mActivity, "Enter User Name!", Toast.LENGTH_SHORT).show();
        }else if(password.length() == 0){
            Toast.makeText(mActivity,"Enter Password!",Toast.LENGTH_SHORT).show();
        }else if(confirmPasswd.length() == 0){
            Toast.makeText(mActivity,"Enter Confirm Password!",Toast.LENGTH_SHORT).show();
        }else if(email.length() == 0){
            Toast.makeText(mActivity,"Enter Email!",Toast.LENGTH_SHORT).show();
        }else if(!password.equals(confirmPasswd)){
            Toast.makeText(mActivity,"Password not the Same!",Toast.LENGTH_SHORT).show();
        }else{
            UserBean mUserBean = new UserBean();
            mUserBean.setUsername(userName);
            mUserBean.setPassword(password);
            mUserBean.setServerIP(tv_ip.getText().toString());
            mUserBean.setServerPort(tv_port.getText().toString());
            GateApplication.getInstance().mUserBean = mUserBean;
            showCustomLoadingView();
            GateApplication.getInstance().isRegister = true;
            if(TCPClient.init()){//已经初始化直接登录
                OutPackUtil.sendMessage(getOutPacket(userName, password, email));
            }
        }
    }

    @Override
    public void notifyTcpPacketArrived(int mCommand, String mPacket) {
        String userName = et_user_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        super.notifyTcpPacketArrived(mCommand, mPacket);
        switch (mCommand){
            case Config.COMMAND_CONNET_SUCCESS:
                LogUtil.println("IreneBond 链接成功2");
                OutPackUtil.sendMessage(getOutPacket(userName, password, email));
                break;
            case Config.COMMAND_REGISTER:
                showCustomNormalView();
                RegisterInPacket mRegisterInPacket = getPacket(mPacket,RegisterInPacket.class);
                Toast.makeText(mActivity,mRegisterInPacket.getResult(),Toast.LENGTH_SHORT).show();
                if(mRegisterInPacket.getResult().equals("register success")) {
                    LoginBean mLoginBean = new LoginBean();
                    mLoginBean.setUsername(userName);
                    mLoginBean.setPassword(password);
                    Intent mIntent = new Intent();
                    mIntent.putExtra("mLoginBean", mLoginBean);
                    mActivity.setResult(RESULT_OK, mIntent);
                    mActivity.finish();
                }
                break;
            case Config.COMMAND_CONNET_FAIL:
                showCustomNormalView();
                break;
        }
    }


    private String getOutPacket(String userName,String password,String email){
        String mContent = "{\"cmd\":\"200\",\"username\":\"%s\",\"password\":\"%s\",\"Email\":\"%s\",\"id\":1}";
        String sendContent = String.format(mContent,userName,password,email);
        return sendContent;
    }
}
