package com.example.myapplication2.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myapplication2.FlickrAPI;
import com.example.myapplication2.LMPhoto;
import com.example.myapplication2.R;
import com.example.myapplication2.databinding.FragmentHomeBinding;

import com.google.firebase.database.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    ImageView lmImageView;

    List<LMPhoto> lmPhotoList;
    int currPhotoIdx = -1;

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