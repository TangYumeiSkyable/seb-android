package com.rihuisoft.loginmode.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;


public class CustomProgressDialog extends Dialog {
    private Context context = null;
    private static CustomProgressDialog customProgressDialog = null;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public static CustomProgressDialog createDialog(Context context) {
        try {
            customProgressDialog = new CustomProgressDialog(context,
                    R.style.CustomProgressDialog);
            customProgressDialog.setContentView(R.layout.progress_dialog);
            customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customProgressDialog;
    }

    @Override
    public void dismiss() {
        //避免内存泄漏，将对应的Activity引用置空 Foking
        this.context = null;
        super.dismiss();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        try {
            if (customProgressDialog == null) {
                return;
            }
            ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
            Animation rotateAnimation = AnimationUtils
                    .loadAnimation(context, R.anim.rotate_anim);
            imageView.setAnimation(rotateAnimation);
            imageView.startAnimation(rotateAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * [Summary] setTitile title
     *
     * @param strTitle
     * @return
     */
    public CustomProgressDialog setTitile(String strTitle) {
        return customProgressDialog;
    }

    /**
     * [Summary] setMessage Prompt content
     *
     * @param strMessage
     * @return
     **/
    public CustomProgressDialog setMessage(String strMessage) {
        try {
            TextView tvMsg = (TextView) customProgressDialog
                    .findViewById(R.id.id_tv_loadingmsg);
            if (tvMsg != null) {
                tvMsg.setText(strMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customProgressDialog;
    }
}
