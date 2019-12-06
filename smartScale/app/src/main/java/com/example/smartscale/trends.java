package com.example.smartscale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class trends extends AppCompatActivity {

    private Button button_measurement, button_trends, button_myAccount,BMI,WEIGHT;

    private static final String TAG = "Trends";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String USERUID;

    private int month = 1;//todo

    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


//    private LineGraphSeries<DataPoint> WeightSeries;
//    private LineGraphSeries<DataPoint> BMISeries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);

        Log.d(TAG, "onCreate: " + TAG + " starts. ");

        USERUID = getIntent().getStringExtra("USERUID");
        if (!USERUID.isEmpty()) {
            Log.d(TAG, "onCreate: Useruid get :" + USERUID);
        } else {
            Log.d(TAG, "onCreate: Useruid not get");
        }


        //initialize common widgets
        button_measurement = findViewById(R.id.measurements);
        button_myAccount = findViewById(R.id.myAccount);
        button_trends = findViewById(R.id.trends);
        BMI = findViewById(R.id.BMI_switch);
        WEIGHT = findViewById(R.id.WEIGHT_switch);



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


        final LineChart chart = findViewById(R.id.chart);

        final List<Entry> entries = new ArrayList<Entry>();

        final DocumentReference docRef = db.collection("UserData").document(USERUID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
//                    Log.d(TAG, "Current data: " + snapshot.getData());

//
                    Map<String, Long> WEIGHT_data = (Map) snapshot.get("WEIGHT");
//                    Map<String, Long> BMI_data = (Map) snapshot.get("BMI");

                    TreeMap<String, Long> WEIGHT_data_S = new TreeMap<>();
                    WEIGHT_data_S.putAll(WEIGHT_data);


                    String WEIGHT_K[] = WEIGHT_data_S.keySet().toArray(new String[WEIGHT_data.keySet().size()]);
//                   Set<String> WEIGHT_key = WEIGHT_data.keySet();
//                    String BMI_key[] = ((String[]) BMI_data.keySet().toArray());

                    try {
//
                        for (int i = 0; i < WEIGHT_K.length; i++) {

                            if (months[month - 1].equals(WEIGHT_K[i].substring(0, 3))) {
                                float date = Float.valueOf(WEIGHT_K[i].substring(4,6));
//                                double date1 = Double.parseDouble(date);
                                Log.d(TAG, "setSeries: date: " + date);
                                entries.add(new Entry(date, WEIGHT_data.get(WEIGHT_K[i])));
                            }

                        }


                        /**find all months
                         for (int i = 0; i < WEIGHT_K.length; i++) {
                         //
                         int month_int = 1;
                         for (int j = 0; j < months.length; j++, month_int++) {
                         //                                Log.d(TAG, "onEvent: "+ WEIGHT_key[i].substring(0, 3));
                         if (months[j].equals(WEIGHT_K[i].substring(0, 3))) {
                         break;
                         }
                         }
                         String date = month_int + "." + WEIGHT_K[i].substring(4);
                         //                            Log.d(TAG, "setSeries: month_int: " + month_int);
                         //                            Log.d(TAG, "setSeries: date: " + date);
                         Float datef = Float.valueOf(date);
                         Log.d(TAG, "setSeries: date: " + datef);

                         entries.add(new Entry(datef, WEIGHT_data.get(WEIGHT_K[i])));
                         }
                         **/


                    } catch (Exception ee) {
                        Log.d(TAG, "onEvent: ERROR" + ee.getMessage());
                    }


                }


                Log.d(TAG, "onEvent: Entry: " + entries);

                LineDataSet dataSet = new LineDataSet(entries, "Label");

                Log.d(TAG, "onEvent: Dataset: " + dataSet);


                LineData lineData = new LineData(dataSet);
//                lineData.setValueFormatter(new DateXAxisFormatter());
//                                           Log.d(TAG, "onEvent: Entry: 1");


                Log.d(TAG, "onEvent: LineData"+ lineData);

                chart.setData(lineData);


                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(true);
                xAxis.setValueFormatter(new DateXAxisFormatter(month));



//                                           Log.d(TAG, "onEvent: Entry: 2");

                chart.invalidate();
            }
        });


//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        CreateGraph(graph, "WEIGHT");


//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
//                new DataPoint(9.11,1),
//                new DataPoint(9.12,5),
//                new DataPoint(9.13,3)
//
//        });
//        graph.addSeries(series);
    }


    public void enter(View v) {
        Intent goNext = new Intent();
        dataSend(goNext);
        switch (v.getId()) {
            case (R.id.myAccount):
                goNext.setClass(this, myAccount.class);
                break;
            case (R.id.measurements):
                goNext.setClass(this, measurements.class);
                break;

        }

        startActivity(goNext);

    }

    public void dataSend(Intent i) {
        return;
    }












