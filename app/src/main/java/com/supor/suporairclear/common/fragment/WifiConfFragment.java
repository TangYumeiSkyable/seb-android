package com.supor.suporairclear.common.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.ACDeviceActivator;
import com.accloud.service.ACBindMgr;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.supor.suporairclear.activity.DeviceBindActivity;
import com.supor.suporairclear.activity.DeviceBindQRActivity;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;

/**
 * Created by enyva on 16/5/28.
 */
public class WifiConfFragment extends Fragment{
    public DevicebindCallback fragmentCallBack = null;
    private ScrollView sc_view;
    private ImageView imageView;
    private LinearLayout linearLayout;
    private EditText et_wifiName,et_pwd;
    private Button btn_ok;
    private TextView tv_wifiName, tv_pwd;
    private ToggleButton show_psd;
    private ACDeviceActivator deviceActivator;
    // The device manager
    ACBindMgr bindMgr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View wifiView = inflater.inflate(R.layout.fragment_wifi, container,false);
        try {
            sc_view = (ScrollView)wifiView.findViewById(R.id.sc_view);
            imageView = (ImageView) wifiView.findViewById(R.id.imageView);
            btn_ok = (Button) wifiView.findViewById(R.id.btn_ok);
            et_pwd = (EditText) wifiView.findViewById(R.id.et_pwd);
            et_wifiName = (EditText) wifiView.findViewById(R.id.et_wifiName);
            tv_wifiName = (TextView) wifiView.findViewById(R.id.tv_wifiName);
            tv_pwd = (TextView) wifiView.findViewById(R.id.tv_pwd);

            et_wifiName.setTypeface(AppConstant.ubuntuMedium);
            et_pwd.setTypeface(AppConstant.ubuntuMedium);
            btn_ok.setTypeface(AppConstant.ubuntuRegular);
            tv_wifiName.setTypeface(AppConstant.ubuntuRegular);
            tv_pwd.setTypeface(AppConstant.ubuntuRegular);

            show_psd = (ToggleButton) wifiView.findViewById(R.id.show_password);
            show_psd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    }
                    Editable etext = et_pwd.getText();
                    Selection.setSelection(etext, etext.length());
                }
            });
            ConstantCache.wifiPasswoed = "";
            btn_ok.setEnabled(false);
            //btn_ok.setBackgroundResource(Color.GRAY);
            init();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return wifiView;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(AppUtils.isConnected(getActivity())){
            if (AppUtils.isWifi(getActivity())) {
                btn_ok.setEnabled(true);
            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        // Access to the device manager
        bindMgr = AC.bindMgr();
        deviceActivator = AC.deviceActivator(ACDeviceActivator.HF);
        et_wifiName.setText(deviceActivator.getSSID());
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            	try {
//        			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//        			imm.hideSoftInputFromWindow(et_pwd.getWindowToken(), 0);
//        		}  catch (Exception e) {
//            		e.printStackTrace();
//            	}
//            }
//        });
        sc_view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deviceActive(et_pwd.getText().toString());
                try {
                    AppUtils.immSet(getActivity());
                    Bundle bundle = new Bundle();
                    bundle.putString("password", et_pwd.getText().toString());
                    ConstantCache.wifiPasswoed=et_pwd.getText().toString();
                    fragmentCallBack.callbackFun3(bundle);
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

//    public void deviceActive(String password) {
//        try {
//            deviceActivator.startAbleLink(deviceActivator.getSSID(), password,
//                    AC.DEVICE_ACTIVATOR_DEFAULT_TIMEOUT,
//                    new PayloadCallback<List<ACDeviceBind>>() {
//                        @Override
//                        public void success(List<ACDeviceBind> deviceBinds) {
//                            if (deviceBinds.size() > 0) {
////								deviceActivator.stopAbleLink();
//                            }
//
//                            ConstantCache.physicalDeviceId = "";
//                            for (ACDeviceBind deviceBind : deviceBinds) {
//                                //if (deviceBind.getSubDomainId() == Config.SUBMAJORDOMAIN) {
//                                    ConstantCache.physicalDeviceId = deviceBind.getPhysicalDeviceId();
//                                    isDeviceBound(deviceBind);
//                                    break;
//                                // }
//                                }
//                            }
//
//                        @Override
//                        public void error(ACException e) {
//                            btn_ok.setEnabled(true);
//                            //btn_ok.setText(getString(R.string.add_device_active_retry));
//                            //connect.setBackgroundResource(R.drawable.shape_btn_green);
//                            Toast.makeText(getActivity(), "未查找到设备，请重试", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } catch (Exception e) {
//            Log.i("Device Bind Failed", e.getMessage());
//        }
//    }
//
//    private void isDeviceBound(final ACDeviceBind deviceBind)
//    {
//        try
//        {
//            bindMgr.isDeviceBound(Config.SUBMAJORDOMAIN, deviceBind.getPhysicalDeviceId(), new PayloadCallback<Boolean>()
//            {
//
//                @Override
//                public void error(ACException arg0) {
//                    Log.i("check device is bound error", arg0.getMessage());
//                }
//
//                @Override
//                public void success(Boolean arg0) {
//                    Log.i("check device is bound success", "success");
//                    if(!arg0)
//                    {
//                        deviceActivator.stopAbleLink();
////                        Intent intent = new Intent();
////                        intent.setClass(AddDeviceActivity.this,
////                                BindDeviceActivity.class);
////                        ConstantCache.appManager
////                                .addActivity(AddDeviceActivity.this);
////                        intent.putExtra("physicalDeviceId", deviceBind.getPhysicalDeviceId());
////                        startActivity(intent);
//                    }
//                }
//
//            });
//
//        } catch(Exception e){}
//    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            fragmentCallBack = (DeviceBindActivity)activity;
        } catch (Exception e) {
            fragmentCallBack = (DeviceBindQRActivity)activity;
        }

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
}
