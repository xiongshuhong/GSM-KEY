package com.irenebond.gsmmkey.network;

public class Config
{
	// 默认timeout 时间 60s
	public final static int SOCKET_TIMOUT = 60 * 1000;
	public final static int SOCKET_READ_TIMOUT = 15 * 1000;
	//如果没有连接无服务器。读线程的sleep时间
	public final static int SOCKET_SLEEP_SECOND = 3 ;
	//心跳包发送间隔时间
	public final static int SOCKET_HEART_SECOND = 40 ;
	public final static String BC = "BC";
	public final static int COMMAND_ERROR = -1;
	public final static int COMMAND_LOGIN = 100;
	public final static int COMMAND_REGISTER = 200;
	public final static int COMMAND_HEART = 103;
	/**
	 * AuthorizedNoSetting
	 */
	public final static int COMMAND_SET_AUTHORIZED_NO = 300;
	public final static int COMMAND_INQUIRY_AUTHORIZED_NO = 302;
	public final static int COMMAND_REMOVE_AUTHORIZED_NO = 104;
	public final static int COMMAND_SET_ALARM_NO = 106;
	public final static int COMMAND_INQUIRY_ALARM_NO = 108;
	public final static int COMMAND_REMOVE_ALARM_NO = 110;
	public final static int COMMAND_INQUIRY_A_NO = 112;
	/**
	 * AlarmNoSetting
	 */
	public final static int COMMAND_ALARM_WORKING_MODE = 120;
	public final static int COMMAND_SET_ALARM_SMS_TEXT = 122;
	public final static int COMMAND_ALARM_TEXT_INQUIRY = 124;
	public final static int COMMAND_SYSTEM_STATUS_DATA_CHECK = 126;

	/**
	 * SystemSetting
	 */
	public final static int COMMAND_ENABLE_ACM_MODE = 130;
	public final static int COMMAND_ENABLE_REPORT_MODE = 132;
	public final static int COMMAND_ENABLE_REPLY_MODE = 134;
	public final static int COMMAND_SET_WORKING_MODE = 136;
	public final static int COMMAND_GOT_TIMER_SETTING = 138;
	public final static int COMMAND_ON_SYSTEM_STATUS_DATA_CHECK = 140;
	public final static int COMMAND_CHECK_GSM_SIGNAL = 142;

	/**
	 * Setup the customized command
	 */
	public static final int COMMAND_SETUP_CUSTOMIZED_OPEN1_TEXT = 150;
	public static final int COMMAND_SETUP_CUSTOMIZED_CLOSE1_TEXT=152;
	public static final int COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL1_TEXT=154;
	public static final int COMMAND_SETUP_CUSTOMIZED_COMMAND_TEXT=156;
	public static final int COMMAND_SETUP_CUSTOMIZED_OPEN2_TEXT = 158;
	public static final int COMMAND_SETUP_CUSTOMIZED_CLOSE2_TEXT=160;
	public static final int COMMAND_SETUP_CUSTOMIZED_PULSE_CONTROL2_TEXT=162;
	public static final int COMMAND_SETUP_CUSTOMIZED_COMMAND2_TEXT=164;

	/**
	 * MainActivity
	 */
	public static final int COMMAND_MAIN_1 = 170;
	public static final int COMMAND_MAIN_2=172;
	public static final int COMMAND_MAIN_3=174;
	public static final int COMMAND_MAIN_4=176;
	public static final int COMMAND_MAIN_5 = 178;
	public static final int COMMAND_MAIN_6=180;

	public final static int COMMAND_CONNET_SUCCESS = 182;
	public final static int COMMAND_CONNET_FAIL = 184;

	/**
	 * GPRS DEVICE NAME
	 */
	public static final int COMMAND_GET_DEVICE_NAME = 190;

	/**
	 * Limit Access No Setting
	 */
	public static final int WEEKLY_ACCESS_NO = 192;
	public static final int DAILY_ACCESS_NO  = 194;
}
