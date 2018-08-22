package com.rihuisoft.loginmode.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.zhy.autolayout.AutoLinearLayout;

/**
 * Created by emma on 2017/5/22.
 */

public class ViewPagerIndicator extends AutoLinearLayout {
    private Paint mPaint;
    private Path mPath;
    private int mTriangleWidth;
    private int mInitTranslationX;//Initialize the offset
    private int mTranslationX;//Finger movement
    private int mTabVisibleCount;
    private static final int COUNT_DEFAULT_TAB = 3;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mTabVisibleCount = COUNT_DEFAULT_TAB;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#009dc2"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(10);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onFinishInflate() {
        int cCount = getChildCount();
        if (cCount == 0) {
            return;
        } else {
            for (int i = 0; i < cCount; i++) {
                View view = getChildAt(i);
                AutoLinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
                lp.weight = 0;
                lp.width = getScreenWidth() / mTabVisibleCount;
                view.setLayoutParams(lp);
            }
        }

        super.onFinishInflate();
    }

    /**
     * Get the width of the screen
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;//Returns the width of the screen

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int) (w / mTabVisibleCount);//Triangle bottom width
        mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2-27;
        initTriangle();
    }

    private void initTriangle() {
//        mTriangleHeight = mTriangleWidth / 2;
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth, -(getHeight() / 18));
        mPath.lineTo(0, -(getHeight() / 18));

    }

    public void scroll(int position, float offset) {
        int tabWidth = getWidth() / mTabVisibleCount;
//      mLineWidth = (int) (tabWidth * (offset + position));

        mTranslationX = (int) (tabWidth * (offset + position));

        //Container movement, when the TAB in a move to the last
        if (position >= mTabVisibleCount - 2 && offset > 0 && getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth + (int) (tabWidth * offset), 0);
            } else {
                this.scrollTo(position * tabWidth + (int) (tabWidth * offset), 0);
            }

        }

        invalidate();
    }

}
