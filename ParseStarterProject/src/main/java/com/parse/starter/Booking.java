package com.parse.starter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.Adapters.BookingAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Booking extends AppCompatActivity {
    static String doctor;
    public static int pos;
    static Date today;
    static Date tomorrow;
    public static boolean onCurrentDay;
    static String choosenDate;
    Bitmap image;
    LoadingScreen loadingScreen;
    static ArrayList<String> slots;
    static ArrayList<String> avail;
    static ArrayList<String> Actualavail;
    ArrayList<String> timeavail;
    static DateFormat date;
    static JSONArray jsonArray, jsonArray2;
    static ParseQuery<ParseObject> query;
    static ParseObject appointments;
    RecyclerView bookingRecyclerView;
    static RecyclerView.Adapter bookingAdapter;
    TextView Date, docname;
    ImageView docImage;
    DatePicker datePicker;
    RecyclerView.LayoutManager layoutManager;
    CardView datecard;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_edit);
        createNotificationChannel();
        loadingScreen = new LoadingScreen(this);
        loadingScreen.startloadingScreen();
        avail = new ArrayList<>();
        Actualavail = new ArrayList<>();
        docname = findViewById(R.id.doctorName);
        docImage = findViewById(R.id.doctorImage);
        datePicker = findViewById(R.id.datepicker);
        datecard = findViewById(R.id.date_card);
        datecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDate();
            }
        });
        timeavail = new ArrayList<>();
        doctor = getIntent().getStringExtra("Doctor");
        docname.setText(doctor);
        getDocImage();
        bookingRecyclerView = findViewById(R.id.BookingRecyclerView);
        query = new ParseQuery<ParseObject>("Doctor");
        slots = new ArrayList<>();
        setTitle("Book Appointment");
        Toast.makeText(this, "Scroll down if there are more slots", Toast.LENGTH_LONG);
        callRecyclerView();
        Date = findViewById(R.id.date);
        getDate();
        try {
            getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }




    }

    public void getDocImage() {
        ParseQuery<ParseUser> queryU = ParseUser.getQuery();
        queryU.whereEqualTo("username", doctor);
        queryU.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    objects.get(0).getParseFile("DP").getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null && data != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                docImage.setImageBitmap(bitmap);
                                Log.i(this.toString(), "Success");
                            } else
                                Log.i(this.toString(), e.getMessage());
                        }
                    });
                } else
                    Log.i(this.toString(), e.getMessage());
            }
        });

    }
    public void getDocSlots() throws ParseException {
        avail.removeAll(avail);
        avail.addAll(Actualavail);
        ParseQuery<ParseObject> slotquery = new ParseQuery<>("Appointments");
        slotquery.whereEqualTo("Doctor",doctor);
        slotquery.whereEqualTo("Day",Date.getText());
        List<ParseObject> objects= slotquery.find();
        if(objects.size()>0){
            for(ParseObject object : objects)
                avail.set(slots.indexOf(object.get("Time").toString()),"Booked");}


        onCurrentDay = Date.getText().equals(date.format(today).toUpperCase());
        Log.i("currentDay", (String) Date.getText());
        bookingAdapter.notifyDataSetChanged();
        loadingScreen.stoploadingScreen();

    }
