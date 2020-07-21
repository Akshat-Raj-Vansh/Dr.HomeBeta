package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivityDoctor extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LoadingScreen loadingScreen;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    TextView subName, subEmail;
    CircularImageView profilePhoto;
    SharedPreferences sharedPreferencesLogIn, sharedPreferences;
    final ArrayList<String> patientnames = new ArrayList<String>();
    final ArrayList<String> nextDaynames = new ArrayList<>();
    TextView today, nextDay;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sharedPreferences = this.getSharedPreferences("Dr.Home local", MODE_PRIVATE);
//        sharedPreferences.edit().putBoolean("isLoggedIn?",true).apply();
//        Log.d("SharePreferences", String.valueOf(sharedPreferences.getBoolean("isLoggedIn?", false)));
       // loadingScreen = new LoadingScreen(this);
       // loadingScreen.startloadingScreen();

        setContentView(R.layout.home_screen_doctor_main);
        sharedPreferences = this.getSharedPreferences("Dr.Home local", MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        today = (TextView)findViewById(R.id.today);
        nextDay = (TextView)findViewById(R.id.nextDay); toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //sets the default opening screen of attendance in home screen
        navigationView.setCheckedItem(R.id.nav_appointments);
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
        appointmentsWindow();
    }

    private void appointmentsWindow() {
        final ListView listView = (ListView) findViewById(R.id.appointmentsListView);
        final ListView nextDayList = findViewById(R.id.nextDayList);
        Toast.makeText(this, "Long press to view Patient's profile.", Toast.LENGTH_LONG).show();
        today.setVisibility(View.GONE);
        nextDay.setVisibility(View.GONE);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, patientnames);
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nextDaynames);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Appointments");
        query.whereEqualTo("Doctor", ParseUser.getCurrentUser().getUsername().toString());

        query.addAscendingOrder("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {

                        double current_time = Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':', '.'));
                        double slot_starting_time = Double.parseDouble(object.get("Time").toString().substring(0, object.get("Time").toString().indexOf('-')).replace(':', '.'));
                        if (object.getString("Day").equals(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date()).toUpperCase())) {
                            today.setText(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date()));
                            today.setVisibility(View.VISIBLE);
                            if (slot_starting_time <= current_time) {// true to be removed

                                patientnames.add("(" + object.get("Time").toString() + ") " + object.get("Patient").toString() + "*******");
                                //compare your set time with current time and do what ever you want
                                Log.i("Time", String.valueOf(current_time));
                            } else {
                                patientnames.add("(" + object.get("Time").toString() + ") " + object.get("Patient").toString());
                            }
                        } else {
                            nextDay.setText("Future Appointments");
                            nextDay.setVisibility(View.VISIBLE);
                            nextDaynames.add("(" + object.get("Time").toString() + ") " + object.get("Patient").toString() + "          " + object.getString("Day"));

                        }

                    }
                    listView.setAdapter(arrayAdapter);
                    nextDayList.setAdapter(arrayAdapter1);
                }

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PatientProfile.class);
                if (patientnames.get(position).indexOf("*") != -1)
                    intent.putExtra("Name", " " + patientnames.get(position).substring(patientnames.get(position).indexOf(" ") + 1, patientnames.get(position).indexOf("*")));
                else
                    intent.putExtra("Name", " " + patientnames.get(position).substring(patientnames.get(position).lastIndexOf(" ")));

                startActivity(intent);

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                double current_time = Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':', '.'));
                double slot_starting_time = Double.parseDouble(patientnames.get(position).substring(patientnames.get(position).indexOf('(') + 1, patientnames.get(position).indexOf('-')).replace(':', '.'));

                if (slot_starting_time <= current_time) {//true to be removed

                    Log.i("Time", "Inside");
                    Intent intent = new Intent(getApplicationContext(), ChatScreen.class);
                    intent.putExtra("activity", "AppointDisplayForDoctors");
                    intent.putExtra("username", patientnames.get(position).substring(patientnames.get(position).indexOf(" ") + 1, patientnames.get(position).indexOf("*")));
                    intent.putExtra("Time", patientnames.get(position).substring(patientnames.get(position).indexOf("(") + 1, patientnames.get(position).indexOf(")")));
                    startActivity(intent);
                    //compare your set time with current time and do what ever you want
                    Log.i("Time", String.valueOf(current_time));

                } else {
                    Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void loadDP() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String userType = currentUser.getString("patientOrDoctor");
        if (userType.equals("Doctor")) {
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
        } else
            profilePhoto.setImageResource(R.drawable.login);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            Toast.makeText(HomeActivityDoctor.this, "Profile Button Clicked", Toast.LENGTH_SHORT).show();
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
        return true;
    }

    private void logOut() {
        ParseUser.logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void openDrawer(View view) {
        drawer.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        //Back button will take user directly to home window instead of login/signup page
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        Intent backToHome = new Intent(Intent.ACTION_MAIN);
        backToHome.addCategory(Intent.CATEGORY_HOME);
        backToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToHome);
    }
}