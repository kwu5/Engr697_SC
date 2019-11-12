package com.example.smartscale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;






public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    protected FirebaseFirestore db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(TAG, "onCreate: MainActivity started");


        signIn();





        Intent goMeasurement = new Intent();
        goMeasurement.setClass(this,measurements.class);
        startActivity(goMeasurement);



    }


    /**initialize Firebase
     *
     */
    public void signIn() {


        db = FirebaseFirestore.getInstance();


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        Log.d(TAG, "signIn: start retrieving from Firebase");
        /**
        db.collection("UserData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: sampleUserData" + document.get("BMI"));
                                temp = (long)document.get("BMI");
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("UserData").document("sampleUser").update("BMI",80);


        db.collection("ESP_SS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: USP_SS" + document.get("WEIGHT_RAW"));
                                //temp = (long)document.get("BMI");
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, document.getId() + " USER " + document.get("USER"));
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


         **/

        //With this change, timestamps stored in Cloud Firestore will be read back as com.google.firebase.
        // Timestamp objects instead of as system java.util.Date objects.
        // So you will also need to update code expecting a java.util.Date to instead expect a Timestamp. For example:

        // Old:
        //java.util.Date date = snapshot.getDate("created_at");
        // New:
        //Timestamp timestamp = snapshot.getTimestamp("created_at");
        //java.util.Date date = timestamp.toDate();


        //todo


    }






    public void basicReadWrite() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /**
        // Create a new user with a first, middle, and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Alan");
        user.put("middle", "Mathison");
        user.put("last", "Turing");
        user.put("born", 1912);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });



        //to read data
        db.collection("SampleUser")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        **/

    }
}


