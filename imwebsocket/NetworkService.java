package com.xiaohongquan.xiaohongquan.imwebsocket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.xiaohongquan.xiaohongquan.cmmon.SCommon;
import com.xiaohongquan.xiaohongquan.imwebsocket.config.ConnectConfig;
import com.xiaohongquan.xiaohongquan.imwebsocket.config.NetworkConfig;
import com.xiaohongquan.xiaohongquan.imwebsocket.listener.NetworkListener;
import com.xiaohongquan.xiaohongquan.imwebsocket.websocketLib.WebSocket;
import com.xiaohongquan.xiaohongquan.imwebsocket.websocketLib.WebSocketConnection;
import com.xiaohongquan.xiaohongquan.imwebsocket.websocketLib.WebSocketException;
import com.xiaohongquan.xiaohongquan.imwebsocket.websocketLib.WebSocketOptions;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by weithink on 16/4/29.
 */
public class NetworkService extends Service {
    private WebSocketConnection mConnection;
    private NetworkListener mNetworkListener;
    private Timer netConnectTimer;
    private TimerTask netConnectTimerTask;

    private boolean netConnect;

    @Override
    public IBinder onBind(Intent intent) {
        return new NetworkServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        netConnectTaskStart();
    }

    @Override
    public void onDestroy() {
        if (mNetworkListener != null) {
            mNetworkListener.serviceDestroy();
        }
        unRegisterNetworkListener();
        netConnectTimer.cancel();
        disconnectSocket();
        super.onDestroy();
    }

    public void connectSocket(boolean initSocket) {
        if (netConnect) {
            return;
        }

        if (mConnection != null && initSocket) {
            mConnection.disconnect();
            mConnection = null;
        }

        if (mConnection == null || !mConnection.isConnected()) {
            mConnection = new WebSocketConnection();
            WebSocketOptions options = new WebSocketOptions();
            options.setMaxFramePayloadSize(NetworkConfig.SOCKET_FRAME_PAYLOAD);
            options.setMaxMessagePayloadSize(NetworkConfig.SOCKET_MSG_PAYLOAD);

            try {
                if(NetworkConfig.SOCKET_URL!= null && NetworkConfig.SOCKET_URL.length() > 0){
                    mConnection.connect(NetworkConfig.SOCKET_URL, new WebSocketConnectionHandler(), options);
                }
            } catch (WebSocketException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void disconnectSocket() {
        if (null!=mConnection)
        mConnection.disconnect();
    }

    public boolean isConnected() {
        if (mConnection == null) {
            return false;
        }
        return mConnection.isConnected();
    }

    public void registerNetworkListener(NetworkListener listener) {
        mNetworkListener = listener;
    }

    public void unRegisterNetworkListener() {
        mNetworkListener = null;
    }

    public void sendTextMsg(String msg) {
        mConnection.sendTextMessage(msg);
    }

    private void netConnectTaskStart() {
        netConnectTimer = new Timer();
        netConnectTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isConnected()) {
                    connectSocket(false);
                }
            }
        };//
//        netConnectTimer.schedule(netConnectTimerTask, ConnectConfig.NET_CONNECT_INTERVAL, ConnectConfig.NET_CONNECT_INTERVAL);
        netConnectTimer.schedule(netConnectTimerTask, ConnectConfig.NET_CONNECT_INTERVAL);
    }

    class WebSocketConnectionHandler implements WebSocket.ConnectionHandler {


        @Override
        public void onOpen() {
            netConnect = false;
            if(isConnected()){
                if (mNetworkListener != null) {
                    mNetworkListener.connect();
                }
            }else{
                disconnectSocket();
            }

        }

        @Override
        public void onClose(int code, String reason) {
            netConnect = false;
//            if (mNetworkListener != null && code == WebSocket.ConnectionHandler.CLOSE_NORMAL) {
//                mNetworkListener.disconnect();
//            }
            if (mNetworkListener != null) {
                mNetworkListener.disconnect();
            }
        }

        @Override
        public void onTextMessage(String payload) {
            if (mNetworkListener != null) {
                mNetworkListener.msgCallback(payload);
            }
        }

        @Override
        public void onRawTextMessage(byte[] payload) {

        }

        @Override
        public void onBinaryMessage(byte[] payload) {

        }
    }

    public class NetworkServiceBinder extends Binder {

        public NetworkService getService() {
            return NetworkService.this;
        }
    }

}
