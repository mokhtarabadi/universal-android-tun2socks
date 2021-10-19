package com.mokhtarabadi.tun2socks.sample;

import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_BYPASS_LAN;
import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_IPV4_DNS_SERVERS;
import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_IPV6_DNS_SERVERS;
import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_SOCKS_PORT;
import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_SOCKS_SERVER;
import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_SUPPORT_IPV4;
import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_SUPPORT_IPV6;
import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_SUPPORT_UDP;
import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.preferences;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mokhtarabadi.tun2socks.sample.databinding.ActivitySettingsBinding;

import java.util.Arrays;
import java.util.HashSet;

public class SettingsActivity extends AppCompatActivity {

  private ActivitySettingsBinding binding;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivitySettingsBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    binding.reset.setOnClickListener(
        v -> {
          preferences.edit().clear().apply();
          updateFromPreferences();
        });

    binding.excludedApps.setEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    binding.excludedApps.setOnClickListener(
        v -> startActivity(new Intent(SettingsActivity.this, ExcludedAppsActivity.class)));

    updateFromPreferences();
  }

  @Override
  protected void onDestroy() {
    saveFromUI();

    super.onDestroy();
  }

  private void updateFromPreferences() {
    binding.supportIpv4.setChecked(PreferenceHelper.isSupportIPv4());
    binding.supportIpv6.setChecked(PreferenceHelper.isSupportIPv6());
    binding.ipv4DnsServers.setText(TextUtils.join(",", PreferenceHelper.getIPv4DNSServers()));
    binding.ipv6DnsServers.setText(TextUtils.join(",", PreferenceHelper.getIPv6DNSServers()));
    binding.socksAddress.setText(PreferenceHelper.getSocksServerAddress());
    binding.socksPort.setText(String.valueOf(PreferenceHelper.getSocksServerPort()));
    binding.supportUdp.setChecked(PreferenceHelper.isSocksSupportUDP());
    binding.bypassLan.setChecked(PreferenceHelper.needBypassLan());
  }

  private void saveFromUI() {
    preferences
        .edit()
        .putBoolean(PREFERENCE_SUPPORT_IPV4, binding.supportIpv4.isChecked())
        .putBoolean(PREFERENCE_SUPPORT_IPV6, binding.supportIpv6.isChecked())
        .putStringSet(
            PREFERENCE_IPV4_DNS_SERVERS,
            new HashSet<>(
                Arrays.asList(binding.ipv4DnsServers.getText().toString().trim().split(","))))
        .putStringSet(
            PREFERENCE_IPV6_DNS_SERVERS,
            new HashSet<>(
                Arrays.asList(binding.ipv6DnsServers.getText().toString().trim().split(","))))
        .putString(PREFERENCE_SOCKS_SERVER, binding.socksAddress.getText().toString().trim())
        .putInt(PREFERENCE_SOCKS_PORT, Integer.parseInt(binding.socksPort.getText().toString()))
        .putBoolean(PREFERENCE_SUPPORT_UDP, binding.supportUdp.isChecked())
        .putBoolean(PREFERENCE_BYPASS_LAN, binding.bypassLan.isChecked())
        .apply();
  }
}