//    private void CreateGraph(final GraphView graph, String field){
//
//        graph.removeAllSeries();
//        // [START listen_document]
//        final DocumentReference docRef = db.collection("UserData").document(USERUID);
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e);
//                    return;
//                }
//
//                if (snapshot != null && snapshot.exists()) {
////                    Log.d(TAG, "Current data: " + snapshot.getData());
//
//
//                    Map<String, Long> WEIGHT_data = (Map) snapshot.get("WEIGHT");
//                    Map<String, Long> BMI_data = (Map) snapshot.get("BMI");
//
//
//                    String WEIGHT_key[] = WEIGHT_data.keySet().toArray(new String[WEIGHT_data.keySet().size()]);
////                    String BMI_key[] = ((String[]) BMI_data.keySet().toArray());
//
//
//                    DataPoint dataPoints[] = new DataPoint[WEIGHT_key.length];
////                    try {
////
//                        for (int i = 0; i < WEIGHT_data.size(); i++) {
////
//                            int month_int = 1;
//                            for (int j = 0; j < months.length; j++, month_int++) {
////                                Log.d(TAG, "onEvent: "+ WEIGHT_key[i].substring(0, 3));
//                                if (months[j].equals(WEIGHT_key[i].substring(0, 3))) {
//                                    break;
//                                }
//                            }
//                            String date = month_int + "."+ WEIGHT_key[i].substring(4);
////                            Log.d(TAG, "setSeries: month_int: " + month_int);
////                            Log.d(TAG, "setSeries: date: " + date);
//                            Float datef = Float.valueOf(date);
//                            Log.d(TAG, "setSeries: date: " + datef);
//
//                            dataPoints[i] = new DataPoint(datef,WEIGHT_data.get(WEIGHT_key[i]));
//                            Log.d(TAG, "onEvent: Datapoint: "+ dataPoints[i]);
//                        }
//
//
//
//
//
//
//
////                    }catch (Exception ee){
////                        Log.d(TAG, "onEvent: ERROR"+ ee.getMessage());
////                    }
//
////                    Date date = new Date();
////                    Date firstDate = new Date();
////                    Date lastDate = new Date();
////
////                    SimpleDateFormat format_fireBase = new SimpleDateFormat("MMM dd");
//////                    SimpleDateFormat format_graph = new SimpleDateFormat("    MMM dd");
////
////                    for (int i = 0; i < WEIGHT_data.size(); i++) {
////
////                        //create date
////
////                        try {
////
////                            date = format_fireBase.parse(WEIGHT_key[i]);
//////                            Log.d(TAG, "onEvent:O_Date "+ WEIGHT_key[i]);
//////                            Log.d(TAG, "onEvent:Date "+ date);
////
////
////
////                            if(i==0) { firstDate = date; }
////                            else if(i == WEIGHT_data.size()-1){ lastDate = date;}
////
////
////                        } catch (ParseException ee) {
////                            ee.printStackTrace();
////                        }
////
//////                        Log.d(TAG, "onEvent: safasdfdsafsfdsafadsfads  "+  WEIGHT_data.get(WEIGHT_key[i]).getClass());
////
////                        long d =  WEIGHT_data.get(WEIGHT_key[i]);
////                        float dd = (float)d;
//////                        dataPoints[i] = new DataPoint(date, Math.round(d));
////                        dataPoints[i] = new DataPoint(date,dd );
////                        Log.d(TAG, "onEvent: Datapoint: "+ dataPoints[i]);
////
////                    }
//
////
//
////                new DataPoint(9.11,1),
////                new DataPoint(9.12,5),
////                new DataPoint(9.13,3)
////
////        });
//                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
//
//
//                    graph.addSeries(series);
//
//                  graph.getGridLabelRenderer().setNumHorizontalLabels(4);
//
//                    graph.getViewport().setMinX(1);
//                   graph.getViewport().setMaxX(12);
////                   graph.getViewport().setXAxisBoundsManual(true);
//
////
////
////                   graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(),format_fireBase));
////                    graph.getGridLabelRenderer().setNumHorizontalLabels(3);
////
////                    graph.getViewport().setMinX(firstDate.getTime());
////                    graph.getViewport().setMaxX(lastDate.getTime());
////                   graph.getViewport().setXAxisBoundsManual(true);
////
////                    graph.getGridLabelRenderer().setHumanRounding(false);
//
//
//                    Log.d(TAG, "onEvent: Finally get something");
//
//
////                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
////                new DataPoint(9.11,1),
////                new DataPoint(9.12,5),
////                new DataPoint(9.13,3)
////
//
////                    try{
//
////                        for(int i = 0; i < WEIGHT_data.size();i++){
//
////                            int month_int = 1;
////                            for(int j=0;j< months.length;j++,month_int++){
////                                if(months[j].equals(WEIGHT_key[i].substring(0,2))){
////                                    break;
////                                }
////                            }
////
////                            Log.d(TAG, "setSeries: month_int: "+ month_int );
//
//
//
//
//
//
//
////                   }
//
//                } else {
//                    Log.d(TAG, "Current data: null");
//                }
//            }
//        });
//        // [END listen_document]
//
//
//
//
//
//    }

}






