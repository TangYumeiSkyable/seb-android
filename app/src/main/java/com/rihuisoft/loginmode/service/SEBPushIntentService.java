package com.rihuisoft.loginmode.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.utils.ToastUtil;
import com.supor.suporairclear.application.MainApplication;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Foking 2018/3/17
 */
public class SEBPushIntentService extends UmengMessageService {
    private static final String TAG = SEBPushIntentService.class.getName();
    private static int NOTIFICATION_FLAG = 1;
    private static int CAN_FLAG = 0;
    private static int GETPENDING_ID = 0;

    @Override
    public void onMessage(Context context, Intent intent) {


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Logger.e(message);
        UMessage msg = null;
        try {
            msg = new UMessage(new JSONObject(message));
        } catch (JSONException e) {
            Logger.e(e.getMessage());
        }

        UTrack.getInstance(context).trackMsgClick(msg);
        Log.d(TAG, "message=" + message); // The message body
        Log.d(TAG, "custom=" + msg.custom); //The contents of a custom message
        Log.d(TAG, "title=" + msg.title); // Notice the title
        Log.d(TAG, "text=" + msg.text); // Notice the title

        Intent intentClick = new Intent(this, NotificationBroadcastReceiver.class);
        intentClick.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentClick.setAction("notification_clicked");
        intentClick.putExtra(NotificationBroadcastReceiver.TYPE, NOTIFICATION_FLAG);
        intentClick.putExtra(AgooConstants.MESSAGE_BODY, message);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, GETPENDING_ID, intentClick, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentCancel = new Intent(this, NotificationBroadcastReceiver.class);
        intentCancel.setAction("notification_cancelled");
        intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, CAN_FLAG);
        intentCancel.putExtra(AgooConstants.MESSAGE_BODY, message);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, GETPENDING_ID++, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
        Resources res = context.getResources();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ico_logo_48)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ico_logo_48))
                .setContentTitle(msg.title)
                .setContentText(msg.text)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntentClick)
                .setDeleteIntent(pendingIntentCancel);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_FLAG++ /* ID of notification */, notificationBuilder.build());

        boolean isClickOrDismissed = true;
        if (isClickOrDismissed) {
            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
        } else {
            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
        }
    }
}