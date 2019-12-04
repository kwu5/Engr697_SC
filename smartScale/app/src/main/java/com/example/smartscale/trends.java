package com.example.smartscale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class trends extends AppCompatActivity {

    Button button_measurement, button_trends, button_myAccount;
    private static final String TAG = "Trends";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);

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
        button_myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter(button_myAccount);
            }
        });


        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(9.11,1),
                new DataPoint(9.12,5),
                new DataPoint(9.13,3)

        });
        graph.addSeries(series);
    }


    public void enter(View v){
        Intent goNext = new Intent();
        dataSend(goNext);
        switch (v.getId()){
            case (R.id.myAccount):
                goNext.setClass(this,myAccount.class);
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


