package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<TravelPlanData> items;

    public MyAdapter(Context context, List<TravelPlanData> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.travel_plan_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.destView.setText(items.get(position).getDest());
        holder.daysView.setText(Integer.toString(items.get(position).getDays()));
        holder.imageView.setImageResource(R.drawable.img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
