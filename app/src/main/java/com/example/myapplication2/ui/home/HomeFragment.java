package com.example.myapplication2.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myapplication2.FlickrAPI;
import com.example.myapplication2.LMPhoto;
import com.example.myapplication2.R;
import android.Manifest;
import com.example.myapplication2.Utility;
import com.example.myapplication2.databinding.FragmentHomeBinding;

import com.example.myapplication2.ui.activity.GptActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.*;
import com.google.android.gms.tasks.*;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    // Things for image generator
    ImageView lmImageView;

    List<LMPhoto> lmPhotoList;
    int currPhotoIdx = -1;

    // Thingies for get location
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
        final Button nextImgButton = binding.nextImgButton;
        final EditText userDestination = binding.editTextDestination;

        // Things for image generator
        lmImageView = binding.homeImageView;
//        FlickrAPI flickrAPI = new FlickrAPI(this);
//        flickrAPI.fetchLMImages();

//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FlickrAPI flickrAPI = new FlickrAPI(this);
//                flickrAPI.fetchLMImages();
//                nextImgButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        nextPhoto();
//                    }
//                });
//            }
//        });
//
        searchButton.setOnClickListener((View v) -> {
            FlickrAPI flickrAPI = new FlickrAPI(this, userDestination.getText().toString());
            flickrAPI.fetchLMImages();
        });

        nextImgButton.setOnClickListener((View v) -> {
            nextPhoto();
        });

        // Thingies for get location
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

    public void receivedLMPhotos(List<LMPhoto> lmPhotoList) {
        this.lmPhotoList = lmPhotoList;
        nextPhoto();
    }

    private void nextPhoto() {
        if (lmPhotoList != null && lmPhotoList.size() > 0){
            currPhotoIdx++;
            currPhotoIdx %= lmPhotoList.size();

            LMPhoto lmPhoto = lmPhotoList.get(currPhotoIdx);

//            Log.d("flickr", "current lmPhoto :"+lmPhoto.toString());
            String imageUrl = lmPhoto.getPhotoURL();

            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(lmImageView);
//            FlickrAPI flickrAPI = new FlickrAPI(this);
//            flickrAPI.fetchPhotoBitmap(lmPhoto.getPhotoURL());
        }
    }

//    private void openUnsplashPage(){
//        Intent i = new Intent(getView().getContext(), UnsplashAuthActivity.class);
//        startActivity(i);
//    }


//    public void receivedPhotoBitmap(Bitmap bitmap) {
//        ImageView lmImageView = binding.homeImageView;
//        lmImageView.setImageBitmap(bitmap);
//    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        longitude.setText("Longitude: " + addresses.get(0).getLongitude());
                        latitude.setText("Latitude: " + addresses.get(0).getLatitude());
                        address.setText("Address: " + addresses.get(0).getAddressLine(0));
                        city.setText("City: " + addresses.get(0).getLocality());
                        country.setText("Country: " + addresses.get(0).getCountryName());
                        Intent i = new Intent(this.getActivity(), GptActivity.class);
                        String state = addresses.get(0).getAdminArea();
                        String city = addresses.get(0).getLocality();
                        i.putExtra("area", city + ", " + state);
                        startActivity(i);
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