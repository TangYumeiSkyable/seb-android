package com.supor.suporairclear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.Constants;

import io.fabric.sdk.android.Fabric;

/**
 * Created by enyva on 16/6/3.
 */
public class AddDeviceActivity extends BaseActivity implements View.OnClickListener{

    private TextView[] tvs;
    private View rl_manual;
    private View rl_qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    	try {
    		 super.onCreate(savedInstanceState);
    	        setContentView(R.layout.activity_adddevice);
    	        AppManager.getAppManager().addActivity(this);
    	        setTitle(getString(R.string.airpurifier_moredevice_show_adddevice_text));
    	        setNavBtn(R.drawable.ico_back, 0);
    	        init();
    	} catch(Exception e) {
			e.printStackTrace();
		}      
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        if (component == TitleBar.LEFT) {
            finish();
        }
    }

    /**
     * Controls the initialization
     */
    private void init() {
    	try {
    		tvs = new TextView[]{(TextView) findViewById(R.id.tv_qrcode), (TextView) findViewById(R.id.tv_manual)};
            for (int i = 0; i < tvs.length; i++) {
                tvs[i].setTypeface(AppConstant.ubuntuRegular);
            }

            rl_manual = (View) findViewById(R.id.rl_manual);
            rl_qrcode = (View) findViewById(R.id.rl_qrcode);

            rl_manual.setOnClickListener(this);
            rl_qrcode.setOnClickListener(this);
    	} catch(Exception e) {
			e.printStackTrace();
		}
        

    }
    @Override
    public void onClick(View view) {
        try
        {
            Intent intent = null;
            switch (view.getId()) {
                case R.id.rl_manual:
                    try{
                        intent = new Intent(AddDeviceActivity.this, APAddDeviceActivity.class);
                        startActivity(intent);
                    }catch(Exception e){e.printStackTrace();}
                    break;
                case R.id.rl_qrcode:
                    try{
                        intent = new Intent(AddDeviceActivity.this, DeviceBindQRActivity.class);
                        intent.putExtra(Constants.ISAP, true);
                        startActivity(intent);
                    }catch(Exception e){e.printStackTrace();}
                    break;
            }
        } catch(Exception e)
        {

        }
    }

}

