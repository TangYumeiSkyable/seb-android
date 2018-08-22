package com.supor.suporairclear.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.service.ACTimerMgr;
import com.accloud.service.ACTimerTask;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.LoadingDialog;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.controller.ACMsgHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by enyva on 16/6/5.
 */
public class TimerSetActivity extends BaseActivity {
    private static final int REQUEST_CODE_REPEAT = 1234;
    private static final int REQUEST_CODE_REFRESH = 1235;
    // h5url
    private String url;
    // Page number：1.The reservation list; 2.Add the booking; 3.Modify the reservation; 4.Repeat setting;
    private int page = 1;
    // On a page：0.More and more 1.The home page
    private int src = 1;
    private Long[] taskIds;
    private String taskId_str;

    private WebView mWebView;
    private WebSettings mWebSettings;
    private Handler mHandler;
    private ACTimerMgr timerMgr = AC.timerMgr();
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private ACBindMgr bindMgr = AC.bindMgr();
    private static String TAG = "TimerSetActivity";
    private JSONArray taskList = new JSONArray();
    private String week_repeat = "Once";
    private String repeat = "once";
    private String[] weeks_array = {"Sunday", " Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private Long deviceId;
    private String deviceName = "default";
    private List<ACObject> deviceList = ConstantCache.deviceList;
    private HashMap<Long, String> deviceMap = new HashMap<Long, String>();
    private static List<ACObject> timerTasks_0;
    private static List<ACTimerTask> timerTasks_1;
    private SimpleDateFormat sdf_full = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf_hm = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat sdf_full_local = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf_ymd_local = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf_hm_local = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat sdf_local = new SimpleDateFormat("HH:mm");
    private ACObject wordMap;
    private FrameLayout mFlWebViewContainer;

    //    private boolean repeat_flg = false;
    private enum handler_key {
        GETTASKLIST_SUCCESS,
        ADDTASK_SUCCESS,
        GET_WEEK_REPEAT,
        SET_WEEK_REPEAT,
        ADD_TIMER_TASK,
        MODIFY_TIMER_TASK,
        DEL_TIMER_TASK,
        REFRESH_PAGE,
        REQUEST_ALL_TASKS,
        SET_DEVICE_NAME,
        INIT_REPEAT,
        USER_OVERDUE
    }

    /**
     * The handler.
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            try {
                TimerSetActivity.handler_key key = TimerSetActivity.handler_key.values()[msg.what];
                switch (key) {
                    case GETTASKLIST_SUCCESS:
                        try {

                            mWebView.loadUrl("javascript:setLabel(" + wordMap.toString() + ")");
                            mWebView.loadUrl("javascript:setValue(" + taskList.toString() + ")");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case SET_WEEK_REPEAT:
                        try {
                            mWebView.loadUrl("javascript:set_repeat('" + week_repeat + "')");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case GET_WEEK_REPEAT:
                        try {
                            mWebView.loadUrl("javascript:get_week()");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case ADD_TIMER_TASK:
                        try {
                            mWebView.loadUrl("javascript:addTimer()");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case MODIFY_TIMER_TASK:
                        try {
                            mWebView.loadUrl("javascript:modifyTimer()");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case DEL_TIMER_TASK:
                        try {
                            mWebView.loadUrl("javascript:delTimer()");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case REFRESH_PAGE:
                        try {
                            mWebView.loadUrl("javascript:load()");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case REQUEST_ALL_TASKS:
                        try {
                            mWebView.loadUrl("javascript:setLabel(" + wordMap.toString() + ")");
                            mWebView.loadUrl("javascript:setDevice(1," + deviceList.toString() + "," + page + ")");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case SET_DEVICE_NAME:
                        try {
                            mWebView.loadUrl("javascript:setLabel(" + wordMap.toString() + ")");
                            if (page == 2) {
                                mWebView.loadUrl("javascript:setDevice(0,'" + deviceName + "'," + page + ");setDefault(" + deviceId + ",'" + ConstantCache.deviceNameMap.get(deviceId) + "')");
                            } else {
                                mWebView.loadUrl("javascript:setDevice(0,'" + deviceName + "'," + page + ")");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case INIT_REPEAT:
                        try {
                            mWebView.loadUrl("javascript:setLabel(" + wordMap.toString() + ")");
                            mWebView.loadUrl("javascript:setValue('" + repeat + "')");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case USER_OVERDUE:
                        AppUtils.reLogin(TimerSetActivity.this);
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
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_timerset);
            AppManager.getAppManager().addActivity(this);
            url = getIntent().getStringExtra("url");
            page = getIntent().getIntExtra("page", 1);
            src = getIntent().getIntExtra("src", 1);
            deviceId = getIntent().getLongExtra("deviceId", 1);
            if (src > 0) {
                deviceName = getIntent().getStringExtra("deviceName");
            }
            if (page == 1) {
                setNavBtn(R.drawable.ico_back, R.drawable.ico_add);
            } else if (page == 2) {
                setNavBtn(R.drawable.ico_cancel, R.drawable.ico_confirm);
                setTitle(getString(R.string.airpurifier_more_show_addscheduling_text));
            } else if (page == 3) {
                taskId_str = getIntent().getStringExtra("taskId_str");
                taskIds = new Long[]{Long.valueOf(taskId_str.split("_")[0]), Long.valueOf(taskId_str.split("_")[1])};
                setNavBtn(R.drawable.ico_cancel, R.drawable.ico_confirm);
                setTitle(getString(R.string.airpurifier_more_show_editscheduling_tex));
            } else {
                repeat = getIntent().getStringExtra("repeat");
                setNavBtn(R.drawable.ico_back, 0);
            }
            for (ACObject device : ConstantCache.deviceList) {
                deviceMap.put(device.getLong("deviceId"), device.getString("deviceName"));
            }
            wordMap = new ACObject();
            wordMap.put("no_device", getString(R.string.airpurifier_more_show_noscheduling_text));
            wordMap.put("add_scheduling", getString(R.string.airpurifier_more_show_addscheduling_text));
            wordMap.put("delete_word", getString(R.string.airpurifier_more_show_deleteword_text));
            wordMap.put("sure_to_delete", getString(R.string.airpurifier_more_show_suretodelete_text));
            wordMap.put("ok_word", getString(R.string.airpurifier_public_ok));
            wordMap.put("cancel_word", getString(R.string.airpurifier_more_show_cancelword_text));
            wordMap.put("save_word", getString(R.string.airpurifier_more_show_saveword_text));

            wordMap.put("sunday_word", getString(R.string.airpurifier_more_show_sundayword_text));
            wordMap.put("monday_word", getString(R.string.airpurifier_more_show_mondayword_text));
            wordMap.put("tuesday_word", getString(R.string.airpurifier_more_show_tuesdayword_text));
            wordMap.put("wednesday_word", getString(R.string.airpurifier_more_show_wednesdayword_text));
            wordMap.put("thursday_word", getString(R.string.airpurifier_more_show_thursdayword_text));
            wordMap.put("friday_word", getString(R.string.airpurifier_more_show_fridayword_text));
            wordMap.put("saturday_word", getString(R.string.airpurifier_more_show_saturdayword_text));

            wordMap.put("sun_word", getString(R.string.airpurifier_more_show_sunword_text));
            wordMap.put("mon_word", getString(R.string.airpurifier_more_show_monword_text));
            wordMap.put("tues_word", getString(R.string.airpurifier_more_show_tuesword_text));
            wordMap.put("wed_word", getString(R.string.airpurifier_more_show_wedword_text));
            wordMap.put("thur_word", getString(R.string.airpurifier_more_show_thurword_text));
            wordMap.put("fri_word", getString(R.string.airpurifier_more_show_friword_text));
            wordMap.put("sat_word", getString(R.string.airpurifier_more_show_satword_text));

            wordMap.put("choose_device", getString(R.string.airpurifier_more_show_choosedevice_text));
            wordMap.put("choose_mode", getString(R.string.airpurifier_more_show_choosemode_text));
            wordMap.put("on_time", getString(R.string.airpurifier_more_show_ontime_text));
            wordMap.put("off_time", getString(R.string.airpurifier_more_show_offtime_text));
            wordMap.put("repeat_word", getString(R.string.airpurifier_more_show_repeatword_text));

            wordMap.put("device_name", getString(R.string.airpurifier_more_show_devicename_tex));
            wordMap.put("device_mode", getString(R.string.airpurifier_more_show_devicemode_tex));
            wordMap.put("auto_day", getString(R.string.airpurifier_more_show_autoday_tex));
            wordMap.put("auto_night", getString(R.string.airpurifier_more_show_autonight_tex));
            wordMap.put("boost_word", getString(R.string.airpurifier_more_show_boostword_tex));
            wordMap.put("silent_word", getString(R.string.airpurifier_more_show_silentword_tex));
            wordMap.put("once_word", getString(R.string.airpurifier_more_show_onceword_tex));

            wordMap.put("my_scheduling", getString(R.string.airpurifier_more_show_myscheduling_tex));
            wordMap.put("edit_scheduling", getString(R.string.airpurifier_more_show_editscheduling_tex));
            wordMap.put("delete_scheduling", getString(R.string.airpurifier_more_show_deletescheduling_tex));
            wordMap.put("repeat_setting", getString(R.string.airpurifier_more_show_repeatsetting_tex));

            wordMap.put("time_warning", getString(R.string.airpurifier_add_shchedule_tip));


            initView();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Controls the initialization
     */
    @SuppressLint("JavascriptInterface")
    private void initView() {
        try {
            mFlWebViewContainer = (FrameLayout) findViewById(R.id.fl_webview_container);
            if (mWebView == null) {
                mWebView = new WebView(this.getApplication());
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mFlWebViewContainer.addView(mWebView, layoutParams);
            }
            mWebSettings = mWebView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);
            mWebSettings.setBuiltInZoomControls(true);
            mWebSettings.setLightTouchEnabled(true);
            mWebSettings.setSupportZoom(true);
            mWebView.setHapticFeedbackEnabled(false);

            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    if (page != 2 && page != 3) {
                        setTitle(title);
                    }
                }

                //To monitor the WebView console messages
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    // TODO Auto-generated method stub
                    if (consoleMessage.message().contains("Uncaught ReferenceError")) {
                        // do something...
                    }
                    return super.onConsoleMessage(consoleMessage);
                }
            });


            mWebView.addJavascriptInterface(new TimerJavaScriptInterface(), "timer");

            mWebView.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final class TimerJavaScriptInterface {
        private boolean addTimerFlag = false;

        TimerJavaScriptInterface() {
        }

        @JavascriptInterface
        public void getRepeat() {
            handler.sendEmptyMessage(handler_key.INIT_REPEAT.ordinal());
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void getTimerList() {
            mWebView.post(new Runnable() {
                public void run() {
                    showDialog();
                    if (src == 0) {
                        taskList = new JSONArray();

                        msgHelper.queryAllDeviceTimer(ConstantCache.deviceList, new PayloadCallback<List<ACObject>>() {
                            @Override
                            public void success(List<ACObject> timerTasks) {
                                dimissDialog();
                                JSONObject map;
                                timerTasks_0 = timerTasks;
                                taskList = new JSONArray();
                                try {
                                    HashMap<String, ACObject> pairMap = new HashMap<String, ACObject>();
                                    for (ACObject timerTask : timerTasks) {
                                        String[] description = timerTask.getString("description").split(":");
                                        if (description.length == 4 && pairMap.get(description[1]) == null) {
                                            pairMap.put(description[1], timerTask);
                                        } else if (description.length == 4 && pairMap.get(description[1]) != null) {
                                            ACObject timerTask0 = pairMap.get(description[1]);
                                            long start;
                                            long end;
                                            if (description[0].equals("1")) {
                                                start = timerTask0.getLong("timePoint");
                                                end = timerTask.getLong("timePoint");
                                            } else {
                                                start = timerTask.getLong("timePoint");
                                                end = timerTask0.getLong("timePoint");
                                            }
                                            if (description.equals(timerTask0.getString("description"))) {
                                                deleteTimers(Long.valueOf(description[2]), timerTask0.getLong("taskId"), timerTask.getLong("taskId"));
                                                continue;
                                            }
                                            try {
                                                if (timerTask.getString("timeCycle").equals("once") && sdf_full_local.parse(sdf_full.format(end)).compareTo(new Date()) < 0 && description.length == 4) {
                                                    deleteTimers(Long.valueOf(description[2]), timerTask0.getLong("taskId"), timerTask.getLong("taskId"));
                                                    continue;
                                                }
                                            } catch (Exception e) {

                                            }
                                            map = new JSONObject();
                                            if (description[0].equals("1")) {
                                                map.put("status", timerTask.getInt("status"));
                                                map.put("taskId", timerTask0.getLong("taskId") + "_" + timerTask.getLong("taskId"));
                                            } else {
                                                map.put("status", timerTask0.getInt("status"));
                                                map.put("taskId", timerTask.getLong("taskId") + "_" + timerTask0.getLong("taskId"));
                                            }
                                            map.put("description", timerTask.getString("description"));
                                            map.put("timePoint", sdf.format(start));
                                            map.put("endTime", sdf.format(end));
                                            map.put("timeCycle", timerTask.getString("timeCycle"));

                                            if (description.length == 4) {
                                                map.put("deviceId", description[2]);
                                            } else {
                                                map.put("deviceId", deviceId);
                                            }
                                            map.put("name", deviceMap.get(map.getLong("deviceId")));
                                            taskList.put(map);
                                            pairMap.remove(description[1]);
                                        }
                                    }
                                    for (String key : pairMap.keySet()) {
                                        ACObject delTask = pairMap.get(key);
                                        String[] description = delTask.getString("description").split(":");
                                        deleteTimer(Long.valueOf(description[2]), delTask.getLong("taskId"));
                                    }
                                    handler.sendEmptyMessage(handler_key.GETTASKLIST_SUCCESS.ordinal());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void error(ACException e) {
                                dimissDialog();
                                if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1 || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                                        || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                                        || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {
                                    handler.sendEmptyMessage(handler_key.USER_OVERDUE.ordinal());

                                }
                                Log.e("queryAllDeviceTimer", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                            }
                        });
                    } else {
                        timerMgr.listTasks(deviceId, new PayloadCallback<List<ACTimerTask>>() {
                            @Override
                            public void success(List<ACTimerTask> timerTasks) {
                                dimissDialog();
                                timerTasks_1 = timerTasks;
                                JSONObject map;
                                taskList = new JSONArray();
                                //Through the logcat view access to the timing task list for display or further processing
                                try {
                                    HashMap<String, ACTimerTask> pairMap = new HashMap<String, ACTimerTask>();
                                    for (ACTimerTask timerTask : timerTasks) {
                                        String[] description = timerTask.getDescription().split(":");
                                        if (description.length == 4 && pairMap.get(description[1]) == null) {
                                            pairMap.put(description[1], timerTask);
                                        } else if (description.length == 4 && pairMap.get(description[1]) != null) {
                                            ACTimerTask timerTask0 = pairMap.get(description[1]);
                                            String start;
                                            String end;
                                            if (description[0].equals("1")) {
                                                start = timerTask0.getTimePoint();
                                                end = timerTask.getTimePoint();
                                            } else {
                                                start = timerTask.getTimePoint();
                                                end = timerTask0.getTimePoint();
                                            }
                                            if (description.equals(timerTask0.getDescription())) {
                                                deleteTimers(Long.valueOf(description[2]), timerTask0.getTaskId(), timerTask.getTaskId());
                                                continue;
                                            }
                                            try {
                                                if (timerTask.getTimeCycle().equals("once") && sdf_full_local.parse(end).compareTo(new Date()) < 0) {
                                                    deleteTimers(deviceId, timerTask0.getTaskId(), timerTask.getTaskId());
                                                    continue;
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            map = new JSONObject();
                                            if (description[0].equals("1")) {
                                                map.put("status", timerTask.getStatus());
                                                map.put("taskId", timerTask0.getTaskId() + "_" + timerTask.getTaskId());
                                            } else {
                                                map.put("status", timerTask0.getStatus());
                                                map.put("taskId", timerTask.getTaskId() + "_" + timerTask0.getTaskId());
                                            }


                                            map.put("description", timerTask.getDescription());
                                            map.put("timePoint", start.substring(11, 16));
                                            map.put("endTime", end.substring(11, 16));
                                            map.put("timeCycle", timerTask.getTimeCycle());

                                            map.put("timezone", timerTask.getTimeZone());
                                            if (description.length == 4) {
                                                map.put("deviceId", description[2]);
                                            } else {
                                                map.put("deviceId", deviceId);
                                            }
                                            map.put("name", deviceMap.get(map.getLong("deviceId")));
                                            taskList.put(map);
                                            pairMap.remove(description[1]);
                                        }

                                    }
                                    for (String key : pairMap.keySet()) {
                                        ACTimerTask delTask = pairMap.get(key);
                                        String[] description = delTask.getDescription().split(":");
                                        deleteTimer(Long.valueOf(description[2]), delTask.getTaskId());
                                    }
                                    handler.sendEmptyMessage(handler_key.GETTASKLIST_SUCCESS.ordinal());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void error(ACException e) {
                                Log.e("listTasks", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                                dimissDialog();
                            }
                        });
                    }

                }
            });

        }

        @JavascriptInterface
        public void getTimer() {
            if (src == 0) {
                handler.sendEmptyMessage(handler_key.REQUEST_ALL_TASKS.ordinal());
            } else {
                handler.sendEmptyMessage(handler_key.SET_DEVICE_NAME.ordinal());
            }

            if (page == 3) {
                showDialog();
                mWebView.post(new Runnable() {
                    public void run() {
                        timerMgr.listTasks(deviceId, new PayloadCallback<List<ACTimerTask>>() {
                            @Override
                            public void success(List<ACTimerTask> timerTasks) {
                                dimissDialog();
                                timerTasks_1 = timerTasks;
                                JSONObject map;
                                taskList = new JSONArray();

                                //Through the logcat view access to the timing task list for display or further proces
                                try {
                                    HashMap<String, ACTimerTask> pairMap = new HashMap<String, ACTimerTask>();
                                    boolean existFlg = false;
                                    for (ACTimerTask timerTask : timerTasks) {
                                        map = new JSONObject();
                                        if (timerTask.getTaskId() != taskIds[0] && timerTask.getTaskId() != taskIds[1]) {
                                            continue;
                                        } else {
                                            String[] description = timerTask.getDescription().split(":");
                                            if (description.length == 4 && pairMap.get(description[1]) == null) {
                                                pairMap.put(description[1], timerTask);
                                            } else if (description.length == 4 && pairMap.get(description[1]) != null) {
                                                ACTimerTask timerTask0 = pairMap.get(description[1]);
                                                String start;
                                                String end;
                                                if (description[0].equals("1")) {
                                                    start = timerTask0.getTimePoint();
                                                    end = timerTask.getTimePoint();
                                                    map.put("taskId", timerTask0.getTaskId() + "_" + timerTask.getTaskId());
                                                } else {
                                                    start = timerTask.getTimePoint();
                                                    end = timerTask0.getTimePoint();
                                                    map.put("taskId", timerTask.getTaskId() + "_" + timerTask0.getTaskId());
                                                }
                                                map.put("description", timerTask.getDescription());
                                                map.put("timePoint", start.substring(11, 16));
                                                map.put("endTime", end.substring(11, 16));
                                                map.put("timeCycle", timerTask.getTimeCycle());
                                                map.put("status", timerTask.getStatus());
                                                map.put("timezone", timerTask.getTimeZone());
                                                map.put("devicMode", getDeviceMode(description[3]));
                                                if (description.length == 4) {
                                                    map.put("deviceId", description[2]);
                                                } else {
                                                    map.put("deviceId", deviceId);
                                                }
                                                map.put("name", deviceMap.get(map.getLong("deviceId")));
                                                repeat = timerTask.getTimeCycle();
                                                taskList.put(map);
                                                existFlg = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (existFlg) {
                                        handler.sendEmptyMessage(handler_key.GETTASKLIST_SUCCESS.ordinal());
                                    } else {
                                        Intent intent = new Intent();
                                        //设置返回数据
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void error(ACException e) {
                                Log.e("listTasks", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                                dimissDialog();
                            }

                        });

                    }
                });
            }
        }


        /**
         * @paramter deviceId
         * @paramter name
         * @paramter timePoint  格式为"yyyy-MM-dd HH:mm:ss"
         * @paramter timeCycle  "week[0,1,2,3,4,5,6]":在每星期的HH:mm:ss时间点循环执行(如周一，周五重复，则表示为"week[1,5]")
         */
        @JavascriptInterface
        public void addTask(final String str_deviceId, final String name, final int model, final String timePoint, final String endTime) {
            final byte[] modes = AppUtils.intToBytes2(model);
            if (addTimerFlag) {
                return;
            }
            showDialog();
            addTimerFlag = true;
            msgHelper.queryAllDeviceTimer(ConstantCache.deviceList, new PayloadCallback<List<ACObject>>() {
                @Override
                public void success(List<ACObject> timerTasks) {
                    Logger.i(Thread.currentThread().getName());
                    timerTasks_0 = timerTasks;

                    addT(str_deviceId, name, modes[3], timePoint, endTime);
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    addT(str_deviceId, name, modes[3], timePoint, endTime);
                    addTimerFlag = false;
                }
            });

        }

        private void addT(final String str_deviceId, final String name, final byte mode, final String timePoint, final String endTime) {
            final Long deviceId = Long.valueOf(str_deviceId);
            if (checkOverlap(deviceId, timePoint, endTime)) {
                addTimerFlag = false;
                dimissDialog();
                ShowToast(getResources().getString(R.string.airpurifier_more_show_appointmenttimeoverlap_text));
                return;
            }
            final String timestamp = String.valueOf(System.currentTimeMillis());
            byte mode2 = 0x04;
            byte mode3 = 0x19;
            String modeName = "null";
            if (mode == 0x01) {
                mode2 = 0x0c;
                mode3 = 0x22;
                modeName = "silent";
            } else if (mode == 0x02) {
                mode2 = 0x0c;
                mode3 = 0x23;
                modeName = "auto night";
            } else if (mode == 0x03) {
                mode2 = 0x0c;
                mode3 = 0x24;
                modeName = "auto day";
            } else if (mode == 0x04) {
                mode2 = 0x0c;
                mode3 = 0x25;
                modeName = "boost";

            }
            ACDeviceMsg msg = new ACDeviceMsg(66, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x10, (byte) 0x03,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, mode2, (byte) 0x01, mode, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, mode3}, "0:" + timestamp + ":" + deviceId + ":" + modeName);


            final String timeCycle = repeat;
            Log.d("addTask", "timeCycle = " + repeat + ", timePoint = " + timePoint + ", endTime = " + endTime + ", name = " + name);
            final String finalModeName = modeName;
            timerMgr.addTask(ACTimerTask.OP_TYPE.CLOUD, deviceId, name, timePoint, timeCycle, msg, new PayloadCallback<ACTimerTask>() {
                @Override
                public void success(ACTimerTask task) {
                    Logger.i(Thread.currentThread().getName());
                    dimissDialog();
                    ACDeviceMsg msg = new ACDeviceMsg(66, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x10, (byte) 0x03,
                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18}, "1:" + timestamp + ":" + deviceId + ":" + finalModeName);
                    timerMgr.addTask(ACTimerTask.OP_TYPE.CLOUD, deviceId, name, endTime, timeCycle, msg, new PayloadCallback<ACTimerTask>() {
                        @Override
                        public void success(ACTimerTask task) {
                            try {
                                ShowToast(getResources().getString(R.string.airpurifier_more_show_adddingtimesuccess_text));
                                Intent intent = new Intent();
                                //Set the data returned
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("addTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }

                @Override
                public void error(ACException e) {
                    ShowToast(getResources().getString(R.string.airpurifier_more_show_operatorfailed_text));
                    Log.e("addTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                    dimissDialog();
                }
            });
        }

        /**
         * @paramter deviceId
         * @paramter taskId
         */
        @JavascriptInterface
        public void modifyTask(final String name, final String timePoint, final String endTime) {
            showDialog();
            timerMgr.listTasks(deviceId, new PayloadCallback<List<ACTimerTask>>() {
                @Override
                public void success(List<ACTimerTask> timerTasks) {
                    dimissDialog();
                    timerTasks_1 = timerTasks;
                    modeifyT(name, timePoint, endTime);
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    Log.e("listTasks", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                    modeifyT(name, timePoint, endTime);
                }
            });

        }

        private void modeifyT(final String name, final String timePoint, final String endTime) {
            if (checkOverlap(deviceId, taskIds, timePoint, endTime)) {
                ShowToast(getResources().getString(R.string.airpurifier_more_show_appointmenttimeoverlap_text));

                return;
            }
            final String timestamp = String.valueOf(System.currentTimeMillis());
            final String timeCycle = repeat;
            ACDeviceMsg msg = new ACDeviceMsg(66, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x10, (byte) 0x03,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x19}, "0:" + timestamp + ":" + deviceId);
            showDialog();
            timerMgr.modifyTask(deviceId, taskIds[0], name, timePoint, timeCycle, msg, new VoidCallback() {
                @Override
                public void success() {
                    dimissDialog();
                    ACDeviceMsg msg = new ACDeviceMsg(66, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x10, (byte) 0x03,
                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18}, "1:" + timestamp + ":" + deviceId);
                    timerMgr.modifyTask(deviceId, taskIds[1], name, endTime, timeCycle, msg, new VoidCallback() {
                        @Override
                        public void success() {
                            try {
                                ShowToast(getResources().getString(R.string.airpurifier_more_show_verifydingtimesuccess_text));
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("modifyTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    ShowToast(getResources().getString(R.string.airpurifier_more_show_operatorfailed_text));
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    Log.e("modifyTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        }

        /**
         * @paramter deviceId
         * @paramter taskId
         */
        @JavascriptInterface
        public void modifyTask(final String str_deviceId, final String name, final int model, final String timePoint, final String endTime) {
            final Long deviceId = Long.valueOf(str_deviceId);
            final byte[] modes = AppUtils.intToBytes2(model);
            showDialog();
            timerMgr.listTasks(deviceId, new PayloadCallback<List<ACTimerTask>>() {
                @Override
                public void success(List<ACTimerTask> timerTasks) {
                    timerTasks_1 = timerTasks;
                    modifyT(str_deviceId, name, modes[3], timePoint, endTime);
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    Log.e("listTasks", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                    modifyT(str_deviceId, name, modes[3], timePoint, endTime);
                }
            });

        }

        private void modifyT(final String str_deviceId, final String name, final byte mode, final String timePoint, final String endTime) {
            final Long deviceId = Long.valueOf(str_deviceId);
            if (checkOverlap(deviceId, taskIds, timePoint, endTime)) {
                ShowToast(getResources().getString(R.string.airpurifier_more_show_appointmenttimeoverlap_text));
                dimissDialog();
                return;
            }

            byte mode2 = 0x04;
            byte mode3 = 0x19;
            String modeName = "null";
            if (mode == 0x01) {
                mode2 = 0x0c;
                mode3 = 0x22;
                modeName = "silent";
            } else if (mode == 0x02) {
                mode2 = 0x0c;
                mode3 = 0x23;
                modeName = "auto night";
            } else if (mode == 0x03) {
                mode2 = 0x0c;
                mode3 = 0x24;
                modeName = "auto day";
            } else if (mode == 0x04) {
                mode2 = 0x0c;
                mode3 = 0x25;
                modeName = "boost";
            }

            final String timestamp = String.valueOf(System.currentTimeMillis());
            final String timeCycle = repeat;
            ACDeviceMsg msg = new ACDeviceMsg(66, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x10, (byte) 0x03,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, mode2, (byte) 0x01, mode, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, mode3}, "0:" + timestamp + ":" + deviceId + ":" + modeName);
            final String finalModeName = modeName;
            timerMgr.modifyTask(deviceId, taskIds[0], name, timePoint, timeCycle, msg, new VoidCallback() {
                @Override
                public void success() {
                    dimissDialog();
                    ACDeviceMsg msg = new ACDeviceMsg(66, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x10, (byte) 0x03,
                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18}, "1:" + timestamp + ":" + deviceId + ":" + finalModeName);
                    timerMgr.modifyTask(deviceId, taskIds[1], name, endTime, timeCycle, msg, new VoidCallback() {
                        @Override
                        public void success() {
                            try {
                                ShowToast(getResources().getString(R.string.airpurifier_more_show_verifydingtimesuccess_text));
                                Intent intent = new Intent();
                                //Set the data returned
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("modifyTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    ShowToast(getResources().getString(R.string.airpurifier_more_show_operatorfailed_text));
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    Log.e("modifyTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        }

        /**
         * @paramter deviceId
         * @paramter taskId
         */
        @JavascriptInterface
        public void delTimer() {
            showDialog();
            timerMgr.deleteTask(deviceId, taskIds[0], new VoidCallback() {
                @Override
                public void success() {
                    dimissDialog();
                    timerMgr.deleteTask(deviceId, taskIds[1], new VoidCallback() {
                        @Override
                        public void success() {
                            try {
                                Intent intent = new Intent();
                                //Set the data returned
                                setResult(RESULT_OK, intent);
                                finish();
                                ShowToast(getResources().getString(R.string.airpurifier_more_show_deletedingtimesuccess_text));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("delTimer", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    ShowToast(getResources().getString(R.string.airpurifier_more_show_operatorfailed_text));
                    Intent intent = new Intent();
                    //Set the data returned
                    setResult(RESULT_OK, intent);
                    finish();
                    Log.e("delTimer", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        }

        /**
         * @paramter deviceId
         * @paramter taskId
         */
        @JavascriptInterface
        public void delTimer(final String str_taskId) {
            String[] tidarray = str_taskId.split("_");
            final Long[] taskIds = new Long[]{Long.valueOf(tidarray[0]), Long.valueOf(tidarray[1])};
            showDialog();
            timerMgr.deleteTask(deviceId, taskIds[0], new VoidCallback() {
                @Override
                public void success() {
                    timerMgr.deleteTask(deviceId, taskIds[1], new VoidCallback() {
                        @Override
                        public void success() {
                            try {
                                ShowToast(getResources().getString(R.string.airpurifier_more_show_deletedingtimesuccess_text));
                                getTimerList();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("delTimer", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    ShowToast(getResources().getString(R.string.airpurifier_more_show_operatorfailed_text));
                    getTimerList();
                    Log.e("delTimer", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        }

        /**
         * @paramter deviceId
         * @paramter taskId
         */
        @JavascriptInterface
        public void delTimer(final String str_deviceId, final String str_taskId) {
            final Long deviceId = Long.valueOf(str_deviceId);

            String[] tidarray = str_taskId.split("_");
            final Long[] tids;
            if (tidarray.length < 2) {
                tids = new Long[]{taskIds[0], taskIds[1]};
            } else {
                tids = new Long[]{Long.valueOf(tidarray[0]), Long.valueOf(tidarray[1])};
            }
            showDialog();
            timerMgr.deleteTask(deviceId, tids[0], new VoidCallback() {
                @Override
                public void success() {

                    dimissDialog();
                    timerMgr.deleteTask(deviceId, tids[1], new VoidCallback() {
                        @Override
                        public void success() {
                            try {
                                ShowToast(getResources().getString(R.string.airpurifier_more_show_deletedingtimesuccess_text));
                                if (page == 3) {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    getTimerList();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("delTimer", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    ShowToast(getResources().getString(R.string.airpurifier_more_show_operatorfailed_text));
                    if (page == 3) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        getTimerList();
                    }
                    Log.e("delTimer", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        }

        /**
         * @paramter deviceId
         * @paramter taskId
         */
        @JavascriptInterface
        public void openTask(final String str_deviceId, final String str_taskId) {
            final Long deviceId = Long.valueOf(str_deviceId);
            String[] tidarray = str_taskId.split("_");
            final Long[] taskIds = new Long[]{Long.valueOf(tidarray[0]), Long.valueOf(tidarray[1])};
            showDialog();
            timerMgr.openTask(deviceId, taskIds[0], new VoidCallback() {
                @Override
                public void success() {
                    dimissDialog();
                    timerMgr.openTask(deviceId, taskIds[1], new VoidCallback() {
                        @Override
                        public void success() {
                            try {
                                Toast.makeText(TimerSetActivity.this, getResources().getString(R.string.airpurifier_more_show_open_dingtime_success_text), AppConstant.TOAST_DURATION).show();
                                getTimerList();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("openTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    Toast.makeText(TimerSetActivity.this, getResources().getString(R.string.airpurifier_more_show_operatorfailed_text), AppConstant.TOAST_DURATION).show();
                    getTimerList();
                    Log.e("openTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });

        }

        /**
         * @paramter deviceId
         * @paramter taskId
         */
        @JavascriptInterface
        public void closeTask(final String str_deviceId, final String str_taskId) {
            final Long deviceId = Long.valueOf(str_deviceId);
            String[] tidarray = str_taskId.split("_");
            final Long[] taskIds = new Long[]{Long.valueOf(tidarray[0]), Long.valueOf(tidarray[1])};
            showDialog();
            timerMgr.closeTask(deviceId, taskIds[0], new VoidCallback() {
                @Override
                public void success() {
                    Logger.i(Thread.currentThread().getName());
                    dimissDialog();
                    timerMgr.closeTask(deviceId, taskIds[1], new VoidCallback() {
                        @Override
                        public void success() {
                            try {
                                Toast.makeText(TimerSetActivity.this, getResources().getString(R.string.airpurifier_more_show_closedingtimesuccess_text), AppConstant.TOAST_DURATION).show();
                                getTimerList();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("openTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
                }

                @Override
                public void error(ACException e) {
                    dimissDialog();
                    Toast.makeText(TimerSetActivity.this, getResources().getString(R.string.airpurifier_more_show_operatorfailed_text), AppConstant.TOAST_DURATION).show();
                    getTimerList();
                    Log.e("openTask", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        }

        @JavascriptInterface
        public void pageChange(final String pagestr) {
            try {
                final int pageno = Integer.valueOf(pagestr);

                Intent intent = null;
                if (pageno == 2) {
                    intent = new Intent(TimerSetActivity.this, TimerSetActivity.class);
                    intent.putExtra("url", "file:///android_asset/appointment-item.html");
                    intent.putExtra("page", pageno);
                    intent.putExtra("taskId_str", "0_0");
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("deviceName", deviceName);
                    intent.putExtra("src", src);
                    startActivityForResult(intent, REQUEST_CODE_REFRESH);
                } else if (pageno == 4) {
                    intent = new Intent(TimerSetActivity.this, TimerSetActivity.class);
                    intent.putExtra("url", "file:///android_asset/repeat.html");
                    intent.putExtra("page", pageno);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("deviceName", deviceName);
                    intent.putExtra("src", src);
                    intent.putExtra("repeat", repeat);
                    startActivityForResult(intent, REQUEST_CODE_REPEAT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @JavascriptInterface
        public void pageChangeM(final String pagestr, final String str_deviceId, final String deviceName, final String str_taskId) {
            try {
                final Long deviceId = Long.valueOf(str_deviceId);
                final int pageno = Integer.valueOf(pagestr);

                Intent intent = null;
                if (pageno == 3) {
                    intent = new Intent(TimerSetActivity.this, TimerSetActivity.class);
                    intent.putExtra("url", "file:///android_asset/appointment-item.html");
                    intent.putExtra("page", pageno);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("taskId_str", str_taskId);
                    intent.putExtra("deviceName", deviceName);
                    intent.putExtra("src", src);
                    startActivityForResult(intent, REQUEST_CODE_REFRESH);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @JavascriptInterface
        public void pageChange(final String pagestr, final String str_taskId) {
            try {
                final int pageno = Integer.valueOf(pagestr);

                Intent intent = null;
                if (pageno == 3) {
                    intent = new Intent(TimerSetActivity.this, TimerSetActivity.class);
                    intent.putExtra("url", "file:///android_asset/appointment-item.html");
                    intent.putExtra("page", pageno);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("taskId_str", str_taskId);
                    intent.putExtra("deviceName", deviceName);
                    intent.putExtra("src", src);
                    startActivityForResult(intent, REQUEST_CODE_REFRESH);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @JavascriptInterface
        public void returnRepeat(final String week, final String repeat) {
            try {
                Intent intent = new Intent();
                //The data returned in the Intent
                intent.putExtra("week", week);
                intent.putExtra("repeat", repeat);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("deviceName", deviceName);
                //Set the data returned
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private String getDeviceMode(String s) {
        if (TextUtils.equals(s, "auto day")) {
            return getString(R.string.airpurifier_more_show_autoday_tex);
        } else if (TextUtils.equals(s, "auto night")) {
            return getString(R.string.airpurifier_more_show_autonight_tex);
        } else if (TextUtils.equals(s, "boost")) {
            return getString(R.string.airpurifier_more_show_boostword_tex);
        } else if (TextUtils.equals(s, "silent")) {
            return getString(R.string.airpurifier_more_show_silentword_tex);
        } else {
            return "";
        }
    }

    private void deleteTimers(final long deviceId, final long id1, final long id2) {
        showDialog();
        timerMgr.deleteTask(deviceId, id2, new VoidCallback() {
            @Override
            public void success() {
                dimissDialog();
                timerMgr.deleteTask(deviceId, id1, new VoidCallback() {
                    @Override
                    public void success() {
                        Log.d("d", id1 + "," + id2 + "Timing was removed successfully");
                    }

                    @Override
                    public void error(ACException e) {
                        Log.e("e", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                    }
                });
            }

            @Override
            public void error(ACException e) {
                dimissDialog();
                Log.e("e", "error:" + e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    private void deleteTimer(final long deviceId, final long id1) {
        showDialog();
        timerMgr.deleteTask(deviceId, id1, new VoidCallback() {
            @Override
            public void success() {
                dimissDialog();
                Log.d("d", id1 + "Timing was removed successfully");
            }

            @Override
            public void error(ACException e) {
                dimissDialog();
                Log.e("e", "error:" + e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CODE_REPEAT) {
                    String week = result.getExtras().getString("week");

                    week_repeat = week;
                    repeat = result.getExtras().getString("repeat");
                    handler.sendEmptyMessage(handler_key.SET_WEEK_REPEAT.ordinal());
                } else if (requestCode == REQUEST_CODE_REFRESH) {

                    handler.sendEmptyMessage(handler_key.REFRESH_PAGE.ordinal());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView != null && handler != null) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
            if ((keyCode == KeyEvent.KEYCODE_BACK) && page == 4) {
                handler.sendEmptyMessage(handler_key.GET_WEEK_REPEAT.ordinal());
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            Intent intent = null;
            switch (page) {
                case 1:
                    if (component == TitleBar.RIGHT) {
                        try {
                            intent = new Intent(TimerSetActivity.this, TimerSetActivity.class);
                            intent.putExtra("url", "file:///android_asset/appointment-item.html");
                            intent.putExtra("page", 2);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("deviceName", deviceName);
                            intent.putExtra("src", src);
                            startActivityForResult(intent, REQUEST_CODE_REFRESH);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (component == TitleBar.LEFT) {
                        finish();
                    }
                    break;
                case 2:
                    if (component == TitleBar.RIGHT) {
                        handler.sendEmptyMessage(handler_key.ADD_TIMER_TASK.ordinal());
                    } else if (component == TitleBar.LEFT) {
                        intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                    break;
                case 3:
                    if (component == TitleBar.RIGHT) {
                        handler.sendEmptyMessage(handler_key.MODIFY_TIMER_TASK.ordinal());
                    } else if (component == TitleBar.LEFT) {
                        intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    break;
                case 4:
                    if (component == TitleBar.LEFT) {
                        handler.sendEmptyMessage(handler_key.GET_WEEK_REPEAT.ordinal());

                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ShowToast(String text) {
        try {
            if (!TextUtils.isEmpty(text)) {
                Toast.makeText(TimerSetActivity.this.getApplicationContext(), text,
                        AppConstant.TOAST_DURATION).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkOverlap(final long deviceId, final String startTime, final String endTime) {
        Date sd, ed, task_st, task_ed;
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        List<HashMap<String, Date>> newList;
        List<HashMap<String, Date>> timerList;
        boolean over_flg = false;
        boolean week_flg = false;
        task_st = null;
        task_ed = null;
        try {
            sd = sdf_full_local.parse(startTime);
            ed = sdf_full_local.parse(endTime);
            Long[] tids = {-1l, -1l};
            if (src == 0) {
                timerTasks_1 = new ArrayList<ACTimerTask>();
                for (ACObject task : timerTasks_0) {
                    ACTimerTask timer = new ACTimerTask();
                    timer.setTaskId(task.getLong("taskId"));
                    timer.setTimeCycle(task.getString("timeCycle"));
                    timer.setDescription(task.getString("description"));
                    timer.setTimePoint(sdf_full.format(task.getLong("timePoint")));
                    timer.setStatus(task.getInt("status"));
                    timerTasks_1.add(timer);
                }
            }
            over_flg = checkOverlap(deviceId, tids, startTime, endTime);
        } catch (Exception e) {
            Log.e("sdf", e.toString());
        }
        return over_flg;
    }

    private boolean checkOverlap(final long deviceId, final Long[] taskIds, final String startTime, final String endTime) {
        Date sd, ed, task_st, task_ed;
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        List<HashMap<String, Date>> newList = new ArrayList<HashMap<String, Date>>();
        List<HashMap<String, Date>> timerList;
        boolean over_flg = false;
        boolean week_flg = false;
        task_st = null;
        task_ed = null;
        try {
            sd = sdf_full_local.parse(startTime);
            ed = sdf_full_local.parse(endTime);
            HashMap<String, ACTimerTask> pairMap = new HashMap<String, ACTimerTask>();
            if (repeat.equals("once")) {
                HashMap<String, Date> newMap = new HashMap<String, Date>();
                newMap.put("start", sd);
                newMap.put("end", ed);
                newList.add(newMap);
            } else {
                String[] cycles_new = repeat.substring(repeat.indexOf("[") + 1, repeat.indexOf("]")).split(",");
                for (int i = 0; i < cycles_new.length; i++) {
                    calendar.setTime(now);
                    int this_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    int timer_week = Integer.valueOf(cycles_new[i]);
                    calendar.add(Calendar.DATE, (timer_week - this_week + 7) % 7);
                    HashMap<String, Date> map = new HashMap<String, Date>();
                    map.put("start", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(sd)));
                    if (sdf_hm_local.format(sd).compareTo(sdf_hm_local.format(ed)) >= 0) {
                        calendar.add(Calendar.DATE, 1);
                    }
                    map.put("end", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(ed)));
                    newList.add(map);
                    if (this_week == timer_week) {
                        calendar.setTime(now);
                        calendar.add(Calendar.DATE, 7);
                        map = new HashMap<String, Date>();
                        map.put("start", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(sd)));
                        if (sdf_hm_local.format(sd).compareTo(sdf_hm_local.format(ed)) >= 0) {
                            calendar.add(Calendar.DATE, 1);
                        }
                        map.put("end", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(ed)));
                        newList.add(map);
                    }
                    if ((timer_week - this_week) == -1) {
                        calendar.setTime(now);
                        calendar.add(Calendar.DATE, -1);
                        map = new HashMap<String, Date>();
                        map.put("start", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(sd)));
                        if (sdf_hm_local.format(sd).compareTo(sdf_hm_local.format(ed)) >= 0) {
                            calendar.add(Calendar.DATE, 1);
                        }
                        map.put("end", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(ed)));
                        newList.add(map);
                    }
                }
            }
            for (ACTimerTask task : timerTasks_1) {
                if (task.getTaskId() == taskIds[0] || task.getTaskId() == taskIds[1]) {
                    continue;
                }

                week_flg = false;
                String[] descriptions = task.getDescription().split(":");
                if (descriptions.length >= 3 && !descriptions[2].equals(String.valueOf(deviceId))) {
                    continue;
                }
                Date task_time = sdf_full_local.parse(task.getTimePoint());
                if (pairMap.get(descriptions[1]) != null) {
                    timerList = new ArrayList<HashMap<String, Date>>();
                    if (descriptions[0].equals("0")) {
                        task_st = task_time;
                        task_ed = sdf_full_local.parse(pairMap.get(descriptions[1]).getTimePoint());
                    } else {
                        task_st = sdf_full_local.parse(pairMap.get(descriptions[1]).getTimePoint());
                        task_ed = task_time;
                    }
                    if (task.getTimeCycle().equals("once")) {
                        HashMap<String, Date> exist_map = new HashMap<String, Date>();
                        exist_map.put("start", task_st);
                        exist_map.put("end", task_ed);
                        timerList.add(exist_map);
                    } else {
                        String[] cycles = task.getTimeCycle().substring(task.getTimeCycle().indexOf("[") + 1, task.getTimeCycle().indexOf("]")).split(",");
                        for (int i = 0; i < cycles.length; i++) {
                            calendar.setTime(now);
                            int this_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                            int timer_week = Integer.valueOf(cycles[i]);
                            calendar.add(Calendar.DATE, (timer_week - this_week + 7) % 7);
                            HashMap<String, Date> map = new HashMap<String, Date>();
                            map.put("start", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(task_st)));
                            if (sdf_hm_local.format(task_st).compareTo(sdf_hm_local.format(task_ed)) >= 0) {
                                calendar.add(Calendar.DATE, 1);
                            }
                            map.put("end", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(task_ed)));
                            timerList.add(map);
                            if (this_week == timer_week) {
                                calendar.setTime(now);
                                calendar.add(Calendar.DATE, 7);
                                map = new HashMap<String, Date>();
                                map.put("start", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(task_st)));
                                if (sdf_hm_local.format(task_st).compareTo(sdf_hm_local.format(task_ed)) >= 0) {
                                    calendar.add(Calendar.DATE, 1);
                                }
                                map.put("end", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(task_ed)));
                                timerList.add(map);
                            }
                            if ((timer_week - this_week) == -1) {
                                calendar.setTime(now);
                                calendar.add(Calendar.DATE, -1);
                                map = new HashMap<String, Date>();
                                map.put("start", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(task_st)));
                                if (sdf_hm_local.format(task_st).compareTo(sdf_hm_local.format(task_ed)) >= 0) {
                                    calendar.add(Calendar.DATE, 1);
                                }
                                map.put("end", sdf_full_local.parse(sdf_ymd_local.format(calendar.getTime()) + " " + sdf_hm_local.format(task_ed)));
                                timerList.add(map);
                            }
                        }
                    }
                    over_flg = checkOverlap(newList, timerList);
                    if (over_flg) {
                        break;
                    }
                } else if (descriptions.length > 1 && pairMap.get(descriptions[1]) == null) {
                    pairMap.put(descriptions[1], task);
                }

            }
        } catch (Exception e) {
            Log.e("sdf", e.toString());
        }

        return over_flg;
    }

    private boolean checkOverlap(List<HashMap<String, Date>> newList, List<HashMap<String, Date>> timerList) {
        boolean over_flg = false;
        try {
            for (HashMap<String, Date> new_timer : newList) {
                for (HashMap<String, Date> exist_timer : timerList) {
                    if (exist_timer.get("start").compareTo(new_timer.get("end")) <= 0 &&
                            exist_timer.get("end").compareTo(new_timer.get("start")) >= 0) {
                        over_flg = true;
                        break;
                    }
                }
                if (over_flg) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return over_flg;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        webViewExit();

    }

    private void webViewExit() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.setTag(null);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
        }
    }
}
