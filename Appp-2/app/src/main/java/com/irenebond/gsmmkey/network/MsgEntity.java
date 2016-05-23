package com.irenebond.gsmmkey.network;

/**
 * 存储发送socket的类，包含要发送的BufTest，以及对应的返回结果的Handler
 * @author Administrator
 *
 */
public class MsgEntity
{
	//要发送的消息
	private byte [] bytes;

	public MsgEntity( byte [] bytes)
	{	
		 this.bytes = bytes;
	}
	
	public byte []  getBytes()
	{
		return this.bytes;
	}
	
}
