package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.ACDeviceActivator;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.common.ACConfiguration;
import com.accloud.service.ACDeviceBind;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;
import com.broadlink.BLResponse;
import com.broadlink.networkapi.APConfigRequest;
import com.google.gson.Gson;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.AppStartActivity;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.utils.ToastUtil;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.config.Constants;
import com.supor.suporairclear.config.SubDominConfig;
import com.supor.suporairclear.config.SubDominUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.broadlink.networkapi.NetworkAPI;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P10Fragment extends BaseAPLinkFragment {
    public static final String TAG = "P10Fragment";
    private CountDownTimer mCountDownTimer;
    private WifiEngine mWifiEngine;
    private WifiManager mWifiManager;
    private boolean isAplinking;//是否正在进行AP配网
    private int tryGetSSIDTypeCount = 0;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        try {
            super.onViewCreated(view, savedInstanceState);
            ((APAddDeviceActivity) getActivity()).setNavBtn(0, 0);
            dotsview.setLightDotsNumber(5);
            desc_one.setVisibility(View.GONE);
            iv_show.setImageLevel(Constants.IMG_P10);
            desc_two.setText(R.string.aplink_p10);
            btn_addDevice.setVisibility(View.INVISIBLE);
            mWifiEngine = new WifiEngine(getActivity().getApplicationContext());

            //注册监听wifi扫描
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            getActivity().registerReceiver(mWifiBroadCastReceiver, filter);
            isAplinking = false;
            mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            prepareApLink();
        } catch (Exception e) {
        }
    }

    private void prepareApLink() {
        Logger.i("prepareApLink");
        if (FragmentUtil.judgeGetActivityCanUse(P10Fragment.this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                new RxPermissions(getActivity())
                        .request(Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    mWifiManager.startScan();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.airpurifier_no_peimission, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                mWifiManager.startScan();
            }

    }

    private BroadcastReceiver mWifiBroadCastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isAplinking) {
                List<ScanResult> list = mWifiManager.getScanResults();
                for (ScanResult scResult : list) {
                    if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(((APAddDeviceActivity) getActivity()).domesticWiFiSIID)) {
                        String capabilities = scResult.capabilities;
                        Logger.i("capabilities=" + capabilities);
                        if (!TextUtils.isEmpty(capabilities)) {
                            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                                Logger.i("wpa");
                                ((APAddDeviceActivity) getActivity()).securityType = APConfigRequest.ENCRYPT_WPA_BOTH;
                            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                                Logger.i("wep");
                                ((APAddDeviceActivity) getActivity()).securityType = APConfigRequest.ENCRYPT_WEP;
                            } else {
                                Logger.i("no");
                                ((APAddDeviceActivity) getActivity()).securityType = APConfigRequest.ENCRYPT_NONE;
                            }
                        }

                    }
                }
                if (getActivity() != null) {
                    final String password = ((APAddDeviceActivity) getActivity()).domesticWiFiPassword;
                    //当密码不为空，但是获取到加密类型是NONE,那就肯定有问题
                    if (!TextUtils.isEmpty(password) && ((APAddDeviceActivity) getActivity()).securityType == APConfigRequest.ENCRYPT_NONE) {
                        if (tryGetSSIDTypeCount < 5) {
                            //继续扫描,扫描次数加一
                            tryGetSSIDTypeCount++;
                            mWifiManager.startScan();
                            Logger.i("扫描加次数：" + tryGetSSIDTypeCount);
                        } else {
                            //通过另一种方式获取
                            if (isGetSSidTypeByKeyMagSuccess() && ((APAddDeviceActivity) getActivity()).securityType == APConfigRequest.ENCRYPT_NONE) {
                                //获取到正确的加密类型
                                Logger.i("success to get securitytype");
                            } else {
                                Logger.i("failed to get securitytype");
                                //没有获取到争取的加密类型
                                ((APAddDeviceActivity) getActivity()).securityType = APConfigRequest.ENCRYPT_WPA_BOTH;
                            }
                            startApLink();
                        }
                    } else {
                        startApLink();
                    }
                }

            }
        }
    };

    private boolean isGetSSidTypeByKeyMagSuccess() {
        //获取加密wifi加密类型
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.SSID.contains(((APAddDeviceActivity) getActivity()).domesticWiFiPassword)) {
                //匹配到这个wifi
                int security = ((APAddDeviceActivity) getActivity()).getSecurity(wifiConfiguration);
                ((APAddDeviceActivity) getActivity()).securityType = security;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setTitle(getString(R.string.fragment_p9_title));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mWifiBroadCastReceiver);
    }

    private void startApLink() {
        isAplinking = true;
        if (getActivity() != null) {
            final String ssid = ((APAddDeviceActivity) getActivity()).domesticWiFiSIID;
            final String password = ((APAddDeviceActivity) getActivity()).domesticWiFiPassword;
            startCountDownTime();

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        APConfigRequest configRequest = new APConfigRequest(ssid, password, ((APAddDeviceActivity) getActivity()).securityType);
                        NetworkAPI instanceBLNetwork = NetworkAPI.getInstanceBLNetwork(P10Fragment.this.getActivity().getApplicationContext());
                        String s = new Gson().toJson(configRequest);
                        String result = instanceBLNetwork.deviceAPConfig(s);
                        BLResponse response = new Gson().fromJson(result, BLResponse.class);
                        if (response.status == BLResponse.SUCCESS) {
                            configWPAWifi(P10Fragment.this.getActivity(), ssid, password);
                        }
                        return result;
                    } catch (Exception e) {
                        return e.getLocalizedMessage() + e.getMessage();
                    }
                }

                @Override
                protected void onPostExecute(String s) {

                    Logger.i("BL：" + s);
                    try {
                        BLResponse response = new Gson().fromJson(s, BLResponse.class);
                        if (response != null && response.isSuccessful()) {
                            ensureWifiConfig();
                        } else {
                            if (FragmentUtil.judgeGetActivityCanUse(P10Fragment.this)) {
                                stopCountDownTime();
                                FragmentUtil.replaceSupportFragment(
                                        getActivity()
                                        , R.id.framlayout_container
                                        , new P14Fragment()
                                        ,P10Fragment.this
                                        , P14Fragment.TAG
                                        , true
                                        , false);
                            }
                        }
                    } catch (Exception e) {
                        if (FragmentUtil.judgeGetActivityCanUse(P10Fragment.this)) {
                            stopCountDownTime();
                            FragmentUtil.replaceSupportFragment(
                                    getActivity()
                                    , R.id.framlayout_container
                                    , new P14Fragment()
                                    ,P10Fragment.this
                                    , P14Fragment.TAG
                                    , true
                                    , false);
                        }
                    }
                }

            }.execute();
        }
    }

    /**
     * 确定手机已经连接身上设备发出的wifi
     */
    private void ensureWifiConfig() {
        Observable
                .interval(3, 5, TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .skipWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        boolean networkConnected = isNetworkOnline();
                        if (networkConnected) {
                            if (isDesWifi()) {
                                return false;
                            } else {
                                configWPAWifi(getActivity(), ((APAddDeviceActivity) getActivity()).domesticWiFiSIID, ((APAddDeviceActivity) getActivity()).domesticWiFiPassword);
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                })
                .firstOrError()
                .flatMapCompletable(Functions.justFunction(Completable.complete()))
                .timeout(60, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Logger.i("切换wifi成功");
                        tryScanDevice();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        stopCountDownTime();
                        if (FragmentUtil.judgeGetActivityCanUse(P10Fragment.this)) {
                            FragmentUtil.replaceSupportFragment(
                                    getActivity()
                                    , R.id.framlayout_container
                                    , new P14Fragment()
                                    ,P10Fragment.this
                                    , P14Fragment.TAG
                                    , true
                                    , false);
                        }
                    }
                });
    }

    public boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 3 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            Logger.i("Process:" + exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isDesWifi() {
        if (mWifiManager != null) {
            WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                String ssid = connectionInfo.getSSID();
                if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
                if (TextUtils.equals(ssid, ((APAddDeviceActivity) getActivity()).domesticWiFiSIID)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void tryScanDevice() {
        AC.deviceActivator(ACDeviceActivator.BL).startAbleLink("", "", 60000, new PayloadCallback<ACDeviceBind>() {
            @Override
            public void success(ACDeviceBind acDeviceBind) {
                if (acDeviceBind != null) {
                    bindDevice(acDeviceBind);
                } else {
                    stopCountDownTime();
                    if (FragmentUtil.judgeGetActivityCanUse(P10Fragment.this)) {
                        FragmentUtil.replaceSupportFragment(
                                getActivity()
                                , R.id.framlayout_container
                                , new P14Fragment()
                                ,P10Fragment.this
                                , P14Fragment.TAG
                                , true
                                , false);
                    }
                }
            }

            @Override
            public void error(ACException e) {
                if (FragmentUtil.judgeGetActivityCanUse(P10Fragment.this)) {
                    Logger.e(e.getMessage());
                    stopCountDownTime();
                    FragmentUtil.replaceSupportFragment(
                            getActivity()
                            , R.id.framlayout_container
                            , new P14Fragment()
                            ,P10Fragment.this
                            , P14Fragment.TAG
                            , true
                            , false);
                }
            }
        });
    }

    private void bindDevice(final ACDeviceBind acDeviceBind) {
        if (acDeviceBind != null) {
            ACConfiguration.CONNECT_TIMEOUT = 60;
            ACConfiguration.READ_TIMEOUT = 60;
            //设置设备默认名称
            String defaultName = "";
            if (acDeviceBind.getSubDomainId() == SubDominConfig.XL.mSubDominId.longValue()) {
                defaultName = getString(R.string.XL_default_name);
            } else if (acDeviceBind.getSubDomainId() == SubDominConfig.XS.mSubDominId.longValue()) {
                defaultName = getString(R.string.xs_default_name);
            }
            AC.bindMgr().bindDevice(SubDominUtil.findNameById(acDeviceBind.getSubDomainId()), acDeviceBind.getPhysicalDeviceId(), defaultName, new PayloadCallback<ACUserDevice>() {
                @Override
                public void success(ACUserDevice acUserDevice) {
                    try {
                        stopCountDownTime();
                        P11Fragment p11Fragment = new P11Fragment();
                        Bundle args = new Bundle();
                        args.putString(Constants.PHYSICALDEVICEID, acDeviceBind.getPhysicalDeviceId());
                        args.putLong(Constants.DEVICEID, acUserDevice.getDeviceId());
                        args.putLong(Constants.SUBDOMAINID, acDeviceBind.getSubDomainId());
                        p11Fragment.setArguments(args);
                        FragmentUtil.replaceSupportFragment(
                                getActivity()
                                , R.id.framlayout_container
                                , p11Fragment
                                ,P10Fragment.this
                                , P11Fragment.TAG
                                , true
                                , false);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void error(ACException e) {
                    //TODO alert bind fail
                    stopCountDownTime();
                    if (FragmentUtil.judgeGetActivityCanUse(P10Fragment.this)) {
                        FragmentUtil.replaceSupportFragment(
                                getActivity()
                                , R.id.framlayout_container
                                , new P14Fragment()
                                ,P10Fragment.this
                                , P14Fragment.TAG
                                , true
                                , false);
                    }
                }
            });
        }
    }

    private void configWPAWifi(Context context, String ssid, String password) {
        try {
            WifiConfiguration configuration = new WifiConfiguration();
            configuration.allowedAuthAlgorithms.clear();
            configuration.allowedGroupCiphers.clear();
            configuration.allowedKeyManagement.clear();
            configuration.allowedPairwiseCiphers.clear();
            configuration.allowedProtocols.clear();
            configuration.SSID = String.format("\"%s\"", ssid);

            if (((APAddDeviceActivity) getActivity()).securityType == 1) {
                configuration.hiddenSSID = true;
                configuration.wepKeys[0] = "\"" + password + "\"";
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.wepTxKeyIndex = 0;
            } else {
                configuration.preSharedKey = String.format("\"%s\"", password);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // For WPA
                configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // For WPA2

                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            }


            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = addNetwork(configuration, wifiManager);
            wifiManager.disconnect();
            wifiManager.enableNetwork(networkId, true);
            boolean reconnect = wifiManager.reconnect();

            Logger.e("是否连接：" + reconnect);
        } catch (Exception e) {
        }
    }

    private int addNetwork(WifiConfiguration configuration, WifiManager wifiManager) {
        try {
            List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration config : configurations) {
                if (config.SSID.equals(configuration.SSID)) {
                    return config.networkId;
                }
            }
        } catch (Exception e) {
        }
        return wifiManager.addNetwork(configuration);
    }

    private void startCountDownTime() {
        mCountDownTimer = new CountDownTimer(120 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (FragmentUtil.judgeGetActivityCanUse(P10Fragment.this)) {

                    FragmentUtil.replaceSupportFragment(
                            getActivity()
                            , R.id.framlayout_container
                            , new P14Fragment()
                            ,P10Fragment.this
                            , P14Fragment.TAG
                            , true
                            , false);
                }
            }
        }.start();
    }

    private void stopCountDownTime() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCountDownTime();
        //放置内存泄漏
        AC.deviceActivator(ACDeviceActivator.BL).stopAbleLink();
    }
}
