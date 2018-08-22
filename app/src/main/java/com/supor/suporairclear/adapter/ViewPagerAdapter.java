package com.supor.suporairclear.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.supor.suporairclear.fragment.FilterFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FilterFragment> mFilterFragments;

    public ViewPagerAdapter(FragmentManager fm, List<FilterFragment> filterFragments) {
        super(fm);
        mFilterFragments = filterFragments;
    }

    @Override
    public Fragment getItem(int position) {

        return mFilterFragments.get(position % 4);
    }

    @Override
    public int getCount() {
        return 4 * 1000 * 1000;
    }

}