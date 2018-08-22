package com.supor.suporairclear.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACDevice;
import com.accloud.service.ACDeviceUser;
import com.accloud.service.ACException;
import com.accloud.service.ACObject;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.activity.BaseActivity;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.common.adapter.MemberListAdapter;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.Config;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.controller.ACMsgHelper;
import com.zxing.ShareActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by enyva on 16/5/28.
 */
public class MyDeviceActivity extends BaseActivity implements View.OnClickListener {

    private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();

    private MemberListAdapter adapter;
    private ListView lv_member;
    private TextView[] tvs;
    private View rl_qcode;
    private View rl_deviceinfo;
    private View line2;
    private Button btn_unbind;
    private int state = 0;
    private ACBindMgr bindMgr;
    private ACMsgHelper msgHelper = new ACMsgHelper();
    private long deviceId;
    private List<ACDeviceUser> deviceUserList;
    private String deviceName, physicalId;
    private String deviceTcode;
    private TextView tv_device, tv_mac;
    private Intent intent = null;
    private Boolean isOwner = false;
    private TextView mFirmware_version;

    //获取固件版本
    public void getFirmwareVersion() {
        String subDomainName = ConstantCache.deviceMap.get(deviceId).getSubDomainName();
        AC.deviceMgr().getDeviceInfo(subDomainName, physicalId, new PayloadCallback<ACDevice>() {
            @Override
            public void success(ACDevice acDevice) {
                mFirmware_version.setText(acDevice != null ? acDevice.getModuleVersion() : "");
            }

            @Override
            public void error(ACException e) {

            }
        });
    }


