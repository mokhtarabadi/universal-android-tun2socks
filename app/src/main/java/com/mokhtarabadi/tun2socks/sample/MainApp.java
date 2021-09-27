package com.mokhtarabadi.tun2socks.sample;

import android.app.Application;
import android.content.Context;

public class MainApp extends Application {

    public volatile static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
    }
}
