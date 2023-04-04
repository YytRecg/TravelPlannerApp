package com.example.myapplication2.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;

import com.example.myapplication2.R;
import com.example.myapplication2.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

public class UserInfoActivity extends AppCompatActivity {

    EditText DOB;
    EditText Username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String Uid = firebaseAuth.getUid();

        DOB = (EditText)findViewById(R.id.userDOB_text);
        Username = (EditText)findViewById(R.id.name_text);
        Button createUserButton = (Button)findViewById(R.id.add_user_button);

        final boolean[] hasProfile = {false};

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hasProfile[0] = dataSnapshot.hasChild(Uid);
                String nameData =  dataSnapshot.child(Uid).child("name").getValue(String.class);
                Username.setText(nameData);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readUserInfo(myRef, Uid);
                if (hasProfile[0]) {
                    showDialog("Notification", "Changed profile!");
                } else {
                    showDialog("Notification","Added profile!");
                }
            }
        });
        //add text changed listener
        DOB.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    DOB.setText(current);
                    DOB.setSelection(sel < current.length() ? sel : current.length());



                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });


    }
    private void showDialog(String title, String msg){
        AlertDialog dialog = new AlertDialog.Builder(UserInfoActivity.this).setTitle(title).setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        dialog.show();
    }
    private void readUserInfo(DatabaseReference myRef, String Uid){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("users");
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        String Uid = firebaseAuth.getUid();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int year = Integer.parseInt(DOB.getText().toString().substring(6));
        int month = Integer.parseInt(DOB.getText().toString().substring(3, 5)) - 1;
        int day = Integer.parseInt(DOB.getText().toString().substring(0, 2));

//        int year = Integer.parseInt("1999");
//        int month = Integer.parseInt("12")-1;
//        int day = Integer.parseInt("12");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        Date date = cal.getTime();
        System.out.print(date);


//        try {
//            inActiveDate = format1.format(date);
//            System.out.println(inActiveDate );
//        } catch (ParseException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
        String strDate = dateFormat.format(date).toString();
        writeUser(myRef, Username.getText().toString(), strDate, Uid);
        
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


    private void writeUser(DatabaseReference myRef, String username, String userDOB, String Uid) {

        Map<String, UserData> childUpdates = new HashMap<>();
        myRef.child(Uid).setValue(new UserData(userDOB, username));
    }

}