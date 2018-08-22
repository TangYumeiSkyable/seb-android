package com.rihuisoft.loginmode.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.accloud.cloudservice.AC;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACObject;
import com.accloud.utils.PreferencesUtils;
import com.rihuisoft.loginmode.activity.LoginActivity;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.model.DeviceInfo;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by enyva on 16/6/14.
 */
public class AppUtils {
    private static String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static ACAccountMgr accountMgr = AC.accountMgr();

    public static void setDeviceList(List<DeviceInfo> dev) {
        try {
            List<ACObject> list = new ArrayList<ACObject>();
            HashMap<Long, String> namemap = new HashMap<Long, String>();
            HashMap<Long, DeviceInfo> dmap = new HashMap<Long, DeviceInfo>();
            for (DeviceInfo item : dev) {
                ACObject map = new ACObject();
                map.put("deviceId", item.getDeviceId());
                map.put("deviceName", item.getDeviceName());
                map.put("owner", item.getOwner());
                list.add(map);
                namemap.put(item.getDeviceId(), item.getDeviceName());
                dmap.put(item.getDeviceId(), item);
            }
            ConstantCache.deviceList = list;
            ConstantCache.deviceNameMap = namemap;
            ConstantCache.deviceMap = dmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     *取得wek[1,2,3] selected[1,2,3]等的数组
     *@param str
     * @result String[]
     */
    public static String[] getStrArray(String str) {
        return str.substring(str.indexOf("[") + 1, str.indexOf("]")).split(",");
    }

    /**
     * To determine whether a network connection
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (null != connectivity) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (null != info && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Determine whether the wifi connection
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    public static void reLogin(final Activity activity) {
        try {
//    		Toast.makeText(activity, "User authentication has expired",
//    				AppConstant.TOAST_DURATION).show();
            accountMgr.logout();

//    		AC.notificationMgr().removeAlias(PreferencesUtils.getLong(activity, "uid", 0 ),
//    				new VoidCallback() {
//    					@Override
//    					public void success() {
//    						Toast.makeText(activity, "Remove the alias",
//    								AppConstant.TOAST_DURATION).show();
//    					}			
//    					@Override
//    					public void error(ACException e) {
//    						Toast.makeText(activity, "Remove the alias failure",
//    								AppConstant.TOAST_DURATION).show();
//    					}
//    				});
            Intent intent = new Intent(activity,
                    LoginActivity.class);
            activity.startActivity(intent);
            ConstantCache.appManager.finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static short getCommendSN() {
        if (ConstantCache.COMMEND_SN < 254) {
            ConstantCache.COMMEND_SN += 1;
        } else {
            ConstantCache.COMMEND_SN = 1;
        }

        return ConstantCache.COMMEND_SN;
    }


    public static void immSet(Activity ac) {
        InputMethodManager imm = (InputMethodManager) ac.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(ac.getWindow().getDecorView().getWindowToken(),
                    0);
        }
    }

    /**
     * Android 5.0 above and set the color status bar
     *
     * @param activity
     * @param color
     */
    public static void setLopStatBar(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);
        } else {
            initSystemBar(activity);
        }
    }

    /**
     * The status bar translucent above 4.4 is effective
     *
     * @param activity
     */
    public static void initSystemBar(Activity activity) {

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(activity, true);
//		}

        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(Color.TRANSPARENT);

    }

    /**
     * Check whether there is a virtual buttons toolbar
     *
     * @param context
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * To determine whether a virtual buttons bar rewritten
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    public static String getMsgTimeStr(Long createTime) {

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(phoneUtils.getCurrentTimeZone()));
        clearCalendar(c, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
        long yesMillis = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_MONTH, 1);
        long tomMillis = c.getTimeInMillis() - 1;
        c.setTimeInMillis(createTime);

        String timeStr = "";
        if (yesMillis <= createTime && createTime < tomMillis) {
            Date time = new Date(createTime);
            SimpleDateFormat parisTimeFormat = new SimpleDateFormat("HH:mm");
            parisTimeFormat.setTimeZone(TimeZone.getDefault());
            return parisTimeFormat.format(time);
        } else {
            timeStr = weekDays[c.get(Calendar.DAY_OF_WEEK) - 1];
        }
        return timeStr;
    }

    private static void clearCalendar(Calendar c, int... fields) {
        for (int f : fields) {
            c.set(f, 0);
        }
    }

    //获取虚拟按键的高度
    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 是否链接wifi
     * @param context
     * @return
     */
    public static boolean isConnectWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public static boolean isHasLocationPermission(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
}
