package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.activity.IFUActivity;

/**
 * Created by Administrator on 2017/4/28.
 */

public class UserInfomationActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mArUserGuide;
    private RelativeLayout mArFaq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_userinfomation);
            initView();
            setTitle(getString(R.string.airpurifier_more_userinfomation));
            setNavBtn(R.drawable.ico_back, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Controls the initialization
     */
    private void initView() {
        mArUserGuide = (RelativeLayout) findViewById(R.id.ar_userguide);
        mArFaq = (RelativeLayout) findViewById(R.id.ar_faq);

        mArUserGuide.setOnClickListener(this);
        mArFaq.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ar_userguide:
                Intent intent = new Intent(UserInfomationActivity.this, IFUActivity.class);
                intent.putExtra("titlebar", getString(R.string.airpurifier_more_userinfomation_user_guide));
                intent.putExtra("data", "11");
                startActivity(intent);
                break;
            case R.id.ar_faq:
                Intent intent2 = new Intent(UserInfomationActivity.this, FAQActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
