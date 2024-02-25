package com.example.watchosapplicaion.presentation.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.watchosapplicaion.R;
import com.example.watchosapplicaion.presentation.database.AppDatabase;
import com.example.watchosapplicaion.presentation.database.LocationDao;
import com.example.watchosapplicaion.presentation.database.LocationEntity;
import com.example.watchosapplicaion.presentation.utils.Constants;
import com.example.watchosapplicaion.presentation.view.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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
 * LocationService : This is the Location Service in the Watch android application.
 * -----------------------------------------------------------------------------------
 * <p>
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

public class LocationService extends Service {
    private android.os.Handler locationHandler = new android.os.Handler(Looper.getMainLooper());
    private Runnable locationRunnable;
    private Runnable deleteRunnable;
    private LocationCallback locationCallback;

    private FusedLocationProviderClient locationProviderClient;
    private NotificationManager notificationManager;
    private int notificationId = 1;
    private AppDatabase database;
    private LocationDao locationDataDao;

    /**
     * Initializes the service, creating necessary instances for location provider,
     * notification manager, database, and setup for runnables and notification channels.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        database = AppDatabase.getDatabase(this);
        locationDataDao = database.locationDao();
        createNotificationChannel();
        showGeneralNotification();
        setupLocationRunnable();
        setupDeleteRunnable();
    }

    /**
     * Creates a notification with a title and content.
     */
    private Notification getNotification(String title, String content, Location location, boolean withLocation) {
        String channelId = withLocation ? "location_service_with_location" : "location_service_without_location";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true);

        if (!withLocation) {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            // Open app on tap
            builder.setContentIntent(pendingIntent)
                    .setOngoing(true);
        } else {
            builder.setOngoing(false);

            if (location != null) {

                // Intent to open Google Maps
                String uri = "geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + location.getLatitude() + "," + location.getLongitude() + "(Label)" + "?z=20";
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");
                PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // Intent to open your list activity
                Intent listIntent = new Intent(this, MainActivity.class);
                PendingIntent listPendingIntent = PendingIntent.getActivity(this, 1, listIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                builder.addAction(R.drawable.google_maps, "Open Google Map", mapPendingIntent)
                        .addAction(R.drawable.customer, "Open App List", listPendingIntent);
            }
        }

        return builder.build();
    }


    /**
     * Creates a notification channel if the SDK version is Oreo or higher. This is necessary for showing notifications.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelWithLocation = new NotificationChannel(
                    "location_service_with_location",
                    "Location Updates",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelWithLocation.setDescription("Shows notifications with location updates.");
            NotificationChannel channelWithoutLocation = new NotificationChannel(
                    "location_service_without_location",
                    "General Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelWithoutLocation.setDescription("Shows notifications without location updates.");
            notificationManager.createNotificationChannel(channelWithLocation);
            notificationManager.createNotificationChannel(channelWithoutLocation);
        }
    }

    /**
     * Sets up the location update runnable. This is a placeholder for future implementation.
     */
    private void setupLocationRunnable() {
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            LocationEntity locationEntity = new LocationEntity(location.getLatitude(), location.getLongitude(), System.currentTimeMillis());
                            locationDataDao.insert(locationEntity)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        showLocationNotification(location);
                                    }, throwable -> {
                                    });
                        }
                    });
                }
                locationHandler.postDelayed(this, Constants.LOCATION_INTERVAL);
            }
        };
        locationHandler.postDelayed(locationRunnable, Constants.LOCATION_INTERVAL);
    }

    /**
     * Sets up the runnable for deleting location data at specified intervals.
     */
    private void setupDeleteRunnable() {
        deleteRunnable = new Runnable() {
            @Override
            public void run() {
                locationDataDao.deleteAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            locationHandler.postDelayed(deleteRunnable, Constants.DELETE_INTERVAL);
                        }, throwable -> {
                            locationHandler.postDelayed(deleteRunnable, Constants.DELETE_INTERVAL);
                        });
            }
        };
        locationHandler.postDelayed(deleteRunnable, Constants.DELETE_INTERVAL);
    }

    /**
     * Requests location updates when the service is started. Ensures the service continues running until explicitly stopped.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = getNotification("Foreground Service Running",
                "Service is actively running.",
                null,
                false);
        startForeground(notificationId, notification);
        requestLocationUpdates();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Cleans up when the service is destroyed, removing callbacks and stopping location updates.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        locationHandler.removeCallbacks(locationRunnable);
        locationHandler.removeCallbacks(deleteRunnable);
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Requests location updates, ensuring they are done on a background thread and processed accordingly.
     */
    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(Constants.LOCATION_INTERVAL);
        locationRequest.setFastestInterval(Constants.LOCATION_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        // Process each location update.
                        showLocationNotification(location);
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                // Handle changes in location availability.
            }
        };

        try {
            locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            // Handle the case where the user revoked the permissions.
        }
    }

    private void showLocationNotification(Location location) {
        String title = "Location Update";
        String content = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
        Notification notification = getNotification(title, content, location, true);
        notificationManager.notify(notificationId, notification);
    }

    private void showGeneralNotification() {
        String title = "Foreground Service";
        String content = "Foreground service is running";
        Notification notification = getNotification(title, content, null, false);
        notificationManager.notify(notificationId + 1, notification);
    }


}
