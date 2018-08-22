package com.supor.suporairclear.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.service.ACUserDevice;
import com.crashlytics.android.Crashlytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.view.NoScrollViewPager;
import com.supor.suporairclear.common.adapter.BindFragmentAdapter;
import com.supor.suporairclear.common.fragment.CaptureFragment;
import com.supor.suporairclear.common.fragment.DeviceBindFragment;
import com.supor.suporairclear.common.fragment.DevicebindCallback;
import com.supor.suporairclear.common.fragment.WifiConfFragment;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.Constants;
import com.zhy.autolayout.AutoLinearLayout;
import com.zxing.CaptureActivity;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by enyva on 16/5/28.
 */
public class DeviceBindQRActivity extends FragmentActivity implements DevicebindCallback, SurfaceHolder.Callback {
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private BindFragmentAdapter mFragmentAdapter;
    private NoScrollViewPager mPageVp;
    /**
     * The Tab display content TextView
     */
    private TextView mTabModelTv, mTabWifiConfTv, mTabDeviceBindTv;

    /**
     * The guide line of the Tab
     */
    private ImageView mTabLineIv;

    /**
     * Fragment1 model selection
     */
    private CaptureFragment captureFragment;

    /**
     * Fragment wifisetting
     */
    private WifiConfFragment mWifiConfFg;

    /**
     * Fragment Equipment binding
     */
    private DeviceBindFragment mDeviceBindFg;


    /**
     * ViewPager the currently selected page
     */
    private int currentIndex;

    /**
     * The width of the screen
     */
    private int screenWidth;
    // tab1
    private InactivityTimer inactivityTimer;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;


    private Result savedResultToShow;

    private boolean hasSurface;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;

    private TextView header_title;
    private TextView header_left_btn;

    /**
     * Scans the Key
     */
    public static final String KEY_SCAN_RESULT = "scan_result";

    /**
     * Help View's Root
     */
    //private LoadingView mLoadingRoot;
    private FrameLayout mViewStub;

    private static final int CAMERA = 2;
    private AutoLinearLayout mTabContainer;

    ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        try {
            super.onCreate(savedInstanceState);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.dark_blue));
                window.setNavigationBarColor(getResources().getColor(R.color.dark_blue));
            }
            setContentView(R.layout.activity_devicebind_qr);
            AppManager.getAppManager().addActivity(this);
            findById();
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mViewStub = (FrameLayout) findViewById(R.id.loading_root);

            hasSurface = false;
            inactivityTimer = new InactivityTimer(this);
            requestPermission(Manifest.permission.CAMERA, CAMERA);

