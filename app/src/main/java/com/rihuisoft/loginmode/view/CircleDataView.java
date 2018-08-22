package com.rihuisoft.loginmode.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;


/**
 * Created by fengjian on 2017/10/27.
 */

public class CircleDataView extends FrameLayout {
    private ImageView mTvCircleBg;
    private TextView mTvFirstLine;
    private TextView mTvSecondLine;
    private TextView mTvTypeDesc;
    private TextView mTvPm25Num;
    private LinearLayout mLlModelPm25;
    private ImageView mIvFaceLevel;
    private LinearLayout mLlModleGas;
    private CircleDateViewType mCircleShowType;
    private View mCircleView;
    private Context mContext;
    private int mPm25Value;
    private int mTvocValue;
    private final int TVOC_A = 1;
    private final int TVOC_B = 2;
    private final int TVOC_C = 3;
    private final int PM_A = 1;
    private final int PM_B = 2;
    private final int PM_C = 3;
    private final int AIR_QUALITY_A = 1;
    private final int AIR_QUALITY_B = 2;
    private final int AIR_QUALITY_C = 3;
    private final int HANLECAROUSEL = 10086;
    private final int HANLECAROUSEL_INTERRUPT = 5000; //PM25和tvoc的切换时间
    private int mPm25Level;

    //设置展示的类型,有TVOC和没有TVOC,没有TVOC的时候只展示PM2.5
    public enum CircleDateViewType {
        HAVAGAS, NOHAVEGAS, HAVAGASNOCAROUSEL
    }

    private CircleDataView(@NonNull Context context) {
        this(context, null);
    }

