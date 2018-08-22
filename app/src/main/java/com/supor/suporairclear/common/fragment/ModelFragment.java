package com.supor.suporairclear.common.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.squareup.picasso.Picasso;
import com.supor.suporairclear.activity.DeviceBindActivity;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by enyva on 16/5/28.
 */
public class ModelFragment extends Fragment{

    public DevicebindCallback fragmentCallBack = null;
    private ListView lv_mode;
    private LinearLayout ll_typelist,ll_add;
    private Button btn_addDevice;
    private TextView tv_device_code;
    private ImageView tv_device_img;
    private String subDomainId,deviceType,subDomainName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View modelView = inflater.inflate(R.layout.fragment_model, container,false);
        try {
        	
            ll_typelist = (LinearLayout) modelView.findViewById(R.id.ll_typelist);
            ll_add = (LinearLayout) modelView.findViewById(R.id.ll_add);
            lv_mode = (ListView) modelView.findViewById(R.id.lv_mode);
            btn_addDevice=(Button) modelView.findViewById(R.id.btn_addDevice);
            tv_device_code = (TextView) modelView.findViewById(R.id.tv_device_code);
            tv_device_img = (ImageView) modelView.findViewById(R.id.tv_device_img);
            btn_addDevice.setTypeface(AppConstant.ubuntuRegular);
            tv_device_code.setTypeface(AppConstant.ubuntuRegular);
            init();
        } catch(Exception e) {
 			e.printStackTrace();
 		} 
        
        return modelView;
    }

    private void init() {
    	try {
    		ll_typelist.setVisibility(View.VISIBLE);
            ll_add.setVisibility(View.GONE);

            setListData();
            btn_addDevice.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
                    ConstantCache.subDomainId=Integer.valueOf(subDomainId);
                    ConstantCache.subDomainName=subDomainName;
                    Bundle bundle = new Bundle();
                    bundle.putString("subDomainId", subDomainId);
                    bundle.putString("name", deviceType);
                    fragmentCallBack.callbackFun2(bundle);
    			}
    		});
    	} catch(Exception e) {
 			e.printStackTrace();
 		} 
        

    }

    private void setListData() {
        final List<Map<String,String>> data = new ArrayList<Map<String,String>>();
        for(ACObject item : ConstantCache.deviceModeList){
            Map model = new HashMap<String,String>();
            model.put("name",item.get("name").toString());
            model.put("img",item.get("img").toString());
            data.add(model);
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, R.layout.item_selectview, new String[]{"name"}, new int[]{R.id.tv_name});
        lv_mode.setAdapter(adapter);
        lv_mode.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){
                ll_typelist.setVisibility(View.GONE);
                ll_add.setVisibility(View.VISIBLE);
                tv_device_code.setText(String.format(getString(R.string.airpurifier_moredevice_show_modulenumber_text), data.get(position).get("name")));
                String deviceHeaderUrl=data.get(position).get("img").toString();
                Picasso.with(getActivity()).load(deviceHeaderUrl.replace("{size}","original")).noFade().into(tv_device_img);
                subDomainId=ConstantCache.deviceModeList.get(position).get("subDomainId").toString();
                subDomainName=ConstantCache.deviceModeList.get(position).get("subDomainName").toString();
                deviceType=ConstantCache.deviceModeList.get(position).get("name").toString();
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        fragmentCallBack = (DeviceBindActivity)activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
}
