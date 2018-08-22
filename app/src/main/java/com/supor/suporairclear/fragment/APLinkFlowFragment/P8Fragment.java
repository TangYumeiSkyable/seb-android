package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.config.Constants;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P8Fragment extends BaseAPLinkFragment {

    public static final String TAG = "P8Fragment";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((APAddDeviceActivity) getActivity()).setNavBtn(0, 0);
        dotsview.setLightDotsNumber(4);
        iv_show.setImageLevel(Constants.IMG_P8);
        desc_one.setVisibility(View.GONE);
        desc_two.setText(R.string.aplink_p8);
        btn_addDevice.setText(getString(R.string.next));
        btn_addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtil.replaceSupportFragment(
                        getActivity()
                        , R.id.framlayout_container
                        , new P9Fragment()
                        ,P8Fragment.this
                        , P9Fragment.TAG
                        , true
                        , false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.fragment_p6_title));
    }
}
