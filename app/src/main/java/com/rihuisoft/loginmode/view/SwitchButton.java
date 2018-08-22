package com.rihuisoft.loginmode.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.groupeseb.airpurifier.R;

public class SwitchButton extends View implements android.view.View.OnClickListener {
    private Bitmap mSwitchBottom, mSwitchThumb, mSwitchFrame, mSwitchMask;
    private float mCurrentX = 0;
    private boolean mSwitchOn = true;//Switch is open by default
    private int mMoveLength;//Largest mobile distance
    private float mLastX = 0;//Press the effective area for the first time

    private Rect mDest = null;//Drawing the size of target area
    private Rect mSrc = null;//The size of the interception of source images
    private int mDeltX = 0;//Moving the offset
    private Paint mPaint = null;
    private OnChangeListener mListener = null;
    private boolean mFlag = false;

    public SwitchButton(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    /**
     * 初始化相关资源
     */
    public void init() {
        try {
            mSwitchBottom = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ico_switch_off_bg);
            mSwitchThumb = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ico_switch_fg);
            mSwitchFrame = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ico_switch_off_bg);
            mSwitchMask = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ico_switch_on_bg);
            setOnClickListener(this);
            setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    return false;
                }
            });

            mMoveLength = mSwitchBottom.getWidth() - mSwitchFrame.getWidth();
            mDest = new Rect(0, 0, mSwitchFrame.getWidth(), mSwitchFrame.getHeight());
            mSrc = new Rect();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setAlpha(255);
            mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        setMeasuredDimension(mSwitchFrame.getWidth(), mSwitchFrame.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        try {
            if (mDeltX > 0 || mDeltX == 0 && mSwitchOn) {
                if (mSrc != null) {
                    mSrc.set(mMoveLength - mDeltX, 0, mSwitchBottom.getWidth()
                            - mDeltX, mSwitchFrame.getHeight());
                }
            } else if (mDeltX < 0 || mDeltX == 0 && !mSwitchOn) {
                if (mSrc != null) {
                    mSrc.set(-mDeltX, 0, mSwitchFrame.getWidth() - mDeltX,
                            mSwitchFrame.getHeight());
                }
            }
            //Here is an off-screen buffer, feel like double buffering mechanism
            int count = canvas.saveLayer(new RectF(mDest), null, Canvas.MATRIX_SAVE_FLAG
                    | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                    | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                    | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

            canvas.drawBitmap(mSwitchBottom, mSrc, mDest, null);
            canvas.drawBitmap(mSwitchThumb, mSrc, mDest, null);
            canvas.drawBitmap(mSwitchFrame, 0, 0, null);
            canvas.drawBitmap(mSwitchMask, 0, 0, mPaint);
            canvas.restoreToCount(count);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurrentX = event.getX();
                    mDeltX = (int) (mCurrentX - mLastX);
                    // If the switch is open scroll left, or switch off the slide to the right (this time is not need to deal with)
                    if ((mSwitchOn && mDeltX < 0) || (!mSwitchOn && mDeltX > 0)) {
                        mFlag = true;
                        mDeltX = 0;
                    }

                    if (Math.abs(mDeltX) > mMoveLength) {
                        mDeltX = mDeltX > 0 ? mMoveLength : -mMoveLength;
                    }
                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(mDeltX) > 0 && Math.abs(mDeltX) < mMoveLength / 2) {
                        mDeltX = 0;
                        invalidate();
                        return true;
                    } else if (Math.abs(mDeltX) > mMoveLength / 2 && Math.abs(mDeltX) <= mMoveLength) {
                        mDeltX = mDeltX > 0 ? mMoveLength : -mMoveLength;
                        mSwitchOn = !mSwitchOn;
                        if (mListener != null) {
                            mListener.onChange(this, mSwitchOn);
                        }
                        invalidate();
                        mDeltX = 0;
                        return true;
                    } else if (mDeltX == 0 && mFlag) {
                        //Got the don't need to be deal with at this moment, for a move has been made
                        mDeltX = 0;
                        mFlag = false;
                        return true;
                    }
                    return super.onTouchEvent(event);
                default:
                    break;
            }
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onTouchEvent(event);
    }

    public void setOnChangeListener(OnChangeListener listener) {
        mListener = listener;
    }

    public interface OnChangeListener {
        public void onChange(SwitchButton sb, boolean state);
    }

    @Override
    public void onClick(View v) {
        try {
            // TODO Auto-generated method stub
            mDeltX = mSwitchOn ? mMoveLength : -mMoveLength;
            mSwitchOn = !mSwitchOn;
            if (mListener != null) {
                mListener.onChange(this, mSwitchOn);
            }
            invalidate();
            mDeltX = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
