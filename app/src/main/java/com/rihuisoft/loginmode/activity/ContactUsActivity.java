package com.rihuisoft.loginmode.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.google.gson.Gson;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentAfterSales;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentHOTLINE;

/**
 * Created by Administrator on 2017/4/28.
 */

public class ContactUsActivity extends BaseActivity {

    private TextView mTvTel;
    private FrameLayout mWebviewContainer;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contactus);
            initView();
            setTitle(getString(R.string.airpurifier_more_contact_us));
            setNavBtn(R.drawable.ico_back, 0);

            mWebviewContainer = (FrameLayout) findViewById(R.id.webview_container);
            if (mWebView == null) {
                mWebView = new WebView(this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mWebviewContainer.addView(mWebView, layoutParams);
            }

            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        Single
                .create(new SingleOnSubscribe<String>() {
                    @Override
                    public void subscribe(final SingleEmitter<String> emitter) throws Exception {
                        DCPServiceUtils.syncContent(DCPServiceContentAfterSales, new PayloadCallback<ACMsg>() {
                            @Override
                            public void error(ACException e) {
                                if (!emitter.isDisposed())
                                    emitter.onError(e);
                            }

                            @Override
                            public void success(ACMsg acMsg) {
                                ArrayList objects = (ArrayList) ((ACObject) acMsg.getObjectData().get("content")).get("objects");
                                String body = ((ACObject) objects.get(0)).get("body");
                                if (body != null) {
                                    emitter.onSuccess(body);
                                } else {
                                    emitter.onError(new IllegalArgumentException());
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mWebView.loadData(s, "text/html", "UTF-8");
                    }
                })
                .observeOn(Schedulers.newThread())
                .flatMap(new Function<String, SingleSource<String>>() {
                    @Override
                    public SingleSource<String> apply(String s) throws Exception {
                        return Single.create(new SingleOnSubscribe<String>() {
                            @Override
                            public void subscribe(final SingleEmitter<String> emitter) throws Exception {
                                DCPServiceUtils.syncContent(DCPServiceContentHOTLINE, new PayloadCallback<ACMsg>() {
                                    @Override
                                    public void error(ACException e) {
                                        if (!emitter.isDisposed())
                                            emitter.onError(e);
                                    }

                                    @Override
                                    public void success(ACMsg acMsg) {
                                        try {
                                            ArrayList objects = (ArrayList) ((ACObject) acMsg.getObjectData().get("content")).get("objects");
                                            String body = ((ACObject) objects.get(0)).get("body");
                                            if (!emitter.isDisposed())
                                                if (body != null) {
                                                    emitter.onSuccess(body);
                                                } else {
                                                    emitter.onError(new IllegalArgumentException());
                                                }
                                        } catch (Exception e) {
                                            if (!emitter.isDisposed())
                                                emitter.onError(e);
                                        }
                                    }
                                });
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String body) throws Exception {
                        mTvTel.setText(Html.fromHtml(body));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }

    /**
     * Controls the initialization
     */
    private void initView() {
        mTvTel = (TextView) findViewById(R.id.tv_tel);
        mTvTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTelDialog();
            }
        });
    }

    private void showTelDialog() {
        new AlertDialogUserDifine(ContactUsActivity.this).builder()
                .setTitle(String.format(getString(R.string.airpurifier_more_show_callconfirm_text), mTvTel.getText().toString()))
                .setPositiveButton(getString(R.string.airpurifier_public_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + mTvTel.getText().toString().replaceAll(" ", "")));
                        startActivity(intent);

                    }
                }).setNegativeButton(getString(R.string.airpurifier_more_show_cancel_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case LEFT:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
