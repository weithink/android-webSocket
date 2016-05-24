package com.xiaohongquan.xiaohongquan.imwebsocket.listener;


public interface NetworkListener {

    public void connect();

    public void msgCallback(String msg);

    public void disconnect();

    public void serviceDestroy();
}
