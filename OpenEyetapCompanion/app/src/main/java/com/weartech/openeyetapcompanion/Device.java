package com.weartech.openeyetapcompanion;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.UUID;

public class Device extends AppCompatActivity {
    private static final String TAG = "DEVICE";
    private static final UUID OET_UUID = UUID.fromString("6380f8d1-497a-4143-975d-e4d636cb628e");

    BluetoothService mBluetoothService;
    BluetoothDevice mDevice;

    // UI elements
    TextView textName;
    TextView textID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mDevice = extras.getParcelable("BT_DEVICE");
        }

        textName = findViewById(R.id.device_name);
        textID = findViewById(R.id.device_ID);

        textName.setText(mDevice.getName());
        textID.setText(mDevice.getAddress());

        // start bluetooth service
        mBluetoothService = new BluetoothService(Device.this);

        startConnection();

        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, new IntentFilter("Msg"));
    }

    public void startBluetoothConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "Initializing Bluetooth connection");

        mBluetoothService.startClient(device, uuid);
    }

    public void startConnection() {
        startBluetoothConnection(mDevice, OET_UUID);
    }

    public void buttonPingDevice(View view) {
        sendMessage("Ping Test", "Open Eyetap Companion", "This is a test ping");
    }

    private void sendMessage(String title, String pack, String text) {
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", title);
            notification.put("package", pack);
            notification.put("text", text);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to package JSON object");
        }

        mBluetoothService.write(notification.toString().getBytes(Charset.defaultCharset()));
    }

    // based on code written by mukesh, 19/5/15
    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Forwarding notification to Bluetooth");
            String notifTitle = intent.getStringExtra("title");
            String notifPackage = intent.getStringExtra("package");
            String notifText = intent.getStringExtra("text");

            sendMessage(notifTitle, notifPackage, notifText);
        }
    };
}
