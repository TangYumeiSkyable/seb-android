package com.supor.suporairclear.common.fragment;

import android.app.Activity;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;

import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.squareup.picasso.Picasso;

import com.supor.suporairclear.activity.DeviceBindQRActivity;

import com.supor.suporairclear.config.ConstantCache;
import com.zxing.view.ViewfinderView;


/**
 * Created by enyva on 16/5/28.
 */
public class CaptureFragment extends Fragment{

    public DevicebindCallback fragmentCallBack = null;
    private ListView lv_mode;
    private LinearLayout ll_add;
    private RelativeLayout rl_capture;
    private Button btn_addDevice;
    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;
    private ImageView iv_devHeader;
    private TextView tv_device_code;

    // Scan the qr code



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View modelView = inflater.inflate(R.layout.fragment_capture, container,false);
        try {
        	
            rl_capture = (RelativeLayout) modelView.findViewById(R.id.rl_capture);
            ll_add = (LinearLayout) modelView.findViewById(R.id.ll_add);
            lv_mode = (ListView) modelView.findViewById(R.id.lv_mode);
            lv_mode = (ListView) modelView.findViewById(R.id.lv_mode);
            btn_addDevice=(Button) modelView.findViewById(R.id.btn_addDevice);
            iv_devHeader=(ImageView) modelView.findViewById(R.id.iv_devHeader);
            tv_device_code=(TextView) modelView.findViewById(R.id.tv_device_code);
            init();
            viewfinderView = (ViewfinderView) modelView.findViewById(R.id.viewfinder_view);
            surfaceView = (SurfaceView) modelView.findViewById(R.id.preview_view);
        } catch(Exception e) {
  			e.printStackTrace();
  		}
        

        return modelView;
    }
    public ViewfinderView getViewfinderView () {
        return viewfinderView;
    }
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }
    @Override
    public void onStart() {
        super.onStart();
        fragmentCallBack.callbackCapture(null);
    }
    private void init() {
        rl_capture.setVisibility(View.VISIBLE);
        ll_add.setVisibility(View.GONE);

        btn_addDevice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
	                fragmentCallBack.callbackFun2(null);
				} catch(Exception e) {
		  			e.printStackTrace();
		  		}
                
			}
		});

    }

    public void displayDevice() {

        if(!"".equals(ConstantCache.bindModelPic)){
            Picasso.with(this.getActivity()).load(ConstantCache.bindModelPic.replace("{size}","original")).noFade().into(iv_devHeader);
            tv_device_code.setText(String.format(getString(R.string.airpurifier_moredevice_show_modulenumber_text), ConstantCache.bindModelName));
        }
    	rl_capture.setVisibility(View.GONE);
        ll_add.setVisibility(View.VISIBLE);

    }
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
        	 fragmentCallBack = (DeviceBindQRActivity)activity;
        } catch(Exception e) {
  			e.printStackTrace();
  		}
       
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
}
