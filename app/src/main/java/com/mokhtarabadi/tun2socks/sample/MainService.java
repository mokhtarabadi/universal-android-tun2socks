package com.mokhtarabadi.tun2socks.sample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.mokhtarabadi.tun2socks.library.Tun2SocksBridge;

import java.io.FileDescriptor;

public class MainService extends VpnService {

    private static final String TAG = "vpn_service";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationChannelCompat notificationChannel =  new NotificationChannelCompat.Builder("vpn_service", NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName("Vpn service")
                .build();
        notificationManager.createNotificationChannel(notificationChannel);

        Notification notification = new NotificationCompat.Builder(this, notificationChannel.getId())
                .setContentTitle("Vpn service")
                .setContentText("Testing Tun2Socks")
                .setAutoCancel(true)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_baseline_vpn_lock_24)
                .build();

        startForeground(0, notification);

        Builder builder = new Builder()
                .setSession("Testing Tun2Socks")
                .setMtu(1500)
                .addAddress("26.26.26.1",30)
                .addRoute("0.0.0.0",0)
                .addAddress("da26:2626::1",126)
                .addRoute("::", 0)
                //.addDnsServer("26.26.26.2");
                .addDnsServer("1.1.1.1");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                builder.addDisallowedApplication(getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        int fd = builder.establish().getFd();

        new Thread(() -> {
            int result = Tun2SocksBridge.start(new String[] {
                    "badvpn-tun2socks",
                    "--logger", "stdout",
                    "--loglevel", "info",
                    "--tunfd", String.valueOf(fd),
                    "--tunmtu", "1500",
                    "--dnsgw", "127.0.0.1:5353",
                    "--netif-ipaddr", "26.26.26.2",
                    "--netif-netmask", "255.255.255.252",
                    "--socks-server-addr", "192.168.1.6:1080",
                    "--netif-ip6addr", "da26:2626::2",
                    "--socks5-udp"
            });

            Log.d(TAG, "tun2socks stopped, status: " + result);
        }).start();

        return START_STICKY;
    }
}