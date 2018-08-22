package com.rihuisoft.loginmode.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACFileInfo;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.accloud.utils.PreferencesUtils;
import com.crashlytics.android.Crashlytics;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.CircleTransform;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.rihuisoft.loginmode.view.BadgeView;
import com.rihuisoft.loginmode.view.CustomProgressDialog;
import com.squareup.picasso.Picasso;
import com.supor.suporairclear.activity.CountryListActivity;
import com.supor.suporairclear.activity.DeviceListActivity;
import com.supor.suporairclear.activity.FilterCheckActivity;
import com.supor.suporairclear.activity.IFUActivity;
import com.supor.suporairclear.activity.MessageListActivity;
import com.supor.suporairclear.activity.ServiceAdrActivity;
import com.supor.suporairclear.activity.TimerSetActivity;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.Constants;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.fabric.sdk.android.Fabric;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;


/**
 * more page Activity
 */
@SuppressLint("NewApi")
public class MoreActivity extends FragmentActivity implements View.OnClickListener {

    // UI references.
    private TextView tv_readnum2, tv_notread, tv_divider, tv_readnum, tv_rightphe, tv_myapp, tv_mymessages, tv_username;
    private View vline, vline2, rl_mydevice;
    private AutoRelativeLayout aurl_, rl_mymessages;
    private TextView[] tvs;
    private CustomProgressDialog p = null;
    private ImageView iv_userimg, messageico, appico, iv_back, messageicos;
    private RelativeLayout rl_serviceAdr, rl_filterstate, rl_airrefer, add_home_ll, rl_preferences;
    private BadgeView badgeView_timer, badgeView_msg;

    // AC Manager
    private ACAccountMgr accountMgr = AC.accountMgr();
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private long deviceId;
    private RelativeLayout mArContactUs;

