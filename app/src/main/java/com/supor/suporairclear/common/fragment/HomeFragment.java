package com.supor.suporairclear.common.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.common.ACHelper;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACOTACheckInfo;
import com.accloud.service.ACOTAUpgradeInfo;
import com.accloud.service.ACObject;
import com.accloud.service.ACPushMgr;
import com.accloud.service.ACPushReceive;
import com.accloud.service.ACPushTable;
import com.accloud.service.ACUserDevice;
import com.accloud.utils.PreferencesUtils;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.MoreActivity;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.rihuisoft.loginmode.view.AutoScrollViewPager;
import com.rihuisoft.loginmode.view.BadgeView;
import com.rihuisoft.loginmode.view.CircleDataView;
import com.rihuisoft.loginmode.view.StartTimerPopWindow;
import com.supor.suporairclear.activity.CountryListActivity;
import com.supor.suporairclear.activity.MainActivity;
import com.supor.suporairclear.activity.OutSideActivity;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.common.adapter.ViewParser;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.SubDominConfig;
import com.supor.suporairclear.config.SubDominUtil;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.controller.SendToDevice;
import com.supor.suporairclear.model.CommendInfo;
import com.supor.suporairclear.service.NetWorkChangeReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.MaybeSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

//import com.ant.liao.GifView;

