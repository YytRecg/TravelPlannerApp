package com.example.myapplication2;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView destView, daysView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.travelPlanImage);
        destView = itemView.findViewById(R.id.destination);
        daysView = itemView.findViewById(R.id.days);
    }
}
