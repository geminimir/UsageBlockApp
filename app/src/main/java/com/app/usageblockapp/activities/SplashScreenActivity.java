package com.app.usageblockapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.usageblockapp.R;
import com.app.usageblockapp.config.Config;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Config.isFirstTime(getApplicationContext())) {
                    Intent i = new Intent(SplashScreenActivity.this, LegalitiesActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, 1800);
    }
}
