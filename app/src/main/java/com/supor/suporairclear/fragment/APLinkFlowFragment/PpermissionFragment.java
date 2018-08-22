package com.supor.suporairclear.fragment.APLinkFlowFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.FragmentUtil;
import com.supor.suporairclear.activity.APAddDeviceActivity;
import com.supor.suporairclear.service.NetWorkChangeReceiver;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class PpermissionFragment extends Fragment implements NetWorkChangeReceiver.OnNetChangeListener {
    public static String TAG = "PpermissionFragment";
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.iv_tip)
    AppCompatImageView ivTip;
    @BindView(R.id.iv_wifi)
    AppCompatImageView ivWifi;
    @BindView(R.id.iv_location)
    AppCompatImageView ivLocation;
    @BindView(R.id.tv_wifi_desc)
    TextView tvWifiDesc;
    @BindView(R.id.tv_location_desc)
    TextView tvLocationDesc;
    @BindView(R.id.btn_wifi)
    Button btnWifi;
    @BindView(R.id.btn_location)
    Button btnLocation;
    Unbinder unbinder;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.tv_can_next)
    TextView tvCanNext;

    private NetWorkChangeReceiver myReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_request_permission, container, false);
        unbinder = ButterKnife.bind(this, view);
        registerNetReceiver();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((APAddDeviceActivity) getActivity()).setNavBtn(R.drawable.ico_back, 0);
        getActivity().setTitle(getString(R.string.request_permission_titile));
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void registerNetReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new NetWorkChangeReceiver();
        getContext().registerReceiver(myReceiver, filter);
        myReceiver.setOnNetChangeListener(this);
    }

    private void initView() {
        if (isConnectWifi() && isHasLocationPermission()) {
            btnLocation.setVisibility(View.GONE);
            btnWifi.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            tvTip.setText(getResources().getString(R.string.request_permission_has));
            tvCanNext.setVisibility(View.VISIBLE);
        } else {
            btnLocation.setVisibility(View.VISIBLE);
            btnWifi.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            tvTip.setText(getResources().getString(R.string.request_permission_tip));
            tvCanNext.setVisibility(View.INVISIBLE);
        }


        if (isConnectWifi()) {
            tvWifiDesc.setText(getResources().getString(R.string.request_permission_wifi_online));
            btnWifi.setClickable(false);
            btnWifi.setBackgroundResource(R.drawable.selectors_btn_disabled);
            ivWifi.setBackgroundResource(R.drawable.img_wifi_icon_normal);
        } else {
            tvWifiDesc.setText(getResources().getString(R.string.request_permission_wifi_offline));
            btnWifi.setClickable(true);
            btnWifi.setBackgroundResource(R.drawable.selectors_btn_button);
            ivWifi.setBackgroundResource(R.drawable.img_wifi_icon);

        }
        if (isHasLocationPermission()) {
            tvLocationDesc.setText(getResources().getString(R.string.request_location_has));
            btnLocation.setClickable(false);
            btnLocation.setBackgroundResource(R.drawable.selectors_btn_disabled);
            ivLocation.setBackgroundResource(R.drawable.img_location_icon_normal);
        } else {
            tvLocationDesc.setText(getResources().getString(R.string.request_location_no));//            ivLocation.setImageResource();
            btnLocation.setClickable(true);
            btnLocation.setBackgroundResource(R.drawable.selectors_btn_button);
            ivLocation.setBackgroundResource(R.drawable.img_location_icon);
        }


    }

    private boolean isHasLocationPermission() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isConnectWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (myReceiver != null) {
            getContext().unregisterReceiver(myReceiver);
        }
    }

    @OnClick({R.id.iv_tip, R.id.btn_wifi, R.id.btn_location, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_tip:
                PpermissionTipFragment ppermissionTipFragment = new PpermissionTipFragment();
                FragmentUtil.replaceSupportFragment(getActivity()
                        , R.id.framlayout_container
                        , ppermissionTipFragment
                        , PpermissionFragment.this
                        , ppermissionTipFragment.TAG
                        , true
                        , false);
                break;
            case R.id.btn_wifi:
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_location:
                getLocationPermission();
                break;
            case R.id.btn_next:
                P4Fragment p4Fragment = new P4Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("deviceType", getArguments().getString("deviceType"));
                p4Fragment.setArguments(bundle);
                FragmentUtil.replaceSupportFragment(getActivity()
                        , R.id.framlayout_container
                        , p4Fragment
                        , PpermissionFragment.this
                        , p4Fragment.TAG
                        , false
                        , false);
                break;
        }
    }

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new RxPermissions(getActivity())
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBoolean) throws Exception {
                            initView();
                        }
                    });
        } else {
        }
    }


    @Override
    public void netChangeListener(boolean b) {
        initView();
    }
}
