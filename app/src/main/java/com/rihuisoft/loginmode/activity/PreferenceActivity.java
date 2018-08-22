package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.activity.IFUActivity;
import com.supor.suporairclear.config.DCPServiceUtils;

import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentPersonalData;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentTypeTermOfUse;

/**
 * Created by Administrator on 2017/4/28.
 */

public class PreferenceActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout personaldata;
    private RelativeLayout rl_legalnotice;
    private RelativeLayout rl_uesofcookies;
    private RelativeLayout rl_termsofuse;
    private RelativeLayout rl_settings;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_preference);
            initView();
            setTitle(getString(R.string.airpurifier_more_show_preference_text)+"&"+getString(R.string.airpurifier_more_show_legal_text));
            setNavBtn(R.drawable.ico_back, 0);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Controls the initialization
     */
    private void initView() {
        personaldata= (RelativeLayout) findViewById(R.id.personaldata);
        rl_legalnotice= (RelativeLayout) findViewById(R.id.rl_legalnotice);
        rl_uesofcookies= (RelativeLayout) findViewById(R.id.rl_uesofcookies);
        rl_settings= (RelativeLayout) findViewById(R.id.rl_settings);
        rl_termsofuse= (RelativeLayout) findViewById(R.id.rl_termsofuse);

        personaldata.setOnClickListener(this);
        rl_legalnotice.setOnClickListener(this);
        rl_uesofcookies.setOnClickListener(this);
        rl_termsofuse.setOnClickListener(this);
        rl_settings.setOnClickListener(this);
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
        switch (v.getId()){

            case R.id.personaldata:

                Intent intent1 = new Intent(PreferenceActivity.this, IFUActivity.class);
                intent1.putExtra("titlebar",getString(R.string.airpurifier_more_show_personaldata_text));
                intent1.putExtra("data","1");
                startActivity(intent1);

                break;
            case R.id.rl_legalnotice:
                Intent intent2 = new Intent(PreferenceActivity.this, IFUActivity.class);
                intent2.putExtra("titlebar",getString(R.string.airpurifier_more_show_legalnotice_text));
                intent2.putExtra("data","2");
                startActivity(intent2);
                break;
            case R.id.rl_uesofcookies:
                Intent intent3 = new Intent(PreferenceActivity.this, CookiesActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_termsofuse:
                Intent intent4 = new Intent(PreferenceActivity.this, IFUActivity.class);
                intent4.putExtra("titlebar",getString(R.string.airpurifier_more_show_termsofuse_text));
                intent4.putExtra("data","4");
                startActivity(intent4);
                break;
            case R.id.rl_settings:
                try{
                    Intent  intent = new Intent(PreferenceActivity.this, SettingActivity.class);
//				intent.putExtra("flag", AppConstant.REGISTER);
                    startActivity(intent);
                }catch(Exception e){e.printStackTrace();}
                break;
        }
    }
}
