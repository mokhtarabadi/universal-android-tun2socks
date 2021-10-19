package com.mokhtarabadi.tun2socks.sample;

import android.app.Application;
import android.content.Context;

public class MainApp extends Application {

  public static volatile Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();

    appContext = getApplicationContext();
  }
}
