package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.activity.APAddDeviceActivity;

public class PpermissionTipFragment  extends Fragment{
    public static String TAG = "PpermissionTipFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_permission_more_info, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((APAddDeviceActivity) getActivity()).setNavBtn(R.drawable.ico_back, 0);
        getActivity().setTitle(getString(R.string.request_permission_more_info_title));
    }
}
