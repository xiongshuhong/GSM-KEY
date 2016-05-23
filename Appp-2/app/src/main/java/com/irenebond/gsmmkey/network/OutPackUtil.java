package com.irenebond.gsmmkey.network;

import com.irenebond.gsmmkey.GateApplication;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.network.packet.LoginOutPacket;
import com.irenebond.gsmmkey.network.packet.base.OutPacket;

import java.util.Objects;

import irene.com.framework.util.LogUtil;

/**
 * Created by Irene on 2016/3/25.
 */
public class OutPackUtil {
    public static Object needSendContent = "";
    public static void sendAuto(){
        GateApplication.getInstance().isLogin = false;
        sendMessage(needSendContent);
    }

    public static void sendLastMessage(){
        LogUtil.println("IreneBond 启动重联机制");
        if(NetworkUtil.isNetworkConnected(GateApplication.getInstance()) && GateApplication.getInstance().isLoginSuccess) {
            GateApplication.getInstance().isLogin = false;
            TCPClient.instance().reConnect();
        }else{
            ((NetBaseActivity) (GateApplication.getInstance().mLastActivity)).notifyTcpPacketArrived(Config.COMMAND_ERROR, "Send Fail");
        }
    }
    public static void sendMessage(Object content){
//        if(GateApplication.getInstance().isLoginSuccess && NetworkUtil.isNetworkConnected(GateApplication.getInstance()) && !TCPClient.instance().canConnectToServer()){
//            LogUtil.println("IreneBond link 重联");
//            TCPClient.instance().reConnect();
//        }
        needSendContent = content;
        if(SocketThreadManager.s_SocketManager == null){
            LogUtil.println("IreneBond 11 11");
            if(NetworkUtil.isNetworkConnected(GateApplication.getInstance()) && GateApplication.getInstance().isLoginSuccess) {
                LogUtil.println("IreneBond 11 12");
                TCPClient.instance().reConnect();
            }else if(!NetworkUtil.isNetworkConnected(GateApplication.getInstance()))
                ((NetBaseActivity) (GateApplication.getInstance().mLastActivity)).notifyTcpPacketArrived(Config.COMMAND_ERROR, "Send Fail");
            return;
        }else{
            if(!NetworkUtil.isNetworkConnected(GateApplication.getInstance()) && GateApplication.getInstance().isLoginSuccess){
                ((NetBaseActivity) (GateApplication.getInstance().mLastActivity)).notifyTcpPacketArrived(Config.COMMAND_ERROR, "Send Fail");
                return;
            }
        }

        if(GateApplication.getInstance().isLogin){
            GateApplication.getInstance().isLogin = false;
            TCPClient.instance().reConnect();
            return;
        }

        if(content instanceof String)
            SocketThreadManager.getInstance().sendPacket((String) content);
        else if(content instanceof OutPacket)
            SocketThreadManager.getInstance().sendPacket((OutPacket) content);
    }
}
