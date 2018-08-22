package com.rihuisoft.loginmode.activity;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.service.ACUserInfo;
import com.accloud.utils.PreferencesUtils;
import com.crashlytics.android.Crashlytics;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.StringUtils;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.rihuisoft.loginmode.view.CustomProgressDialog;
import com.supor.suporairclear.activity.MainActivity;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.model.UserInfo;
import com.supor.suporairclear.service.LocationService;

import java.net.HttpURLConnection;

import io.fabric.sdk.android.Fabric;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.supor.suporairclear.config.AppConstant.ubuntuLight;
import static com.supor.suporairclear.config.AppConstant.ubuntuRegular;

@TargetApi(23)
public class LoginActivity extends Activity implements OnClickListener, View.OnLayoutChangeListener {
    private AlertDialogUserDifine ad;
    private TextView tv_email;
    private TextView tv_password;
    private Toast mToast;
    private EditText phoneEditText;
    private EditText pwdEditText;
    private TextView forgetPwd;
    private TextView register;
    private Button loginBtn;
    private ImageView iv_change_mode;
    private View ll_logo;

    private long lastTime;

    private CustomProgressDialog p = null;
    private static String tel;
    private static String pwd;
    private boolean login_flg = false, psdCloseFlg = true;
    ACAccountMgr accountMgr;
    //The Activity of the outermost Layout view
    private com.zhy.autolayout.AutoLinearLayout activityRootView;
    //The screen height
    private int screenHeight = 0;
    //After the software set up accounts for a high threshold
    private int keyHeight = 0;
    private String email;
    private static final String permission = ACCESS_FINE_LOCATION;
    private static final int REQUEST_GMS = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            AppManager.getAppManager().addActivity(this);
            AppUtils.setLopStatBar(this, getResources().getColor(R.color.dark_blue));
            email = getIntent().getStringExtra("email");
            initView();
            accountMgr = AC.accountMgr();

            activityRootView = (com.zhy.autolayout.AutoLinearLayout) findViewById(R.id.root_layout);
            screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
            keyHeight = screenHeight / 3;

