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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class measurements extends AppCompatActivity {

    private Button button_measurement, button_trends, button_myAccount,button_start;
    private TextView textView_BMI;

    private static final String TAG = "Measurement";



    private static boolean isuserInfoSet = false;

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();


    private boolean nUser = true;
    private static boolean nLogin = true;


    private double BMI = 0;

    //id for testing
    private final String sampleID = "sampleUser";
    private final float sampleHeight = 110;

    private static String USERUID;
    private double WEIGHT;
    private double HEIGHT;
    private int AGE;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Log.d(TAG, "onCreate: <" + TAG + "> starts. ");


        //initialize common widgets
        button_measurement = findViewById(R.id.measurements);
        button_myAccount = findViewById(R.id.myAccount);
        button_trends = findViewById(R.id.trends);
        button_start = findViewById(R.id.start);

        textView_BMI = findViewById(R.id.BMI);


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

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //todo
                if(isuserInfoSet){
                    changeDocument();

                }
            }
        });



        if(nLogin) {
            setUserInfo();
            nLogin = false;
        }

        textView_BMI.setText(String.valueOf(BMI));


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
                        if(task.isSuccessful()) {
                            Log.d(TAG, "List all userData below: ");
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d(TAG, TAG + "-- user read :" + documentSnapshot.getData());
                                if(documentSnapshot.get("USERUID").equals(USERUID)){
                                    nUser = false;
                                    Log.d(TAG, "setUserInfo: user info found: "+ documentSnapshot.getData());
                                    Log.d(TAG, "setUserInfo: id is  "+ documentSnapshot.getId());

                                    isuserInfoSet = true;

                                    break;
                                }
                            }

                            //create new user document
                            if(nUser){
                                goInputUserInfo();







//                                db.collection("UserData").add(data)
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                            @Override
//                                            public void onSuccess(DocumentReference documentReference) {
//                                                Log.d(TAG, "setUserInfo :new user Document created for user : "+ data.get("USERUID"));
//
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w(TAG, "setUserInfo : Error when create new user document:  ",e );
//                                            }
//                                        });
                            }
//
                        }else{
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
    public void goInputUserInfo(){
            Intent goInputUserInfo = new Intent();
            goInputUserInfo.setClass(this,inputUserInfo.class);

            startActivityForResult(goInputUserInfo,1);

        }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {

               createNewUserDoc(data);
               isuserInfoSet = true;



            }else{
                Log.d(TAG, "onActivityResult: value not return");
            }
        }
    }

    /**
     * 1.checkpause
     * 2.put in listener

    private void checkPause(){

        if(){
            try {
                Thread.sleep(300);
            }catch (Exception e){
                Log.d(TAG, "checkPause: Exception"+ e.getMessage());
            }
        }

    }
     */

    public void createNewUserDoc(Intent data){


        HEIGHT = data.getDoubleExtra("height",0);
        AGE = data.getIntExtra("age",0);
        Log.d(TAG, "createNewUserDoc: value return");
        Log.d(TAG, "createNewUserDoc: Height:"+ HEIGHT);
        Log.d(TAG, "createNewUserDoc: Age:"+ AGE);


        Map<String, Object> nData = new HashMap<>();

        BMI = 0;
        WEIGHT = 0;

        //todo ask user for info
        nData.put("HEIGHT",HEIGHT);
        nData.put("USERUID", USERUID);
        nData.put("WEIGHT", WEIGHT);
        nData.put("BMI",BMI);
        nData.put("AGE",AGE);

        DocumentReference newUser = db.collection("UserData").document(USERUID);
        newUser.set(nData);



    }






    /**
     * 1. Get raw_data from FireBase
     * 2. Perform calculation due to asynchronous
     *
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


    /**
     * 1.Perform calculation
     * 2.call upload method
     * @param documentSs
     * @param USERUID
     */
    public void calculation(final DocumentSnapshot documentSs, final String USERUID) {


        try{

            WEIGHT = documentSs.getDouble("WEIGHT").intValue();

            final DocumentReference documentRef = db.collection("UserData").document(USERUID);
            documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    double height =  documentSnapshot.getDouble("HEIGHT");

                    BMI = WEIGHT/ (float) Math.pow(height,2);
                    Log.d(TAG, "calculation: BMI is "+ BMI);

                    uploadData(documentSs,USERUID);
                }
            });



        }catch (Exception e){
            Log.d(TAG, "calculation: Error initializing weight and height: "+ e.getMessage());
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
        Log.d(TAG, "uploadData: weight: "+ weight);

        Map <String,Object> updates = new HashMap<>();
        updates.put("WEIGHT",weight);
        updates.put("BMI",BMI);

        DocumentReference doc = db.collection("UserData").document(userId);

        if(doc == null){
            Log.d(TAG, "uploadData: cannot find targeted document");
        }

        doc.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        textView_BMI.setText(String.valueOf((float)BMI));
    }





}
