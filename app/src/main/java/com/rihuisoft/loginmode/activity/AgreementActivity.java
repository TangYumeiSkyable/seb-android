package com.rihuisoft.loginmode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.supor.suporairclear.activity.IFUActivity;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.DCPServiceUtils;

import java.util.ArrayList;

import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentTypeTermOfUse;


/**
 * Created by enyva on 16/6/2.
 */
public class AgreementActivity extends BaseActivity {
    private TextView tv_agreement;
    private TextView textView;
    private TextView tv_title;

    private static String TAG = "AgreementActivity";
    private TextView tvContent;
    private CheckBox cb_accept;
    private Button btnConfirm;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_agreement);
            setTitle(getString(R.string.airpurifier_login_show_register_agreementtitle_text));
            AppManager.getAppManager().addActivity(this);
            setNavBtn(R.drawable.ico_back, 0);
            tv_agreement = (TextView) findViewById(R.id.tv_agreement);
            tv_agreement.setTypeface(AppConstant.ubuntuRegular);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_title.setTypeface(AppConstant.ubuntuRegular);
            textView = (TextView) findViewById(R.id.textView);
            textView.setTypeface(AppConstant.ubuntuRegular);
            tvContent = (TextView) findViewById(R.id.tv_content);
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            cb_accept = (CheckBox) findViewById(R.id.cb_accept);
            btnConfirm = (Button) findViewById(R.id.btn_accept);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agree();
                }
            });
            btnConfirm.setClickable(false);
            cb_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btnConfirm.setBackgroundResource(R.drawable.selectors_btn_button);
                        btnConfirm.setClickable(true);
                    } else {
                        btnConfirm.setBackgroundResource(R.drawable.selectors_btn_disabled);
                        btnConfirm.setClickable(false);
                    }
                }
            });

            //处理Agreement富文本
            String beforeText = getString(R.string.airpurifier_login_show_registerreadagreement_text) + " ";
            String afterText = getString(R.string.airpurifier_user_agreement_content);

            SpannableString spannableString = new SpannableString(beforeText + afterText);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.btn_orange));
            spannableString.setSpan(foregroundColorSpan, beforeText.length(), (beforeText + afterText).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            tv_agreement.setText(spannableString);

            tv_agreement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(AgreementActivity.this, IFUActivity.class);
                    intent1.putExtra("titlebar", getString(R.string.airpurifier_more_show_personaldata_text));
                    intent1.putExtra("data", "1");
                    startActivity(intent1);
                }
            });
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The initial control
     */
    private void initView() {
        try {
            DCPServiceUtils.syncContent(DCPServiceContentTypeTermOfUse, new PayloadCallback<ACMsg>() {
                @Override
                public void error(ACException e) {

                }

                @Override
                public void success(ACMsg acMsg) {
                    //fix bug   issue -486
                    try {
                        ACObject acObject  =  (ACObject) acMsg.getObjectData().get("content");
                        if(acObject != null){
                            ArrayList objects = (ArrayList) acObject.get("objects");
                            if(objects != null && !objects.isEmpty()){
                                String body = ((ACObject) objects.get(0)).contains("body") ? (String) ((ACObject) objects.get(0)).get("body") : "";
                                tvContent.setText(Html.fromHtml(body));
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void agree() {
        try {
            Intent intent = new Intent(AgreementActivity.this, PhoneActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            switch (component) {
                case LEFT:
                    finish();
                    break;
                case RIGHT:
                    break;
                case RIGHT2:
                    break;
                case TITLE:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
