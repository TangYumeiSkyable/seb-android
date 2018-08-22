package com.supor.suporairclear.service;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.accloud.utils.PreferencesUtils;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.ChineseToEnglish2;
import com.rihuisoft.loginmode.utils.StringUtils;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.AppConstant;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

/**
 * @author xiong_it
 * @description
 * @charset UTF-8
 * @date 2015-7-20上午10:31:39
 */
public class LocationService extends IntentService implements LocationListener {
    private static final String TAG = "LocationService";
    private static final String SERVICE_NAME = "LocationService";

    private static final long MIN_TIME = 1000l;
    private static final float MIN_DISTANCE = 10f;

    private LocationManager locationManager;
    private String locationProvider;

    public LocationService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            Log.i(TAG, "Can't locate");
            //Can't locate: 1, prompt the user to open the location services;2, jump to the interface
            Completable
                    .complete()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            Log.i(TAG, "toast" + Thread.currentThread().getName());
                            Toast.makeText(LocationService.this, getString(R.string.airpurifier_location_show_locationfailed_text), Toast.LENGTH_LONG).show();
                            Intent i = new Intent();
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    });
            return;
        }

        if (locationProvider == null) {
            //没有获取到Provider 提示使用者
            Intent failLocationIntent = new Intent();
            failLocationIntent.setAction(Constants.LOCATION_ACTION_FAIL);
            sendBroadcast(failLocationIntent);
            return;
        }

        if (ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat
                        .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            showLocation(location);
        }
        locationManager.requestLocationUpdates(locationProvider, MIN_TIME, MIN_DISTANCE, this);
    }

    private void showLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        ConstantCache.location_longitude = latitude;
        ConstantCache.location_latitude = longitude;
        MainApplication.latitude = latitude;
        MainApplication.longitude = longitude;

        StringBuffer a = new StringBuffer();
        List<Address> locationList = getLocationList(latitude, longitude);
        if (!locationList.isEmpty()) {
            Address address = locationList.get(0);

            if (address.getLocality() == null) return;
            MainApplication.location_city = address.getLocality().replace("市", "");
            if (StringUtils.isHanzi(MainApplication.location_city)) {
                MainApplication.location_city = ChineseToEnglish2.getFullSpell(MainApplication.location_city).toUpperCase();
            } else {
                MainApplication.location_city = MainApplication.location_city.toUpperCase();
            }

            PreferencesUtils.putString(MainApplication.getInstance(), "latitude", String.valueOf(MainApplication.latitude));
            PreferencesUtils.putString(MainApplication.getInstance(), "longitude", String.valueOf(MainApplication.longitude));
            PreferencesUtils.putString(MainApplication.getInstance(), "city", MainApplication.location_city);

            a.append(address.getCountryName() != null ? address.getCountryName(): "" );
            a.append(address.getCountryCode() != null ? address.getCountryCode(): "" );
            a.append(address.getLocality() != null ? address.getLocality(): "");
            for (int i = 0; address.getAddressLine(i) != null; i++) {
                a.append(address.getAddressLine(i));
            }
            // 通知Activity
            Intent intent = new Intent();
            intent.setAction(AppConstant.LOCATION_ACTION);
            intent.putExtra(AppConstant.LOCATION, ConstantCache.location_city);
            sendBroadcast(intent);
            Log.i(TAG, "sendBroadcast");
        } else {
            Intent failLocationIntent = new Intent();
            failLocationIntent.setAction(Constants.LOCATION_ACTION_FAIL);
            sendBroadcast(failLocationIntent);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        showLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    private List<Address> getLocationList(double latitude, double longitude) {
        Log.i(TAG, "getLocationList");
        Geocoder gc = new Geocoder(this);
        List<Address> locationList = new ArrayList<Address>();
        try {
            locationList = gc.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationList;
    }

}
