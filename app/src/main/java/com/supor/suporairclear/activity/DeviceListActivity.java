package com.supor.suporairclear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.accloud.service.ACUserDevice;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.ToastUtil;
import com.supor.suporairclear.common.adapter.DeviceListAdapter;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.model.DeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by enyva on 16/5/28.
 */
public class DeviceListActivity extends BaseActivity {

    private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
    private DeviceListAdapter adapter;
    private ListView lv_device;
    private ACBindMgr bindMgr;
    private List<DeviceInfo> deviceList;

    private enum handler_key {
        GETALLDEVICE_SUCCESS,
    }

    /**
     * The handler.
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            handler_key key = handler_key.values()[msg.what];
            switch (key) {
                case GETALLDEVICE_SUCCESS:
                    try {
                        setView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                default:
                    break;
            }
        }
    };

    private void setView() {
        try {
            if (adapter == null) {
                adapter = new DeviceListAdapter(deviceList, this);
                lv_device.setAdapter(adapter);

                lv_device.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent = new Intent(DeviceListActivity.this, MyDeviceActivity.class);
                        intent.putExtra("deviceId", deviceList.get(position).getDeviceId());
                        intent.putExtra("deviceName", deviceList.get(position).getDeviceName());
                        intent.putExtra("physicalId", deviceList.get(position).getPhysicalDeviceId());

                        String deviceType = "";
                        try {  //fix bug  479
                            if (ConstantCache.deviceModeList != null && ! ConstantCache.deviceModeList.isEmpty())
                                for (ACObject item : ConstantCache.deviceModeList) {
                                        if (item.contains("subDomainId") && item.get("subDomainId").equals(String.valueOf(deviceList.get(position).getSubDomainId()))) {
                                            deviceType = item.get("name").toString();
                                        }
                                }
                            } catch (Exception e) {
                               e.printStackTrace();
                        }
                        intent.putExtra("deviceTcode", deviceType);
                        //intent.putExtra("deviceName", deviceList.get(position).getDeviceName());
                        startActivity(intent);
                    }

                });
            } else {
                adapter.reset(deviceList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_devicelist);
            AppManager.getAppManager().addActivity(this);
            setTitle(getString(R.string.airpurifier_moredevice_show_deviceList_title));
            setNavBtn(R.drawable.ico_back, R.drawable.ico_add);
            bindMgr = AC.bindMgr();
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        try {
            if (component == TitleBar.LEFT) {
                finish();
            }
            if (component == TitleBar.RIGHT) {
                Intent intent = new Intent(DeviceListActivity.this, APAddDeviceActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controls the initialization
     */
    private void init() {
        lv_device = (ListView) findViewById(R.id.lv_device);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceList();
    }

    // Access to equipment list
    public void getDeviceList() {
        try {
            try {
                showDialog();
                bindMgr.listDevicesWithStatus(new PayloadCallback<List<ACUserDevice>>() {
                    @Override
                    public void success(List<ACUserDevice> arg0) {
                        dimissDialog();
                        try {
                            deviceList = new ArrayList<DeviceInfo>();
                            for (ACUserDevice tmp : arg0) {
                                DeviceInfo device = new DeviceInfo();
                                device.setDeviceId(tmp.getDeviceId());
                                device.setDeviceName(tmp.getName());
                                device.setSubDomainId(tmp.getSubDomainId());
                                device.setStatus(tmp.getStatus());
                                device.setPhysicalDeviceId(tmp.getPhysicalDeviceId());
                                device.setOwner(tmp.getOwner());
                                deviceList.add(device);
                            }
                            AppUtils.setDeviceList(deviceList);
                            handler.sendEmptyMessage(handler_key.GETALLDEVICE_SUCCESS.ordinal());
                        } catch (Exception e) {
                            Log.e("listDevicesWithStatus", "error:" + e.getMessage() + "-->" + e.toString());
                        }
                    }

                    @Override
                    public void error(ACException e) {
                        dimissDialog();
                        ToastUtil.showToast(DeviceListActivity.this.getApplicationContext(), getString(R.string.airpurifier_failed_get_device_list));
                        Log.e("listDevicesWithStatus", "error:" + e.getErrorCode() + "-->" + e.getMessage());
                    }
                });
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

