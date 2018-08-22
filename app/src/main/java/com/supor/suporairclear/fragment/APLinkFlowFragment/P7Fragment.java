package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.utils.Logger;
import com.supor.suporairclear.config.Constants;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P7Fragment extends BaseAPLinkFragment {

    public static final String TAG = "P7Fragment";

    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.i("P7Fragment:onCreateView");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(mWifiBroadCastReceiver, filter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Logger.i("P7Fragment:onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.i("P7Fragment:onStop");
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dotsview.setLightDotsNumber(4);
        desc_one.setVisibility(View.GONE);
        iv_show.setImageLevel(Constants.IMG_P7);
        desc_two.setText(R.string.connect);
        btn_addDevice.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onResume() {
        Logger.i("P7Fragment:onResume");
        super.onResume();
        getActivity().setTitle(getString(R.string.fragment_p6_title));
        if(this.isVisible()){
            handleJump(getActivity());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.i("P7Fragment:onDestryView");
        try {
            if(mWifiBroadCastReceiver != null){
                getActivity().unregisterReceiver(mWifiBroadCastReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private BroadcastReceiver mWifiBroadCastReceiver = new BroadcastReceiver() {
        private int count;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (P7Fragment.this.isResumed())
                handleJump(context);

        }
    };

    private void handleJump(final Context context) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (FragmentUtil.judgeGetActivityCanUse(P7Fragment.this))
                    if (currentWifiIsSebWIfi(context)) {
                        FragmentUtil.replaceSupportFragment(
                                getActivity()
                                , R.id.framlayout_container
                                , new P8Fragment()
                                , P7Fragment.this
                                , P8Fragment.TAG
                                , true
                                , false);
                    } else {
                        FragmentUtil.replaceSupportFragment(
                                getActivity()
                                , R.id.framlayout_container
                                , new P15Fragment()
                                , P7Fragment.this
                                , P8Fragment.TAG
                                , true
                                , false);
                    }
            }
        };
        handler.postDelayed(runnable, 1000);

    }


    private boolean currentWifiIsSebWIfi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                String ssid = connectionInfo.getSSID();
                if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
                return ssid != null && ssid.contains("AIR PURIFIER");
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            if(handler != null && runnable != null){
                handler.removeCallbacks(runnable);
                try {
                    if(mWifiBroadCastReceiver != null){
                        getActivity().unregisterReceiver(mWifiBroadCastReceiver);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else {
            try {
                if(mWifiBroadCastReceiver != null){
                    handleJump(getActivity());
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                    getActivity().registerReceiver(mWifiBroadCastReceiver, filter);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
