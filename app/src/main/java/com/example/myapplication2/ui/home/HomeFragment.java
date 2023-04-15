package com.example.myapplication2.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.target.SimpleTarget;
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
    private ImageView lmImageView1, lmImageView2;
    private Button locationButton, searchButton, detectButton;
    private EditText userDestination, numDays;
    private int page = 1;

    List<LMPhoto> lmPhotoList;
    int currPhotoIdx = -1;

    // Thingies for get location
    private FusedLocationProviderClient fusedLocationProviderClient;
//    private TextView longitude, latitude, address, city, country;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        Log.d("D_LIFECYCLE", "Home Fragment onCreateView");

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        searchButton = binding.searchButton;
        detectButton = binding.detectButton;
//        final Button nextImgButton = binding.nextImgButton;
        userDestination = binding.editTextDestination;
        numDays = binding.editTextDays;

        // Things for image generator
        lmImageView1 = binding.homeImageView1;
        lmImageView2 = binding.homeImageView2;
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
            if (!Utility.isNetworkConnected(this.getActivity())) {
                Utility.showDialog(this.getActivity(),"Error", "No Internet!");
            }else{
                FlickrAPI.setPage(page);
                FlickrAPI flickrAPI = new FlickrAPI(this, userDestination.getText().toString());
                flickrAPI.fetchLMImages();
            }
        });

        lmImageView1.setOnClickListener((View v) -> {
            if (lmPhotoList != null && lmPhotoList.size() > 0) {
                currPhotoIdx++;
                if (currPhotoIdx >= lmPhotoList.size()) {
                    page++;
                    FlickrAPI.setPage(page);
                    FlickrAPI flickrAPI = new FlickrAPI(this, userDestination.getText().toString());
                    flickrAPI.fetchLMImages();
                } else {
                    nextPhoto1();
                }
            }

        });


        // Thingies for get location
//        longitude = binding.longitude;
//        latitude = binding.latitude;
//        address = binding.address;
//        city = binding.city;
//        country = binding.country;
        locationButton = binding.locationButton;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        locationButton.setOnClickListener(v -> {
            if (!Utility.isNetworkConnected(this.getActivity())) {
                Utility.showDialog(this.getActivity(),"Error", "No Internet!");
            }else {
                getLastLocation();
            }
        });

        return root;
    }

    public void receivedLMPhotos(List<LMPhoto> lmPhotoList) {
        this.lmPhotoList = lmPhotoList;
        if (lmPhotoList != null && lmPhotoList.size() > 0){
            currPhotoIdx++;
            nextPhoto1();
        }
    }

    private void nextPhoto1() {
//        if (lmPhotoList != null && lmPhotoList.size() > 0){
//            currPhotoIdx++;
            currPhotoIdx %= lmPhotoList.size();
                LMPhoto lmPhoto = lmPhotoList.get(currPhotoIdx);

//            Log.d("flickr", "current lmPhoto :"+lmPhoto.toString());
                String imageUrl = lmPhoto.getPhotoURL();

                Glide.with(this)
                        .load(imageUrl)
                        .centerCrop()
                        .into(lmImageView1);

//            FlickrAPI flickrAPI = new FlickrAPI(this);
//            flickrAPI.fetchPhotoBitmap(lmPhoto.getPhotoURL());
            }
//        }

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
//                        longitude.setText("Longitude: " + addresses.get(0).getLongitude());
//                        latitude.setText("Latitude: " + addresses.get(0).getLatitude());
//                        address.setText("Address: " + addresses.get(0).getAddressLine(0));
//                        city.setText("City: " + addresses.get(0).getLocality());
//                        country.setText("Country: " + addresses.get(0).getCountryName());
                        int days = 7;
                        try {
                            days = Integer.parseInt(numDays.getText().toString());
                        } catch (NumberFormatException e){
                            Utility.showDialog(this.getActivity(),"Warning","Invalid #Days!");
                        }
                        GptActivity.setDays(days);
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