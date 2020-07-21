package com.parse.starter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PatientProfile extends AppCompatActivity {

    TextView pat_Name, pat_Phone, pat_Loc, pat_Age;
    String PatientName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        PatientName = getIntent().getStringExtra("Name");
        setTitle("Patient Profile");
        Log.i("here","here");
        PatientName= PatientName.substring(PatientName.lastIndexOf(" ") + 1);
        Log.i("here","here!!!!!!!!!");
        Toast.makeText(PatientProfile.this, "Name is " + PatientName, Toast.LENGTH_SHORT).show();
        Log.i("DoctorName", "Name is " + PatientName);
        linkLayoutFeatures();
        if (PatientName.equals(ParseUser.getCurrentUser().get("username").toString()))
            initializeID();
        else
            getInformation();
//        loadDP();
    }


    private void getInformation() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", PatientName);
//        query.setLimit(1);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Log.i("User","FOUND!!!!!!!!!!!");
                        for (ParseUser user : objects) {
                            pat_Name.setText(user.get("fullName").toString());
                            pat_Phone.setText("Phone: " + user.get("Mobile_Number").toString());
                            pat_Loc.setText("Address: " + user.get("Address").toString());
                            pat_Age.setText("Age: "+user.get("Age").toString());

                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void linkLayoutFeatures() {
        pat_Name = findViewById(R.id.pat_name);
        pat_Loc = findViewById(R.id.pat_loc);
        pat_Phone = findViewById(R.id.pat_num);
        pat_Age = findViewById(R.id.pat_age);
    }

    @SuppressLint("SetTextI18n")
    private void initializeID() {
        ParseUser Doctor;
        Doctor = ParseUser.getCurrentUser();
        pat_Name.setText(Doctor.get("username").toString());
        pat_Phone.setText("Phone: " + Doctor.get("Mobile_Number").toString());
        pat_Loc.setText("Address: " + Doctor.get("Address").toString());
        pat_Age.setText("Age:"+Doctor.get("Age").toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.editProfile) {
            Log.i("user:",ParseUser.getCurrentUser().toString());
            if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("patient")) {
                Intent intent = new Intent(getApplicationContext(), PatientProfileEditingActivity.class);
                startActivity(intent);
            } else if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("doctor")) {
                Intent intent = new Intent(getApplicationContext(), DoctorProfileEditActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    public void backPressed(View view){
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }
    public void shareContact(View view) {
        Toast.makeText(PatientProfile.this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }
    public void editProfile(View view) {
        Log.i("user:",ParseUser.getCurrentUser().toString());
        if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("patient")) {
            Intent intent = new Intent(getApplicationContext(), PatientProfileEditingActivity.class);
            startActivity(intent);
        } else if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("doctor")) {
            Intent intent = new Intent(getApplicationContext(), DoctorProfileEditActivity.class);
            startActivity(intent);
        }
    }
}