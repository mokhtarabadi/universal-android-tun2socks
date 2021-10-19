package com.mokhtarabadi.tun2socks.sample;

import android.content.Context;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.getkeepsafe.relinker.ReLinker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainNative {

  private static final String TAG = "tun2socks";
  private static volatile boolean isInitialized = false;

  /**
   * Try to load native libraries
   *
   * @param context applicationContext
   */
  public static void initialize(Context context) {
    if (isInitialized) {
      Log.w(TAG, "initialization before done");
      return;
    }

    ReLinker.log(message -> Log.d(TAG, message))
        .recursively()
        .loadLibrary(
            context,
            "native-lib",
            new ReLinker.LoadListener() {
              @Override
              public void success() {
                isInitialized = true;
              }

              @Override
              public void failure(Throwable t) {
                isInitialized = false;
                Log.e(TAG, "failed to load native libraries", t);
              }
            });
  }

  /**
   * try to start badvpn-tunsocks (call this in separate thread)
   *
   * @param logLevel one of {@link LogLevel}
   * @param vpnInterfaceFileDescriptor the file descriptor after you called {@link
   *     VpnService.Builder#establish()}
   * @param vpnInterfaceMtu tun mtu, also must set this value for vpn interface by calling {@link
   *     VpnService.Builder#setMtu(int)}
   * @param socksServerAddress socks5 server address
   * @param socksServerPort socks5 server port
   * @param netIPv4Address an ipv4 address
   * @param netIPv6Address if not null, tun2socks will process ipv6 packets too
   * @param netmask netmask for example 255.255.255.0
   * @param forwardUdp if socks5 server support UDP, set this to true otherwise set to false
   * @return true of process finished successfully false if there is a problem!
   */
  public static boolean startTun2Socks(
      LogLevel logLevel,
      ParcelFileDescriptor vpnInterfaceFileDescriptor,
      int vpnInterfaceMtu,
      String socksServerAddress,
      int socksServerPort,
      String netIPv4Address,
      @Nullable String netIPv6Address,
      String netmask,
      boolean forwardUdp) {
    // TODO: 9/26/21 "--dnsgw", "127.0.0.1:5353"

    ArrayList<String> arguments = new ArrayList<>();
    arguments.add("badvpn-tun2socks"); // app name (:D)
    arguments.addAll(
        Arrays.asList("--logger", "stdout")); // set logger to stdout so can see logs in logcat
    arguments.addAll(
        Arrays.asList("--loglevel", String.valueOf(logLevel.ordinal()))); // set log level
    arguments.addAll(
        Arrays.asList("--tunfd", String.valueOf(vpnInterfaceFileDescriptor.getFd()))); // set fd
    arguments.addAll(Arrays.asList("--tunmtu", String.valueOf(vpnInterfaceMtu)));
    arguments.addAll(Arrays.asList("--netif-ipaddr", netIPv4Address));

    if (!TextUtils.isEmpty(netIPv6Address)) {
      arguments.addAll(Arrays.asList("--netif-ip6addr", netIPv6Address));
    }

    arguments.addAll(Arrays.asList("--netif-netmask", netmask));
    arguments.addAll(
        Arrays.asList(
            "--socks-server-addr",
            String.format(Locale.US, "%s:%d", socksServerAddress, socksServerPort)));

    if (forwardUdp) {
      arguments.add("--socks5-udp");
    }

    int exitCode = start_tun2socks(arguments.toArray(new String[] {}));
    return exitCode == 0;
  }

  /**
   * start tun2socks with args
   *
   * @param args like when you run main() method on c!
   * @return other than zero mean failed
   */
  private static native int start_tun2socks(String[] args);

  /** try to stop badvpn-tun2socks */
  public static native void stopTun2Socks();

  /** print usage help in logcat */
  public static native void printTun2SocksHelp();

  /** print version in logcat */
  public static native void printTun2SocksVersion();

  /** badvpn-tun2socks logLevel */
  public enum LogLevel {
    NONE, // 0
    ERROR, // 1
    WARNING, // 2
    NOTICE, // 3
    INFO, // 4
    DEBUG // 5
  }
}
