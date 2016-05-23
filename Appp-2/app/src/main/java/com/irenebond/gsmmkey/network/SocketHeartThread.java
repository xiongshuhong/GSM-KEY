package com.irenebond.gsmmkey.network;


import com.irenebond.gsmmkey.GateApplication;
import com.irenebond.gsmmkey.network.packet.HeartOutPacket;

import irene.com.framework.util.LogUtil;

class SocketHeartThread extends Thread
{
	boolean isStop = false;
	static SocketHeartThread s_instance;

	private TCPClient mTcpClient = null;

	static final String tag = "SocketHeartThread";

	public static synchronized SocketHeartThread instance()
	{
		if (s_instance == null)
		{
			s_instance = new SocketHeartThread();
		}
		return s_instance;
	}

	public void stopThread()
	{
		isStop = true;
		s_instance = null;
	}

	/**
	 * 连接socket到服务器, 并发送初始化的Socket信息
	 *
	 * @return
	 */


	private void reConnect()
	{
		TCPClient.instance().reConnect();
	}


	public void run()
	{
		isStop = false;
		while (!isStop)
		{
			// 发送一个心跳包看服务器是否正常
			boolean canConnectToServer = TCPClient.instance().canConnectToServer();
			if(canConnectToServer == false){
				LogUtil.println("IreneBond 开始重新连接");
//				if(GateApplication.getInstance().isLoginSuccess)//只有登录成功的需要重连
//					reConnect();
			}
			try
			{
				Thread.sleep(Config.SOCKET_HEART_SECOND * 1000);

			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private HeartOutPacket getHeartOutPacket(){
		HeartOutPacket mHeartOutPacket = new HeartOutPacket();
		return mHeartOutPacket;
	}

}
