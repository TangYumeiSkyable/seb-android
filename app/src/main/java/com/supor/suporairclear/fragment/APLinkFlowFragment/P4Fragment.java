package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.Manifest;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.accloud.common.ACConfiguration;
import com.broadlink.networkapi.APConfigRequest;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.AppStartActivity;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.APAlertDialogUserDifine;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.config.Constants;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.BitSet;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P4Fragment extends BaseAPLinkFragment implements View.OnClickListener {
    public static String TAG = "P4Fragment";


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((APAddDeviceActivity) getActivity()).setNavBtn(R.drawable.ico_back, 0);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String deviceType = arguments.getString("deviceType");
            Log.e(P4Fragment.TAG, deviceType);
        }
        dotsview.setLightDotsNumber(2);
        desc_one.setVisibility(View.GONE);
        iv_show.setImageLevel(Constants.IMG_P4);
        desc_two.setText(getString(R.string.aplink_p4));
        btn_addDevice.setText(getString(R.string.next));
        btn_addDevice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addDevice:
                if (FragmentUtil.judgeGetActivityCanUse(this)) {
                    if (isWifiConnected(getActivity().getApplicationContext())) {
                        storeWifiSSid();
                        FragmentUtil.replaceSupportFragment(
                                getActivity()
                                , R.id.framlayout_container
                                , new P5Fragment()
                                ,P4Fragment.this
                                , P5Fragment.TAG
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


    private void storeWifiSSid() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();

        //获取当前wifi SSID
        if (connectionInfo != null) {
            String ssid = connectionInfo.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            ((APAddDeviceActivity) getActivity()).domesticWiFiSIID = ssid;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.turn_on);
    }

}

