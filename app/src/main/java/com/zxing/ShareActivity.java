package com.zxing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.google.zxing.WriterException;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.supor.suporairclear.activity.ShareToTelActivity;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.Config;
import com.zxing.encoding.BitmapUtil;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by Administrator on 2015/4/29.
 */
public class ShareActivity extends BaseActivity {
    private String code;
    private long deviceId;
    private String deviceTcode;
    private ImageView img;
    private RelativeLayout refresh;
    private TextView tv_title, tv_info, share_refresh;

    private static Timer mTimer;
	private TimerTask mTimerTask = null;
	private boolean isTimerNull;
	private Boolean timerDisable = false;
	private boolean isPause = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	deviceTcode = getIntent().getStringExtra("deviceTcode");
        code = getIntent().getStringExtra("shareCode");
        deviceId = getIntent().getLongExtra("deviceId", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        setTitle(getString(R.string.airpurifier_more_show_shareqrcode_text));
        setNavBtn(R.drawable.ico_back, 0);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_info = (TextView) findViewById(R.id.tv_info);
        share_refresh = (TextView) findViewById(R.id.share_refresh);
        
        tv_title.setTypeface(AppConstant.ubuntuRegular);
        tv_info.setTypeface(AppConstant.ubuntuRegular);
        share_refresh.setTypeface(AppConstant.ubuntuRegular);
        
        tv_title.setText("Air Purifier " + deviceTcode);
        

        img = (ImageView) findViewById(R.id.share_img);
        refresh = (RelativeLayout) findViewById(R.id.rl_setting);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ShareActivity.this, ShareToTelActivity.class);
                intent.putExtra("deviceId",deviceId);
                startActivity(intent);

            }
        });
        setImg(code);
    }

    public void onResume() {
    	super.onResume();
    	startTimer();
    }
    public void onPause() {
    	super.onPause();
    	stopTimer();
    }
    private void getCode() {
    	AC.bindMgr().getShareCode(Config.SUBMAJORDOMAIN, deviceId, 5 * 60, new PayloadCallback<String>() {
            @Override
            public void success(String shareCode) {
            	code = shareCode;
            	setImg(code);
            }

            @Override
            public void error(ACException e) {
            	Log.e("getShareCode",  "error:" + e.getErrorCode() + "-->" + e.getMessage());
                
            }
        });
    }
    public void setImg(String code) {
        Bitmap bitmap;
        try {
            bitmap = BitmapUtil.createQRCode(code, getResources().getDimensionPixelOffset(R.dimen.share_img));

            if (bitmap != null) {
                img.setImageBitmap(bitmap);
            }

        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        finish();
    }

	private void startTimer() {
		try {
			if (mTimer == null && mTimerTask == null) {
				isTimerNull = true;
			} else {
				isTimerNull = false;
			}
			if (mTimer == null) {
				mTimer = new Timer();
			}

			if (mTimerTask == null) {
				mTimerTask = new TimerTask() {
					@Override
					public void run() {
						timerDisable = false;
						getCode();
					}
				};
			}

			if (isTimerNull && mTimer != null && mTimerTask != null) {
				mTimer.schedule(mTimerTask, 60000, 240000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopTimer() {
		try {
			timerDisable = true;
			Log.i("RefreshTest", "RefreshTest+DeviceFragment :: timerFlag2 : "
					+ timerDisable);
			if (mTimer != null) {
				mTimer.purge();
				mTimer.cancel();
				mTimer = null;
			}

			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
