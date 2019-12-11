package com.example.smartscale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.Query;
//import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class measurements extends AppCompatActivity {

    private Button button_measurement,button_trends, button_myAccount, button_start;
    private TextView textView_BMI, textView_WEIGHT;

    private static final String TAG = "Measurement";




    private DatabaseReference databaseReference;


    private boolean nUser = true;
    private static boolean nLogin = true;

    private Map<String, Object> weightUpdate = new HashMap<>();
    private Map<String, Object> BMIUpdate = new HashMap<>();

    private Map<String, Object> WEIGHT = new HashMap<>();
    private Map<String, Object> BMI = new HashMap<>();


    //id for testing
    private final String sampleID = "sampleUser";
    private final float sampleHeight = 110;

    private static String USERUID;
    private static boolean isUserInfoSet = false;


    private float weight_raw = 0f;
    private float bmi_raw = 0f;


    private double HEIGHT;
    private int AGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Log.d(TAG, "onCreate: <" + TAG + "> starts. ");

        databaseReference = FirebaseDatabase.getInstance().getReference();


        //initialize common widgets
        button_measurement = findViewById(R.id.measurements);
//        button_myAccount = findViewById(R.id.myAccount);
        button_trends = findViewById(R.id.trends);
        button_start = findViewById(R.id.start);

        textView_BMI = findViewById(R.id.BMI);
        textView_WEIGHT = findViewById(R.id.weight);

        //set up listener for bottom buttons
//        button_myAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                enter(button_myAccount);
//            }
//        });
        button_trends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enter(button_trends);
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserInfoSet) {
                    changeDocument();

                }
            }
        });


        if (nLogin) {
            setUserInfo();
            nLogin = false;
        }


    }


    public void enter(View v) {
        Intent goNext = new Intent();
        sentData(goNext);
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
     * 1. load user info passed from FirebaseUIActivity for local calculation
     * 2. if new user, create new document; prompt user to enter his/her information
     * 3. set document id;
     */
    public void setUserInfo() {


        USERUID = getIntent().getStringExtra("userUID");
        if (USERUID != null) {
            Log.d(TAG, "setUserInfo: userUid: " + USERUID);

        } else {
            Log.d(TAG, "setUserInfo: get userUid failed");
        }


        //check if new user here
        databaseReference.child("UserData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    Log.d(TAG, "List all userData below: ");
                    Log.d(TAG, TAG + "-- user read :" + user.toString());

                    if (user.getKey().equals(USERUID)) {
                        nUser = false;
                        Log.d(TAG, "setUserInfo: user info found: " + user.getChildren().toString());
                        Log.d(TAG, "setUserInfo: id is  " + user.getKey());


                        isUserInfoSet = true;


                        break;
                    }
                }

                //create new user document
                if (nUser) {
                    goInputUserInfo();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w(TAG, "setUserInfo: ", databaseError.toException());
            }
        });





//        db.collection("UserData").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
////                            Log.d(TAG, "List all userData below: ");
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
////                                Log.d(TAG, TAG + "-- user read :" + documentSnapshot.getData());
//                                if (documentSnapshot.get("USERUID").equals(USERUID)) {
//                                    nUser = false;
//                                    Log.d(TAG, "setUserInfo: user info found: " + documentSnapshot.getData());
//                                    Log.d(TAG, "setUserInfo: id is  " + documentSnapshot.getId());
//
//
//                                    isuserInfoSet = true;
//
//
//                                    break;
//                                }
//                            }
//
//                            //create new user document
//                            if (nUser) {
//                                goInputUserInfo();
//                            }
////
//                        } else {
//                            Log.d(TAG, TAG + " Error getting documents: ", task.getException());
//                        }
////
//                    }
//                });
////


    }

    /**
     * called in setUserInfo, go to inputUserInfo intent
     */
    public void goInputUserInfo() {
        Intent goInputUserInfo = new Intent();
        goInputUserInfo.setClass(this, inputUserInfo.class);
        startActivityForResult(goInputUserInfo, 1);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                createNewUserDoc(data);
                isUserInfoSet = true;
            } else {
                Log.d(TAG, "onActivityResult: value not return");
            }
        }
    }




    public void createNewUserDoc(Intent data) {


        HEIGHT = data.getDoubleExtra("height", 0);
        AGE = data.getIntExtra("age", 0);

        Log.d(TAG, "createNewUserDoc: value return");
        Log.d(TAG, "createNewUserDoc: Height:" + HEIGHT);
        Log.d(TAG, "createNewUserDoc: Age:" + AGE);


//        Map<String, Object> nData = new HashMap<>();

//        WEIGHT = new HashMap<>();
//        BMI= new HashMap<>();


//        //todo ask user for info
//        nData.put("HEIGHT", HEIGHT);
//        nData.put("USERUID", USERUID);
//        nData.put("AGE", AGE);
//        nData.put("WEIGHT", WEIGHT);
//        nData.put("BMI", BMI);
//
//
//        DocumentReference newUser = db.collection("UserData").document(USERUID);
//        newUser.set(nData);


        databaseReference.child("UserData").setValue(USERUID);
        String key = databaseReference.child("UserData").child(USERUID).getKey();
        User user = new User(HEIGHT);
        Map<String, Object> userData = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/UserData/"+key,userData);

        databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "createNewUserDoc: create new user success ");
            }
        });


    }


    /**
     * 1. Get raw_data from FireBase
     * 2. Perform calculation due to asynchronous
     */
    public void changeDocument() {


//        DocumentReference docRef = db.collection("ESP_SS_T").document("WEIGHT_RAW_T");
        databaseReference.child("ESP_SS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "changeDocument: data exists: "+ dataSnapshot.getKey());
                    calculation( dataSnapshot,USERUID);
                } else {
                    Log.d(TAG, "changeDocument: No such data ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "changeDocument: get failed with ", databaseError.toException());
            }
        });
    }

