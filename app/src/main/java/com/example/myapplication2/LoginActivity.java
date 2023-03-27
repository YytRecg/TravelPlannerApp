package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World!");
    }

    public void launchTest(View v) {
        Intent i = new Intent(this, UserInfoActivity.class);
        startActivity(i);
    }

    public void launchMain(View v) {
        Intent i = new Intent(this, TestActivity.class);
        startActivity(i);
    }
}