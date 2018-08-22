package com.supor.suporairclear.common.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.ACDeviceActivator;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACDeviceBind;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.service.ACUserDevice;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.supor.suporairclear.activity.AddDeviceActivity;
import com.supor.suporairclear.activity.DeviceBindActivity;
import com.supor.suporairclear.activity.DeviceBindQRActivity;
import com.supor.suporairclear.activity.MainActivity;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.Config;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.model.CommendInfo;

import java.util.List;

/**
 * Created by enyva on 16/5/28.
 */
public class DeviceBindFragment extends Fragment{
    private LinearLayout ll_search, ll_deviceinfo, ll_error;
    private Button btn_ok, btn_retry, btn_add;
    private ACDeviceActivator deviceActivator;
    private TextView tv_mac_tag;
    private EditText et_name;
    private TextView tv_wait_info, tv_mac, tv_name_tag, tv_error1, tv_error2, tv_error3, tv_retry;
    private ImageView fireImg;
    private AnimationDrawable animDrawable;
    private long deviceId;
    private ACMsgHelper msgHelper = new ACMsgHelper();
    // The device manager
    private ACBindMgr bindMgr;
    private String subDomainId="",name="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View bindView = inflater.inflate(R.layout.fragment_binddevice, container,false);
        try {
        	 
             ll_search = (LinearLayout) bindView.findViewById(R.id.ll_search);
             ll_deviceinfo = (LinearLayout) bindView.findViewById(R.id.ll_deviceinfo);
             ll_error = (LinearLayout) bindView.findViewById(R.id.ll_error);
             btn_ok = (Button) bindView.findViewById(R.id.btn_ok);
             btn_retry = (Button) bindView.findViewById(R.id.btn_retry);
             btn_add = (Button) bindView.findViewById(R.id.btn_add);
             tv_mac_tag = (TextView)bindView.findViewById(R.id.tv_mac_tag);
             et_name = (EditText) bindView.findViewById(R.id.et_name);
             
             tv_wait_info = (TextView)bindView.findViewById(R.id.tv_wait_info);
             tv_mac = (TextView)bindView.findViewById(R.id.tv_mac);
             tv_name_tag = (TextView)bindView.findViewById(R.id.tv_name_tag);
             tv_error1 = (TextView)bindView.findViewById(R.id.tv_error1);
             tv_error2 = (TextView)bindView.findViewById(R.id.tv_error2);
             tv_error3 = (TextView)bindView.findViewById(R.id.tv_error3);
             tv_retry = (TextView)bindView.findViewById(R.id.tv_retry);
             
             fireImg = (ImageView)bindView.findViewById(R.id.fireImg);
             animDrawable = (AnimationDrawable) fireImg  
                     .getBackground();  
             
             
             tv_retry.setTypeface(AppConstant.notoscan);
             btn_ok.setTypeface(AppConstant.ubuntuRegular);
             btn_retry.setTypeface(AppConstant.ubuntuRegular);
             btn_add.setTypeface(AppConstant.ubuntuRegular);
             tv_mac_tag.setTypeface(AppConstant.ubuntuRegular);
             et_name.setTypeface(AppConstant.ubuntuRegular);
             tv_wait_info.setTypeface(AppConstant.ubuntuRegular);
             tv_mac.setTypeface(AppConstant.ubuntuRegular);
             tv_name_tag.setTypeface(AppConstant.ubuntuRegular);
             tv_error1.setTypeface(AppConstant.ubuntuRegular);
             tv_error2.setTypeface(AppConstant.ubuntuRegular);
             tv_error3.setTypeface(AppConstant.ubuntuRegular);

             if (getActivity().getClass().getName().equals("com.supor.suporairclear.activity.DeviceBindActivity")) {
             	btn_add.setText("Scan QR code to add it");
             } else {
             	btn_add.setText("Manually Add");
             }
             
             btn_ok.setEnabled(true);
             btn_add.setOnClickListener(new OnClickListener() {
     			
     			@Override
     			public void onClick(View v) {
     				Intent intent = null;
     				if ("Scan QR code to add it".equals(btn_add.getText().toString())) {
     					intent = new Intent(getActivity(), DeviceBindQRActivity.class);
     				} else {
     					intent = new Intent(getActivity(), DeviceBindActivity.class);
     				}
     				startActivity(intent);
     				getActivity().finish();
     			}
     		});
             
        } catch(Exception e) {
  			e.printStackTrace();
  		}
       
