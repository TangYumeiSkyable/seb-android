package com.rihuisoft.loginmode.utils;

import android.content.Context;
import android.widget.Toast;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class ToastUtil {
    public static void showToast(final Context context, final String content) {
        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showLongToast(final Context context, final String content) {
        Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                Toast.makeText(context, content, Toast.LENGTH_LONG).show();
            }
        });
    }
}
