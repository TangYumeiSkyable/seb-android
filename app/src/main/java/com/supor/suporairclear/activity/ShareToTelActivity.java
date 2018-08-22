package com.supor.suporairclear.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.StringUtils;
import com.supor.suporairclear.config.Config;

/**
 * Created by enyva on 16/6/12.
 */
public class ShareToTelActivity extends BaseActivity implements View.OnClickListener {

    private EditText phoneNum;
    private Button btn_sure;
    private long deviceId;
    private String userPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_sharetotel);
            AppManager.getAppManager().addActivity(this);
            setTitle(R.string.airpurifier_moredevice_show_phoneshare_text);
            setNavBtn(R.drawable.ico_back, null, 0, null);
            findById();
            deviceId = getIntent().getLongExtra("deviceId", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Controls the initialization
     */
    private void findById() {
        try {
            phoneNum = (EditText) findViewById(R.id.edt_tel);
            phoneNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                     userPhoneNum = phoneNum.getText().toString();
                    if (userPhoneNum.length() > 0 && StringUtils.isEmail(userPhoneNum)) {
                        btn_sure.setEnabled(true);
                    } else{
                        btn_sure.setEnabled(false);
                    }
                }

            });
            btn_sure = (Button) findViewById(R.id.btn_sure);
            btn_sure.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_sure:
                    try {
                        AC.bindMgr().bindDeviceWithUser(Config.SUBMAJORDOMAIN,
                                deviceId, userPhoneNum, new VoidCallback() {
                                    @Override
                                    public void success() {
                                        ShowToast(getString(R.string.airpurifier_moredevice_show_sharesuccessful_text));
                                        setResult(200);
                                        finish();
                                    }

                                    @Override
                                    public void error(ACException e) {
                                        if (e.getErrorCode() == 6007) {
                                            ShowToast(getString(R.string.airpurifier_moredevice_show_usernotexist_text));
                                            return;
                                        } else {
                                            ShowToast(getString(R.string.airpurifier_moredevice_show_devicesharefailed_text));
                                        }
                                    }
                                });

                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.v("exception", e.getMessage());
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
        }

    }
}
