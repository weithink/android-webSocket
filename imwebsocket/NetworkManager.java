package com.xiaohongquan.xiaohongquan.imwebsocket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


import com.xiaohongquan.xiaohongquan.cmmon.SCommon;
import com.xiaohongquan.xiaohongquan.imwebsocket.listener.NetworkListener;
import com.xiaohongquan.xiaohongquan.imwebsocket.listener.NetworkServiceListener;
import com.xiaohongquan.xiaohongquan.imwebsocket.protocol.ReceiveHandler;
import com.xiaohongquan.xiaohongquan.utils.SUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 连接管理器
 */
public class NetworkManager {

    private NetworkService mNetworkService;
    private NetworkServiceListener mNetworkServiceListener;

    private ArrayList mReceiveHandlerList;

    private NetworkServiceConnection mNetworkServiceConnection;

    private NetworkManager() {
        mReceiveHandlerList = new ArrayList();
    }

    public static NetworkManager getInstance() {
        return SingletonFactory.instance;
    }

    public void startNetworkService(Context context, NetworkServiceListener listener) {
        mNetworkServiceListener = listener;
        mNetworkServiceConnection = new NetworkServiceConnection();
        Intent networkServiceIntent = new Intent(context, NetworkService.class);
        context.getApplicationContext().startService(networkServiceIntent);
        context.getApplicationContext().bindService(networkServiceIntent, mNetworkServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopNetworkService(Context context) {
        if (mNetworkServiceConnection != null) {
            context.getApplicationContext().unbindService(mNetworkServiceConnection);
        }
        context.getApplicationContext().stopService(new Intent(context.getApplicationContext(), NetworkService.class));
    }

    public void connect() {
        if (mNetworkService != null) {
            mNetworkService.connectSocket(true);
        }
    }

    public void tryConnect() {
        if (mNetworkService != null) {
            mNetworkService.connectSocket(false);
        }
    }

    public void disconnect() {
        if (mNetworkService != null) {
            mNetworkService.disconnectSocket();
        }
    }

    public boolean isConnected() {
        if (mNetworkService == null) {
            return false;
        }
        return mNetworkService.isConnected();
    }

    public void registerNetworkReceive(ReceiveHandler handler) {
        mReceiveHandlerList.add(handler);
    }

    public void unRegisterNetworkReceive(ReceiveHandler handler) {
        mReceiveHandlerList.remove(handler);
    }

    public void sendMsg(String msg) {
        if (SUtils.isEmpty(msg)) {
            SCommon.log(this.getClass().getSimpleName() + " .. sendMsg : IS NULL ");
            return;
        } else {
            SCommon.log(this.getClass().getSimpleName() + " .. sendMsg : NOT NULL ");
        }
        mNetworkService.sendTextMsg(msg);
    }

    private void registerNetworkReceive() {
        mNetworkService.registerNetworkListener(new NetworkListener() {
            @Override
            public void connect() {
                msgReceive(ReceiveHandler.STATUS_CONNECT);
            }

            @Override
            public void msgCallback(String msg) {
                msgReceive(msg);
            }

            @Override
            public void disconnect() {
                msgReceive(ReceiveHandler.STATUS_DISCONNECT);
            }

            @Override
            public void serviceDestroy() {
                msgReceive(ReceiveHandler.STATUS_SERVICE_DESTROY);
            }

            private void msgReceive(String msg) {
                Iterator<ReceiveHandler> iterator = mReceiveHandlerList.iterator();
                while (iterator.hasNext()) {
                    iterator.next().receive(msg);
                }
            }
        });
    }

    private static class SingletonFactory {
        private static NetworkManager instance = new NetworkManager();
    }

    private class NetworkServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mNetworkService = ((NetworkService.NetworkServiceBinder) service).getService();
            if (mNetworkService != null) {
                registerNetworkReceive();
                mNetworkServiceListener.serviceOnStar();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mNetworkServiceListener.serviceOnStop();
        }
    }
}
