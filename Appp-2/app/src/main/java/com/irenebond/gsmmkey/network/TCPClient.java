package com.irenebond.gsmmkey.network;

import android.os.Handler;
import android.os.Message;

import com.irenebond.gsmmkey.GateApplication;
import com.irenebond.gsmmkey.base.NetBaseActivity;
import com.irenebond.gsmmkey.network.packet.HeartOutPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import irene.com.framework.util.LogUtil;


/**
 * NIO TCP 客户端
 *
 */
public class TCPClient
{
	// 信道选择器
	private Selector selector;

	// 与服务器通信的信道
	SocketChannel socketChannel;

	// 要连接的服务器Ip地址
	private String hostIp;

	// 要连接的远程服务器在监听的端口
	private int hostListenningPort;

	public static TCPClient s_Tcp = null;

	public boolean isInitialized = false;

	public static synchronized TCPClient instance()
	{
		LogUtil.println("IreneBond 11 初始化远程线程成功 s_Tcp: " + s_Tcp);
		if (s_Tcp == null)
		{
			s_Tcp = new TCPClient(GateApplication.getInstance().mUserBean.getServerIP(),
					GateApplication.getInstance().mUserBean.getServerPort());
		}
		return s_Tcp;
	}

	public static boolean init(){
		if(s_Tcp == null){
			s_Tcp = new TCPClient(GateApplication.getInstance().mUserBean.getServerIP(),
					GateApplication.getInstance().mUserBean.getServerPort());
			return false;
		}
		return true;
	}

	private static final int INITIALIZE_SUCCESS = 1;
	private static final int INITIALIZE_FAIL = -1;

	private Handler mHandler = new Handler(GateApplication.getInstance().getMainLooper()){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
				case INITIALIZE_SUCCESS:
					((NetBaseActivity) (GateApplication.getInstance().mLastActivity)).notifyTcpPacketArrived(Config.COMMAND_CONNET_SUCCESS, null);
					isInitialized = true;
					break;
				case INITIALIZE_FAIL:
					((NetBaseActivity) (GateApplication.getInstance().mLastActivity)).notifyTcpPacketArrived(Config.COMMAND_CONNET_FAIL, null);
					isInitialized = false;
					s_Tcp = null;
					break;
				default:
					break;
			}
		}

	};

	/**
	 * 构造函数
	 *
	 * @param HostIp
	 * @param HostListenningPort
	 * @throws IOException
	 */
	public TCPClient(String HostIp, int HostListenningPort)
	{
		this.hostIp = HostIp;
		this.hostListenningPort = HostListenningPort;
		initialize();
	}

	/**
	 * 初始化
	 *
	 * @throws IOException
	 */
	public void initialize()
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				boolean done = false;
				try
				{
					LogUtil.println("IreneBond hostIp: " + hostIp + " hostListenningPort: " + hostListenningPort);
					// 打开监听信道并设置为非阻塞模式
					socketChannel = SocketChannel.open(new InetSocketAddress(hostIp,
							hostListenningPort));
					LogUtil.println("IreneBond socketChannel: " + socketChannel);
					if (socketChannel != null)
					{
						socketChannel.socket().setTcpNoDelay(false);
						socketChannel.socket().setKeepAlive(true);
						// 设置 读socket的timeout时间
						socketChannel.socket().setSoTimeout(
								Config.SOCKET_READ_TIMOUT);
						socketChannel.configureBlocking(false);

						// 打开并注册选择器到信道
						selector = Selector.open();
						if (selector != null)
						{
							socketChannel.register(selector, SelectionKey.OP_READ);
							done = true;
						}
						mHandler.sendEmptyMessage(INITIALIZE_SUCCESS);
					}
				} catch(Exception e){
					LogUtil.println("IreneBond e: " + e);
					mHandler.sendEmptyMessage(INITIALIZE_FAIL);
				}finally
				{
					try{
						if (!done && selector != null)
						{
							selector.close();
						}
						if (!done && socketChannel != null)
						{
							socketChannel.close();
						}
					}catch(IOException e){
						mHandler.sendEmptyMessage(INITIALIZE_FAIL);
						LogUtil.println("IreneBond e2: " + e);
					}
				}
			}

		}).start();

	}

	static void blockUntil(SelectionKey key, long timeout) throws IOException
	{

		int nkeys = 0;
		if (timeout > 0)
		{
			nkeys = key.selector().select(timeout);

		} else if (timeout == 0)
		{
			nkeys = key.selector().selectNow();
		}

		if (nkeys == 0)
		{
			throw new SocketTimeoutException();
		}
	}

	/**
	 * 发送字符串到服务器
	 *
	 * @param message
	 * @throws IOException
	 */
	public void sendMsg(String message) throws IOException
	{
		ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("utf-8"));

		if (socketChannel == null)
		{
			throw new IOException();
		}
		socketChannel.write(writeBuffer);
	}

	/**
	 * 发送数据
	 *
	 * @param bytes
	 * @throws IOException
	 */
	public void sendMsg(byte[] bytes) throws IOException
	{
		ByteBuffer writeBuffer = ByteBuffer.wrap(bytes);

		if (socketChannel == null)
		{
			throw new IOException();
		}
		socketChannel.write(writeBuffer);
	}

	/**
	 *
	 * @return
	 */
	public synchronized Selector getSelector()
	{
		return this.selector;
	}

	/**
	 * Socket连接是否是正常的
	 *
	 * @return
	 */
	public boolean isConnect()
	{
		boolean isConnect = false;
		if(socketChannel == null)
			return isConnect;
		if (this.isInitialized)
		{
			isConnect =  this.socketChannel.isConnected();
		}
		return isConnect;
	}

	/**
	 * 关闭socket 重新连接
	 *
	 * @return
	 */
	public void reConnect()
	{
		closeTCPSocket();
		initialize();
	}

	/**
	 * 服务器是否关闭，通过发送一个socket信息
	 *
	 * @return
	 */
	public boolean canConnectToServer()
	{
		try
		{
			if (socketChannel != null)
			{
				if(GateApplication.getInstance().mGPRSDevicesListBean != null && NetworkUtil.isNetworkConnected(GateApplication.getInstance()))
					socketChannel.socket().sendUrgentData(0xff);
				else {
					return false;
				}
//				socketChannel.socket().sendUrgentData(0xff);
			}
		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 关闭socket
	 */
	public void closeTCPSocket()
	{
		try
		{
			if (socketChannel != null)
			{
				socketChannel.close();
			}

		} catch (IOException e)
		{

		}
		try
		{
			if (selector != null)
			{
				selector.close();
			}
		} catch (IOException e)
		{
		}
	}

	/**
	 * 每次读完数据后，需要重新注册selector，读取数据
	 */
	public synchronized void repareRead()
	{
		if (socketChannel != null)
		{
			try
			{
				selector = Selector.open();
				socketChannel.register(selector, SelectionKey.OP_READ);
			} catch (ClosedChannelException e)
			{
				e.printStackTrace();

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
