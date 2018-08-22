package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.zhy.autolayout.utils.L;

import java.util.List;

/**
 * 获取wif相关信息
 */
public class WifiEngine {

    private Context mContext;
    private final WifiManager mWifiManager;

    public WifiEngine(Context context) {
        mContext = context.getApplicationContext();
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public List<ScanResult> getScanResult() {
        if (mWifiManager != null) {
            return mWifiManager.getScanResults();
        } else {
            return null;
        }
    }

    public void startScan() {
        L.e("startScan");
        if (mWifiManager != null)
            mWifiManager.startScan();
    }

    /**
     * 获取当前wifi的SSID
     *
     * @return
     */
    public String getCurrentWifiSSID() {
        int wifiState = mWifiManager.getWifiState();
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                String ssid = connectionInfo.getSSID();
                String replace = ssid.replace("\"", "");
                return replace;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean linkAppointWifi(String ssid, String password) {
        if (mWifiManager != null) {
            WifiConfiguration configuration = new WifiConfiguration();

            configuration.SSID = ssid;

            configuration.hiddenSSID = true;
            configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            configuration.status = WifiConfiguration.Status.ENABLED;

            int networkId = addNetwork(configuration, mWifiManager);
            mWifiManager.disconnect();
            mWifiManager.enableNetwork(networkId, true);
            boolean reconnect = mWifiManager.reconnect();
            return reconnect;
        } else {
            throw new IllegalArgumentException("WIFIManager is not null");
        }
    }

    //是否连接WIFI
    public boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo != null) {
            return true;
        }

        return false;
    }

    private int addNetwork(WifiConfiguration configuration, WifiManager wifiManager) {
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (configuration != null)
            for (WifiConfiguration config : configurations) {
                if (config.SSID != null && config.SSID.equals(configuration.SSID)) {
                    return config.networkId;
                }
            }
        return wifiManager.addNetwork(configuration);
    }
}
