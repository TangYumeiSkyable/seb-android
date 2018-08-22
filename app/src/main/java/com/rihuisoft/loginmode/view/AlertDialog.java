package com.rihuisoft.loginmode.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;


public class AlertDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;
	private Button btn_neg;
	private Button btn_pos;
//	private ImageView img_line;
	private Display display;
	private boolean showTitle = false;
	private boolean showMsg = false;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;
	private OncloseListener oncloseListener;

	public AlertDialog(Context context) {
		try {
			this.context = context;
			WindowManager windowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			display = windowManager.getDefaultDisplay();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public AlertDialog builder() {
		try {
			// Get Dialog layout
			View view = LayoutInflater.from(context).inflate(
					R.layout.view_alertdialog, null);

			// To get custom controls within the Dialog layout
			lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
			txt_title = (TextView) view.findViewById(R.id.txt_title);
			txt_title.setVisibility(View.GONE);
			txt_msg = (TextView) view.findViewById(R.id.txt_msg);
			txt_msg.setVisibility(View.GONE);
			btn_neg = (Button) view.findViewById(R.id.btn_neg);
			btn_neg.setVisibility(View.GONE);
			btn_pos = (Button) view.findViewById(R.id.btn_pos);
			btn_pos.setVisibility(View.GONE);
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

	public AlertDialog setTitle(String title) {
		try {
			showTitle = true;
			if ("".equals(title)) {
				txt_title.setText("title");
			} else {
				txt_title.setText(title);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public AlertDialog setMsg(String msg) {
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

	public AlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public AlertDialog setPositiveButton(String text,
			final OnClickListener listener) {
		try {
			showPosBtn = true;
			if ("".equals(text)) {
				btn_pos.setText("sure");
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

	public AlertDialog setNegativeButton(String text,
			final OnClickListener listener) {
		try {
			showNegBtn = true;
			if ("".equals(text)) {
				btn_neg.setText("cancle");
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

			if (!showPosBtn && !showNegBtn) {
				btn_pos.setText("determine");
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
//				img_line.setVisibility(View.VISIBLE);
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

	public void setCloseListener(OncloseListener mOncloseListener) {
		// TODO Auto-generated method stub
		this.oncloseListener = mOncloseListener;
		
	}
	
	public interface OncloseListener {
		void onClose();
	}
}

