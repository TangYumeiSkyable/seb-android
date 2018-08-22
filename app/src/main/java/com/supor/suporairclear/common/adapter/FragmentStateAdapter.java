package com.supor.suporairclear.common.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 2016/3/28.下午2:58.
 * @author bobomee.
 */
public class FragmentStateAdapter extends FragmentStatePagerAdapter {

    private FragmentStateAdapter adapter;
    private FragmentStateAdapter(FragmentManager fm) {
        super(fm);
    }
    private List<ACObject> list = new ArrayList<>();
    private FragmentStateAdapter(FragmentManager fm, List<ACObject> list) {
        super(fm);
        this.list = list;
    }
    public static FragmentStateAdapter getInstance(FragmentManager fm, List<ACObject> list) {
        return new FragmentStateAdapter(fm, list);
    }
    @Override
    public Fragment getItem(int position) {
        if (position > list.size()) {
            position = 1;
        }
        if (position == 0) {
            position = list.size();
        }
        return SimpleFragment.newInstance(position - 1, list.get(position - 1).getString("name"), list.get(position - 1).getInt("value"));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public static class SimpleFragment extends Fragment {

        public static final String POSITION = "POSITION";
        public static final String NAME = "NAME";
        public static final String VALUE = "VALUE";
        private int position = 0, value = 0;
        private String name;

        public static SimpleFragment newInstance(int pPosition, String name, int value) {
            SimpleFragment fragment = new SimpleFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(POSITION, pPosition);
            bundle.putInt(VALUE, value);
            bundle.putString(NAME, name);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            position = getArguments().getInt(POSITION);
            value = getArguments().getInt(VALUE);
            name = getArguments().getString(NAME);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.item_filter, container, false);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_value = (TextView) view.findViewById(R.id.tv_value);
            ProgressBar pb_progressbar = (ProgressBar) view.findViewById(R.id.pb_progressbar);
            pb_progressbar.getBackground().setAlpha(0);

            Random random = new Random();
//            view.setBackgroundColor(0xff00ff00 | random.nextInt(0x00ff00ff));
            tv_name.setText(name);
            tv_value.setText(value + "%");
            pb_progressbar.setProgress(value);

            return view;
        }
    }
}
