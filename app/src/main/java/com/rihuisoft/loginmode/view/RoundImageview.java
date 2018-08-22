package com.rihuisoft.loginmode.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.rihuisoft.loginmode.utils.DensityUtil;


/**
 * 圆形的imageview
 *
 * @author 冯剑
 */
public class RoundImageview extends ImageView {

    // private int roundWidth = 13;
    // private int roundHeight = 13;

    public RoundImageview(Context context) {
        super(context);
        // init(context, null);
    }

    public RoundImageview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // init(context, attrs);
    }

    public RoundImageview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 重写draw()
     */
    @Override
    public void draw(Canvas canvas) {

        // 实例化一个和ImageView一样大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);

        // 实例化一个canvas，这个canvas对应的内存为上面的bitmap
        Canvas canvas2 = new Canvas(bitmap);
        if (bitmap.isRecycled()) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            canvas2 = new Canvas(bitmap);
        }

        // 将imageView自己绘制到canvas2上，这个导致bitmap里面存放了imageView
        super.draw(canvas2);

        // 利用canvas画一个圆角矩形，这个会修改bitmap的数据
        drawRoundAngle(canvas2);

        // 将裁剪好的bitmap绘制到系统当前canvas上，这样裁剪好的imageview就能显示到屏幕上
        Paint paint = new Paint();
        paint.setXfermode(null);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        bitmap.recycle();

        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true); // 設置畫筆為無鋸齒
        circlePaint.setColor(Color.parseColor("#c3c4c5")); // 設置畫筆顏色
        circlePaint.setStrokeWidth(DensityUtil.dipToPx(getContext(), 1)); // 線寬
        circlePaint.setStyle(Style.STROKE); // 空心效果
        Rect rectCircle = new Rect(DensityUtil.dipToPx(getContext(), 2), DensityUtil.dipToPx(getContext(), 2), bitmap.getWidth()
                - DensityUtil.dipToPx(getContext(), 2), bitmap.getHeight() - DensityUtil.dipToPx(getContext(), 2));
        RectF rectCircleF = new RectF(rectCircle);
        canvas.drawOval(rectCircleF, circlePaint);
    }


    private void drawRoundAngle(Canvas canvas) {
        Paint maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Path maskPath = new Path();
        // maskPath.addRoundRect(new RectF(0.0F, 0.0F, getWidth(), getHeight()),
        // roundWidth, roundHeight, Path.Direction.CW);
        maskPath.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - DensityUtil.dipToPx(getContext(), 2), Direction.CW);
        // 这是设置了填充模式，非常关键
        maskPath.setFillType(Path.FillType.INVERSE_WINDING);
        canvas.drawPath(maskPath, maskPaint);
    }
}
