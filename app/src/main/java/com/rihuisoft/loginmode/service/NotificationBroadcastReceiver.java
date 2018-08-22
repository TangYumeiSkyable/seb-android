package com.rihuisoft.loginmode.service;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.AppStartActivity;
import com.rihuisoft.loginmode.activity.LoginActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.view.UmengMessageAlertDialog;
import com.supor.suporairclear.activity.FilterCheckActivity;
import com.supor.suporairclear.activity.MainActivity;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.CommendInfo;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

import java.util.List;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String TYPE = "type"; //This type is to update Notification information, the do not understand friend can go to search, a lot
    private ACMsgHelper msgHelper = new ACMsgHelper();
    ACAccountMgr accountMgr = AC.accountMgr();
    private Context mContext;


    public boolean isRunningForeground() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(mContext.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);

        if (action.equals("notification_clicked")) {
            Log.i("Notification", "click");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
            try {
                if (!isRunningForeground()) {
                    Intent intent_ = new Intent(context,
                            LoginActivity.class);
                    intent_.putExtra("flag", 1);
                    intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(intent_);
                }
                String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
                UMessage msg = new UMessage(new JSONObject(message));
                postDeal(context, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void postDeal(final Context context, final UMessage msg) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (ConstantCache.deviceNameMap != null) {
                    Log.d("dealMessage",
                            "ConstantCache.deviceNameMap != null");
                    dealMessage(context, msg);
                } else {
                    Log.d("postDeal(context, msg);",
                            "ConstantCache.deviceNameMap == null");
                    postDeal(context, msg);
                }
            }
        }, 500);
    }

    private void dealMessage(final Context context, UMessage msg) {
        final String deviceIdStr = msg.extra.get("deviceId").toString();

        if (msg.extra.get("notifyType") == null || msg.extra.get("notifyType").contains("0")) {
            final long deviceId = Long.valueOf(msg.extra.get("deviceId"));
            String filterName = msg.extra.get("filterName");

            new UmengMessageAlertDialog(context)
                    .builder()
                    .setFilterChange(ConstantCache.deviceNameMap.get(deviceId),
                            msg.extra.get("cost"), msg.extra.get("filterName"))
                    .setPositiveButton(context.getString(R.string.to_change), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context,
                                    FilterCheckActivity.class);
                            intent.putExtra("flag", 2);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            context.startActivity(intent);
                        }
                    }).setNegativeButton(context.getString(R.string.no_more_warn), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUserInfo("notifyFlg3", false);
                }
            }).show();
        } else if (msg.extra.get("notifyType").contains("1")) {
            final String[] ids = deviceIdStr.split(",");
            final long deviceId = Long.valueOf(ids[0]);

            new UmengMessageAlertDialog(context)
                    .builder()
                    .setDeviceStart(msg.extra.get("PM25"),
                            msg.extra.get("hcho"))
                    .setPositiveButton(context.getString(R.string.immediately_turn_on), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startAirClear(context, deviceId);
                        }
                    }).setNegativeButton(context.getString(R.string.no_more_warn), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUserInfo("notifyFlg1", false);
                }
            }).show();
        } else if (msg.extra.get("notifyType").contains("2")) {
            final String[] ids = deviceIdStr.split(",");
            String name = "";
            for (int i = 0; i < ids.length; i++) {
                if (ConstantCache.deviceNameMap.get(Long.valueOf(ids[i])) != null) {
                    name += ConstantCache.deviceNameMap.get(Long
                            .valueOf(ids[i]));
                }
                if (i != ids.length - 1) {
                    name += ",";
                }
            }
            new UmengMessageAlertDialog(context)
                    .builder()
                    .setWorktime(msg.extra.get("time"), name,
                            msg.extra.get("type"), new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                }
                            })
                    .setPositiveButton(context.getString(R.string.immediately_turn_on), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < ids.length; i++) {
                                long deviceId = Long.valueOf(ids[i]);
                                startAirClear(context, deviceId);
                            }

                        }
                    }).setNegativeButton(context.getString(R.string.no_more_warn), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUserInfo("notifyFlg2", false);
                }
            }).show();
        }
    }

    private void startAirClear(final Context context, long deviceId) {
        CommendInfo commend = new CommendInfo();
        commend.setDeviceId(deviceId);
        commend.setCommend("on_off");
        commend.setValue(1);
        msgHelper.controlDevice(commend, new VoidCallback() {
            @Override
            public void success() {

                // handler.sendEmptyMessage(handler_key.GETDEVICEINFO_SUCCESS.ordinal());
                Toast.makeText(context, context.getString(R.string.airpurifier_more_show_opensupotrsuccess_text), AppConstant.TOAST_DURATION).show();
            }

            @Override
            public void error(ACException e) {
                if (e.getErrorCode() == 3807) {
                    Toast.makeText(
                            context,
                            context.getString(R.string.airpurifier_moredevice_show_devicenotonline_text),
                            AppConstant.TOAST_DURATION).show();
                } else {
                    Toast.makeText(
                            context,
                            context.getString(R.string.airpurifier_moredevice_show_controlfailed_text),
                            AppConstant.TOAST_DURATION).show();
                }
                Log.e("controlDevice",
                        "error:" + e.getErrorCode() + "-->" + e.getMessage());

            }
        });

    }

    /**
     * Set user extended attributes
     */
    public void setUserInfo(String key, Boolean value) {
        ACObject userProfile = new ACObject();
        userProfile.put(key, value);
        accountMgr.setUserProfile(userProfile, new VoidCallback() {
            @Override
            public void success() {
                Log.i("setUserInfo", "Extended attributes set successfully");
            }

            @Override
            public void error(ACException e) {
                // Network error, or other, according to the um participant etErrorCode () do different tip or processing
                Log.e("setUserProfile",
                        "error:" + e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }
}