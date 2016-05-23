package com.irenebond.gsmmkey.network.observer;

/**
 * Created by Irene on 2016/3/4.
 */
public interface PacketObserver {
    public void notifyTcpPacketArrived(String mContent);
    public void notifyTcpPacketArrived(int mCommand, String mPacket);
}
