package com.krakendepp.heart_beat.BTCommunication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BTConnector {
    private final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    private BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter adapterForDiscoverDevice;

    public BTConnector(Context context) {
        this.context = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isDisabled() {
        if (!btAdapter.isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList getBondedDevices() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
        pairedDevices = btAdapter.getBondedDevices();
        ArrayList list_ = new ArrayList();

        for (BluetoothDevice bt : pairedDevices) list_.add(bt);
        return list_;
    }

    public void getDiscoverableDevices(ListView listView, ProgressBar progressBar) {

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}

        if(!btAdapter.isDiscovering()){
            System.out.println("Scanning start");
            ArrayList<BluetoothDevice> discoverableDevices = new ArrayList<>();
            adapterForDiscoverDevice = new ArrayAdapter(context, android.R.layout.simple_list_item_1, discoverableDevices)
            {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                // Get the object at the current position
                BluetoothDevice device = (BluetoothDevice) discoverableDevices.get(position);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                textView.setText(device.getName());
                return view;
            }};

            listView.setAdapter(adapterForDiscoverDevice);

            BroadcastReceiver receiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    System.out.println("Found");
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                            discoverableDevices.add(device);
                            adapterForDiscoverDevice.notifyDataSetChanged(); // Notify adapter of data change
                            System.out.println(device.getName());
                        }
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        progressBar.setVisibility(View.VISIBLE); // Show ProgressBar
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        progressBar.setVisibility(View.INVISIBLE); // Hide ProgressBar

                    }
                }
            };

            // Register for ACTION_FOUND
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(receiver, filter);

            // Register for ACTION_DISCOVERY_STARTED and ACTION_DISCOVERY_FINISHED
            IntentFilter startFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            IntentFilter finishFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(receiver, startFilter);
            context.registerReceiver(receiver, finishFilter);

            btAdapter.startDiscovery();
        }

        else{
            System.out.println("Still scanning");
        }
    }

    public BluetoothSocket connectToDevice(BluetoothDevice device) {
        BluetoothSocket btSocket = null;
        int counter = 0;
        do {
            try {
                if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                btSocket = device.createRfcommSocketToServiceRecord(mUUID);
                btSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        } while (!btSocket.isConnected() && counter < 3);
        return btSocket;
    }

    public boolean pairWithDevice(BluetoothDevice device) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
            return btAdapter.getBondedDevices().add(device);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createBond(BluetoothDevice btDevice) throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }
}

