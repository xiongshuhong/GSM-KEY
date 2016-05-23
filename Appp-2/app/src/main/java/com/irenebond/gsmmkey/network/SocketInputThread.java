package com.irenebond.gsmmkey.network;

import android.text.TextUtils;

import com.irenebond.gsmmkey.GateApplication;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.bean.UserBean;
import com.irenebond.gsmmkey.network.packet.LoginOutPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import irene.com.framework.util.LogUtil;

/**
 * 客户端读消息线程
 *
 * @author way
 *
 */
public class SocketInputThread extends Thread
{
	private boolean isStart = true;

	private static String tag = "socket";
	// private MessageListener messageListener;// 消息监听接口对象

	public SocketInputThread(){
	}

	public void setStart(boolean isStart)
	{
		this.isStart = isStart;
	}

	@Override
	public void run()
	{
		while (isStart)
		{
			// 手机能联网，读socket数据
			if (NetworkUtil.isNetworkConnected(GateApplication.getInstance()))
			{
				LogUtil.println("IreneBond读取数据3: ");
				if (!TCPClient.instance().isConnect())
				{
					CLog.e(tag, "TCPClient connet server is fail read thread sleep second" + Config.SOCKET_SLEEP_SECOND);
					try
					{
						sleep(Config.SOCKET_SLEEP_SECOND * 1000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				LogUtil.println("IreneBond读取数据: ");
				readSocket();

				// 如果连接服务器失败,服务器连接失败，sleep固定的时间，能联网，就不需要sleep

				CLog.e("socket","TCPClient.instance().isConnect() " + TCPClient.instance().isConnect() );
			}else
				isStart = false;
		}
	}

	public void readSocket()
	{
		Selector selector = TCPClient.instance().getSelector();
		LogUtil.println("IreneBond读取数据2: " + selector);
		if (selector == null)
		{
			return;
		}
		try
		{
			// 如果没有数据过来，一直柱塞
			while (selector.select() > 0)
			{
				LogUtil.println("IreneBond读取数据4: ");
				if(!NetworkUtil.isNetworkConnected(GateApplication.getInstance())){
					SocketThreadManager.getInstance().releaseInstance();
					break;
				}
				if(!isStart)
					break;
				for (SelectionKey sk : selector.selectedKeys())
				{
					// 如果该SelectionKey对应的Channel中有可读的数据
					if (sk.isReadable())
					{
						// 使用NIO读取Channel中的数据
						SocketChannel sc = (SocketChannel) sk.channel();
						ByteBuffer buffer = ByteBuffer.allocate(2048);
						try
						{
							sc.read(buffer);
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							// continue;
						}
						buffer.flip();
						String receivedString = "";
						// 打印收到的数据
						try
						{
							receivedString = Charset.forName("UTF-8")
									.newDecoder().decode(buffer).toString();
							//消息派发
							if(!TextUtils.isEmpty(receivedString)) {
								LogUtil.println("IreneBond读取数据4: " + receivedString);
								((NetBaseActivity) (GateApplication.getInstance().mLastActivity)).notifyTcpPacketArrived(receivedString);
							}
						} catch (CharacterCodingException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						buffer.clear();
						buffer = null;

						try
						{
							// 为下一次读取作准备
							sk.interestOps(SelectionKey.OP_READ);
							// 删除正在处理的SelectionKey
							selector.selectedKeys().remove(sk);

						} catch (CancelledKeyException e)
						{
							e.printStackTrace();
						}


					}
				}
			}
			// selector.close();
			// TCPClient.instance().repareRead();

		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClosedSelectorException e2)
		{
		}
	}

}
