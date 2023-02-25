package com.example.myapplication2.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication2.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private static final String FRAGMENT_NAME = DashboardFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        Log.i("D_LIFECYCLE", FRAGMENT_NAME + " onCreateView");

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
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

//    @Override
//    public void onDetach() {
//        Log.i("D_LIFECYCLE", FRAGMENT_NAME +" onDetach");
//        super.onDetach();
//    }

}