    private enum handler_key {
        GETDEVICEUSERLIST_SUCCESS,
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
                    case GETDEVICEUSERLIST_SUCCESS:
                        try {
                            ACDeviceUser tmpUser = null;
                            if (deviceUserList != null && deviceUserList.size() > 0) {
                                for (ACDeviceUser userInfo : deviceUserList) {
                                    if (userInfo.getUserId() == MainApplication.mUser.getUserId()) {
                                        if (userInfo.getUserType() == 1) {
                                            isOwner = true;
                                        }
                                        ;
                                        tmpUser = userInfo;
                                    }
                                }
                                if (tmpUser != null) {
                                    deviceUserList.remove(tmpUser);
                                }
                                setView();
                            } else {
                                tvs[1].setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;


                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_mydevice);
            AppManager.getAppManager().addActivity(this);
            setTitle(getString(R.string.airpurifier_moredevice_show_mydevice_title));
            setNavBtn(R.drawable.ico_back, R.drawable.ico_add);
            bindMgr = AC.bindMgr();

            init();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setView() {

        try {
            if (deviceUserList.size() == 0) {
                tvs[1].setVisibility(View.GONE);
                line2.setVisibility(View.GONE);
                lv_member.setVisibility(View.GONE);
                return;
            } else {
                tvs[1].setVisibility(View.VISIBLE);
                line2.setVisibility(View.VISIBLE);
                lv_member.setVisibility(View.VISIBLE);
            }
            adapter = new MemberListAdapter(this, deviceUserList, isOwner);
            lv_member.setAdapter(adapter);
            setListViewHeight(lv_member);
            adapter.setUnbindDeviceListener(new MemberListAdapter.UnbindDeviceListener() {
                @Override
                public void unbindDeviceLis(final int position) {
                    new AlertDialogUserDifine(MyDeviceActivity.this).builder()
                            .setMsg(String.format(getString(R.string.airpurifier_more_show_suredeletemember_text), deviceUserList.get(position).getName()))
                            .setPositiveButton(getString(R.string.airpurifier_public_ok), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Click on the "confirm" after the operation
                                    unbindDeviceWithUser(position);
                                    state = 1;
                                }
                            })
                            .setNegativeButton(getString(R.string.airpurifier_more_show_cancel_button), new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // Click on the "return" after the operation, there is no set without any operation
                                }
                            }).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setListViewHeight(ListView lv) {
        try {
            ListAdapter la = lv.getAdapter();
            if (null == la) {
                return;
            }
            // calculate height of all items.
            int h = 0;
            final int cnt = la.getCount();
            for (int i = 0; i < cnt; i++) {
                View item = la.getView(i, null, lv);
                item.measure(0, 0);
                h += item.getMeasuredHeight();
            }
            // reset ListView height
            ViewGroup.LayoutParams lp = lv.getLayoutParams();
            lp.height = h + (lv.getDividerHeight() * (cnt - 1));
            lv.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        switch (component) {
            case LEFT:
                finish();
                break;
            case RIGHT:
                Intent intent = new Intent(MyDeviceActivity.this, APAddDeviceActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Controls the initialization
     */
    private void init() {
        try {
            deviceId = getIntent().getLongExtra("deviceId", 0);
            deviceName = getIntent().getStringExtra("deviceName");
            lv_member = (ListView) findViewById(R.id.lv_member);
            deviceTcode = getIntent().getStringExtra("deviceTcode");
            physicalId = getIntent().getStringExtra("physicalId");
            tvs = new TextView[]{(TextView) findViewById(R.id.tv1), (TextView) findViewById(R.id.tv2),
                    (TextView) findViewById(R.id.tv_deviceinfo), (TextView) findViewById(R.id.tv_device),
                    (TextView) findViewById(R.id.tv_devicecode), (TextView) findViewById(R.id.tv_mac_add)};
            for (int i = 0; i < tvs.length; i++) {
                tvs[i].setTypeface(AppConstant.ubuntuRegular);
            }
            tv_mac = (TextView) findViewById(R.id.tv_mac_add);
            tv_device = (TextView) findViewById(R.id.tv_device);
            mFirmware_version = (TextView) findViewById(R.id.tv_firmware_version);
            if (deviceName != null && !"".equals(deviceName)) {
                tv_device.setText(deviceName);
            }

            tv_mac.setText(subString(physicalId));

            line2 = (View) findViewById(R.id.line2);
            rl_qcode = (View) findViewById(R.id.rl_qcode);
            rl_deviceinfo = (View) findViewById(R.id.rl_deviceinfo);
            btn_unbind = (Button) findViewById(R.id.btn_unbind);
            btn_unbind.setTypeface(AppConstant.ubuntuRegular);
            rl_qcode.setOnClickListener(this);
            rl_deviceinfo.setOnClickListener(this);
            btn_unbind.setOnClickListener(this);
            getFirmwareVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String subString(String str1) {
        String str2 = "";
        for (int i = 0; i < str1.length(); i++) {
            if (i * 2 + 2 > str1.length()) {
                str2 += str1.substring(i * 2, str1.length());
                break;
            }
            str2 += str1.substring(i * 2, i * 2 + 2) + ":";
        }

        str2 = str2.substring(0, str2.length() - 1);

        return new StringBuilder(str2).toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserList();
    }


    @SuppressLint("StringFormatInvalid")
    @Override
    public void onClick(final View view) {
        try {
//        	if (!isOwner) {
//        		 ShowToast("没有权限，请联系设备管理员");
//        		 return;
//        	}

            switch (view.getId()) {
                case R.id.rl_deviceinfo:

                    try {
                        if (!isOwner) {
                            ShowToast(getString(R.string.airpurifier_more_show_noauthority_text));
                            return;
                        }
                        intent = new Intent(MyDeviceActivity.this, ModifyNameActivity.class);
//				intent.putExtra("flag", AppConstant.RESETPASSWORD);
                        //flag 0:user 1:device
                        intent.putExtra("flag", 1);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("deviceName", deviceName);

                        startActivityForResult(intent, 1);

                        //startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.rl_qcode:
                    if (!isOwner) {
                        ShowToast(getString(R.string.airpurifier_more_show_noauthority_text));
                        return;
                    }

                    AC.bindMgr().getShareCode(ConstantCache.deviceMap.get(deviceId).getSubDomainName(), deviceId, 5 * 60, new PayloadCallback<String>() {
                        @Override
                        public void success(String shareCode) {
//                            ShowToast("二维码取得成功:"+shareCode);
                            intent = new Intent(MyDeviceActivity.this, ShareActivity.class);
                            intent.putExtra("shareCode", shareCode);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("deviceTcode", deviceTcode);
                            startActivity(intent);
                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("getShareCode", "error:" + e.getErrorCode() + "-->" + e.getMessage());

                        }
                    });

                    break;
                case R.id.btn_unbind:
                    if (state == 0) {
                        new AlertDialogUserDifine(this).builder().setTitle(getString(R.string.airpurifier_more_show_warning_text))
                                .setMsg(String.format(getString(R.string.airpurifier_more_show_sureremovebinddevice_text), deviceName))
                                .setPositiveButton(getString(R.string.airpurifier_moredevice_show_confirm_text), new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        //Click on the "confirm" after the operation
                                        ((Button) view).setText(getString(R.string.airpurifier_more_show_binddevice_text));
                                        unbindDevice();
                                        state = 1;


                                    }
                                })
                                .setNegativeButton(getString(R.string.airpurifier_moredevice_show_refuse_text), new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // Click on the "return" after the operation, there is no set without any operation
                                    }
                                }).show();
                    } else {
                        try {
                            intent = new Intent(MyDeviceActivity.this, AddDeviceActivity.class);
//				intent.putExtra("flag", AppConstant.RESETPASSWORD);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    break;
            }
        } catch (Exception e) {

        }
    }

    /**
     * Equipment solutions to
     *
     * @param deviceId
     */
    private void unbindDeviceOfApp(long deviceId) {
        try {

        } catch (Exception e) {
            Log.v("exception", e.getMessage());
        }
    }

    /**
     * The administrator unbundling equipment
     *
     * @param
     */
    private void unbindDevice() {
        bindMgr.unbindDevice(ConstantCache.deviceMap.get(deviceId).getSubDomainName(), deviceId, new VoidCallback() {
            @Override
            public void success() {
                List<ACObject> userIdList = new ArrayList<ACObject>();
                if (isOwner) {

                    for (ACDeviceUser user : deviceUserList) {
                        ACObject u = new ACObject();
                        u.put("userId", user.getUserId());
                        userIdList.add(u);
                    }

                } else {
                    ACObject u = new ACObject();
                    u.put("userId", MainApplication.mUser.getUserId());
                    userIdList.add(u);
                }

                msgHelper.deleteMessageForUnbind(userIdList, deviceId, new VoidCallback() {

                    @Override
                    public void success() {
                        ShowToast(getString(R.string.airpurifier_more_show_ubbindsuccess_text));
                        finish();
                    }

                    @Override
                    public void error(ACException e) {
                        Log.e("deleteMessageForUnbind", "delete The message error:" + e.getErrorCode() + "-->" + e.getMessage());
                        finish();
                    }
                });

            }

            @Override
            public void error(ACException e) {
                Log.e("unbindDevice", "Unbundling failure error:" + e.getErrorCode() + "-->" + e.getMessage());
                ShowToast(getString(R.string.airpurifier_more_show_ubbindfailed_text));
            }
        });
    }

    /**
     * Administrators to cancel the share
     *
     * @param
     */
    private void unbindDeviceWithUser(final int position) {
        bindMgr.unbindDeviceWithUser(ConstantCache.deviceMap.get(deviceId).getSubDomainName(), deviceUserList.get(position).getUserId(), deviceId, new VoidCallback() {
            @Override
            public void success() {
                List<ACObject> userIdList = new ArrayList<ACObject>();
                ACObject u = new ACObject();
                u.put("userId", deviceUserList.get(position).getUserId());
                userIdList.add(u);
                msgHelper.deleteMessageForUnbind(userIdList, deviceId, new VoidCallback() {

                    @Override
                    public void success() {
                        ShowToast(getString(R.string.airpurifier_more_show_cancelsharesuccess_text));
                        getUserList();
                    }

                    @Override
                    public void error(ACException e) {
                        Log.e("deleteMessageForUnbind", "delete The message error:" + e.getErrorCode() + "-->" + e.getMessage());
                    }
                });

            }

            @Override
            public void error(ACException e) {
                Log.e("unbindDeviceWithUser", "Cancel the sharing failure error:" + e.getErrorCode() + "-->" + e.getMessage());
                ShowToast(getString(R.string.airpurifier_more_show_cancelsharefailed_text));
            }
        });
    }

    private void getUserList() {
        try {
            bindMgr.listUsers(Config.SUBMAJORDOMAIN, deviceId,
                    new PayloadCallback<List<ACDeviceUser>>() {
                        @Override
                        public void success(List<ACDeviceUser> acBindings) {
                            deviceUserList = acBindings;
                            handler.sendEmptyMessage(handler_key.GETDEVICEUSERLIST_SUCCESS
                                    .ordinal());
                        }

                        @Override
                        public void error(ACException e) {
                            Log.e("listUsers", e.getErrorCode() + "-->" + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            // TODO: handle exception
            Log.v("exception", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1) {
                if (data.getStringExtra("deviceName") != null) {
                    String result_value = data.getStringExtra("deviceName");
                    tv_device.setText(result_value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

