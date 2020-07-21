package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class AppointmentDisplayForPatients extends AppCompatActivity {
    ListView listView;
    ListView nextlistView;
    String[] receivername = {""};
    TextView today, nextDay;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_display_for_patients);
        setTitle("Your Appointments");
        Log.i("Activity", "Appointments!!!!");
        listView = (ListView) findViewById(R.id.appointmentsListView);
        today = findViewById(R.id.today);
        nextDay = findViewById(R.id.nextDay);
        nextlistView = findViewById(R.id.nextdayListView);
        final ArrayList<String> doctornamesToday = new ArrayList<String>();
        final ArrayList<String> doctornamesnextDay = new ArrayList<>();
        today.setVisibility(View.GONE);
        nextDay.setVisibility(View.GONE);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctornamesToday);
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctornamesnextDay);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Appointments");
        query.whereEqualTo("Patient", ParseUser.getCurrentUser().getUsername().toString());
//        query.whereEqualTo("Day",new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date()));
        query.addDescendingOrder("createdAt");


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (final ParseObject object : objects) {


                        double current_time = Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':', '.'));
                        double slot_starting_time = Double.parseDouble(object.get("Time").toString().substring(0, object.get("Time").toString().indexOf('-')).replace(':', '.'));
                        if (object.getString("Day").equals(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date()).toUpperCase())) {
                            Log.i("Appointment","Inside");
                            today.setText(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date()));
                            today.setVisibility(View.VISIBLE);
                            if (slot_starting_time <= current_time) {// true to be removed

                                doctornamesToday.add("(" + object.get("Time").toString() + ") " + object.get("Doctor").toString() + "*******");
                                //compare your set time with current time and do what ever you want
                                Log.i("Time", String.valueOf(current_time));
                            } else {
                                doctornamesToday.add("(" + object.get("Time").toString() + ") " + object.get("Doctor").toString());
                            }
                        } else {
                            doctornamesnextDay.add("(" + object.get("Time").toString() + ") " + object.get("Doctor").toString()+"              "+object.getString("Day"));

                            nextDay.setText("Future Appointments");
                            nextDay.setVisibility(View.VISIBLE);
                        }


                        receivername[0] = object.get("Doctor").toString();
//                        Log.i("Names",user.getUsername());

                    }
                    nextlistView.setAdapter(arrayAdapter1);
                    listView.setAdapter(arrayAdapter);

                }


//        //getImages(); not used now

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                double current_time = Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':', '.'));
                double slot_starting_time = Double.parseDouble(doctornamesToday.get(position).substring(doctornamesToday.get(position).indexOf('(') + 1, doctornamesToday.get(position).indexOf('-')).replace(':', '.'));

                if (slot_starting_time <= current_time) {//true to be removed

                    Log.i("Time", "Inside");
                    Intent intent = new Intent(getApplicationContext(), ChatScreenPoint.class);
                    intent.putExtra("activity", "AppointDisplayForPatients");
                    intent.putExtra("username", doctornamesToday.get(position).substring(doctornamesToday.get(position).indexOf(" ") + 1, doctornamesToday.get(position).indexOf("*")));
                    intent.putExtra("Time", doctornamesToday.get(position).substring(doctornamesToday.get(position).indexOf("(") + 1, doctornamesToday.get(position).indexOf(")")));

                    startActivity(intent);
                    //compare your set time with current time and do what ever you want
                    Log.i("Time", String.valueOf(current_time));

                } else {
                    Toast.makeText(getApplicationContext(), "Please wait for your turn!", Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
    }

    public void backPressed(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
        finish();
    }
}
