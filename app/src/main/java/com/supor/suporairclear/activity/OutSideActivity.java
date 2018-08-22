package com.supor.suporairclear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by enyva on 16/6/3.
 */
public class OutSideActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_pointblueno2,iv_pointblueno3,iv_pointbluepm25,iv_pointbluepm10;
    private TextView tv_detailcontext,tv_detailtitle;
    private TextView tv_airlevel;
    private AutoRelativeLayout rl_more;
    private long deviceId;
    private TextView tv_no2, tv_o3, tv_pm25, tv_pm10, tv_no2_val, tv_o3_val, tv_pm25_val, tv_pm10_val, tv_city;
    private ImageView iv_air;
    private View al_city;
    private ACMsgHelper msgHelper = new ACMsgHelper();

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_no2:
            case R.id.tv_no2_val:
               tv_detailtitle.setText(getResources().getString(R.string.airpurifier_more_airquality_tvno2));
                tv_detailcontext.setText(getResources().getString(R.string.airpurifier_more_airquality_no2));
                iv_pointblueno2.setVisibility(View.VISIBLE);
                iv_pointblueno3.setVisibility(View.INVISIBLE);
                iv_pointbluepm25.setVisibility(View.INVISIBLE);
                iv_pointbluepm10.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_o3:
            case R.id.tv_o3_val:
                tv_detailtitle.setText(getResources().getString(R.string.airpurifier_more_airquality_tvo3));
                tv_detailcontext.setText(getResources().getString(R.string.airpurifier_more_airquality_o3));
                iv_pointblueno2.setVisibility(View.INVISIBLE);
                iv_pointblueno3.setVisibility(View.VISIBLE);
                iv_pointbluepm25.setVisibility(View.INVISIBLE);
                iv_pointbluepm10.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_pm25:
            case R.id.tv_pm25_val:
                tv_detailtitle.setText(getResources().getString(R.string.airpurifier_more_airquality_tvpm25));
                tv_detailcontext.setText(getResources().getString(R.string.airpurifier_more_airquality_pm25));
                iv_pointblueno2.setVisibility(View.INVISIBLE);
                iv_pointblueno3.setVisibility(View.INVISIBLE);
                iv_pointbluepm25.setVisibility(View.VISIBLE);
                iv_pointbluepm10.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_pm10:
            case R.id.tv_pm10_val:
                tv_detailtitle.setText(getResources().getString(R.string.airpurifier_more_airquality_tvpm10));
                tv_detailcontext.setText(getResources().getString(R.string.airpurifier_more_airquality_pm10));
                iv_pointblueno2.setVisibility(View.INVISIBLE);
                iv_pointblueno3.setVisibility(View.INVISIBLE);
                iv_pointbluepm25.setVisibility(View.INVISIBLE);
                iv_pointbluepm10.setVisibility(View.VISIBLE);
                break;
        }
    }

    private enum handler_key{
        GETDATA_SUCCESS,

        USER_OVERDUE
    }

    /** The handler. */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            try {
                handler_key key = handler_key.values()[msg.what];
                switch (key) {
                    case GETDATA_SUCCESS:
                        setView();
                        break;
                    case USER_OVERDUE:
                        AppUtils.reLogin(OutSideActivity.this);
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
        	setContentView(R.layout.activity_outside);

            AppManager.getAppManager().addActivity(this);
            setTitle(getString(R.string.airpurifier_outdoor_show_outdoortitle_text));
            setNavBtn(R.drawable.ico_back, 0);
            initView();
        } catch(Exception e) {
  			e.printStackTrace();
  		}        
    }


    /**
     * Controls the initialization
     */
    private void initView() {
    	try {
            iv_pointblueno2 = (ImageView) findViewById(R.id.iv_pointblueno2);
            iv_pointblueno3 = (ImageView) findViewById(R.id.iv_pointblueno3);
            iv_pointbluepm25 = (ImageView) findViewById(R.id.iv_pointbluepm25);
            iv_pointbluepm10 = (ImageView) findViewById(R.id.iv_pointbluepm10);
            tv_detailcontext = (TextView) findViewById(R.id.tv_detailcontext);
            tv_detailcontext.setText(getResources().getString(R.string.airpurifier_more_airquality_no2));
            tv_detailtitle = (TextView) findViewById(R.id.tv_detailtitle);
            tv_airlevel = (TextView) findViewById(R.id.tv_airlevel);
            deviceId = getIntent().getLongExtra("deviceId", 0);
            iv_air = (ImageView) findViewById(R.id.iv_air);
            tv_no2 = (TextView) findViewById(R.id.tv_no2);
            tv_o3 = (TextView) findViewById(R.id.tv_o3);
            tv_pm25 = (TextView) findViewById(R.id.tv_pm25);
            tv_pm10 = (TextView) findViewById(R.id.tv_pm10);
            tv_city = (TextView) findViewById(R.id.tv_city);

            tv_no2_val = (TextView) findViewById(R.id.tv_no2_val);
            tv_no2_val.setOnClickListener(this);
            tv_o3_val = (TextView) findViewById(R.id.tv_o3_val);
            tv_o3_val.setOnClickListener(this);
            tv_pm25_val = (TextView) findViewById(R.id.tv_pm25_val);
            tv_pm25_val.setOnClickListener(this);
            tv_pm10_val = (TextView) findViewById(R.id.tv_pm10_val);
            tv_pm10_val.setOnClickListener(this);

            tv_no2.setText(getResources().getString(R.string.airpurifier_more_airquality_tvno2));
            tv_no2.setOnClickListener(this);
            tv_o3.setText(getResources().getString(R.string.airpurifier_more_airquality_tvo3));
            tv_o3.setOnClickListener(this);
            tv_pm25.setText(getResources().getString(R.string.airpurifier_more_airquality_tvpm25));
            tv_pm25.setOnClickListener(this);
            tv_pm10.setText(getResources().getString(R.string.airpurifier_more_airquality_tvpm10));
            tv_pm10.setOnClickListener(this);
            tv_no2_val.setTypeface(AppConstant.ubuntuMedium);
            tv_o3_val.setTypeface(AppConstant.ubuntuMedium);
            tv_pm25_val.setTypeface(AppConstant.ubuntuMedium);
            tv_pm10_val.setTypeface(AppConstant.ubuntuMedium);
            rl_more = (AutoRelativeLayout) findViewById(R.id.rl_more);
            al_city = findViewById(R.id.al_city);
            rl_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OutSideActivity.this,AirQualityActivity.class);
                    startActivity(intent);
                }
            });
            al_city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OutSideActivity.this, CountryListActivity.class);
                    startActivity(intent);
                }
            });
    	} catch(Exception e) {
  			e.printStackTrace();
  		}   
    }
    @Override
    protected void onResume() {
        super.onResume();
        tv_city.setText(MainApplication.location_city);
        getWeather();
        handler.sendEmptyMessage(handler_key.GETDATA_SUCCESS.ordinal());
    }
    private void setView() {
        weatherInfo = ConstantCache.weatherInfo;
        if (weatherInfo.getInt("NO2") == 0) {
            tv_no2_val.setText("--");
        } else {
            tv_no2_val.setText(String.valueOf(weatherInfo.getInt("NO2")));
        }
        if (weatherInfo.getInt("O3") == 0) {
            tv_o3_val.setText("--");
        } else {
            tv_o3_val.setText(String.valueOf(weatherInfo.getInt("O3")));
        }
        if (weatherInfo.getInt("PM2_5") == 0) {
            tv_pm25_val.setText("--");
        } else {
            tv_pm25_val.setText(String.valueOf(weatherInfo.getInt("PM2_5")));
        }
        if (weatherInfo.getInt("PM10") == 0) {
            tv_pm10_val.setText("--");
        } else {
            tv_pm10_val.setText(String.valueOf(weatherInfo.getInt("PM10")));
        }
        if (weatherInfo.getInt("overall") < AppConstant.AIR_1) {
            iv_air.setImageResource(R.drawable.ico_air_01);
            tv_airlevel.setText(getResources().getString(R.string.airpurifier_more_airquality_fresh));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_2) {
            iv_air.setImageResource(R.drawable.ico_air_02);
            tv_airlevel.setText(getResources().getString(R.string.airpurifier_more_airquality_moderate));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_3) {
            iv_air.setImageResource(R.drawable.ico_air_03);
            tv_airlevel.setText(getResources().getString(R.string.airpurifier_more_airquality_high));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_4) {
            iv_air.setImageResource(R.drawable.ico_air_04);
            tv_airlevel.setText(getResources().getString(R.string.airpurifier_more_airquality_very));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_5) {
            iv_air.setImageResource(R.drawable.ico_air_05);
            tv_airlevel.setText(getResources().getString(R.string.airpurifier_more_airquality_excessive));
        } else if (weatherInfo.getInt("overall") < AppConstant.AIR_6) {
            iv_air.setImageResource(R.drawable.ico_air_06);
            tv_airlevel.setText(getResources().getString(R.string.airpurifier_more_airquality_extreme));
        } else {
            iv_air.setImageResource(R.drawable.ico_air_07);
            tv_airlevel.setText(getResources().getString(R.string.airpurifier_more_airquality_airpocalypse));
        }
    }
    private ACObject weatherInfo;
    private void getWeather() {
        msgHelper.queryWeather(MainApplication.location_city, MainApplication.latitude, MainApplication.longitude,
                new PayloadCallback<ACObject>() {
                    @Override
                    public void success(ACObject acObject) {
                        weatherInfo = acObject;
                        ConstantCache.weatherInfo = acObject;
                        handler.sendEmptyMessage(handler_key.GETDATA_SUCCESS
                                .ordinal());
                    }

                    @Override
                    public void error(ACException e) {
                        if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_4|| e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {

                            handler.sendEmptyMessage(handler_key.USER_OVERDUE
                                    .ordinal());
                        }
                        Log.e("LocalPM25", "error:" + e.getErrorCode() + "-->"
                                + e.getMessage());
                    }
                });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode,event) ;
    }
    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
    	try {
    		switch (component) {
            case LEFT:
               finish();
                break;
            case RIGHT:
                break;
            case RIGHT2:
                break;
            case TITLE:
                break;
        }
    	} catch(Exception e) {
  			e.printStackTrace();
  		}   
        
    }
}
