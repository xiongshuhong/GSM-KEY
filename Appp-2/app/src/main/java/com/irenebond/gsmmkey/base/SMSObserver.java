package com.irenebond.gsmmkey.base;

/**
 * Created by Irene on 2016/3/31.
 */
public interface SMSObserver {
    public void notifySMSPacketArrived(String mContent);
}
