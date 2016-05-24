package com.xiaohongquan.xiaohongquan.imwebsocket.protocol;



/**
 * 消息接收者
 */
public interface ReceiveHandler {

     String STATUS_CONNECT = "connect";
     String STATUS_DISCONNECT = "disconnect";
     String STATUS_SERVICE_DESTROY = "service_destory";

    /**
     * 接收到的消息都会回调此方法
     */
    void receive(String msg);

}
