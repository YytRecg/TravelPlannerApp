package com.example.myapplication2.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.example.myapplication2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication2.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {

    private ActivityTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("D_LIFECYCLE", "TestActivity onCreate");

        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        // M: this line below adds the Back Arrow to the nav layout
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder().build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_test);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("D_LIFECYCLE", "TestActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("D_LIFECYCLE", "TestActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("D_LIFECYCLE", "TestActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("D_LIFECYCLE", "TestActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("D_LIFECYCLE", "TestActivity onDestroy");
    }

}