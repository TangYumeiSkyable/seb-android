package com.rihuisoft.loginmode.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {
	private boolean isCanScroll = true;  
	
	public NoScrollViewPager(Context context) {  
        super(context);  
    }  
  
    public NoScrollViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
   
    public void setScanScroll(boolean isCanScroll){  
        this.isCanScroll = isCanScroll;  
    }  
  
  
    @Override  
    public boolean onTouchEvent(MotionEvent ev) {  
        if (!isCanScroll) {  
            return true;  
        }  
        return super.onTouchEvent(ev);  
    }  
}
