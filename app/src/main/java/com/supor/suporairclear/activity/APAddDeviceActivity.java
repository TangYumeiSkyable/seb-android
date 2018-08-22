package com.supor.suporairclear.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.broadlink.networkapi.APConfigRequest;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.fragment.APLinkFlowFragment.P2Fragment;
import com.supor.suporairclear.fragment.APLinkFlowFragment.P3Fragment;

import java.lang.reflect.Field;

/**
 * Created by fengjian on 2017/11/9.
 * app-rating popup
 */

public class APAddDeviceActivity extends BaseActivity {

    public String domesticWiFiSIID;
    public String domesticWiFiPassword;
    public int securityType;
    public WifiConfiguration wifiConfiguration;

    public int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return APConfigRequest.ENCRYPT_WPA_BOTH;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return APConfigRequest.ENCRYPT_WPA_BOTH;
        }

        return (config.wepKeys[0] != null) ? APConfigRequest.ENCRYPT_WEP : APConfigRequest.ENCRYPT_NONE;
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case LEFT:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ap_add_device);
        setNavBtn(R.drawable.ico_back, 0);
        ConstantCache.appManager.addActivity(this);
        FragmentUtil.replaceSupportFragment(this
                , R.id.framlayout_container
                , new P3Fragment()
                ,null
                , P3Fragment.TAG
                , false
                , false);
    }

}
