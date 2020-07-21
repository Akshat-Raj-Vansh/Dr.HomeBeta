
package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends AppCompatActivity {

    String activeUser = "";
    TextView username;
    String time;
    Button button;
    Button back;
    ArrayList<String> messages = new ArrayList<>();
    Handler handler;

    ArrayAdapter arrayAdapter;

    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.msg);

        ParseObject message = new ParseObject("Message");

        final String messageContent = chatEditText.getText().toString();

        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient", activeUser);
        message.put("message", messageContent);

        chatEditText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    messages.add(messageContent);

                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        linkLayoutFeatures();
        Intent intent = getIntent();

        activeUser = intent.getStringExtra("username");
        time = intent.getStringExtra("Time");
        username.setText(activeUser);
        ListView chatListView = (ListView) findViewById(R.id.chatListView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);

        if(getIntent().getStringExtra("activity").equals("OldAppointments")){
            button.setVisibility(View.GONE);
        }

        chatListView.setAdapter(arrayAdapter);

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");

        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient", activeUser);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

        query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        messages.clear();

                        for (ParseObject message : objects) {

                            String messageContent = message.getString("message");

                            if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {

                                messageContent = "> " + messageContent;

                            }

                            Log.i("Info", messageContent);

                            messages.add(messageContent);

                        }

                        arrayAdapter.notifyDataSetChanged();

                    }

                }

            }
        });


        handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");

                query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
                query1.whereEqualTo("recipient", activeUser);

                ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

                query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
                query2.whereEqualTo("sender", activeUser);

                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                queries.add(query1);
                queries.add(query2);

                ParseQuery<ParseObject> query = ParseQuery.or(queries);

                query.orderByAscending("createdAt");

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                            if (objects.size() > 0) {

                                messages.clear();

                                for (ParseObject message : objects) {

                                    String messageContent = message.getString("message");

                                    if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {

                                        messageContent = "> " + messageContent;

                                    }

                                    Log.i("Info", messageContent);

                                    messages.add(messageContent);

                                }

                                arrayAdapter.notifyDataSetChanged();

                            }

                        }

                    }
                });






                handler.postDelayed(this,1000);
            }
        };
        handler.post(run);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void EndSession(View view){

        ParseObject OldAppointments = new ParseObject("OldAppointments");
        OldAppointments.put("user1",ParseUser.getCurrentUser().getUsername().toString());
        OldAppointments.put("user2",activeUser);
        OldAppointments.saveInBackground();



        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("?")
                .setMessage("Do you want to close the session?" )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseQuery<ParseObject> query = new  ParseQuery<ParseObject>("Appointments");
                        if(ParseUser.getCurrentUser().getString("patientOrDoctor").equals("doctor")){
                            query.whereEqualTo("Doctor",ParseUser.getCurrentUser().getUsername());
                            query.whereEqualTo("Patient",activeUser);
                            query.whereEqualTo("Time",time);
                            Log.i("Patient",activeUser);}

                        else
                        {
                            query.whereEqualTo("Patient",ParseUser.getCurrentUser().getUsername());
                            query.whereEqualTo("Doctor",activeUser);
                            query.whereEqualTo("Time",time);
                            Log.i("Doctor",activeUser);
                            Log.i("Time",time);
                        }


                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e==null){
                                    if(objects.size()>0){
                                        objects.get(0).deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e==null)
                                                    Log.i(this.toString(),"Succesfull");
                                                else
                                                    Log.i(this.toString(),e.getMessage());
                                            }
                                        });
                                    }else
                                        Log.i(this.toString(),"ObjectSizeProblem");
                                }
                                else
                                    Log.i(this.toString(),e.getMessage());
                            }
                        });
                        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Doctor");
                        query2.whereEqualTo("DoctorName",ParseUser.getCurrentUser().getString("patientOrDoctor").equals("doctor")?ParseUser.getCurrentUser().getUsername():activeUser);
                        query2.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e==null && objects.size()>0){
                                    JSONArray jsonArray = objects.get(0).getJSONArray("TimeSlot");
                                    for(int i=0;i<jsonArray.length();i++){
                                        try {
                                            String key = jsonArray.getJSONObject(i).keys().next();
                                            if(key.equals(time)){
                                                jsonArray.getJSONObject(i).put(time,"Available");
                                                break;
                                            }
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                    objects.get(0).put("TimeSlot",jsonArray);
                                    objects.get(0).saveInBackground();
                                }
                            }
                        });
                        Intent intent;
                        if(ParseUser.getCurrentUser().getString("patientOrDoctor").equals("doctor")) {
                            Log.i(this.toString(),"Inside");
                            intent = new Intent(getApplicationContext(), HomeActivityDoctor.class);
                            startActivity(intent);
                        }
                        else
                        {
                            intent = new Intent(getApplicationContext(), HomeActivityPatient.class);
                            startActivity(intent);
                        }
                    }
                })
                .show();



    }

    private void linkLayoutFeatures() {
        username = findViewById(R.id.username);
        button= findViewById(R.id.endSessionButton);
    }


}