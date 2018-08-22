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

public class P11Fragment extends BaseAPLinkFragment {
    public static final String TAG = "P11Fragment";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((APAddDeviceActivity) getActivity()).setNavBtn(0, 0);
        dotsview.setLightDotsNumber(5);
        desc_one.setVisibility(View.GONE);
        desc_two.setText(getString(R.string.aplink_p11));
        iv_show.setImageLevel(Constants.IMG_P11);
        btn_addDevice.setText(getString(R.string.next));
        //从P10传过来的mac地址
        final Bundle arguments = getArguments();
        btn_addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P12Fragment fragment = new P12Fragment();
                fragment.setArguments(arguments);
                FragmentUtil.replaceSupportFragment(
                        getActivity()
                        , R.id.framlayout_container
                        , fragment
                        ,P11Fragment.this
                        , P12Fragment.TAG
                        , true
                        , false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.fragment_p9_title));
    }
}
