package com.krakendepp.heart_beat.BTCommunication;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;

//For starting to listen just use .start()

public class BTDataHandler extends Thread {
    private BluetoothSocket bluetoothSocket;

    public BTDataHandler(BluetoothSocket bluetoothSocket){
        this.bluetoothSocket = bluetoothSocket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = this.bluetoothSocket.getInputStream();
            while (true) {
                byte b = (byte) inputStream.read();
                System.out.println((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
