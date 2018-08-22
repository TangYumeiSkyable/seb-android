package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.view.DotsView;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.config.Constants;
import com.supor.suporairclear.config.SubDominConfig;
import com.supor.suporairclear.config.SubDominUtil;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P12Fragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "P12Fragment";
    private DotsView mDotsview;
    private TextView mTvAddress;
    private Button mBtnAddDevice;
    private EditText mEtdeivcename;
    private String mPhysicalDeviceId;
    private Long mDeviceId;
    private Long mSubDominId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_p12, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.fragment_p12_title));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((APAddDeviceActivity) getActivity()).setNavBtn(0, 0);
        mBtnAddDevice.setText(R.string.confirm);

    }

    private void initView(View view) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPhysicalDeviceId = arguments.getString(Constants.PHYSICALDEVICEID);
            mDeviceId = arguments.getLong(Constants.DEVICEID);
            mSubDominId = arguments.getLong(Constants.SUBDOMAINID);
        }
        mDotsview = (DotsView) view.findViewById(R.id.dotsview);
        mTvAddress = (TextView) view.findViewById(R.id.tv_address);
        mEtdeivcename = (EditText) view.findViewById(R.id.et_deivcename);
        mBtnAddDevice = (Button) view.findViewById(R.id.btn_addDevice);
        mDotsview.setLightDotsNumber(6);
        mTvAddress.setText(mPhysicalDeviceId);
        mBtnAddDevice.setOnClickListener(this);
        if (mSubDominId != null)
            if (mSubDominId == SubDominConfig.XL.mSubDominId.longValue()) {
                mEtdeivcename.setHint(R.string.XL_default_name);
            } else if (mSubDominId == SubDominConfig.XS.mSubDominId.longValue()) {
                mEtdeivcename.setHint(R.string.xs_default_name);
            }

        mEtdeivcename.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    //TODO:你自己的业务逻辑
                    changeDeviceName();
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
                changeDeviceName();
                break;
        }
    }

    private void changeDeviceName() {
        if (mEtdeivcename != null && mEtdeivcename.getText() != null) {
            String trim = mEtdeivcename.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                trim = mEtdeivcename.getHint().toString().trim();
                if (TextUtils.isEmpty(trim))
                    if (mSubDominId != null)
                        if (mSubDominId == SubDominConfig.XL.mSubDominId.longValue()) {
                            trim = getString(R.string.XL_default_name);
                        } else if (mSubDominId == SubDominConfig.XS.mSubDominId.longValue()) {
                            trim = getString(R.string.xs_default_name);
                        } else {
                            trim = "Intense Pure Air";
                        }
            }
            AC.bindMgr().changeName(SubDominUtil.findNameById(mSubDominId), mDeviceId, trim, new VoidCallback() {
                @Override
                public void success() {
                    if (FragmentUtil.judgeGetActivityCanUse(P12Fragment.this))
                        FragmentUtil.replaceSupportFragment(getActivity()
                                , R.id.framlayout_container
                                , new P13Fragment()
                                ,P12Fragment.this
                                , P13Fragment.TAG
                                , true
                                , false);
                }

                @Override
                public void error(ACException e) {
                    if (FragmentUtil.judgeGetActivityCanUse(P12Fragment.this)) {
                        Toast.makeText(getActivity(), R.string.fail, Toast.LENGTH_SHORT).show();
                        FragmentUtil.replaceSupportFragment(getActivity()
                                , R.id.framlayout_container
                                , new P13Fragment()
                                ,P12Fragment.this
                                , P13Fragment.TAG
                                , true
                                , false);
                    }
                }
            });
        }
    }
}
