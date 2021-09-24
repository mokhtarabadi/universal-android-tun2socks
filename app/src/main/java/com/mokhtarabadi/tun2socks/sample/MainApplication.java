package com.mokhtarabadi.tun2socks.sample;

import android.app.Application;

import com.mokhtarabadi.tun2socks.library.Tun2SocksBridge;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Tun2SocksBridge.initialize(this);
    }
}
