package com.example.smartscale;

import java.util.HashMap;
import java.util.Map;

public class User {



    private HashMap<String,Object> BMI = new HashMap<>();
    private HashMap<String,Object> WEIGHT = new HashMap<>();

    private double HEIGHT;
    private float currentWeight = 0f;
    private float currentBMI = 0f;

    public User(){

    }

    public User( double Height){

        this.HEIGHT = Height;

    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("HEIGHT",HEIGHT);
        result.put("currentBMI",currentBMI);
        result.put("currentWeight",currentWeight);
        result.put("BMI",BMI);
        result.put("WEIGHT",WEIGHT);

        return  result;
    }


    public double getHEIGHT(){
        return HEIGHT;

    }


    public HashMap<String,Object> getBMI(){
        return BMI;
    }


    public HashMap<String,Object> getWEIGHT(){
        return WEIGHT;
    }


    public void setcurrentWeight(float currentWeight){
        this.currentWeight = currentWeight;
    }

    public void setcurrentBMI(float currentBMI){
        this.currentBMI = currentBMI;
    }




    public void updateBMI(String date, float bmi){
        BMI.put(date,bmi);

    }


    public void updateWeight(String date, float weight){
        WEIGHT.put(date,(int)weight);

    }


}
