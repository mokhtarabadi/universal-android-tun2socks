package com.mokhtarabadi.tun2socks.sample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.mokhtarabadi.tun2socks.library.Tun2SocksBridge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton startMaterialButton = findViewById(R.id.start_vpn_btn);
        startMaterialButton.setOnClickListener(v -> {
            Intent intent = MainService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, 1);
            } else {
                Intent intent2 = new Intent(this, MainService.class);
                intent2.setAction(MainService.ACTION_START);
                ContextCompat.startForegroundService(this, intent2);
            }
        });

        MaterialButton stopMaterialButton = findViewById(R.id.stop_vpn_btn);
        stopMaterialButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainService.class);
            intent.setAction(MainService.ACTION_STOP);
            ContextCompat.startForegroundService(this, intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            ContextCompat.startForegroundService(this, new Intent(this, MainService.class));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}