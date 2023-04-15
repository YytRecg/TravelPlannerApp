package com.example.myapplication2.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.myapplication2.R;

public class TravelPlanActivity extends AppCompatActivity {

    private TextView travelPlanTextView;
//    private String des = "";


//    public void setDes(String des) {
//        this.des = des;
//    }
//    public String getDes() {
//        return des;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_plan);

        String travelPlanDetails = getIntent().getStringExtra("DETAILS");
        travelPlanTextView = findViewById(R.id.textView_travel_plan);
        travelPlanTextView.setMovementMethod(new ScrollingMovementMethod());
        travelPlanTextView.setText(travelPlanDetails);
    }


}