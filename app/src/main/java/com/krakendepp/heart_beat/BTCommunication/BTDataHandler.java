package com.krakendepp.heart_beat.BTCommunication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.krakendepp.heart_beat.DataBaseManager.PreferenceManager;
import com.krakendepp.heart_beat.GraphPlotter.GraphPlotter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

//For starting to listen just use .start()

public class BTDataHandler extends AsyncTask<Void, Void, String> {

    private BTConnector btConnector;
    private BluetoothDevice bluetoothDevice;
    private Context context ;
    private TextView btstate ,heartRate , pastRecords , statusTextView;
    private GraphPlotter graphPlotter;
    private Activity activity;
    private ImageButton startButton;
    private Boolean start;

    private static JSONObject personals ;
    private  String selectedPersonal , userEmail ;
    private static PreferenceManager preferenceManager;

    private int triMax, triMin;


    public BTDataHandler(Context context , Activity activity, BTConnector btConnector,
                         BluetoothDevice bluetoothDevice , TextView btstate ,
                         TextView heartRate , ImageButton startButton ,
                         JSONObject personals , String selectedPersonal ,String userEmail , PreferenceManager preferenceManager,
                         TextView statusTextView , TextView pastRecords,
                         GraphPlotter graphPlotter ){

        this.personals = personals;
        this.selectedPersonal = selectedPersonal;
        this.userEmail = userEmail;
        this.preferenceManager = preferenceManager;

        this.statusTextView = statusTextView;
        this.pastRecords = pastRecords;

        this.btConnector = btConnector;
        this.bluetoothDevice = bluetoothDevice;
        this.context  = context;
        this.btstate = btstate;
        this.heartRate = heartRate;
        this.startButton = startButton;
        this.graphPlotter = graphPlotter;
        this.activity = activity;
        this.start = false;

        System.out.println("-----------------11111");

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start = true;
            }
        });
    }

    public void setTrimesterRange(int min ,int max){
        this.triMax = max;
        this.triMin = min;
    }

    public void setSelectedPersonal(String selectedPersonal){
        this.selectedPersonal = selectedPersonal;
    }

    public void setPersonals(JSONObject personals){
        this.personals = personals;
    }

    @Override
    protected String doInBackground(Void... voids) {

//            System.out.println("-----------------");
//            // Update UI components here if needed
//            onProgressUpdate(-1);
//            System.out.println("All good up to now");
//
//
//                for (int i = 0; i < 100; i++) {
//                    onProgressUpdate(1000);
//                    try {
//                        Thread.sleep(50); // 50 milliseconds
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//
//                while (true) {
//                    int finalValue = 80 + (int) (Math.random() * 20);
//                    if (finalValue == 1000){
//                        break;
//                    }
//                    activity.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            heartRate.setText(String.valueOf(finalValue)+" bpm");}});
//
//                    onProgressUpdate(finalValue);
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        System.out.println("Problem in sleep ");
//                    }
//                }
//
//
//
//        return "Ok";
        BluetoothSocket bluetoothSocket= btConnector.connectToDevice(bluetoothDevice);
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            // Update UI components here if needed
            onProgressUpdate(-1);
            System.out.println("All good up to now");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}


            try {
                InputStream inputStream = bluetoothSocket.getInputStream();
                String constructor = "";
                int value;

                for (int i = 0; i < 100; i++) {
                    onProgressUpdate(1000);
                    try {
                        Thread.sleep(50); // 50 milliseconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (true) {

                    byte b = (byte) inputStream.read();
                    if (b != 13 && b != 10) {
                        constructor = constructor + (char) b;
                    } else {
                        if (b == 13) {

                            try{
                                Thread.sleep(50);
                            value = Integer.parseInt(constructor);
                                int finalValue = value;
                                activity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                            heartRate.setText(String.valueOf(finalValue)+" bpm");}});
                            constructor = "";
                            onProgressUpdate(value);
                            }
                            catch (Exception e){
                                System.out.println(e);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                onProgressUpdate(-2);
            }
        } else {
            // Update UI components here if needed
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}

        }

        return "Ok";

    }

    protected void onProgressUpdate(Integer... values) {
        // Update the UI with the progress value
        int ind = values[0];
        if(ind == -1){

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    btstate.setText("Connected");
                    btstate.setTextColor(Color.rgb(0,255,0)); }});

        }

        else if (ind == -2){
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
            btstate.setText("Disconneted");
            btstate.setTextColor(Color.parseColor("#9A90A5"));}});
        }

        else if (ind == 1000){
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    graphPlotter.addEntry(50);}});
        }

        else{
            if(start){
                try {
                    if(selectedPersonal !=null){
                        System.out.println(ind);
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(context,"Heat beat recorded for "+selectedPersonal+" : "+ String.valueOf(ind), Toast.LENGTH_LONG);
                        pastRecords.setText(String.valueOf(ind)+" bpm");}});

                    personals.put(selectedPersonal, ind);
                    preferenceManager.saveJson(userEmail, personals);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                start = false;

            }
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(ind<triMin){
                    statusTextView.setText("Low");
                        statusTextView.setTextColor(Color.rgb(255,0,0));}

                    else if (ind>triMax){
                        statusTextView.setText("High");
                        statusTextView.setTextColor(Color.rgb(255,0,0));
                    }
                    else{
                        statusTextView.setText("Great");
                        statusTextView.setTextColor(Color.rgb(0,255,0));
                    }
                    graphPlotter.addEntry(ind);}});

        }

    }


}



