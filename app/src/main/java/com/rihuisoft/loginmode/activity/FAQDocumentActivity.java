package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.config.Constants;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/28.
 */

public class FAQDocumentActivity extends BaseActivity {


    private FrameLayout mWebviewContainer;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_webview_container);
            initView();
            setNavBtn(R.drawable.ico_back, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Controls the initialization
     */
    private void initView() {

        Intent intent = getIntent();
        mWebviewContainer = (FrameLayout) findViewById(R.id.webview_container);
        if (mWebView == null) {
            mWebView = new WebView(this);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mWebviewContainer.addView(mWebView, layoutParams);
        }
        ACObject acObject = (ACObject) intent.getSerializableExtra(Constants.KEY_FAQBEAN);
        if (acObject != null) {
            setTitle(acObject.get("title").toString());
            ArrayList<ACObject> threeGradeContent = acObject.get("sonContents");
            String body = threeGradeContent.get(0).get("body").toString();
            mWebView.loadData(body, "text/html", "UTF-8");
        }
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
