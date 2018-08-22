package com.supor.suporairclear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.CommendInfo;

import java.text.SimpleDateFormat;


/**
 * Created by emma on 2017/5/18.
 */

public class SuggestopenairActivity extends BaseActivity {

    private SimpleDateFormat df;
    private TextView tv_pmitem;

    private TextView tv_pm;
    private ACMsgHelper msgHelper = new ACMsgHelper();

    private TextView tv_week;
    private String weekDate = null;
    private Button btn_openair;
    private Long deviceId;
    private String name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestopenairpurifier);
        AppManager.getAppManager().addActivity(this);
        setTitle(getString(R.string.airpurifier_more_show_openairpurifier_text));
        setNavBtn(R.drawable.ico_back, 0);
        initView();
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            if (component == TitleBar.LEFT) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controls the initialization
     */
    private void initView() {
        btn_openair = (Button) findViewById(R.id.btn_openair);
        tv_week = (TextView) findViewById(R.id.tv_week);
        tv_pm = (TextView) findViewById(R.id.tv_pm);
        tv_pm.setTypeface(AppConstant.ubuntuRegular);
        tv_pmitem = (TextView) findViewById(R.id.tv_pmitem);
        tv_pmitem.setTypeface(AppConstant.ubuntuMedium);
        btn_openair.setEnabled(false);

        Intent intent = getIntent();
        try {
            Long PM25 = intent.getExtras().getLong("PM25", 0l);
            Long hcho = intent.getExtras().getLong("hcho", 0l);
            deviceId = intent.getExtras().getLong("deviceId");
            Long createTimes = intent.getExtras().getLong("createTimes");
            Long powerFlg = intent.getExtras().getLong("powerFlg", 0l);
            String messageId = intent.getExtras().getString("messageId");


            tv_week.setText(AppUtils.getMsgTimeStr(createTimes));
            //当PM25的值大于100，字体显示为红色
            if (PM25 > 100) {
                tv_pmitem.setTextColor(getResources().getColor(R.color.red_text));
            } else {
                tv_pmitem.setTextColor(getResources().getColor(R.color.btn_orange));
            }
            tv_pmitem.setText(PM25 + "");

            getMessage(messageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        btn_openair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAirCleaner(deviceId);
            }
        });

    }


    public void getMessage(final String messageId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgHelper.queryMessage(messageId, new PayloadCallback<ACObject>() {
                    @Override
                    public void success(ACObject acObject) {
                        Long powerFlg = acObject.getLong("powerFlg");
                        if (powerFlg != 1) {
                            btn_openair.setEnabled(true);
                        }
                    }

                    @Override
                    public void error(ACException e) {
                        Log.e("queryMessage", "error:" + e.getErrorCode() + "-->" + e.getMessage());

                    }
                });
            }
        });
    }

    public void openAirCleaner(final Long deviceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommendInfo commend = new CommendInfo();
                commend.setDeviceId(deviceId);
                commend.setCommend("on_off");
                commend.setValue(1);

                msgHelper.controlDevice(commend, new VoidCallback() {
                    @Override
                    public void success() {
                        btn_openair.setEnabled(false);
                        Toast.makeText(SuggestopenairActivity.this, R.string.airpurifier_more_show_opensupotrsuccess_text,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void error(ACException e) {
                        if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1 || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                                || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {
                        }

                        if (e.getErrorCode() == 3807) {
                            Toast.makeText(
                                    SuggestopenairActivity.this,
                                    R.string.airpurifier_moredevice_show_devicenotonline_text,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(
                                    SuggestopenairActivity.this,
                                    R.string.airpurifier_moredevice_show_controlfailed_text,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }
}
