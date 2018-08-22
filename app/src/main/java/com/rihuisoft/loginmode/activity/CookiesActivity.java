package com.rihuisoft.loginmode.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.accloud.utils.PreferencesUtils;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.view.SwitchView;
import com.supor.suporairclear.application.MainApplication;


/**
 * Created by Administrator on 2017/4/28.
 */

public class CookiesActivity extends BaseActivity {
    private boolean states = false;
    private SwitchView switchview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cookies);
            initView();
            setTitle(getString(R.string.airpurifier_login_cookies_title_android));
            setNavBtn(R.drawable.ico_back, "", 0, getString(R.string.airpurifier_more_show_agree_text));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Controls the initialization
     */
    private void initView() {
        switchview = (SwitchView) findViewById(R.id.switchview);
        String checkState = PreferencesUtils.getString(this,"cookieCheck","unChecked");
        states = TextUtils.equals("Checked",checkState);
        switchview.setOpened(states);
        switchview.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                states = true;
                view.setOpened(true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                states = false;
                view.setOpened(false);
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case LEFT:
                finish();
                break;
            case RIGHT:
                if (!states) {
                    PreferencesUtils.putString(MainApplication.getInstance(), "cookieCheck", "unChecked");
                } else {
                    PreferencesUtils.putString(MainApplication.getInstance(), "cookieCheck", "Checked");
                }
                finish();
                break;
        }
    }
}
