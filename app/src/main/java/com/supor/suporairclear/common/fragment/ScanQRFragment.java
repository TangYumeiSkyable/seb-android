package com.supor.suporairclear.common.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.groupeseb.airpurifier.R;
import com.supor.suporairclear.activity.DeviceBindQRActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by enyva on 16/5/28.
 */
public class ScanQRFragment extends Fragment{

    public DevicebindCallback fragmentCallBack = null;
    private ListView lv_mode;
    private LinearLayout ll_typelist,ll_add;
    private Button btn_addDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View modelView = inflater.inflate(R.layout.fragment_model, container,false);
        try {
        	ll_typelist = (LinearLayout) modelView.findViewById(R.id.ll_typelist);
            ll_add = (LinearLayout) modelView.findViewById(R.id.ll_add);
            lv_mode = (ListView) modelView.findViewById(R.id.lv_mode);
            lv_mode = (ListView) modelView.findViewById(R.id.lv_mode);
            btn_addDevice=(Button) modelView.findViewById(R.id.btn_addDevice);
            init();
        } catch(Exception e) {
 			e.printStackTrace();
 		} 
        
        return modelView;
    }

    private void init() {
        ll_typelist.setVisibility(View.VISIBLE);
        ll_add.setVisibility(View.GONE);
        List<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();

        HashMap<String,Object>map = new HashMap<String,Object>();
        map.put("name", "Supor_device1");
        data.add(map);
        map=new HashMap<String,Object>();
        map.put("name", "Supor_device2");
        data.add(map);
        map=new HashMap<String,Object>();
        map.put("name", "Supor_device3");
        data.add(map);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, R.layout.item_selectview, new String[]{"name"}, new int[]{R.id.tv_name});


        lv_mode.setAdapter(adapter);

        lv_mode.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){

                ll_typelist.setVisibility(View.GONE);
                ll_add.setVisibility(View.VISIBLE);

            }
        });

        btn_addDevice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

                fragmentCallBack.callbackFun2(null);
			}
		});

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
