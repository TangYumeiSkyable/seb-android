package com.rihuisoft.loginmode.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.activity.MainActivity;
import com.supor.suporairclear.activity.TimerSetActivity;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.CommendInfo;

public class StartTimerPopWindow extends PopupWindow implements View.OnClickListener {
	private View conentView;
	private TextView start_timer;
	private long deviceId;
	private String deviceName;

	private int locx;
	private Activity context;
	private ACMsgHelper msgHelper = new ACMsgHelper();

	public StartTimerPopWindow(final Activity context, View v, long deviceId, String deviceName) {
		try {
			this.deviceId = deviceId;
			this.deviceName = deviceName;
			this.context = context;
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			conentView = inflater.inflate(R.layout.start_timer_popup_dialog, null);
			conentView.getBackground().setAlpha(160);
			start_timer = (TextView) conentView.findViewById(R.id.tv_starttimer);
			start_timer.setOnClickListener(this);

			int h = context.getWindowManager().getDefaultDisplay().getHeight();
			int w = context.getWindowManager().getDefaultDisplay().getWidth();

			locx = v.getMeasuredWidth()/2;
			// Setting SelectPicPopupWindow View
			this.setContentView(conentView);
			// Setting SelectPicPopupWindow The pop-up window wide
//			this.setWidth(width);
			this.setWidth(v.getMeasuredWidth()*2);
			// setting SelectPicPopupWindowThe pop-up window hight
			this.setHeight(LayoutParams.WRAP_CONTENT);
			// setting SelectPicPopupWindow Click the pop-up window
			this.setFocusable(true);
			this.setOutsideTouchable(true);
			// Refresh the state
			this.update();
			//Instantiate a ColorDrawable translucent in color
			ColorDrawable dw = new ColorDrawable(0000000000);
			// Point and other places to make it disappear back key, set this to trigger OnDismisslistener, set the other controls changes, and so on
			this.setBackgroundDrawable(dw);
			// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
			//Set SelectPicPopupWindow pop-up window animation effects
//			this.setAnimationStyle(R.style.AnimationPreview);
		} catch(Exception e) {
			e.printStackTrace();
		}
		

	}

	public void showPopupWindow(View parent) {
		try {
			if (!this.isShowing()) {
				int[] location = new int[2];
				parent.getLocationOnScreen(location);
//				this.setWidth(parent.getWidth());
				this.showAtLocation(parent, Gravity.NO_GRAVITY, location[0]-locx, location[1]);
			} else {
				this.dismiss();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.tv_starttimer:
				this.dismiss();
				Intent intent = new Intent(context, TimerSetActivity.class);
				intent.putExtra("url", "file:///android_asset/appointment-list.html");
				intent.putExtra("page", 1);
				intent.putExtra("deviceId", deviceId);
				intent.putExtra("deviceName", deviceName);
				intent.putExtra("src", 1);
				MainActivity.deviceId = deviceId;
				context.startActivity(intent);

				break;

		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
