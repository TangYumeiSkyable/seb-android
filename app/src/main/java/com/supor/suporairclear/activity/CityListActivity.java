package com.supor.suporairclear.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.utils.PreferencesUtils;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.common.adapter.CityListAdapter;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.controller.ACMsgHelper;

import java.util.List;

/**
 * Created by enyva on 16/5/28.
 */
public class CityListActivity extends BaseActivity{
    private ListView lv_city;
    private List<ACObject> data;
    private CityListAdapter adapter;
    private String country;
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private enum handler_key{
        GETDATA_SUCCESS,
        USER_OVERDUE
    }

    /** The handler. */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            try {
                handler_key key = handler_key.values()[msg.what];
                switch (key) {
                    case GETDATA_SUCCESS:
                        setView();
                        break;
                    case USER_OVERDUE :
                        AppUtils.reLogin(CityListActivity.this);
                        break;
                }
            } catch (Exception e) {

            }
        }
    };
    private void setView() {
    	try {
            adapter = new CityListAdapter(data, this);

            lv_city.setAdapter(adapter);

            lv_city.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id){
                    for (int i = 0; i < data.size(); i++) {
                        ACObject map = data.get(i);
                        if (i == position) {
                            map.put("check", true);
                        } else {
                            map.put("check", false);
                        }
                        MainApplication.location_city = data.get(position).getString("name");
                        MainApplication.latitude =  data.get(position).getDouble("latitude");
                        MainApplication.longitude =  data.get(position).getDouble("longitude");
                        PreferencesUtils.putString(MainApplication.getInstance(), "city", MainApplication.location_city);
                        PreferencesUtils.putString(MainApplication.getInstance(), "latitude", String.valueOf(MainApplication.latitude));
                        PreferencesUtils.putString(MainApplication.getInstance(), "longitude", String.valueOf(MainApplication.longitude));
                        adapter.reset(data);
                        CityListActivity.this.finish();
                    }
//                    Intent intent = new Intent(this,)
                }
            });
            
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }
    private void getData() {
        msgHelper.queryCityList(country, new PayloadCallback<List<ACObject>>() {
            @Override
            public void success(List<ACObject> acObjects) {
                for (ACObject item : acObjects) {
                    if (item.getString("name").equals(MainApplication.location_city)) {
                        item.put("check", true);
                    } else {
                        item.put("check", false);
                    }
                }
                data = acObjects;
                handler.sendEmptyMessage(handler_key.GETDATA_SUCCESS.ordinal());
            }

            @Override
            public void error(ACException e) {
                if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1 || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                        || e.getErrorCode() == AppConstant.ERR_OVERDUE_4|| e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                        || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {
                    handler.sendEmptyMessage(handler_key.USER_OVERDUE.ordinal());
                }
                Log.e("queryMessage", "error:" + e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	setContentView(R.layout.activity_citylist);
            AppManager.getAppManager().addActivity(this);
            setTitle(getString(R.string.airpurifier_adjust_show_modifycity_text));
            setNavBtn(R.drawable.ico_back, 0);
            lv_city = (ListView) findViewById(R.id.lv_city);
            country = getIntent().getStringExtra("country");
            getData();
        } catch(Exception e) {
			e.printStackTrace();
		}
        

    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
    	try {
    		if (component == TitleBar.LEFT) {
                finish();
            }
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
            
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}

