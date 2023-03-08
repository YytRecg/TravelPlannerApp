package com.example.myapplication2.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    EditText emailEt, passwordEt;
    Button createAccBtn, loginAccBtn;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewModel later if needed put here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loginpage, container, false);

        emailEt = v.findViewById(R.id.email_id);
        passwordEt = v.findViewById(R.id.password_text);
        createAccBtn = v.findViewById(R.id.new_user_button);
        loginAccBtn = v.findViewById(R.id.login_button);
        progressBar = v.findViewById(R.id.account_progressbar);

        createAccBtn.setOnClickListener(view -> onCreateAccount());
        loginAccBtn.setOnClickListener(view -> getActivity().finish());

        return v;
    }

    private void onCreateAccount() {
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();

        if (!validateData(email, password)) {return;}

        createAccountInFirebase(email, password);
    }

    private void createAccountInFirebase(String email, String password) {
        setProgressBarVisibility(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            setProgressBarVisibility(false);
            if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Please verify your email!", Toast.LENGTH_SHORT).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                } else {
                    Toast.makeText(getActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void setProgressBarVisibility(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean validateData(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordEt.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }
}