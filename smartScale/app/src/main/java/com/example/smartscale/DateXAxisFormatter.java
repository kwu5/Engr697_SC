package com.example.smartscale;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class DateXAxisFormatter extends ValueFormatter  {

    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private String TAG = "DateXAxisFormatter";

    private int month;


    DateXAxisFormatter(int month){
        this.month = month;
    }




    @Override
    public java.lang.String getFormattedValue(float value){



       String date = String.valueOf((int)value);

//        Log.d(TAG, "getFormattedValue: "+ value);
//        Log.d(TAG, "getFormattedValue: "+ date);

       return months[month-1]+ " " + date ;

        }


}