    @SuppressLint("NewApi")
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            Fabric.with(this, new Crashlytics());
            super.onCreate(savedInstanceState);
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_more);
            AppManager.getAppManager().addActivity(this);
            AppUtils.setLopStatBar(this, Color.TRANSPARENT);
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int REQUEST_CALL = 0;
    private boolean callPermission = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPermission = true;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + tvs[0].getText().toString().replace("-", "")));
                startActivity(intent);
            } else {
                callPermission = false;
            }
        }
    }

    /**
     * page initialization
     */
    public void initView() {

        tv_readnum2 = (TextView) findViewById(R.id.tv_readnum2);
        tv_notread = (TextView) findViewById(R.id.tv_notread);
        tv_divider = (TextView) findViewById(R.id.tv_divider);
        tv_readnum = (TextView) findViewById(R.id.tv_readnum);
        tv_rightphe = (TextView) findViewById(R.id.tv_rightphe);
        mArContactUs = (RelativeLayout) findViewById(R.id.ar_contact_us);

        tv_mymessages = (TextView) findViewById(R.id.tv_mymessage);
        rl_mymessages = (AutoRelativeLayout) findViewById(R.id.rl_mymessage);
        messageicos = (ImageView) findViewById(R.id.messageico);
        vline = findViewById(R.id.vline);
        vline2 = findViewById(R.id.vline2);
        appico = (ImageView) findViewById(R.id.appico);
        tv_myapp = (TextView) findViewById(R.id.tv_myapp);
        aurl_ = (AutoRelativeLayout) findViewById(R.id.aurl_);

        tvs = new TextView[]{
                (TextView) findViewById(R.id.tv_tel), (TextView) findViewById(R.id.tv_username),
                (TextView) findViewById(R.id.tv_myapp), (TextView) findViewById(R.id.tv_mymessage),
                (TextView) findViewById(R.id.tv_mydevice), (TextView) findViewById(R.id.tv_devicenum),
                (TextView) findViewById(R.id.tv_mydevice), (TextView) findViewById(R.id.tv_filterstate),
                (TextView) findViewById(R.id.tv_airquaref), (TextView) findViewById(R.id.tv_phone),
                (TextView) findViewById(R.id.tv_network), (TextView) findViewById(R.id.tv_notread),
                (TextView) findViewById(R.id.tv_divider), (TextView) findViewById(R.id.tv_readnum),
                (TextView) findViewById(R.id.tv_rightphe), (TextView) findViewById(R.id.tv_filter)};

        rl_mydevice = (View) findViewById(R.id.rl_mydevice);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_userimg = (ImageView) findViewById(R.id.iv_userimg);


        rl_serviceAdr = (RelativeLayout) findViewById(R.id.rl_serviceAdr);
        rl_filterstate = (RelativeLayout) findViewById(R.id.rl_filterstate);
        rl_airrefer = (RelativeLayout) findViewById(R.id.rl_airrefer);
        add_home_ll = (RelativeLayout) findViewById(R.id.add_home_ll);
        rl_preferences = (RelativeLayout) findViewById(R.id.rl_preferences);

        messageico = (ImageView) findViewById(R.id.messageico);
        appico = (ImageView) findViewById(R.id.appico);
        badgeView_msg = new BadgeView(this, messageico);

        badgeView_timer = new BadgeView(this, appico);

        setListener();

        tv_username = (TextView) findViewById(R.id.tv_username);
        if (PreferencesUtils.getString(MoreActivity.this, "name", "") != null) {
            tv_username.setText(PreferencesUtils.getString(MoreActivity.this, "name", ""));
        } else {
            tv_username.setText(getString(R.string.airpurifier_more_show_newuser_text));
            PreferencesUtils.putString(MoreActivity.this, "name", getString(R.string.airpurifier_more_show_newuser_text));
        }
        if (ConstantCache.deviceList != null) {
            if (ConstantCache.deviceList.size() > 1) {
                tvs[5].setText(ConstantCache.deviceList.size() + " " + getString(R.string.airpurifier_more_devices));
            } else {
                tvs[5].setText(ConstantCache.deviceList.size() + " " + getString(R.string.airpurifier_more_show_gongdevice_text));
            }
        }
        NetWorkGetHeadIcomUrl(iv_userimg, MainApplication.mUser.getUserId());
    }

    /**
     * Listener initialization
     */
    private void setListener() {
        rl_mydevice.setOnClickListener(this);
        iv_userimg.setOnClickListener(this);
        rl_serviceAdr.setOnClickListener(this);
        rl_filterstate.setOnClickListener(this);
        rl_airrefer.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        rl_preferences.setOnClickListener(this);
        add_home_ll.setOnClickListener(this);
        mArContactUs.setOnClickListener(this);

        aurl_.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    //touch down
                    case MotionEvent.ACTION_DOWN: {
                        appico.setImageResource(R.drawable.ico_clock_sel);
                        vline.setVisibility(View.VISIBLE);
                        tv_myapp.setTextColor(getResources().getColor(R.color.default_blue));
                    }
                    break;
                    //touch up
                    case MotionEvent.ACTION_UP: {
                        appico.setImageResource(R.drawable.ico_clock_nor);
                        vline.setVisibility(View.GONE);
                        tv_myapp.setTextColor(getResources().getColor(R.color.more_setting_text));
                        if (ConstantCache.deviceList != null && ConstantCache.deviceList.size() == 0) {
                            Toast.makeText(MoreActivity.this, R.string.airpurifier_login_show_tosnodevice_text,
                                    AppConstant.TOAST_DURATION).show();
                            break;
                        }
                        try {
                            Intent intent = new Intent(MoreActivity.this, TimerSetActivity.class);
                            intent.putExtra("url", "file:///android_asset/appointment-list.html");
                            intent.putExtra("page", 1);
                            intent.putExtra("deviceId", -1l);
                            intent.putExtra("src", 0);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    //touch move
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return true;
            }
        });
        rl_mymessages.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //touch down
                    case MotionEvent.ACTION_DOWN:
                        tv_readnum2.setTextColor(getResources().getColor(R.color.default_blue));
                        tv_notread.setTextColor(getResources().getColor(R.color.default_blue));
                        tv_divider.setTextColor(getResources().getColor(R.color.default_blue));
                        tv_readnum.setTextColor(getResources().getColor(R.color.default_blue));
                        tv_rightphe.setTextColor(getResources().getColor(R.color.default_blue));
                        messageicos.setImageResource(R.drawable.ico_message_sel);
                        vline2.setVisibility(View.VISIBLE);
                        tv_mymessages.setTextColor(getResources().getColor(R.color.default_blue));
                        break;
                    //touch up
                    case MotionEvent.ACTION_UP:
                        tv_readnum2.setTextColor(getResources().getColor(R.color.more_setting_text));
                        tv_notread.setTextColor(getResources().getColor(R.color.more_setting_text));
                        tv_divider.setTextColor(getResources().getColor(R.color.more_setting_text));
                        tv_readnum.setTextColor(getResources().getColor(R.color.more_setting_text));
                        tv_rightphe.setTextColor(getResources().getColor(R.color.more_setting_text));

                        messageicos.setImageResource(R.drawable.ico_message_nor);
                        vline2.setVisibility(View.GONE);
                        tv_mymessages.setTextColor(getResources().getColor(R.color.more_setting_text));

                        if ("0".equals(tvs[13].getText().toString())) {
                            Toast.makeText(MoreActivity.this, R.string.airpurifier_more_show_tosnomsg_text,
                                    AppConstant.TOAST_DURATION).show();
                            break;
                        }
                        Intent intent = new Intent(MoreActivity.this, MessageListActivity.class);
                        startActivity(intent);

                        break;
                    //touch move
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * view set
     */
    public void setView() {
        try {
            tv_username.setText(PreferencesUtils.getString(MoreActivity.this, "name", ""));
            if (ConstantCache.deviceList.size() > 1) {
                tvs[5].setText(ConstantCache.deviceList.size() + " " + getString(R.string.airpurifier_more_devices));
            } else {
                tvs[5].setText(ConstantCache.deviceList.size() + " " + getString(R.string.airpurifier_more_show_gongdevice_text));
            }
            NetWorkGetHeadIcomUrl(iv_userimg, MainApplication.mUser.getUserId());
            msgHelper.queryMoreInfo(ConstantCache.deviceList, new PayloadCallback<ACObject>() {
                @Override
                public void success(ACObject result) {
                    Log.d("queryMoreInfo", result.toString());
                    long sumAppointment = result.getLong("sumAppointment");
                    if (sumAppointment > 0) {
                        badgeView_timer.show();
                    }
                    // messages total
                    long sumMessage = result.getLong("sumMessage");
                    // unread
                    long readFlag0 = result.getLong("readFlag0");
                    if (readFlag0 > 0) {
                        badgeView_msg.show();
                    } else {
                        badgeView_msg.hide();
                    }
                    tvs[11].setText(String.valueOf(readFlag0));
                    tvs[13].setText(String.valueOf(sumMessage));
                    // filter 1：normal，0：replace
                    long filterStatus = result.getLong("filterStatus");
                    if (filterStatus == 0) {
                        tvs[15].setText(R.string.airpurifier_more_show_needchange_text);
                    } else {
                        tvs[15].setText("");
                    }
                    if (ConstantCache.deviceList.size() == 0) {
                        tvs[15].setText("");
                    }
                }

                @Override
                public void error(ACException e) {
                    if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1 || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                            || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                            || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {
                        AppUtils.reLogin(MoreActivity.this);
                    }
                    Log.e("queryMoreInfo", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();
    }

    /*
     * headerUrl download
     */
    protected void NetWorkGetHeadIcomUrl(final ImageView img, final Long userid) {
        try {
            if (userid != null) {
                //ACFile
                ACFileInfo fileInfo = new ACFileInfo(getResources().getString(R.string.airpurifier_application_show_appname_text) + "_img", "header_" + userid + ".jpg");
                AC.fileMgr().getDownloadUrl(fileInfo, 0, new PayloadCallback<String>() {
                    @Override
                    public void success(String arg0) {
                        if (arg0 != null && !"".equals(arg0)) {
                            Picasso.with(MoreActivity.this).load(arg0).noFade().transform(new CircleTransform()).into(img);
                        } else {
                            img.setImageResource(R.drawable.img_big_avator);
                        }
//                        handlePicCache(arg0);
                    }

                    @Override
                    public void error(ACException arg0) {
                        img.setImageResource(R.drawable.img_big_avator);
                    }
                });
            } else {
                img.setImageResource(R.drawable.img_big_avator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String handlePicCache(final String netPicUrl) {
        Disposable get = Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(CompletableEmitter e) throws Exception {

                        SSLContext ctx = SSLContext.getInstance("TLS");
                        ctx.init(new KeyManager[0],
                                new TrustManager[]{new DefaultTrustManager()},
                                new SecureRandom());
                        SSLContext.setDefault(ctx);


                        //本地图片存放的位置
                        File rootDirectory = new File(Environment.getExternalStorageDirectory() + Constants.IMAGE_SAVE_PATH);
                        File file = new File(rootDirectory.getPath(), Constants.HEAD_IMAGE);

                        if (file.exists()) file.delete();
                        URL url = new URL(netPicUrl);
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(3 * 1000);
                        urlConnection.setReadTimeout(4 * 1000);
                        urlConnection.setRequestMethod("GET");

                        urlConnection.connect();
                        if (urlConnection.getResponseCode() == 200) {
                            int len;

                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            InputStream inputStream = urlConnection.getInputStream();

                            byte[] bytes = new byte[1024];
                            while (true) {
                                len = inputStream.read(bytes);
                                if (len == -1) break;
                                fileOutputStream.write(len);
                            }

                            inputStream.close();
                            fileOutputStream.close();

                            e.onComplete();
                        }


                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
        return "";
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

    /*
     * onClick Listener
     */
    @Override
    public void onClick(View view) {
        try {
            Intent intent = null;
            switch (view.getId()) {
                // my devices
                case R.id.rl_mydevice:
                    if (ConstantCache.deviceList.size() == 0) {
                        Toast.makeText(MoreActivity.this, R.string.airpurifier_login_show_tosnodevice_text,
                                AppConstant.TOAST_DURATION).show();
                        break;
                    }
                    intent = new Intent(MoreActivity.this, DeviceListActivity.class);
                    startActivity(intent);
                    break;
                //serviceAdr
                case R.id.rl_serviceAdr:
                    intent = new Intent(MoreActivity.this, ServiceAdrActivity.class);
                    startActivity(intent);
                    break;
                //userInfo
                case R.id.iv_userimg:
                    intent = new Intent(MoreActivity.this, PersonalInfoActivity.class);
//				intent.putExtra("flag", AppConstant.REGISTER);
                    startActivity(intent);
                    break;
                //preferences
                case R.id.rl_preferences:
                    intent = new Intent(MoreActivity.this, PreferenceActivity.class);
                    startActivity(intent);
                    break;
                //my filters
                case R.id.rl_filterstate:
                    if (ConstantCache.deviceList.size() == 0) {
                        Toast.makeText(MoreActivity.this, R.string.airpurifier_login_show_tosnodevice_text, AppConstant.TOAST_DURATION).show();
                        break;
                    }
                    intent = new Intent(MoreActivity.this, FilterCheckActivity.class);
                    intent.putExtra("flag", 0);
                    startActivity(intent);
                    break;
                // IFU
                case R.id.rl_airrefer:
                    try {
                        intent = new Intent(MoreActivity.this, UserInfomationActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.ar_contact_us:
                    intent = new Intent(MoreActivity.this, ContactUsActivity.class);
                    startActivity(intent);
                    break;
                // back
                case R.id.iv_back:
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fixInputMethodManagerLeak(this);
        AppManager.getAppManager().finishActivity(this);
        Logger.i(this.getClass().getSimpleName() + ":onDestroy");
    }

    public void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}