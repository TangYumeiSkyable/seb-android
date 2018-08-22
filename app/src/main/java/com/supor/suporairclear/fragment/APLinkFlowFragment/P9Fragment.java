package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.view.DotsView;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P9Fragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "P9Fragment";
    private DotsView mDotsview;
    private TextView mTextView;
    private EditText mEditPassword;
    private Button mBtnAddDevice;
    private ImageView mIvRevealPwd;
    private TextView mTvRequestPermissionTip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_p9, container, false);
        initView(view);
        ((APAddDeviceActivity) getActivity()).setNavBtn(0, 0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String domesticWiFiSIID = ((APAddDeviceActivity) getActivity()).domesticWiFiSIID;
        mTextView.setText(TextUtils.isEmpty(domesticWiFiSIID) ? getString(R.string.not_found) : domesticWiFiSIID);
        getActivity().setTitle(getString(R.string.fragment_p9_title));
    }

    private void initView(View view) {
        mDotsview = (DotsView) view.findViewById(R.id.dotsview);
        mTextView = (TextView) view.findViewById(R.id.tv_name);
        mEditPassword = (EditText) view.findViewById(R.id.edit_password);
        mBtnAddDevice = (Button) view.findViewById(R.id.btn_addDevice);
        mIvRevealPwd = (ImageView) view.findViewById(R.id.iv_reveal_pwd);
        mTvRequestPermissionTip = (TextView) view.findViewById(R.id.tv_request_permission_tip);

        mDotsview.setLightDotsNumber(5);
        mBtnAddDevice.setOnClickListener(this);
        mIvRevealPwd.setOnClickListener(this);
        mBtnAddDevice.setText(R.string.aplink_p9_bt);

        //根据版本对应的文字是否显示
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mTvRequestPermissionTip.setVisibility(View.VISIBLE);
        } else {
            mTvRequestPermissionTip.setVisibility(View.GONE);
        }

        mEditPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    //TODO:你自己的业务逻辑
                    submit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addDevice:
                submit();
                break;
            case R.id.iv_reveal_pwd:
                revealPassword();
                break;
        }
    }

    private void submit() {
        if (mEditPassword != null && FragmentUtil.judgeGetActivityCanUse(P9Fragment.this)) {
            String password = mEditPassword.getText().toString().trim();
            ((APAddDeviceActivity) getActivity()).domesticWiFiPassword = password;
            FragmentUtil.replaceSupportFragment(getActivity()
                    , R.id.framlayout_container
                    , new P10Fragment()
                    , P9Fragment.this
                    , P10Fragment.TAG
                    , true
                    , false);
        }
    }

    private void revealPassword() {
        mIvRevealPwd.setActivated(!mIvRevealPwd.isActivated());
        mEditPassword.setTransformationMethod(mIvRevealPwd.isActivated() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        Editable etext = mEditPassword.getText();
        Selection.setSelection(etext, etext.length());
    }
}
