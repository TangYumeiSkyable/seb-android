package com.supor.suporairclear.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.ViewPagerIndicator;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.fragment.VpSImpleFragment;
import com.supor.suporairclear.fragment.VpSImpleFragment2;
import com.supor.suporairclear.fragment.VpSImpleFragment3;
import com.supor.suporairclear.fragment.VpSImpleFragment4;
import com.supor.suporairclear.fragment.VpSImpleFragment5;
import com.supor.suporairclear.fragment.VpSImpleFragment6;
import com.supor.suporairclear.fragment.VpSImpleFragment7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by emma on 2017/5/22.
 */

public class AirQualityActivity extends BaseActivity {


    private ImageView iv_tabicon1;
    private ImageView iv_tabicon2;
    private ImageView iv_tabicon3;
    private ImageView iv_tabicon4;
    private ImageView iv_tabicon5;
    private ImageView iv_tabicon6;
    private ImageView iv_tabicon7;

    private ImageView iv_cutting3;
    private ImageView iv_cutting4;
    private ImageView iv_cutting5;
    private ImageView iv_cutting6;
    private ViewPager vp_viewpager;
    private ViewPagerIndicator id_indicator;
    private List<android.support.v4.app.Fragment> mContents = new ArrayList<android.support.v4.app.Fragment>();
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airquality);
        AppManager.getAppManager().addActivity(this);
        setTitle(getString(R.string.airpurifier_more_show_airquality_text));
        setNavBtn(R.drawable.ico_back, 0);
        initView();
        initDatas();
        vp_viewpager.setAdapter(mAdapter);
        vp_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                id_indicator.scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    iv_tabicon1.setImageResource(R.drawable.ico_air_01m_sel);
                    iv_tabicon2.setImageResource(R.drawable.ico_air_02m_nor);
                    iv_tabicon3.setImageResource(R.drawable.ico_air_03m_nor);
                    iv_tabicon4.setImageResource(R.drawable.ico_air_04m_nor);
                    iv_tabicon5.setImageResource(R.drawable.ico_air_05m_nor);
                    iv_tabicon6.setImageResource(R.drawable.ico_air_06m_nor);
                    iv_tabicon7.setImageResource(R.drawable.ico_air_07m_nor);
                } else if (position == 1) {
                    iv_tabicon1.setImageResource(R.drawable.ico_air_01m_nor);
                    iv_tabicon2.setImageResource(R.drawable.ico_air_02m_sel);
                    iv_tabicon3.setImageResource(R.drawable.ico_air_03m_nor);
                    iv_tabicon4.setImageResource(R.drawable.ico_air_04m_nor);
                    iv_tabicon5.setImageResource(R.drawable.ico_air_05m_nor);
                    iv_tabicon6.setImageResource(R.drawable.ico_air_06m_nor);
                    iv_tabicon7.setImageResource(R.drawable.ico_air_07m_nor);
                    iv_cutting3.setVisibility(View.GONE);
                } else if (position == 2) {
                    iv_tabicon1.setImageResource(R.drawable.ico_air_01m_nor);
                    iv_tabicon2.setImageResource(R.drawable.ico_air_02m_nor);
                    iv_tabicon3.setImageResource(R.drawable.ico_air_03m_sel);
                    iv_tabicon4.setImageResource(R.drawable.ico_air_04m_nor);
                    iv_tabicon5.setImageResource(R.drawable.ico_air_05m_nor);
                    iv_tabicon6.setImageResource(R.drawable.ico_air_06m_nor);
                    iv_tabicon7.setImageResource(R.drawable.ico_air_07m_nor);
                    iv_cutting3.setVisibility(View.VISIBLE);
                    iv_cutting4.setVisibility(View.GONE);
                } else if (position == 3) {
                    iv_tabicon1.setImageResource(R.drawable.ico_air_01m_nor);
                    iv_tabicon2.setImageResource(R.drawable.ico_air_02m_nor);
                    iv_tabicon3.setImageResource(R.drawable.ico_air_03m_nor);
                    iv_tabicon4.setImageResource(R.drawable.ico_air_04m_sel);
                    iv_tabicon5.setImageResource(R.drawable.ico_air_05m_nor);
                    iv_tabicon6.setImageResource(R.drawable.ico_air_06m_nor);
                    iv_tabicon7.setImageResource(R.drawable.ico_air_07m_nor);
                    iv_cutting4.setVisibility(View.VISIBLE);
                    iv_cutting5.setVisibility(View.GONE);
                } else if (position == 4) {
                    iv_tabicon1.setImageResource(R.drawable.ico_air_01m_nor);
                    iv_tabicon2.setImageResource(R.drawable.ico_air_02m_nor);
                    iv_tabicon3.setImageResource(R.drawable.ico_air_03m_nor);
                    iv_tabicon4.setImageResource(R.drawable.ico_air_04m_nor);
                    iv_tabicon5.setImageResource(R.drawable.ico_air_05m_sel);
                    iv_tabicon6.setImageResource(R.drawable.ico_air_06m_nor);
                    iv_tabicon7.setImageResource(R.drawable.ico_air_07m_nor);
                    iv_cutting5.setVisibility(View.VISIBLE);
                    iv_cutting6.setVisibility(View.GONE);
                } else if (position == 5) {
                    iv_tabicon1.setImageResource(R.drawable.ico_air_01m_nor);
                    iv_tabicon2.setImageResource(R.drawable.ico_air_02m_nor);
                    iv_tabicon3.setImageResource(R.drawable.ico_air_03m_nor);
                    iv_tabicon4.setImageResource(R.drawable.ico_air_04m_nor);
                    iv_tabicon5.setImageResource(R.drawable.ico_air_05m_nor);
                    iv_tabicon6.setImageResource(R.drawable.ico_air_06m_sel);
                    iv_tabicon7.setImageResource(R.drawable.ico_air_07m_nor);
                    iv_cutting6.setVisibility(View.VISIBLE);
                } else if (position == 6) {
                    iv_tabicon1.setImageResource(R.drawable.ico_air_01m_nor);
                    iv_tabicon2.setImageResource(R.drawable.ico_air_02m_nor);
                    iv_tabicon3.setImageResource(R.drawable.ico_air_03m_nor);
                    iv_tabicon4.setImageResource(R.drawable.ico_air_04m_nor);
                    iv_tabicon5.setImageResource(R.drawable.ico_air_05m_nor);
                    iv_tabicon6.setImageResource(R.drawable.ico_air_06m_nor);
                    iv_tabicon7.setImageResource(R.drawable.ico_air_07m_sel);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Data initialization
     */
    private void initDatas() {

        VpSImpleFragment fragment = new VpSImpleFragment();
        VpSImpleFragment2 fragment2 = new VpSImpleFragment2();
        VpSImpleFragment3 fragment3 = new VpSImpleFragment3();
        VpSImpleFragment4 fragment4 = new VpSImpleFragment4();
        VpSImpleFragment5 fragment5 = new VpSImpleFragment5();
        VpSImpleFragment6 fragment6 = new VpSImpleFragment6();
        VpSImpleFragment7 fragment7 = new VpSImpleFragment7();
        mContents.add(fragment);
        mContents.add(fragment2);
        mContents.add(fragment3);
        mContents.add(fragment4);
        mContents.add(fragment5);
        mContents.add(fragment6);
        mContents.add(fragment7);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };
    }

    /**
     * Controls the initialization
     */
    private void initView() {

        iv_tabicon1 = (ImageView) findViewById(R.id.iv_tabicon1);
        iv_tabicon2 = (ImageView) findViewById(R.id.iv_tabicon2);
        iv_tabicon3 = (ImageView) findViewById(R.id.iv_tabicon3);
        iv_tabicon4 = (ImageView) findViewById(R.id.iv_tabicon4);
        iv_tabicon5 = (ImageView) findViewById(R.id.iv_tabicon5);
        iv_tabicon6 = (ImageView) findViewById(R.id.iv_tabicon6);
        iv_tabicon7 = (ImageView) findViewById(R.id.iv_tabicon7);
        iv_cutting3 = (ImageView) findViewById(R.id.iv_cutting3);
        iv_cutting4 = (ImageView) findViewById(R.id.iv_cutting4);
        iv_cutting5 = (ImageView) findViewById(R.id.iv_cutting5);
        iv_cutting6 = (ImageView) findViewById(R.id.iv_cutting6);
        vp_viewpager = (ViewPager) findViewById(R.id.vp_viewpager);
        id_indicator = (ViewPagerIndicator) findViewById(R.id.id_indicator);
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            switch (component) {
                case LEFT:
                    finish();
                    break;
                case RIGHT:
                    break;
                case RIGHT2:
                    break;
                case TITLE:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
