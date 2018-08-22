package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.supor.suporairclear.config.Constants;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P15Fragment extends BaseAPLinkFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_ap_common, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        desc_one.setVisibility(View.GONE);
        desc_two.setText(R.string.aplink_p15_desc);
        dotsview.setLightDotsNumber(4);
        iv_show.setImageLevel(Constants.IMG_P15);
        btn_addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FragmentUtil.judgeGetActivityCanUse(P15Fragment.this))
                    getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.fragment_p15_title));
    }
}
