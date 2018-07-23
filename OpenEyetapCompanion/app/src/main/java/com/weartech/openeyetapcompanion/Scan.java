package com.weartech.openeyetapcompanion;

import android.Manifest;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.bluetooth.BluetoothAdapter.ERROR;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

public class Scan extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private final static String TAG = "ScanActivity";

    BluetoothAdapter mBluetoothAdapter;
    Button buttonScan;

    ListView lvNewDevices;
    public ArrayList<BluetoothDevice> mNewDevices = new ArrayList<>();
    public DeviceListAdapter mNewDeviceListAdapter;

    ListView lvPairedDevices;
    public ArrayList<BluetoothDevice> mPairedDevices = new ArrayList<>();
    public DeviceListAdapter mPairedDeviceListAdapter;

    TextView statusBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Bluetooth adapter
        mBluetoothAdapter = getDefaultAdapter();

        // UI elements
        buttonScan = (Button) findViewById(R.id.button_scan);
        lvNewDevices = (ListView) findViewById(R.id.list_new);
        lvNewDevices.setOnItemClickListener(Scan.this);

        lvPairedDevices = (ListView) findViewById(R.id.list_paired);
        lvPairedDevices.setOnItemClickListener(Scan.this);

        statusBar = (TextView) findViewById(R.id.status_bar);

        // Check if Bluetooth is supported
        if(mBluetoothAdapter == null) {
            Log.d(TAG, "toggleBT: Does not have BT capabilities");

            // TODO alert, and force quit
        }

        // Check if Bluetooth is enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBluetoothStateReceiver, BTIntent);
        }

        statusBar.setText("Bluetooth enabled.");

        // Bluetooth pairing
        IntentFilter BTPair = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiverPair, BTPair);

        scanDevices();
    }


    public void buttonScan(View view) {
        scanDevices();
    }

    public void scanDevices() {
        // clearing list view and device list
        statusBar.setText("Scanning for devices...");

        mNewDevices.clear();
        mPairedDevices.clear();

        lvPairedDevices.setAdapter(null);

        lvNewDevices.setAdapter(null);

        Log.d(TAG, "buttonDiscover: Searching for devices");

        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "buttonDiscover: Cancelling discovery");
        }
        checkBTPermissions();

        Log.d(TAG, "buttonDiscover: Starting discovery");

        mBluetoothAdapter.startDiscovery();
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiverDiscover, discoverDevicesIntent);
    }

    private void checkBTPermissions() {
        int permissionCheck = this.checkSelfPermission("Manifsest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifsest.permission.ACCESS_COARSE_LOCATION");

        if(permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        else {
            Log.d(TAG, "checkBTPermissions: No need for permissions");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getAdapter().equals(mNewDeviceListAdapter)) {

            mBluetoothAdapter.cancelDiscovery();

            BluetoothDevice device = mNewDevices.get(i);

            Log.d(TAG, "onItemClick: item clicked");
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();

            Log.d(TAG, "onItemClick: Name: " + deviceName);
            Log.d(TAG, "onItemClick: Address: " + deviceAddress);

            device.createBond();
        }
        else if (adapterView.getAdapter().equals(mPairedDeviceListAdapter)) {
            statusBar.setText("Attempting to connect...");

            mBluetoothAdapter.cancelDiscovery();

            BluetoothDevice device = mPairedDevices.get(i);

            // Switch to device activity
            Intent intent = new Intent(this, Main.class);
            intent.putExtra("BT_DEVICE", device);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();

        try{
            unregisterReceiver(mBluetoothStateReceiver);
        }
        catch(Exception e) {
            Log.d(TAG, e.getMessage());
        }

        try{
            unregisterReceiver(mBroadcastReceiverDiscover);
        }
        catch(Exception e) {
            Log.d(TAG, e.getMessage());
        }

        try{
            unregisterReceiver(mBroadcastReceiverPair);
        }
        catch(Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND, from developer.android.com
    private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // from video by CodingWithMitch
            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, ERROR);
                Log.d(TAG, "mBluetoothStateReceiver: " + state);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE_OFF");
                        statusBar.setText("Bluetooth disabled.");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE_TURNING_OFF");
                        statusBar.setText("Disabling Bluetooth...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE_ON");
                        statusBar.setText("Bluetooth enabled.");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE_TURNING_ON");
                        statusBar.setText("Enabling Bluetooth...");
                        break;
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiverDiscover = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            Log.d(TAG, "onReceive: ACTION_FOUND");

            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // TODO change device filter to use something other than name
                //if (!mNewDevices.contains(device) && device.getName().equals("bananapi")) {
                if (!mNewDevices.contains(device) && !mPairedDevices.contains(device)) {
                    // check if this device is already paired
                    if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        mPairedDevices.add(device);

                        mPairedDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mPairedDevices, "connect");
                        lvPairedDevices.setAdapter(mPairedDeviceListAdapter);
                    }
                    else {
                        mNewDevices.add(device);
                        Log.d(TAG, "onReceive: " + device.getName() + " : " + device.getAddress());

                        mNewDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mNewDevices, "pair");
                        lvNewDevices.setAdapter(mNewDeviceListAdapter);
                    }
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiverPair = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                Log.d(TAG, "onReceive: ACTION_BOND_STATE_CHANGED");

                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mNewDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mNewDevices, "pair");
                lvNewDevices.setAdapter(mNewDeviceListAdapter);

                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED");
                    statusBar.setText("Device paired.");

                    // refresh list views
                    mPairedDevices.add(mDevice);
                    mPairedDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mPairedDevices, "connect");
                    lvPairedDevices.setAdapter(mPairedDeviceListAdapter);

                    mNewDevices.remove(mDevice);
                    mNewDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mNewDevices, "pair");
                    lvNewDevices.setAdapter(mNewDeviceListAdapter);
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING");
                    statusBar.setText("Pairing device...");
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE");
                    statusBar.setText("No pairing.");
                }
            }
        }
    };
}

