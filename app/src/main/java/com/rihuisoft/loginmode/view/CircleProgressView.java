package com.rihuisoft.loginmode.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.config.AppConstant;

/**
 * Created by enyva on 15/11/26.
 */

/** * Custom gorgeous ProgressBar. */
public class CircleProgressView extends View {
			/** * The Angle of the progress bar occupied */
	private static final int ARC_FULL_DEGREE = 288;
	// The number of the progress bar
	private static final int COUNT = 65;
	// Each of the progress bar occupied Angle
	private static final float ARC_EACH_PROGRESS = ARC_FULL_DEGREE * 1.0f
			/ (COUNT - 1);
	/** * The length of arc fine lines each */
	private static int ARC_LINE_LENGTH = 10;
	private static int ARC_LINE_LENGTH_MAX = 20;
	/** * Arc fine line, the width of each, using the finest line when the 0 */
	private static int ARC_LINE_WIDTH_MAX = 5;
	/** * Components of the wide, high */
	private int width, height;
	/** * A maximum progress bar and the current progress valu */
	private float max=100, progress, pm25=2;
	/** * Draw up the curve of the brush */
	private Paint progressPaint;
	/** * Draw up the text of the brush */
	private Paint textPaint;
	/** * Draw the text background round brush */
	private Paint textBgPaint;
	/** * The radius of the circular arc */
	private int circleRadius;
	/** * Circular circle position */
	private int centerX, centerY;
	/** * Draw up the curve of the rectangular area */
	private RectF circleRectF;
	/** * Water filled with brush*/
	private Paint fullWaterPaint;
	/** * Frost brush*/
	private Paint cleanFrogPaint;

	private Boolean offFlg = true;
	//device
	private int tvoc_level = -1;

	private Context context;
	
	//progress
	private int textColor= Color.parseColor("#f2f2f2");

	public void setDevice(int device){
		invalidate();
	}
	
	public CircleProgressView(Context context) {
		super(context);
		init();
	}

	public CircleProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		progressPaint = new Paint();
		progressPaint.setAntiAlias(true);
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setAntiAlias(true);
		textBgPaint = new Paint();
		textBgPaint.setColor(Color.YELLOW);
		textBgPaint.setAntiAlias(true);
		fullWaterPaint = new Paint();
		fullWaterPaint.setAntiAlias(true);
		cleanFrogPaint = new Paint();
		cleanFrogPaint.setAntiAlias(true);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		if (width == 0 || height == 0) {
			width = getMeasuredWidth();
			height = getMeasuredHeight();
			int half=Math.min(width, height) >> 1;
			ARC_LINE_WIDTH_MAX=Math.round(half/25f);
			ARC_LINE_LENGTH=Math.round(half/25f);
			ARC_LINE_LENGTH_MAX=Math.round(half/15f);
			// The arc radius and centre point
			circleRadius = (Math.min(width, height)) >> 1;
			centerX = width / 2;
			centerY = height / 2;
			// The circular arc in the rectangular area
			circleRectF = new RectF();
			circleRectF.left = centerX - circleRadius;
			circleRectF.top = centerY - circleRadius;
			circleRectF.right = centerX + circleRadius;
			circleRectF.bottom = centerY + circleRadius;
//		}
	}

	private Rect textBounds = new Rect();

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float drawDegree = 1.6f;
		float start = 90 + ((360 - ARC_FULL_DEGREE) >> 1);
		double a = (start + drawDegree) / 180 * Math.PI;
		// The progress bar is the starting point of view
		float drawProgress = 0f;
		boolean isError = false;
		if(progress == -1)
		{
			isError = true;
			progress = 0;
		} else if (progress > max) {
			drawProgress = max;
		} else {
			drawProgress = progress;
		}
		Log.i("current progress", progress + "-" +drawProgress+"");
		float sweep1 = ARC_FULL_DEGREE * (drawProgress / max);
		float sweep2 = ARC_FULL_DEGREE - sweep1; //The rest of the Angle / / draw the starting position of small circle
		// Progress across the Angle of the / / draw the progress bar
		progressPaint.setStyle(Paint.Style.STROKE);
		//The progress bar

		progressPaint.setStrokeWidth(ARC_LINE_WIDTH_MAX);
		if (progress < AppConstant.PM_1) {
			progressPaint.setAlpha(128);
			progressPaint.setColor(getResources().getColor(R.color.btn_text_white));
		} else if (progress <= AppConstant.PM_1 && tvoc_level <= AppConstant.TVOC_A) {
			progressPaint.setAlpha(153);
			progressPaint.setColor(getResources().getColor(R.color.default_blue));
		} else if (progress <= AppConstant.PM_2 && tvoc_level <= AppConstant.TVOC_B) {
			progressPaint.setAlpha(179);
			progressPaint.setColor(getResources().getColor(R.color.purple));
		} else {
			progressPaint.setAlpha(153);
			progressPaint.setColor(getResources().getColor(R.color.red));
		}

		PathEffect effects = new PathDashPathEffect(makePathDash(), 35, 0,
				PathDashPathEffect.Style.ROTATE);

		progressPaint.setPathEffect(effects);
		canvas.drawArc(RectFSpace(circleRadius - 8), start, ARC_FULL_DEGREE, false, progressPaint);
		
		//The progress of
		textPaint.setTextSize(circleRadius/1.2f);
		String text = "--";
		if(offFlg){
			text = String.valueOf((int) ((pm25)) );
		}

