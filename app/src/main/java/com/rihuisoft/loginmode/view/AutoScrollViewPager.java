package com.rihuisoft.loginmode.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.Logger;
import com.supor.suporairclear.adapter.ViewPagerAdapter;
import com.supor.suporairclear.fragment.FilterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class AutoScrollViewPager extends FrameLayout {
    private ViewPager mViewPager;
    private List<ACObject> mACObjects = new ArrayList<>();
    private LayoutParams mLayoutParams;
    private boolean isAlreadyShow;
    private List<FilterFragment> mFilterFragments;
    private Disposable mDisposable;
    private ViewPagerAdapter mViewPagerAdapter;
    private boolean isAutoScroll;
    private FragmentManager mFragmentManager;

    public AutoScrollViewPager(@NonNull Context context) {
        this(context, null);
    }

    public AutoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AutoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_viewpager, this, false);
        mViewPager = (ViewPager) inflate.findViewById(R.id.viewpager);
        addView(inflate);
        addOnScrollChanged();
    }

    public void prepareWork(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        if (mViewPagerAdapter == null) {
            mViewPager.setVisibility(View.INVISIBLE);
            mFilterFragments = new ArrayList<>();
            //设置视图
            FilterFragment preFilterFragment = new FilterFragment();
            FilterFragment activeCarbonFragment = new FilterFragment();
            FilterFragment hepaFragment = new FilterFragment();
            FilterFragment nanocapturFragment = new FilterFragment();

            mFilterFragments.add(preFilterFragment);
            mFilterFragments.add(activeCarbonFragment);
            mFilterFragments.add(hepaFragment);
            mFilterFragments.add(nanocapturFragment);


        }
    }

    /**
     * 设置数据
     */
    public void setData(List<ACObject> acObjectList) {
        if (acObjectList == null) return;
        //更新数据
        mACObjects.clear();
        mACObjects.addAll(acObjectList);
        for (int i = 0; i < acObjectList.size(); i++) {
            Bundle arguments = mFilterFragments.get(i).getArguments();
            if (arguments == null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("acobject", acObjectList.get(i));
                mFilterFragments.get(i).setArguments(bundle);
            } else {
                arguments.putSerializable("acobject", acObjectList.get(i));
            }
        }
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter.notifyDataSetChanged();
        } else {
            mViewPagerAdapter = new ViewPagerAdapter(mFragmentManager, mFilterFragments);
            mViewPager.setAdapter(mViewPagerAdapter);
        }

    }

    /**
     * 显示View
     */
    public void showAutoScrollView() {
        if (!isAlreadyShow) {
            Logger.i("显示成功");
            mViewPager.setVisibility(View.VISIBLE);
            isAlreadyShow = true;
            scrollShow();
        } else {
            Logger.e("show fail");
        }
    }

    /**
     * 移除View
     */
    public void removeAutoScrollview() {
        if (isAlreadyShow) {
            Logger.i("移除成功");
            isAlreadyShow = false;
            if (!mDisposable.isDisposed()) mDisposable.dispose();
        } else {
            Logger.e("remove fail");
        }
    }

    private void scrollShow() {
        mDisposable = Observable
                .interval(3, 3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Logger.i("scroll" + aLong);
                        if (mViewPager != null)
                        //设置主动滚动
                        {
                            int currentItem = mViewPager.getCurrentItem();
                            int item = currentItem + 1;
                            int childCount = mViewPager.getChildCount();
                            mViewPager.setCurrentItem(item, true);
                        }
                    }
                });
    }

    private void addOnScrollChanged() {
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mDisposable != null && !mDisposable.isDisposed()) {
                            mDisposable.dispose();
                            isAutoScroll = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isAutoScroll) {
                            scrollShow();
                            isAutoScroll = true;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (!isAutoScroll) {
                            scrollShow();
                            isAutoScroll = true;
                        }
                        break;

                }
                return false;
            }
        });
    }
}