        return bindView;
    }
    
    public void init(String subDomainId,String name) {
    	try {
    		deviceActivator = AC.deviceActivator(ACDeviceActivator.BL);
            bindMgr = AC.bindMgr();
            this.name=name;
            this.subDomainId=subDomainId;
            deviceActive(ConstantCache.wifiPasswoed);


            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	if (!et_name.getText().toString().isEmpty()) {
                		bindDeviceProcess(et_name.getText().toString(),ConstantCache.physicalDeviceId);
                	}
                    
                }
            });
            btn_retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	getActivity().finish();
                }
            });
    	} catch(Exception e) {
  			e.printStackTrace();
  		}
        
    }

    public void deviceActive(String password) {
        try {
            ll_search.setVisibility(View.VISIBLE);
            ll_deviceinfo.setVisibility(View.GONE);
            ll_error.setVisibility(View.GONE);
            animDrawable.start();

        } catch (Exception e) {
            Log.i("Device Bind Failed", e.getMessage());
        }
    }

    private void showDeviceInfo(ACDeviceBind deviceBind) {
    	try {
           tv_mac.setText(subString(deviceBind.getPhysicalDeviceId()));
             et_name.setText(name);
    	     et_name.addTextChangedListener(nameWatcher);
    	} catch(Exception e) {
  			e.printStackTrace();
  		}
       
    }

    private String subString(String str1){
        String str2 = "";
        for(int i=0;i<str1.length();i++){
            if(i*2+2>str1.length()){
                str2 += str1.substring(i*2, str1.length());
                break;
            }
            str2 += str1.substring(i*2, i*2+2)+":";
        }
        if(str2.endsWith(":")){
            str2 = str2.substring(0, str2.length()-1);
        }
        return new StringBuilder(str2).toString();
    }

    private TextWatcher nameWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            if("".equals(s.toString())){
                btn_ok.setEnabled(false);
            }else{
            	btn_ok.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    private void bindDeviceProcess(String deviceName,String physicalDeviceId)
    {
        try
        {
            AC.bindMgr().bindDevice(ConstantCache.subDomainName, physicalDeviceId, deviceName, new PayloadCallback<ACUserDevice>() {
                @Override
                public void success(ACUserDevice userDevice) {
                    
                    try {
                    	deviceId = userDevice.getDeviceId();
                        Toast.makeText(getActivity(),getString(R.string.airpurifier_moredevice_show_bindsuccessfuly_text), AppConstant.TOAST_DURATION).show();
                        CommendInfo commend = new CommendInfo();
                		commend.setDeviceId(deviceId);
                		commend.setCommend("pm25");
                		commend.setValue(0);
                		controlDevice(ConstantCache.subDomainName,commend, 0);
//                        Intent intent = new Intent(getActivity(), MainActivity.class);
//                        startActivity(intent);
                        
                    } catch (Exception e) {
                    	
                    }
                    
                }
                @Override
                public void error(ACException e) {
                	try {
                		Log.i("device bind error", e.getMessage());
                        Toast.makeText(getActivity(),getString(R.string.airpurifier_moredevice_show_bindfailed_text), AppConstant.TOAST_DURATION).show();
                        getActivity().finish();
                	} catch (Exception e1) {
                		
                	}
                    
                }
            });
        } catch(Exception e)
        {
        }
    }
    protected void controlDevice(String domainName,final CommendInfo commend, final int old_val) {

		msgHelper.controlDevice(domainName,commend, new VoidCallback() {
			@Override
			public void success() {
				try {
					getActivity().finish();
//	            	AppManager.getAppManager().finishActivity(AddDeviceActivity.class);

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                    ConstantCache.appManager.finishAllActivity();

                    Log.i("controlDevice", "Equipment control success" + commend.getCommend() + ":"
									+ commend.getValue() + "," + old_val);
				} catch (Exception e1) {
            		
            	}
				
			}

			@Override
			public void error(ACException e) {
				try {
					getActivity().finish();
//	            	AppManager.getAppManager().finishActivity(AddDeviceActivity.class);

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                    ConstantCache.appManager.finishAllActivity();

				} catch (Exception e1) {
            		
            	}
				
			}
		});
	}
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }


}
