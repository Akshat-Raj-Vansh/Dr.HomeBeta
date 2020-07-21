package com.parse.starter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView appName = findViewById(R.id.appName);
        sharedPreferences = this.getSharedPreferences("Dr.Home local", MODE_PRIVATE);
        appName.setY(1000);
        appName.animate().translationYBy(-1000).setDuration(2000);
        ImageView splashImage = findViewById(R.id.splashImage);
        splashImage.animate().alpha(1.0f).setDuration(2000);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("SharePreferences SS", String.valueOf(sharedPreferences.getBoolean("isLoggedIn?", false)));
                if (!sharedPreferences.getBoolean("isLoggedIn?", false)) {
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                } else {
                    if (ParseUser.getCurrentUser().get("patientOrDoctor").equals("patient"))
                        startActivity(new Intent(SplashScreen.this, HomeActivityPatient.class));
                    else
                        startActivity(new Intent(SplashScreen.this, HomeActivityDoctor.class));
                }
                finish();
            }
        }, 2000);
    }
}