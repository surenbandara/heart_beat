package com.krakendepp.heart_beat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.krakendepp.heart_beat.Authentication.AuthManager;
import com.krakendepp.heart_beat.BTCommunication.BTConnector;
import com.krakendepp.heart_beat.DataBaseManager.PreferenceManager;
import com.krakendepp.heart_beat.GraphPlotter.GraphPlotter;
import com.krakendepp.heart_beat.databinding.ActivityMain2Binding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import androidx.activity.result.ActivityResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    public AlertDialog alertDialog, alertDialogSearch, alertDialogUserAdd;

    public ListView listView, listView1, discoverdDeviceViewList;
    public TextView chosePersonal, pastRecord ,btState;
    public Button refreshButton, cacelButton, addUserButton, cancleButtonUserAdd;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1001;

    private SearchView searchView;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter adapterForBT;
    private ArrayList pairedDevices = new ArrayList();
    private ArrayList<String> personalNameList;

    private ImageView imageViewButton;
    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    Intent intent = getIntent(); // Get the current Intent
                    finish(); // Finish the current activity
                    startActivity(intent); // Start the activity again with the same Intent

                }
            }
    );
    private final AuthManager authManager = new AuthManager(this, signInLauncher);

    private PreferenceManager preferenceManager;
    private JSONObject personals;
    private String userEmail;

    private EditText nameField;
    private String selectedPersonal = null;

    private ProgressBar progressBar;
    private GraphPlotter graphPlotter;

    private BTConnector btConnector = new BTConnector(this);
    private Context context = this;


    private Task<LocationSettingsResponse> task;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH ,
                        Manifest.permission.BLUETOOTH_CONNECT ,
                        Manifest.permission.BLUETOOTH_CONNECT ,
                        Manifest.permission.BLUETOOTH_SCAN ,
                        Manifest.permission.BLUETOOTH_ADMIN},
                11);



        LocationRequest locationRequest = LocationRequest.create();
        LocationSettingsRequest.Builder builder_ = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder_.build());

        task.addOnFailureListener(e -> {
            // Handle failure
            if (e instanceof ResolvableApiException) {

                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MainActivity2.this,34345);
                } catch (Exception ex) {
                    // Ignore the error
                }
            }
        });










        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        preferenceManager = new PreferenceManager(this);
        personalNameList = new ArrayList<>();

        if (user != null) {
            userEmail = user.getEmail();
            personals = preferenceManager.getJson(userEmail);

        } else {
            personals = new JSONObject();
            userEmail = "guest";
        }

        for (Iterator<String> it = personals.keys(); it.hasNext(); ) {
            String key = it.next();
            personalNameList.add(key);
        }

        graphPlotter = new GraphPlotter((LineChart) findViewById(R.id.lineChart));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        // Generate a random number between 1 and 10
