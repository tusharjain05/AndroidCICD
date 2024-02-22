package com.example.watchosapplicaion.presentation.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.wear.widget.WearableLinearLayoutManager;


import com.example.watchosapplicaion.R;
import com.example.watchosapplicaion.databinding.ActivityMainBinding;
import com.example.watchosapplicaion.presentation.adapter.NewsAdapter;
import com.example.watchosapplicaion.presentation.database.AppDatabase;
import com.example.watchosapplicaion.presentation.database.LocationDao;
import com.example.watchosapplicaion.presentation.service.LocationService;
import com.example.watchosapplicaion.presentation.utils.Constants;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Project Name : StandAloneApplication
 *
 * @author VE00YM465
 * @company YMSLI
 * @date 14-02-2024
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 * <p>
 * Description
 * -----------------------------------------------------------------------------------
 * MainActivity : This is the Main Activity in the Watch android application.
 * -----------------------------------------------------------------------------------
 * <p>
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private NewsAdapter newsAdapter;
    private NewsViewModel viewModelNews;


    @Override
    protected void onStart() {
        super.onStart();
        viewModelNews.fetchArticles();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        viewModelNews = new ViewModelProvider(this).get(NewsViewModel.class);
        setupRecyclerView();
        subscribeToViewModel();
        checkAndRequestPermissions();
        activityMainBinding.logoutButton.setOnClickListener(view -> showLogoutConfirmationDialog());
        activityMainBinding.newsRecyclerView.post(() -> activityMainBinding.newsRecyclerView.requestFocus());

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSessionAndLogoutIfNeeded();
    }

    /**
     * Sets up the RecyclerView with a layout manager and adapter.
     */
    private void setupRecyclerView() {
        activityMainBinding.newsRecyclerView.setFocusable(true);
        activityMainBinding.newsRecyclerView.setFocusableInTouchMode(true); // Necessary for some devices
        activityMainBinding.newsRecyclerView.requestFocus();
        activityMainBinding.newsRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        activityMainBinding.newsRecyclerView.setEdgeItemsCenteringEnabled(true);
        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        activityMainBinding.newsRecyclerView.setAdapter(newsAdapter);
    }

    /**
     * Subscribes to ViewModel live data for article updates and loading status.
     */
    private void subscribeToViewModel() {
        viewModelNews.getArticles().observe(this, articles -> {
            newsAdapter.setArticles(articles);
            activityMainBinding.progressBar.setVisibility(View.GONE);  // Hide the progress bar
            activityMainBinding.newsRecyclerView.setVisibility(View.VISIBLE);
            activityMainBinding.logoutButton.setVisibility(View.VISIBLE);// Show the RecyclerView
        });

        viewModelNews.getIsLoading().observe(this, isLoading -> {
            activityMainBinding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            activityMainBinding.newsRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            activityMainBinding.logoutButton.setVisibility(isLoading ? View.GONE : View.VISIBLE);

        });

        viewModelNews.getResponseError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks and requests necessary permissions for the application.
     */
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(this, R.string.location_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Start fetching location from the watch
     */
    private void startLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    /**
     * This function is used to check whether user has valid login session or not
     */
    private boolean isSessionValid() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_SESSION, MODE_PRIVATE);
        long loginTimestamp = sharedPreferences.getLong(Constants.LOGIN_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();
        long sessionTimeout = Constants.LOGIN_SESSION;
        return (currentTime - loginTimestamp) < sessionTimeout;
    }

    /**
     * This function is used to logout user if session is invalid
     */
    public void checkSessionAndLogoutIfNeeded() {
        if (!isSessionValid()) {
            logoutUser();
        }
    }

    /**
     * This function is used when user logsout. Stop the location service and clear the room database and shared preference values
     */
    public void logoutUser() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        LocationDao dao = db.locationDao();
        Completable.fromAction(dao::deleteAll)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_SESSION, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    stopLocationService();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Log.d("Logout", "User Logged out and database cleared");
                }, throwable -> {
                    Log.e("MainActivity", "Error clearing the database", throwable);
                });
    }

    /**
     * This function is used to stop the location service
     */
    private void stopLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
    }

    /**
     * This function is used to display logout dialog box
     */
    private void showLogoutConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(".")
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton(R.string.no, null)
                .setCancelable(false)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        negativeButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        positiveButton.setOnClickListener(view -> logoutUser());
        negativeButton.setOnClickListener(view -> dialog.dismiss());
    }


    /**
     * This function is used for scroll view using rotatory input
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_SCROLL &&
                event.isFromSource(InputDevice.SOURCE_ROTARY_ENCODER)) {
            Log.d("RotaryInput", "Rotary event detected in activity.");
            final float delta = -event.getAxisValue(MotionEvent.AXIS_SCROLL) *
                    ViewConfiguration.get(this).getScaledVerticalScrollFactor();
            activityMainBinding.newsRecyclerView.scrollBy(0, Math.round(delta));

            return true;
        }
        return super.onGenericMotionEvent(event);
    }


}
