package com.mokhtarabadi.tun2socks.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mokhtarabadi.tun2socks.sample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.start.setOnClickListener(v -> {
            Intent intent = MainService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, 1);
            } else {
                toggleVpnService(true);
            }
        });

        binding.stop.setOnClickListener(v -> toggleVpnService(false));
        binding.settings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            toggleVpnService(true);
        } else {
            Toast.makeText(this, "Really!?", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    private void toggleVpnService(boolean start) {
        Intent intent = new Intent(this, MainService.class);
        intent.setAction(start ? MainService.ACTION_START : MainService.ACTION_STOP);
        ContextCompat.startForegroundService(this, intent);
    }
}