package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.config.Constants;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P6Fragment extends BaseAPLinkFragment {
    public static final String TAG = "P6Fragment";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((APAddDeviceActivity) getActivity()).setNavBtn(R.drawable.ico_back, 0);
        dotsview.setLightDotsNumber(4);
        desc_one.setVisibility(View.GONE);
        iv_show.setImageLevel(Constants.IMG_P6);
        desc_two.setText(R.string.aplink_p6);
        btn_addDevice.setText(R.string.aplink_p6_bt_text);
        btn_addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWifiSetting();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jumpP7Fragment();
                    }
                }, 500);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.fragment_p6_title));
    }

    private void jumpP7Fragment() {
        FragmentUtil.replaceSupportFragment(
                getActivity()
                , R.id.framlayout_container
                , new P7Fragment()
                ,P6Fragment.this
                , P7Fragment.TAG
                , true
                , false);
    }

    public void goToWifiSetting() {

        //the phone of LG cannot support the way of belove
        //Intent mIntent = new Intent();
        //ComponentName comp = new ComponentName("com.android.settings","com.android.settings.wifi.WifiSettings");
        //mIntent.setComponent(comp);
        //mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //mIntent.setAction("android.intent.action.VIEW");
        //startActivity(mIntent);

        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
