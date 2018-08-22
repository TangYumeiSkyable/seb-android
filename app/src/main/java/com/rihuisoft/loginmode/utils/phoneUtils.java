package com.rihuisoft.loginmode.utils;

import android.content.Context;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/5/25.
 */

public class phoneUtils {
    /**
     * Gets the current time zone
     * @return
     */
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, TimeZone.SHORT);
        return strTz;

    }


    /**
     * Gets the current system language format
     * @param mContext
     * @return
     */
    public static String getCurrentLanguage(Context mContext){
        Locale locale =mContext.getResources().getConfiguration().locale;
        String language=locale.getLanguage();
        return language;
    }

    /**
     * Gets the current system state
     * @param mContext
     * @return
     */
    public static String getCurrentCountry(Context mContext){
        Locale locale =mContext.getResources().getConfiguration().locale;
        String country = locale.getCountry();
        return country;
    }
}
