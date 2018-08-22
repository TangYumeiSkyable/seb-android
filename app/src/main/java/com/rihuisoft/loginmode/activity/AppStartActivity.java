package com.rihuisoft.loginmode.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.utils.PreferencesUtils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.groupeseb.airpurifier.R;
import com.rihuisoft.loginmode.utils.AppManager;
import com.rihuisoft.loginmode.utils.Logger;
import com.rihuisoft.loginmode.utils.phoneUtils;
import com.rihuisoft.loginmode.view.AlertDialogUserDifine;
import com.supor.suporairclear.application.MainApplication;
import com.supor.suporairclear.config.Config;
import com.supor.suporairclear.config.ConstantCache;
import com.supor.suporairclear.config.DCPServiceUtils;
import com.supor.suporairclear.model.Settings;
import com.supor.suporairclear.model.TimeCount;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.util.EncodingUtils;
import cz.msebera.android.httpclient.util.TextUtils;
import io.fabric.sdk.android.Fabric;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AppStartActivity extends Activity {

    private enum handler_key {
        READSETTINGS,
        READSETTINGS_SUCCESS,
        READSETTINGS_FAIL,
        PAGE_CHANGE
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
                    case READSETTINGS:
                        //https://sebplatform.api.groupe-seb.com/assets/PRO_AIR/app/database/android/1/settings.json
                        SettingGet thread = new SettingGet(Config.getSettingJsonUrl(AppStartActivity.this.getApplicationContext()));
                        thread.start();
                        break;
                    case READSETTINGS_SUCCESS:
                        setLanguage();
                        break;
                    case READSETTINGS_FAIL:
                        File f = new File("setting.json");
                        if (!f.exists()) {
                            save("setting.json", defaultSettings());
                        }
                        setLanguage();
                        break;
                    case PAGE_CHANGE:
                        Intent intent;
                        intent = new Intent(AppStartActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        AppStartActivity.this.finish();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public class SettingGet extends Thread {
        private String url;

        public SettingGet(String url) {
            this.url = url;
        }

        public void run() {
            try {
                URL url = new URL(this.url);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(3 * 1000);
                urlConnection.setReadTimeout(4 * 1000);
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    Logger.i("请求成功");
                    InputStream is = urlConnection.getInputStream();
                    String state = getStringFromInputStream(is);
                    save("setting.json", state);
                    is.close();
                    handler.sendEmptyMessage(handler_key.READSETTINGS_SUCCESS.ordinal());
                } else {
                    Logger.e("请求失败");
                    //当返回码不是200，直接跳转失败
                    handler.sendEmptyMessage(handler_key.READSETTINGS_FAIL.ordinal());
                }
                urlConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(handler_key.READSETTINGS_FAIL.ordinal());
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(handler_key.READSETTINGS_FAIL.ordinal());
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(handler_key.READSETTINGS_FAIL.ordinal());
            } finally {

            }
        }
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        // 一定要写len=is.read(buffer)
        // 如果while((is.read(buffer))!=-1)则无法将数据写入buffer中
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        try {
            super.onCreate(savedInstanceState);
            AppManager.getAppManager().addActivity(this);
            ACAccountMgr accountMgr;
            accountMgr = AC.accountMgr();
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ImageView startImg = new ImageView(this);
            startImg.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            startImg.setImageResource(R.drawable.start_page);
            startImg.setScaleType(ScaleType.FIT_XY);
            dealCount();
            final boolean flag = PreferencesUtils.getBoolean(getApplicationContext(), "isFirst", true);
            setContentView(startImg);
            setDCPTokenInvalidListener();
            handler.sendEmptyMessage(handler_key.READSETTINGS.ordinal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * DCP token失效回调
     */
    private void setDCPTokenInvalidListener() {
        DCPServiceUtils.setDcpTokenInvalidCallback(new DCPServiceUtils.DCPTokenInvalidCallback() {
            @Override
            public void onDCPTokenInvalid() {
                logout();
            }
        });
    }

    private void logout() {
//        PreferencesUtils.putBoolean(this, AppConstant.SP_DCPSERVICE_TOKEN_INVALID_KEY,true);
        DCPServiceUtils.logout(new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg arg0) {
                Intent intent = new Intent(AppStartActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                ConstantCache.appManager.finishAllActivity();
            }

            @Override
            public void error(ACException arg0) {
                Intent intent = new Intent(AppStartActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                ConstantCache.appManager.finishAllActivity();
            }
        });
    }

    private void dealCount() {
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
            MainApplication.quitNumber = MainApplication.tipNumber;
            TimeCount quitObj = new TimeCount();
            quitObj.setTime(sf.format(System.currentTimeMillis()));
            quitObj.setNumber(MainApplication.quitNumber);
            PreferencesUtils.putString(MainApplication.getInstance(), "quitNumber", new Gson().toJson(quitObj));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public void save(String filename, String content) throws Exception {
        FileOutputStream outStream = openFileOutput(filename, MODE_PRIVATE);
        outStream.write(content.getBytes());
        outStream.close();
    }

    /**
     * Language Settings
     */
    private void setLanguage() {
        Settings settings = getSettingInfo();
        String country = phoneUtils.getCurrentCountry(AppStartActivity.this);
        String language = phoneUtils.getCurrentLanguage(AppStartActivity.this);
        Boolean matchFlg = false;
        String setLag = "";
        String defLag = "";
        String market = "";
        if (settings != null) {
            for (Settings.Language item : settings.getMarkets()) {
                if (item.getName().equals("GS_" + country)) {
                    setLag = item.getDefault_lang();
                    for (String lag : item.getAvailable_lang()) {
                        if (lag.equals(language)) {
                            setLag = lag;
                        }
                    }
                    //如果当前系统国家在setting.json中找到，那么就设置market为当前国家
                    market = item.getName();
                }
                if (item.getName().equals(settings.getFallback_market())) {
                    defLag = item.getDefault_lang();
                    for (String lag : item.getAvailable_lang()) {
                        if (lag.equals(language)) {
                            defLag = lag;
                        }
                    }
                }
            }
            if (settings.getMust_upgrade()) {
                new AlertDialogUserDifine(AppStartActivity.this).builder()
                        .setTitle(getString(R.string.airpurifier_update_tip_title)).setMsg(getString(R.string.airpurifier_update_tip_msg_start))
                        .setPositiveButton(getString(R.string.airpurifier_update_tip_goto), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(Config.PLAY_STORE_URL);
                                intent.setData(content_url);
                                startActivity(intent);
                                AppManager.getAppManager().AppExit(MainApplication.getInstance());
                            }
                        }).setNegativeButton(getString(R.string.airpurifier_update_tip_quit), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppManager.getAppManager().AppExit(MainApplication.getInstance());
                    }
                }).setCancelable(false).show();
            }
        }
        if (!"".equals(setLag)) {
        } else if (!"".equals(defLag)) {
            setLag = defLag;
        } else {
            setLag = "en";
        }

        //设置默认market
        if (settings != null && TextUtils.isEmpty(market)) {
            market = settings.getFallback_market();
        }

        DCPServiceUtils.setMarket(market);
        DCPServiceUtils.setLanguage(setLag);


        switchLanguage(setLag);
        handler.sendEmptyMessageDelayed(handler_key.PAGE_CHANGE.ordinal(), 1000);
    }

    public String defaultSettings() throws IOException {
        InputStreamReader streamReader = new InputStreamReader(getAssets().open("setting.json"), "UTF-8");
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        streamReader.close();
        return stringBuilder.toString();
    }

    /**
     * Set up information
     *
     * @return
     */
    public Settings getSettingInfo() {
        String res = "";
        try {
            FileInputStream fin = openFileInput("setting.json");
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
            return new Gson().fromJson(res, Settings.class);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Language selection
     *
     * @param language
     */
    private void switchLanguage(String language) {
        //Set up the language types
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("fr")) {
            config.locale = Locale.FRANCE;
        } else if (language.equals("de")) {
            config.locale = Locale.GERMANY;
        } else if (language.equals("es")) {
            Locale spanish = new Locale("es", "ES");
            config.locale = spanish;
        } else if (language.equals("pt")) {
            Locale pt = new Locale("pt", "PT");
            config.locale = pt;
        } else if (language.equals("nl")) {
            Locale nl = new Locale("nl", "NL");
            config.locale = nl;
        } else if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else if(language.equals("it")){
            Locale it = new Locale("it", "IT");
            config.locale = it;
        } else if(language.equals("zh")){
            Locale zh = new Locale("zh", "HK");
            config.locale = zh;
        }
        resources.updateConfiguration(config, dm);
        //设置DCP请求过程中需要用到的语言
        DCPServiceUtils.setLanguage(language);
        //Save the type of language
        PreferencesUtils.putString(MainApplication.getInstance(), "language", language);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fixInputMethodManagerLeak(this);
        AppManager.getAppManager().finishActivity(this);
    }


    public void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
