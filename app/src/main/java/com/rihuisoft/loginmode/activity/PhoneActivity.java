package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.net.Uri;
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

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.service.ACUserInfo;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.StringUtils;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.rihuisoft.loginmode.view.LoadingDialog;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.model.UserInfo;

import static com.supor.suporairclear.config.AppConstant.ubuntuRegular;


/**
 * Created by gxx on 2015/9/14.
 */
public class PhoneActivity extends BaseActivity {
    private TextView tv_phone_lbl;
    private TextView tv_vcode;
    private TextView tv_psd_lbl;

    private EditText phoneEditText;
    private EditText pwdEditText, confirmEditText;
    private Button next;
    private String phone;
    private String password;
    private ToggleButton show_psd, show_confirm_psd, tb_email;
    private ImageView del_confirm;
    private boolean sign_up_flg = false;

    ACAccountMgr acMgr;
    private ImageView mPasswordVisible;
    private ImageView mConfirmPasswordVisible;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            phone = getIntent().getStringExtra("phone");

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_phone);
            AppManager.getAppManager().addActivity(this);
            setNavBtn(R.drawable.ico_back, 0);

            acMgr = AC.accountMgr();
            mLoadingDialog = new LoadingDialog(this);
            setTitle(getString(R.string.airpurifier_register_show_registertext_title));
            MainApplication.getInstance().initLocation();
            initView();

        } catch (Exception e) {

        }
    }

    /**
     * In the early treatment
     */
    public void initView() {
        tv_phone_lbl = (TextView) findViewById(R.id.tv_phone_lbl);
        tv_phone_lbl.setTypeface(ubuntuRegular);
        tv_vcode = (TextView) findViewById(R.id.tv_vcode);
        tv_vcode.setTypeface(ubuntuRegular);
        tv_psd_lbl = (TextView) findViewById(R.id.tv_psd_lbl);
        tv_psd_lbl.setTypeface(ubuntuRegular);
        //设置密码可见与否
        mPasswordVisible = (ImageView) findViewById(R.id.iv_password_visible);
        mPasswordVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordVisible.setActivated(!mPasswordVisible.isActivated());
                pwdEditText.setTransformationMethod(mPasswordVisible.isActivated() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                Editable etext = pwdEditText.getText();
                Selection.setSelection(etext, etext.length());
            }
        });

        mConfirmPasswordVisible = (ImageView) findViewById(R.id.iv_confirm_password_visible);
        mConfirmPasswordVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmPasswordVisible.setActivated(!mConfirmPasswordVisible.isActivated());
                confirmEditText.setTransformationMethod(mConfirmPasswordVisible.isActivated() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                Editable etext = confirmEditText.getText();
                Selection.setSelection(etext, etext.length());
            }
        });

        //账号
        phoneEditText = (EditText) findViewById(R.id.phone);
        phoneEditText.setTypeface(AppConstant.ubuntuLight);
        phoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!phoneEditText.getText().toString().equals("")) {
                        del_confirm.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        //PSW
        pwdEditText = (EditText) findViewById(R.id.login_edit_pwd);
        pwdEditText.setTypeface(AppConstant.ubuntuLight);
        pwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    del_confirm.setVisibility(View.GONE);
                }
            }
        });
        //confirm PSW
        confirmEditText = (EditText) findViewById(R.id.confirm_pwd);
        confirmEditText.setTypeface(AppConstant.ubuntuLight);
        confirmEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    del_confirm.setVisibility(View.GONE);
                }
            }
        });

        del_confirm = (ImageView) findViewById(R.id.delete_confirm);
        next = (Button) findViewById(R.id.phone_next);
        next.setTypeface(ubuntuRegular);
        del_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneEditText.setText("");
                del_confirm.setVisibility(View.GONE);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!sign_up_flg) {
                    return;
                }
                password = pwdEditText.getText().toString();
                phone = phoneEditText.getText().toString().trim();
                String confirm = confirmEditText.getText().toString();

                if (phone.isEmpty()) {
                    return;
                }
                if (phone.length() == 0
                        || !StringUtils.isEmail(phone)) {
                    phoneEditText.startAnimation(shake);
                    ShowToast(getString(R.string.airpurifier_login_show_registertosinvalidphonenumber_text));
                    return;
                }
                if (password.isEmpty() || confirm.isEmpty()) {
                    return;
                }
                if (!password.equals(confirm)) {
                    ShowToast(getString(R.string.airpurifier_login_show_registercodenotconsistent_text));
                    return;
                }
                if (password.length() < 8
                        || !StringUtils.isAccountRegex(password)) {
                    pwdEditText.startAnimation(shake);
                    ShowToast(getString(R.string.airpurifier_login_show_registerentercode_text));
                    return;
                }

                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
                acMgr.checkExist(phone, new PayloadCallback<Boolean>() {
                    @Override
                    public void success(Boolean isExist) {
                        // TODO
                        if (isExist) {
                            if (mLoadingDialog != null) {
                                mLoadingDialog.dismiss();
                            }
                            // User already exists
                            ShowToast(getString(R.string.airpurifier_login_show_registertosphonealreadyregidter_text));
                            return;
                        }
                        hideSoftInput(next);
                        register(phone, phone, password, "");
                    }

                    @Override
                    public void error(ACException e) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                        if (e.getErrorCode() == 404
                                || e.getErrorCode() == ACException.INTERNET_ERROR
                                || e.getErrorCode() == 1999) {
                            ShowToast(getString(R.string.airpurifier_login_show_tosneterror_text));
                        } else {
                            ShowToast(getString(R.string.airpurifier_login_show_registertosphonealreadyregidter_text));
                        }
                    }
                });

            }
        });


        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                phoneEditText.setTypeface(AppConstant.ubuntuLight);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneEditText.setTypeface(AppConstant.ubuntuRegular);
            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneEditText.setTypeface(AppConstant.ubuntuMedium);
                if (!phoneEditText.getText().toString().isEmpty()) {
                    del_confirm.setVisibility(View.VISIBLE);
                } else {
                    del_confirm.setVisibility(View.GONE);
                }
                if (phoneEditText.getText().toString().isEmpty() || !StringUtils.isEmail(phoneEditText.getText().toString().trim()) || phoneEditText.getText().toString().trim().contains(" ")) {
                    if (tb_email.isChecked()) {
                        tb_email.setChecked(false);
                    }
                } else {
                    if (!tb_email.isChecked()) {
                        tb_email.setChecked(true);
                    }
                }
                sign_up_flg = show_confirm_psd.isChecked() && show_psd.isChecked() && tb_email.isChecked();

                if (sign_up_flg) {
                    next.setBackgroundResource(R.drawable.selectors_btn_button);
                } else {
                    next.setBackgroundResource(R.drawable.selectors_btn_disabled);
                }
            }
        });
        pwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                pwdEditText.setTypeface(AppConstant.ubuntuLight);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwdEditText.setTypeface(AppConstant.ubuntuRegular);
            }

            @Override
            public void afterTextChanged(Editable s) {
                pwdEditText.setTypeface(AppConstant.ubuntuMedium);
                if (pwdEditText.getText().toString().isEmpty() || !StringUtils.isAccountRegex(pwdEditText.getText().toString())) {
                    if (show_psd.isChecked()) {
                        show_psd.setChecked(false);
                        show_confirm_psd.setChecked(false);
                    }
                } else {
                    if (!show_psd.isChecked()) {
                        show_psd.setChecked(true);
                    }
                    if (!pwdEditText.getText().toString().isEmpty() && !confirmEditText.getText().toString().isEmpty()
                            && confirmEditText.getText().toString().equals(pwdEditText.getText().toString())) {
                        //	if (sign_up_flg && !show_confirm_psd.isChecked()) {
                        show_confirm_psd.setChecked(true);
                        //}
                    } else {
                        if (show_confirm_psd.isChecked()) {
                            show_confirm_psd.setChecked(false);
                        }
                    }

                }
                sign_up_flg = show_confirm_psd.isChecked() && show_psd.isChecked() && tb_email.isChecked();

                if (sign_up_flg) {
                    next.setBackgroundResource(R.drawable.selectors_btn_button);
                } else {
                    next.setBackgroundResource(R.drawable.selectors_btn_disabled);
                }
            }
        });

        confirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmEditText.setTypeface(AppConstant.ubuntuLight);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmEditText.setTypeface(AppConstant.ubuntuRegular);
            }

            @Override
            public void afterTextChanged(Editable s) {

                confirmEditText.setTypeface(AppConstant.ubuntuMedium);
                if (!pwdEditText.getText().toString().isEmpty() && !confirmEditText.getText().toString().isEmpty() && StringUtils.isAccountRegex(confirmEditText.getText().toString()) == true
                        && confirmEditText.getText().toString().equals(pwdEditText.getText().toString())) {
                    //	if (sign_up_flg && !show_confirm_psd.isChecked()) {
                    show_confirm_psd.setChecked(true);
                    //}
                } else {
                    if (show_confirm_psd.isChecked()) {
                        show_confirm_psd.setChecked(false);
                    }
                }
                sign_up_flg = show_confirm_psd.isChecked() && show_psd.isChecked() && tb_email.isChecked();

                if (sign_up_flg) {
                    next.setBackgroundResource(R.drawable.selectors_btn_button);
                } else {
                    next.setBackgroundResource(R.drawable.selectors_btn_disabled);
                }

            }
        });


        show_confirm_psd = (ToggleButton) findViewById(R.id.show_confirm_password);
        show_psd = (ToggleButton) findViewById(R.id.show_password);
        tb_email = (ToggleButton) findViewById(R.id.tb_email_checked);

        next.setBackgroundResource(R.drawable.selectors_btn_disabled);
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            switch (component) {
                case LEFT:
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * User registration
     *
     * @param mail
     * @param name
     * @param pwd
     * @param code
     */
    private void register(String mail, String name, String pwd, String code) {
        next.setClickable(false);
        DCPServiceUtils.register(name, mail, pwd, code,
                new PayloadCallback<ACUserInfo>() {
                    @Override
                    public void success(ACUserInfo userInfo) {
                        try {
                            if (mLoadingDialog != null) {
                                mLoadingDialog.dismiss();
                            }
                            // TODO popWindow
                            next.setEnabled(true);
                            MainApplication.getInstance();
                            // TODO
                            MainApplication.UserLogin(new UserInfo(userInfo
                                    .getUserId(), userInfo.getName(), phone));
                            ShowToast(getString(R.string.airpurifier_login_show_tosregistsuccessful_text));
                            setUserInfo();
                            Intent intent = new Intent(
                                    PhoneActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void error(ACException e) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                        next.setClickable(true);


                        if (e.getErrorCode() == 5000) {
                            //UDS注册失败
                            ShowToast(getString(R.string.airpurifier_regist_fail));
                        } else if (e.getErrorCode() == 5001) {
                            //UDS登录失败 弹框 ok ->登录  cancel -> 当前页
                            showAlertDialog();
                        } else if (e.getErrorCode() == 3502 ) {
                            ShowToast(getString(R.string.airpurifier_login_show_registertosphonealreadyregidter_text));
                        }  else if (e.getErrorCode() == 2015) {
                            showTipDialog();
                        } else {
                            ShowToast(getString(R.string.airpurifier_public_fail));
                        }
                    }
                });

    }

    private void showTipDialog() {
        new AlertDialogUserDifine(PhoneActivity.this).builder()
                .setMsg(getString(R.string.password_not_secured_enough))
                .setPositiveButton(getString(R.string.airpurifier_public_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    private void showAlertDialog() {
        new AlertDialogUserDifine(PhoneActivity.this).builder()
                .setTitle(getString(R.string.airpurifier_regist_login_fail))
                .setPositiveButton(getString(R.string.airpurifier_public_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppManager.getAppManager().finishActivity(PhoneActivity.class);
                        AppManager.getAppManager().finishActivity(AgreementActivity.class);

                    }
                }).setNegativeButton(getString(R.string.airpurifier_more_show_cancel_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    /**
     * Set user extended attributes
     */
    public void setUserInfo() {
        try {
            ACObject userProfile = new ACObject();
            //Note here to put in the key and the value of type corresponding to the new extended attributes when fill in the properties of the logo with the attribute type
            userProfile.put("notifyFlg1", false);
            userProfile.put("notifyFlg2", false);
            userProfile.put("notifyFlg3", false);
            userProfile.put("notifyFlg4", false);
            acMgr.setUserProfile(userProfile, new VoidCallback() {
                @Override
                public void success() {

                }

                @Override
                public void error(ACException e) {
                    //Network error, or other, according to the um participant etErrorCode () do different tip or processing
                    Log.e("setUserProfile", "error:" + e.getErrorCode() + "-->" + e.getMessage());
//                    ShowToast("error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
