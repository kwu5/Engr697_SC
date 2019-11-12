package com.example.smartscale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class measurements extends AppCompatActivity {

    Button button_measurement, button_trends, button_myAccount;
    private static final String TAG = "Measurement";



    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    private double BMI;

    //id for testing
    private final String sampleID = "sampleUser";
    private final float sampleHeight = 110;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Log.d(TAG, "onCreate: <" + TAG + "> starts. ");


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


        setUserInfo();
        changeDocument();


        //calculation();
        //updateDataFireBase();


    }


    public void enter(View v) {
        Intent goNext = new Intent();
        updateData(goNext);
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


    /**
     * load user info from firebase for local calculation
     */
    public void setUserInfo(){

    }


    /**
     * 1. Get raw_data from FireBase
     * 2. Perform calculation due to asynchronous
     * 3. upload data to fireBase
     */
    public void changeDocument() {

        DocumentReference docRef = db.collection("ESP_SS").document("WEIGHT_RAW");




        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSs = task.getResult();
                    if (documentSs.exists()) {
                        Log.d(TAG, "onComplete: DocumentSnapshot Data: " + documentSs.getData());
                        calculation(documentSs);
                        uploadData(documentSs, sampleID);



                    } else {
                        Log.d(TAG, "onComplete: No such document");
                    }
                } else {
                    Log.w(TAG, "onComplete: get failed with ", task.getException());
                }

            }
        });
   }


        /**
        db.collection("UserData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                weight = (float)document.get("BMI");
                                userId = (String)document.get("USER");

                                Log.d(TAG, "getData: "+  document.getId() + " => " + document.getData());
                                Log.d(TAG, "getData: "+ document.getId() +  " BMI "+ document.get("BMI")  );
                                Log.d(TAG, "getData: "+ document.getId() + " USER " + document.get("USER"));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });




        db.collection("UserData").document("sampleUser").update("BMI",80);


        Map<String,Object > sampleUser2 = new HashMap<>();
        sampleUser2.put("BMI",59);
        sampleUser2.put("userID",002);
        String id1 = " sampleUser2";
        db.collection("UserData").document(id1).set(sampleUser2);


        Log.d(TAG, "getData: procession finished");
         **/




    public void calculation(DocumentSnapshot documentSs) {


        String userId = sampleID;
        double weight = documentSs.getDouble("WEIGHT");


        if( (weight == 0.0f) && (userId == null) ){
            Log.d(TAG, "calculation: Error initializing weight and userId");
        }else{


            BMI = weight/ (float) Math.pow(sampleHeight,2);
            Log.d(TAG, "calculation: BMI is "+ BMI);

        }


    }


    /**
     *
     * 2.send data to other intent
     * @param i
     */
    public void updateData(Intent i) {
        return;
    }


    /**
     * update data to fireBase
     */
    public void uploadData(DocumentSnapshot documentSs, String userId){


        double weight = documentSs.getDouble("WEIGHT");

        Map <String,Object> updates = new HashMap<>();
        updates.put("WEIGHT",weight);
        updates.put("BMI",BMI);


        db.collection("UserData").document(userId).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "uploadData: upload success");
                }else{
                    Log.d(TAG, "uploadData: uploadData failed");
                }
            }
        });
        updates.clear();
    }





}
