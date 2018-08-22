package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.APAlertDialogUserDifine;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.config.Constants;

import java.util.List;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P2Fragment extends BaseAPLinkFragment implements View.OnClickListener {
    public static final String TAG = "P2Fragment";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((APAddDeviceActivity) getActivity()).setNavBtn(R.drawable.ico_back, 0);
        dotsview.setVisibility(View.GONE);
        desc_one.setVisibility(View.GONE);
        iv_show.setImageLevel(Constants.IMG_P2);
        desc_two.setText(getString(R.string.aplink_p2));
        btn_addDevice.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(" ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addDevice:
                if (FragmentUtil.judgeGetActivityCanUse(this)) {
                    if (isWifiConnected(getActivity().getApplicationContext())) {
                        storeWiFiInfo();
                        FragmentUtil.replaceSupportFragment(
                                getActivity()
                                , R.id.framlayout_container
                                , new P3Fragment()
                                ,P2Fragment.this
                                , P3Fragment.TAG
                                , true
                                , false);
                    } else {
                        new APAlertDialogUserDifine(getActivity()).builder()
                                .setMsg(getString(R.string.need_connect_wifi))
                                .setPositiveButton(getString(R.string.know), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        goToWifiSetting();
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    private void storeWiFiInfo() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();

        if (connectionInfo != null) {
            String ssid = connectionInfo.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            ((APAddDeviceActivity) getActivity()).domesticWiFiSIID = ssid;
            Logger.i("storeWiFiInfo-ssid:" + ssid);

            //获取加密wifi加密类型
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (wifiConfiguration.SSID == null) {
                    int security = ((APAddDeviceActivity) getActivity()).getSecurity(wifiConfiguration);
                    ((APAddDeviceActivity) getActivity()).securityType = security;
                    ((APAddDeviceActivity) getActivity()).wifiConfiguration = wifiConfiguration;
                    Logger.i("storeWiFiInfo-ssid:" + security);
                    return;
                }


                if (wifiConfiguration.SSID.contains(ssid)) {
                    //匹配到这个wifi
                    int security = ((APAddDeviceActivity) getActivity()).getSecurity(wifiConfiguration);
                    ((APAddDeviceActivity) getActivity()).securityType = security;
                    ((APAddDeviceActivity) getActivity()).wifiConfiguration = wifiConfiguration;
                    Logger.i("storeWiFiInfo-ssid:" + security);
                }
            }
        }
    }

}