package com.parse.starter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DoctorProfileFillingActivity extends AppCompatActivity {
    EditText fullName;
    EditText email;
    EditText specialisation;
    EditText raddress;
    EditText mobileNumber;
    EditText workingExperience;
    EditText pricePerSessionEditText;
    EditText morningSlot;
    EditText afterLunchSlot;
    RadioButton genderradioButton;
    RadioGroup radioGroup;
    ParseUser user;
    ArrayList<String> timeslots;
    int hour ,minute;
    JSONArray slots;
    JSONArray givenTime;
    ParseQuery<ParseUser> query;
    String phototype="";
    ParseObject parseObject;
    boolean DP=false,AADHAR=false,LICENSE=false;
    Button DPbutton,AADHARbutton,LICENSEbutton;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile_filling);
        setTitle("Profile Update");
        sharedPreferences = this.getSharedPreferences("Dr.Home local", MODE_PRIVATE);
        fullName = (EditText) findViewById(R.id.fullNameEditText);
        email = (EditText) findViewById(R.id.emailEditText);
        specialisation= (EditText) findViewById(R.id.specialisationEditText);
        raddress = (EditText) findViewById(R.id.raddressEditText);
        mobileNumber = (EditText) findViewById(R.id.mobileNumberEditText);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        DPbutton=(Button) findViewById(R.id.uploadDpButton);
        AADHARbutton=(Button)findViewById(R.id.uploadAdharButton);
        LICENSEbutton=(Button)findViewById(R.id.uploadLicenseButton);
        workingExperience=findViewById(R.id.workingExperienceEditText);
        pricePerSessionEditText=findViewById(R.id.pricePerSession);
        morningSlot=(EditText)findViewById(R.id.morningSlotEditText);
        afterLunchSlot=(EditText)findViewById(R.id.afterLunchslotEditText);
        givenTime = new JSONArray();
        timeslots = new ArrayList<>();
        slots = new JSONArray();

        parseObject = new ParseObject("Doctor");


    }

    public void getPhoto() {
        try{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("Image Selected", "Good work");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile(ParseUser.getCurrentUser().getUsername(), byteArray);

//                ParseObject object = new ParseObject("Image");
                if(phototype.equals("DP"))
                    ParseUser.getCurrentUser().put("DP", file);
                else if(phototype.equals("AADHAR"))
                    ParseUser.getCurrentUser().put("AADHAR", file);
//                object.put("username", ParseUser.getCurrentUser().getUsername());
                else if(phototype.equals("LICENSE"))
                    ParseUser.getCurrentUser().put("LICENSE", file);

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(DoctorProfileFillingActivity.this, "Image has been shared!", Toast.LENGTH_SHORT).show();
                            if(phototype.equals("DP"))
                            {
                                DP=true;
                                DPbutton.setBackgroundColor(R.color.grey);
                                DPbutton.setClickable(false);
                                phototype="";

                            }
                            else if(phototype.equals("AADHAR"))
                            {
                                AADHAR=true;
                                AADHARbutton.setBackgroundColor(R.color.grey);
                                AADHARbutton.setClickable(false);
                                phototype="";
                            }
//
                            else if(phototype.equals("LICENSE"))
                            {
                                LICENSE=true;
                                LICENSEbutton.setBackgroundColor(R.color.grey);
                                LICENSEbutton.setClickable(false);
                                phototype="";
                            }
                        } else {
                            e.printStackTrace();
                            Toast.makeText(DoctorProfileFillingActivity.this, "There has been an issue uploading the image :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{

        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    public void getBankDetails() {
        Intent intent = new Intent(getApplicationContext(), BankAccountDetails.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void uploadDpclicked(View view) {
        phototype="DP";
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Please upload your original profile picture." )
                .setPositiveButton("OK",null)
                .show();
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void uploadAdharClicked(View view) {
        phototype="AADHAR";
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Warning!")
                .setMessage("Any  type of Forgery may lead to legal actions." )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        } else {
                            getPhoto();
                        }
                    }
                })
                .show();



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void uploadLicenseClicked(View view) {
        phototype="LICENSE";
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Warning!")
                .setMessage("Any  type of Forgery may lead to legal actions." )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        } else {
                            getPhoto();
                        }
                    }
                })
                .show();

    }

    public void doneClicked(View view) {
        Log.i("button","done button clicked");
        final int selectedId = radioGroup.getCheckedRadioButtonId();
        if (fullName.getText().toString().matches("") || email.getText().toString().matches("")
                || specialisation.getText().toString().matches("")
                || raddress.getText().toString().matches("")||selectedId==-1
                ||  !DP || !AADHAR || !LICENSE) {
            Toast.makeText(this, "All fields are necessary.",Toast.LENGTH_SHORT).show();

        }
        else {
            givenTime.put(morningSlot.getText().toString().replace(':','.'));
            givenTime.put(afterLunchSlot.getText().toString().replace(':','.'));

            ParseUser.getCurrentUser().put("fullName", fullName.getText().toString());
            ParseUser.getCurrentUser().put("email",email.getText().toString());

            ParseUser.getCurrentUser().put("Specialisation", specialisation.getText().toString());
            ParseUser.getCurrentUser().put("WorkingExperience", workingExperience.getText().toString());
            ParseUser.getCurrentUser().put("PricePerSession", pricePerSessionEditText.getText().toString());

            ParseUser.getCurrentUser().put("Address", raddress.getText().toString());      // doctor's raddress is clinic address

            ParseUser.getCurrentUser().put("Mobile_Number", mobileNumber.getText().toString());
            ParseUser.getCurrentUser().put("Time", givenTime);

            genderradioButton = (RadioButton) findViewById(selectedId);

            ParseUser.getCurrentUser().put("Gender", genderradioButton.getText().toString());


            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void done(ParseException e) {
                    if(e!=null){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Email format not correct",Toast.LENGTH_SHORT).show();
                    }
                    else{

                        getTimeslots();
                        getBankDetails();
                    }
                }
            });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getTimeslots() {

        Log.i("Time", givenTime.toString());
        for (int i = 0; i < givenTime.length(); i++) {
            double start;
            double end;
            try {
                start = Double.parseDouble(givenTime.getString(i).substring(0, givenTime.getString(i).indexOf("-")));
                end = Double.parseDouble(givenTime.getString(i).substring(givenTime.getString(i).indexOf("-") + 1));
                Log.i("Time",givenTime.getString(i).substring(0,givenTime.getString(i).indexOf("-"))+" "+givenTime.getString(i).substring(givenTime.getString(i).indexOf("-")+1));
                DateFormat df = new SimpleDateFormat("HH:mm");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, (int) start);
                cal.set(Calendar.MINUTE, (int) (Math.round((start - (int) start)* 100)));
                Log.i("Time",Integer.toString((int) (Math.round((start - (int) start)* 100))));
                String slot = "";
                while (cal.get(Calendar.HOUR_OF_DAY) < (int) end) {

                    slot += df.format(cal.getTime()) + "-";
                    cal.add(Calendar.MINUTE, 30);
                    slot += df.format(cal.getTime());
                    if (cal.get(Calendar.HOUR_OF_DAY) == (int) end && cal.get(Calendar.MINUTE)>(int)(Math.round((end - (int) end) * 100) ))
                        break;
                    timeslots.add(slot);

                    slot = "";
                }
                double check = end - (int) end;
                if (check != 0) {
                    slot += df.format(cal.getTime()) + "-";
                    cal.add(Calendar.MINUTE, 30);
                    slot += df.format(cal.getTime());
                    timeslots.add(slot);

                }

            } catch (Exception error) {
                Log.i("Error", error.getMessage());
            }
        }


        Log.i("Size", Integer.toString(timeslots.size()));
        for(String s:timeslots){
            try {
                slots.put(new JSONObject().put(s,"Available"));
                Log.i("timeSlot",s);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        Log.i("JsonObject",slots.toString());
        parseObject.put("DoctorName",ParseUser.getCurrentUser().getUsername());
        parseObject.put("TimeSlot",slots);
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Log.i(this.toString(),"Success");
                else
                    Log.i(this.toString(),e.getMessage()) ;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.profile_filling_screen_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            sharedPreferences.edit().putBoolean("isLoggedIn?",false).apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
    private void createNotifications() throws JSONException {

        Log.i(this.toString(),"InsideNotification");
        Intent intent = new Intent(getApplicationContext(),NotificationBroadCaster.class);
        intent.putExtra("Doctor","Doctor");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int time=Integer.parseInt(givenTime.getString(0).substring(0, givenTime.getString(0).indexOf("-")));
        hour = (int)time;
        minute=(int) (Math.round((time - (int) time)* 100));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);
        long timeInMillis = calendar.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis,86400000 ,pendingIntent);

    }


}