		float textLen;
		// Computing text height
		textPaint.getTextBounds("8", 0, 1, textBounds);
		float h1 = textBounds.height();
		if(progress == 0 && progressString.length()>0 && !isError){			
			textLen = textPaint.measureText(text);
			canvas.drawText(progressString, centerX - 4*textLen / 5, centerY  +  h1 / 3, textPaint);
		}else{	

			if(isError)
			{
				text = "--";
				textLen = textPaint.measureText(text);
				canvas.drawText(text, centerX - textLen / 2, centerY  + h1 / 3, textPaint);
			} else {
				textLen = textPaint.measureText(text);
				canvas.drawText(text, centerX - textLen / 2, centerY  + h1 / 3, textPaint);
			}
		}
		textPaint.setTextSize(circleRadius/6.8f);
		text = "PM2.5";
		textLen = textPaint.measureText(text);
		canvas.drawText(text, centerX - textLen / 2, centerY  + h1 / 3 - circleRadius*0.75f, textPaint);
		// The next text line
		textPaint.setTextSize(circleRadius/7.0f);

		if (tvoc_level >= AppConstant.TVOC_A) {
			text = "TVOC";
			textLen = textPaint.measureText(text);
			canvas.drawText(text, centerX - textLen / 2, centerY + circleRadius
					/ 2f, textPaint);

			//The underlying text
			textPaint.setTextSize(circleRadius/8.0f);
			Resources res=getResources();
			Bitmap bm = null;
			if (tvoc_level == AppConstant.TVOC_A) {
				bm = BitmapFactory.decodeResource(res, R.drawable.ico_face_happy);
			} else if (tvoc_level == AppConstant.TVOC_B) {
				bm = BitmapFactory.decodeResource(res, R.drawable.ico_face_general);
			} else if (tvoc_level == AppConstant.TVOC_C) {
				bm = BitmapFactory.decodeResource(res, R.drawable.ico_face_sad);
			}
			canvas.drawBitmap(bm, centerX - bm.getWidth() / 2, centerY + circleRadius
					/ 2f + bm.getHeight() / 2, textPaint);
		}
	}

	public void setMax(int max) {
		this.max = max;
		invalidate();
	}
	public void setTvoc(int level) {
		this.tvoc_level = level;
		invalidate();
	}
	public void setPm25(float pm25,boolean offFlg) {
		this.pm25 = pm25;
		this.offFlg=offFlg;
		invalidate();
	}
	public void setProgressColor(int color) {
		textColor=color;		
		invalidate();
	}

	private static Path makePathDash() {
		Path p = new Path();
		p.addCircle(0, 0, 8, Path.Direction.CW);
		return p;
	}

	// Animation switching schedule(asynchronous)
	public void setProgress(final float progress) {
		float oldProgress = CircleProgressView.this.progress;
//		for (int i = 1; i <= 100; i++) {
		if(progress != -1)
		{
			CircleProgressView.this.progress = oldProgress
					+ (progress - oldProgress);
			
		} else {
			CircleProgressView.this.progress = progress;
		}
		Log.i("dddddd", this.progress+"ffff");
		postInvalidate();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				float oldProgress = SampleView.this.progress;
////				for (int i = 1; i <= 100; i++) {
//					SampleView.this.progress = oldProgress
//							+ (progress - oldProgress);
////					postInvalidate();
////					SystemClock.sleep(20);
////				}
//			}
//		}).start();
	}
	private String progressString="";
	public void setProgress(final float progress,String str) {
		CircleProgressView.this.progress = progress;
		if(null !=str && str.length()>0)
			progressString = str;
		postInvalidate();
	}
	// Direct construction schedule value (synchronous)
	public void setProgressSync(float progress) {
		this.progress = progress;
		invalidate();
	}

	/**
	 * Among the calculate gradient effect of a color value.Only supports # aarrggbb patterns, such as # ccc9c9b2
	 */
	public String calColor(float fraction, String startValue, String endValue) {
		int start_a, start_r, start_g, start_b;
		int end_a, end_r, end_g, end_b;
		// start
		start_a = getIntValue(startValue, 1, 3);
		start_r = getIntValue(startValue, 3, 5);
		start_g = getIntValue(startValue, 5, 7);
		start_b = getIntValue(startValue, 7, 9);
		// end
		end_a = getIntValue(endValue, 1, 3);
		end_r = getIntValue(endValue, 3, 5);
		end_g = getIntValue(endValue, 5, 7);
		end_b = getIntValue(endValue, 7, 9);
		return "#"
				+ getHexString((int) (start_a + fraction * (end_a - start_a)))
				+ getHexString((int) (start_r + fraction * (end_r - start_r)))
				+ getHexString((int) (start_g + fraction * (end_g - start_g)))
				+ getHexString((int) (start_b + fraction * (end_b - start_b)));
	}

	// From the original # AARRGGBB color values specified in the position to intercept, and converted to int.
	private int getIntValue(String hexValue, int start, int end) {
		return Integer.parseInt(hexValue.substring(start, end), 16);
	}

	private String getHexString(int value) {
		String a = Integer.toHexString(value);
		if (a.length() == 1) {
			a = "0" + a;
		}
		return a;
	}
	
	
	//
	public RectF RectFSpace(float size){
		return new RectF(centerX-size, centerY-size, centerX+size, centerY+size);
	}
}