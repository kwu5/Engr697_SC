package com.example.smartscale;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class inputUserInfo extends AppCompatActivity {

    EditText editText_height;
    EditText editText_age;
    Button button_enter;

    //todo data fields
    double height;
        int age;

    private final String TAG = "inputUserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_user_info);

        Log.d(TAG, "<InputUserInfo>: start ");

        editText_height = findViewById(R.id.heightIn);
        editText_age = findViewById(R.id.ageIn);




        button_enter = findViewById(R.id.enter);
        button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_height.getText().toString().isEmpty() || editText_age.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter the Data",Toast.LENGTH_SHORT).show();

                }else{

                    //todo data fields
                    height = Double.parseDouble(editText_height.getText().toString());
                    age = Integer.parseInt(editText_age.getText().toString());


                    Intent intent = new Intent();
                    intent.putExtra("height",height);
                    intent.putExtra("age",age);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }
            }
        });





    }
}
