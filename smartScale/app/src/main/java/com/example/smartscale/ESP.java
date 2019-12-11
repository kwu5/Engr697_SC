package com.example.smartscale;

import java.util.HashMap;
import java.util.Map;

public class ESP {



    private float WEIGHT = 0f;

    private String TIMESET;

    public ESP(){

    }

    public ESP(float weight , String timeSet){
        WEIGHT = weight;
        TIMESET = timeSet;

    }




    public float getWeight(){
        return WEIGHT;
    }

    public String getTimeSet(){
        return TIMESET;
    }




}
