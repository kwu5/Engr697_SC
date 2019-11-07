package com.example.smartscale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class measurements extends AppCompatActivity {

    Button button_measurement, button_trends, button_myAccount;
    private static final String TAG = "Measurement";

    //All data list here
    private float weight;
    private String userId;

    private float BMI,bodyFat;

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Log.d(TAG, "onCreate: " + TAG + " starts. ");


        //initialize common widgets
        button_measurement = findViewById(R.id.measurements);
        button_myAccount = findViewById(R.id.myAccount);
        button_trends = findViewById(R.id.trends);

        //set up listener for bottom buttons
        button_myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter(button_myAccount);
            }
        });
        button_trends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter(button_trends);
            }
        });

        //todo
        getData();
        calculation();
        sendDataCloud();


    }


    public void enter(View v) {
        Intent goNext = new Intent();
        sendData(goNext);
        switch (v.getId()) {
            case (R.id.trends):
                goNext.setClass(this, trends.class);
                break;
            case (R.id.myAccount):
                goNext.setClass(this, myAccount.class);
                break;

        }

        startActivity(goNext);

    }


    public void getData() {


    }

    public void calculation() {

    }


    public void sendData(Intent i) {
        return;
    }


    public void sendDataCloud() {

    }

}
