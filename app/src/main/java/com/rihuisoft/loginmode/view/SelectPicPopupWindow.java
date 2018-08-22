package com.rihuisoft.loginmode.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.config.AppConstant;

/**
 * Created by Administrator on 2016/6/12.
 */

public class SelectPicPopupWindow extends Activity implements View.OnClickListener {
    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
        btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
        btn_take_photo.setTypeface(AppConstant.ubuntuRegular);
        btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
        btn_pick_photo.setTypeface(AppConstant.ubuntuRegular);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

        layout = (LinearLayout) findViewById(R.id.pop_layout);
        //添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // To obtain the screen width and height
        WindowManager.LayoutParams p = getWindow().getAttributes(); // Get the current dialog parameter values
        p.height = (int) (d.getHeight() * 1.0); //Height is set to 1.0 of the screen
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的0.8

        getWindow().setAttributes(p); // Settings to take effect
        getWindow().setGravity(Gravity.RIGHT); // Set the right alignment

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_take_photo:
                intent = new Intent();
                intent.putExtra("mode", 1);
                setResult(RESULT_OK, intent);
                break;
            case R.id.btn_pick_photo:
                intent = new Intent();
                intent.putExtra("mode", 2);
                setResult(RESULT_OK, intent);
                break;
            case R.id.btn_cancel:
                break;
            default:
                break;
        }
        finish();
    }
}