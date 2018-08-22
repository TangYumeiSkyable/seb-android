package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.accloud.service.ACProduct;
import com.bumptech.glide.Glide;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppUtils;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.view.DotsView;
import com.squareup.picasso.Picasso;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.activity.DeviceBindQRActivity;
import com.supor.suporairclear.config.Constants;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.config.DeviceTypebean;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhy.autolayout.AutoRelativeLayout;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.supor.suporairclear.config.DCPServiceUtils.DCPServiceContentType.DCPServiceContentAppliances;

/**
 * Created by fengjian on 2017/11/9.
 * function:
 */

public class P3Fragment extends Fragment {
    public static final String TAG = "P3Fragment";
    private static final int REQEST_CODE = 10085;
    private DotsView mDotsview;
    private AutoRelativeLayout mRlQrcode;
    private ListView mListView;
    private List<DeviceTypebean> mAcObjects;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_p3, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((APAddDeviceActivity) getActivity()).setNavBtn(R.drawable.ico_back, 0);
        getActivity().setTitle(getString(R.string.select_product));
        getDeviceModeList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){ // 回来时刷新数据
            getDeviceModeList();
        }
    }


    private void initView(View view) {
        mDotsview = (DotsView) view.findViewById(R.id.dotsview);
        mRlQrcode = (AutoRelativeLayout) view.findViewById(R.id.rl_qrcode);
        mListView = (ListView) view.findViewById(R.id.listview);

        mRlQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoQR();
            }
        });
        mDotsview.setLightDotsNumber(1);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !(AppUtils.isConnectWifi(getContext()) && AppUtils.isHasLocationPermission(getContext()))){
                    PpermissionFragment permissionFragment = new PpermissionFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceType", mAcObjects.get(position).getSubDominId());
                    permissionFragment.setArguments(bundle);
                    FragmentUtil.replaceSupportFragment(getActivity()
                            , R.id.framlayout_container
                            , permissionFragment
                            ,P3Fragment.this
                            , permissionFragment.TAG
                            , true
                            , false);
                }else {
                    P4Fragment p4Fragment = new P4Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceType", mAcObjects.get(position).getSubDominId());
                    p4Fragment.setArguments(bundle);
                    FragmentUtil.replaceSupportFragment(getActivity()
                            , R.id.framlayout_container
                            , p4Fragment
                            ,P3Fragment.this
                            , P4Fragment.TAG
                            , true
                            , false);
                }
            }
        });
    }

    private void gotoQR() {
        final Intent intent = new Intent(getActivity(), DeviceBindQRActivity.class);
        //添加这个标识是为了不影响原来的逻辑，
        //如果是从这个页面跳转到二维码扫描，那么根据标识，执行相应的判断
        intent.putExtra(Constants.ISAP, true);
        if (FragmentUtil.judgeGetActivityCanUse(P3Fragment.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                new RxPermissions(getActivity())
                        .request(Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    P3Fragment.this.startActivityForResult(intent, REQEST_CODE);
                                } else {

                                }
                            }
                        });
            } else {
                P3Fragment.this.startActivityForResult(intent, REQEST_CODE);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQEST_CODE && data != null) {
            String subdominname = data.getStringExtra(Constants.SUBDOMINNAME);
            String bindmodelname = data.getStringExtra(Constants.BINDMODELNAME);
            String bindmodelpic = data.getStringExtra(Constants.BINDMODELPIC);
            Long subdomainid = data.getLongExtra(Constants.SUBDOMAINID, 0L);
            //如果要使用请进行非空判断
            if (subdomainid != 0 && FragmentUtil.judgeGetActivityCanUse(P3Fragment.this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !(AppUtils.isConnectWifi(getContext()) && AppUtils.isHasLocationPermission(getContext()))) {
                    PpermissionFragment permissionFragment = new PpermissionFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceType", subdomainid + "");
                    permissionFragment.setArguments(bundle);
                    FragmentUtil.replaceSupportFragment(getActivity()
                            , R.id.framlayout_container
                            , permissionFragment
                            ,P3Fragment.this
                            , permissionFragment.TAG
                            , true
                            , false);
                } else {
                    P4Fragment p4Fragment = new P4Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceType", subdomainid + "");
                    p4Fragment.setArguments(bundle);
                    FragmentUtil.replaceSupportFragment(getActivity()
                            , R.id.framlayout_container
                            , p4Fragment
                            ,P3Fragment.this
                            , P4Fragment.TAG
                            , true
                            , false);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void getDeviceModeList() {
        Observable
                .create(new ObservableOnSubscribe<ACMsg>() {
                    @Override
                    public void subscribe(final ObservableEmitter<ACMsg> emitter) throws Exception {
                        DCPServiceUtils.syncContent(DCPServiceContentAppliances, new PayloadCallback<ACMsg>() {
                            @Override
                            public void error(ACException e) {
                                emitter.onError(e);
                            }

                            @Override
                            public void success(ACMsg acMsg) {
                                emitter.onNext(acMsg);
                            }
                        });
                    }
                })
                .flatMap(new Function<ACMsg, ObservableSource<List<DeviceTypebean>>>() {
                    @Override
                    public ObservableSource<List<DeviceTypebean>> apply(final ACMsg acMsg) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<List<DeviceTypebean>>() {
                            @Override
                            public void subscribe(final ObservableEmitter<List<DeviceTypebean>> launch) throws Exception {
                                AC.productMgr().fetchAllProducts(new PayloadCallback<List<ACProduct>>() {
                                    @Override
                                    public void success(List<ACProduct> products) {
                                        ArrayList<ACObject> objects = (ArrayList) ((ACObject) acMsg.getObjectData().get("content")).get("objects");
                                        if (objects == null) return;
                                        List<DeviceTypebean> data = new ArrayList<DeviceTypebean>();
                                        Logger.i("AC返回数据object：" + objects);
                                        for (ACObject obj : objects) {
                                            Logger.i("创建数据：" + data.size());
                                            DeviceTypebean deviceTypebean = new DeviceTypebean();
                                            deviceTypebean.setDeviceName(obj.get("name").toString());
                                            deviceTypebean.setDeviceType(obj.get("id").toString());
                                            if (obj.get("medias") != null && ((ArrayList<ACObject>) obj.get("medias")).size() > 0) {
                                                deviceTypebean.setDeivceIconUrl(((ArrayList<ACObject>) obj.get("medias")).get(0).get("thumbs").toString());
                                            }
                                            data.add(deviceTypebean);
                                        }
                                        ListIterator<DeviceTypebean> deviceTypebeanListIterator = data.listIterator();
                                        while (deviceTypebeanListIterator.hasNext()) {
                                            boolean isDelete = true;
                                            DeviceTypebean typebean = deviceTypebeanListIterator.next();
                                            for (ACProduct product : products) {
                                                if (typebean.getDeviceType().equals(product.model)) {
                                                    isDelete = false;
                                                    typebean.setSubDominId(product.sub_domain);
                                                    typebean.setSubDominName(product.sub_domain_name);
                                                }
                                            }
                                            if (isDelete)
                                                deviceTypebeanListIterator.remove();
                                        }
                                        Logger.i("data：" + data.size());
                                        launch.onNext(data);

                                    }

                                    @Override
                                    public void error(ACException e) {
                                        launch.onError(e);
                                    }
                                });
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DeviceTypebean>>() {
                    @Override
                    public void accept(List<DeviceTypebean> acObjects) throws Exception {
                        Logger.i("数据请求成功：" + acObjects.size());
                        mAcObjects = acObjects;
                        ChooseDeviceTypeAdapter chooseDeviceTypeAdapter = new ChooseDeviceTypeAdapter(new SoftReference<Context>(getActivity()), acObjects);
                        mListView.setAdapter(chooseDeviceTypeAdapter);
                        chooseDeviceTypeAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (FragmentUtil.judgeGetActivityCanUse(P3Fragment.this)) {
                            Toast.makeText(getActivity(), R.string.airpurifier_failed_get_device_list, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    public static class ChooseDeviceTypeAdapter extends BaseAdapter {
        private Context mContext;
        private List<DeviceTypebean> mDeviceTypebeans = new ArrayList<>();
        private ImageView device_icon;
        private TextView moble_name;

        public ChooseDeviceTypeAdapter(SoftReference<Context> contextSoftReference, List<DeviceTypebean> deviceTypebeans) {
            Context context = contextSoftReference.get();
            if (context != null)
                mContext = context.getApplicationContext();
            mDeviceTypebeans.clear();
            mDeviceTypebeans.addAll(deviceTypebeans);
        }

        @Override
        public int getCount() {
            return mDeviceTypebeans.size();
        }

        @Override
        public Object getItem(int position) {
            return mDeviceTypebeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mContext != null) {
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_choose_device, parent, false);
                this.device_icon = (ImageView) inflate.findViewById(R.id.device_icon);
                this.moble_name = (TextView) inflate.findViewById(R.id.moble_name);
                this.moble_name.setText(mDeviceTypebeans.get(position).getDeviceName());
                this.moble_name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Ubuntu-R.ttf"));

                Glide.with(mContext).load(mDeviceTypebeans.get(position).getDeivceIconUrl().replace("{size}", "original"))
                .placeholder(R.drawable.img_p1).error(R.drawable.img_p1).dontAnimate().into(device_icon);

                return inflate;
            }
            return null;
        }
    }


}
