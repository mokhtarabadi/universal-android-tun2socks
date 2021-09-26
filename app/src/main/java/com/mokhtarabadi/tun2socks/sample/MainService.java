package com.mokhtarabadi.tun2socks.sample;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.system.Os;
import android.util.Log;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mokhtarabadi.tun2socks.library.Tun2SocksBridge;

import java.io.IOException;

public class MainService extends VpnService {

    private static final String TAG = "vpn_service";

    public static final String ACTION_START = "tun2socks_sample_start";
    public static final String ACTION_STOP = "tun2socks_sample_stop";

    private static final String PRIVATE_VLAN4_CLIENT = "10.0.0.1";
    private static final String PRIVATE_VLAN4_ROUTER = "10.0.0.2";
    private static final String PRIVATE_VLAN4_DNS = "1.1.1.1";

    private static final String PRIVATE_VLAN6_CLIENT = "fc00::1";
    private static final String PRIVATE_VLAN6_ROUTER = "fc00::2";
    private static final String PRIVATE_VLAN6_DNS = "2606:4700:4700::1111";

    private static final String PRIVATE_NETMASK = "255.255.255.252";

    private static final int PRIVATE_MTU = 1500;

    private static final boolean SUPPORT_IPV4 = true;
    private static final boolean SUPPORT_IPV6 = true;

    private static final boolean SUPPORT_UDP = true;

    private Notification notification;
    private ParcelFileDescriptor descriptor;

    @Override
    public void onCreate() {
        super.onCreate();

        Tun2SocksBridge.initialize(getApplicationContext()); // need this

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat.Builder("vpn_service", NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName("Vpn service")
                .build();
        notificationManager.createNotificationChannel(notificationChannel);

        notification = new NotificationCompat.Builder(this, notificationChannel.getId())
                .setContentTitle("Vpn service")
                .setContentText("Testing Tun2Socks")
                .setSmallIcon(R.drawable.ic_baseline_vpn_lock_24)
                .build();
    }

    @Override
    public void onRevoke() {
        stopService();
        super.onRevoke();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(ACTION_STOP)) {
            stopService();
            return START_NOT_STICKY;
        }
        startService();
        return START_STICKY;
    }

    private void startService() {
        if (descriptor != null) {
            Log.w(TAG, "seems vpn already running!?");
            return;
        }

        startForeground(1, notification);

        Builder builder = new Builder()
                .setSession("Testing Tun2Socks")
                .setMtu(PRIVATE_MTU);

        // ipv4
        if (SUPPORT_IPV4) {
            builder.addAddress(PRIVATE_VLAN4_CLIENT, 30) // 30 equal to 255.255.255.252
                    .addRoute("0.0.0.0", 0)
                    .addDnsServer(PRIVATE_VLAN4_DNS);
        }

        // ipv6
        if (SUPPORT_IPV6) {
            builder.addAddress(PRIVATE_VLAN6_CLIENT, 126)
                    .addRoute("::", 0)
                    .addDnsServer(PRIVATE_VLAN6_DNS);
        }

        // TODO: 9/26/21 support .addDnsServer("10.0.0.2") mean --dnsgw

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                builder.addDisallowedApplication("com.termux"); // i used termux
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        descriptor = builder.establish();
        if (descriptor == null) {
            stopSelf();
            return;
        }

        new Thread(() -> {
            boolean result = Tun2SocksBridge.start(
                    Tun2SocksBridge.LogLevel.INFO,
                    descriptor,
                    PRIVATE_MTU,
                    "127.0.0.1", 1080, // i used termux to setup an socks5 server
                    PRIVATE_VLAN4_ROUTER,
                    PRIVATE_VLAN6_ROUTER,
                    PRIVATE_NETMASK,
                    SUPPORT_UDP
            );

            Log.d(TAG, "tun2socks stopped, result: " + result);
        }).start();
    }

    private void stopService() {
        if (descriptor == null) {
            Log.w(TAG, "seems already stopped!?");
            return;
        }

        stopForeground(true);

        Tun2SocksBridge.terminate();
        descriptor = null; // TODO: 9/26/21 close file descriptor in native code
        stopSelf();

        Process.killProcess(Process.myPid()); // need run process in another process!!
    }
}