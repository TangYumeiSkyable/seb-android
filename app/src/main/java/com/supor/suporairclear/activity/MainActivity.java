package com.supor.suporairclear.activity;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACOTACheckInfo;
import com.accloud.service.ACOTAMgr;
import com.accloud.service.ACOTAUpgradeInfo;
import com.accloud.service.ACObject;
import com.accloud.service.ACProduct;
import com.accloud.service.ACProductMgr;
import com.accloud.service.ACUserDevice;
import com.accloud.utils.PreferencesUtils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.activity.CookiesActivity;
import com.rihuisoft.loginmode.activity.LoginActivity;
import com.rihuisoft.loginmode.activity.MoreActivity;
import com.rihuisoft.loginmode.activity.SettingActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.rihuisoft.loginmode.view.AppraiseAlertDialog;
import com.rihuisoft.loginmode.view.LoadingDialog;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.common.adapter.HomeAdapter;
import com.supor.suporairclear.common.adapter.ViewParser;
import com.supor.suporairclear.common.fragment.HomeFragment;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.DeviceInfo;
import com.supor.suporairclear.model.LocalPm25Info;
import com.supor.suporairclear.model.TimeCount;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.zhy.autolayout.utils.L;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.supor.suporairclear.config.AppConstant.DCPSERVICE_TOKEN_INVALID_CODE;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentAppliances;


