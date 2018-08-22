package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.service.ACUserDevice;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.view.CustomProgressDialog;
import com.rihuisoft.loginmode.view.SwitchView;
import com.supor.suporairclear.config.ConstantCache;

import java.util.List;


public class SettingActivity extends BaseActivity {
    private TextView tv_verno;

    private boolean[] states = {true, true, true, true};
    private ACAccountMgr accountMgr;

    private ACBindMgr bindMgr;
    private List<ACUserDevice> deviceList;
    private SwitchView mSwitchviewIndoor;
    private SwitchView mSwitchviewOutdoor;
    private SwitchView mSwitchviewFilter;
    private SwitchView mSwitchviewWifi;


    private enum handler_key {
        GETUSERINFO_SUCCESS
    }

    public enum NOTUFICATION_FLAG {
        ONE, TWO, THREE, FOUR
    }

    /**
     * The handler.
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                handler_key key = handler_key.values()[msg.what];
                switch (key) {
                    case GETUSERINFO_SUCCESS:
                        setView();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setting);
            AppManager.getAppManager().addActivity(this);
            accountMgr = AC.accountMgr();

            bindMgr = AC.bindMgr();
            setTitle(getString(R.string.airpurifier_more_show_setting_title));
            initView();
            setNavBtn(R.drawable.ico_back, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Page to initialize
     */
    public void initView() {
        try {
            tv_verno = (TextView) findViewById(R.id.tv_verno);
            String text = "v" + this.getPackageManager().getPackageInfo(
                    "com.groupeseb.airpurifier", 0).versionName;
            tv_verno.setText(text);

            OnSwitchChangedListener onSwitchChangedListener = new OnSwitchChangedListener();
            //室内空气
            mSwitchviewIndoor = (SwitchView) findViewById(R.id.switchview_indoor);
            mSwitchviewIndoor.setTag(NOTUFICATION_FLAG.ONE);
            mSwitchviewIndoor.setOnStateChangedListener(onSwitchChangedListener);

            //室外空气
            mSwitchviewOutdoor = (SwitchView) findViewById(R.id.switchview_outdoor);
            mSwitchviewOutdoor.setTag(NOTUFICATION_FLAG.TWO);
            mSwitchviewOutdoor.setOnStateChangedListener(onSwitchChangedListener);

            //滤网
            mSwitchviewFilter = (SwitchView) findViewById(R.id.switchview_filter);
            mSwitchviewFilter.setTag(NOTUFICATION_FLAG.THREE);
            mSwitchviewFilter.setOnStateChangedListener(onSwitchChangedListener);

            //wifi固件
            mSwitchviewWifi = (SwitchView) findViewById(R.id.switchview_wifi);
            mSwitchviewWifi.setTag(NOTUFICATION_FLAG.FOUR);
            mSwitchviewWifi.setOnStateChangedListener(onSwitchChangedListener);

            setView();
            getUserInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 监听开关
     */
    private class OnSwitchChangedListener implements SwitchView.OnStateChangedListener {

        @Override
        public void toggleToOn(SwitchView view) {
            notificationToggle(view, true);
        }

        @Override
        public void toggleToOff(SwitchView view) {
            notificationToggle(view, false);
        }
    }

    private void notificationToggle(SwitchView view, boolean isClose) {
        if (view != null) {
            NOTUFICATION_FLAG tag = (NOTUFICATION_FLAG) view.getTag();
            switch (tag) {
                case ONE:
                    states[0] = isClose;
                    setUserInfo("notifyFlg1", isClose);
                    break;
                case TWO:
                    states[1] = isClose;
                    setUserInfo("notifyFlg2", isClose);
                    break;
                case THREE:
                    states[2] = isClose;
                    setUserInfo("notifyFlg3", isClose);
                    break;
                case FOUR:
                    states[3] = isClose;
                    setUserInfo("notifyFlg4", isClose);
                    break;
            }
            view.setOpened(isClose);
        }
    }


    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            switch (component) {
                case LEFT:
                    finish();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Get the user extended attributes
     */
    private void getUserInfo() {
        accountMgr.getUserProfile(new PayloadCallback<ACObject>() {
            @Override
            public void success(ACObject object) {
                try {
                    ConstantCache.userProInfo = object;
                    handler.sendEmptyMessage(handler_key.GETUSERINFO_SUCCESS.ordinal());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(ACException e) {
            }
        });
    }

    /**
     * Set user extended attributes
     */
    public void setUserInfo(final String key, final Boolean value) {
        ACObject userProfile = new ACObject();
        userProfile.put(key, value);
        accountMgr.setUserProfile(userProfile, new VoidCallback() {
            @Override
            public void success() {
                ConstantCache.userProInfo.put(key, value);
            }

            @Override
            public void error(ACException e) {
                Log.e("setUserProfile", "error:" + e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    //刷新View
    private void setView() {

        if (ConstantCache.userProInfo != null) {
            states[2] = ConstantCache.userProInfo.getBoolean("notifyFlg3");
            states[1] = ConstantCache.userProInfo.getBoolean("notifyFlg2");
            states[0] = ConstantCache.userProInfo.getBoolean("notifyFlg1");
            states[3] = ConstantCache.userProInfo.getBoolean("notifyFlg4");
        } else {
            states[2] = false;
            states[1] = false;
            states[0] = false;
            states[3] = false;
        }

        mSwitchviewIndoor.setOpened(states[0]);
        mSwitchviewOutdoor.setOpened(states[1]);
        mSwitchviewFilter.setOpened(states[2]);
        mSwitchviewWifi.setOpened(states[3]);

    }
}