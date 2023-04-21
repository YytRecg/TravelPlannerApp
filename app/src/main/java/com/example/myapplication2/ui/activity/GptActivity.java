package com.example.myapplication2.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.BuildConfig;
import com.example.myapplication2.FlickrAPI;
import com.example.myapplication2.R;
import com.example.myapplication2.TravelPlanData;
import com.example.myapplication2.UserData;
import com.example.myapplication2.Utility;
import com.example.myapplication2.databinding.ActivityGptBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GptActivity extends AppCompatActivity implements OnMapReadyCallback{

    private ActivityGptBinding binding;
    private TextView gptTextView;
    private Button saveButton;
    private boolean ifPermissionGranted;
    private MapView mapView;
    private GoogleMap mMap;
    private double mLatitude = 37.7749; // example latitude
    private double mLongitude = -122.4194; // example longitude
    private Marker mMarker;

    public static int getDays() {
        return days;
    }

    public static void setDays(int days) {
        GptActivity.days = days;
    }

    public static int days = 7;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gptTextView = binding.gptText;
        gptTextView.setMovementMethod(new ScrollingMovementMethod());
        saveButton = binding.planSaveButton;
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setEnabled(false);

        // Things for store travel plan
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("travel_plans");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String Uid = firebaseAuth.getUid();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Log.d("D_gpt", "yes extras");
            // map view
            mLatitude = extras.getDouble("lat");
            mLongitude = extras.getDouble("lon");
            Log.d("D_location", "lat/lon: "+mLatitude+"/"+mLongitude);
            mapView = binding.mapView;
            checkMyPermission();
            if (ifPermissionGranted) {
                mapView.getMapAsync(this);
                mapView.onCreate(savedInstanceState);

            }
            // travel plan recommendation
            String area = extras.getString("area");
            String question = "In the following format, generate a " + Integer.toString(days) + " day travel plan for " + area + ".\n" +
                    "Day:\n" +
                    "Location:\n" +
                    "Summary:";
            callGPTApi(question);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    writeTravelPlans(myRef, Uid, area, gptTextView.getText().toString());
                    Toast.makeText(GptActivity.this, "Travel plan added!", Toast.LENGTH_SHORT).show();
//                    Utility.showDialog(GptActivity.this, "Notification","Travel plan added!");
                }
            });
        } else {
            Log.d("D_gpt", "no extras");
        }

    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                Toast.makeText(GptActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                ifPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();
    }

    private void callGPTApi(String question) {
        Log.d("D_GPT", "question " + question);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("prompt", question);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer " + BuildConfig.GPT_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("D_GPT", e.getMessage());
                gptTextView.setText("Timeout, please try again:( (try a shorter plan)");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("D_GPT", "onResponse 1");

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonRes = new JSONObject(response.body().string());
                        JSONArray jsonArr = jsonRes.getJSONArray("choices");
                        String res = jsonArr.getJSONObject(0).getString("text");
                        Log.d("D_GPT", res);
                        runOnUiThread(() -> gptTextView.setText(res));
                        // Update the UI on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set the visibility of the button to "visible"
                                saveButton.setVisibility(View.VISIBLE);
                                saveButton.setEnabled(true);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                } else {
                    Log.d("D_GPT", "onResponse failure");
                    Log.d("D_GPT", response.toString());
                }
            }
        });

    }

    private void writeTravelPlans(DatabaseReference myRef, String Uid, String area, String res){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("users");
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        String Uid = firebaseAuth.getUid();

        myRef.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Map<String, TravelPlanData> childUpdates = new HashMap<>();
                myRef.child(Uid).child(Long.toString(count+1)).setValue(new TravelPlanData(days, area, res));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
//        writeUser(myRef, days, res, myRef.Uid);

//        myRef.child("datetime").setValue(strDate);


//            create new user
//            writeNewPost(myRef, "", "yuting2", "");

//            remove data
//        myRef.child("-NR0KYPZv0wX3yLsv69M").child("name").removeValue();

//            set data
//            myRef.child("-NR0KYPZv0wX3yLsv69M").child("name").setValue(userDestination.getText().toString());


//            retrieve data
//            myRef.child("-NR0KYPZv0wX3yLsv69M").child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    }
//                    else {
//                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                        userDestination.setText(String.valueOf(task.getResult().getValue()));
//                    }
//                }
//            });
    }


    private void writeUser(DatabaseReference myRef, String dest, String plans, String Uid, String Pid) {

        Map<String, TravelPlanData> childUpdates = new HashMap<>();
        myRef.child(Uid).child(Pid).setValue(new TravelPlanData(days, dest, plans));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the specified location and move the camera
        LatLng location = new LatLng(mLatitude, mLongitude);
        mMarker = mMap.addMarker(new MarkerOptions().position(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}