package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.Adapters.DoctorsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DoctorsList extends AppCompatActivity {

    LoadingScreen loadingScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);
        loadingScreen = new LoadingScreen(this);
        loadingScreen.startloadingScreen();
        final RecyclerView recyclerView = findViewById(R.id.RecyclerView_DoctorList);
        final ArrayList<String> doctornames = new ArrayList<String>();
        final ArrayList<Bitmap> doctorImage  = new ArrayList<>();
        final ArrayList<String> workExp = new ArrayList<String>();
        final HashMap<String,Bitmap> imageMap  = new HashMap<>();
        final ArrayList<String> pricePerSession = new ArrayList<String>();
//        final ArrayList<Integer> avatars = new ArrayList<>();
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Toast.makeText(this,"Long press to view Doctor's profile." ,Toast.LENGTH_LONG).show();
        final RecyclerView.Adapter adapter= new DoctorsListAdapter(this,doctornames,doctorImage,workExp,pricePerSession);
        setTitle(getIntent().getStringExtra("Type")+'s');
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("Specialisation",getIntent().getStringExtra("Type"));
        Log.i("Type",getIntent().getStringExtra("Type"));
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (final ParseUser user : objects) {
                            doctornames.add(user.getUsername());
                            workExp.add(user.get("WorkExperience").toString());
                            pricePerSession.add(user.get("PricePerSession").toString());
                            user.getParseFile("DP").getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e==null && data!=null){
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                        imageMap.put(user.getParseFile("DP").getName().substring(user.getParseFile("DP").getName().indexOf("_")+1),bitmap);
                                        Log.i(this.toString(),user.getParseFile("DP").getName());
                                        recyclerView.setAdapter(adapter);
                                    }
                                    else{
                                        Log.i(this.toString(),e.getMessage());
                                    }
                                    if(imageMap.size()==doctornames.size()){
                                        for(String name : doctornames){
                                            doctorImage.add(imageMap.get(name));
                                            Log.i(this.toString(),name);
                                        }
                                        Log.i(this.toString(),Integer.toString(doctorImage.size()));
                                        loadingScreen.stoploadingScreen();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
//                                    avatars.add(R.drawable.doctor);
                            Log.i("Names",user.getUsername());

                        }


                    }
                    else
                        loadingScreen.stoploadingScreen();


                } else {

                    e.printStackTrace();
                }
            }
        });



//        //getImages(); not used now
    }


    public void backPressed(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }
}