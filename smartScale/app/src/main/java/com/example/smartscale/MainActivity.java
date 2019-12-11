package com.example.smartscale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;







public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(TAG, "onCreate: MainActivity started");


        Log.d(TAG, "onCreate: start retrieving from Firebase");

        Intent goFirebaseUIActivity = new Intent();
        goFirebaseUIActivity.setClass(this,FirebaseUIActivity.class);
        startActivity(goFirebaseUIActivity);


        finish();





    }





}

