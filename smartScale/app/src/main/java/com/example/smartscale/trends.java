package com.example.smartscale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class trends extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

//    private Context context;

    private Button button_measurement, button_trends, button_myAccount, BMI, WEIGHT;
    private Spinner chooseMonth;
    private LineChart chart;

    private static final String TAG = "Trends";

    private char showFlag = 'w';
    /***w: show Weight
     * b: show BMI
     */


//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference dbRef;
    private String USERUID;

    private static int themonth;  //to get the month selected, +1 to the value

    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private List<Entry> entries = new ArrayList<Entry>();


    private HashMap<String, Object> BMI_map;
    private HashMap<String, Object> WEIGHT_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);
//        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();

        Log.d(TAG, "onCreate: " + TAG + " starts. ");

        USERUID = getIntent().getStringExtra("USERUID");
        if (!USERUID.isEmpty()) {
            Log.d(TAG, "onCreate: Useruid get :" + USERUID);
        } else {
            Log.d(TAG, "onCreate: Useruid not valid");
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
                showChart1();

            }
        });
        WEIGHT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFlag = 'w';
                showChart1();

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseMonth.setAdapter(adapter);
        chooseMonth.setOnItemSelectedListener(this);


    }

    public void showChart1() {

        entries.clear();


        DatabaseReference userdb = dbRef.child("UserData").child(USERUID);

        userdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WEIGHT_map = dataSnapshot.getValue(User.class).getWEIGHT();
                BMI_map = dataSnapshot.getValue(User.class).getBMI();
                Log.d(TAG, "onDataChange: " + WEIGHT_map);

                showChart2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "showChart: ", databaseError.toException());
            }
        });

    }


    public void showChart2() {

        switch (showFlag) {
            case 'w':
                if (WEIGHT_map != null) {
                    showData(WEIGHT_map, themonth);
                } else {
                    Log.d(TAG, "showChart: WEIGHT_map not exist");
                }
                break;
            case 'b':
                if (BMI_map != null) {
                    showData(BMI_map, themonth);
                } else {
                    Log.d(TAG, "showChart: BMI_map not exist");
                }
                break;
            default:
                Log.d(TAG, "showChart: Not a valid arg ");
        }


    }

    public void showData(HashMap<String, Object> data, final int month) {


        TreeMap<String, Object> data_S = new TreeMap<>();
        data_S.putAll(data);


        String[] data_K = data_S.keySet().toArray(new String[data_S.keySet().size()]);


        try {


            Log.d(TAG, "showData: month =  " + month);
            Log.d(TAG, "showData: data_K" + data_K.toString());
            for (int i = 0; i < (data_K.length); i++) {


                if (months[month].equals(data_K[i].substring(0, 3))) {

                    float date = Float.valueOf(data_K[i].substring(3, 5));
                    Log.d(TAG, "showData: date: " + date);
                    float dataf = Float.valueOf(data.get(data_K[i]).toString());
                    entries.add(new Entry(date, dataf));
                }


            }


        } catch (Exception ee) {
            Log.d(TAG, "onEvent: BMI ERROR" + ee.getMessage());
        }


        Log.d(TAG, "onEvent: Entry: " + entries);

        LineDataSet dataSet = new LineDataSet(entries, " ");

        Log.d(TAG, "onEvent: DataSet: " + dataSet);


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


        Log.d(TAG, "onItemSelected: The month " + themonth);
        showChart1();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        themonth = 12;
        Log.d(TAG, "onItemSelected: The month " + themonth);
        showChart1();


    }
}