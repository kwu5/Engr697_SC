package com.example.smartscale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Arrays;
import java.util.List;

public class FirebaseUIActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private final String TAG = "FireBaseUIActivity";

    private String currentUserUid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        Log.d(TAG, "onCreate: FireBaseUIActivity start");
        signOut();
        createSignInIntent();




        }


    /**
     * 1.initialize Firebase
     * 2.perform googleSignInActivity
     */
    public void createSignInIntent() {


        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());
        Log.d(TAG, "signIn: build successful");



        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]


    }


    /**
     * 1. get current user info if login successful
     *
     * 2. start <measurement> if is logined
     * @param requestCode
     * @param resultCode
     * @param data
     */
    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            Log.d(TAG, "onActivityResult: RC_SIGN_IN");

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Log.d(TAG, "onActivityResult: current user uid: + "+ currentUserUid);
                Log.d(TAG, "onActivityResult: sign in successful");





                //start new activity
                Intent goMeasurement = new Intent();
                goMeasurement.putExtra("userUID", currentUserUid);
                goMeasurement.setClass(this, measurements.class);
                startActivity(goMeasurement);

                finish();

            } else {

                if(response == null){
                    Log.w(TAG, "onActivityResult: response is null ");


                }else{

                    Log.d(TAG, "onActivityResult:   Error code:" + response.getError().getErrorCode() );
                }
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

            }
        }
    }
    // [END auth_fui_result]


    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: Sign out successful");
                    }
                });
        // [END auth_fui_signout]
    }


}
