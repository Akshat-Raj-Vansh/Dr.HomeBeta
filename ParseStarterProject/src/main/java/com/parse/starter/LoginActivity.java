/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  Boolean signUpModeActive = true;
  TextView loginTextView;
  EditText usernameEditText;
  EditText passwordEditText;

  String userType = "";
  SharedPreferences sharedPreferences;
  public void showUserList() {
    sharedPreferences.edit().putBoolean("isLoggedIn?", true).apply();
    Intent intent = new Intent(getApplicationContext(), HomeActivityPatient.class);
    startActivity(intent);
  }
  public void showAppointmentList() {
    Intent intent = new Intent(getApplicationContext(), HomeActivityDoctor.class);
    startActivity(intent);
  }

  public void JumpToPatientProfileUpdate() {
    Intent intent = new Intent(getApplicationContext(), PatientProfileFillingActivity.class);
    startActivity(intent);
  }

  public void JumpToDoctorProfileUpdate() {
    Intent intent = new Intent(getApplicationContext(), DoctorProfileFillingActivity.class);
    startActivity(intent);
  }

  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
      signUpClicked(view);
    }

    return false;
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.loginTextView) {
      Button signUpButton = findViewById(R.id.signUpButton);

      if (signUpModeActive) {
        signUpModeActive = false;
        signUpButton.setText("Login");
        loginTextView.setText("or, Sign Up");
      } else {
        signUpModeActive = true;
        signUpButton.setText("Sign Up");
        loginTextView.setText("or, Login");
      }

    } else if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
  }

  public void setUserId(ParseUser user) {     // whether doctor or patient

    Switch userTypeSwitch = (Switch) findViewById(R.id.userIdSwitch);
    Log.i("Switch value", String.valueOf(userTypeSwitch.isChecked()));
    userType = "patient";
    if (userTypeSwitch.isChecked()) {
      userType = "doctor";
    }
    user.put ("patientOrDoctor",userType);
    Log.i("Info", "Redirecting as " + userType);
  }

  public void signUpClicked(View view) {

    if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
      Toast.makeText(this, "A username and a password are required.",Toast.LENGTH_SHORT).show();

    } else {
      if (signUpModeActive) {
        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());
        setUserId(user);
        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("Signup", "Success");
              //    setUserId();
              if(userType.equals("patient"))
                JumpToPatientProfileUpdate();
              else if(userType.equals("doctor")){
                JumpToDoctorProfileUpdate();
              }
              //  showUserList();
            } else {
              Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      } else {
        // Login
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (user != null) {

              if(ParseUser.getCurrentUser().get("fullName")==null){
                if(ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("doctor")){
                  JumpToDoctorProfileUpdate();
                }
                else if(ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("patient")){
                  JumpToPatientProfileUpdate();
                }
              }
              else {
                Log.i("Login", "ok!");
                if(ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("doctor")){
                  showAppointmentList();
                }
                else if(ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("patient")){
                  showUserList();
                }
                //  setUserId();

              }
            } else {
              Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i("ACTIVITY","MAIN ACTIVITY LAUNCHED!!!");
    setTitle("Sign Up / Login");
    setContentView(R.layout.activity_login);
    sharedPreferences = this.getSharedPreferences("Dr.Home local", MODE_PRIVATE);
    loginTextView = findViewById(R.id.loginTextView);
    loginTextView.setOnClickListener(this);
    usernameEditText = findViewById(R.id.usernameEditText);
    passwordEditText = findViewById(R.id.passwordEditText);
    ImageView logoImageView = findViewById(R.id.logoImageView);
    ConstraintLayout backgroundLayout =  findViewById(R.id.backgroundLayout);
    logoImageView.setOnClickListener(this);
    backgroundLayout.setOnClickListener(this);
    passwordEditText.setOnKeyListener(this);

    if (ParseUser.getCurrentUser() != null) {
      if(ParseUser.getCurrentUser().get("fullName")==null) {
        if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("doctor")) {
          JumpToDoctorProfileUpdate();
        } else if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("patient")) {
          JumpToPatientProfileUpdate();
        }
      }
      else
      if(ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("doctor")){
        showAppointmentList();
      }
      else if(ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("patient")){
        showUserList();
      }
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}