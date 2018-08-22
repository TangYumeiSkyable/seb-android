package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.supor.suporairclear.config.Constants;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P13Fragment extends BaseAPLinkFragment {
    public static final String TAG = "P13Fragment";


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dotsview.setVisibility(View.GONE);
        iv_show.setImageLevel(Constants.IMG_P13);
        desc_one.setVisibility(View.GONE);
        desc_two.setText(R.string.aplink_p13_desc);
        btn_addDevice.setText(R.string.ok);
        btn_addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FragmentUtil.judgeGetActivityCanUse(P13Fragment.this)) {
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.fragment_p13_title));
    }
}
