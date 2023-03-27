package com.example.myapplication2.ui.home;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import com.example.myapplication2.databinding.FragmentHomeBinding;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.*;
import com.google.android.gms.tasks.*;


import java.io.IOException;
import java.util.*;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

//    Thingies for get location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView longitude, latitude, address, city, country;
    private Button locationButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        Log.d("D_LIFECYCLE", "Home Fragment onCreateView");

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button searchButton = binding.searchButton;
        final EditText userDestination = binding.editTextDestination;

//        Thingies for get location
        longitude = binding.longitude;
        latitude = binding.latitude;
        address = binding.address;
        city = binding.city;
        country = binding.country;
        locationButton = binding.locationButton;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        locationButton.setOnClickListener(v -> {
            getLastLocation();
        });

        return root;
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        longitude.setText("Longitude: " + addresses.get(0).getLongitude());
                        latitude.setText("Latitude: " + addresses.get(0).getLatitude());
                        address.setText("Longitude: " + addresses.get(0).getAddressLine(0));
                        city.setText("Longitude: " + addresses.get(0).getLocality());
                        country.setText("Longitude: " + addresses.get(0).getCountryName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission();
        } else {
            requestPermission();
        }
    }

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Log.d("D_MIKE", "requestPermission 7");
                Boolean fineLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false
                );
                Boolean coarseLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION, false
                );
                if (fineLocationGranted != null && fineLocationGranted) {
                    // Precise location access granted.
                    Log.d("D_MIKE", "requestPermission 2");
                    getLastLocation();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    // Only approximate location access granted.
                    Log.d("D_MIKE", "requestPermission 3");
                    getLastLocation();
                } else {
                    Log.d("D_MIKE", "requestPermission 4");
                    // No location access granted.
                }
            });

    private void requestPermission() {
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
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