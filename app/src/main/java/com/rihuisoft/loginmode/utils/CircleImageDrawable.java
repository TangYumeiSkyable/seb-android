package com.rihuisoft.loginmode.utils;

/**
 * Created by enyva on 16/6/9.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public class CircleImageDrawable extends Drawable
{

    private Paint mPaint;
    private int mWidth;
    private Bitmap mBitmap ;

    public CircleImageDrawable(Bitmap bitmap)
    {
    	try {
    		mBitmap = bitmap ;
            BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP,
                    TileMode.CLAMP);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setShader(bitmapShader);
            mWidth = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    @Override
    public void draw(Canvas canvas)
    {
    	try {
    		canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    @Override
    public int getIntrinsicWidth()
    {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight()
    {
        return mWidth;
    }

    @Override
    public void setAlpha(int alpha)
    {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf)
    {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }

}