    public CircleDataView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CircleDataView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCircleView = LayoutInflater.from(context).inflate(R.layout.layout_circle, this, false);
        mContext = context;
        initView(mCircleView);
    }

    private void initView(View view) {
        mTvCircleBg = (ImageView) view.findViewById(R.id.tv_circle_bg);
        mTvFirstLine = (TextView) view.findViewById(R.id.tv_first_line);
        mTvSecondLine = (TextView) view.findViewById(R.id.tv_second_line);
        mTvTypeDesc = (TextView) view.findViewById(R.id.tv_type_desc);
        mTvPm25Num = (TextView) view.findViewById(R.id.tv_pm25_num);
        mLlModelPm25 = (LinearLayout) view.findViewById(R.id.ll_model_pm25);
        mIvFaceLevel = (ImageView) view.findViewById(R.id.iv_face_level);
        mLlModleGas = (LinearLayout) view.findViewById(R.id.ll_modle_gas);
    }

    public void setCircleShowType(CircleDateViewType circleShowType) {
        mCircleShowType = circleShowType;
    }

    public void showCircleDataView() {
        Log.i("dubug_mill", "显示圆圈视图");
        boolean noSetCircleShowType = (mCircleShowType != CircleDateViewType.HAVAGAS) && (mCircleShowType != CircleDateViewType.NOHAVEGAS) && (mCircleShowType != CircleDateViewType.HAVAGASNOCAROUSEL);
        if (noSetCircleShowType) {
            throw new IllegalArgumentException("please set one type for CircleDataView via setCircleShowType()");
        }

        showCircleViewBg();
        if (mCircleShowType == CircleDateViewType.HAVAGAS) {
            changePM25Data();
            changeGASData();
            hanleCarousel();
        } else if (mCircleShowType == CircleDateViewType.NOHAVEGAS) {
            changePM25Data();
            showPM25MOdle();
            noHandlCarousel();
        } else if (mCircleShowType == CircleDateViewType.HAVAGASNOCAROUSEL) { //有PM25和TVOC 但是不显示TVOC
            changePM25Data();
            showPM25MOdle();
            noHandlCarousel();
        }

        if (mCircleView != null) {
            ViewGroup parent = (ViewGroup) mCircleView.getParent();
            if (parent == null) {
                addView(mCircleView);
            }
        }
    }

    private void showCircleViewBg() {
        if (mCircleShowType == CircleDateViewType.HAVAGAS || mCircleShowType == CircleDateViewType.HAVAGASNOCAROUSEL) {
            int airQualitylevel = Math.max(mPm25Level, mTvocValue); //取两个值中的最小值做空气质量指标
            followAirQualityShowDesc(airQualitylevel);
        } else {
            followAirQualityShowDesc(mPm25Level);
            Log.i("dubug_mill", "NOHAVEGAS_mPm25Level:" + mPm25Level);
        }
    }

    private void followAirQualityShowDesc(int air_quality_level) {
        mTvCircleBg.setImageLevel(air_quality_level);
        switch (air_quality_level) {
            case AIR_QUALITY_A:
                mTvFirstLine.setVisibility(View.VISIBLE);
                mTvSecondLine.setVisibility(View.GONE);
                mTvFirstLine.setText(R.string.air_quality_A);
                break;
            case AIR_QUALITY_B:
                mTvFirstLine.setVisibility(View.VISIBLE);
                mTvSecondLine.setVisibility(View.VISIBLE);
                mTvFirstLine.setText(R.string.moderate);
                mTvSecondLine.setText(R.string.pollution);
                break;
            case AIR_QUALITY_C:
                mTvFirstLine.setVisibility(View.VISIBLE);
                mTvSecondLine.setVisibility(View.VISIBLE);
                mTvFirstLine.setText(R.string.high);
                mTvSecondLine.setText(R.string.pollution);
                break;
        }
    }


    private void changePM25Data() {
        if (mPm25Value == 0) {
            mTvPm25Num.setText("--");
        } else {
            mTvPm25Num.setText(String.valueOf(mPm25Value));
        }
    }

    private void showPM25MOdle() {
        if (mTvTypeDesc != null && mContext != null) {
            mTvTypeDesc.setText(mContext.getText(R.string.circle_pm25_des));
            mLlModelPm25.setVisibility(View.VISIBLE);
            mLlModleGas.setVisibility(View.INVISIBLE);
            mIvFaceLevel.setVisibility(View.INVISIBLE);
        }
    }

    private void showGASModle() {
        if (mTvTypeDesc != null && mContext != null) {
            mTvTypeDesc.setText(mContext.getText(R.string.circle_gas_des));
            mLlModelPm25.setVisibility(View.INVISIBLE);
            mLlModleGas.setVisibility(View.VISIBLE);
            mIvFaceLevel.setVisibility(View.VISIBLE);
        }
    }

    private void changeGASData() {
        switch (mTvocValue) {
            case TVOC_A:
                mIvFaceLevel.setImageLevel(TVOC_A);
                break;
            case TVOC_B:
                mIvFaceLevel.setImageLevel(TVOC_B);
                break;
            case TVOC_C:
                mIvFaceLevel.setImageLevel(TVOC_C);
                break;
            default:
                mIvFaceLevel.setImageLevel(TVOC_A);
        }
    }

    public void dismissCircleDataView() {
        removeAllViews();
        mCarouselHandler.removeMessages(HANLECAROUSEL);
        mContext = null;
    }

    public void setPm25Value(int pm25Value) {
        mPm25Value = pm25Value;
    }

    public void setTvocValue(int tvocValue) {
        if ((tvocValue == 1) || (tvocValue == 2) || (tvocValue == 3))
            mTvocValue = tvocValue;
    }

    public void setPM25Level(int pm25Level) {
        mPm25Level = pm25Level;
    }

    private void hanleCarousel() {
        if (!mCarouselHandler.hasMessages(HANLECAROUSEL))
            mCarouselHandler.sendEmptyMessage(HANLECAROUSEL);
    }

    private void noHandlCarousel() {
        if (mCarouselHandler.hasMessages(HANLECAROUSEL))
            mCarouselHandler.removeMessages(HANLECAROUSEL);
    }

    private Handler mCarouselHandler = new Handler() {
        private int count;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean showPM25 = count % 2 == 0;
            if (showPM25) {
                showPM25MOdle();
            } else {
                showGASModle();
            }
            count++;
            sendEmptyMessageDelayed(HANLECAROUSEL, HANLECAROUSEL_INTERRUPT);
        }
    };

}
