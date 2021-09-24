package com.mokhtarabadi.tun2socks.library;

import android.content.Context;

import com.getkeepsafe.relinker.ReLinker;

public class Tun2SocksBridge {

    public static void initialize(Context context) {
        ReLinker.log(new ReLinker.Logger() {
            @Override
            public void log(String message) {
                System.out.println(message);
            }
        }).recursively()
                .loadLibrary(context, "tun2socks-bridge", new ReLinker.LoadListener() {
                    @Override
                    public void success() {
                        System.out.println("success");
                    }

                    @Override
                    public void failure(Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    /**
     * A native method that is implemented by the 'library' native library,
     * which is packaged with this application.
     */
    public static native int start(String[] args);
}