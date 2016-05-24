package com.xiaohongquan.xiaohongquan.imwebsocket.config;


/**
 * 配置
 */
public class ConnectConfig {

    public static final String SOCKET_HEART_BEAT_MSG = "~#QK#~";

    public static long NET_CONNECT_INTERVAL = 5 * 1000;                                     //重连的时间
    public static long SOCKET_HEART_BEAT_RATE = 5 * 1000;                                   //心跳时间

}
