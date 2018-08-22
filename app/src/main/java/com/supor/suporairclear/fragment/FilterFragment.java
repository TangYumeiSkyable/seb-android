package com.supor.suporairclear.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.Logger;

/**
 * 控制页四个滤芯 转换
 */
public class FilterFragment extends Fragment {
    private ProgressBar mPbProgressbar;
    private TextView mTvName;
    private TextView mTvValue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPbProgressbar = (ProgressBar) view.findViewById(R.id.pb_progressbar);
        mTvValue = (TextView) view.findViewById(R.id.tv_value);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        if (mTvName.getText().toString().contains("--")) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                ACObject acObject = (ACObject) arguments.getSerializable("acobject");
                if (acObject != null && mPbProgressbar != null && mTvValue != null && mTvName != null) {
                    String name = acObject.getString("name");
                    int value = acObject.getInt("value");
                    mPbProgressbar.setMax(100);
                    mPbProgressbar.setProgress(value);
                    Logger.i("name:" + name + "，value：" + value);
                    Logger.i("getMax：" + mPbProgressbar.getMax() + ",getProgress:" + mPbProgressbar.getProgress());
                    mTvValue.setText(value + "%");
                    mTvName.setText(name);
                }
            }
        }


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                ACObject acObject = (ACObject) arguments.getSerializable("acobject");
                if (acObject != null && mPbProgressbar != null && mTvValue != null && mTvName != null) {
                    String name = acObject.getString("name");
                    int value = acObject.getInt("value");
                    mPbProgressbar.setMax(100);
                    mPbProgressbar.setProgress(value);
                    Logger.i("name:" + name + "，value：" + value);
                    Logger.i("getMax：" + mPbProgressbar.getMax() + ",getProgress:" + mPbProgressbar.getProgress());
                    mTvValue.setText(value + "%");
                    mTvName.setText(name);
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}