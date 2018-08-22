package com.supor.suporairclear.common.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by enyva on 16/5/27.
 */
public class HomeAdapter extends PagerAdapter {
    private List<View> list;
    private List<String> titlelist;

    public HomeAdapter(List<String> titlelist, List<View> list) {
        this.titlelist = titlelist;
        this.list = list;
    }

    //获得当前页卡的标题
    @Override
    public CharSequence getPageTitle(int position) {
        return titlelist.get(position);
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(list.get(position));
        return list.get(position);
    }

    //销毁页卡
    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(list.get(position));
    }

    //得到所有页卡的个数
    @Override
    public int getCount() {
        return list.size();
    }

    //判断当前显示页卡是否匹配
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
