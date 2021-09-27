package com.mokhtarabadi.tun2socks.sample;

import android.content.SharedPreferences;

import com.frybits.harmony.Harmony;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreferenceHelper {

    public static final String PREFERENCE_SUPPORT_IPV4 = "support_ipv4";
    public static final String PREFERENCE_SUPPORT_IPV6 = "support_ipv6";
    public static final String PREFERENCE_IPV4_DNS_SERVERS = "ipv4_dns_servers";
    public static final String PREFERENCE_IPV6_DNS_SERVERS = "ipv6_dns_servers";
    public static final String PREFERENCE_SOCKS_SERVER = "socks_server";
    public static final String PREFERENCE_SOCKS_PORT = "socks_port";
    public static final String PREFERENCE_SUPPORT_UDP = "socks_support_udp";
    public static final String PREFERENCE_BYPASS_LAN = "bypass_lan";
    public static final String PREFERENCE_EXCLUDED_APPS = "excluded_apps";

    public static final SharedPreferences preferences = Harmony.getSharedPreferences(MainApp.appContext, "main");

    public static boolean isSupportIPv4() {
        return preferences.getBoolean(PREFERENCE_SUPPORT_IPV4, true);
    }

    public static boolean isSupportIPv6() {
        return preferences.getBoolean(PREFERENCE_SUPPORT_IPV6, true);
    }

    public static Set<String> getIPv4DNSServers() {
        return preferences.getStringSet(PREFERENCE_IPV4_DNS_SERVERS, new HashSet<>(Arrays.asList("1.0.0.1", "1.1.1.1")));
    }

    public static Set<String> getIPv6DNSServers() {
        return preferences.getStringSet(PREFERENCE_IPV6_DNS_SERVERS, new HashSet<>(Arrays.asList("2606:4700:4700::1001", "2606:4700:4700::1111")));
    }

    public static String getSocksServerAddress() {
        return preferences.getString(PREFERENCE_SOCKS_SERVER, "127.0.0.1");
    }

    public static int getSocksServerPort() {
        return preferences.getInt(PREFERENCE_SOCKS_PORT, 1080);
    }

    public static boolean isSocksSupportUDP() {
        return preferences.getBoolean(PREFERENCE_SUPPORT_UDP, true);
    }

    public static boolean needBypassLan() {
        return preferences.getBoolean(PREFERENCE_BYPASS_LAN, true);
    }

    public static Set<String> getExcludedApps() {
        return preferences.getStringSet(PREFERENCE_EXCLUDED_APPS, new HashSet<>());
    }
}
