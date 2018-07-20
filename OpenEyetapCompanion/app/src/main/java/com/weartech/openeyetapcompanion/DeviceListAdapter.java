package com.weartech.openeyetapcompanion;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice>{

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int mViewResourceId;

    private ImageView icon;

    private String iconType;

    public DeviceListAdapter(Context context, int resource, ArrayList<BluetoothDevice> devices, String iconType) {
        super(context, resource, devices);

        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        mViewResourceId = resource;

        this.iconType = iconType;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        BluetoothDevice device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.textDeviceName);
            TextView deviceAddress = (TextView) convertView.findViewById(R.id.textDeviceAddr);
            // TextView deviceBond = (TextView) convertView.findViewById(R.id.textDeviceBonded);
            ImageView icon = (ImageView) convertView.findViewById(R.id.scan_device_action_icon);

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }
            if (deviceAddress != null) {
                deviceAddress.setText(device.getAddress());
            }

            switch(this.iconType) {
                case "pair":
                    icon.setImageResource(R.drawable.add);
                    break;
                case "connect":
                    icon.setImageResource(R.drawable.link);
                    break;
                default:
                    icon.setImageResource(R.drawable.add);
                    break;
            }
            /*
            if (deviceBond != null) {
                switch(device.getBondState()) {
                    case BluetoothDevice.BOND_BONDED:
                        deviceBond.setText("Bonded");
                        deviceBond.setTextColor(Color.parseColor("#66ba57"));
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        deviceBond.setText("Bonding");
                        deviceBond.setTextColor(Color.parseColor("#bababa"));
                        break;
                    case BluetoothDevice.BOND_NONE:
                        deviceBond.setText("No Bond");
                        deviceBond.setTextColor(Color.parseColor("#bababa"));
                        break;
                }
            } */
        }

        return convertView;
    }
}