            IntentFilter filter = new IntentFilter();
            filter.addAction(AppConstant.LOCATION_ACTION);
            populateAutoComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controls the initialization
     */
    public void initView() {
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_email.setTypeface(ubuntuRegular);
        tv_password = (TextView) findViewById(R.id.tv_password);
        tv_password.setTypeface(ubuntuRegular);
        ll_logo = findViewById(R.id.ll_logo);
        phoneEditText = (EditText) findViewById(R.id.login_edit_phone);
        phoneEditText.setTypeface(ubuntuLight);

        pwdEditText = (EditText) findViewById(R.id.login_edit_pwd);
        pwdEditText.setTypeface(ubuntuLight);

        forgetPwd = (TextView) findViewById(R.id.forgetPwd);
        forgetPwd.setTypeface(ubuntuRegular);
        register = (TextView) findViewById(R.id.register);
        register.setTypeface(ubuntuRegular);
        loginBtn = (Button) findViewById(R.id.login);
        loginBtn.setTypeface(ubuntuRegular);
        iv_change_mode = (ImageView) findViewById(R.id.iv_change_psd);

        forgetPwd.setOnClickListener(this);
        register.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        iv_change_mode.setOnClickListener(this);

        phoneEditText.addTextChangedListener(textWatcher);
        pwdEditText.addTextChangedListener(textWatcher);
        phoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    if (!StringUtils.isEmail(phoneEditText.getText().toString())) {
                        ShowToast(getString(R.string.airpurifier_login_show_invalidmail_toast));
                    }
                    ll_logo.setVisibility(View.VISIBLE);
                }
            }
        });
        pwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    ll_logo.setVisibility(View.VISIBLE);
                }
            }
        });

        loginBtn.setBackgroundResource(R.drawable.selectors_btn_disabled);
        popCookies();
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            phoneEditText.setTypeface(AppConstant.ubuntuLight);
            pwdEditText.setTypeface(AppConstant.ubuntuLight);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            phoneEditText.setTypeface(AppConstant.ubuntuRegular);
            pwdEditText.setTypeface(AppConstant.ubuntuRegular);
        }

        @Override
        public void afterTextChanged(Editable s) {
            phoneEditText.setTypeface(AppConstant.ubuntuMedium);
            pwdEditText.setTypeface(AppConstant.ubuntuMedium);
            try {
                if (phoneEditText.getText().toString().isEmpty() || !StringUtils.isEmail(phoneEditText.getText().toString())) {
                    loginBtn.setBackgroundResource(R.drawable.selectors_btn_disabled);
                    login_flg = false;
                } else if (pwdEditText.getText().toString().isEmpty()) {
                    loginBtn.setBackgroundResource(R.drawable.selectors_btn_disabled);
                    login_flg = false;
                } else {
                    loginBtn.setBackgroundResource(R.drawable.selectors_btn_button);
                    login_flg = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ad.dismiss();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MainApplication.getInstance().initLocation();
        String check = PreferencesUtils.getString(LoginActivity.this, "cookieCheck", "");
        if (!(check.equals("Checked") || check.equals("unChecked"))) {
            if (!ad.isShow()) {
                ad.show();
            }
            return;
        }

        try {
            if (PreferencesUtils.getString(LoginActivity.this, "telephoneNo", "") != null &&
                    !PreferencesUtils.getString(LoginActivity.this, "telephoneNo", "").equals("")) {
                phoneEditText.setText(PreferencesUtils.getString(LoginActivity.this, "telephoneNo", ""));
            }
            if (email != null) {
                phoneEditText.setText(email);
            }

            if (accountMgr.isLogin()) {
                checkUserInfo();
                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        activityRootView.getWindowVisibleDisplayFrame(rect);
                        int rootInvisibleHeight = activityRootView.getRootView().getHeight() - rect.bottom;
                        Log.d("TAG", "lin.getRootView().getHeight()=" + activityRootView.getRootView().getHeight() + ",rect.bottom=" + rect.bottom + ",rootInvisibleHeight=" + rootInvisibleHeight);
                        if (rootInvisibleHeight <= 150) {
                            ll_logo.setVisibility(View.VISIBLE);
                        } else {
                            ll_logo.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateAutoComplete() {
        if (PreferencesUtils.getString(MainApplication.getInstance(), "city") != null || !mayRequestContacts()) {
            return;
        }
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
    }

    private boolean mayRequestContacts() {
        boolean result = true;
        int targetSdkVersion = getApplicationInfo().targetSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                result = checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                result = PermissionChecker.checkSelfPermission(this, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
            if (!result) {
//                requestPermissions(new String[]{permission}, REQUEST_GMS);
            }
        }

        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private void popCookies() {
        try {
            ad = new AlertDialogUserDifine(this);
            ad.builder().setTitle(getString(R.string.airpurifier_login_cookies_title_android)).setMsg(getString(R.string.airpurifier_login_cookies_msg_android))
                    .setPositiveButton(getString(R.string.airpurifier_public_ok), new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PreferencesUtils.putString(MainApplication.getInstance(), "cookieCheck", "Checked");
                        }
                    }).setNegativeButton(getString(R.string.airpurifier_login_show_more_text), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, CookiesActivity.class);
                    startActivity(intent);
                }
            }, false).setCancelable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            ll_logo.setVisibility(View.VISIBLE);
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            ll_logo.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View view) {
        try {
            Intent intent = null;
            switch (view.getId()) {
                case R.id.forgetPwd:
                    try {
                        intent = new Intent(LoginActivity.this, PasswordSetActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.register:
                    intent = new Intent(LoginActivity.this, AgreementActivity.class);
                    startActivity(intent);
                    break;
                case R.id.login:
                    if (!login_flg) {
                        break;
                    }
                    try {
                        tel = phoneEditText.getText().toString().trim();
                        pwd = pwdEditText.getText().toString();

                        p = CustomProgressDialog.createDialog(LoginActivity.this);
                        p.setMessage(getString(R.string.airpurifier_login_show_toslogining_text));
                        p.show();
                        login();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.iv_change_psd:
                    psdCloseFlg = !psdCloseFlg;
                    if (psdCloseFlg) {
                        iv_change_mode.setImageResource(R.drawable.ico_eye_close);
                        pwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        iv_change_mode.setImageResource(R.drawable.ico_eye_open);
                        pwdEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    Editable etext = pwdEditText.getText();
                    Selection.setSelection(etext, etext.length());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set user extended attributes
     */
    public void checkUserInfo() {
        try {
            accountMgr.getUserProfile(new PayloadCallback<ACObject>() {
                @Override
                public void success(ACObject object) {
                    try {
                        boolean flg = false;

                        Boolean notifyFlg1 = object.getBoolean("notifyFlg1");
                        Boolean notifyFlg2 = object.getBoolean("notifyFlg2");
                        Boolean notifyFlg3 = object.getBoolean("notifyFlg3");
                        Boolean notifyFlg4 = object.getBoolean("notifyFlg4");
                        String space = object.get("space");
                        if (object.get("notifyFlg1") == null) {
                            flg = true;
                            notifyFlg1 = true;
                        }
                        if (object.get("notifyFlg2") == null) {
                            flg = true;
                            notifyFlg2 = true;
                        }
                        if (object.get("notifyFlg3") == null) {
                            flg = true;
                            notifyFlg3 = true;
                        }
                        if (object.get("notifyFlg4") == null) {
                            flg = true;
                            notifyFlg4 = true;
                        }
                        if (flg) {
                            setUserInfo(notifyFlg1, notifyFlg2, notifyFlg3, notifyFlg4);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void error(ACException e) {
                    setUserInfo(true, true, true, true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Set user extended attributes
     */
    public void setUserInfo(boolean notifyFlg1, boolean notifyFlg2, boolean notifyFlg3, boolean notifyFlg4) {
        try {
            ACObject userProfile = new ACObject();
            userProfile.put("notifyFlg1", notifyFlg1);
            userProfile.put("notifyFlg2", notifyFlg2);
            userProfile.put("notifyFlg3", notifyFlg3);
            userProfile.put("notifyFlg4", notifyFlg4);
            accountMgr.setUserProfile(userProfile, new VoidCallback() {
                @Override
                public void success() {
                    Log.i("setUserInfo", "Extended attributes set successfully");
                }

                @Override
                public void error(ACException e) {
                    Log.e("setUserInfo", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     */
    public void login() {
        try {
            loginBtn.setClickable(false);
            DCPServiceUtils.login(tel, pwd, new PayloadCallback<ACUserInfo>() {
                @Override
                public void success(ACUserInfo userInfo) {
                    p.dismiss();
                    ShowToast(getString(R.string.airpurifier_login_show_tosloginsuccess_text));
                    MainApplication.getInstance();
                    MainApplication.UserLogin(
                            new UserInfo(userInfo.getUserId(), userInfo.getName() == null ? tel : userInfo.getName(), tel));
                    loginBtn.setClickable(true);

                    checkUserInfo();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }

                @Override
                public void error(ACException e) {
                    Log.e("Login",e.toString() + "====" + e.getErrorCode());
                    loginBtn.setClickable(true);
                    p.dismiss();

                    if (e.getErrorCode() == 404
                            || e.getErrorCode() == ACException.INTERNET_ERROR
                            || e.getErrorCode() == 1999) {
                        ShowToast(getString(R.string.airpurifier_login_show_tosneterror_text));
                    } else {
                        ShowToast(getString(R.string.airpurifier_login_show_toswrongpwdaccount_text));
                    }
                }
            });
        } catch (Exception e) {
            Log.v("exception", e.getMessage());
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (System.currentTimeMillis() - lastTime > 2000) {
                    Toast.makeText(this, R.string.airpurifier_login_show_tospressagainexit_text, AppConstant.TOAST_DURATION).show();
                    lastTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void ShowToast(String text) {
        try {
            if (!TextUtils.isEmpty(text)) {
                if (mToast == null) {
                    mToast = Toast.makeText(LoginActivity.this.getApplicationContext(), text,
                            AppConstant.TOAST_DURATION);
                } else {
                    mToast.setText(text);
                }
                mToast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}