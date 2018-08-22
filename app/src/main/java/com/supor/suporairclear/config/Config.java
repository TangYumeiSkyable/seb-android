package com.supor.suporairclear.config;

import android.content.Context;
import android.content.pm.PackageManager;

import com.rihuisoft.loginmode.activity.AppStartActivity;
import com.rihuisoft.loginmode.activity.BaseActivity;

/**
 * Created by zhaoqy on 2015/7/16.
 */
public class Config {

    public static final String MAJORDOAMIN = "groupeseb";
    public static final long MAJORDOMAINID = 5133;
    public static final int SUBDOMAINID = 5376;
    public static final String SUBMAJORDOMAIN = "rowentaxl";
    public static final String MYSERVICENAME = "SEBService";

    public static final int serviceVersion = 1;

    /**
     * Customer service telephone
     */
    public static final String CONSUMER_HOTLINE = "400-1889-809";

    /**
     * messenger
     */
    public static final int REQ_CODE = 68;

    /**
     * messenger
     */
    public static final int RESP_CODE = 210;

    public static final String PLAY_STORE_URL = "http://play.google.com/store/apps/details?id=com.groupeseb.airpurifier";

    /**
     * Setting json url
     */
    public static String getSettingJsonUrl(Context context) {
        String url = "";
        try {
            int versionCode = context.getPackageManager().getPackageInfo("com.groupeseb.airpurifier", 0).versionCode;
            url = "https://sebplatform.api.groupe-seb.com/assets/PRO_AIR/app/database/android/" + versionCode + "/settings.json";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return url;
    }


}
