package com.irenebond.gsmmkey.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.irenebond.gsmmkey.network.packet.base.OutPacket;

import java.util.LinkedHashMap;

import irene.com.framework.util.LogUtil;


public class SocketThreadManager
{

	public static SocketThreadManager s_SocketManager = null;

	private SocketInputThread mInputThread = null;

	private SocketOutputThread mOutThread = null;

//	private SocketHeartThread mHeartThread = null;

	// 获取单例
	public static SocketThreadManager getInstance()
	{
		if(s_SocketManager == null){
			s_SocketManager = new SocketThreadManager();
			s_SocketManager.startThreads();
		}
		return s_SocketManager;
	}

	public static boolean init(){
		LogUtil.println("IreneBond 11 初始化");
		if (s_SocketManager == null)
		{
			s_SocketManager = new SocketThreadManager();
			s_SocketManager.startThreads();
			return true;
		}else
			return false;
	}

	// 单例，不允许在外部构建对象
	private SocketThreadManager()
	{
//		mHeartThread = SocketHeartThread.instance();
		mInputThread = new SocketInputThread();
		mOutThread = new SocketOutputThread();
	}

	/**
	 * 启动线程
	 */

	private void startThreads()
	{
//		mHeartThread.start();//暂时关闭心跳
		mInputThread.start();
		mInputThread.setStart(true);
		mOutThread.start();
		mOutThread.setStart(true);
		// mDnsthread.start();
	}

	/**
	 * stop线程
	 */
	public void stopThreads()
	{
//		mHeartThread.stopThread();
//		mHeartThread = null;
		if(mInputThread != null) {
			mInputThread.setStart(false);
			mInputThread = null;
		}
		if(mOutThread != null) {
			mOutThread.setStart(false);
			mOutThread = null;
		}

		if(s_SocketManager != null)
			s_SocketManager = null;
	}

	public void releaseInstance()
	{
		LogUtil.println("IreneBond mCommandOutPacket 被调用了: ");
		if (s_SocketManager != null)
		{
			s_SocketManager.stopThreads();
			s_SocketManager = null;
		}
	}

	/**
	 * 发送消息的实体
	 * @param buffer
	 */
	public void sendPacket(byte [] buffer)
	{
		MsgEntity entity = new MsgEntity(buffer);
		mOutThread.addMsgToSendList(entity);
	}

	/**
	 * 发送消息的实体
	 * @param content
	 */
	public void sendPacket(String content){
		LogUtil.println("IreneBond network 12" + content);
		MsgEntity entity = new MsgEntity(content.getBytes());
		mOutThread.addMsgToSendList(entity);
	}

	public void sendPacket(OutPacket mOutpacket){
		LinkedHashMap<String, Object> mLinkedContent =  ConvertUtil.objectToHashMap(mOutpacket);
		LogUtil.println("发送数据12：" + mLinkedContent);
		String mContent = JSON.toJSONString(mLinkedContent, SerializerFeature.WriteMapNullValue);
		LogUtil.println("发送数据11：" + mContent);
		sendPacket(mContent.replace("\"$change\":null,",""));
	}

}
