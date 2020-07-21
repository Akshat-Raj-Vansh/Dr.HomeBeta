package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentDisplayForDoctors extends AppCompatActivity {
    final ArrayList<String> patientnames = new ArrayList<String>();
    final ArrayList<String> nextDaynames  = new ArrayList<>();
    SharedPreferences sharedPreferences;
    TextView today,nextDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_display_for_doctors);
        setTitle("Your Appointments");
        sharedPreferences = this.getSharedPreferences("Dr.Home local", MODE_PRIVATE);
        today= findViewById(R.id.today);
        nextDay=findViewById(R.id.nextDay);
        Log.i("Activity", "Appointments!!!!");
        final ListView listView = (ListView) findViewById(R.id.appointmentsListView);
        final ListView nextDayList= findViewById(R.id.nextDayList);
        Toast.makeText(this,"Long press to view Patient's profile." ,Toast.LENGTH_LONG).show();
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

                        double current_time =Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':','.'));
                        double slot_starting_time =Double.parseDouble(object.get("Time").toString().substring(0,object.get("Time").toString().indexOf('-')).replace(':','.'));
                        if(object.getString("Day").equals(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date()).toUpperCase())) {
                            today.setText(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date()));
                            today.setVisibility(View.VISIBLE);
                            if (slot_starting_time <= current_time ) {// true to be removed

                                patientnames.add("(" + object.get("Time").toString() + ") " + object.get("Patient").toString() + "*******");
                                //compare your set time with current time and do what ever you want
                                Log.i("Time", String.valueOf(current_time));
                            } else {
                                patientnames.add("(" + object.get("Time").toString() + ") " + object.get("Patient").toString());
                            }
                        }
                        else{
                            nextDay.setText("Future Appointments");
                            nextDay.setVisibility(View.VISIBLE);
                            nextDaynames.add("(" + object.get("Time").toString() + ") " + object.get("Patient").toString()+"          "+object.getString("Day"));

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
                Intent intent =new Intent(getApplicationContext(), PatientProfile.class);
                if(patientnames.get(position).indexOf("*")!=-1)
                    intent.putExtra("Name", " "+patientnames.get(position).substring(patientnames.get(position).indexOf(" ") + 1,patientnames.get(position).indexOf("*")));
                else
                    intent.putExtra("Name"," "+ patientnames.get(position).substring(patientnames.get(position).lastIndexOf(" ")));

                startActivity(intent);

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                double current_time =Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':','.'));
                double slot_starting_time =Double.parseDouble(patientnames.get(position).substring(patientnames.get(position).indexOf('(')+1,patientnames.get(position).indexOf('-')).replace(':','.'));

                if(slot_starting_time<=current_time) {//true to be removed

                    Log.i("Time", "Inside");
                    Intent intent = new Intent(getApplicationContext(), ChatScreen.class);
                    intent.putExtra("activity","AppointDisplayForDoctors");
                    intent.putExtra("username", patientnames.get(position).substring(patientnames.get(position).indexOf(" ") + 1,patientnames.get(position).indexOf("*")));
                    intent.putExtra("Time",patientnames.get(position).substring(patientnames.get(position).indexOf("(") + 1,patientnames.get(position).indexOf(")")));
                    startActivity(intent);
                    //compare your set time with current time and do what ever you want
                    Log.i("Time", String.valueOf(current_time));

                }
                else {
                    Toast.makeText(getApplicationContext(),"Please wait",Toast.LENGTH_LONG).show();
                }


            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.logout_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.profile) {
            if(ParseUser.getCurrentUser().get("patientOrDoctor").equals("patient")){
                Intent intent = new Intent(getApplicationContext(), PatientProfile.class);
                intent.putExtra("Name",ParseUser.getCurrentUser().get("username").toString());
                startActivity(intent);
            }
            else if(ParseUser.getCurrentUser().get("patientOrDoctor").equals("doctor")){
                Intent intent = new Intent(getApplicationContext(), DoctorProfile.class);
                intent.putExtra("Name",ParseUser.getCurrentUser().get("username").toString());
                startActivity(intent);
            }

        } else if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            sharedPreferences.edit().putBoolean("isLoggedIn?", false).apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.oldAppointments) {

            Intent intent = new Intent(getApplicationContext(), OldAppointments.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Back button will take user directly to home window instead of login/signup page
        Intent backToHome = new Intent(Intent.ACTION_MAIN);
        backToHome.addCategory(Intent.CATEGORY_HOME);
        backToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToHome);
    }


    public void backPressed(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }


}


