package com.rihuisoft.loginmode.view;

import android.app.Dialog;
import android.content.Context;
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


public class AlertDialogUserDifine {
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

	public AlertDialogUserDifine(Context context) {
		try {
			this.context = context;
			WindowManager windowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			display = windowManager.getDefaultDisplay();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public AlertDialogUserDifine builder() {
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
			((View)btn_neg.getParent()).setVisibility(View.GONE);
			btn_pos = (Button) view.findViewById(R.id.btn_pos);
			btn_pos.setVisibility(View.GONE);
//			img_line = (ImageView) view.findViewById(R.id.img_line);
//			img_line.setVisibility(View.GONE);

			// Definition Dialog layout and parameters
			dialog = new Dialog(context, R.style.AlertDialogStyle);
			dialog.setContentView(view);

			//To resize the dialog background
			lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
					.getWidth() * 0.83), LayoutParams.WRAP_CONTENT));
		} catch(Exception e) {
			e.printStackTrace();
		}
		

		return this;
	}

	public AlertDialogUserDifine setTitle(String title) {
		try {
			showTitle = true;
			if ("".equals(title)) {
				txt_title.setText(R.string.airpurifier_notification_show_title_text);
			} else {
				txt_title.setText(title);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public AlertDialogUserDifine setMsg(String msg) {
		try {
			showMsg = true;
			if ("".equals(msg)) {
				txt_msg.setText(R.string.airpurifier_notification_show_message_text);
			} else {
				txt_msg.setText(msg);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public AlertDialogUserDifine setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public AlertDialogUserDifine setPositiveButton(String text,
			final OnClickListener listener) {
		try {
			showPosBtn = true;
			if ("".equals(text)) {
				btn_pos.setText(R.string.airpurifier_public_ok);
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

	public AlertDialogUserDifine setNegativeButton(String text,
			final OnClickListener listener) {
		try {
			showNegBtn = true;
			if ("".equals(text)) {
				btn_neg.setText(R.string.airpurifier_more_show_cancel_button);
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
	public AlertDialogUserDifine setNegativeButton(String text,
												   final OnClickListener listener, boolean flg) {
		try {
			showNegBtn = true;
			if ("".equals(text)) {
				btn_neg.setText(R.string.airpurifier_more_show_cancel_button);
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
				txt_title.setText(R.string.airpurifier_dialog_show_cue_text);
				txt_title.setVisibility(View.VISIBLE);
			}

			if (showTitle) {
				txt_title.setVisibility(View.VISIBLE);
			}

			if (showMsg) {
				txt_msg.setVisibility(View.VISIBLE);
			}

			if (!showPosBtn && !showNegBtn) {
				btn_pos.setText(R.string.airpurifier_public_ok);
				btn_pos.setVisibility(View.VISIBLE);
				btn_pos.setBackgroundResource(R.drawable.selectors_btn_button);
				btn_pos.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}

			if (showPosBtn && showNegBtn) {
				btn_pos.setVisibility(View.VISIBLE);
				btn_pos.setBackgroundResource(R.drawable.selectors_btn_button);
				btn_neg.setVisibility(View.VISIBLE);
				((View)btn_neg.getParent()).setVisibility(View.VISIBLE);
				btn_neg.setBackgroundResource(R.drawable.selectors_btn_black);
//				img_line.setVisibility(View.VISIBLE);
			}

			if (showPosBtn && !showNegBtn) {
				btn_pos.setVisibility(View.VISIBLE);
				btn_pos.setBackgroundResource(R.drawable.selectors_btn_button);
			}

			if (!showPosBtn && showNegBtn) {
				((View)btn_neg.getParent()).setVisibility(View.VISIBLE);
				btn_neg.setVisibility(View.VISIBLE);
				btn_neg.setBackgroundResource(R.drawable.selectors_btn_black);
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
	public boolean isShow(){
		return dialog.isShowing();
	}
}

