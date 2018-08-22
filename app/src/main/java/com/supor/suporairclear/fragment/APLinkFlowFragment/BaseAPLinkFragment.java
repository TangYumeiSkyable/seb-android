package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.view.DotsView;

/**
 * Created by fengjian on 2017/11/13.
 * function:
 */

public class BaseAPLinkFragment extends Fragment {
    DotsView dotsview;
    TextView desc_one;
    ImageView iv_show;
    TextView desc_two;
    Button btn_addDevice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_ap_common, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dotsview = (DotsView) view.findViewById(R.id.dotsview);
        desc_one = (TextView) view.findViewById(R.id.desc_one);
        iv_show = (ImageView) view.findViewById(R.id.iv_show);
        desc_two = (TextView) view.findViewById(R.id.desc_two);
        btn_addDevice = (Button) view.findViewById(R.id.btn_addDevice);
    }

    public boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public void goToWifiSetting() {

        //the phone of smartisan cannot support the way of belove

        //Intent i = new Intent();
        ////Honeycomb
        //i.setClassName("com.android.settings", "com.android.settings.Settings$WifiSettingsActivity");
        //startActivity(i);

        //It is ok.
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
