package com.rihuisoft.loginmode.activity;

import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserInfo;
import com.accloud.utils.PreferencesUtils;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.StringUtils;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by emma on 2017/7/21.
 */

public class ChangepasswordActivity extends BaseActivity implements View.OnClickListener {
    private AutoRelativeLayout rl_phonerl;
    private Button phone_next;
    private EditText login_edit_pwd;
    private EditText confirm_pwd;
    private ToggleButton tb_email_checked;
    private ToggleButton show_password;
    private ToggleButton show_confirm_password;
    private boolean sign_up_flg = false;
    private ImageView del_confirm;
    private ImageView mPasswordVisible;
    private ImageView mConfirmPasswordVisible;

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            switch (component) {
                case LEFT:
                    finish();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        setTitle(getString(R.string.airpurifier_more_show_changepwdtitle_text));
        setNavBtn(R.drawable.ico_back, 0);
        initVivew();
    }

    private void initVivew() {
        rl_phonerl = (AutoRelativeLayout) findViewById(R.id.rl_phonerl);
        rl_phonerl.setVisibility(View.GONE);
        del_confirm = (ImageView) findViewById(R.id.delete_confirm);
        tb_email_checked = (ToggleButton) findViewById(R.id.tb_email_checked);
        show_password = (ToggleButton) findViewById(R.id.show_password);
        show_confirm_password = (ToggleButton) findViewById(R.id.show_confirm_password);
        login_edit_pwd = (EditText) findViewById(R.id.login_edit_pwd);
        confirm_pwd = (EditText) findViewById(R.id.confirm_pwd);
        phone_next = (Button) findViewById(R.id.phone_next);
        phone_next.setText(getString(R.string.airpurifier_more_show_changepwdtitle_text));
        phone_next.setOnClickListener(this);

        //设置密码可见与否
        mPasswordVisible = (ImageView) findViewById(R.id.iv_password_visible);
        mPasswordVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordVisible.setActivated(!mPasswordVisible.isActivated());
                login_edit_pwd.setTransformationMethod(mPasswordVisible.isActivated() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                Editable etext = login_edit_pwd.getText();
                Selection.setSelection(etext, etext.length());
            }
        });

        mConfirmPasswordVisible = (ImageView) findViewById(R.id.iv_confirm_password_visible);
        mConfirmPasswordVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmPasswordVisible.setActivated(!mConfirmPasswordVisible.isActivated());
                confirm_pwd.setTransformationMethod(mConfirmPasswordVisible.isActivated() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                Editable etext = confirm_pwd.getText();
                Selection.setSelection(etext, etext.length());
            }
        });


        del_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                del_confirm.setVisibility(View.GONE);
            }
        });
        login_edit_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    del_confirm.setVisibility(View.GONE);
                    if (!login_edit_pwd.getText().toString().equals("")) {
                    }

                }
            }
        });
        confirm_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    del_confirm.setVisibility(View.GONE);
                    if (!confirm_pwd.getText().toString().equals("")) {
                    }

                }
            }
        });
        login_edit_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                login_edit_pwd.setTypeface(AppConstant.ubuntuLight);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login_edit_pwd.setTypeface(AppConstant.ubuntuRegular);
            }

            @Override
            public void afterTextChanged(Editable s) {
                login_edit_pwd.setTypeface(AppConstant.ubuntuMedium);
                if (!login_edit_pwd.getText().toString().isEmpty()) {
                } else {
                }

                if (login_edit_pwd.getText().toString().isEmpty() || !StringUtils.isAccountRegex(login_edit_pwd.getText().toString())) {
                    if (show_password.isChecked()) {
                        show_password.setChecked(false);
                        show_confirm_password.setChecked(false);
                    }
                } else {
                    if (!show_password.isChecked()) {
                        show_password.setChecked(true);
                    }
                    if (!login_edit_pwd.getText().toString().isEmpty() && !show_confirm_password.getText().toString().isEmpty()
                            && show_confirm_password.getText().toString().equals(login_edit_pwd.getText().toString())) {
                        show_confirm_password.setChecked(true);
                    } else {
                        if (show_confirm_password.isChecked()) {
                            show_confirm_password.setChecked(false);
                        }
                    }

                }
                sign_up_flg = show_confirm_password.isChecked() && show_password.isChecked() && tb_email_checked.isChecked();

                if (sign_up_flg) {
                    phone_next.setBackgroundResource(R.drawable.selectors_btn_button);
                } else {
                    phone_next.setBackgroundResource(R.drawable.selectors_btn_disabled);
                }
            }
        });
        confirm_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirm_pwd.setTypeface(AppConstant.ubuntuLight);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirm_pwd.setTypeface(AppConstant.ubuntuRegular);
            }

            @Override
            public void afterTextChanged(Editable s) {
                confirm_pwd.setTypeface(AppConstant.ubuntuMedium);
                if (!confirm_pwd.getText().toString().isEmpty()) {
                } else {
                }


                if (!login_edit_pwd.getText().toString().isEmpty() && !confirm_pwd.getText().toString().isEmpty()
                        && confirm_pwd.getText().toString().equals(login_edit_pwd.getText().toString())) {
                    show_confirm_password.setChecked(true);
                } else {
                    if (show_confirm_password.isChecked()) {
                        show_confirm_password.setChecked(false);
                    }
                }
                sign_up_flg = show_confirm_password.isChecked() && show_password.isChecked() && show_confirm_password.isChecked();

                if (sign_up_flg) {
                    phone_next.setBackgroundResource(R.drawable.selectors_btn_button);
                } else {
                    phone_next.setBackgroundResource(R.drawable.selectors_btn_disabled);
                }

            }

        });
    }

    ;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_next:
                if (!sign_up_flg) {
                    return;
                }
                showDialog();
                DCPServiceUtils.updatePassword(PreferencesUtils.getString(ChangepasswordActivity.this, "telephoneNo", ""), confirm_pwd.getText().toString(), new PayloadCallback<ACUserInfo>() {
                    @Override
                    public void success(ACUserInfo acUserInfo) {
                        dimissDialog();
                        ShowToast(getResources().getString(R.string.airpurifier_personal_updatePassword_success));
                        finish();
                    }

                    @Override
                    public void error(ACException e) {
                        dimissDialog();
                        if(e.getErrorCode() == 2015){
                            showTipDialog();
                        }else {
                            ShowToast(getResources().getString(R.string.airpurifier_personal_updatePassword_error));
                        }
                    }
                });
                break;
        }
    }

    private void showTipDialog() {
        new AlertDialogUserDifine(ChangepasswordActivity.this).builder()
                .setMsg(getString(R.string.password_not_secured_enough))
                .setPositiveButton(getString(R.string.airpurifier_public_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }
}