@TargetApi(Build.VERSION_CODES.M)
public class MainActivity extends FragmentActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager mPager;
    private View no_device_page;
    private LinearLayout ll_city;
    private TextView nowCity;
    private ImageView header_pm25_level;
    private TextView tv_airQuality;
    private ImageView iv_more_flg;
    private Button btn_addDevice;
    private ArrayList<Fragment> fragmentList;
    private FragmentStatePagerAdapter mFragmentPagerAdapter;
    private HomeAdapter adapter;
    private List<View> list;
    private List<String> titlelist;
    private ACBindMgr bindMgr;
    private Dialog dialog;
    private AppraiseAlertDialog appraisedialog;

    private List<DeviceInfo> deviceList;
    public static long deviceId = -1;
    private LocalPm25Info localPm25;
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private ACAccountMgr accountMgr = AC.accountMgr();
    private ACProductMgr productMgr = AC.productMgr();
    private RelativeLayout rl_maintitle;
    private long lastTime;
    private ACOTAMgr otaMgr;
    private List<ACUserDevice> devices;
    private SimpleDateFormat sf;
    private boolean isShow = true;
    private boolean receiverFlag = false;
    private LocationBroadcastReceiver locationBroadcastReceiver;
    private LoadingDialog mLoadingDialog;
    private boolean isFirstOpenApp = true;//是否是第一次打开App,进入控制页就不展示了

    public static HomeFragment homeFragment;

    @Override
    public void onClick(View v) {
        try {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.ll_city:
                    intent = new Intent(this, CountryListActivity.class);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.anim_bottom_in,
                            R.anim.no_anim);
                    break;
                case R.id.rl_maintitle:
                    intent = new Intent(MainActivity.this, OutSideActivity.class);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.anim_right_in,
                            R.anim.no_anim);
                    break;
                case R.id.btn_addDevice:
                    intent = new Intent(this, APAddDeviceActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iv_more_flg:
                    intent = new Intent(this, MoreActivity.class);
                    startActivity(intent);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private enum handler_key {
        GETALLDEVICE_SUCCESS,
        GETCITYPM25_SUCCESS,
        GETDEVICEMODELLIST_SUCCESS,
        GETDEVICEMODELLIST_FAIL,
        GETUSERINFO_SUCCESS,
        USER_OVERDUE,
        TOKEN_INVALID
    }

    /**
     * The handler.
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            try {
                handler_key key = handler_key.values()[msg.what];
                switch (key) {
                    case GETALLDEVICE_SUCCESS:
                        try {
                            AppUtils.setDeviceList(deviceList);
                            getUserInfo();
                            if (deviceList != null && deviceList.size() > 0) {

                                if (deviceList.size() > 1 && MainApplication.first_flg) {
                                    dialog.show();
                                    PreferencesUtils.putString(MainApplication.getInstance(), "first_flg", "true");
                                    MainApplication.first_flg = false;
                                }
                                if (MainApplication.first_device_flg) {
                                    new AlertDialogUserDifine(MainActivity.this).builder()
                                            .setTitle(getString(R.string.airpurifier_more_show_notifications_text)).setMsg(getString(R.string.airpurifier_more_show_cookiesmsg_text))
                                            .setPositiveButton(getString(R.string.airpurifier_more_show_settings_text), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                                                    startActivity(intent);
                                                }
                                            }).setNegativeButton(getString(R.string.airpurifier_more_show_nothank_text), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).show();
                                    PreferencesUtils.putString(MainApplication.getInstance(), "first_device_flg", "true");
                                    MainApplication.first_device_flg = false;
                                }
                                mPager.removeAllViewsInLayout();//removeAllViews();//Assignment before you will be in the Adapter
                                mFragmentPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), deviceList);
                                //mFragmentPagerAdapter.notifydatasetchanged();
                                if(isFirstOpenApp){
                                    int ticknum = 0 ;//"ticknum" is the calculator of users' app login times
                                    ticknum = PreferencesUtils.getInt(getApplicationContext(), "ticknum", 0);//variable storage local method
                                    ticknum++;
                                    PreferencesUtils.putInt(MainApplication.getInstance(), "ticknum", ticknum);
                                    if(ticknum==5||ticknum==10){// rating popup appears when the user login at the fifth and the tenth time
                                        appraisedialog.show();
                                    }
//                                    Toast.makeText(MainActivity.this,"ticknum : "+ticknum, Toast.LENGTH_SHORT).show();
                                }
                                isFirstOpenApp = false;
                                mPager.setAdapter(mFragmentPagerAdapter);

                                if (deviceId > 0) {
                                    for (int i = 0; i < deviceList.size(); i++) {
                                        if (deviceList.get(i).getDeviceId() == deviceId) {
                                            mPager.setCurrentItem(i);
                                        }
                                    }
                                }

                                mPager.setVisibility(View.VISIBLE);
                                no_device_page.setVisibility(View.GONE);
                            } else {
                                mPager.setVisibility(View.GONE);
                                no_device_page.setVisibility(View.VISIBLE);

                            }

                            getWeather();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case GETCITYPM25_SUCCESS:
                        setOutdoor();
                        break;
                    case GETDEVICEMODELLIST_SUCCESS:
                        getProductInfo();
                        break;
                    case GETDEVICEMODELLIST_FAIL:

                        break;
                    case USER_OVERDUE:
                        AppUtils.reLogin(MainActivity.this);
                        break;
                    case GETUSERINFO_SUCCESS:
                        ota();
                        break;
                    case TOKEN_INVALID:
                        accountMgr.logout();
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        ConstantCache.appManager.finishAllActivity();
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    private void appraise_bad_diolog(){//popup for general or bad
        new AlertDialogUserDifine(MainActivity.this).builder()
                .setTitle(getString(R.string.airpurifier_appraisetwo_title_android)).setMsg(getString(R.string.airpurifier_appraisetwo_msg_android))
                .setPositiveButton(getString(R.string.airpurifier_appraisetwo_btn_yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent data=new Intent(Intent.ACTION_SENDTO);//method of mailbox transfer
                        data.setData(Uri.parse("mailto:applications.seb@groupeseb.com"));
                        data.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.airpurifier_appraisethree_email_title));
                        data.putExtra(Intent.EXTRA_TEXT, getString(R.string.airpurifier_appraisethree_email_msg));
                        startActivity(data);
                    }
                }).setNegativeButton(getString(R.string.airpurifier_appraisetwo_btn_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }
    private void appraise_happy_diolog(){//popup for good
        new AlertDialogUserDifine(MainActivity.this).builder()
                .setTitle(getString(R.string.airpurifier_appraisethree_title_android)).setMsg(getString(R.string.airpurifier_appraisethree_msg_android))
                .setPositiveButton(getString(R.string.airpurifier_appraisethree_btn_yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);//Google play transfer
                        intent.setData(Uri.parse("market://details?id=" + "com.groupeseb.airpurifier"));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + "com.groupeseb.airpurifier"));
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this,"no",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }).setNegativeButton(getString(R.string.airpurifier_appraisetwo_btn_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }
    private void getProductInfo() {
        productMgr.fetchAllProducts(new PayloadCallback<List<ACProduct>>() {
            @Override
            public void success(List<ACProduct> products) {

                Iterator<ACObject> iterator = ConstantCache.deviceModeList.iterator();
                while (iterator.hasNext()) {
                    ACObject item = iterator.next();
                    Boolean mode = true;
                    for (ACProduct product : products) {
                        if (item.get("id").equals(product.model)) {
                            item.put("subDomainId", product.sub_domain);
                            item.put("subDomainName", product.sub_domain_name);
                            mode = false;
                            break;
                        }
                    }
                    if (mode) {
                        iterator.remove();
                    }
                }
            }

            @Override
            public void error(ACException e) {
            }
        });
    }

    @Override
    public void onPause() {
        if (receiverFlag) {
            receiverFlag = false;
            unregisterReceiver(locationBroadcastReceiver);
        }
        super.onPause();
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        try {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            AppUtils.setLopStatBar(this, Color.TRANSPARENT);
            Logger.i(System.currentTimeMillis() + "");
            setContentView(R.layout.activity_supor_home);
            registerReceiver();
            otaMgr = AC.otaMgr();
            AppManager.getAppManager().addActivity(this);
            mLoadingDialog = new LoadingDialog(this);

            //注册别名
            PushAgent mPushAgent = PushAgent.getInstance(this);
            long userId = PreferencesUtils.getLong(MainApplication.getInstance(), "uid");
            mPushAgent.addAlias(userId + "", "ablecloud", new UTrack.ICallBack() {
                @Override
                public void onMessage(boolean isSuccess, String message) {
                    Logger.i("设置别名：" + isSuccess);
                }

            });

            getDeviceModeList();
            setAppLanguage();
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(NetworkReceiver, intentFilter);
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        receiverFlag = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.LOCATION_ACTION);
        locationBroadcastReceiver = new LocationBroadcastReceiver();
        this.registerReceiver(locationBroadcastReceiver, filter);
    }

    private BroadcastReceiver NetworkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!NetworkAvailable()) {
                if (isShow == true) {
                    if (isRunningForeground())
                        Toast.makeText(MainActivity.this, getString(R.string.airpurifier_login_show_isnetwork_text), Toast.LENGTH_SHORT).show();
                    isShow = false;
                }
                if(homeFragment != null){
                    homeFragment.netWorkChange2RefreshUI(false);
                }
            } else {
                isShow = true;
                if(homeFragment != null){
                    homeFragment.netWorkChange2RefreshUI(true);
                }
            }
        }

    };

    public boolean isRunningForeground() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Whether network connection
     */
    private boolean NetworkAvailable() {
        try {
            Thread.sleep(600);
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo != null || networkInfo.isAvailable()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Controls the initialization
     */
    private void initView() {

        bindMgr = AC.bindMgr();
        mPager = (ViewPager) findViewById(R.id.viewpager);
        no_device_page = (View) findViewById(R.id.nodevice_page);

        nowCity = (TextView) findViewById(R.id.nowCity);
        tv_airQuality = (TextView) findViewById(R.id.tv_airQuality);
        header_pm25_level = (ImageView) findViewById(R.id.pm25);
        iv_more_flg = (ImageView) findViewById(R.id.iv_more_flg);
        btn_addDevice = (Button) findViewById(R.id.btn_addDevice);
        ll_city = (LinearLayout) findViewById(R.id.ll_city);
        ll_city.setOnClickListener(this);
        btn_addDevice.setOnClickListener(this);
        iv_more_flg.setOnClickListener(this);

        rl_maintitle = (RelativeLayout) no_device_page.findViewById(R.id.rl_maintitle);
        rl_maintitle.setOnClickListener(this);

        dialog = new Dialog(this, R.style.Dialog_Fullscreen);
        dialog.setContentView(R.layout.guide_dialog);
        Button iv = (Button) dialog.findViewById(R.id.btn_ikonw);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        appraisedialog = new AppraiseAlertDialog(this);
        appraisedialog.builder().setTitle(getString(R.string.airpurifier_appraise_title_android)).setMsg(getString(R.string.airpurifier_appraise_msg_android))
                .setBadButton("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appraise_bad_diolog();
                    }
                }).setBofButton("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appraise_bad_diolog();
                    }
                }).setHappyButton("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appraise_happy_diolog();
                    }
                });
    }
    private ACObject weatherInfo;


    private void setAppLanguage() {
        //给UDS设置当前语言
        Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter emitter) throws Exception {
                        ACObject acObject = new ACObject();
                        acObject.put("systemLanguage", DCPServiceUtils.getLanguage());
                        AC.accountMgr().setUserProfile(acObject, new VoidCallback() {
                            @Override
                            public void success() {
                                emitter.onComplete();
                            }

                            @Override
                            public void error(ACException e) {
                                emitter.onError(e);
                            }
                        });
                    }
                })
                .observeOn(Schedulers.newThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void getWeather() {
        msgHelper.queryWeather(MainApplication.location_city, MainApplication.latitude, MainApplication.longitude,
                new PayloadCallback<ACObject>() {
                    @Override
                    public void success(ACObject acObject) {
                        weatherInfo = acObject;
                        ConstantCache.weatherInfo = acObject;
                        handler.sendEmptyMessage(handler_key.GETCITYPM25_SUCCESS
                                .ordinal());
                    }

                    @Override
                    public void error(ACException e) {
                        if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {

                            handler.sendEmptyMessage(handler_key.USER_OVERDUE
                                    .ordinal());
                        }
                        Log.e("LocalPM25", "error:" + e.getErrorCode() + "-->"
                                + e.getMessage());
                    }
                });
    }

    private void setOutdoor() {
        weatherInfo = ConstantCache.weatherInfo;
        if (weatherInfo == null) {
            return;
        }
        List<Integer> list = new ArrayList<>();
        list.add(weatherInfo.getInt("PM"));
        list.add(weatherInfo.getInt("NO2"));
        list.add(weatherInfo.getInt("O3"));
        FragmentManager childFragmentManager = getSupportFragmentManager();
        ViewParser.scrollRightVp(childFragmentManager, rl_maintitle, R.id.picslooper2, R.id.pageIndexor2, list);
        int level = -1;
        //获取不到城市，图片不显示，文字显示--
        if (weatherInfo.getInt("overall") == 0) {
            return;
        }
        if (weatherInfo.getInt("overall") < AppConstant.AIR_1) {
            header_pm25_level.setImageResource(R.drawable.ico_air_01s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_fresh));
            level = 1;
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_2) {
            header_pm25_level.setImageResource(R.drawable.ico_air_02s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_moderate));
            level = 2;
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_3) {
            header_pm25_level.setImageResource(R.drawable.ico_air_03s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_high));
            level = 3;
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_4) {
            header_pm25_level.setImageResource(R.drawable.ico_air_04s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_very));
            level = 4;
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_5) {
            header_pm25_level.setImageResource(R.drawable.ico_air_05s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_excessive));
            level = 5;
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_6) {
            header_pm25_level.setImageResource(R.drawable.ico_air_06s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_extreme));
            level = 6;
        } else {
            header_pm25_level.setImageResource(R.drawable.ico_air_07s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_airpocalypse));
            level = 7;
        }
        judgeNotice(level);
    }

    private void judgeNotice(final int level) {
        if (!outdoor_avail) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    judgeNotice(level);
                }
            }, 2000);
        } else if (outdoor_notice_flg) {
            if (level != 2) {
                sf = new SimpleDateFormat("HH:mm");
                String now = sf.format(System.currentTimeMillis());
                if ("07:00".compareTo(now) <= 0 && "21:00".compareTo(now) > 0) {
                    if (MainApplication.tipNumber == 1 && MainApplication.tipNumber == MainApplication.quitNumber) {
                        if (level != MainApplication.outdoorVal) {
                            outdoorNotice(level);
                        }
                    } else if (MainApplication.tipNumber == 0) {
                        outdoorNotice(level);
                    }
                }
            }
        }
    }

    private void outdoorNotice(int level) {
        int random = ((int) System.currentTimeMillis() / 1000) % 2;
        String msg;
        if (random == 0) {
            if (level == 1) {
                msg = getString(R.string.airpurifier_outdoor_airquality_notification_1_a);
            } else if (level == 3) {
                msg = getString(R.string.airpurifier_outdoor_airquality_notification_3_a);
            } else if (level == 4) {
                msg = getString(R.string.airpurifier_outdoor_airquality_notification_4_a);
            } else {
                msg = getString(R.string.airpurifier_outdoor_airquality_notification_5_a);
            }
        } else {
            if (level == 1) {
                msg = getString(R.string.airpurifier_outdoor_airquality_notification_1_b);
            } else if (level == 3) {
                msg = getString(R.string.airpurifier_outdoor_airquality_notification_3_b);
            } else if (level == 4) {
                msg = getString(R.string.airpurifier_outdoor_airquality_notification_4_b);
            } else {
                msg = getString(R.string.airpurifier_outdoor_airquality_notification_5_b);
            }
        }
        if (!MainApplication.location_city.isEmpty())
            new AlertDialogUserDifine(MainActivity.this).builder()
                    .setMsg(msg)
                    .setPositiveButton(getString(R.string.airpurifier_moredevice_show_sure_button), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).setCancelable(false).show();
        MainApplication.tipNumber++;
        TimeCount tipObj = new TimeCount();
        tipObj.setTime(new SimpleDateFormat("yyyy/MM/dd").format(System.currentTimeMillis()));
        tipObj.setNumber(MainApplication.tipNumber);
        tipObj.setValue(level);
        MainApplication.outdoorVal = level;
        PreferencesUtils.putString(MainApplication.getInstance(), "tipNumber", new Gson().toJson(tipObj));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        unregisterReceiver(NetworkReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ConstantCache.deviceModeList != null && ConstantCache.deviceModeList.size() <= 0) {
            getDeviceModeList();
        }
        if (mFragmentPagerAdapter != null) {
            mFragmentPagerAdapter = null;
        }

        nowCity.setText(MainApplication.location_city);
        handler.sendEmptyMessage(handler_key.GETCITYPM25_SUCCESS
                .ordinal());
        getDeviceList();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (ConstantCache.deviceModeList != null && ConstantCache.deviceModeList.size() <= 0) {
            getDeviceModeList();
        }
        if (mFragmentPagerAdapter != null) {
            mFragmentPagerAdapter = null;
        }

        nowCity.setText(MainApplication.location_city);
        handler.sendEmptyMessage(handler_key.GETCITYPM25_SUCCESS
                .ordinal());
        getDeviceList();

        super.onRestoreInstanceState(savedInstanceState);
    }

    // Access to equipment list
    public void getDeviceList() {
        if (mLoadingDialog != null && isFirstOpenApp) {
            showDialog();
        }
        try {
            List<Integer> list = new ArrayList<>();
            list.add(0);
            list.add(0);
            list.add(0);
            FragmentManager childFragmentManager = getSupportFragmentManager();
            ViewParser.scrollRightVp(childFragmentManager, rl_maintitle, R.id.picslooper2, R.id.pageIndexor2, list);
            bindMgr.listDevicesWithStatus(new PayloadCallback<List<ACUserDevice>>() {
                @Override
                public void success(List<ACUserDevice> arg0) {
                    if (mLoadingDialog != null)
                        dimissDialog();
                    try {
                        devices = arg0;
                        deviceList = new ArrayList<DeviceInfo>();
                        for (ACUserDevice tmp : arg0) {
                            DeviceInfo device = new DeviceInfo();
                            device.setDeviceId(tmp.getDeviceId());
                            device.setDeviceName(tmp.getName());
                            device.setSubDomainId(tmp.getSubDomainId());
                            device.setSubDomainName(tmp.getSubDomain());
                            device.setStatus(tmp.getStatus());
                            device.setPhysicalDeviceId(tmp.getPhysicalDeviceId());
                            device.setOwner(tmp.getOwner());
                            deviceList.add(device);
                        }
                        handler.sendEmptyMessage(handler_key.GETALLDEVICE_SUCCESS.ordinal());
                    } catch (Exception e) {
                        Log.e("listDevicesWithStatus", "error:" + e.getMessage() + "-->" + e.toString());

                    }
                }

                @Override
                public void error(ACException e) {
                    if (mLoadingDialog != null)
                        dimissDialog();
                    Log.e("listDevicesWithStatus", "error:" + e.getMessage() + "-->" + e.toString());
                    handler.sendEmptyMessage(handler_key.GETALLDEVICE_SUCCESS.ordinal());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            getWeather();
        }
    }

    private boolean outdoor_notice_flg = false;
    private boolean outdoor_avail = false;

    /**
     * Get the user extended attributes
     */
    private void getUserInfo() {
        accountMgr.getUserProfile(new PayloadCallback<ACObject>() {
            @Override
            public void success(ACObject object) {
                try {
                    ConstantCache.userProInfo = object;
                    outdoor_notice_flg = object.getBoolean("notifyFlg2");
                    outdoor_avail = true;
                    if (object.getBoolean("notifyFlg4")) {
                        if (deviceList != null && deviceList.size() > 0) {
                            handler.sendEmptyMessage(handler_key.GETUSERINFO_SUCCESS.ordinal());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void error(ACException e) {
                outdoor_avail = true;
            }
        });
    }

    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private static int NUM_ITEMS;
        private List<DeviceInfo> deviceFragment;

        public MyPagerAdapter(FragmentManager fragmentManager, List<DeviceInfo> list) {
            super(fragmentManager);
            //this.deviceFragment.clear();
            this.NUM_ITEMS = list.size();
            this.deviceFragment = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            homeFragment = HomeFragment.newInstance(deviceFragment.get(position).getDeviceId(), deviceFragment.get(position).getDeviceName(), deviceFragment.get(position).getSubDomainId());

            return homeFragment;

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void getDeviceModeList() {
        DCPServiceUtils.syncContent(DCPServiceContentAppliances, new PayloadCallback<ACMsg>() {
            @Override
            public void error(ACException e) {
                if (e.getErrorCode() == DCPSERVICE_TOKEN_INVALID_CODE) {
                    handler.sendEmptyMessage(handler_key.TOKEN_INVALID.ordinal());
                    return;
                }
                ConstantCache.deviceModeList = new ArrayList<ACObject>();
                handler.sendEmptyMessage(handler_key.GETDEVICEMODELLIST_FAIL.ordinal());
            }

            @Override
            public void success(ACMsg acMsg) {
                try {
                    ArrayList<ACObject> objects = (ArrayList) ((ACObject) acMsg.getObjectData().get("content")).get("objects");
                    List<ACObject> data = new ArrayList<ACObject>();
                    for (ACObject obj : objects) {
                        ACObject map = new ACObject();
                        map.put("name", obj.get("name").toString());
                        map.put("id", obj.get("id").toString());
                        if (obj.get("medias") != null && ((ArrayList<ACObject>) obj.get("medias")).size() > 0) {
                            map.put("img", ((ArrayList<ACObject>) obj.get("medias")).get(0).get("thumbs").toString());
                        }
                        data.add(map);
                    }
                    ConstantCache.deviceModeList = data;
                    handler.sendEmptyMessage(handler_key.GETDEVICEMODELLIST_SUCCESS.ordinal());
                } catch (Exception e) {
                    L.e(e.getMessage());
                }

            }
        });
    }

    private void ota() {
        for (ACUserDevice item : devices) {
            updateUnit(item);
        }
    }

    private void updateUnit(final ACUserDevice item) {
        final long deviceId = item.getDeviceId();   //Logic device id
        final String subDomain = item.getSubDomain();
        final int otaType = 1;      //Upgrade types, 1 is the MCU system upgrade
        otaMgr.checkUpdate(subDomain, new ACOTACheckInfo(deviceId, otaType), new PayloadCallback<ACOTAUpgradeInfo>() {
            @Override
            public void success(ACOTAUpgradeInfo info) {
                //By judging the info. IsUpdate () to judge whether there is a new version update
                if (info.isUpdate()) {
                    String msg = getString(R.string.airpurifier_more_show_updatetipmsg_text);
                    final String newVersion = info.getTargetVersion();
                    new AlertDialogUserDifine(MainActivity.this).builder()
                            .setTitle(getString(R.string.airpurifier_update_tip_title)).setMsg(String.format(msg, info.getCurrentVersion()) +
                            String.format(getString(R.string.airpurifier_update_tip_msg2), info.getTargetVersion()))
                            .setPositiveButton(getString(R.string.airpurifier_moredevice_show_sure_button), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    otaMgr.confirmUpdate(subDomain, deviceId, newVersion, otaType, new VoidCallback() {
                                        @Override
                                        public void success() {
                                            Toast.makeText(MainActivity.this, getString(R.string.airpurifier_update_success), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void error(ACException e) {
                                            Toast.makeText(MainActivity.this, getString(R.string.airpurifier_update_failed), Toast.LENGTH_SHORT).show();
                                            updateUnit(item);
                                        }
                                    });
                                }
                            }).setCancelable(false).show();
                }
            }

            @Override
            public void error(ACException e) {
            }
        });
    }

    @SuppressLint("WrongConstant")
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (System.currentTimeMillis() - lastTime > 2000) {
                    Toast.makeText(this, R.string.airpurifier_login_show_tospressagainexit_text, AppConstant.TOAST_DURATION).show();
                    lastTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onKeyDown(keyCode, event);
    }


    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String locationInfo = intent.getStringExtra(AppConstant.LOCATION);
            nowCity.setText(MainApplication.location_city);
            if (weatherInfo == null) {
                getWeather();
            }
        }
    }

    public void showDialog() {
        Completable
                .complete()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (mLoadingDialog != null
                                && !mLoadingDialog.isAlreadyShow()
                                && !mLoadingDialog.isShowing()
                                && !MainActivity.this.isFinishing()) {
                            mLoadingDialog.show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable.getMessage());
                    }
                });

    }

    public void dimissDialog() {
        Completable
                .complete()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (mLoadingDialog != null
                                && mLoadingDialog.isAlreadyShow()
                                && !MainActivity.this.isFinishing()) {
                            mLoadingDialog.dismiss();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable.getMessage());
                    }
                });
    }

}
