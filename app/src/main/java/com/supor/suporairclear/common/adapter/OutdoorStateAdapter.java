package com.supor.suporairclear.common.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.groupeseb.airpurifier.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/3/28.下午2:58.
 * @author bobomee.
 */
public class OutdoorStateAdapter extends FragmentStatePagerAdapter {

    public OutdoorStateAdapter(FragmentManager fm) {
        super(fm);
    }
    private List<Integer> list = new ArrayList<>();
    public OutdoorStateAdapter(FragmentManager fm, List<Integer> list) {
        super(fm);
        this.list = list;
    }
    @Override
    public Fragment getItem(int position) {
        if (position > getCount()) {
            position = 1;
        }
        if (position == 0) {
            position  = getCount();
        }
        return SimpleFragment.newInstance(position - 1, list.get(position - 1));

//        return SimpleFragment.newInstance(position, list.get(position).getString("name"), list.get(position).getInt("value"));
    }

    @Override
    public int getCount() {
        return 3;
    }

    public static class SimpleFragment extends Fragment {

        public static final String POSITION = "POSITION";
        public static final String NAME = "NAME";
        public static final String VALUE = "VALUE";
        private int position = 0, value = 0;
        private String name;

        public static SimpleFragment newInstance(int pPosition, int value) {
            SimpleFragment fragment = new SimpleFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(POSITION, pPosition);
            bundle.putInt(VALUE, value);
//            bundle.putString(NAME, name);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments().getInt(POSITION);
            value = getArguments().getInt(VALUE);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.item_outdoor, container, false);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_value = (TextView) view.findViewById(R.id.tv_value);

            if (position == 0) {
                tv_name.setText("PM");
            } else if (position == 1) {
                tv_name.setText("NO₂");
            } else if (position == 2) {
                tv_name.setText("O₃");
            }
            if (value == 0) {
                tv_value.setText("--");
            } else {
                tv_value.setText(String.valueOf(value));
            }

            return view;
        }
    }
}