//
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSs = task.getResult();
//                    if (documentSs.exists()) {
//                        Log.d(TAG, "changeDocument: DocumentSnapshot Data: " + documentSs.getData());
//
//                        calculation(documentSs, USERUID);
//
//                    } else {
//                        Log.d(TAG, "changeDocument: No such document");
//                    }
//                } else {
//                    Log.w(TAG, "changeDocument: get failed with ", task.getException());
//                }
//
//            }
//        });
//    }


    /**
     * 1.Perform calculation
     * 2.call upload method
     *
     * @param
     * @param USERUID
     */
//    public void calculation(final DocumentSnapshot documentSs, final String USERUID) {
//
//
//        try {
//
//
//            final DocumentReference documentRef = db.collection("UserData").document(USERUID);
//            documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    double height = documentSnapshot.getDouble("HEIGHT");
//
//                    weight_raw = documentSs.getDouble("WEIGHT").intValue();
//                    Log.d(TAG, "calculation:Weight :" + weight_raw);
//                    bmi_raw = weight_raw / (float) Math.pow(height, 2);
//                    Log.d(TAG, "calculation: BMI is " + bmi_raw);
//
//                    uploadData(documentSs, USERUID);
//                }
//            });
//
//
//        } catch (Exception e) {
//            Log.d(TAG, "calculation: Error initializing weight and height: " + e.getMessage());
//        }
//
//
//    }

    public void calculation( final DataSnapshot ESPdataSs,final String USERUID) {



            DatabaseReference USERDataSs = databaseReference.child("UserData").child(USERUID);

            USERDataSs.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userdataSnapshot) {




                    double height = userdataSnapshot.getValue(User.class).getHEIGHT();


                    weight_raw = ESPdataSs.getValue(ESP.class).getWeight();
                    bmi_raw = weight_raw / (float) Math.pow(height, 2);


                    Log.d(TAG, "calculation: Height  "+ height);
                    Log.d(TAG, "calculation:Weight :" + weight_raw);
                    Log.d(TAG, "calculation: BMI is " + bmi_raw);

                    uploadData(ESPdataSs,userdataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "calculation: Error initializing weight and height: " + databaseError.getMessage());
                }
            });






//
//            final DocumentReference documentRef = db.collection("UserData").document(USERUID);
//            documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    double height = documentSnapshot.getDouble("HEIGHT");
//
//                    weight_raw = documentSs.getDouble("WEIGHT").intValue();
//                    Log.d(TAG, "calculation:Weight :" + weight_raw);
//                    bmi_raw = weight_raw / (float) Math.pow(height, 2);
//                    Log.d(TAG, "calculation: BMI is " + bmi_raw);
//
//                    uploadData(documentSs, USERUID);
//                }
//            });
//
//
//        } catch (Exception e) {
//            Log.d(TAG, "calculation: Error initializing weight and height: " + e.getMessage());
//        }


    }


    /**
     * 2.send data to other intent
     *
     * @param i
     */
    public void sentData(Intent i) {

        i.putExtra("USERUID", USERUID);


        return;
    }


    /**
     * update data to fireBase
     */
