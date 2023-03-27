package com.example.myapplication2.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication2.databinding.FragmentHomeBinding;

import com.google.firebase.database.*;
import com.google.android.gms.tasks.*;

import java.util.*;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        Log.d("D_LIFECYCLE", "Home Fragment onCreateView");

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button searchButton = binding.searchButton;
        final EditText userDestination = binding.editTextDestination;

        return root;
    }

    private void writeNewPost(DatabaseReference myRef, String userBio, String username, String photo) {

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/bio/", userBio);
        childUpdates.put("/name/", username);
        childUpdates.put("/photo/", photo);
        myRef.push().updateChildren(childUpdates);
    }

    @IgnoreExtraProperties
    public class User {

        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}