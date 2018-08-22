package com.supor.suporairclear.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.accloud.utils.PreferencesUtils;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.crop.Crop;
import com.rihuisoft.loginmode.utils.AppManager;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.Config;
import com.supor.suporairclear.config.DCPServiceUtils;

/**
 * Created by enyva on 16/5/28.
 */
public class ModifyNameActivity extends BaseActivity implements View.OnClickListener{

    private EditText et_nickname;
    private Button btn_modify;
    private ImageView iv_delete;
    private ACAccountMgr accountMgr;
    private long flag,deviceId;
    private String deviceName,userName;
    private boolean modify_flg =false;
    private enum handler_key{
        CHANGENAME_SUCCESS,
        CHANGEDEVICENAME_SUCCESS,
    }

    /** The handler. */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            try {
                ModifyNameActivity.handler_key key = ModifyNameActivity.handler_key.values()[msg.what];
                Intent intent = null;
                switch (key) {
                    case CHANGENAME_SUCCESS:
                        MainApplication.mUser.setNickName(et_nickname.getText().toString());
                        intent = new Intent();
                        setResult(RESULT_OK, intent);
                        ModifyNameActivity.this.finish();
                        break;
                    case CHANGEDEVICENAME_SUCCESS:

                        intent =new Intent(ModifyNameActivity.this, MyDeviceActivity.class);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("deviceName", et_nickname.getText().toString());
                        setResult(1, intent);
                        finish();

                    default:
                        break;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_modifyname);
            AppManager.getAppManager().addActivity(this);
            accountMgr = AC.accountMgr();

            setNavBtn(R.drawable.ico_back, 0);
            init();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {

        try {
            switch (component) {
                case LEFT:
                    if (flag == 1) {
                        Intent intent =new Intent(ModifyNameActivity.this, MyDeviceActivity.class);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("deviceName", deviceName);
                        setResult(1, intent);
                    }
                    finish();
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if((keyCode== KeyEvent.KEYCODE_BACK)) {
                if (flag == 1) {
                    Intent intent =new Intent(ModifyNameActivity.this, MyDeviceActivity.class);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("deviceName", deviceName);
                    setResult(1, intent);
                }
                finish();
                return true ;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return super.onKeyDown(keyCode,event) ;
    }
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (et_nickname.getText().toString().isEmpty()) {
                    et_nickname.setTypeface(AppConstant.ubuntuLight);
                } else {
                    et_nickname.setTypeface(AppConstant.ubuntuMedium);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Controls the initialization
     */
    private void init() {

        try {
            flag = getIntent().getIntExtra("flag", 0);
//          ShowToast("flag"+flag);

            et_nickname = (EditText) findViewById(R.id.et_nickname);
            et_nickname.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
            btn_modify = (Button) findViewById(R.id.btn_modify);
            iv_delete = (ImageView) findViewById(R.id.iv_delete);
            iv_delete.setVisibility(View.GONE);

            et_nickname.setTypeface(AppConstant.ubuntuLight);
            et_nickname.addTextChangedListener(textWatcher);
//          btn_modify.setTypeface(AppConstant.ubuntuMedium);
            btn_modify.setBackgroundResource(R.drawable.selectors_btn_gray);

            btn_modify.setOnClickListener(this);

            if(flag == 1){
                deviceId = getIntent().getLongExtra("deviceId", 0);
                deviceName = getIntent().getStringExtra("deviceName");
                setTitle(getString(R.string.airpurifier_more_show_modifydevicename_title));
                et_nickname.setHint(getResources().getString(R.string.airpurifier_more_show_pleaseenterdevicename_text));
                //et_nickname.setText("");
            }else{
                userName = getIntent().getStringExtra("userName");
                setTitle(getString(R.string.airpurifier_more_show_modifyName_title));
                //et_nickname.setText(userName);
                //et_nickname.setText("");
            }
            iv_delete.setOnClickListener(this);
            et_nickname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (!et_nickname.getText().toString().isEmpty()) {
                            iv_delete.setVisibility(View.VISIBLE);
                            btn_modify.setBackgroundResource(R.drawable.selectors_btn_button);
                            modify_flg = true;
                        } else {
                            iv_delete.setVisibility(View.GONE);
                            btn_modify.setBackgroundResource(R.drawable.selectors_btn_gray);
                            modify_flg = false;
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        try
        {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.btn_modify:
                    if (!modify_flg) {
                        break;
                    }
                    if(flag == 0){
                        if(et_nickname.getText()!=null && !"".equals(et_nickname.getText()) ){
                            changeName(et_nickname.getText().toString());
                        } else if (et_nickname.getText().toString().length() > 12) {
                            ShowToast(getString(R.string.airpurifier_more_show_enternickname_text));
                        }
                        else{
                            ShowToast(getString(R.string.airpurifier_more_show_pleaseenternickname_text));
                        }
                    }else{
                        if(et_nickname.getText()!=null && !"".equals(et_nickname.getText()) ){
                            changeDeviceName(et_nickname.getText().toString());
                        }else{
                            ShowToast(getString(R.string.airpurifier_more_show_pleaseenterdevicename_text));
                        }
                    }
                    break;
                case R.id.iv_delete:
                    et_nickname.setText("");
                    iv_delete.setVisibility(View.GONE);
                    break;

            }
        } catch(Exception e)
        {

        }
    }
    private void changeName(final String name) {
        DCPServiceUtils.changeNickName(name, new PayloadCallback<ACMsg>(){
            @Override
            public void success(ACMsg msg) {
                PreferencesUtils.putString(ModifyNameActivity.this, "name", name);
                //Through the object. The toString () view extended attributes information
                handler.sendEmptyMessage(handler_key.CHANGENAME_SUCCESS.ordinal());
            }

            @Override
            public void error(ACException e) {
                Toast.makeText(ModifyNameActivity.this, R.string.airpurifier_more_show_virifynicknamefailed_text, AppConstant.TOAST_DURATION).show();
                //Network error, or other, according to the um participant etErrorCode () do different tip or processing
                Log.e("changeNickName", "error:" + e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    private void changeDeviceName(final String name) {
        AC.bindMgr().changeName(Config.SUBMAJORDOMAIN,deviceId,name, new VoidCallback(){
            @Override
            public void success() {
                //Through the object. The toString () view extended attributes information
                handler.sendEmptyMessage(handler_key.CHANGEDEVICENAME_SUCCESS.ordinal());
            }

            @Override
            public void error(ACException e) {
                Toast.makeText(ModifyNameActivity.this, R.string.airpurifier_more_show_virifynicknamefailed_text, AppConstant.TOAST_DURATION).show();
                //Network error, or other, according to the um participant etErrorCode () do different tip or processing
                Log.e("changeName", "error:" + e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

}

