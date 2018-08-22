package com.supor.suporairclear.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络状态监听
 */
public class NetWorkChangeReceiver extends BroadcastReceiver{
    private  OnNetChangeListener listener;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(listener != null){
            listener.netChangeListener(wifi.isConnected());
        }
    }

    public void setOnNetChangeListener(OnNetChangeListener listener){
        this.listener = listener;
    }

    public interface OnNetChangeListener{
        /**
         * 监听网络状态
         * @param b 是否链接
         */
        void netChangeListener(boolean b);
    }

}
