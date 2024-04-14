package com.krakendepp.heart_beat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.os.Handler;

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.krakendepp.heart_beat.Authentication.AuthManager;
import androidx.activity.result.ActivityResultCallback;

public class SplashActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        startActivity(new Intent(SplashActivity.this, MainActivity2.class));
                        finish();}

                    else if(result.getResultCode() == Activity.RESULT_CANCELED){

                        startActivity(new Intent(SplashActivity.this, MainActivity2.class));
                        finish();

                    }
                }
            }
    );
    private final AuthManager authManager = new AuthManager(this , signInLauncher);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        authManager.createSignInIntent();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity2.class);
                startActivity(i);

                finish();
            }
        }, 1000);}

    }
}