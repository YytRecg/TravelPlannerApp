package com.example.myapplication2.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.MyAdapter;
import com.example.myapplication2.RecyclerViewInterface;
import com.example.myapplication2.TravelPlanData;
import com.example.myapplication2.databinding.FragmentDashboardBinding;
import com.example.myapplication2.ui.activity.TravelPlanActivity;
import com.example.myapplication2.ui.activity.UserInfoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements RecyclerViewInterface {

    private int days;
    private String dest, Uid;
    private TextView name, birthday;
    private ImageView avatar;
    private RecyclerView travelPlanList;
    private Button userInfoButton;

    private static final int IMAGE_PICK_CODE = 1000;
    List<TravelPlanData> items = new ArrayList<>();

    private FragmentDashboardBinding binding;
    private StorageReference storageRef;
    private Uri avatarUri;
    private static final String FRAGMENT_NAME = DashboardFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference planRef = database.getReference("travel_plans");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Uid = firebaseAuth.getUid();


        Log.i("D_LIFECYCLE", FRAGMENT_NAME + " onCreateView");

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userInfoButton = binding.userInfoButton;
        avatar = binding.imageView;
        name = binding.textViewName;
        birthday = binding.textViewBirthday;


        //things for travel plan list recycler view
        travelPlanList = binding.recyclerViewTravelPlans;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        MyAdapter myAdapter = new MyAdapter(this.getActivity(), items, this);


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the image chooser
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_CODE);
            }
        });


        retrieveAvatar();


//        items.add(new TravelPlanData(7, "Paris", ""));

//        travelPlanList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//        travelPlanList.setAdapter(new MyAdapter(this.getActivity().getApplicationContext(), items));

//        // Retrieve "days" data from Firebase
//        planRef.child(Uid).child("1").child("days").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Handle retrieved data
//                days = dataSnapshot.getValue(int.class);
//                System.out.println("Days: " + days);
////                Log.d("D_travelPlans", Integer.toString(days));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle database error
//                System.err.println("Failed to retrieve data: " + databaseError.getMessage());
//            }
//        });
//        planRef.child(Uid).child("1").child("dest").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Handle retrieved data
//                dest = dataSnapshot.getValue(String.class);
////                System.out.println("Days: " + days);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle database error
//                System.err.println("Failed to retrieve data: " + databaseError.getMessage());
//            }
//        });
//
////        Log.d("D_travelPlans", Integer.toString(days));
//        System.out.println("Days!: " + days);


        userInfoButton.setOnClickListener((View v) -> {
            openUserInfoPage();
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Uid)) {
                    String nameData =  dataSnapshot.child(Uid).child("name").getValue(String.class);
                    String DOBData =  dataSnapshot.child(Uid).child("DOB").getValue(String.class);
                    name.setText(nameData);
                    birthday.setText(DOBData);
                } else {
                    openUserInfoPage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        planRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Uid)) {
                    long count = dataSnapshot.child(Uid).getChildrenCount();
                    System.out.println(count);
                    for (int i = 1; i < count+1; i++) {
                        Integer days =  dataSnapshot.child(Uid).child(Integer.toString(i)).child("days").getValue(Integer.class);
                        String dest =  dataSnapshot.child(Uid).child(Integer.toString(i)).child("dest").getValue(String.class);
                        String plans =  dataSnapshot.child(Uid).child(Integer.toString(i)).child("plans").getValue(String.class);
                        items.add(new TravelPlanData(days, dest, plans));
                    }
                    System.out.println(items.toString());
                    travelPlanList.setLayoutManager(linearLayoutManager);
                    travelPlanList.setAdapter(myAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return root;
    }

    private void retrieveAvatar() {
        StorageReference folderRef = storageRef.child("images").child(Uid.toString());
        folderRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();
                    if (items.size() == 0) {
                        // folder is empty, do nothing
                    } else {
                        // folder is not empty, delete all files
                        items.get(0).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Successfully downloaded data to bytes array, now convert it to a bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                // Display the bitmap in an image view
                                avatar.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(getActivity(), "Failed retrieve avatar", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle error
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Set the selected image as the avatar image
            Uri imageUri = data.getData();
            avatar.setImageURI(imageUri);
            // Create a new AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // Set the message to display in the dialog
            builder.setMessage("Do you want to save your avatar?");

            // Add the "Yes" button and define its onClickListener
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle "Yes" button click here
                    // For example, save the avatar and dismiss the dialog
                    avatarUri = imageUri;
                    saveAvatar();
                    dialog.dismiss();
                }
            });

            // Add the "No" button and define its onClickListener
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle "No" button click here
                    // For example, dismiss the dialog without saving the avatar
                    dialog.dismiss();
                }
            });

            // Create the AlertDialog and show it
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void saveAvatar() {
        StorageReference folderRef = storageRef.child("images").child(Uid.toString());
        folderRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();
                    if (items.size() == 0) {
                        // folder is empty
                    } else {
                        // folder is not empty, delete all files
                        for (StorageReference item : items) {
                            item.delete();
                        }
                    }
                } else {
                    // Handle error
                }
            }
        });
        // Create a reference to the file to be uploaded
        StorageReference imageRef = storageRef.child("images/" +Uid.toString()+"/"+ avatarUri.getLastPathSegment());

        // Upload the file to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(avatarUri);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image uploaded successfully
                // Get the download URL of the uploaded image
                Toast.makeText(getActivity(), "Avatar saved!", Toast.LENGTH_SHORT).show();
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save the download URL to Firebase Firestore or Realtime Database
                });
            } else {
                Toast.makeText(getActivity(), "Saving failed:(", Toast.LENGTH_SHORT).show();
                // Handle errors here
            }
        });
    }


    private void openUserInfoPage(){
        Intent i = new Intent(getView().getContext(), UserInfoActivity.class);
        startActivity(i);
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

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(getView().getContext(), TravelPlanActivity.class);
        i.putExtra("DETAILS", items.get(position).getPlans());
        startActivity(i);

    }
}