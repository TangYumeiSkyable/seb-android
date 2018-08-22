package com.supor.suporairclear.activity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.utils.ToastUtil;
import com.supor.suporairclear.config.DCPServiceUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentCookies;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentManual;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentPersonalData;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentTypeLegalNotice;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentTypeTermOfUse;

/**
 * Created by emma on 2017/5/10.
 */

public class IFUActivity extends BaseActivity {
    private DCPServiceUtils.DCPServiceContentType type;
    private FrameLayout mWebviewContainer;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_container);
        initView();
    }

    /**
     * Controls the initialization
     */
    private void initView() {
        mWebviewContainer = (FrameLayout) findViewById(R.id.webview_container);
        if (mWebView == null) {
            mWebView = new WebView(this);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mWebviewContainer.addView(mWebView, layoutParams);
        }

        String titlebar = getIntent().getStringExtra("titlebar");
        String data = getIntent().getStringExtra("data");
        setTitle(titlebar);
        setNavBtn(R.drawable.ico_back, 0);
        if (titlebar == null || data == null || titlebar.equals("") || data.equals("")) {
            return;
        } else {
            if (data.equals("11")) {
                type = DCPServiceContentManual;
                getData(type);
            } else {
                try {
                    if (data.equals("1")) {
                        type = DCPServiceContentPersonalData;
                    } else if (data.equals("2")) {
                        type = DCPServiceContentTypeLegalNotice;
                    } else if (data.equals("3")) {
                        type = DCPServiceContentCookies;
                    } else if (data.equals("4")) {
                        type = DCPServiceContentTypeTermOfUse;
                    }
                    getData(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void getData(DCPServiceUtils.DCPServiceContentType type) {
        showDialog();
        DCPServiceUtils.syncContent(type, new PayloadCallback<ACMsg>() {
            @Override
            public void error(ACException e) {
                ToastUtil.showToast(IFUActivity.this, getString(R.string.airpurifier_public_fail));
                dimissDialog();
            }

            @Override
            public void success(ACMsg acMsg) {
                dimissDialog();
                HashMap<String, Object> objectData = acMsg.getObjectData();
                ACObject content = (ACObject) acMsg.getObjectData().get("content");
                Object o = ((ACObject) acMsg.getObjectData().get("content")).get("objects");

                if (acMsg != null
                        && objectData != null
                        && content != null
                        && o != null
                        && mWebView != null) {

                    ArrayList objects = (ArrayList) o;
                    String body = ((ACObject) objects.get(0)).get("body");
                    mWebView.loadData(body, "text/html", "UTF-8");
                } else {
                    ToastUtil.showToast(IFUActivity.this, getString(R.string.airpurifier_public_fail));
                }
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
