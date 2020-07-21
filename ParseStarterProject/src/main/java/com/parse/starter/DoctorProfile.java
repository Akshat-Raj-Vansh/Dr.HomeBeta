package com.parse.starter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class DoctorProfile extends AppCompatActivity {

    TextView doc_Name, doc_Loc, doc_Spec;
    ImageView doc_Img, doc_ImgBlur;
    String DocName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        setTitle("Doctor Profile");
        DocName = getIntent().getStringExtra("Name");
        Toast.makeText(DoctorProfile.this, "Name is " + DocName, Toast.LENGTH_SHORT).show();
        Log.i("DoctorName", "Name is " + DocName);
        linkLayoutFeatures();
        if (DocName.equals(ParseUser.getCurrentUser().get("username").toString())) {
            initializeID();
            loadDP();
        } else {
            getInformation();

        }
    }

    @SuppressLint("SetTextI18n")
    private void getInformation() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", DocName);
//        query.setLimit(1);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Log.i("User", "FOUND!!!!!!!!!!!");
                        for (ParseUser user : objects) {
                            doc_Name.setText(user.get("fullName").toString());
//                            doc_Phone.setText("Phone: " + user.get("Mobile_Number").toString());
                            doc_Loc.setText("Address: " + user.get("Address").toString());
                            doc_Spec.setText(user.get("Specialisation").toString());
                            loadDP2(user);

                        }

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void linkLayoutFeatures() {
        doc_Name = findViewById(R.id.doc_name);
        doc_Img = findViewById(R.id.doc_img);
        doc_ImgBlur = findViewById(R.id.doc_imgBlur);
        doc_Loc = findViewById(R.id.doc_loc);
//        doc_Phone = findViewById(R.id.doc_num);
        doc_Spec = findViewById(R.id.doc_spec);
    }

    @SuppressLint("SetTextI18n")
    private void initializeID() {
        ParseUser Doctor;
        Doctor = ParseUser.getCurrentUser();
        doc_Name.setText(Doctor.get("username").toString());
//        doc_Phone.setText("Phone: " + Doctor.get("Mobile_Number").toString());
        doc_Loc.setText("Address: " + Doctor.get("Address").toString());
        doc_Spec.setText(Doctor.get("Specialisation").toString());
    }

    public void loadDP() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile profileImage = currentUser.getParseFile("DP");
        profileImage.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // data has the bytes for the resume
                    Bitmap profile_image_bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    doc_Img.setImageBitmap(profile_image_bitmap);
                    doc_ImgBlur.setImageBitmap(profile_image_bitmap);
                } else {
                    // something went wrong
                    Log.i("loadDP", "Problem Encountered");
                }
            }
        });
    }

    public void loadDP2(ParseUser x) {
        ParseUser currentUser = x;
        ParseFile profileImage = currentUser.getParseFile("DP");
        profileImage.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // data has the bytes for the resume
                    Bitmap profile_image_bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    doc_Img.setImageBitmap(profile_image_bitmap);
                    doc_ImgBlur.setImageBitmap(profile_image_bitmap);
                } else {
                    // something went wrong
                    Log.i("loadDP", "Problem Encountered");
                }
            }
        });
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
            Log.i("user:", ParseUser.getCurrentUser().toString());
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

    public void editProfile(View view) {
        Log.i("user:", ParseUser.getCurrentUser().toString());
        if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("patient")) {
            Intent intent = new Intent(getApplicationContext(), PatientProfileEditingActivity.class);
            startActivity(intent);
        } else if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("doctor")) {
            Intent intent = new Intent(getApplicationContext(), DoctorProfileEditActivity.class);
            startActivity(intent);
        }
    }

    public void shareContact(View view) {
        Toast.makeText(DoctorProfile.this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }

    public void backPressed(View view) {
        startActivity(new Intent(DoctorProfile.this, HomeActivityDoctor.class));
        finish(); }
}