package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;

/**
 * Created by enyva on 16/6/2.
 */
public class Phone1Activity extends BaseActivity{

    private Button btn_phone_next;
    private TextView tv_phone_lbl;

	private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    	try {
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_phone1);

            AppManager.getAppManager().addActivity(this);
            setNavBtn(R.drawable.ico_back, 0);

			setTitle(getString(R.string.airpurifier_login_show_reset_title_text));
			email = getIntent().getStringExtra("email");
            findById();
            initView();
    	} catch(Exception e) {
			e.printStackTrace();
		}       
    }

    private void findById() {
    	try {
            btn_phone_next = (Button) this.findViewById(R.id.phone_next);
            tv_phone_lbl = (TextView) this.findViewById(R.id.tv_phone_lbl);

            btn_phone_next.setBackgroundResource(R.drawable.selectors_btn_button);
    	} catch(Exception e) {
			e.printStackTrace();
		}  
        
    }

    private void initView() {
    	try {
			String info = String.format(getString(R.string.airpurifier_login_show_psdinfo_text), email);
			SpannableString  msp = new SpannableString(info);
			msp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_blue)), info.indexOf(email), info.indexOf(email) + email.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			tv_phone_lbl.setText(msp);

    	        btn_phone_next.setOnClickListener(new View.OnClickListener() {
    	            @Override
    	            public void onClick(View v) {
    	                Intent intent = new Intent(Phone1Activity.this, LoginActivity.class);
						intent.putExtra("email", email);
						startActivity(intent);
						finish();
    	            }
    	        });
    	} catch(Exception e) {
			e.printStackTrace();
		}  
       
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
    	try {
    		 switch (component) {
             case LEFT:
                 finish();
                 break;
         }
    	} catch(Exception e) {
			e.printStackTrace();
		} 
       
    }
}
