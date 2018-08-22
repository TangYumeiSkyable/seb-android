package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.utils.Logger;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.config.Constants;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P5Fragment extends BaseAPLinkFragment implements View.OnClickListener {
    public static final String TAG = "P5Fragment";

    @Override
    public void onResume() {
        super.onResume();
        Logger.i("P5Fragment:onResume");
        getActivity().setTitle(R.string.active_wifi);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        ((APAddDeviceActivity) getActivity()).setNavBtn(R.drawable.ico_back, 0);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String deviceType = arguments.getString("deviceType");
            Log.e(P4Fragment.TAG, deviceType);
        }
        dotsview.setLightDotsNumber(3);
        desc_one.setVisibility(View.GONE);
        iv_show.setImageLevel(Constants.IMG_P5);
        desc_two.setText(getString(R.string.aplink_p5));
        btn_addDevice.setText(getString(R.string.next));
        btn_addDevice.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addDevice:
                if (FragmentUtil.judgeGetActivityCanUse(this)) {
                    FragmentUtil.replaceSupportFragment(
                            getActivity()
                            , R.id.framlayout_container
                            , new P6Fragment()
                            ,P5Fragment.this
                            , P6Fragment.TAG
                            , true
                            , false);

                }
                break;
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.i("P5Fragment:onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("P5Fragment:onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.i("P5Fragment:onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.i("P5Fragment:onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.i("P5Fragment:onStart");
    }


    @Override
    public void onPause() {
        super.onPause();
        Logger.i("P5Fragment:onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.i("P5Fragment:onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.i("P5Fragment:onDestoryView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i("P5Fragment:onDestory");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.i("P5Fragment:onDetach");
    }
}
