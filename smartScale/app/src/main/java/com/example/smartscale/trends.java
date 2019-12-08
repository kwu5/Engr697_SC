package com.example.smartscale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

public class trends extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

//    private Context context;

    private Button button_measurement, button_trends, button_myAccount,BMI,WEIGHT;
    private Spinner chooseMonth;
    private LineChart chart ;

    private static final String TAG = "Trends";

    private char showFlag = 'w';
    /***w: show Weight
     * b: show BMI
     */




    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String USERUID;

    private static int themonth  ;  //to get the month selected, +1 to the value

    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private List<Entry> entries = new ArrayList<Entry>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);
//        context = this;

        Log.d(TAG, "onCreate: " + TAG + " starts. ");

        USERUID = getIntent().getStringExtra("USERUID");
        if (!USERUID.isEmpty()) {
            Log.d(TAG, "onCreate: Useruid get :" + USERUID);
        } else {
            Log.d(TAG, "onCreate: Useruid not get");
        }


        //initialize common widgets
        button_measurement = findViewById(R.id.measurements);
//        button_myAccount = findViewById(R.id.myAccount);
        button_trends = findViewById(R.id.trends);
        BMI = findViewById(R.id.BMI_switch);
        WEIGHT = findViewById(R.id.WEIGHT_switch);
        chooseMonth = findViewById(R.id.spinner);
        chart = findViewById(R.id.chart);



        //set up listeners
        button_measurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter(button_measurement);
            }
        });
//        button_myAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                enter(button_myAccount);
//
//            }
//        });
        BMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFlag = 'b';
                showChart(showFlag);

            }
        });
        WEIGHT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFlag = 'w';
                showChart(showFlag);

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.months,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseMonth.setAdapter(adapter);
        chooseMonth.setOnItemSelectedListener(this);



    }

    public void showChart( char showFlag) {

        entries.clear();



        final DocumentReference docRef = db.collection("UserData").document(USERUID);
        switch (showFlag) {
            case 'w':
                showWeight(docRef,themonth);
                break;
            case 'b':
                showBMI(docRef,themonth);
                break;
                default:
                    Log.d(TAG, "showChart: Not a valid arg ");
        }


}

    public void showBMI(DocumentReference docRef, final int month){


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
                    Map<String, Double> BMI_data = (Map) snapshot.get("BMI");

                    //to get a sorted map
                    TreeMap<String, Double> BMI_data_S = new TreeMap<>();
                    BMI_data_S.putAll(BMI_data);


                    String BMI_K[] = BMI_data_S.keySet().toArray(new String[BMI_data.keySet().size()]);


                    try {


//                        if (month == 12) {
//                            Log.d(TAG, "onEvent 13: month =  "+ month);
//                            for (int i = 0; i < 7; i++) {
//                                float date = Float.valueOf(WEIGHT_K[i].substring(4, 6));
//                                entries.add(new Entry(date,WEIGHT_data.get(WEIGHT_K[i])));
//                            }

//                    } else {

                        Log.d(TAG, "onEvent else: month =  " + month);
                        for (int i = 0; i < (BMI_K.length); i++) {


                            if (months[month].equals(BMI_K[i].substring(0, 3))) {

                                float date = Float.valueOf(BMI_K[i].substring(4, 6));
                                Log.d(TAG, "setSeries: date: " + date);
                                entries.add(new Entry(date, BMI_data.get(BMI_K[i]).floatValue()));
                            }

                        }


//                        }

                    } catch (Exception ee) {
                        Log.d(TAG, "onEvent: BMI ERROR" + ee.getMessage());
                    }


                }


                Log.d(TAG, "onEvent: Entry: " + entries);

                LineDataSet dataSet = new LineDataSet(entries, " ");

                Log.d(TAG, "onEvent: Dataset: " + dataSet);


                LineData lineData = new LineData(dataSet);


                Log.d(TAG, "onEvent: LineData" + lineData);

                chart.setData(lineData);


                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new DateXAxisFormatter(month));
                xAxis.setAxisMinimum(1);
                xAxis.setAxisMaximum(31);

                chart.invalidate();
            }
        });


    }
    
    public void showWeight(DocumentReference docRef,final int month){

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

                    //to get a sorted map
                    TreeMap<String, Long> WEIGHT_data_S = new TreeMap<>();
                    WEIGHT_data_S.putAll(WEIGHT_data);


                    String WEIGHT_K[] = WEIGHT_data_S.keySet().toArray(new String[WEIGHT_data.keySet().size()]);


                    try {


//                        if (month == 12) {
//                            Log.d(TAG, "onEvent 13: month =  "+ month);
//                            for (int i = 0; i < 7; i++) {
//                                float date = Float.valueOf(WEIGHT_K[i].substring(4, 6));
//                                entries.add(new Entry(date,WEIGHT_data.get(WEIGHT_K[i])));
//                            }

//                    } else {

                        Log.d(TAG, "onEvent else: month =  " + month);
                        for (int i = 0; i < (WEIGHT_K.length); i++) {


                            if (months[month].equals(WEIGHT_K[i].substring(0, 3))) {

                                float date = Float.valueOf(WEIGHT_K[i].substring(4, 6));
                                Log.d(TAG, "setSeries: date: " + date);
                                entries.add(new Entry(date, WEIGHT_data.get(WEIGHT_K[i])));
                            }

                        }


//                        }

                    } catch (Exception ee) {
                        Log.d(TAG, "onEvent: WEIGHT ERROR" + ee.getMessage());
                    }


                }


                Log.d(TAG, "onEvent: Entry: " + entries);

                LineDataSet dataSet = new LineDataSet(entries, " ");

                Log.d(TAG, "onEvent: Dataset: " + dataSet);


                LineData lineData = new LineData(dataSet);


                Log.d(TAG, "onEvent: LineData" + lineData);

                chart.setData(lineData);


                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new DateXAxisFormatter(month));
                xAxis.setAxisMinimum(1);
                xAxis.setAxisMaximum(31);

                chart.invalidate();
            }
        });



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



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//        String monthShowed = adapterView.getItemAtPosition(i).toString();
        themonth = adapterView.getSelectedItemPosition();




        Log.d(TAG, "onItemSelected: The month "+ themonth);
        showChart(showFlag);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        themonth = 12;
        Log.d(TAG, "onItemSelected: The month "+ themonth);
        showChart(showFlag);


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






