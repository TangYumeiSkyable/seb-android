package com.rihuisoft.loginmode.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
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


public class AppraiseAlertDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;
	private Button btn_bad;
	private Button btn_bof;
	private Button btn_happy;
	private Button btn_close;
//	private ImageView img_line;
	private Display display;
	private boolean showTitle = false;
	private boolean showMsg = false;
	private boolean showBadBtn = false;
	private boolean showBofBtn = false;
	private boolean showHappyBtn = false;
	private OncloseListener oncloseListener;

	public AppraiseAlertDialog(Context context) {
		try {
			this.context = context;
			WindowManager windowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			display = windowManager.getDefaultDisplay();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public AppraiseAlertDialog builder() {
		try {
			// Get Dialog layout
			View view = LayoutInflater.from(context).inflate(
					R.layout.view_appraisealertdialog, null);
//			view.getBackground().setAlpha(175);
			// To get custom controls within the Dialog layout
			lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
			txt_title = (TextView) view.findViewById(R.id.txt_title);
			txt_title.setVisibility(View.GONE);
			txt_msg = (TextView) view.findViewById(R.id.txt_msg);
			txt_msg.setVisibility(View.GONE);
			btn_bad = (Button) view.findViewById(R.id.btn_bad);
			btn_bad.setVisibility(View.GONE);
			btn_bof = (Button) view.findViewById(R.id.btn_bof);
			btn_bof.setVisibility(View.GONE);
			btn_happy = (Button) view.findViewById(R.id.btn_happy);
			btn_happy.setVisibility(View.GONE);
			btn_close = (Button) view.findViewById(R.id.btn_close);
//			img_line = (ImageView) view.findViewById(R.id.img_line);
//			img_line.setVisibility(View.GONE);

			// Definition Dialog layout and parameters
			dialog = new Dialog(context, R.style.AlertDialogStyle);
			dialog.setContentView(view);

			// To resize the dialog background
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

	public AppraiseAlertDialog setTitle(String title) {
		try {
			showTitle = true;
			if ("".equals(title)) {
				txt_title.setText("title");
			} else {
				txt_title.setText(title);
			}
			txt_title.setTextColor(Color.RED);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public AppraiseAlertDialog setMsg(String msg) {
		try {
			showMsg = true;
			if ("".equals(msg)) {
				txt_msg.setText("content");
			} else {
				txt_msg.setText(msg);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public AppraiseAlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public AppraiseAlertDialog setBadButton(String text,
                                                 final OnClickListener listener) {
		try {
			showBadBtn = true;
			if ("".equals(text)) {
				btn_bad.setText("");
			} else {
				btn_bad.setText(text);
			}
			btn_bad.setOnClickListener(new OnClickListener() {
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

	public AppraiseAlertDialog setBofButton(String text,
                                                 final OnClickListener listener) {
		try {
			showBofBtn = true;
			if ("".equals(text)) {
				btn_bof.setText("");
			} else {
				btn_bof.setText(text);
			}
			btn_bof.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(v);
					dialog.dismiss();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
//
		return this;
	}
	public AppraiseAlertDialog setHappyButton(String text,
												 final OnClickListener listener) {
		try {
			showHappyBtn = true;
			if ("".equals(text)) {
				btn_happy.setText("");
			} else {
				btn_happy.setText(text);
			}
			btn_happy.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(v);
					dialog.dismiss();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
//
		return this;
	}
	private void setLayout() {
		try {
			if (!showTitle && !showMsg) {
				txt_title.setText("prompt");
				txt_title.setVisibility(View.VISIBLE);
			}

			if (showTitle) {
				txt_title.setVisibility(View.VISIBLE);
			}

			if (showMsg) {
				txt_msg.setVisibility(View.VISIBLE);
			}

			btn_bad.setVisibility(View.VISIBLE);
			btn_bof.setVisibility(View.VISIBLE);
			btn_happy.setVisibility(View.VISIBLE);
			btn_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
//			if (!showPosBtn && !showNegBtn) {
//				btn_pos.setText("determine");
//				btn_pos.setVisibility(View.VISIBLE);
//				btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
//				btn_pos.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						dialog.dismiss();
//					}
//				});
//			}
//
//			if (showPosBtn && showNegBtn) {
//				btn_pos.setVisibility(View.VISIBLE);
//				btn_pos.setBackgroundResource(R.drawable.alertdialog_left_selector);
//				btn_neg.setVisibility(View.VISIBLE);
//				btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
//			}
//
//			if (showPosBtn && !showNegBtn) {
//				btn_pos.setVisibility(View.VISIBLE);
//				btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
//			}
//
//			if (!showPosBtn && showNegBtn) {
//				btn_neg.setVisibility(View.VISIBLE);
//				btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
//			}
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

	public void setCloseListener(OncloseListener mOncloseListener) {
		// TODO Auto-generated method stub
		this.oncloseListener = mOncloseListener;
		
	}
	
	public interface OncloseListener {
		void onClose();
	}
}

