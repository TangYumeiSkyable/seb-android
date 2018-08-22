package com.supor.suporairclear.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.config.AppConstant;

/**
 * Created by emma on 2017/5/22.
 */

public class VpSImpleFragment extends Fragment {
    private int mTitle;
    public static final String BUNDLE_TITLE = "title";

    private TextView tv_airqualitytitle;
    private TextView tv_airqualitycontent;
    private TextView tv_airqualitycontent2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_airquality, container, false);
        tv_airqualitytitle = (TextView) view.findViewById(R.id.tv_airqualitytitle);
        tv_airqualitytitle.setText(getString(R.string.airpurifier_more_airquality_fresh));
        tv_airqualitytitle.setTypeface(AppConstant.ubuntuRegular);

        tv_airqualitycontent = (TextView) view.findViewById(R.id.tv_airqualitycontent);
        tv_airqualitycontent.setText(getString(R.string.airpurifier_more_air_fresh_headline));
        tv_airqualitycontent.setTypeface(AppConstant.ubuntuMedium);

        tv_airqualitycontent2 = (TextView) view.findViewById(R.id.tv_airqualitycontent2);
        tv_airqualitycontent2.setText(getString(R.string.airpurifier_more_air_fresh_description));
        tv_airqualitycontent2.setTypeface(AppConstant.ubuntuMedium);
        return view;
    }

}
