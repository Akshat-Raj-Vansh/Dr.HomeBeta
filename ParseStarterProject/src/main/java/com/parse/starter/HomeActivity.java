package com.parse.starter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.Adapters.SpecialityAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<String> speciality;
    HashMap<String, Bitmap> imageMap;
    ArrayList<Bitmap> image;
    RecyclerView recyclerViewspecialities;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    LoadingScreen loadingScreen;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    TextView subName, subEmail;
    CircularImageView profilePhoto;
    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Activity", "USERLIST ACTIVITY !!!!!!");
        setTitle("Doc Home");
        sharedPreferences = this.getSharedPreferences("Dr.Home local", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isLoggedIn?",true).apply();
        Log.d("SharePreferences", String.valueOf(sharedPreferences.getBoolean("isLoggedIn?", false)));
        loadingScreen = new LoadingScreen(this);
        loadingScreen.startloadingScreen();
        setContentView(R.layout.home_screen_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        recyclerViewspecialities = findViewById(R.id.RecyclerView_Specialities);
        speciality = new ArrayList<>();
        image = new ArrayList<Bitmap>();
        imageMap = new HashMap<>();
        callRecyclerview();
        //  addSpecialities();
        addImagesAndSpecialities();
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //sets the default opening screen of attendance in home screen
        navigationView.setCheckedItem(R.id.home);
        subName = headerView.findViewById(R.id.all_sub_username);
        subEmail = headerView.findViewById(R.id.all_sub_email);
        profilePhoto = headerView.findViewById(R.id.profilePhoto);
        subName.setText(ParseUser.getCurrentUser().getString("fullName"));
        subEmail.setText(ParseUser.getCurrentUser().getEmail());
        try {
            loadDP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDP() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String userType = currentUser.getString("patientOrDoctor");
        if(userType.equals("Doctor")){
            final ParseFile profileImage = currentUser.getParseFile("DP");
            profileImage.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // data has the bytes for the resume
                        Bitmap profile_image_bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profilePhoto.setImageBitmap(profile_image_bitmap);
                    } else {
                        // something went wrong
                        Log.i("loadDP", "Problem Encountered");
                    }
                }
            });
        }
        else
            profilePhoto.setImageResource(R.drawable.login);
    }

    public void addImagesAndSpecialities() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("SpecialisationsImages");

        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    int counterr = 0;
                    for (ParseObject object : objects) {
                        counterr++;
                        speciality.add(object.getString("Specialisation"));
                        Log.i("Spec.", "ADDED!!");
                        final ParseFile file = (ParseFile) object.getParseFile("Specialisation_Image");

                        final int finalCounterr = counterr;
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    imageMap.put(file.getName().substring(file.getName().indexOf('_') + 1, file.getName().indexOf('.')), bitmap);

                                    Log.i("IMAGE", "LOADED " + file.getName().substring(file.getName().indexOf('_') + 1, file.getName().indexOf('.')));
                                } else {
                                    Log.i("PROBLEM", "IS HERE!!!!!");
                                    e.printStackTrace();
                                }
                                Log.i("print", Integer.toString(finalCounterr));
                                Log.i("Sizes", Integer.toString(imageMap.size()) + "/" + Integer.toString(speciality.size()));
                                if (imageMap.size() == speciality.size()) {
                                    for (String name : speciality) {
                                        image.add(imageMap.get(name));
                                    }
                                    loadingScreen.stoploadingScreen();
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });
        Log.i("reached out of querry", "yes!");
        return;
    }


    void callRecyclerview() {
        adapter = new SpecialityAdapter(this, speciality, image);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //  layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerViewspecialities.setLayoutManager(layoutManager);
        recyclerViewspecialities.setAdapter(adapter);
        Log.i("adapter", "Adapter is set!!!");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //(findViewById(R.id.nav_all_subjects)).setClickable(false);
        if (id == R.id.nav_profile) {
            Toast.makeText(HomeActivity.this, "Profile Button Clicked", Toast.LENGTH_SHORT).show();
            if (ParseUser.getCurrentUser().get("patientOrDoctor").equals("patient")) {
                Intent intent = new Intent(getApplicationContext(), PatientProfile.class);
                intent.putExtra("Name", ParseUser.getCurrentUser().get("username").toString());
                startActivity(intent);
            } else if (ParseUser.getCurrentUser().get("patientOrDoctor").equals("doctor")) {
                Intent intent = new Intent(getApplicationContext(), DoctorProfile.class);
                intent.putExtra("Name", ParseUser.getCurrentUser().get("username").toString());
                startActivity(intent);
            }
        } else if (id == R.id.nav_appointments) {
            Intent intent = new Intent(getApplicationContext(), AppointmentDisplayForPatients.class);
            intent.putExtra("Name", ParseUser.getCurrentUser().get("username").toString());
            startActivity(intent);
        } else if (id == R.id.nav_previousappointments) {
            Intent intent = new Intent(getApplicationContext(), OldAppointments.class);
            intent.putExtra("Name", ParseUser.getCurrentUser().get("username").toString());
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Log Out")
                    .setMessage("Do you want to log out?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logOut();
                        }
                    }).setNegativeButton("no", null).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        recyclerViewspecialities.setClickable(true);
        return true;
    }

    private void logOut() {
        ParseUser.logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void openDrawer(View view) {
        recyclerViewspecialities.setClickable(false);
        drawer.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        //Back button will take user directly to home window instead of login/signup page
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            recyclerViewspecialities.setClickable(true);
        }
        Intent backToHome = new Intent(Intent.ACTION_MAIN);
        backToHome.addCategory(Intent.CATEGORY_HOME);
        backToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToHome);
    }
}
