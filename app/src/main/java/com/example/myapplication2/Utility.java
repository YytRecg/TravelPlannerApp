package com.example.myapplication2;

import android.content.Context;
import android.widget.Toast;

public class Utility {

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
