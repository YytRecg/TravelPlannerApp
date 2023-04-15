package com.example.myapplication2.ui.dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.MyAdapter;
import com.example.myapplication2.RecyclerViewInterface;
import com.example.myapplication2.TravelPlanData;
import com.example.myapplication2.databinding.FragmentDashboardBinding;
import com.example.myapplication2.ui.activity.TravelPlanActivity;
import com.example.myapplication2.ui.activity.UserInfoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements RecyclerViewInterface {

    int days;
    String dest;
    TextView name, birthday;
    RecyclerView travelPlanList;
    List<TravelPlanData> items = new ArrayList<>();

    private FragmentDashboardBinding binding;
    private static final String FRAGMENT_NAME = DashboardFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference planRef = database.getReference("travel_plans");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String Uid = firebaseAuth.getUid();


        Log.i("D_LIFECYCLE", FRAGMENT_NAME + " onCreateView");

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button userInfoButton = binding.userInfoButton;

        name = binding.textViewName;
        birthday = binding.textViewBirthday;

        //things for travel plan list recycler view
        travelPlanList = binding.recyclerViewTravelPlans;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        MyAdapter myAdapter = new MyAdapter(this.getActivity(), items, this);

//        items.add(new TravelPlanData(7, "Paris", ""));

//        travelPlanList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//        travelPlanList.setAdapter(new MyAdapter(this.getActivity().getApplicationContext(), items));

//        // Retrieve "days" data from Firebase
//        planRef.child(Uid).child("1").child("days").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Handle retrieved data
//                days = dataSnapshot.getValue(int.class);
//                System.out.println("Days: " + days);
////                Log.d("D_travelPlans", Integer.toString(days));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle database error
//                System.err.println("Failed to retrieve data: " + databaseError.getMessage());
//            }
//        });
//        planRef.child(Uid).child("1").child("dest").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Handle retrieved data
//                dest = dataSnapshot.getValue(String.class);
////                System.out.println("Days: " + days);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle database error
//                System.err.println("Failed to retrieve data: " + databaseError.getMessage());
//            }
//        });
//
////        Log.d("D_travelPlans", Integer.toString(days));
//        System.out.println("Days!: " + days);


        userInfoButton.setOnClickListener((View v) -> {
            openUserInfoPage();
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Uid)) {
                    String nameData =  dataSnapshot.child(Uid).child("name").getValue(String.class);
                    String DOBData =  dataSnapshot.child(Uid).child("DOB").getValue(String.class);
                    name.setText(nameData);
                    birthday.setText(DOBData);
                } else {
                    openUserInfoPage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        planRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Uid)) {
                    long count = dataSnapshot.child(Uid).getChildrenCount();
                    System.out.println(count);
                    for (int i = 1; i < count+1; i++) {
                        Integer days =  dataSnapshot.child(Uid).child(Integer.toString(i)).child("days").getValue(Integer.class);
                        String dest =  dataSnapshot.child(Uid).child(Integer.toString(i)).child("dest").getValue(String.class);
                        String plans =  dataSnapshot.child(Uid).child(Integer.toString(i)).child("plans").getValue(String.class);
                        items.add(new TravelPlanData(days, dest, plans));
                    }
                    System.out.println(items.toString());
                    travelPlanList.setLayoutManager(linearLayoutManager);
                    travelPlanList.setAdapter(myAdapter);
                } else {
                    openUserInfoPage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

//        // things to display travel plans
//        planRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild(Uid)) {
//                    Log.d("D_travelPlans", "can read!");
//                    int days =  dataSnapshot.child(Uid).child("0").child("days").getValue(int.class);
//                    String dest =  dataSnapshot.child(Uid).child("0").child("dest").getValue(String.class);
//                    Log.d("D_travelPlans", Integer.toString(days));
//                    items.add(new TravelPlanData(days, dest, ""));
//                    travelPlanList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//                    travelPlanList.setAdapter(new MyAdapter(this.getActivity().getApplicationContext(), items));
//
//
//                } else {
//                    openUserInfoPage();
//                }
//            }

//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });





//        myR
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Post post = dataSnapshot.getValue(Post.class);
//                System.out.println(post);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });


//            final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        items.add(new TravelPlanData(days, dest, ""));
        return root;
    }

    private void openUserInfoPage(){
        Intent i = new Intent(getView().getContext(), UserInfoActivity.class);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onDestroyView");
        binding = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onCreate");
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onAttach");
//    }

    @Override
    public void onStart() {
        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onDestroy");
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(getView().getContext(), TravelPlanActivity.class);
        i.putExtra("DETAILS", items.get(position).getPlans());
        startActivity(i);

    }

//    @Override
//    public void onDetach() {
//        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onDetach");
//        super.onDetach();
}