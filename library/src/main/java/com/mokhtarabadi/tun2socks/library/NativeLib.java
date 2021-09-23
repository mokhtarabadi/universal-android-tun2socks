package com.mokhtarabadi.tun2socks.library;

public class NativeLib {

    // Used to load the 'library' library on application startup.
    static {
        System.loadLibrary("library");
    }

    /**
     * A native method that is implemented by the 'library' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}