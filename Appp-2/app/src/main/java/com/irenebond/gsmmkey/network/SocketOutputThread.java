package com.irenebond.gsmmkey.network;

import com.irenebond.gsmmkey.GateApplication;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.network.packet.LoginOutPacket;
import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import irene.com.framework.util.LogUtil;


/**
 * 客户端写消息线程
 *
 * @author way
 *
 */
public class SocketOutputThread extends Thread
{
	private boolean isStart = true;
	private static String tag = "socketOutputThread";
	private List<MsgEntity> sendMsgList;

	public SocketOutputThread()
	{
		sendMsgList = new CopyOnWriteArrayList<MsgEntity>();
	}

	public void setStart(boolean isStart)
	{
		this.isStart = isStart;
		synchronized (this)
		{
			notify();
		}
	}

	// 使用socket发送消息
	public boolean sendMsg(byte[] msg) throws Exception
	{


		if (msg == null)
		{
			CLog.e(tag, "sendMsg is null");
			return false;
		}

		try
		{
			TCPClient.instance().sendMsg(msg);
		} catch (IOException e)
		{
			throw (e);
		}

		return true;
	}

	// 使用socket发送消息
	public void addMsgToSendList(MsgEntity msg)
	{

		synchronized (this)
		{
			LogUtil.println("IreneBond network 2");
			this.sendMsgList.add(msg);
			notify();
		}
	}

	@Override
	public void run()
	{
		while (isStart)
		{
			LogUtil.println("IreneBond network 3");
			// 锁发送list
			synchronized (sendMsgList)
			{
				LogUtil.println("IreneBond network 4");
				// 发送消息
				for (MsgEntity msg : sendMsgList)
				{
					try
					{
						LogUtil.println("IreneBond sendMsgList1: " + sendMsgList.size());
						sendMsg(msg.getBytes());
						sendMsgList.remove(msg);
						LogUtil.println("IreneBond sendMsgList2: " + sendMsgList.size());
						// 成功消息，通过hander回传,暂时不通知
						LogUtil.println("IreneBond 发送数据成功：" + new String(msg.getBytes()));
					}catch (ClosedChannelException e){
						e.printStackTrace();
						LogUtil.println("IreneBond error: " + e);
						onError();
					}catch (SocketException e){
						e.printStackTrace();
						LogUtil.println("IreneBond error: " + e);
						onError();
					}catch (IOException e){
						e.printStackTrace();
						LogUtil.println("IreneBond error: " + e);
						onError();
					}catch (Exception e)
					{
						e.printStackTrace();
						LogUtil.println("IreneBond error: " + e);
						onError();
					}
				}
			}

			synchronized (this)
			{
				try
				{
					wait();
					LogUtil.println("IreneBond network 5");
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					LogUtil.println("IreneBond network 6");
					e.printStackTrace();
				}// 发送完消息后，线程进入等待状态
			}
		}
	}

	private void onError(){
		sendMsgList.clear();//数据异常时，清空队列
		if(GateApplication.getInstance().mLastActivity != null) {
//			((NetBaseActivity) (GateApplication.getInstance().mLastActivity)).notifyTcpPacketArrived(Config.COMMAND_ERROR, "Send Fail");
			OutPackUtil.sendLastMessage();
		}
	}



}
