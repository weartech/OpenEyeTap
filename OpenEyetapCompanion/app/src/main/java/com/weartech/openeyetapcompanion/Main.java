package com.weartech.openeyetapcompanion;

import android.app.Notification;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.UUID;


public class Main extends AppCompatActivity {
    private final static String TAG = "MAIN";
    private static final UUID OET_UUID = UUID.fromString("6380f8d1-497a-4143-975d-e4d636cb628e");

    BluetoothDevice mDevice;
    BluetoothService mBluetoothService;

    TextView tvDeviceName;
    TextView tvDeviceAddress;

    ImageView ivScanDevice;

    View vDisconnect;
    Button bDisconnect;

    NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Elements
        tvDeviceName = findViewById(R.id.preview_name);
        tvDeviceAddress = findViewById(R.id.preview_addr);

        ivScanDevice = findViewById(R.id.iv_scan_device);

        vDisconnect = findViewById(R.id.disconnect_view);
        bDisconnect = findViewById(R.id.disconnect_button);

        // for checking permissions
        /*Intent notificationIntent = new Intent(
                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");*/
        //startActivity(notificationIntent);

        //Intent notifIntent = new Intent(this, NotificationService.class);
        //this.startService(notifIntent);

        Bundle extras = getIntent().getExtras();

        IntentFilter deviceDisconnectedIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mDeviceStatusReceiver, deviceDisconnectedIntent);

        if(extras != null) {
            mDevice = extras.getParcelable("BT_DEVICE");

            if(mDevice != null) {
                startConnection();
            }


        }

        updateUI();

        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, new IntentFilter("Msg"));



    }

    public void buttonManageDevice(View view) {
        if (mDevice == null) {          // if device is null, navigate to scan
            Intent intent = new Intent(this, Scan.class);
            startActivity(intent);
        }
        else {                          // otherwise, close connection
            // TODO: prompt user for confirmation, and close
        }

    }


    public void startBluetoothConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "Initializing Bluetooth connection");

        mBluetoothService = new BluetoothService(Main.this);

        mBluetoothService.startClient(device, uuid);
    }

    public void startConnection() {
        startBluetoothConnection(mDevice, OET_UUID);
    }

    private void sendMessage(String title, String pack, String text, String img) {
        Log.d(TAG, "Packaging notification.");

        JSONObject notification = new JSONObject();
        try {
            notification.put("title", title);
            notification.put("package", pack);
            notification.put("text", text);
            notification.put("img", img);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to package JSON object");
        }

        if(mBluetoothService != null) {
            mBluetoothService.writeJSON(notification.toString().getBytes(Charset.defaultCharset()), "notif");
        }
    }

    private void sendFile(byte[] bytes, String name, String ext) {
        if(mBluetoothService != null) {
            mBluetoothService.writeFile(bytes, name, ext);
        }
    }

    private void updateUI() {
        if(mDevice != null) {   // device connected
            tvDeviceName.setText(mDevice.getName());
            tvDeviceAddress.setText(mDevice.getAddress());

            ivScanDevice.setVisibility(View.GONE);

            vDisconnect.setVisibility(View.VISIBLE);
            bDisconnect.setVisibility(View.VISIBLE);
        }
        else {                  // no device connected
            tvDeviceName.setText(R.string.scan_for_devices);
            tvDeviceAddress.setText(R.string.addr_connect);

            ivScanDevice.setVisibility(View.VISIBLE);

            vDisconnect.setVisibility(View.GONE);
            bDisconnect.setVisibility(View.GONE);


        }
    }

    // based on code written by mukesh, 19/5/15
    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Forwarding notification to Bluetooth");
            String notifTitle = intent.getStringExtra("title");
            String notifPackage = intent.getStringExtra("package");
            String notifText = intent.getStringExtra("text");

            byte[] notifIcon = intent.getByteArrayExtra("icon");
            sendFile(notifIcon, "icon", "png");

            sendMessage(notifTitle, notifPackage, notifText, "icon.png");
        }
    };

    private BroadcastReceiver mDeviceStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Device disconnected.");
            mBluetoothService.closeConnections();
        }
    };

    public void buttonDisconnect(View view) {
        Log.d(TAG, "Disconnecting device from server");
        mBluetoothService.closeConnections();
        mDevice = null;
        updateUI();
    }
}
