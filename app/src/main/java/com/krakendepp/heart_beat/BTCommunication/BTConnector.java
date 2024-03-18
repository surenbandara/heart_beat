package com.krakendepp.heart_beat.BTCommunication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BTConnector {
    private final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    public BTConnector(Context context){
        this.context = context;
    }

    public Set<BluetoothDevice> getDevices() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();

        return bondedDevices;
    }

    public BluetoothSocket connectToDevice(BluetoothDevice device) {
        BluetoothSocket btSocket = null;
        int counter = 0;
        do {
            try {
                if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                btSocket = device.createRfcommSocketToServiceRecord(mUUID);
                btSocket.connect();
            } catch ( IOException e) {
                e.printStackTrace();
            }
            counter++;
        } while (!btSocket.isConnected() && counter < 3);
        return btSocket;
    }
}
