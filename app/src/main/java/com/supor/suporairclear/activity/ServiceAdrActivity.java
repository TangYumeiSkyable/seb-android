package com.supor.suporairclear.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.rihuisoft.loginmode.activity.ContactUsActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.Logger;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.ExternUriBean;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentEXTERNAL_URL;

/**
 * Created by enyva on 16/6/3.
 */
public class ServiceAdrActivity extends BaseActivity {

    private WebView mWebView;
    private WebSettings mWebSettings;
    private ACMsgHelper msgHelper = new ACMsgHelper();

    private JSONArray servicePointList = new JSONArray();

    private static String TAG = "ServiceAdrActivity";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_serviceadr);
            AppManager.getAppManager().addActivity(this);
            setNavBtn(R.drawable.ico_back, 0);
            initView();
            getUri();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getUri() {
        Single
                .create(new SingleOnSubscribe<ExternUriBean>() {
                    @Override
                    public void subscribe(final SingleEmitter<ExternUriBean> emitter) throws Exception {
                        DCPServiceUtils.syncContent(DCPServiceContentEXTERNAL_URL, new PayloadCallback<ACMsg>() {
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
                        mWebView.loadUrl(externUriBean.getObjects().get(0).getExternalLink());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * Controls the initialization
     */
    @SuppressLint("JavascriptInterface")
    private void initView() {

        try {
            setTitle(getString(R.string.airpurifier_more_show_aftersalesservicenetwork_text));
            mWebView = (WebView) findViewById(R.id.wb_serviceAdr);

            mWebSettings = mWebView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);
            mWebSettings.setBuiltInZoomControls(true);
            mWebSettings.setLightTouchEnabled(true);
            mWebSettings.setSupportZoom(true);
            mWebView.setHapticFeedbackEnabled(false);


            mWebView.addJavascriptInterface(this, "address");

            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {

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
                    Log.d(TAG, view.getProgress() + ":" + view.getProgress() + "onPageFinished URL" + url + "");
                    Log.e(TAG, "onPageFinished URL" + url);

                    dimissDialog();
                    super.onPageFinished(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "onPageStarted URL" + url);
                    Log.i(TAG, "onPageStarted view" + view.getUrl());
                    showDialog();
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    view.loadUrl(url);
                    Log.i(TAG, "shouldOverrideUrlLoading URL" + url);
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    // TODO Auto-generated method stub
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void call(String tel) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            //url:Uniform resource locator
            //uri:A uniform resource identifier (wider)
            tel = tel.replace("-", "");
            intent.setData(Uri.parse("tel:" + tel));
            //Open system dialer
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            //If you can back
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            switch (component) {
                case LEFT:
                    if (mWebView.canGoBack()) {
                        //If you can back
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    break;
                case RIGHT:
                    break;
                case RIGHT2:
                    break;
                case TITLE:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
