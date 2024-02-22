package com.example.watchosapplicaion.presentation.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
 * SystemBroadcastReceiver : This is the SystemBroadcastReceiver in the Watch android application
 * and is responsible to listen to event defined in the file.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

public class SystemBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SystemBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);
        Intent broadcastIntent = new Intent(Constants.ACTION_BROADCAST_EVENT);
        broadcastIntent.putExtra(Constants.EXTRA_ACTION, action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
                Log.d(TAG, "Device just finished booting");
                break;
            case Intent.ACTION_BATTERY_LOW:
                Log.d(TAG, "Battery is low");
                break;
            case Intent.ACTION_BATTERY_OKAY:
                Log.d(TAG, "Battery is okay");
                break;
            case Intent.ACTION_POWER_CONNECTED:
                Log.d(TAG, "Power connected");
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                Log.d(TAG, "Power disconnected");
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    Log.d(TAG, "Bluetooth is on");
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    Log.d(TAG, "Bluetooth is off");
                }
                break;

        }
    }
}
