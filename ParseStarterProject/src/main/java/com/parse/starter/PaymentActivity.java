package com.parse.starter;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.parse.starter.Booking.avail;
import static com.parse.starter.Booking.slots;

public class PaymentActivity extends AppCompatActivity {


    private String UPI = "";
    TextView amountEt;
    TextView nameEt;
    Button send;
    String price="";
    final int UPI_PAYMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initializeViews();
        ParseQuery<ParseUser> queryz = ParseUser.getQuery();
        queryz.whereEqualTo("username", Booking.doctor);
        queryz.setLimit(1);

        queryz.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Log.i("User", "FOUND!!!!!!!!!!!");
                        for (ParseUser user : objects) {
                            price=user.get("PricePerSession").toString();
                            amountEt.setText(user.get("PricePerSession").toString());

                        }

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        nameEt.setText(ParseUser.getCurrentUser().getUsername());
        getID();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Payment 1", "onClick reached");
                String amount = amountEt.getText().toString();
                Toast.makeText(PaymentActivity.this, UPI, Toast.LENGTH_SHORT).show();
                Log.i("Payment 2", UPI);
                payUsingUpi(amount, UPI, ParseUser.getCurrentUser().getUsername(), "Payment to iSmriti");
            }
        });
    }

    private void getID() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Payments");
        query.whereEqualTo("Company", "iSmriti");
        query.addAscendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        UPI = object.getString("UPI");
                        Log.i("No of objects", String.valueOf(objects.size()));
                        Log.i("Payment Inside", UPI);
                        break;
                    }
                }
            }
        });
        Log.i("Payment Outside", UPI);
    }

    void initializeViews() {
        send = findViewById(R.id.send);
        amountEt = findViewById(R.id.amount_et);
        nameEt = findViewById(R.id.name);
    }

    void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(PaymentActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        PaymentActivityDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        PaymentActivityDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    PaymentActivityDataOperation(dataList);
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void PaymentActivityDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(PaymentActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "PaymentActivityDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                double current_time =Double.parseDouble(new SimpleDateFormat("HH:mm").format(new Date()).replace(':','.'));
                //
                final String day = (current_time<Double.parseDouble(slots.get(slots.size()-1).substring(0,slots.get(slots.size()-1).indexOf('-')).replace(':','.'))? "Today": "Tommorow");

                createNotifications(slots.get(Booking.pos),day);
                Booking.avail.set(Booking.pos,"Booked");
                Log.i("Position", String.valueOf(Booking.pos));
                Log.i("Avail",Booking.avail.toString());
                Booking.bookingAdapter.notifyDataSetChanged();
                //               try {
//                    Booking.jsonArray.getJSONObject(Booking.pos).put(slots.get(Booking.pos),"Booked");
//                    Booking.query.whereEqualTo("DoctorName",Booking.doctor);
//                   Booking.query.findInBackground(new FindCallback<ParseObject>() {
//                        @Override
//                        public void done(List<ParseObject> objects, ParseException e) {
//                            if(e==null && objects.size()>0){
//                                objects.get(0).put("TimeSlot",Booking.jsonArray);
//                                objects.get(0).saveInBackground();
//                            }
//                            else
//                                Log.i(this.toString(),e.getMessage());
//                        }
                //  });
                Booking.appointments = new ParseObject("Appointments");
                Booking.appointments.put("Doctor",Booking.doctor);
                Booking.appointments.put("Patient",ParseUser.getCurrentUser().getUsername());
                Booking.appointments.put("Time",slots.get(Booking.pos));
                Booking.appointments.put("Day",getIntent().getStringExtra("Day"));
                Booking.appointments.saveInBackground();

//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                ParseObject Payments = new ParseObject("PaymentsMadeToDocs");
                Payments.put("Doctor",Booking.doctor);
                Payments.put("Patient",ParseUser.getCurrentUser().getUsername());
                Payments.put("Price",price);
                Payments.saveInBackground();



                //
                Toast.makeText(PaymentActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: " + approvalRefNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(PaymentActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PaymentActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PaymentActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }




    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            CharSequence name = "AppointmentsChannel";
            String descrition = "Remainder for Appointments";
            int imp = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("AppointmentTime",name,imp);
            channel.setDescription(descrition);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }



    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createNotifications(String time,String day){

        Log.i(this.toString(),"InsideNotification");
        Intent intent = new Intent(getApplicationContext(),NotificationBroadCaster.class);
        intent.putExtra("Doctor",Booking.doctor);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour=Integer.parseInt(time.substring(0,time.indexOf(":")));
        int minute =Integer.parseInt(time.substring(time.indexOf(":")+1,time.indexOf('-')));
        if(minute==0){
            hour-=1;
            minute=50;}
        else
            minute-=10;

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);
        long timeInMillis = calendar.getTimeInMillis();
        if(!day.equals("Today")){
            timeInMillis += 86400000;
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

    }
}
