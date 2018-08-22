package com.supor.suporairclear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.ListView;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.CalendarUtils;
import com.supor.suporairclear.adapter.MessageItemAdapter;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.controller.SendToDevice;
import com.supor.suporairclear.model.Replacementremind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by enyva on 16/6/3.
 */
public class MessageListActivity extends BaseActivity {
    private Replacementremind replacementremind = new Replacementremind();

    private ListView lv_msglist;
    private MessageItemAdapter messageItemAdapter;
    private int read[] = {R.drawable.read, R.drawable.unread};

    private Handler mHandler;
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private SendToDevice sendToDevice;
    private JSONArray messageList = new JSONArray();
    private ACObject message;
    private static String TAG = "MessageListActivity";
    private boolean isTrue = false;


    private enum handler_key {
        GETMESSAGELIST_SUCCESS,
        GETMESSAGE_SUCCESS,
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
                handler_key key = handler_key.values()[msg.what];
                switch (key) {
                    case GETMESSAGELIST_SUCCESS:
                        try {
                            messageItemAdapter = new MessageItemAdapter(MessageListActivity.this, messageList);
                            lv_msglist.setAdapter(messageItemAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case USER_OVERDUE:
                        AppUtils.reLogin(MessageListActivity.this);
                        break;
                    case GETMESSAGE_SUCCESS:
                        if (!message.get("msgType").toString().equals("2")) {
                            Intent intent2 = new Intent(MessageListActivity.this, ReplacementremindActivity.class);
                            intent2.putExtra("deviceName", message.getString("deviceName"));
                            intent2.putExtra("deviceId", message.getString("deviceId"));
                            intent2.putExtra("initial_filter", message.getLong("initial_filter"));
                            intent2.putExtra("act_filter", message.getLong("act_filter"));
                            intent2.putExtra("HEPA_filter", message.getLong("HEPA_filter"));
                            intent2.putExtra("nano_filter", message.getLong("nano_filter"));
                            intent2.putExtra("text", message.getString("text"));
                            startActivity(intent2);
                        } else {
                            Intent intent = new Intent(MessageListActivity.this, SuggestopenairActivity.class);
                            intent.putExtra("deviceName", message.getString("deviceName"));
                            intent.putExtra("PM25", message.getLong("PM25"));
                            intent.putExtra("hcho", message.getLong("hcho"));
                            intent.putExtra("deviceId", message.getLong("deviceId"));
                            intent.putExtra("createTimes", message.getLong("createTime"));
                            if (message.get("powerFlg") != null) {
                                intent.putExtra("powerFlg", message.getLong("powerFlg"));
                            }
                            startActivity(intent);
                        }
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
        setContentView(R.layout.activity_messagelist);
        AppManager.getAppManager().addActivity(this);
        setTitle(getString(R.string.airpurifier_more_show_mynews_text));
        setNavBtn(R.drawable.ico_back, 0);
        initView();
    }

    private void initView() {
        List<String> list = new ArrayList<String>();
        lv_msglist = (ListView) findViewById(R.id.lv_msglist);

        lv_msglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isTrue == false) {
                    try {
                        setReadFlg(messageList.getJSONObject(position).getString("messageId"));
                        isTrue = true;

                        JSONObject jsonObject = messageList.getJSONObject(position);
                        String msgType = jsonObject.getString("msgType");
                        if (!msgType.equals("2")) {
                            Intent intent2 = new Intent(MessageListActivity.this, ReplacementremindActivity.class);
                            intent2.putExtra("deviceName", jsonObject.getString("devicename"));
                            intent2.putExtra("deviceId", jsonObject.getString("deviceId"));
                            intent2.putExtra("initial_filter", jsonObject.getLong("initial_filter"));
                            intent2.putExtra("act_filter", jsonObject.getLong("act_filter"));
                            intent2.putExtra("HEPA_filter", jsonObject.getLong("HEPA_filter"));
                            intent2.putExtra("nano_filter", jsonObject.getLong("nano_filter"));
                            intent2.putExtra("text", jsonObject.getString("text"));
                            startActivity(intent2);
                        } else {
                            Intent intent = new Intent(MessageListActivity.this, SuggestopenairActivity.class);
                            intent.putExtra("deviceName", jsonObject.getString("devicename"));
                            intent.putExtra("PM25", jsonObject.getLong("PM25"));
                            intent.putExtra("hcho", jsonObject.getLong("hcho"));
                            intent.putExtra("deviceId", jsonObject.getLong("deviceId"));
                            intent.putExtra("createTimes", jsonObject.getLong("createTimes"));
                            intent.putExtra("messageId", jsonObject.getString("messageId"));
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isTrue = false;
        getMessageList();
    }

    SimpleDateFormat sd = new SimpleDateFormat("MM-dd");
    String weekstr = "";

    //TODO
    @JavascriptInterface
    public void getMessageList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog();
                msgHelper.queryMessageList(new PayloadCallback<List<ACObject>>() {

                    @Override
                    public void success(List<ACObject> acObject) {
                        dimissDialog();
                        JSONObject map;
                        messageList = new JSONArray();
                        Log.i("sss", acObject.toString());
                        for (ACObject item : acObject) {
                            try {
                                map = new JSONObject();
                                if (ConstantCache.deviceNameMap.get(item.getLong("deviceId")) == null) {
                                    continue;
                                }
                                map.put("userId", item.getLong("userId"));
                                Calendar cal = CalendarUtils.zeroFromHour(item.getLong("createTime"));
                                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                                    weekstr = "Mon";
                                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                                    weekstr = "Tues";
                                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                                    weekstr = "Wed";
                                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                                    weekstr = "Thur";
                                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                                    weekstr = "Fri";
                                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                    weekstr = "Sat";
                                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                    weekstr = "Sun";
                                }
                                map.put("createTime", sd.format(item.getLong("createTime")) + " " + weekstr);
                                map.put("createTimes", item.getString("createTime"));
                                map.put("messageId", item.getString("messageId"));
                                map.put("msgType", item.get("msgType").toString());
                                map.put("deviceId", item.getLong("deviceId"));
                                map.put("PM25", item.getLong("PM25"));
                                map.put("hcho", item.getLong("hcho"));
                                map.put("readFlag", item.getLong("readFlag"));
                                if (String.valueOf(item.getLong("readFlag")).equals("0")) {
                                    map.put("read", read[0]);
                                } else if (String.valueOf(item.getLong("readFlag")).equals("1")) {
                                    map.put("read", read[1]);
                                }
                                map.put("text", item.getString("text"));
                                map.put("suggest", item.getString("suggest"));
                                map.put("initial_filter", item.getLong("initial_filter"));
                                map.put("HEPA_filter", item.getLong("HEPA_filter"));
                                map.put("nano_filter", item.getLong("nano_filter"));
                                map.put("act_filter", item.getLong("act_filter"));
                                map.put("devicename", ConstantCache.deviceNameMap.get(item.getLong("deviceId")));

                                map.put("city", "Paris");
                                if (!item.getString("msgType").equals("2")) {
                                    String filterName = "";
                                    Long time = 0l;
                                    if (item.getString("text").contains("Pre-filter")) {
                                        filterName = "Pre-";
                                        time = item.getLong("initial_filter");
                                    } else if (item.getString("text").contains("HEPA filter")) {
                                        filterName = "HEPA ";
                                        time = item.getLong("HEPA_filter");
                                    } else if (item.getString("text").contains("Nano capture filter")) {
                                        filterName = "Nano capture ";
                                        time = item.getLong("nano_filter");
                                    } else if (item.getString("text").contains("Active carbon filter")) {
                                        filterName = "Active carbon ";
                                        time = item.getLong("act_filter");
                                    }
                                    map.put("title", getString(R.string.airpurifier_more_show_filtertitile_text));
                                    map.put("item", getString(R.string.airpurifier_more_show_filteritem_text) + " " + filterName + " " + getString(R.string.airpurifier_more_show_filteritemone_text) + " <f\n" +
                                            "                                 ont color=\"#36424a\">" + ConstantCache.deviceNameMap.get(item.getLong("deviceId")) + "</font>" + getString(R.string.airpurifier_more_show_filteritemtwo_text) + " " + time + " " + getString(R.string.airpurifier_more_show_filteritemthree_text));
                                } else if (item.getString("msgType").equals("2")) {
                                    map.put("title", getString(R.string.airpurifier_more_show_pmtitle_text));
//                                    map.put("item", getString(R.string.airpurifier_more_show_pmitem_text));
                                    map.put("item", getString(R.string.airpurifier_more_show_pmitem_text) + " " + ConstantCache.deviceNameMap.get(item.getLong("deviceId")) + " " + getString(R.string.airpurifier_more_show_pmitem_text2));
                                }

                                messageList.put(map);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(handler_key.GETMESSAGELIST_SUCCESS.ordinal());
                    }

                    @Override
                    public void error(ACException e) {
                        dimissDialog();
                        if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1 || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {
                            handler.sendEmptyMessage(handler_key.USER_OVERDUE.ordinal());
                        }
                        Log.e("queryMessageList", "error:" + e.getErrorCode() + "-->" + e.getMessage());

                    }
                });
            }
        });
    }

    @JavascriptInterface
    public void toSupor() {
        Intent intent = new Intent(this, FilterCheckActivity.class);
        intent.putExtra("flag", 1);
        startActivity(intent);
    }

    @JavascriptInterface
    public void setReadFlg(final String messageId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgHelper.updateMessageInfo(messageId, new VoidCallback() {
                    @Override
                    public void success() {
                        Log.d("updateMessageInfo", "success");
                    }

                    @Override
                    public void error(ACException e) {
                        if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1 || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {
                            handler.sendEmptyMessage(handler_key.USER_OVERDUE.ordinal());
                        }
                        Log.e("setReadFlg", "error:" + e.getErrorCode() + "-->" + e.getMessage());

                    }
                });
            }
        });
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        if (component == TitleBar.LEFT) {
            finish();
        }
    }
}
