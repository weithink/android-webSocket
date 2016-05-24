package com.xiaohongquan.xiaohongquan.imwebsocket.config;

/**
 * 事件配置
 */
public class EventConfig {

    public static final String EVENT_LOGIN = "login";                   //登录
    public static final String EVENT_LOGOUT = "logout";                 //登出
    public static final String EVENT_LOGIN_SUCCESS = "loginsuccess";     //登录成功事件
    public static final String EVENT_LOGIN_ERROR = "loginerror";         //登录出错事件
    public static final String EVENT_MESSAGE = "message";               //收到消息事件
    public static final String EVENT = "event";
    public static final String EVENT_ALERT = "alert";
    public static final String SEND_MSG_SUCCESS = "sendmsgsuccess";       //发送成功
    public static final String SEND_MSG_ERROR = "sendmsgerror";           //发送失败
    public static final String INIT_CHANNEL = "initchannel";             //聊天初始化
    public static final String ERROR = "error";                         //出错
    public static final String KEEP_ALIVE = "keepalive";                 //心跳包检测
}
