package com.example.myapplication2.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myapplication2.FlickrAPI;
import com.example.myapplication2.LMPhoto;

import android.Manifest;
import android.widget.Toast;

import com.example.myapplication2.R;
import com.example.myapplication2.Utility;
import com.example.myapplication2.databinding.FragmentHomeBinding;

import com.example.myapplication2.ui.activity.GptActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;


import java.io.IOException;
import java.util.*;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ImageView lmImageView1, lmImageView2;
    private Button locationButton, searchButton;
    private FloatingActionButton detectButton ;
    private EditText userDestination;
    private int travelDays;
    private Chip tag1, tag2, tag3, tag4;
    private ChipGroup tags;
    private int page = 1;
    private List<Float> loc;
    private FlickrAPI flickrAPI;

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
        userDestination = binding.editTextDestination;
        tag1 = binding.chip1;
        tag2 = binding.chip2;
        tag3 = binding.chip3;
        tag4 = binding.chip4;
        tags = binding.chips;

        // Things for image generator
        lmImageView1 = binding.homeImageView1;

        tag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ((Chip) view).getText().toString();
                userDestination.setText(text);
            }
        });
        tag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ((Chip) view).getText().toString();
                userDestination.setText(text);
            }
        });
        tag3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ((Chip) view).getText().toString();
                userDestination.setText(text);
            }
        });
        tag4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ((Chip) view).getText().toString();
                userDestination.setText(text);
            }
        });

        searchButton.setOnClickListener((View v) -> {
            if (!Utility.isNetworkConnected(this.getActivity())) {
                Utility.showDialog(this.getActivity(),"Error", "No Internet!");
            }else{
                FlickrAPI.setPage(page);
                if (userDestination.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Random Landmarks", Toast.LENGTH_SHORT).show();
                    flickrAPI = new FlickrAPI(this, "beautiful scenary");
                }else{
                    flickrAPI = new FlickrAPI(this, userDestination.getText().toString());
                }
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
                    loc=nextPhoto();
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
                showInputDialogNearby();
            }
        });

        detectButton.setOnClickListener(v -> {
            if (!Utility.isNetworkConnected(this.getActivity())) {
                Utility.showDialog(this.getActivity(),"Error", "No Internet!");
            }else {
                showInputDialogDetect();
            }
        });

        return root;
    }

    public void showInputDialogNearby() {
        // Create a new AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(com.example.myapplication2.R.layout.dialog_input, null);
        builder.setView(dialogView);

        // Get a reference to the EditText view in the layout
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the positive button to save the user input and dismiss the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the user input from the EditText view
                String userInput = editText.getText().toString();

                // Do something with the user input
                Toast.makeText(getActivity(), "Number of days: " + userInput, Toast.LENGTH_SHORT).show();
                try {
                    travelDays = Integer.parseInt(userInput);
                    if (travelDays > 0){
                        getLastLocation();
                    }else{
                        Toast.makeText(getActivity(), "Warning: Invalid #Days!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e){
                    Toast.makeText(getActivity(), "Warning: Invalid #Days!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showInputDialogDetect() {
        // Create a new AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(com.example.myapplication2.R.layout.dialog_input, null);
        builder.setView(dialogView);

        // Get a reference to the EditText view in the layout
        EditText editText = dialogView.findViewById(R.id.editText);

        // Set the positive button to save the user input and dismiss the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the user input from the EditText view
                String userInput = editText.getText().toString();

                // Do something with the user input
                Toast.makeText(getActivity(), "Number of days: " + userInput, Toast.LENGTH_SHORT).show();
                try {
                    travelDays = Integer.parseInt(userInput);
                    if (travelDays > 0){
                        getImageLocation();
                    }else{
                        Toast.makeText(getActivity(), "Warning: Invalid #Days!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e){
                    Toast.makeText(getActivity(), "Warning: Invalid #Days!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void getImageLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation((double) loc.get(0), (double) loc.get(1), 1);
//                        longitude.setText("Longitude: " + addresses.get(0).getLongitude());
//                        latitude.setText("Latitude: " + addresses.get(0).getLatitude());
//                        address.setText("Address: " + addresses.get(0).getAddressLine(0));
//                        city.setText("City: " + addresses.get(0).getLocality());
//                        country.setText("Country: " + addresses.get(0).getCountryName());
//                        int days = 7;
//                        try {
//                            days = Integer.parseInt(numDays.getText().toString());
//                        } catch (NumberFormatException e){
//                            Utility.showDialog(this.getActivity(),"Warning","Invalid #Days!");
//                        }
                        GptActivity.setDays(travelDays);
                        Intent i = new Intent(this.getActivity(), GptActivity.class);
                        String state = addresses.get(0).getAdminArea();
                        String city = addresses.get(0).getLocality();
                        i.putExtra("area", city + ", " + state);
                        i.putExtra("lat", (double) loc.get(0));
                        i.putExtra("lon", (double) loc.get(1));
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

    public void receivedLMPhotos(List<LMPhoto> lmPhotoList) {
        this.lmPhotoList = lmPhotoList;
        if (lmPhotoList != null && lmPhotoList.size() > 0){
            currPhotoIdx++;
            loc=nextPhoto();
        }
    }

    private List<Float> nextPhoto() {
        List<Float> location = new ArrayList<>();
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
                float lat = lmPhoto.getLat();
                float lon = lmPhoto.getLon();
                location.add(lat);
                location.add(lon);
                if ((lat != 0) && (lon != 0)) {
                    detectButton.setVisibility(View.VISIBLE);
        }else{
                    detectButton.setVisibility(View.INVISIBLE);
                }
                return location;


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
//                        int days = 7;
//                        try {
//                            days = Integer.parseInt(numDays.getText().toString());
//                        } catch (NumberFormatException e){
//                            Utility.showDialog(this.getActivity(),"Warning","Invalid #Days!");
//                        }
                        GptActivity.setDays(travelDays);
                        Intent i = new Intent(this.getActivity(), GptActivity.class);
                        String state = addresses.get(0).getAdminArea();
                        String city = addresses.get(0).getLocality();
                        i.putExtra("area", city + ", " + state);
                        i.putExtra("lat", location.getLatitude());
                        i.putExtra("lon", location.getLongitude());
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