package com.rihuisoft.loginmode.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.List;

/**
 * Created by wangkun on 20/06/2017.
 */

public class NetworkUtils {
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] allNetworks = manager.getAllNetworks();
            for (Network network : allNetworks) {
                NetworkInfo networkInfo = manager.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        && networkInfo.isConnected()) {
                    return true;
                }
            }
            return false;
        } else {
            NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
    }

    public static void startScan(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
    }

    public static List<ScanResult> getScanResult(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getScanResults();
    }

    public static void configOpenWifi(Context context, String ssid) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = String.format("\"%s\"", ssid);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        configWifi(context, configuration);
    }

    public static void configWPAWifi(Context context, String ssid, String password) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = String.format("\"%s\"", ssid);
        configuration.preSharedKey = String.format("\"%s\"", password);
        configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // For WPA
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // For WPA2
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        configWifi(context, configuration);
    }

    private static void configWifi(Context context, WifiConfiguration configuration) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int networkId = addNetwork(configuration, wifiManager);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
    }

    private static int addNetwork(WifiConfiguration configuration, WifiManager wifiManager) {
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configurations) {
            if (config.SSID.equals(configuration.SSID)) {
                return config.networkId;
            }
        }
        return wifiManager.addNetwork(configuration);
    }
}