/**
 * Created by enyva on 16/5/27.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private Long deviceId, subdomainId;
    private String deviceName;
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private ACBindMgr bindMgr = AC.bindMgr();
    private RelativeLayout rl_airinfo;

    private LinearLayout ll_layout;
    private View ll_outdoor_banner, rl_daily, rl_sleep, rl_boost, rl_mute;
    private ImageView iv_daily, iv_sleep, iv_boost, iv_mute;
    private TextView tv_room;
    private ImageView iv_switch, iv_light,iv_anion;
    private TextView nowCity;
    private ImageView header_pm25_level;
    private TextView tv_airQuality;
    private View v_narBar;
    private ImageView iv_more_flg;
    private Boolean pageVisiable = false;
    public boolean popupFlg = false;

    private ImageView iv_timerSet;
    private RelativeLayout rl_maintitle, rl_more_flg;
    private LinearLayout ll_city;
    private ACObject weatherInfo;
    private ACObject deviceInfo;
    private ACPushMgr pushMgr = null;
    private SendToDevice sendToDevice;
    private BadgeView badgeView_more;
    private Animation myAnimation;
    private Float currentLight;
    // Sleep mode:
    private int mode = 1;
    // switch 0：close 1：open
    private int switch_ = 0;
    private int anion = 0;
    // PM25value
    private int pm25_value = 0, pm25_level = 0;
    // TVOC value 1：optimal 2：good 3：Light pollution 4：Moderate pollution 5：Serious pollution
    private int smell_level = 1;
    // 1：optimal 2：good 3：Light pollution 4：Moderate pollution 5：Serious pollution
    private int pm_tvoc_level = 1;
    // Composite mesh filter time remaining
    private long filter_time1 = -1, filter_time2 = -1, filter_time3 = -1, filter_time4 = -1;

    // Background lamp brightness adjustment 0: mode 1:1 mode 2 2: model 3
    private int bkLight_level = 0;
    private boolean more_flg = false;

    private static Timer mTimer;
    private TimerTask mTimerTask = null;
    private boolean isTimerNull;
    private Boolean timerDisable = false;
    private boolean isPause = false;

    private static int LIGHT_OFF = 2;
    private static int LIGHT_WEAK = 1;
    private static int LIGHT_STRONG = 0;
    private int control_count = 0;

    private boolean isFlush = true;
    private boolean isLightAnimation = false;

    private static Timer fTimer;
    private TimerTask fTimerTask = null;
    private int watch_count = 3;
    private CircleDataView circleProgressView;

    private LinearLayout ll_appointment;
    private TextView tv_time;
    private TextView tv_time_flg;
    private int offLineCount = 0;
    List<ACObject> filters = new ArrayList<>();
    private AutoScrollViewPager mAutoscrollviewpager;
    private Disposable mUpdateConfirmUpdate;
    private Disposable mGetLatestWifiModuleDisable;

    private boolean netStatus = true;
    private boolean isOnclick = false;
    public static final String TAG = "HomeFragment";


    private enum handler_key {
        GETDEVICEINFO_SUCCESS, GETDEVICEINFO_FAIL, GETCITYPM25_SUCCESS, GETCITYPM25_FAIL, USER_OVERDUE, SET_CONTROLVIEW_TRUE,
        SET_CONTROLVIEW_FALSE, SET_VIEW
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
                    case GETDEVICEINFO_SUCCESS:
                        String startTime = deviceInfo.getString("timePoint");
                        String description = deviceInfo.getString("description");
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                        if (description != null && !description.isEmpty()
                                && startTime != null && !startTime.isEmpty()) {
                            ll_appointment.setVisibility(View.VISIBLE);
                            tv_time.setText(sdf.format(Long.valueOf(startTime)));
                            if (description.substring(0, 1).equals("0")) {
                                tv_time_flg.setText(getString(R.string.airpurifier_more_show_open_text));
                            } else {
                                tv_time_flg.setText(getString(R.string.airpurifier_more_show_close_text));
                            }

                        } else {
                            ll_appointment.setVisibility(View.GONE);
                        }
                        setView();
                        if (mTimer == null && !timerDisable) {
                            startTimer();
                        }
                        break;
                    case GETDEVICEINFO_FAIL:

                        circleProgressView.setCircleShowType(CircleDataView.CircleDateViewType.NOHAVEGAS);
                        circleProgressView.setPm25Value(0);
                        circleProgressView.setPM25Level(1);
                        circleProgressView.showCircleDataView();

                        //失败的时候也显示，滤芯状态
                        mAutoscrollviewpager.showAutoScrollView();

                        clearMode();
                        iv_switch.setImageResource(R.drawable.btn_close);
                        Log.e(TAG,"btn_close===== GETDEVICEINFO_FAIL");
                        iv_light.setImageResource(R.drawable.ico_light_off);

                        break;
                    case GETCITYPM25_SUCCESS:
                        setOutdoor();
                        break;
                    case USER_OVERDUE:
                        AppUtils.reLogin(getActivity());
                        break;
                    case SET_CONTROLVIEW_TRUE:
                        setControlView(true);
                        break;
                    case SET_CONTROLVIEW_FALSE:
                        setControlView(false);
                        break;
                    case SET_VIEW:
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

    String targetWifiModuleVersion;
    String currentWifiModuleVersion;
    boolean isUpdateWifiModuleNotice;

    private void getLatestWifiModule() {
        isUpdateWifiModuleNotice = true;
        //设备升级类型 1系统MCU升级 2 WiFi通信模组升级
        mGetLatestWifiModuleDisable = Single
                .create(new SingleOnSubscribe<ACOTAUpgradeInfo>() {
                    @Override
                    public void subscribe(final SingleEmitter<ACOTAUpgradeInfo> emitter) throws Exception {
                        final ACOTACheckInfo acotaCheckInfo = new ACOTACheckInfo(deviceId, 2);
                        //检查更新
                        AC.otaMgr().checkUpdate(SubDominUtil.findNameById(subdomainId), acotaCheckInfo, new PayloadCallback<ACOTAUpgradeInfo>() {
                            @Override
                            public void success(ACOTAUpgradeInfo acotaUpgradeInfo) {
                                Logger.i("otaUpdate checkUpdate:success " + acotaCheckInfo.getVersion());
                                if (!emitter.isDisposed())
                                    emitter.onSuccess(acotaUpgradeInfo);
                            }

                            @Override
                            public void error(ACException e) {
                                if (!emitter.isDisposed()) emitter.onError(e);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<ACOTAUpgradeInfo>() {
                    @Override
                    public boolean test(ACOTAUpgradeInfo acotaUpgradeInfo) throws Exception {
                        //确认是否更新
                        Logger.i("otaUpdate acotaUpgradeInfo.isUpdate():" + acotaUpgradeInfo.isUpdate());
                        return acotaUpgradeInfo.isUpdate();
                    }
                })
                .flatMapSingle(new Function<ACOTAUpgradeInfo, SingleSource<Boolean>>() {
                    @Override
                    public SingleSource<Boolean> apply(final ACOTAUpgradeInfo acotaUpgradeInfo) throws Exception {
                        targetWifiModuleVersion = acotaUpgradeInfo.getTargetVersion();
                        currentWifiModuleVersion = acotaUpgradeInfo.getCurrentVersion();
                        Logger.i("otaUpdate targetWifiModuleVersion:" + targetWifiModuleVersion + "-currentWifiModuleVersion:" + currentWifiModuleVersion);
                        return Single.create(new SingleOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(final SingleEmitter<Boolean> emitter) throws Exception {
                                AC.accountMgr().getUserProfile(new PayloadCallback<ACObject>() {
                                    @Override
                                    public void success(ACObject acObject) {
                                        if (!emitter.isDisposed()) {
                                            Logger.i("otaUpdate notifyFlg4:" + acObject.getBoolean("notifyFlg4"));
                                            emitter.onSuccess(acObject.getBoolean("notifyFlg4"));
                                        }
                                    }

                                    @Override
                                    public void error(ACException e) {
                                        if (!emitter.isDisposed()) emitter.onError(e);
                                    }
                                });
                            }
                        });
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        if (!aBoolean) {
                            //如果不允许推送，不显示对话框，确认即可
                            confirWifiModleUpdate(targetWifiModuleVersion);
                        }
                        return aBoolean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        //弹出对话框
                        if (FragmentUtil.judgeGetActivityCanUse(HomeFragment.this)) {
                            new AlertDialogUserDifine(getActivity()).builder()
                                    .setMsg(String.format(
                                            getString(R.string.airpurifier_more_show_updatedevice_content)
                                            , currentWifiModuleVersion
                                            , targetWifiModuleVersion))
                                    .setPositiveButton(getString(R.string.know), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            confirWifiModleUpdate(targetWifiModuleVersion);
                                        }
                                    }).setCancelable(false).show();

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e("otaUpdate error:" + throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 确认更新固件
     *
     * @param targetWifiModuleVersion
     */
    private void confirWifiModleUpdate(final String targetWifiModuleVersion) {
        mUpdateConfirmUpdate = Completable
                .create(new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter e) throws Exception {
                        AC.otaMgr().confirmUpdate(SubDominUtil.findNameById(subdomainId), deviceId, targetWifiModuleVersion, 2, new VoidCallback() {
                            @Override
                            public void success() {
                                if (!e.isDisposed()) e.onComplete();
                            }

                            @Override
                            public void error(ACException excep) {
                                if (!e.isDisposed()) e.onError(excep);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Logger.i("otaUpdate confirmUpdate success");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e("otaUpdate confirmUpdate fail:" + throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });

    }

    private void setData() {
        Log.i("dubug_mill", "设置圆圈视图");
        if (deviceInfo.get("error") != null && deviceInfo.getInt("error") == 1) {
            showPop();
        }
        if (deviceInfo.get("filter_initial") != null) {
            filter_time1 = deviceInfo.getInt("filter_initial");
        }
        if (deviceInfo.get("filter_activecarbon") != null) {
            filter_time2 = deviceInfo.getInt("filter_activecarbon");
        }
        if (deviceInfo.get("filter_HEPA") != null) {
            filter_time3 = deviceInfo.getInt("filter_HEPA");
        }

        if (deviceInfo.get("filter_nano") != null) {
            filter_time4 = deviceInfo.getInt("filter_nano");
        }
        if (deviceInfo.get("model") != null) {
            mode = deviceInfo.getInt("model");
        }
        if (deviceInfo.get("on_off") != null) {
            switch_ = deviceInfo.getInt("on_off") % 2;
        }

        if (deviceInfo.get("anion") != null) {
            anion = deviceInfo.getInt("anion");
        }

        if (deviceInfo.get("pm25") != null) {
            if (offFlg) {
                pm25_value = deviceInfo.getInt("pm25");
            } else {
                pm25_value = 0;
            }

        }
        if (deviceInfo.get("pm25_level") != null) {
            pm25_level = deviceInfo.getInt("pm25_level");
        }

        if (deviceInfo.get("hcho") != null) {
            smell_level = deviceInfo.getInt("hcho");
            if (smell_level > 3) {
                smell_level = 3;
            }

        }
        if (subdomainId == AppConstant.DEVICE_XS) {
            //offFlg true is online
            circleProgressView.setCircleShowType(CircleDataView.CircleDateViewType.NOHAVEGAS);
            if (!deviceInfo.getBoolean("offFlg")) {
                circleProgressView.setPm25Value(0);
                circleProgressView.setPM25Level(1);
            } else {
                circleProgressView.setPM25Level(pm25_level);
                circleProgressView.setPm25Value(pm25_value);
            }
        } else {
            //offFlg true is online
            if (!deviceInfo.getBoolean("offFlg")) {
                circleProgressView.setCircleShowType(CircleDataView.CircleDateViewType.HAVAGAS);
                circleProgressView.setPm25Value(0);
                circleProgressView.setPM25Level(1);
            } else {
                if (switch_ == 0) {
                    circleProgressView.setCircleShowType(CircleDataView.CircleDateViewType.HAVAGASNOCAROUSEL);
                } else {
                    circleProgressView.setCircleShowType(CircleDataView.CircleDateViewType.HAVAGAS);
                    circleProgressView.setTvocValue(smell_level);
                }
                circleProgressView.setPM25Level(pm25_level);
                circleProgressView.setPm25Value(pm25_value);
            }
        }
        circleProgressView.showCircleDataView();

        tv_room.setText(deviceName);

        if (deviceInfo.get("light") != null) {
            bkLight_level = deviceInfo.getInt("light") % 3;
        }

        if (deviceInfo.get("flag") != null
                && deviceInfo.getLong("flag") > 0) {
            more_flg = true;
        } else if (deviceInfo.get("flag") != null) {
            more_flg = false;
        }
        if (filterChanged()) {
            setFilters();
        }
    }

    private boolean filterChanged() {
        Boolean change = false;
        Long f1Value = Math.round(filter_time1 / 10.0);
        Long f2Value = Math.round(filter_time2 / 10.0);
        Long f3Value = Math.round(filter_time3 / 10.0);
        Long f4Value = Math.round(filter_time4 / 10.0);

        if (filters != null && filters.size() == 4) {
            if (!filters.get(0).get("value").toString().equals(String.valueOf(f1Value)) ||
                    !filters.get(1).get("value").toString().equals(String.valueOf(f2Value)) ||
                    !filters.get(2).get("value").toString().equals(String.valueOf(f3Value)) ||
                    !filters.get(3).get("value").toString().equals(String.valueOf(f4Value))) {
                change = true;
            }
        } else {
            return true;
        }

        return change;
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
        FragmentManager childFragmentManager = getChildFragmentManager();
        ViewParser.scrollRightVp(childFragmentManager, view, R.id.picslooper2, R.id.pageIndexor2, list);
        //获取不到城市，图片不显示，文字显示--
        if (weatherInfo.getInt("overall") == 0) {
            return;
        }
        if (weatherInfo.getInt("overall") < AppConstant.AIR_1) {
            header_pm25_level.setImageResource(R.drawable.ico_air_01s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_fresh));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_2) {
            header_pm25_level.setImageResource(R.drawable.ico_air_02s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_moderate));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_3) {
            header_pm25_level.setImageResource(R.drawable.ico_air_03s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_high));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_4) {
            header_pm25_level.setImageResource(R.drawable.ico_air_04s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_very));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_5) {
            header_pm25_level.setImageResource(R.drawable.ico_air_05s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_excessive));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_6) {
            header_pm25_level.setImageResource(R.drawable.ico_air_06s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_extreme));
        } else {
            header_pm25_level.setImageResource(R.drawable.ico_air_07s);
            tv_airQuality.setText(getString(R.string.airpurifier_more_airquality_airpocalypse));
        }
    }

    private void getWeather() {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(0);
        list.add(0);
        FragmentManager childFragmentManager = getChildFragmentManager();
        ViewParser.scrollRightVp(childFragmentManager, view, R.id.picslooper2, R.id.pageIndexor2, list);
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

    private void setFilters() {
        filters = new ArrayList<>();
        ACObject f1 = new ACObject();
        f1.put("name", getString(R.string.airpurifier_more_show_prefilter_tex));
        f1.put("value", (Math.round(filter_time1 / 10.0)));
        filters.add(f1);
        ACObject f2 = new ACObject();
        f2.put("name", getString(R.string.airpurifier_more_show_activefilter_tex));
        f2.put("value", (Math.round(filter_time2 / 10.0)));
        filters.add(f2);
        ACObject f3 = new ACObject();
        f3.put("name", getString(R.string.airpurifier_more_show_hepafilter_tex));
        f3.put("value", (Math.round(filter_time3 / 10.0)));
        filters.add(f3);
        ACObject f4 = new ACObject();
        f4.put("name", getString(R.string.airpurifier_more_show_nanofilter_tex));
        f4.put("value", (Math.round(filter_time4 / 10.0)));
        filters.add(f4);

        mAutoscrollviewpager.setData(filters);
        mAutoscrollviewpager.showAutoScrollView();

    }

    private void setView() {
        setControlView(false);
        nowCity.setText(MainApplication.location_city);
        if (more_flg) {
            badgeView_more.show();
        } else {
            badgeView_more.hide();
        }
    }

    // newInstance constructor for creating fragment with arguments
    public static HomeFragment newInstance(long deviceId, String deviceName,
                                           long subdomainId) {
        HomeFragment fragmentFirst = new HomeFragment();
        try {
            Bundle args = new Bundle();
            args.putLong("someInt", deviceId);
            args.putLong("subdomainId", subdomainId);
            args.putString("someName", deviceName);
            fragmentFirst.setArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(System.currentTimeMillis() + "");
        deviceId = getArguments().getLong("someInt", Long.valueOf(0));
        subdomainId = getArguments().getLong("subdomainId");
        deviceName = getArguments().getString("someName");
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.LOCATION_ACTION);
        getContext().registerReceiver(new LocationBroadcastReceiver(), filter);


    }

    private void setAnionVisible(Long subdomainId) {
        if(subdomainId == null || subdomainId == 0) return;
        if(subdomainId.equals(SubDominConfig.TEFAL_XL.mSubDominId) || subdomainId.equals(SubDominConfig.TEFAL_XS.mSubDominId)){
            iv_anion.setVisibility(View.VISIBLE);
        }else {
            iv_anion.setVisibility(View.GONE);
        }
    }

    // Inflate the view for the fragment based on layout XML
    @SuppressLint({"InlinedApi", "NewApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            ll_outdoor_banner = view.findViewById(R.id.ll_outdoor_banner);

            rl_daily = view.findViewById(R.id.rl_daily);
            rl_sleep = view.findViewById(R.id.rl_sleep);
            rl_boost = view.findViewById(R.id.rl_boost);
            rl_mute = view.findViewById(R.id.rl_mute);

            iv_daily = (ImageView) view.findViewById(R.id.iv_daily);
            iv_sleep = (ImageView) view.findViewById(R.id.iv_sleep);
            iv_boost = (ImageView) view.findViewById(R.id.iv_boost);
            iv_mute = (ImageView) view.findViewById(R.id.iv_mute);

            ll_layout = (LinearLayout) view.findViewById(R.id.ll_layout);

            iv_switch = (ImageView) view.findViewById(R.id.iv_switch);
            iv_light = (ImageView) view.findViewById(R.id.iv_light);
            iv_anion = (ImageView) view.findViewById(R.id.iv_anion);

            rl_airinfo = (RelativeLayout) view.findViewById(R.id.rl_airinfo);
            rl_more_flg = (RelativeLayout) view.findViewById(R.id.rl_more_flg);
            // rl_airinfo.addView(gif1);
            iv_more_flg = (ImageView) view.findViewById(R.id.iv_more_flg);
            badgeView_more = new BadgeView(getActivity(), iv_more_flg);
            // badgeView_more.show();
            tv_room = (TextView) view.findViewById(R.id.tv_room);
            nowCity = (TextView) view.findViewById(R.id.nowCity);
            tv_airQuality = (TextView) view.findViewById(R.id.tv_airQuality);
            header_pm25_level = (ImageView) view.findViewById(R.id.pm25);
            v_narBar = (View) view.findViewById(R.id.v_narbar);
            ll_city = (LinearLayout) view.findViewById(R.id.ll_city);
            ll_appointment = (LinearLayout) view
                    .findViewById(R.id.ll_appointment);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_time_flg = (TextView) view.findViewById(R.id.tv_time_flg);
            nowCity.setTypeface(AppConstant.ubuntuRegular);
            tv_room.setTypeface(AppConstant.ubuntuRegular);
            ll_city.setOnClickListener(this);
            iv_light.setOnClickListener(this);
            iv_anion.setOnClickListener(this);
            iv_switch.setOnClickListener(this);

            rl_maintitle = (RelativeLayout) view
                    .findViewById(R.id.rl_maintitle);
            rl_maintitle.setOnClickListener(this);

            iv_timerSet = (ImageView) view.findViewById(R.id.iv_switch);
            iv_timerSet.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    StartTimerPopWindow startTimerPopWindow = new StartTimerPopWindow(
                            getActivity(), iv_timerSet, deviceId, deviceName);
                    startTimerPopWindow.showPopupWindow(iv_timerSet);

                    return true;
                }
            });

            rl_more_flg.setOnClickListener(this);

            tv_room.setText(deviceName);
            rl_daily.setOnClickListener(this);
            rl_sleep.setOnClickListener(this);
            rl_boost.setOnClickListener(this);
            rl_mute.setOnClickListener(this);
            ll_outdoor_banner.setOnClickListener(this);

            //设置滤芯轮播
            mAutoscrollviewpager = (AutoScrollViewPager) view.findViewById(R.id.autoscrollviewpager);
            mAutoscrollviewpager.prepareWork(getChildFragmentManager());

            if (MainApplication.location_city != null
                    && !MainApplication.location_city.isEmpty()) {
                nowCity.setText(MainApplication.location_city.replace("市", ""));
            }
            circleProgressView = (CircleDataView) view.findViewById(R.id.cv_pm);
            if (AppUtils.hasNavBar(this.getActivity())) {
                v_narBar.setVisibility(View.INVISIBLE);
            } else {
                v_narBar.setVisibility(View.GONE);
            }

            setAnionVisible(subdomainId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private View view;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
//        setFilters();
        getWeather();
    }

    private void connectWatch(final Long deviceId) {
        try {
            pushMgr.connect(new VoidCallback() {
                @Override
                public void success() {
                    changeWatch(deviceId);
                }

                @Override
                public void error(ACException e) {
                    Log.e("connectWatch", "Subscribe to the failure error:" + e.getErrorCode()
                            + "-->" + e.getMessage());
                    retryWatch();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void retryWatch() {
        if (watch_count > 0) {
            pushMgr.disconnect();
            pushMgr = AC.pushMgr();
            connectWatch(deviceId);
            watch_count--;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");

        if (getUserVisibleHint()) {
            getDeviceInfo();
            handler.sendEmptyMessage(handler_key.GETCITYPM25_SUCCESS
                    .ordinal());
        } else {

            if (pushMgr != null) {
                unWatch();
                pushMgr = null;
            }
        }
    }


    @Override
    public void onPause() {
        Log.e(TAG,"onPause");
        try {
            stopTimer();

            Log.e(TAG,"stopTimeronPause");

            if (fTimer != null) {
                fTimer.cancel();
                fTimer = null;
            }
            if (pushMgr != null) {
                unWatch();
                pushMgr = null;
            }
            super.onPause();
        } catch (Exception e) {
        }
    }

    @Override
    public void
    setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG,"setUserVisibleHint");
        try {
            if (isVisibleToUser) {
                //确认无误之后，再开打开
                if (!isUpdateWifiModuleNotice) getLatestWifiModule();
                if (mTimer != null) {
                    stopTimer();
                    Log.e(TAG,"stopTimersetUserVisibleHint");
                }
                pageVisiable = true;
                timerDisable = false;
                getDeviceInfo();

            } else {

                if (pushMgr != null) {
                    unWatch();
                    pushMgr = null;
                }
                pageVisiable = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void receiveChange() {
        try {
            pushMgr.onReceive(new PayloadCallback<ACPushReceive>() {
                @Override
                public void success(ACPushReceive pushReceive) {
                    try {
                        ACObject object = pushReceive.getPayload();
                        if (object.toString().equals(deviceInfo.toString())) {
                            return;
                        }
                        deviceInfo = object;
                        offFlg = true;
                        deviceInfo.put("offFlg", offFlg);
                        ConstantCache.deviceStatusList.put(deviceId, deviceInfo);
                        setData();
                        Log.i("receive", "deviceinfo control_count = " + control_count);

                        if (control_count == 0) {
                            setIsFlushDelay();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(ACException e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void changeWatch(final Long deviceId) {
        try {
            ACPushTable table = new ACPushTable();
            table.setClassName("DeviceInfoManager");
            ACObject primaryKey = new ACObject();
            primaryKey.put("deviceId", deviceId);
            table.setPrimaryKey(primaryKey);
            table.setOpType(ACPushTable.OPTYPE_DELETE
                    | ACPushTable.OPTYPE_REPLACE | ACPushTable.OPTYPE_UPDATE);
            Log.i("changeWatch", "start");
            pushMgr.watch(table, new VoidCallback() {
                @Override
                public void success() {
                    Log.i("changeWatch", "success");
                    receiveChange();

                }

                @Override
                public void error(ACException e) {
                    Log.i("changeWatch", "error");
                    Log.e("watch", "Set the subscription failed" + "error:" + e.getErrorCode() + "-->"
                            + e.getMessage());
                    retryWatch();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void unWatch() {
        try {
            ACPushTable table = new ACPushTable();
            table.setClassName("DeviceInfoManager");
            ACObject primaryKey = new ACObject();
            primaryKey.put("deviceId", deviceId);
            table.setPrimaryKey(primaryKey);
            pushMgr.unwatch(table, new VoidCallback() {
                @Override
                public void success() {
                }

                @Override
                public void error(ACException e) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showPop() {
        if (!popupFlg) {
            popupFlg = true;
            new AlertDialogUserDifine(getActivity()).builder()
                    .setTitle(getString(R.string.airpurifier_more_show_deviceFailure_text))
                    .setMsg(getString(R.string.airpurifier_more_show_rightcovers_text))
                    .setPositiveButton(getString(R.string.airpurifier_more_show_getit_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupFlg = false;
                        }
                    }).setCancelable(false).show();
        }

    }

    /**
     * get device info form cloud,return locked
     */
    private boolean offFlg = true;

    protected void getDeviceInfo() {
        try {

            deviceInfo = ConstantCache.deviceStatusList.get(deviceId);
            if (deviceInfo != null) {
                setData();

                setControlView(false);
            }

            bindMgr.listDevicesWithStatus(new PayloadCallback<List<ACUserDevice>>() {
                @Override
                public void success(List<ACUserDevice> arg0) {

                    try {
                        for (ACUserDevice tmp : arg0) {
                            if (tmp.getDeviceId() == deviceId) {
                                if (tmp.getStatus() != 0) {
                                    Log.i("dubug_mill", "DataStatus:" + tmp.getStatus());
                                    offFlg = true;
                                    if (ConstantCache.deviceStatusList.get(deviceId) != null) {
                                        switch_ = ConstantCache.deviceStatusList.get(deviceId).getInt("on_off");
                                    } else {
                                        switch_ = 0;
                                    }
                                    if (pageVisiable) {
                                    }

                                } else {
                                    offFlg = false;
                                }
                            }

                        }

                    } catch (Exception e) {
                        Log.e("listDevicesWithStatus", "error:" + e.getMessage() + "-->" + e.toString());

                    }
                }

                @Override
                public void error(ACException e) {
                    Log.e("listDevicesWithStatus", "error:" + e.getMessage() + "-->" + e.toString());

                }
            });

            if (deviceId != null) {
                Log.i("dubug_mill", "queryDeviceInfo");
                msgHelper.queryDeviceInfo(deviceId, new PayloadCallback<ACObject>() {
                    @Override
                    public void success(ACObject result) {

                        try {
                            Log.d("result", result.toString());
                            deviceInfo = result;
                            deviceInfo.put("offFlg", offFlg);
                            if (offFlg) {
                                ConstantCache.deviceStatusList.put(deviceId, deviceInfo);
                            } else {
                                deviceInfo.put("on_off", 0);
                                deviceInfo.put("pm25", 0);
                                ConstantCache.deviceStatusList.put(deviceId, deviceInfo);
                            }
                            setData();
                            if (pageVisiable) {
                                pushMgr = AC.pushMgr();
                                connectWatch(deviceId);
                            }
                            handler.sendEmptyMessage(handler_key.GETDEVICEINFO_SUCCESS
                                    .ordinal());
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void error(ACException e) {
                        try {
                            ConstantCache.deviceStatusList.remove(deviceId);
                            if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1
                                    || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                                    || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                                    || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {

                                handler.sendEmptyMessage(handler_key.USER_OVERDUE.ordinal());
                            } else if (e.getErrorCode() == AppConstant.ERR_OVERDUE_3) {
                                MainActivity activity = (MainActivity) getActivity();
                                activity.getDeviceList();
                            }
                            handler.sendEmptyMessage(handler_key.GETDEVICEINFO_FAIL
                                    .ordinal());
                            Log.e("queryDeviceInfo", "error:" + e.getErrorCode() + "-->"
                                    + e.getMessage());
                        } catch (Exception eac) {

                        }

                    }
                });
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }

    }

    private void setIsFlushDelay() {
        isFlush = false;
        if (fTimer != null) {
            fTimer.cancel();
        }
        fTimer = new Timer();
        fTimerTask = new TimerTask() {
            @Override
            public void run() {
                isFlush = true;
                handler.sendEmptyMessage(handler_key.SET_VIEW
                        .ordinal());
            }
        };
        fTimer.schedule(fTimerTask, 500);
    }

    private void clearMode() {
        iv_daily.setImageResource(R.drawable.ico_daily_nor);
        iv_daily.setBackgroundResource(R.drawable.circle_gray_bg);
        iv_sleep.setImageResource(R.drawable.ico_sleep_nor);
        iv_sleep.setBackgroundResource(R.drawable.circle_gray_bg);
        iv_boost.setImageResource(R.drawable.ico_boost_nor);
        iv_boost.setBackgroundResource(R.drawable.circle_gray_bg);
        iv_mute.setImageResource(R.drawable.ico_mute_nor);
        iv_mute.setBackgroundResource(R.drawable.circle_gray_bg);
    }

    @Override
    public void onClick(View view) {
        try {
            Intent intent = null;
            CommendInfo commend = null;
            int old_val, old_mode = mode;
            switch (view.getId()) {
                case R.id.rl_daily:
                    if (switch_ == 0) {
                        return;
                    }
                    old_val = mode;
                    mode = 3;
                    clearMode();
                    iv_daily.setImageResource(R.drawable.ico_daily_sel);
                    iv_daily.setBackgroundResource(R.drawable.circle_rec_white_bg);
                    //点击之后 设置灯光可点击
                    iv_light.setClickable(true);
                    commend = new CommendInfo();
                    commend.setDeviceId(deviceId);
                    commend.setCommend("model");
                    commend.setValue(mode);
                    controlDevice(commend, old_val, old_mode);
                    break;
                case R.id.rl_sleep:

                    if (switch_ == 0) {
                        return;
                    }
                    old_val = mode;
                    mode = 2;
                    clearMode();
                    iv_sleep.setImageResource(R.drawable.ico_sleep_sel);
                    iv_sleep.setBackgroundResource(R.drawable.circle_rec_white_bg);
                    //点击之后 将灯光立即变暗,并且灯光不可点击
                    iv_light.setImageResource(R.drawable.ico_light_off);
                    iv_light.setClickable(false);
                    ;
                    commend = new CommendInfo();
                    commend.setDeviceId(deviceId);
                    commend.setCommend("model");
                    commend.setValue(mode);
                    controlDevice(commend, old_val, old_mode);
                    break;
                case R.id.rl_boost:
                    if (switch_ == 0) {
                        return;
                    }
                    old_val = mode;
                    mode = 4;
                    clearMode();
                    iv_boost.setImageResource(R.drawable.ico_boost_sel);
                    iv_boost.setBackgroundResource(R.drawable.circle_rec_white_bg);
                    //点击之后 设置灯光可点击
                    iv_light.setClickable(true);
                    commend = new CommendInfo();
                    commend.setDeviceId(deviceId);
                    commend.setCommend("model");
                    commend.setValue(mode);
                    controlDevice(commend, old_val, old_mode);
                    break;
                case R.id.rl_mute:
                    if (switch_ == 0) {
                        return;
                    }
                    old_val = mode;
                    mode = 1;
                    clearMode();
                    iv_mute.setImageResource(R.drawable.ico_mute_sel);
                    iv_mute.setBackgroundResource(R.drawable.circle_rec_white_bg);
                    //点击之后 设置灯光可点击
                    iv_light.setClickable(true);
                    commend = new CommendInfo();
                    commend.setDeviceId(deviceId);
                    commend.setCommend("model");
                    commend.setValue(mode);
                    controlDevice(commend, old_val, old_mode);
                    break;

                case R.id.iv_switch:
                    isOnclick = true;
                    old_val = switch_;
                    switch_ = Math.abs(switch_ - 1) % 2;
                    if (switch_ != 0) {
                        ((ImageView) view).setImageResource(R.drawable.btn_open);
                    } else {
                        ((ImageView) view).setImageResource(R.drawable.btn_close);
                    }
                    setControlView(false);
                    commend = new CommendInfo();
                    commend.setDeviceId(deviceId);
                    commend.setCommend("on_off");
                    commend.setValue(switch_);
                    controlDevice(commend, old_val, old_mode);
                    break;
                case R.id.iv_light:
                    if (switch_ == 0) {
                        return;
                    }
                    old_val = bkLight_level;
                    bkLight_level = (bkLight_level + 1) % 3;
                    if (mode == 2 && bkLight_level == LIGHT_STRONG) {
                        bkLight_level = LIGHT_WEAK;
                    }
                    if (bkLight_level == LIGHT_STRONG) {
                        ((ImageView) view)
                                .setImageResource(R.drawable.ico_light_high);
                    } else if (bkLight_level == LIGHT_WEAK) {
                        ((ImageView) view)
                                .setImageResource(R.drawable.ico_light_low);
                    } else if (bkLight_level == LIGHT_OFF) {
                        ((ImageView) view).setImageResource(R.drawable.ico_light_off);
                    }
                    commend = new CommendInfo();
                    commend.setDeviceId(deviceId);
                    commend.setCommend("light");
                    commend.setValue(bkLight_level);
                    controlDevice(commend, old_val, old_mode);
                    break;
                case R.id.iv_anion:
                    if (switch_ == 0) {
                        return;
                    }
                    old_val = anion;
                    anion = Math.abs(anion - 1) % 2;
                    if (anion != 0) {
                        ((ImageView) view).setImageResource(R.drawable.ico_anion_on);
                    } else {
                        ((ImageView) view).setImageResource(R.drawable.ico_anion_0ff);
                    }

                    commend = new CommendInfo();
                    commend.setDeviceId(deviceId);
                    commend.setCommend("anion");
                    commend.setValue(anion);
                    controlDevice(commend, old_val, old_mode);
                    break;
                case R.id.rl_more_flg:
                    try {
                        intent = new Intent(getActivity(), MoreActivity.class);
                        // intent.putExtra("deviceId", deviceId);
                        // intent.putExtra("flag", AppConstant.REGISTER);
                        MainActivity.deviceId = deviceId;
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.ll_city:
                    intent = new Intent(getActivity(), CountryListActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_bottom_in,
                            R.anim.no_anim);
                    break;
                case R.id.rl_maintitle:
                    intent = new Intent(getActivity(), OutSideActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_right_in,
                            R.anim.no_anim);
                    break;
                case R.id.ll_outdoor_banner:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setControlView(boolean fail) {
        try {
            if (switch_ == 1 && netStatus) {
                iv_switch.setImageResource(R.drawable.btn_open);
                Log.e(TAG,"btn_open====switch_ 1");
                if (mode == 3) {
                    iv_light.setClickable(true);
                    clearMode();
                    iv_daily.setImageResource(R.drawable.ico_daily_sel);
                    iv_daily.setBackgroundResource(R.drawable.circle_rec_white_bg);
                } else if (mode == 2) {
                    //当是睡眠模式的时候，不能控制灯光
                    iv_light.setClickable(false);
                    clearMode();
                    iv_sleep.setImageResource(R.drawable.ico_sleep_sel);
                    iv_sleep.setBackgroundResource(R.drawable.circle_rec_white_bg);
                } else if (mode == 4) {
                    iv_light.setClickable(true);
                    clearMode();
                    iv_boost.setImageResource(R.drawable.ico_boost_sel);
                    iv_boost.setBackgroundResource(R.drawable.circle_rec_white_bg);
                } else if (mode == 1) {
                    iv_light.setClickable(true);
                    clearMode();
                    iv_mute.setImageResource(R.drawable.ico_mute_sel);
                    iv_mute.setBackgroundResource(R.drawable.circle_rec_white_bg);
                }
                if (mode == 2 && bkLight_level == LIGHT_STRONG) {
                    bkLight_level = LIGHT_WEAK;
                }
                if (bkLight_level == LIGHT_OFF) {
                    iv_light.setImageResource(R.drawable.ico_light_off);
                } else if (bkLight_level == LIGHT_STRONG) {
                    iv_light.setImageResource(R.drawable.ico_light_high);
                } else if (bkLight_level == LIGHT_WEAK) {
                    iv_light.setImageResource(R.drawable.ico_light_low);
                }

                if(anion == 0){
                    iv_anion.setImageResource(R.drawable.ico_anion_0ff);
                }else {
                    iv_anion.setImageResource(R.drawable.ico_anion_on);
                }

            } else if (switch_ == 0) {
                clearMode();
                iv_switch.setImageResource(R.drawable.btn_close);
                Log.e(TAG,"btn_close  ==== switch_0");
                iv_light.setImageResource(R.drawable.ico_light_off);
            } else if (switch_ == 3) {
                clearMode();
                iv_switch.setImageResource(R.drawable.btn_close);
                Log.e(TAG,"btn_close ==== switch_3");
                iv_light.setImageResource(R.drawable.ico_light_off);
                handler.sendEmptyMessage(handler_key.GETDEVICEINFO_FAIL
                        .ordinal());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Control equipment state
     */
    protected void controlDevice(final CommendInfo commend, final int old_val, final int old_mode) {
        if (!commend.getCommend().equals("pm25")) {
        }

        control_count++;
        msgHelper.controlDevice(commend, new VoidCallback() {
            @Override
            public void success() {
                offFlg = true;
                offLineCount = 0;
                if (control_count > 0) {
                    int delay = 1000;
                    if (commend.getCommend().equals("on_off") && switch_ == 1) {
                        delay = 100;
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            control_count--;
                            if (control_count <= 0) {
                                setIsFlushDelay();
                            }
                        }
                    }, delay);

                }
                Log.i("success", "deviceinfo isFlush = " + isFlush + ",control_count = " + control_count);

            }

            @Override
            public void error(ACException e) {
                try {
                    if (control_count > 0) {
                        control_count--;
                    }

                    if (commend.getCommend().equals("pm25") && e.getErrorCode() == AppConstant.TIME_OUT) {
                        return;
                    }
                    if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1
                            || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                            || e.getErrorCode() == AppConstant.ERR_OVERDUE_4
                            || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                            || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {

                        handler.sendEmptyMessage(handler_key.USER_OVERDUE.ordinal());
                    } else if (e.getErrorCode() == 3807) {
                        switch_ = 3;
                        offLineCount++;
                        if (offFlg && offLineCount >= 3) {
                            Toast.makeText(
                                    getActivity(),
                                    getActivity().getString(R.string.airpurifier_moredevice_show_devicenotonline_text),
                                    Toast.LENGTH_SHORT).show();
                            offFlg = false;
                        } else if (isOnclick){
                            isOnclick = false;
                            Toast.makeText(
                                    getActivity(),
                                    getActivity().getString(R.string.airpurifier_moredevice_show_devicenotonline_text),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!commend.getCommend().equals("pm25")) {
                            if (e.getErrorCode() == 1999) {
                                Toast.makeText(
                                        getActivity(),
                                        getActivity().getString(R.string.airpurifier_moredevice_show_controlfailed_text),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        getActivity().getString(R.string.airpurifier_moredevice_show_netnormal_text),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    if (commend.getCommend().equals("model")) {
                        mode = old_val;
                    } else if (commend.getCommend().equals("on_off")) {
                        switch_ = old_val;
                    } else if (commend.getCommend().equals("light")) {
                        bkLight_level = old_val;
                    } else if(commend.getCommend().equals("anion")){
                        anion = old_val;
                    }
                    isFlush = true;

                    handler.sendEmptyMessage(handler_key.SET_CONTROLVIEW_TRUE
                            .ordinal());
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        });
    }

    private void pm25Refresh() {
        CommendInfo commend = new CommendInfo();
        commend.setDeviceId(deviceId);
        commend.setCommend("pm25");
        commend.setValue(0);
        controlDevice(commend, 0, mode);
    }

    private void startTimer() {
        Log.i("dubug_mill", "开始定时");
        try {
            if (mTimer == null && mTimerTask == null) {
                isTimerNull = true;
            } else {
                isTimerNull = false;
            }
            if (mTimer == null) {
                mTimer = new Timer();
            }

            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        timerDisable = false;
                        pm25Refresh();
                        Log.d(TAG,"pm25Refresh");
                        do {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                            }
                        } while (isPause);
                    }
                };
            }

            if (isTimerNull && mTimer != null && mTimerTask != null) {
                mTimer.schedule(mTimerTask, 3000, 3000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTimer() {
        Log.i("dubug_mill", "结束定时");
        try {
            timerDisable = true;
            Log.i("RefreshTest", "RefreshTest+DeviceFragment :: timerFlag2 : "
                    + timerDisable);
            if (mTimer != null) {
                mTimer.purge();
                mTimer.cancel();
                mTimer = null;
            }

            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String locationInfo = intent.getStringExtra(AppConstant.LOCATION);
            if (nowCity != null) nowCity.setText(MainApplication.location_city);
            if (getContext() != null)
                getContext().unregisterReceiver(this);//When you don't need to log ou
            PreferencesUtils.putString(MainApplication.getInstance(), "city", MainApplication.location_city);
        }
    }

    @Override
    public void onDetach() {
        if (circleProgressView != null) circleProgressView.dismissCircleDataView();
        if (mGetLatestWifiModuleDisable != null && !mGetLatestWifiModuleDisable.isDisposed())
            mGetLatestWifiModuleDisable.dispose();
        if (mUpdateConfirmUpdate != null && !mUpdateConfirmUpdate.isDisposed())
            mUpdateConfirmUpdate.dispose();
        super.onDetach();
    }

    /**
     *
     * @param netState 网络状态
     */
    public void netWorkChange2RefreshUI(boolean netState){
        if(netState){
            startTimer();
            netStatus = true;
        }else {
            stopTimer();
            switch_ = 0;
            netStatus = false;

        }
        if (getUserVisibleHint()) {
            getDeviceInfo();

        } else {

            if (pushMgr != null) {
                unWatch();
                pushMgr = null;
            }
        }
    }


}
