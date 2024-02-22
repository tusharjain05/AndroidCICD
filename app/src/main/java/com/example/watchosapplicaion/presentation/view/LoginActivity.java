package com.example.watchosapplicaion.presentation.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.watchosapplicaion.R;
import com.example.watchosapplicaion.databinding.ActivityLoginBinding;
import com.example.watchosapplicaion.presentation.utils.Constants;

import dagger.hilt.android.AndroidEntryPoint;

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
 * LoginActivity : This is the Login Activity in the Watch android application.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@AndroidEntryPoint
public class LoginActivity extends ComponentActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    private static final String TAG = "Notification Permission";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        setupObservers();
        binding.loginButton.setOnClickListener(view -> performLogin());
        checkAndRequestNotificationPermission();
    }

    /**
     * Attempts to perform login using the credentials entered by the user.
     */
    private void performLogin() {
        String username = binding.usernameEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        if (!username.isEmpty() && !password.isEmpty()) {
            loginViewModel.loginUser(username, password);
        } else {
            Toast.makeText(this, R.string.login_validation, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up observers for the ViewModel's LiveData
     */
    private void setupObservers() {
        loginViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    );

        loginViewModel.getLoginSuccessful().observe(this, loginSuccessful -> {
            if (loginSuccessful) {
                saveLoginSession();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginViewModel.getErrorResponse().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(LoginActivity.this,error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions(String[], int).
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
            } else {
                Toast.makeText(this, R.string.notification_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Checks if the notification permission is needed.
     */
    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, Constants.NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * This function stores the login session in the shared preference
     */
    public void saveLoginSession() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_SESSION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(Constants.LOGIN_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }


}
