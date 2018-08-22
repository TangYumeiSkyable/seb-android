package com.rihuisoft.loginmode.activity;


import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACNotificationMgr;
import com.crashlytics.android.Crashlytics;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.BaseTitleLayout;
import com.rihuisoft.loginmode.view.LoadingDialog;
import com.rihuisoft.loginmode.view.UmengMessageAlertDialog;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.CommendInfo;
import com.umeng.message.PushAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import io.fabric.sdk.android.Fabric;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by zhaoqy on 2015/4/3.
 */
@SuppressLint("NewApi")
public abstract class BaseActivity extends FragmentActivity {

    private BaseTitleLayout layout;

    private Toast mToast;

    Animation shake;


    GestureDetector mGestureDetector;

    private boolean mNeedBackGesture = false;
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());

        //友盟统计应用启动数据
        PushAgent.getInstance(this).onAppStart();
        Logger.i(this.getClass().getSimpleName() + ":onCreate");
        try {
            super.onCreate(savedInstanceState);
            shake = AnimationUtils.loadAnimation(this,
                    R.anim.shake);

            AppUtils.setLopStatBar(this, getResources().getColor(R.color.dark_blue));

            mLoadingDialog = new LoadingDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (mNeedBackGesture) {
            return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }


    public void setNeedBackGesture(boolean mNeedBackGesture) {
        this.mNeedBackGesture = mNeedBackGesture;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @Override
    public void setContentView(int layoutResID) {
        try {
            if (layout == null) {
                layout = new BaseTitleLayout(this, layoutResID);
            }
            layout.setFitsSystemWindows(true);
            super.setContentView(layout);
            this.setClickListener(new View[]{layout.leftll, layout.rightrl, layout.right2Btn});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        try {
            if (!TextUtils.isEmpty(title))
                layout.title.setText(title);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param leftBtnDraw
     * @param rightBtnDraw
     */
    public void setNavBtn(int leftBtnDraw, int rightBtnDraw) {
        this.setNavBtn(leftBtnDraw, rightBtnDraw, 0);
    }

    public void setNavBtn(int leftBtnDraw, int rightBtnDraw, int right2BtnDraw) {
        if (layout != null) {
            setSingleNavBtn(layout.leftBtn, leftBtnDraw);
            setSingleNavBtn(layout.rightBtn, rightBtnDraw);
            setSingleNavBtn(layout.right2Btn, right2BtnDraw);
        }
    }

    public void setNavBtn(int leftBtnDraw, String leftVal, int rightBtnDraw, String rightVal) {
        if (layout != null) {
            setSingleNavBtn(layout.leftBtn, leftBtnDraw, leftVal);
            setSingleNavBtn(layout.rightBtn, rightBtnDraw, rightVal);
        }
    }

    /**
     * @param btn
     * @param res
     */
    private void setSingleNavBtn(TextView btn, int res) {
        try {
            if (res > 0) {
                btn.setVisibility(View.VISIBLE);
                btn.setBackgroundResource(res);
            } else {
                btn.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setSingleNavBtn(TextView btn, int res, String value) {
        try {
            if (res > 0) {
                btn.setBackgroundResource(res);
            } else if (!TextUtils.isEmpty(value)) {
                btn.setText(value);
            } else {
                btn.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param views
     */
    private void setClickListener(View[] views) {
        try {
            for (View v : views) {
                v.setOnClickListener(listener);
                //感觉这个隐藏软键盘没有用，先隐藏掉 Foking
                //hideSoftInput(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                if (view.equals(layout.leftll)) {
                    HandleTitleBarEvent(TitleBar.LEFT, view);
                } else if (view.equals(layout.rightrl)) {
                    HandleTitleBarEvent(TitleBar.RIGHT, view);
                } else if (view.equals(layout.right2Btn)) {
                    HandleTitleBarEvent(TitleBar.RIGHT2, view);
                } else if (view.equals(layout.title)) {
                    HandleTitleBarEvent(TitleBar.TITLE, view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * @param component
     * @param v
     */
    protected abstract void HandleTitleBarEvent(TitleBar component, View v);

    public enum TitleBar {
        LEFT,
        RIGHT,
        RIGHT2,
        TITLE
    }

    // 鑷畾涔塗oast
    public void ShowToast(String text) {
        try {
            if (!TextUtils.isEmpty(text)) {
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), text,
                            AppConstant.TOAST_DURATION);
                } else {
                    mToast.setText(text);
                }
                mToast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void hideSoftInput(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showDialog() {
        Completable
                .complete()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (mLoadingDialog != null
                                && !mLoadingDialog.isAlreadyShow()
                                && !mLoadingDialog.isShowing()
                                && !BaseActivity.this.isFinishing()) {
                            mLoadingDialog.show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable.getMessage());
                    }
                });

    }

    public void dimissDialog() {
        Completable
                .complete()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (mLoadingDialog != null
                                && mLoadingDialog.isAlreadyShow()
                                && !BaseActivity.this.isFinishing()) {
                            mLoadingDialog.dismiss();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable.getMessage());
                    }
                });
    }

    public void hideTitleBar() {
        try {
            View view = (View) findViewById(R.id.titlebar_layout);
            view.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fixInputMethodManagerLeak(this);
        AppManager.getAppManager().finishActivity(this);
        Logger.i(this.getClass().getSimpleName() + ":onDestroy");
    }

    public void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


}
