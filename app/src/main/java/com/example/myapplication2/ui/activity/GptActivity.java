package com.example.myapplication2.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication2.BuildConfig;
import com.example.myapplication2.R;
import com.example.myapplication2.TravelPlanData;
import com.example.myapplication2.UserData;
import com.example.myapplication2.Utility;
import com.example.myapplication2.databinding.ActivityGptBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class GptActivity extends AppCompatActivity {

    private ActivityGptBinding binding;
    private TextView gptTextView;
    private int days = 7;

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

        // Things for store travel plan
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("travel_plans");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String Uid = firebaseAuth.getUid();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Log.d("D_gpt", "yes extras");
            String area = extras.getString("area");
            String question = "In the following format, generate a "+Integer.toString(days)+" day travel plan for " + area + ".\n" +
                    "Day:\n" +
                    "Location:\n" +
                    "Summary:";
            callGPTApi(question);
            String res = gptTextView.getText().toString();
            // Things to store travel plans
            writeTravelPlans(myRef, Uid, area, res);
        } else {
            Log.d("D_gpt", "no extras");
        }
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
}