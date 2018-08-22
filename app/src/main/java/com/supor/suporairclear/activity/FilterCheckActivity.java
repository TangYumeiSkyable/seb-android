package com.supor.suporairclear.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.google.gson.Gson;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.Logger;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.ExternUriBean;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentAPPLIANCESHOP;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentEXTERNAL_URL;

/**
 * Created by enyva on 16/6/3.
 */
public class FilterCheckActivity extends BaseActivity {

    private WebView mWebView;
    private WebSettings mWebSettings;
    private Handler mHandler;
    // 0:filter  1:mall
    private int flag;

    private List<ACObject> filterList;
    private static String TAG = "FilterCheckActivity";
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private ACObject wordMap;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_filtercheck);
            AppManager.getAppManager().addActivity(this);
            flag = getIntent().getIntExtra("flag", 0);
            if (flag == 0) {
                setNavBtn(R.drawable.ico_back, 0);
                wordMap = new ACObject();
                wordMap.put("filter_condition", getString(R.string.airpurifier_more_show_filtercondition_tex));
                wordMap.put("pre_filter", getString(R.string.airpurifier_more_show_prefilter_tex));
                wordMap.put("active_filter", getString(R.string.airpurifier_more_show_activefilter_tex));
                wordMap.put("hepa_filter", getString(R.string.airpurifier_more_show_hepafilter_tex));
                wordMap.put("nano_filter", getString(R.string.airpurifier_more_show_nanofilter_tex));
                wordMap.put("to_buy", getString(R.string.airpurifier_more_show_tobuy_tex));
                wordMap.put("clean_filter", getString(R.string.airpurifier_more_cleanfilter_tex));
                getFilterStatus();
            } else {
                setNavBtn(R.drawable.ico_back, 0);
                loadData();
            }
            initWebView();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化WebView
     */
    private void initWebView() {
        mHandler = new Handler();
        mWebView = (WebView) findViewById(R.id.wb_filterState);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebView.setHapticFeedbackEnabled(false);
        mWebView.addJavascriptInterface(this, "filter");

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (flag == 0) {
                    setTitle(title);
                }
                Logger.i("webView---onReceivedTitle");
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                // TODO Auto-generated method stub
                if (consoleMessage.message().contains("Uncaught ReferenceError")) {
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (flag == 0) {
                    view.loadUrl("javascript:setLabel(" + wordMap.toString() + ")");
                    if (filterList.size() > 0 && flag == 0) {
                        view.loadUrl("javascript:setValue(" + filterList.toString() + ")");
                    }
                }
                Logger.i("webView---onPageFinished");
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                Logger.i("webView---onPageStarted");

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                Logger.i("webView---onPageStarted");
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
    }

    /**
     * Controls the initialization
     */
    @SuppressLint("JavascriptInterface")
    private void loadData() {
        if (flag == 0) {
            mWebView.loadUrl("file:///android_asset/filterstate.html");
        } else {
            showDialog();
            Single
                    .create(new SingleOnSubscribe<ExternUriBean>() {
                        @Override
                        public void subscribe(final SingleEmitter<ExternUriBean> emitter) throws Exception {
                            DCPServiceUtils.syncContent(DCPServiceContentAPPLIANCESHOP, new PayloadCallback<ACMsg>() {
                                @Override
                                public void error(ACException e) {
                                    if (!emitter.isDisposed())
                                        emitter.onError(e);
                                }

                                @Override
                                public void success(ACMsg acMsg) {
                                    String content = acMsg.getObjectData().get("content").toString();
                                    ExternUriBean externUriBean = new Gson().fromJson(content, ExternUriBean.class);
                                    if (externUriBean != null) {
                                        emitter.onSuccess(externUriBean);
                                    } else {
                                        emitter.onError(new IllegalArgumentException());
                                    }
                                }
                            });
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ExternUriBean>() {
                        @Override
                        public void accept(ExternUriBean externUriBean) throws Exception {
                            dimissDialog();
                            mWebView.loadUrl(externUriBean.getObjects().get(0).getExternalLink());
                            setTitle(externUriBean.getObjects().get(0).getTitle());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        }
    }

    @JavascriptInterface
    public void toSupor(String flg) {
        Intent intent = new Intent(FilterCheckActivity.this, FilterCheckActivity.class);
        intent.putExtra("flag", Integer.valueOf(flg));
        startActivity(intent);
    }


    private void getFilterStatus() {
        showDialog();
        msgHelper.queryAllFilterStatus(ConstantCache.deviceList, new PayloadCallback<List<ACObject>>() {

            @Override
            public void success(List<ACObject> result) {
                dimissDialog();
                try {
                    Log.d("getFilterStatus", result.toString());
                    filterList = result;
                    for (ACObject device : filterList) {
                        if (ConstantCache.deviceNameMap.get(device.getLong("deviceId")) != null) {
                            device.put("deviceName", ConstantCache.deviceNameMap.get(device.getLong("deviceId")) + " " + getString(R.string.airpurifier_more_show_filtercondition_content_key));
                        } else {
                            device.put("deviceName", getString(R.string.airpurifier_more_show_invaliddevice_text));
                        }
                    }
                    loadData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void error(ACException e) {
                dimissDialog();
                if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1 || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                        || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                        || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {
                    AppUtils.reLogin(FilterCheckActivity.this);
                }
                Log.e("queryAllFilterStatus", "error:" + e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView != null)
            if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case LEFT:
                finish();
                break;
            case RIGHT:
                break;
            default:
                break;
        }
    }
}
