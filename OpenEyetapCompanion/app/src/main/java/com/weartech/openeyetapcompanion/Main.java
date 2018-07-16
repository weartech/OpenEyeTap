package com.weartech.openeyetapcompanion;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.nio.charset.Charset;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.ERROR;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;


public class Main extends AppCompatActivity {
    private final static String TAG = "MAIN";

    BluetoothDevice mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // for checking permissions
        /*Intent notificationIntent = new Intent(
                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");*/
        //startActivity(notificationIntent);
    }

    public void buttonNavigateToScan(View view) {
        Intent intent = new Intent(this, Scan.class);
        startActivity(intent);
    }


    public void buttonNavigateToDevice(View view) {
        Intent intent = new Intent(this, Device.class);
        startActivity(intent);
    }
}