//    public void getTimeslots(){
//        query = ParseUser.getQuery();
//        Log.i("Name",doctor);
//        query.whereEqualTo("username",doctor);
//        query.findInBackground(new FindCallback<ParseUser>() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void done(List<ParseUser> objects, ParseException e) {
//                if(e==null && objects.size()>0){
//                    givenTime = objects.get(0).getJSONArray("Time");
//                    Log.i("Time",givenTime.toString());
//                    for(int i=0;i<givenTime.length();i++){
//                        double start;
//                        double end;
//                        try{
//                            start = Double.parseDouble(givenTime.getString(i).substring(0,givenTime.getString(i).indexOf("-")));
//                            end = Double.parseDouble(givenTime.getString(i).substring(givenTime.getString(i).indexOf("-")+1));
////                                 Log.i("Time",givenTime.getString(i).substring(0,givenTime.getString(i).indexOf("-"))+" "+givenTime.getString(i).substring(givenTime.getString(i).indexOf("-")+1));
//                            DateFormat df = new SimpleDateFormat("HH:mm");
//                            Calendar cal = Calendar.getInstance();
//                            cal.set(Calendar.HOUR_OF_DAY, (int)start);
//                            cal.set(Calendar.MINUTE, (int)(start-(int)start)*100);
//
//                            String slot="";
//                            while (cal.get(Calendar.HOUR_OF_DAY)<(int)end) {
//
//                                slot+=df.format(cal.getTime())+"-";
//                                cal.add(Calendar.MINUTE, 30);
//                                slot+=df.format(cal.getTime());
//                                timeslots.add(slot);
//
//                                slot="";
//                            }
//                            double check = end-(int)end;
//                            if(check!=0){
//                                slot+=df.format(cal.getTime())+"-";
//                                cal.add(Calendar.MINUTE, 30);
//                                slot+=df.format(cal.getTime());
//                                timeslots.add(slot);
//
//                            }
//
//                        }
//                        catch (Exception error){
//                            Log.i("Error",error.getMessage());
//                        }
//                    }
//
//
//                }
//                else{
//                    Log.i("Error",e.getMessage());
//                }
//                Log.i("Size",Integer.toString(timeslots.size()));
//
//                bookingAdapter.notifyDataSetChanged();
//            }
//
//        });
//
//
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getDate() {
        Calendar calendar = Calendar.getInstance();
        today = calendar.getTime();
        date = new SimpleDateFormat("dd MMMM", Locale.getDefault());
    }

    public void getData() throws ParseException {
        query.whereEqualTo("DoctorName", doctor);
        List<ParseObject> objects = query.find();
        if(objects.size()>0)
            jsonArray = objects.get(0).getJSONArray("TimeSlot");
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                slots.add(obj.keys().next());
                Actualavail.add((String) obj.get(slots.get(i)));
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        double current_time = Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':', '.'));
        Log.i("Current time",Double.toString(current_time));
        for (int i = 0; i < slots.size(); i++) {
            if (current_time > Double.parseDouble(slots.get(i).substring(0, slots.get(i).indexOf('-')).replace(':', '.')) && current_time <24.00)
                timeavail.add("Unavailable");
            else
                timeavail.add("Available");





//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if (e == null && objects.size() > 0)
//                    jsonArray = objects.get(0).getJSONArray("TimeSlotsFinal");
//                else
//                    Log.i("Error", e.getMessage());
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    try {
//                        JSONObject obj = (JSONObject) jsonArray.get(i);
//                        slots.add(obj.keys().next());
//                        Actualavail.add((String) obj.get(slots.get(i)));
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                double current_time = Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':', '.'));
//                for (int i = 0; i < slots.size(); i++) {
//                    if (current_time > Double.parseDouble(slots.get(i).substring(0, slots.get(i).indexOf('-')).replace(':', '.')) && current_time < Double.parseDouble(slots.get(slots.size() - 1).substring(0, slots.get(slots.size() - 1).indexOf('-')).replace(':', '.')))
//                        timeavail.add("Unavailable");
//                    else
//                        timeavail.add("Available");
//                    String day = (current_time < Double.parseDouble(slots.get(slots.size() - 1).substring(0, slots.get(slots.size() - 1).indexOf('-')).replace(':', '.')) ? "Today" : "Tommorow");
//                    if (day.equals("Today"))
//                        Date.setText(date.format(today).toUpperCase());
//                    else
//                        Date.setText(date.format(tomorrow).toUpperCase());
//
//                    getDocSlots();
//                }
//
//
//            }
//        });
        }
        Date.setText(date.format(today).toUpperCase());
        getDocSlots();


    }

    public void getData2(final String day) {
        slots.clear();
        avail.clear();
        query.whereEqualTo("DoctorName", doctor);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0)
                    jsonArray2 = objects.get(0).getJSONArray(day);
                else
                    Log.i("Error", e.getMessage());
                for (int i = 0; i < jsonArray2.length(); i++) {
                    try {
                        JSONObject obj = (JSONObject) jsonArray2.get(i);
                        slots.add(obj.keys().next());
                        avail.add((String) obj.get(slots.get(i)));
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                double current_time = Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':', '.'));
                for (int i = 0; i < slots.size(); i++) {
                    if (current_time > Double.parseDouble(slots.get(i).substring(0, slots.get(i).indexOf('-')).replace(':', '.')) && current_time < Double.parseDouble(slots.get(slots.size() - 1).substring(0, slots.get(slots.size() - 1).indexOf('-')).replace(':', '.')))
                        timeavail.add("Unavailable");
                    else
                        timeavail.add("Available");
                    String day = (current_time < Double.parseDouble(slots.get(slots.size() - 1).substring(0, slots.get(slots.size() - 1).indexOf('-')).replace(':', '.')) ? "Today" : "Tommorow");
                    if (day.equals("Today"))
                        Date.setText(date.format(today).toUpperCase());

                }
                bookingAdapter.notifyDataSetChanged();
                loadingScreen.stoploadingScreen();
            }
        });


    }

    public void callRecyclerView() {

        bookingAdapter = new BookingAdapter(this, slots, avail, timeavail);
        //,this);
        layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        bookingRecyclerView.setLayoutManager(layoutManager);
        bookingRecyclerView.setAdapter(bookingAdapter);

    }

    public void book(View view) throws JSONException {
        Log.i("Slot", slots.get(pos));
        final double current_time = Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':', '.'));
        double slotTime = Double.parseDouble(slots.get(pos).substring(0, slots.get(pos).indexOf('-')).replace(':', '.')); // jo time compare krna ho
        if (!timeavail.get(pos).equals("Unavailable") && current_time >= slotTime && current_time < Double.parseDouble(slots.get(slots.size() - 1).substring(0, slots.get(slots.size() - 1).indexOf('-')).replace(':', '.'))) {
            timeavail.add(pos, "Unavailable");
            Log.i("Position", String.valueOf(pos));
            bookingAdapter.notifyDataSetChanged();
        }
        Log.i("Avail", avail.toString());
        if (avail.get(pos).equals("Booked") || (timeavail.get(pos).equals("Unavailable")&&onCurrentDay)) {
            Log.i("avail", avail.get(pos));
            Toast.makeText(this, "Book another session", Toast.LENGTH_SHORT).show();

        } else {
            final String day = (current_time < Double.parseDouble(slots.get(slots.size() - 1).substring(0, slots.get(slots.size() - 1).indexOf('-')).replace(':', '.')) ? "Today" : "Tommorow");
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Are Sure?")
                    .setMessage("You will be booking a session at " + slots.get(pos) + " on " + Date.getText())
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                            intent.putExtra("docname", getIntent().getStringExtra("Doctor"));
                            intent.putExtra("Time",slots.get(pos));
                            intent.putExtra("Day",Date.getText());
                            startActivity(intent);

//                            avail.set(pos,"Booked");
//                            Log.i("Position", String.valueOf(pos));
//                            Log.i("Avail",avail.toString());
//                            bookingAdapter.notifyDataSetChanged();
//                            appointments = new ParseObject("Appointments");
//                            appointments.put("Doctor",doctor);
//                            appointments.put("Patient",ParseUser.getCurrentUser().getUsername());
//                            appointments.put("Time",slots.get(pos));
//                            appointments.put("Day",Date.getText());
//                            appointments.saveInBackground();

//                        createNotifications(slots.get(pos),day);
//                        avail.set(pos,"Booked");
//                        Log.i("Position", String.valueOf(pos));
//                        Log.i("Avail",avail.toString());
//                        bookingAdapter.notifyDataSetChanged();
//                        try {
//                           jsonArray.getJSONObject(pos).put(slots.get(pos),"Booked");
//                            query.whereEqualTo("DoctorName",doctor);
//                            query.findInBackground(new FindCallback<ParseObject>() {
//                                @Override
//                                public void done(List<ParseObject> objects, ParseException e) {
//                                    if(e==null && objects.size()>0){
//                                        objects.get(0).put("TimeSlot",jsonArray);
//                                        objects.get(0).saveInBackground();
//                                    }
//                                    else
//                                        Log.i(this.toString(),e.getMessage());
//                                }
//                            });
//                            appointments = new ParseObject("Appointments");
//                            appointments.put("Doctor",doctor);
//                            appointments.put("Patient",ParseUser.getCurrentUser().getUsername());
//                            appointments.put("Time",slots.get(pos));
//                            appointments.put("Day",date.format(day.equals("Today")?today:tomorrow));
//                            appointments.saveInBackground();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        }
                    }).show();

        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "AppointmentsChannel";
            String descrition = "Remainder for Appointments";
            int imp = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("AppointmentTime", name, imp);
            channel.setDescription(descrition);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createNotifications(String time, String day) {

        Log.i(this.toString(), "InsideNotification");
        Intent intent = new Intent(getApplicationContext(), NotificationBroadCaster.class);
        intent.putExtra("Doctor", doctor);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.indexOf('-')));
        if (minute == 0) {
            hour -= 1;
            minute = 50;
        } else
            minute -= 10;

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        long timeInMillis = calendar.getTimeInMillis();
        if (!day.equals("Today")) {
            timeInMillis += 86400000;
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

    }

    public void backPressed(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivityPatient.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void changeDate() {
        LayoutInflater layoutInflater = LayoutInflater.from(Booking.this);
        View promptsView = layoutInflater.inflate(R.layout.activity_booking_calendar, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Booking.this);
        alertDialogBuilder.setView(promptsView);
        datePicker = promptsView.findViewById(R.id.datepicker);
        Calendar calendar = Calendar.getInstance();
        datePicker.setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        datePicker.setMaxDate(calendar.getTimeInMillis());
        alertDialogBuilder.setCancelable(false).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(DialogInterface dialog, int id) {
                //  Toast.makeText(getApplicationContext(), "Working", Toast.LENGTH_SHORT).show();
                choosenDate = datePicker.getDayOfMonth() + " " + getMonth(datePicker.getMonth()+1);
                Date.setText(choosenDate.toUpperCase());
                try {
                    getDocSlots();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //getData2(getDay());
            }
        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getDay() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mon-yyyy");
            Date date1 = simpleDateFormat.parse(calendar.getTime().toString());
            Date date2 = simpleDateFormat.parse(datePicker.getDayOfMonth() + "-" + datePicker.getMonth() + "-" + datePicker.getYear());
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long difftDays = difference / (24 * 60 * 60 * 1000);
            Log.i("TIME", String.valueOf(difftDays));
            return "TimeSlotDay" + String.valueOf(difftDays);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "TimeSlotDay2";
    }

    public String getMonth(int i) {
        String month = "";
        switch (i) {
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
        }
        return month.toUpperCase();
    }
}
