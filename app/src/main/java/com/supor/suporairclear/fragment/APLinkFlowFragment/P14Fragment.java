package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P14Fragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "P14Fragment";
    private Button mBtnAddDevice;
    private TextView mAplinkFailTip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_p14, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    private void initView(View view) {
        mBtnAddDevice = (Button) view.findViewById(R.id.btn_addDevice);
        mAplinkFailTip = (TextView) view.findViewById(R.id.tv_aplink_fail_tip);
        mBtnAddDevice.setOnClickListener(this);

        //根据版本对应的文字是否显示
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mAplinkFailTip.setVisibility(View.VISIBLE);
        } else {
            mAplinkFailTip.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addDevice:
                if (FragmentUtil.judgeGetActivityCanUse(P14Fragment.this))
                    getActivity().getSupportFragmentManager().popBackStackImmediate(P5Fragment.TAG, 0);
                break;
        }
    }
}
