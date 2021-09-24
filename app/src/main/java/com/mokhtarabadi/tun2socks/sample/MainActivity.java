package com.mokhtarabadi.tun2socks.sample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mokhtarabadi.tun2socks.library.Tun2SocksBridge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = MainService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            ContextCompat.startForegroundService(this, new Intent(this, MainService.class));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            ContextCompat.startForegroundService(this, new Intent(this, MainService.class));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}