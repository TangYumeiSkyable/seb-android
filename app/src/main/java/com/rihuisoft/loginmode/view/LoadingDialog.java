package com.rihuisoft.loginmode.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.Logger;


public class LoadingDialog extends Dialog {
    private Context mContext;
    private ImageView mImageView;
    private boolean isAlreadyShow;

    public LoadingDialog(@NonNull Context context) {
        this(context, -1);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.CustomProgressDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        mImageView = (ImageView) findViewById(R.id.loadingImageView);
        setCanceledOnTouchOutside(false);

    }

    public boolean isAlreadyShow() {
        return isAlreadyShow;
    }

    @Override
    public void show() {
        Logger.i("LoadingDialog show ");
        isAlreadyShow = true;
        super.show();

    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (mContext != null) {
            mImageView = (ImageView) findViewById(R.id.loadingImageView);

            Animation rotateAnimation = AnimationUtils
                    .loadAnimation(mContext, R.anim.rotate_anim);
            mImageView.setAnimation(rotateAnimation);
            mImageView.startAnimation(rotateAnimation);
        }
    }

    @Override
    public void dismiss() {
        Logger.i("LoadingDialog dismiss ");
        isAlreadyShow = false;
        super.dismiss();

    }
}