//    public void uploadData(DocumentSnapshot documentSs, String userId) {
    public void uploadData(DataSnapshot ESPDataSs, final DataSnapshot userDataSs) {

//        Log.d(TAG, "uploadData: weight: " + weight_raw);


        final String time = ESPDataSs.getValue(ESP.class).getTimeSet().substring(0, 3) + ESPDataSs.getValue(ESP.class).getTimeSet().substring(9, 11) ;
//        Log.d(TAG, "uploadData: Time is :"+ time);



//        WEIGHT.put("WEIGHT", weightUpdate);


//        userDataSs.getValue(User.class).updateBMI(time,bmi_raw);
//        userDataSs.getValue(User.class).updateWeight(time,weight_raw);
//        Log.d(TAG, "uploadData success ");

        DatabaseReference userdb = databaseReference.child("UserData").child(USERUID);


        WEIGHT = userDataSs.getValue(User.class).getWEIGHT();
        BMI = userDataSs.getValue(User.class).getBMI();





        WEIGHT.put(time, weight_raw);
        BMI.put(time,bmi_raw);

        userdb.child("currentBMI").setValue(bmi_raw);
        userdb.child("currentWeight").setValue(weight_raw);
        userdb.child("BMI").setValue(BMI);
        userdb.child("WEIGHT").setValue(WEIGHT).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "uploadData: all data upload.");
            }
        });






//        String key = databaseReference.child("UserData").child(USERUID).getKey();
//
//        Map<String, Object> updates = new HashMap<>();
//
//        weightUpdate.put(time,weight_raw);
//        BMIUpdate.put(time,bmi_raw);
//
//        updates.put("currentBMI",bmi_raw);
//        updates.put("currentWeight",weight_raw);
//        updates.put("BMI",BMIUpdate);
//        updates.put("WEIGHT",weightUpdate);
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/UserData/"+key,updates);
//
//        databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "uploadData: uploadData success ");
//            }
//        });







//        databaseReference.child("UserData").child(USERUID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("/UserData/"+key,userData);
//
//
//
//
//
//                dataSnapshot.getValue(User.class).updateBMI(time,bmi_raw);
//                dataSnapshot.getValue(User.class).updateWeight(time,weight_raw);
//                dataSnapshot.getValue(User.class).setcurrentBMI(bmi_raw);
//                dataSnapshot.getValue(User.class).setcurrentWeight(weight_raw);
//                Log.d(TAG, "uploadData success ");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.w(TAG, "uploadData failed :"+ databaseError.toException());
//            }
//        });

//                addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "uploadData: weight upload success");
//            }
//        });
//
//
//        BMIUpdate.put(time, bmi_raw);
//        BMI.put("BMI", BMIUpdate);
//        databaseReference.child("UserData").child(USERUID).updateChildren(BMIUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "uploadData: BMI upload success");
//            }
//        });
//
//
//        databaseReference.child("UserData").child(USERUID).child("currentWeight").setValue(weight_raw).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "uploadData: raw_weight upload success");
//            }
//        });
//
//
//        databaseReference.child("UserData").child(USERUID).child("currentBMI").setValue(bmi_raw).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "uploadData: raw_BMI upload success");
//            }
//        });
//
//



        displayData();



//        Log.d(TAG, "uploadData: weight: " + weight_raw);
//
//
//        String time = documentSs.getTimestamp("TIME").toDate().toString().substring(4, 10);
////        Log.d(TAG, "uploadData: Time is :"+ time);
//
//
//        weightUpdate.put(time, weight_raw);
//        WEIGHT.put("WEIGHT", weightUpdate);
//
//        BMIUpdate.put(time, bmi_raw);
//        BMI.put("BMI", BMIUpdate);
//
//
//        db.collection("UserData").document(userId).
//                set(WEIGHT, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "uploadData: weight upload success");
//                } else {
//                    Log.d(TAG, "uploadData: weight uploadData failed");
//                }
//
//            }
//        });
//
//        db.collection("UserData").document(userId).
//                set(BMI, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "uploadData: BMI upload success");
//                } else {
//                    Log.d(TAG, "uploadData: BMI uploadData failed");
//                }
//
//            }
//        });
//
//
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("currentWeight", weight_raw);
//        updates.put("currentBMI", bmi_raw);
////        updates.put("WEIGHT",weightUpdate);
////        updates.put("BMI",BMIUpdate);
//
//
//        DocumentReference doc = db.collection("UserData").document(userId);
//
//
//        if (doc == null) {
//            Log.d(TAG, "uploadData: cannot find targeted document");
//        }
//
//        doc.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "uploadData: upload success");
//                } else {
//                    Log.d(TAG, "uploadData: uploadData failed");
//                }
//            }
//        });
//
//
//        updates.clear();
//        displayData();
//

    }


    public void displayData() {
        textView_BMI.setText("BMI\n" + String.format("%.02f",bmi_raw));
        textView_WEIGHT.setText("Weight\n" + weight_raw);
    }


}
