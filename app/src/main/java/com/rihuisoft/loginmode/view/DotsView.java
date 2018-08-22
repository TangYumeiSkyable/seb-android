package com.rihuisoft.loginmode.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.groupeseb.airpurifier.R;


/**
 * Created by fengjian on 2017/11/10.
 * function:
 */

public class DotsView extends FrameLayout {
    private LinearLayout dots_container;

    public DotsView(@NonNull Context context) {
        this(context, null);
    }

    public DotsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DotsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_dotsview, this, false);
        this.dots_container = (LinearLayout) rootView.findViewById(R.id.dots_container);
        addView(rootView);
    }

    public void setLightDotsNumber(int lightDotsNumber) {
        int childCount = dots_container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i < lightDotsNumber) {
                dots_container.getChildAt(i).setSelected(true);
            } else {
                dots_container.getChildAt(i).setSelected(false);
            }
        }
    }
}
