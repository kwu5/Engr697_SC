package com.example.smartscale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class myAccount extends AppCompatActivity {

    Button button_measurement, button_trends, button_myAccount;
    private static final String TAG = "MyAccount";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        Log.d(TAG, "onCreate: " +  TAG + " starts. ");


        //initialize common widgets
        button_measurement = findViewById(R.id.measurements);
        button_myAccount = findViewById(R.id.myAccount);
        button_trends = findViewById(R.id.trends);

        //set up listener for bottom buttons
        button_measurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter(button_measurement);
            }
        });
        button_trends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter(button_trends);
            }
        });

    }


    public void enter(View v){
        Intent goNext = new Intent();
        dataSend(goNext);
        switch (v.getId()){
            case (R.id.trends):
                goNext.setClass(this,trends.class);
                break;
            case(R.id.measurements):
                goNext.setClass(this,measurements.class);
                break;

        }

        startActivity (goNext);

    }
    public void dataSend(Intent i){
        return ;
    }

    }

