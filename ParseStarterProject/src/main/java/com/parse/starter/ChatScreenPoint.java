package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.Adapters.MessageAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ChatScreenPoint extends AppCompatActivity {

    //username
    //profile
    //endSessionButton
    String activeUser = "";
    TextView username;
    String time;
    Button button;
    Button back;
    ArrayList<String> messages = new ArrayList<>();
    //  ArrayList<Bitmap> images = new ArrayList<>();
    Handler handler;
    RecyclerView recyclerViewMessages;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager1;


    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.msg);
        if(!chatEditText.getText().equals(null)) {
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
                        //        images.add(null);
                        adapter.notifyDataSetChanged();
                    }
                    recyclerViewMessages.scrollToPosition(messages.size() - 1);
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen_point);
        username = findViewById(R.id.username);
        button = findViewById(R.id.endSessionButton);
        recyclerViewMessages = findViewById(R.id.RecyclerView_chat);
        linkLayoutFeatures();
        Intent intent = getIntent();

        activeUser = intent.getStringExtra("username");
        time = intent.getStringExtra("Time");
        username.setText(activeUser);

        callRecyclerview();

        if (getIntent().getStringExtra("activity").equals("OldAppointments")) {
            button.setVisibility(View.GONE);
        }


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

                                messageContent = "swayam>" + messageContent;

                            }

                            Log.i("Info", messageContent);

                            messages.add(messageContent);
                            //        images.add(null);

                        }

                        adapter.notifyDataSetChanged();


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

                                        messageContent = "swayam>" + messageContent;

                                    }

                                    Log.i("Info", messageContent);

                                    messages.add(messageContent);
                                    //                     images.add(null);

                                }

                                adapter.notifyDataSetChanged();


                            }

                        }

                    }
                });


                handler.postDelayed(this, 1000);
            }
        };
        handler.post(run);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void EndSession(View view) {

        ParseObject OldAppointments = new ParseObject("OldAppointments");
        OldAppointments.put("user1", ParseUser.getCurrentUser().getUsername().toString());
        OldAppointments.put("user2", activeUser);
        OldAppointments.saveInBackground();


        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("?")
                .setMessage("Do you want to close the session?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"You may view this chat from your \"Previous appointments\" tab now",Toast.LENGTH_LONG).show();
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Appointments");
                        if (ParseUser.getCurrentUser().getString("patientOrDoctor").equals("doctor")) {
                            query.whereEqualTo("Doctor", ParseUser.getCurrentUser().getUsername());
                            query.whereEqualTo("Patient", activeUser);
                            query.whereEqualTo("Time", time);
                            Log.i("Patient", activeUser);
                        } else {
                            query.whereEqualTo("Patient", ParseUser.getCurrentUser().getUsername());
                            query.whereEqualTo("Doctor", activeUser);
                            query.whereEqualTo("Time", time);
                            Log.i("Doctor", activeUser);
                            Log.i("Time", time);
                        }


                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        objects.get(0).deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null)
                                                    Log.i(this.toString(), "Succesfull");
                                                else
                                                    Log.i(this.toString(), e.getMessage());
                                            }
                                        });
                                    } else
                                        Log.i(this.toString(), "ObjectSizeProblem");
                                } else
                                    Log.i(this.toString(), e.getMessage());
                            }
                        });
                        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Doctor");
                        query2.whereEqualTo("DoctorName", ParseUser.getCurrentUser().getString("patientOrDoctor").equals("doctor") ? ParseUser.getCurrentUser().getUsername() : activeUser);
                        query2.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null && objects.size() > 0) {
                                    JSONArray jsonArray = objects.get(0).getJSONArray("TimeSlot");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            String key = jsonArray.getJSONObject(i).keys().next();
                                            if (key.equals(time)) {
                                                jsonArray.getJSONObject(i).put(time, "Available");
                                                break;
                                            }
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                    objects.get(0).put("TimeSlot", jsonArray);
                                    objects.get(0).saveInBackground();
                                }
                            }
                        });
                        Intent intent;
                        if (ParseUser.getCurrentUser().getString("patientOrDoctor").equals("doctor")) {
                            Log.i(this.toString(), "Inside");
                            intent = new Intent(getApplicationContext(), HomeActivityDoctor.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(getApplicationContext(), HomeActivityPatient.class);
                            startActivity(intent);
                        }
                    }
                })
                .show();


    }

    private void linkLayoutFeatures() {
        username = findViewById(R.id.username);
        button = findViewById(R.id.endSessionButton);
        recyclerViewMessages = findViewById(R.id.RecyclerView_chat);
    }

    void callRecyclerview() {
        adapter = new MessageAdapter(this, messages);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        Log.i("LAyoutmanager","SET!!");
        //  layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(adapter);
        Log.i("adapter", "Adapter is set!!!");
    }
}