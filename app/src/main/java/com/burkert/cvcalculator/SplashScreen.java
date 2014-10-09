package com.burkert.cvcalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;

public class SplashScreen extends Activity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();
        handler.postDelayed(splashTimer, 1000);
    }

    Runnable splashTimer = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.mainactivity_slide_up, R.anim.splashscreen_slide_up);
        }
    };

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(splashTimer);
        finish();
    }
}
