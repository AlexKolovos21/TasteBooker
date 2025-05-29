package com.example.tastebooker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tastebooker.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private ShapeableImageView profileImage;
    private TextView userName;
    private TextView userEmail;
    private Button editProfileButton;
    private Button myBookingsButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupToolbar();
        initViews();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Προφίλ");
    }

    private void initViews() {
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        editProfileButton = findViewById(R.id.editProfileButton);
        myBookingsButton = findViewById(R.id.myBookingsButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Φόρτωση δεδομένων χρήστη από SharedPreferences ή βάση δεδομένων
        // Προσωρινά, ορισμός δοκιμαστικών δεδομένων
        userName.setText("Όνομα Χρήστη");
        userEmail.setText("user@example.com");
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        myBookingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MyBookingsActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            // Υλοποίηση λειτουργίας αποσύνδεσης
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
    }
} 