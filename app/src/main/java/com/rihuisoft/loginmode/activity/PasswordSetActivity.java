package com.rihuisoft.loginmode.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.StringUtils;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.DCPServiceUtils;


public class PasswordSetActivity extends BaseActivity{

	private TextView tv_phone_lbl;
	private EditText phoneEditText;
	private Button next;
	private ImageView del_email;
	ACAccountMgr acMgr;
	private boolean sign_up_flg = false;
	private String phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_password_set);
			AppManager.getAppManager().addActivity(this);
			setNavBtn(R.drawable.ico_back, 0);

			acMgr = AC.accountMgr();

			setTitle(getString(R.string.airpurifier_login_show_reset_title_text));
			MainApplication.getInstance().initLocation();
			initView();
		} catch(Exception e)
		{

		}
	}
	/**
	 * Controls the initialization
	 */
	public void initView() {
		tv_phone_lbl = (TextView) findViewById(R.id.tv_phone_lbl);
		tv_phone_lbl.setTypeface(AppConstant.ubuntuRegular);


		phoneEditText = (EditText) findViewById(R.id.phone);

		del_email = (ImageView) findViewById(R.id.delete_email);
		//phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);

		next = (Button) findViewById(R.id.phone_next);
		next.setTypeface(AppConstant.ubuntuRegular);

		del_email.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				phoneEditText.setText("");
				del_email.setVisibility(View.GONE);
			}
		});
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (!sign_up_flg) {
					return;
				}
				phone = phoneEditText.getText().toString().trim();

				acMgr.checkExist(phone, new PayloadCallback<Boolean>() {
					@Override
					public void success(Boolean isExist) {
						// TODO
						if (!isExist) {
							// The user does not exist
							ShowToast(getString(R.string.airpurifier_login_show_tosphonenotregidter_text));
							return;
						}
						resetPsd(phone);
					}

					@Override
					public void error(ACException e) {
						ShowToast(getString(R.string.airpurifier_login_show_registertosinvalidphonenumber_text));
					}
				});

			}
		});
		phoneEditText.addTextChangedListener(textWatcher);

		next.setBackgroundResource(R.drawable.selectors_btn_disabled);
	}
	private void resetPsd(final String tel) {
		try {
			next.setClickable(false);
			DCPServiceUtils.resetPassword(tel,new PayloadCallback<Boolean>() {
				@Override
				public void success(Boolean result) {
					try {
						next.setClickable(true);
						if (result) {
							ShowToast(getString(R.string.airpurifier_login_show_verifypwdsuccess_text));
							MainApplication.getInstance();
//							MainApplication.UserLogin(new UserInfo(userInfo
//									.getUserId(), userInfo.getName(), phone));
							Intent intent = new Intent(
									PasswordSetActivity.this,
									Phone1Activity.class);
							intent.putExtra("email", tel);
							startActivity(intent);
							finish();
						} else {
							ShowToast(getString(R.string.airpurifier_login_show_passwordresetfail_text));
						}
					} catch(Exception e) {
						e.printStackTrace();
					}

				}

				@Override
				public void error(ACException e) {
					next.setClickable(true);
					ShowToast(getString(R.string.airpurifier_login_show_passwordresetfail_text));
				}
			});

		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			phoneEditText.setTypeface(AppConstant.ubuntuMedium);
			try {
				if (!phoneEditText.getText().toString().isEmpty()) {
					del_email.setVisibility(View.VISIBLE);
				} else {
					del_email.setVisibility(View.GONE);
				}

				sign_up_flg = !phoneEditText.getText().toString().isEmpty() && StringUtils.isEmail(phoneEditText.getText().toString());

				if (sign_up_flg) {
					next.setBackgroundResource(R.drawable.selectors_btn_button);
				} else {
					next.setBackgroundResource(R.drawable.selectors_btn_disabled);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	@Override
	protected void HandleTitleBarEvent(TitleBar component, View v) {
		try {
			if (component == TitleBar.LEFT) {
				Intent intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
				finish();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