//                        int randomNumber = (int) (Math.random() * 10) + 1;
//                        graphPlotter.addEntry(randomNumber);
//
//
//                        // Sleep for 500 milliseconds
//                        Thread.sleep(5);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();


        btState = findViewById(R.id.btState);
        chosePersonal = findViewById(R.id.chosePersonal);
        pastRecord = findViewById(R.id.personPastRecord);


        ArrayList<String> trimesterList = new ArrayList<>();
        trimesterList.add("First Trimester (120 - 150 BPM)");
        trimesterList.add("Second Trimester (110 - 160 BPM)");
        trimesterList.add("Third Trimester (90 - 140 BPM)");

        // Setting up the Spinner with the ArrayList
        Spinner trimesterSpinner = findViewById(R.id.trimesterSpinner);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trimesterList);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trimesterSpinner.setAdapter(adapterSpinner);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        builder.setView(dialogView);
        alertDialog = builder.create();

        AlertDialog.Builder builderSearch = new AlertDialog.Builder(this);
        View dialogViewSearch = getLayoutInflater().inflate(R.layout.popup_search, null);
        builderSearch.setView(dialogViewSearch);
        alertDialogSearch = builderSearch.create();

        AlertDialog.Builder builderCreateUser = new AlertDialog.Builder(this);
        View dialogViewCreateUser = getLayoutInflater().inflate(R.layout.popup_adduser, null);
        builderCreateUser.setView(dialogViewCreateUser);
        alertDialogUserAdd = builderCreateUser.create();

        nameField = dialogViewCreateUser.findViewById(R.id.user_name);
        addUserButton = dialogViewCreateUser.findViewById(R.id.save_button);
        cancleButtonUserAdd = dialogViewCreateUser.findViewById(R.id.cancel_button_adduser);


        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialogUserAdd != null && alertDialogUserAdd.isShowing()) {
                    personalNameList.add(nameField.getText().toString());

                    if (userEmail != "guest") {
                        try {
                            personals.put(nameField.getText().toString(), 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        preferenceManager.saveJson(userEmail, personals);
                    } else {
                        try {
                            personals.put(nameField.getText().toString(), 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    nameField.setText("");
                    alertDialogUserAdd.dismiss();

                }
            }
        });
        cancleButtonUserAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialogUserAdd != null && alertDialogUserAdd.isShowing()) {
                    alertDialogUserAdd.dismiss();

                }
            }
        });


        // Initialize views
        listView1 = dialogViewSearch.findViewById(R.id.list_view);
        searchView = dialogViewSearch.findViewById(R.id.search_view);
        imageViewButton = dialogViewSearch.findViewById(R.id.image_view_button);


        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Perform actions based on the clicked item
                Object item = parent.getItemAtPosition(position);
                selectedPersonal = item.toString();
                chosePersonal.setText(selectedPersonal);
                try {
                    if (personals.get(selectedPersonal).toString().equals("0")) {
                        pastRecord.setText("No Records");
                    } else {
                        pastRecord.setText(personals.get(selectedPersonal).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                alertDialogSearch.dismiss();
            }
        });
        imageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogSearch.dismiss();
                alertDialogUserAdd.show();
            }
        });

        // Initialize adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, personalNameList);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


        listView = dialogView.findViewById(R.id.paired_devices_list_view);
        discoverdDeviceViewList = dialogView.findViewById(R.id.discovered_devices_list_view);
        progressBar = dialogView.findViewById(R.id.progress_bar_discoverable);
        refreshButton = dialogView.findViewById(R.id.refresh_button);
        cacelButton = dialogView.findViewById(R.id.cancel_button);


        discoverdDeviceViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Perform actions based on the clicked item
                BluetoothDevice item = (BluetoothDevice) parent.getItemAtPosition(position);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        boolean pairing = false;
                        try {
                            pairing = btConnector.createBond(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (pairing) {
                            BluetoothSocket bluetoothSocket = btConnector.connectToDevice(item);
                            if(bluetoothSocket != null && bluetoothSocket.isConnected()){
                                btState.setText("Connected");
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                                Toast.makeText(getApplicationContext(), "Connection established : " + item.getName(), Toast.LENGTH_SHORT).show();

                                try {
                                    InputStream inputStream = bluetoothSocket.getInputStream();
                                    while (true) {
                                        byte b = (byte) inputStream.read();
                                        System.out.println((char) b);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                                Toast.makeText(getApplicationContext(), "Connection failed : " + item.getName(), Toast.LENGTH_SHORT).show();

                            }


                        }
                    }
                });


                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                Toast.makeText(getApplicationContext(), "Connecting with : " + item.getName(), Toast.LENGTH_SHORT).show();



            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Perform actions based on the clicked item
                BluetoothDevice item = (BluetoothDevice) parent.getItemAtPosition(position);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothSocket bluetoothSocket = btConnector.connectToDevice(item);
                        if(bluetoothSocket != null && bluetoothSocket.isConnected()){
                            btState.setText("Connected");
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                            Toast.makeText(getApplicationContext(), "Connection established : " + item.getName(), Toast.LENGTH_SHORT).show();

                            try {
                                InputStream inputStream = bluetoothSocket.getInputStream();
                                while (true) {
                                    byte b = (byte) inputStream.read();
                                    System.out.println((char) b);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                            Toast.makeText(getApplicationContext(), "Connection failed : " + item.getName(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });


                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}

                Toast.makeText(getApplicationContext(), "Connecting with : " + item.getName(), Toast.LENGTH_SHORT).show();

                            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.addOnFailureListener(e -> {
                    // Handle failure
                    if (e instanceof ResolvableApiException) {

                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity2.this,34345);
                        } catch (Exception ex) {
                            // Ignore the error
                        }
                    }
                });

                pairedDevices = btConnector.getBondedDevices();
                adapterForBT = new ArrayAdapter(context, android.R.layout.simple_list_item_1, pairedDevices)
                {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        // Get the object at the current position
                        BluetoothDevice device = (BluetoothDevice) pairedDevices.get(position);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                        textView.setText(device.getName());
                        return view;
                    }};
                listView.setAdapter(adapterForBT);

                btConnector.getDiscoverableDevices(discoverdDeviceViewList, progressBar);


            }
        });
        cacelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();

                }
            }
        });

    }

    // Method to create a rounded bitmap
    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get reference to the profile picture ImageView
        MenuItem profileItem = menu.findItem(R.id.profile);

        // Load the user's profile picture using Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                Picasso.get().load(photoUrl).placeholder(R.drawable.default_profile_image).into(
                        new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                // Resize the bitmap if needed (e.g., for icons)
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                                Bitmap roundedBitmap = getRoundedBitmap(resizedBitmap);
                                profileItem.setIcon(new BitmapDrawable(getResources(), roundedBitmap));
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                // Handle error loading bitmap
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                // Optionally implement this method if needed
                            }
                        });
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        System.out.println(id);
        if (id == R.id.search) {
            alertDialogSearch.show();
            // Set adapter to ListView
            listView1.setAdapter(adapter);
            return true;
        } else if (id == R.id.bluetooth) {

            task.addOnFailureListener(e -> {
                // Handle failure
                if (e instanceof ResolvableApiException) {

                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity2.this,34345);
                    } catch (Exception ex) {
                        // Ignore the error
                    }
                }
            });
            if (btConnector.isDisabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
                }
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 323434);
            } else {
                pairedDevices = btConnector.getBondedDevices();
                adapterForBT = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pairedDevices) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        // Get the object at the current position
                        BluetoothDevice device = (BluetoothDevice) pairedDevices.get(position);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
                        textView.setText(device.getName());
                    return view;
                }
            };
                listView.setAdapter(adapterForBT);

                btConnector.getDiscoverableDevices(discoverdDeviceViewList,progressBar);
                alertDialog.show();}
            return true;}

        else if(id == R.id.sign_out){
            authManager.signOut();
            return true;}


        return super.onOptionsItemSelected(item);
    }


}