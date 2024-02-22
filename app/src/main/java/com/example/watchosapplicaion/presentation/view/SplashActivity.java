package com.example.watchosapplicaion.presentation.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.watchosapplicaion.R;
import com.example.watchosapplicaion.databinding.ActivitySplashBinding;
import com.example.watchosapplicaion.presentation.utils.Constants;

/**
 * Project Name : StandAloneApplication
 *
 * @author VE00YM465
 * @company YMSLI
 * @date 14-02-2024
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * SplashActivity : This is the Splash Activity in the Watch android application.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showSplashScreen();
    }


    /**
     * Displays the splash screen for a predefined duration before transitioning to the LoginActivity.
     */
    private void showSplashScreen() {
        new Handler().postDelayed(() -> {
            if (isSessionValid()) {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
            finish();
        }, Constants.SPLASH_DISPLAY_LENGTH);
    }

    /**
     * This function is used to validate user login session
     */
    private boolean isSessionValid() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_SESSION, MODE_PRIVATE);
        long loginTimestamp = sharedPreferences.getLong(Constants.LOGIN_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();
        long sessionDuration = Constants.LOGIN_SESSION;
        return (currentTime - loginTimestamp) < sessionDuration;
    }
}
