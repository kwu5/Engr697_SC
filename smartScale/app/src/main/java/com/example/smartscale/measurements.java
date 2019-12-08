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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class measurements extends AppCompatActivity {

    private Button button_measurement, button_trends, button_myAccount, button_start;
    private TextView textView_BMI, textView_WEIGHT;

    private static final String TAG = "Measurement";


    private static boolean isuserInfoSet = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private boolean nUser = true;
    private static boolean nLogin = true;

    private Map<String, Integer> weightUpdate = new HashMap<>();
    private Map<String, Float> BMIUpdate = new HashMap<>();

    private Map<Object, Object> WEIGHT = new HashMap<>();
    private Map<Object, Object> BMI = new HashMap<>();


    //id for testing
    private final String sampleID = "sampleUser";
    private final float sampleHeight = 110;

    private static String USERUID;

    private int weight_raw = 0;
    private float bmi_raw = 0f;


    private double HEIGHT;
    private int AGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Log.d(TAG, "onCreate: <" + TAG + "> starts. ");


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

                //todo
                if (isuserInfoSet) {
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
        db.collection("UserData").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            Log.d(TAG, "List all userData below: ");
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                Log.d(TAG, TAG + "-- user read :" + documentSnapshot.getData());
                                if (documentSnapshot.get("USERUID").equals(USERUID)) {
                                    nUser = false;
                                    Log.d(TAG, "setUserInfo: user info found: " + documentSnapshot.getData());
                                    Log.d(TAG, "setUserInfo: id is  " + documentSnapshot.getId());


                                    isuserInfoSet = true;


                                    break;
                                }
                            }

                            //create new user document
                            if (nUser) {
                                goInputUserInfo();
                            }
//
                        } else {
                            Log.d(TAG, TAG + " Error getting documents: ", task.getException());
                        }
//
                    }
                });
//


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
                isuserInfoSet = true;


            } else {
                Log.d(TAG, "onActivityResult: value not return");
            }
        }
    }

    /**
     * 1.checkpause
     * 2.put in listener
     * <p>
     * private void checkPause(){
     * <p>
     * if(){
     * try {
     * Thread.sleep(300);
     * }catch (Exception e){
     * Log.d(TAG, "checkPause: Exception"+ e.getMessage());
     * }
     * }
     * <p>
     * }
     */

    public void createNewUserDoc(Intent data) {


        HEIGHT = data.getDoubleExtra("height", 0);
        AGE = data.getIntExtra("age", 0);

        Log.d(TAG, "createNewUserDoc: value return");
        Log.d(TAG, "createNewUserDoc: Height:" + HEIGHT);
        Log.d(TAG, "createNewUserDoc: Age:" + AGE);


        Map<String, Object> nData = new HashMap<>();

//        WEIGHT = new HashMap<>();
//        BMI= new HashMap<>();


        //todo ask user for info
        nData.put("HEIGHT", HEIGHT);
        nData.put("USERUID", USERUID);
        nData.put("AGE", AGE);
        nData.put("WEIGHT", WEIGHT);
        nData.put("BMI", BMI);


        DocumentReference newUser = db.collection("UserData").document(USERUID);
        newUser.set(nData);


    }


    /**
     * 1. Get raw_data from FireBase
     * 2. Perform calculation due to asynchronous
     */
    public void changeDocument() {

        DocumentReference docRef = db.collection("ESP_SS_T").document("WEIGHT_RAW_T");


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSs = task.getResult();
                    if (documentSs.exists()) {
                        Log.d(TAG, "changeDocument: DocumentSnapshot Data: " + documentSs.getData());

                        calculation(documentSs, USERUID);

                    } else {
                        Log.d(TAG, "changeDocument: No such document");
                    }
                } else {
                    Log.w(TAG, "changeDocument: get failed with ", task.getException());
                }

            }
        });
    }


    /**
     * 1.Perform calculation
     * 2.call upload method
     *
     * @param documentSs
     * @param USERUID
     */
    public void calculation(final DocumentSnapshot documentSs, final String USERUID) {


        try {


            final DocumentReference documentRef = db.collection("UserData").document(USERUID);
            documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    double height = documentSnapshot.getDouble("HEIGHT");

                    weight_raw = documentSs.getDouble("WEIGHT").intValue();
                    Log.d(TAG, "calculation:Weight :" + weight_raw);
                    bmi_raw = weight_raw / (float) Math.pow(height, 2);
                    Log.d(TAG, "calculation: BMI is " + bmi_raw);

                    uploadData(documentSs, USERUID);
                }
            });


        } catch (Exception e) {
            Log.d(TAG, "calculation: Error initializing weight and height: " + e.getMessage());
        }


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
    public void uploadData(DocumentSnapshot documentSs, String userId) {


        Log.d(TAG, "uploadData: weight: " + weight_raw);


        String time = documentSs.getTimestamp("TIME").toDate().toString().substring(4, 10);
//        Log.d(TAG, "uploadData: Time is :"+ time);


        weightUpdate.put(time, weight_raw);
        WEIGHT.put("WEIGHT", weightUpdate);

        BMIUpdate.put(time, bmi_raw);
        BMI.put("BMI", BMIUpdate);


        db.collection("UserData").document(userId).
                set(WEIGHT, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "uploadData: weight upload success");
                } else {
                    Log.d(TAG, "uploadData: weight uploadData failed");
                }

            }
        });

        db.collection("UserData").document(userId).
                set(BMI, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "uploadData: BMI upload success");
                } else {
                    Log.d(TAG, "uploadData: BMI uploadData failed");
                }

            }
        });


        Map<String, Object> updates = new HashMap<>();
        updates.put("currentWeight", weight_raw);
        updates.put("currentBMI", bmi_raw);
//        updates.put("WEIGHT",weightUpdate);
//        updates.put("BMI",BMIUpdate);


        DocumentReference doc = db.collection("UserData").document(userId);


        if (doc == null) {
            Log.d(TAG, "uploadData: cannot find targeted document");
        }

        doc.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "uploadData: upload success");
                } else {
                    Log.d(TAG, "uploadData: uploadData failed");
                }
            }
        });


        updates.clear();
        displayData();


    }


    public void displayData() {
        textView_BMI.setText("BMI\n" + bmi_raw);
        textView_WEIGHT.setText("Weight\n" + weight_raw);
    }


}
