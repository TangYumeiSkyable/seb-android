/*
 * Copyright (C) 2017.  BoBoMEe(wbwjx115@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.supor.suporairclear.common.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.accloud.service.ACObject;
import com.com.loopview.drawableindicator.anim.RotateEnter;
import com.com.loopview.drawableindicator.anim.ZoomInEnter;
import com.com.loopview.drawableindicator.widget.AnimIndicator;

import java.util.List;

/**
 * Created on 2017/1/14.下午3:28.
 *
 * @author bobomee.
 */

public final class ViewParser {

  private static ViewPager scrollVp(View _view, int scrollId) {
    ViewPager viewPager = ViewFindUtils.find(_view, scrollId);
    viewPager.setFocusable(true);

    return viewPager;
  }

//  private static FragmentStateAdapter adapterVp(FragmentManager _fragmentManager,
//      ViewPager _viewPager) {
//    FragmentStateAdapter fragmentStateAdapter = new FragmentStateAdapter(_fragmentManager);
//    _viewPager.setAdapter(fragmentStateAdapter);
//    return fragmentStateAdapter;
//  }
  private static FragmentStateAdapter adapterVp(FragmentManager _fragmentManager,
                                                ViewPager _viewPager, List<ACObject> list) {
    FragmentStateAdapter fragmentStateAdapter = FragmentStateAdapter.getInstance(_fragmentManager, list);
    _viewPager.setAdapter(fragmentStateAdapter);
    return fragmentStateAdapter;
  }
//  public static void scrollRightVp(FragmentManager _fragmentManager, View view, int scrollId) {
//    ViewPager viewPager = scrollVp(view, scrollId);
//    FragmentStateAdapter fragmentStateAdapter = adapterVp(_fragmentManager, viewPager);
//  }
  public static void scrollRightVp(FragmentManager _fragmentManager, View view, int scrollId, List<ACObject> list) {
    ViewPager viewPager = scrollVp(view, scrollId);
    FragmentStateAdapter fragmentStateAdapter = adapterVp(_fragmentManager, viewPager, list);
  }
  public static void scrollRightVp(FragmentManager _fragmentManager, View view, int scrollId,
                                   int indicatorId, List<Integer> list) {
    ViewPager viewPager = scrollVp(view, scrollId);
    OutdoorStateAdapter outdoorStateAdapter = new OutdoorStateAdapter(_fragmentManager, list);
    final AnimIndicator baseIndicator =  ViewFindUtils.find(view, indicatorId);
    baseIndicator.setUnselectAnimClass(RotateEnter.class)
            .setSelectAnimClass(ZoomInEnter.class)
            .setMovingAnimClass(RotateEnter.class);
    viewPager.setAdapter(outdoorStateAdapter);
//    viewPager.startAutoScroll();
    baseIndicator.setIndicatorCount(viewPager.getAdapter().getCount());
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        baseIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }
  private static int px2dip(Context context, int pxValue) {
    float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }
}
