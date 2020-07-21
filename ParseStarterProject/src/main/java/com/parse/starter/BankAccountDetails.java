package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BankAccountDetails extends AppCompatActivity {

    EditText accountHolderName;
    EditText ifscCode;
    EditText accountNumber;
    EditText bankName;
    EditText upimobileNumber;
    RadioGroup radioGroup;
    ParseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_details);
        setTitle("Update Profile");

        accountHolderName=(EditText) findViewById(R.id.accountHolderNameEditText);
        ifscCode=(EditText) findViewById(R.id.ifscEditText);
        accountNumber=(EditText)findViewById(R.id.accountNumberEditText);
        bankName=(EditText) findViewById(R.id.BankNameEditText);
        upimobileNumber=(EditText)findViewById(R.id.upiMobileNumberEditText);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);

    }

    public void doneClicked(View view) {
        Log.i("button","done button clicked");
        if (accountHolderName.getText().toString().matches("") || ifscCode.getText().toString().matches("")
                || accountNumber.getText().toString().matches("")|| bankName.getText().toString().matches("")) {
            Toast.makeText(this, "Please Fill all necessary fields!",Toast.LENGTH_SHORT).show();

        }
        else {

            ParseUser.getCurrentUser().put("AccountHolderName", accountHolderName.getText().toString());
            ParseUser.getCurrentUser().put("IFSC",ifscCode.getText().toString());

            ParseUser.getCurrentUser().put("AccountNumber", accountNumber.getText().toString());

            ParseUser.getCurrentUser().put("BankName", bankName.getText().toString());

            ParseUser.getCurrentUser().put("UPIMobileNumber", upimobileNumber.getText().toString());




            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e!=null){
                        e.printStackTrace();
                    }
                    else{
                      //  startActivity(new Intent(BankAccountDetails.this, AppointmentDisplayForDoctors.class));
                //        startActivity(new Intent(BankAccountDetails.this, AppointmentDisplayForDoctors.class));
                        finish();
                    }
                }
            });
        }
    }
}