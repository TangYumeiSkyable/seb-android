package com.supor.suporairclear.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.utils.PreferencesUtils;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.AppStartActivity;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.ToastUtil;
import com.supor.suporairclear.adapter.CountryAdapter;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.common.fragment.HomeFragment;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.Constants;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.supor.suporairclear.service.LocationService;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by enyva on 16/5/28.
 */
public class CountryListActivity extends BaseActivity {
    private ListView lv_country;
    private List<HashMap<String, String>> data = new ArrayList<>();
    ;
    private TextView tv_current_city;
    private ACMsgHelper msgHelper = new ACMsgHelper();

    private enum handler_key {
        GETDATA_SUCCESS,
        USER_OVERDUE
    }

    /**
     * The handler.
     */
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
                    case USER_OVERDUE:
                        AppUtils.reLogin(CountryListActivity.this);
                        break;
                }
            } catch (Exception e) {

            }
        }
    };

    private void setView() {
        if (data != null && data.size() > 0) {
            List<String> countryList = new ArrayList<>();
            for (HashMap<String, String> stringHashMap : data) {
                String name = stringHashMap.get("name");
                countryList.add(name);
            }
            CountryAdapter countryAdapter = new CountryAdapter(countryList, CountryListActivity.this);
            lv_country.setAdapter(countryAdapter);
        }


        lv_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(CountryListActivity.this, CityListActivity.class);
                intent.putExtra("country", data.get(position).get("name"));
                startActivity(intent);
                CountryListActivity.this.finish();
            }
        });

    }

    private void getData() {
        msgHelper.queryCountryList(new PayloadCallback<List<ACObject>>() {
            @Override
            public void success(List<ACObject> acObjects) {
                for (ACObject item : acObjects) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", item.getString("country"));
                    data.add(map);
                }
                handler.sendEmptyMessage(handler_key.GETDATA_SUCCESS.ordinal());
            }

            @Override
            public void error(ACException e) {
                if (e.getErrorCode() == AppConstant.ERR_OVERDUE_1 || e.getErrorCode() == AppConstant.ERR_OVERDUE_2
                        || e.getErrorCode() == AppConstant.ERR_OVERDUE_4 || e.getErrorCode() == AppConstant.ERR_OVERDUE_5
                        || e.getErrorCode() == AppConstant.ERR_OVERDUE_6) {
                    handler.sendEmptyMessage(handler_key.USER_OVERDUE.ordinal());
                }
                ToastUtil.showToast(CountryListActivity.this, getString(R.string.airpurifier_get_country_list_fail));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_countrylist);
            AppManager.getAppManager().addActivity(this);
            setTitle(getString(R.string.airpurifier_adjust_show_modifycity_text));
            setNavBtn(R.drawable.ico_back, 0);
            lv_country = (ListView) findViewById(R.id.lv_country);
            tv_current_city = (TextView) findViewById(R.id.tv_current_city);
            IntentFilter filter = new IntentFilter();
            filter.addAction(AppConstant.LOCATION_ACTION);
            filter.addAction(Constants.LOCATION_ACTION_FAIL);
            registerReceiver(new LocationReceiver(), filter);

            getData();
            findViewById(R.id.bt_relocation).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        new RxPermissions(CountryListActivity.this)
                                .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            startService(new Intent(CountryListActivity.this, LocationService.class));
                                        } else {
                                        }
                                    }
                                });
                    } else {
                        startService(new Intent(CountryListActivity.this, LocationService.class));
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppConstant.LOCATION_ACTION)) {
                final String address = intent.getStringExtra(AppConstant.LOCATION);

                tv_current_city.setText(MainApplication.location_city);
                CountryListActivity.this.unregisterReceiver(this);//When you don't need to log ou
                PreferencesUtils.putString(MainApplication.getInstance(), "city", MainApplication.location_city);
                //定位成功
            } else if (intent.getAction().equals(Constants.LOCATION_ACTION_FAIL)) {
                //定位失败
                ToastUtil.showToast(context, getString(R.string.airpurifier_location_show_locationfailed_text));
            }
        }
    }


    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            if (component == TitleBar.LEFT) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        tv_current_city.setText(MainApplication.location_city);
    }


}