//            inactivityTimer = new InactivityTimer(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isGranted(String permission) {
        return !isMarshmallow() || isGranted_(permission);
    }

    private boolean isGranted_(String permission) {
        int checkSelfPermission = ActivityCompat.checkSelfPermission(this, permission);
        return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void requestPermission(String permission, int requestCode) {
        if (!isGranted(permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            init();
            initTabLineWidth();
            if (!CameraManager.supportCameraPortrait()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
                initTabLineWidth();
                if (!CameraManager.supportCameraPortrait()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            } else {
                // Permission Denied
                Toast.makeText(this, R.string.airpurifier_no_camera_permission,
                        Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Controls the initialization
     */
    private void findById() {
        try {
            header_title = (TextView) this.findViewById(R.id.header_title);
            header_title.setText(R.string.airpurifier_moredevice_show_adddevice_text);
            header_left_btn = (TextView) this.findViewById(R.id.header_left_btn);
            mTabContainer = (AutoLinearLayout) this.findViewById(R.id.tab_container);
            header_left_btn.setBackgroundResource(R.drawable.ico_back);
            header_left_btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }

            });

            mTabModelTv = (TextView) this.findViewById(R.id.tv_model);
            mTabWifiConfTv = (TextView) this.findViewById(R.id.tv_wifi);
            mTabDeviceBindTv = (TextView) this.findViewById(R.id.tv_bind);

            mTabLineIv = (ImageView) this.findViewById(R.id.iv_tab_line);

            mTabModelTv.setTypeface(AppConstant.ubuntuRegular);
            mTabWifiConfTv.setTypeface(AppConstant.ubuntuLight);
            mTabDeviceBindTv.setTypeface(AppConstant.ubuntuLight);

            mTabModelTv.setTextColor(getApplicationContext().getResources().getColor(R.color.default_blue));
            mPageVp = (NoScrollViewPager) this.findViewById(R.id.vp_page);
            mPageVp.setScanScroll(false);
            Intent aPIntent = getIntent();
            if (aPIntent != null && aPIntent.getBooleanExtra(Constants.ISAP, false)) {
                mTabContainer.setVisibility(View.GONE);
            } else {
                mTabContainer.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Data initialization
     */
    private void init() {
        try {
            captureFragment = new CaptureFragment();
            mWifiConfFg = new WifiConfFragment();
            mDeviceBindFg = new DeviceBindFragment();

            mFragmentList.add(captureFragment);
            mFragmentList.add(mWifiConfFg);
            mFragmentList.add(mDeviceBindFg);

            mFragmentAdapter = new BindFragmentAdapter(
                    this.getSupportFragmentManager(), mFragmentList);
            mPageVp.setAdapter(mFragmentAdapter);
            mPageVp.setCurrentItem(0);

            mPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                /**
                 * The state of the sliding state Has three states (0) : 1 is sliding 2: sliding over 0: didn't do anything.
                 */
                @Override
                public void onPageScrollStateChanged(int state) {

                }

                /**
                 * position :The current page, and the page you click on the slide
                 * offset:The percentage of the current page migration
                 * offsetPixels:The current page offset pixel location
                 */
                @Override
                public void onPageScrolled(int position, float offset,
                                           int offsetPixels) {
                    try {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                                .getLayoutParams();

                        Log.e("offset:", offset + "");
                        /**
                         * Using currentIndex (the current page) and position (the next page), and offset
                         * Set the mTabLineIv from sliding to the left of the scene:
                         * Write three pages,
                         * From left to right are 0, respectively
                         * 0->1; 1->2; 2->1; 1->0
                         */

                        if (currentIndex == 0 && position == 0)// 0->1
                        {
                            lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                                    * (screenWidth / 3));

                        } else if (currentIndex == 1 && position == 0) // 1->0
                        {
                            lp.leftMargin = (int) (-(1 - offset)
                                    * (screenWidth * 1.0 / 3) + currentIndex
                                    * (screenWidth / 3));

                        } else if (currentIndex == 1 && position == 1) // 1->2
                        {
                            lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                                    * (screenWidth / 3) - 6);
                        } else if (currentIndex == 2 && position == 1) // 2->1
                        {
                            lp.leftMargin = (int) (-(1 - offset)
                                    * (screenWidth * 1.0 / 3) + currentIndex
                                    * (screenWidth / 3));
                        }
                        mTabLineIv.setLayoutParams(lp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onPageSelected(int position) {
                    try {
                        resetTextView();
                        switch (position) {
                            case 0:
                                mTabModelTv.setTypeface(AppConstant.ubuntuRegular);
                                mTabModelTv.setTextColor(getApplicationContext().getResources().getColor(R.color.default_blue));
                                break;
                            case 1:
                                mTabWifiConfTv.setTypeface(AppConstant.ubuntuRegular);
                                mTabWifiConfTv.setTextColor(getApplicationContext().getResources().getColor(R.color.default_blue));
                                break;
                            case 2:
                                mTabDeviceBindTv.setTypeface(AppConstant.ubuntuRegular);
                                mTabDeviceBindTv.setTextColor(getApplicationContext().getResources().getColor(R.color.default_blue));
                                break;
                        }
                        currentIndex = position;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Set the width of the slider to a third of the screen (depending on the number of Tab)
     */
    private void initTabLineWidth() {
        try {
            DisplayMetrics dpMetrics = new DisplayMetrics();
            getWindow().getWindowManager().getDefaultDisplay()
                    .getMetrics(dpMetrics);
            screenWidth = dpMetrics.widthPixels;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                    .getLayoutParams();
            lp.width = (screenWidth - 10) / 3;
            mTabLineIv.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Reset the color
     */
    private void resetTextView() {
        try {
            mTabModelTv.setTextColor(getApplicationContext().getResources().getColor(R.color.gray_tab));
            mTabWifiConfTv.setTextColor(getApplicationContext().getResources().getColor(R.color.gray_tab));
            mTabDeviceBindTv.setTextColor(getApplicationContext().getResources().getColor(R.color.gray_tab));
            mTabModelTv.setTypeface(AppConstant.ubuntuLight);
            mTabWifiConfTv.setTypeface(AppConstant.ubuntuLight);
            mTabDeviceBindTv.setTypeface(AppConstant.ubuntuLight);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callbackFun1(Bundle arg) {
//        if(null == mModelFg){//可以避免切换的时候重复创建
//            mModelFg = new ModelFragment();
//        }
//        Bundle data = new Bundle();
//        data.putString("TEXT", "传递给fragment2");
//        mModelFg.setArguments(data);
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//        fragmentTransaction.add(R.id.rl_container,fragment2);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commitAllowingStateLoss();
//        currentFragment = fragment2;
        mPageVp.setCurrentItem(0);

    }

    @Override
    public void callbackFun2(Bundle arg) {
        mPageVp.setCurrentItem(1);
    }

    @Override
    public void callbackFun3(Bundle arg) {
        mPageVp.setCurrentItem(2);
        mDeviceBindFg.init(String.valueOf(ConstantCache.subDomainId), ConstantCache.bindModelName);
    }

    @Override
    public void callbackCapture(Bundle arg) {
        try {
            inactivityTimer.onResume();
            cameraManager = new CameraManager(getApplication());

            viewfinderView = captureFragment.getViewfinderView();

            viewfinderView.setCameraManager(cameraManager);

            handler = null;
            resetStatusView();
            //hideLoading();
            SurfaceView surfaceView = captureFragment.getSurfaceView();
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            if (hasSurface) {
                // The activity was paused but not stopped, so the surface still exists. Therefore
                // surfaceCreated() won't be called, so init the camera here.
                initCamera(surfaceHolder);
            } else {
                // Install the callback and wait for surfaceCreated() to init the camera.
                surfaceHolder.removeCallback(this);
                surfaceHolder.addCallback(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            if (currentIndex == 0) {
                if (handler != null) {
                    handler.quitSynchronously();
                    handler = null;
                }
                inactivityTimer.onPause();
                if (cameraManager != null) {
                    cameraManager.closeDriver();
                }
                //historyManager = null; // Keep for onActivityResult
                if (!hasSurface) {
                    SurfaceView surfaceView = captureFragment.getSurfaceView();
                    SurfaceHolder surfaceHolder = surfaceView.getHolder();
                    surfaceHolder.removeCallback(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            inactivityTimer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
        //hideLoading();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (currentIndex == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:

                        finish();
//                    setResult(RESULT_CANCELED);
//                    finish();
                        break;
                    case KeyEvent.KEYCODE_FOCUS:
                    case KeyEvent.KEYCODE_CAMERA:
                        // Handle these events so they don't launch the Camera app
                        return true;
                    // Use volume up/down to turn on light
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        cameraManager.setTorch(false);
                        return true;
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        cameraManager.setTorch(true);
                        return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Show preview marquee
     */
    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
    }

    /**
     * Decoding/save the Bitmap
     *
     * @param bitmap
     * @param result Scan results
     */
    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        try {
            // Bitmap isn't used yet -- will be used soon
            if (handler == null) {
                savedResultToShow = result;
            } else {
                if (result != null) {
                    savedResultToShow = result;
                }
                if (savedResultToShow != null) {
                    Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                    handler.sendMessage(message);
                }
                savedResultToShow = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (holder == null) {
                Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
            }
            if (!hasSurface) {
                hasSurface = true;
                initCamera(holder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        try {
            inactivityTimer.onActivity();
            //每隔一秒重置二维码扫描
            restartPreviewAfterDelay(1000);
            String result = rawResult.getText();
            //AP 配网需要
            Intent aPIntent = getIntent();
            if (aPIntent != null && aPIntent.getBooleanExtra(Constants.ISAP, false)) {
                if (!result.contains("#") && result.indexOf("&") > 0) {
                    //model&APPLIANCE_1045&subdomainId&subDomainId
                    Intent intent = new Intent();
                    String[] msgArray = result.split("&");
                    for (ACObject mode : ConstantCache.deviceModeList) {
                        if (msgArray[1].equals(mode.get("id").toString())) {
                            ConstantCache.subDomainId = Integer.valueOf(msgArray[3]);
                            ConstantCache.subDomainName = mode.getString("subDomainName");
                            ConstantCache.bindModelName = mode.getString("name");
                            ConstantCache.bindModelPic = mode.getString("img");
                            intent.putExtra(Constants.SUBDOMAINID, Long.valueOf(msgArray[3]));
                            intent.putExtra(Constants.SUBDOMINNAME, mode.getString("subDomainName"));
                            intent.putExtra(Constants.BINDMODELNAME, mode.getString("name"));
                            intent.putExtra(Constants.BINDMODELPIC, mode.getString("img"));
                            this.setResult(Activity.RESULT_OK, intent);
                            if (!DeviceBindQRActivity.this.isFinishing())
                                DeviceBindQRActivity.this.finish();
                            return;
                        }
                    }
                } else if (result.contains("#") && result.split("#").length == 2) {
                    AC.bindMgr().bindDeviceWithShareCode(result,
                            new PayloadCallback<ACUserDevice>() {
                                @Override
                                public void success(ACUserDevice userDevice) {
                                    try {
                                        Toast.makeText(DeviceBindQRActivity.this, DeviceBindQRActivity.this.getString(R.string.airpurifier_moredevice_show_bindsuccessfuly_text), AppConstant.TOAST_DURATION)
                                                .show();
                                        Intent intent = new Intent();
                                        intent.setClass(DeviceBindQRActivity.this, MainActivity.class);
                                        DeviceBindQRActivity.this.startActivity(intent);
                                        ConstantCache.appManager.finishActivity(APAddDeviceActivity.class);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void error(ACException e) {
                                    if (e.getErrorCode() == 3817) {
                                        Toast.makeText(
                                                DeviceBindQRActivity.this,
                                                DeviceBindQRActivity.this.getString(R.string.airpurifier_moredevice_show_qrcodeisoverdue_text),
                                                AppConstant.TOAST_DURATION).show();

                                    } else {
                                        Toast.makeText(
                                                DeviceBindQRActivity.this,
                                                DeviceBindQRActivity.this.getString(R.string.airpurifier_moredevice_show_additionfailed_text),
                                                AppConstant.TOAST_DURATION).show();

                                    }

                                    DeviceBindQRActivity.this.finish();
                                }
                            });

                } else {
                    //不是目标result
                    Toast.makeText(this, getString(R.string.airpurifier_moredevice_show_qrcodeidwrong_text), Toast.LENGTH_SHORT).show();
                }
                return;
                //AP 配网结束
            }
            cameraManager.stopPreview();
            Intent intent = new Intent();
            intent.putExtra(KEY_SCAN_RESULT, rawResult.toString());

            sendReplyMessage(R.id.return_scan_result, intent, 0);
            //loading();
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {
        try {
            if (handler != null) {
                Message message = Message.obtain(handler, id, arg);
                if (delayMS > 0L) {
                    handler.sendMessageDelayed(message, delayMS);
                } else {
                    handler.sendMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialize the Camera
     *
     * @param surfaceHolder Show preview SurfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            if (surfaceHolder == null) {
                throw new IllegalStateException("No SurfaceHolder provided");
            }
            if (cameraManager.isOpen()) {
                Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
                return;
            }
            try {
                cameraManager.openDriver(surfaceHolder);
                // Creating the handler starts the preview, which can also throw a RuntimeException.
                if (handler == null) {
                    handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
                }

                handler.setBindNewDeviceListener(new CaptureActivityHandler.BindNewDeviceListener() {
                    @Override
                    public void bindNewDevice() {
                        Log.i(TAG, "bindNewDevice");
                        captureFragment.displayDevice();
                        if (handler != null) {
                            handler.quitSynchronously();
                            handler = null;
                        }
                        inactivityTimer.onPause();
                        cameraManager.closeDriver();
                        //historyManager = null; // Keep for onActivityResult
                        if (!hasSurface) {
                            SurfaceView surfaceView = captureFragment.getSurfaceView();
                            SurfaceHolder surfaceHolder = surfaceView.getHolder();
                            surfaceHolder.removeCallback(DeviceBindQRActivity.this);
                        }
                    }
                });
                decodeOrStoreSavedBitmap(null, null);
            } catch (IOException ioe) {
                Log.w(TAG, ioe);
                displayFrameworkBugMessageAndExit();
            } catch (RuntimeException e) {
                // Barcode Scanner has seen crashes in the wild of this variety:
                // java.?lang.?RuntimeException: Fail to connect to camera service
                Log.w(TAG, "Unexpected error initializing camera", e);
                displayFrameworkBugMessageAndExit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialize camera fails, pop-up close prompt dialog
     */
    private void displayFrameworkBugMessageAndExit() {
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }


    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }
}

