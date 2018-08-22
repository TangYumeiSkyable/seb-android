package com.supor.suporairclear.application;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.accloud.common.ACConfiguration;
import com.accloud.service.ACNotificationMgr;
import com.accloud.utils.PreferencesUtils;
import com.google.gson.Gson;
import com.rihuisoft.loginmode.service.SEBPushIntentService;
import com.rihuisoft.loginmode.utils.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.supor.suporairclear.config.Config;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.TimeCount;
import com.supor.suporairclear.model.UserInfo;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Map;


/**
 * Created by zhaoqy on 2015/12/17.
 */
public class MainApplication extends MultiDexApplication {
    private static MainApplication mInstance;
    public static UserInfo mUser;
    public static String location_province;
    public static String location_city;
    public static double latitude, longitude;
    public static String
            location_district;
    public static Long deviceId;
    public static boolean set_city_flg;
    public static boolean first_flg, first_device_flg;
    public TextView mLocationResult, logMsg;

    private ActivityLifecycleCallbacksImpl mActivityLifecycleCallbacksImpl;

    public Vibrator mVibrator;
    public ACNotificationMgr notificationMgr;
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private static Typeface TypeFaceUbuntu;
    public static Context sContext;
    public static boolean cookies_state = true;
    public int activityCounter = 0;
    public static int tipNumber = 0, quitNumber = 0, outdoorVal = 0;
    private SimpleDateFormat sf;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate() {
        super.onCreate();
        //内存泄漏检测工具 -- start--
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        umengConfig();
        try {
            sContext = getApplicationContext();
            changeFont();
//            AC.init(this, Config.MAJORDOAMIN, Config.MAJORDOMAINID, AC.TEST_MODE);
            AC.init(this, Config.MAJORDOAMIN, Config.MAJORDOMAINID, AC.PRODUCTION_MODE, AC.REGIONAL_CENTRAL_EUROPE);
            ACConfiguration.CONNECT_TIMEOUT = 30;
            ACConfiguration.READ_TIMEOUT = 30;

            mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
            mInstance = this;
            first_flg = PreferencesUtils.getString(MainApplication.getInstance(), "first_flg") == null;
            first_device_flg = PreferencesUtils.getString(MainApplication.getInstance(), "first_device_flg") == null;
            String tip = PreferencesUtils.getString(MainApplication.getInstance(), "tipNumber");
            String quit = PreferencesUtils.getString(MainApplication.getInstance(), "quitNumber");
            sf = new SimpleDateFormat("yyyy/MM/dd");
            if (tip != null) {
                TimeCount tipObj = new Gson().fromJson(tip, TimeCount.class);
                if (sf.format(System.currentTimeMillis()).equals(tipObj.getTime())) {
                    tipNumber = tipObj.getNumber();
                    outdoorVal = tipObj.getValue();
                }
            }
            if (quit != null) {
                TimeCount quitObj = new Gson().fromJson(quit, TimeCount.class);
                if (sf.format(System.currentTimeMillis()).equals(quitObj.getTime())) {
                    quitNumber = quitObj.getNumber();
                }
            }
            initUserInfo();
            initLocation();

            mActivityLifecycleCallbacksImpl = new ActivityLifecycleCallbacksImpl();
            this.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacksImpl);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 友盟配置
     */
    private void umengConfig() {
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret
         */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "b94d1a13fd86619f69d4e8c16b60f601");
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true);

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //设置完全自定义推送
        mPushAgent.setPushIntentServiceClass(SEBPushIntentService.class);
        //注册推送服务，每次调用register方法都会回调该接口

        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Logger.i(deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Logger.i(s + ":" + s1);
            }
        });


    }


    public void initLocation() {
        location_province = PreferencesUtils.getString(MainApplication.getInstance(), "province");
        location_city = PreferencesUtils.getString(MainApplication.getInstance(), "city");
        location_district = PreferencesUtils.getString(MainApplication.getInstance(), "district");
        if (PreferencesUtils.getString(MainApplication.getInstance(), "latitude") != null) {
            latitude = Double.valueOf(PreferencesUtils.getString(MainApplication.getInstance(), "latitude"));
        }
        if (PreferencesUtils.getString(MainApplication.getInstance(), "longitude") != null) {
            longitude = Double.valueOf(PreferencesUtils.getString(MainApplication.getInstance(), "longitude"));
        }

        if (location_province == null) {
            location_province = "Paris";
        }

        if (location_city == null) {
            location_city = "--";
            location_district = "";
            set_city_flg = false;
        } else {
            set_city_flg = true;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void initUserInfo() {
        try {
            if (AC.accountMgr().isLogin()) {
                long uid = PreferencesUtils.getLong(MainApplication.getInstance(), "uid", 0);
                String name = PreferencesUtils.getString(MainApplication.getInstance(), "name");
                String telephoneNo = PreferencesUtils.getString(MainApplication.getInstance(), "telephoneNo");
                mUser = new UserInfo(uid, name, telephoneNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void UserLogin(UserInfo user) {
        try {
            mUser = user;
            PreferencesUtils.putLong(MainApplication.getInstance(), "uid", mUser.getUserId());
            PreferencesUtils.putString(MainApplication.getInstance(), "name", mUser.getNickName());
            PreferencesUtils.putString(MainApplication.getInstance(), "telephoneNo", mUser.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static MainApplication getInstance() {
        return mInstance;
    }


    /**
     * Set the default font
     */
    public void changeFont() {
        TypeFaceUbuntu = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-R.ttf");
        try {
            Field field = Typeface.class.getDeclaredField("MONOSPACE");
            field.setAccessible(true);
            field.set(null, TypeFaceUbuntu);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private class ActivityLifecycleCallbacksImpl implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityCounter += 1;
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCounter -= 1;
            if (activityCounter == 0) {
                quitNumber = tipNumber;
                TimeCount quitObj = new TimeCount();
                quitObj.setTime(sf.format(System.currentTimeMillis()));
                quitObj.setNumber(quitNumber);
                PreferencesUtils.putString(MainApplication.getInstance(), "quitNumber", new Gson().toJson(quitObj));
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

}
