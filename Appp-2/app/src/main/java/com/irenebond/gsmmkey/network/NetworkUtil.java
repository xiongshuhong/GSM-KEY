package com.irenebond.gsmmkey.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *   判断网络连接状态
 * @author wjh
 *
 */
public class NetworkUtil
{
	/**
	 * 判断是否有网络连接
	 * @return
	 */
	public static boolean isNetworkConnected(Context mContext)
	{
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
			return false;
		else{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null){
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
						return true;
			}
		}
		return false;
	}
	/**
	 * 判断WIFI网络是否可用
	 * @return
	 */
	public static boolean isWifiConnected(Context mContext)
	{
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWiFiNetworkInfo != null)
		{
			return mWiFiNetworkInfo.isAvailable();
		}else
			return false;
	}
	/**
	 * 判断MOBILE网络是否可用
	 * @return
	 */
	public static boolean isMobileConnected(Context mContext)
	{
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mMobileNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mMobileNetworkInfo != null)
		{
			return mMobileNetworkInfo.isAvailable();
		}else
			return false;

	}

	public static int getConnectedType(Context mContext)
	{
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager
				.getActiveNetworkInfo();
		if (mNetworkInfo != null && mNetworkInfo.isAvailable())
		{
			return mNetworkInfo.getType();
		}else
			return -1;
	}

}
