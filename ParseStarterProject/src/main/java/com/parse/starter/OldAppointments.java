package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class OldAppointments extends AppCompatActivity {
    final ArrayList<String> patientnames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_appointments);
        setTitle("Old Appointments");

        final ListView listView = (ListView) findViewById(R.id.appointmentsListView);
        Toast.makeText(this, "Long press to view profile.", Toast.LENGTH_LONG).show();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, patientnames);
        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("OldAppointments");

        query1.whereEqualTo("user1", ParseUser.getCurrentUser().getUsername());

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("OldAppointments");

        query2.whereEqualTo("user2", ParseUser.getCurrentUser().getUsername());

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        if (object.get("user1").toString().equals(ParseUser.getCurrentUser().getUsername().toString())) {
                            if (!patientnames.contains(object.get("user2").toString()))
                                patientnames.add(object.get("user2").toString());
                        } else {
                            if (!patientnames.contains(object.get("user2").toString()))
                                patientnames.add(object.get("user1").toString());
                        }
                        //compare your set time with current time and do what ever you want


                    }
                    listView.setAdapter(arrayAdapter);
                }

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                if (ParseUser.getCurrentUser().get("patientOrDoctor").toString().equals("patient")) {
                    intent = new Intent(getApplicationContext(), DoctorProfile.class);
                } else
                    intent = new Intent(getApplicationContext(), PatientProfile.class);

                intent.putExtra("Name", patientnames.get(position).toString());

                startActivity(intent);


                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                Intent intent = new Intent(getApplicationContext(), ChatScreenPoint.class);
                intent.putExtra("activity", "OldAppointments");
                intent.putExtra("username", patientnames.get(position));

                startActivity(intent);
                //compare your set time with current time and do what ever you want


            }
        });
    }

    @Override
    public void onBackPressed() {
        if(ParseUser.getCurrentUser().get("patientOrDoctor").equals("patient"))
            startActivity(new Intent(OldAppointments.this, HomeActivityPatient.class));
        else
            startActivity(new Intent(OldAppointments.this, HomeActivityDoctor.class));
        super.onBackPressed();
    }
}