package com.rihuisoft.loginmode.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.config.AppConstant;

public class UmengMessageAlertDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private LinearLayout ll_air, ll_set_worktime;
	private TextView txt_title;
	private TextView txt_pm25, txt_hcho, txt_msg2, txt_set_worktime;
	private TextView txt_msg;
	private Button btn_neg;
	private Button btn_pos;
//	private ImageView img_line;
	private Display display;
	private boolean showTitle = false;
	private boolean showMsg = false;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;
	// 0: filter replacement;1: empty net open;2: go to work time
	private int type = 0;
	private OncloseListener oncloseListener;

	public UmengMessageAlertDialog(Context context) {
		try {
			this.context = context;
			WindowManager windowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			display = windowManager.getDefaultDisplay();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public UmengMessageAlertDialog builder() {
		try {
			View view = LayoutInflater.from(context).inflate(
					R.layout.umeng_message_alertdialog, null);

			lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
			ll_air = (LinearLayout) view.findViewById(R.id.ll_air);
			ll_air.setVisibility(View.GONE);
			ll_set_worktime = (LinearLayout) view.findViewById(R.id.ll_set_worktime);
			ll_set_worktime.setVisibility(View.GONE);
			txt_title = (TextView) view.findViewById(R.id.txt_title);
			txt_title.setVisibility(View.GONE);
			txt_title.setTypeface(AppConstant.ubuntuMedium);
			txt_msg = (TextView) view.findViewById(R.id.txt_msg);
			txt_msg.setVisibility(View.GONE);
			txt_msg.setTypeface(AppConstant.ubuntuRegular);
			btn_neg = (Button) view.findViewById(R.id.btn_neg);
			btn_neg.setVisibility(View.GONE);
			btn_neg.setTypeface(AppConstant.ubuntuRegular);
			btn_pos = (Button) view.findViewById(R.id.btn_pos);
			btn_pos.setVisibility(View.GONE);
			btn_pos.setTypeface(AppConstant.ubuntuRegular);
			
			txt_pm25 = (TextView) view.findViewById(R.id.txt_pm25);
			txt_pm25.setTypeface(AppConstant.pfBold);
			txt_hcho = (TextView) view.findViewById(R.id.txt_hcho);
			txt_hcho.setTypeface(AppConstant.pfBold);
			txt_msg2 = (TextView) view.findViewById(R.id.txt_msg2);
			txt_msg2.setVisibility(View.GONE);
			txt_msg2.setTypeface(AppConstant.ubuntuRegular);

			txt_set_worktime = (TextView) view.findViewById(R.id.txt_set_worktime);
			txt_set_worktime.setVisibility(View.GONE);
			txt_set_worktime.setTypeface(AppConstant.ubuntuRegular);
			
			dialog = new Dialog(context, R.style.AlertDialogStyle);
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.setContentView(view);

			lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
					.getWidth() * 0.68), LayoutParams.WRAP_CONTENT));

			dialog.setOnDismissListener(new OnDismissListener() {
	            @Override
	            public void onDismiss(DialogInterface dialog) {
	            	if(null != oncloseListener){
	            		oncloseListener.onClose();
	            		}
	            	}
	        });
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public UmengMessageAlertDialog setFilterChange(String deviceName, String cost, String filterName) {
		try {
			type = 0;
			showMsg = true;
			String str = context.getString(R.string.UmengMessageAlertDialog_content_one) + deviceName + " " + filterName + context.getString(R.string.UmengMessageAlertDialog_content_two) + cost + context.getString(R.string.UmengMessageAlertDialog_content_three);

			SpannableStringBuilder style=new SpannableStringBuilder(str);

			style.setSpan(new StyleSpan(AppConstant.pfBold.BOLD), 2, (2 + deviceName.length()), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

			txt_msg.setText(style);
			txt_msg.setText(style);
		}  catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public UmengMessageAlertDialog setDeviceStart(String pm25, String hcho) {
		try {
			type = 1;
			txt_title.setText(R.string.umeng_message_alert_current_air_quality);
			txt_pm25.setText(pm25);
			String tvocLevel = context.getString(R.string.severe_pollution);
			if ("1".equals(hcho)) {
				tvocLevel = context.getString(R.string.umeng_message_alert_excellent);
			} else if ("2".equals(hcho)) {
				tvocLevel = context.getString(R.string.umeng_message_alert_good);
			}  else if ("3".equals(hcho)) {
				tvocLevel = context.getString(R.string.umeng_message_alert_terrible);
			}
			txt_hcho.setText(tvocLevel);
			txt_msg.setText(R.string.umeng_message_alert_advises);
		}  catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public UmengMessageAlertDialog setWorktime(String time, String deviceName, String workType, final OnClickListener listener) {
		try {
			type = 2;
			String wt = "";
			if ("0".equals(workType)) {
				wt = context.getString(R.string.umeng_message_alert_off_duty);
			} else if ("1".equals(workType)) {
				wt = context.getString(R.string.umeng_message_alert_on_duty);
			}
			String str = context.getString(R.string.umeng_message_alert_content_four) + deviceName + context.getString(R.string.umeng_message_alert_content_five)+ wt + ".";
			SpannableStringBuilder style =new SpannableStringBuilder(str);
			style.setSpan(new StyleSpan(AppConstant.pfBold.BOLD), (str.length() - 5 - deviceName.length()), (str.length() - 5), Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 
			txt_msg2.setText(style);
			txt_set_worktime.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(v);
					dialog.dismiss();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}

		return this;
	}

	public UmengMessageAlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public UmengMessageAlertDialog setPositiveButton(String text,
			final OnClickListener listener) {
		try {
			showPosBtn = true;
			if ("".equals(text)) {
				btn_pos.setText(R.string.umeng_message_alert_choose_ok);
			} else {
				btn_pos.setText(text);
			}
			btn_pos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(v);
					dialog.dismiss();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public UmengMessageAlertDialog setNegativeButton(String text,
			final OnClickListener listener) {
		try {
			showNegBtn = true;
			if ("".equals(text)) {
				btn_neg.setText(R.string.umeng_message_alert_choose_cancel);
			} else {
				btn_neg.setText(text);
			}
			btn_neg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(v);
					dialog.dismiss();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}

	private void setLayout() {
		try {
			if (type == 0) {
				txt_msg.setVisibility(View.VISIBLE);
			} else if (type == 1) {
				ll_air.setVisibility(View.VISIBLE);
				txt_msg.setVisibility(View.VISIBLE);
				txt_title.setVisibility(View.VISIBLE);
				txt_msg.setTypeface(AppConstant.ubuntuRegular);
			} else if (type == 2) {
				ll_set_worktime.setVisibility(View.VISIBLE);
				txt_msg2.setVisibility(View.VISIBLE);
				txt_set_worktime.setVisibility(View.VISIBLE);
			}

			if (!showPosBtn && !showNegBtn) {
				btn_pos.setText(R.string.umeng_message_alert_choose_ok);
				btn_pos.setVisibility(View.VISIBLE);
				btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
				btn_pos.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}

			if (showPosBtn && showNegBtn) {
				btn_pos.setVisibility(View.VISIBLE);
				btn_pos.setBackgroundResource(R.drawable.alertdialog_left_selector);
				btn_neg.setVisibility(View.VISIBLE);
				btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
			}

			if (showPosBtn && !showNegBtn) {
				btn_pos.setVisibility(View.VISIBLE);
				btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
			}

			if (!showPosBtn && showNegBtn) {
				btn_neg.setVisibility(View.VISIBLE);
				btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public void show() {
		try {
			setLayout();
			dialog.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void dismiss() {
		dialog.dismiss();
	}


	public interface OncloseListener {
		void onClose();
	}
}

