package com.parse.starter;

import android.annotation.TargetApi;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PatientProfileFillingActivity extends AppCompatActivity {

    EditText fullName;
    EditText email;
    EditText age;
    EditText raddress;
    EditText mobileNumber;
    RadioButton genderradioButton;
    RadioGroup radioGroup;
    ParseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details_filling);
        setTitle("Update Profile");

        fullName=(EditText) findViewById(R.id.fullNameEditText);
        email=(EditText) findViewById(R.id.emailEditText);
        age=(EditText)findViewById(R.id.ageEditText);
        raddress=(EditText) findViewById(R.id.raddressEditText);
        mobileNumber=(EditText)findViewById(R.id.mobileNumberEditText);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);

    }
    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), HomeActivityPatient.class);
        startActivity(intent);
    }

    public void doneClicked(View view) {
        Log.i("button","done button clicked");
        final int selectedId = radioGroup.getCheckedRadioButtonId();
        if (fullName.getText().toString().matches("") || email.getText().toString().matches("")
                || age.getText().toString().matches("")|| raddress.getText().toString().matches("")||selectedId==-1) {
            Toast.makeText(this, "All fields are necessary.",Toast.LENGTH_SHORT).show();

        }
        else {

            ParseUser.getCurrentUser().put("fullName", fullName.getText().toString());
            ParseUser.getCurrentUser().put("email",email.getText().toString());

            ParseUser.getCurrentUser().put("Age", age.getText().toString());

            ParseUser.getCurrentUser().put("Address", raddress.getText().toString());

            ParseUser.getCurrentUser().put("Mobile_Number", mobileNumber.getText().toString());


            genderradioButton = (RadioButton) findViewById(selectedId);

            ParseUser.getCurrentUser().put("Gender", genderradioButton.getText().toString());


            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e!=null){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Email format not correct",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        showUserList();
                    }
                }
            });


        }


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

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}



