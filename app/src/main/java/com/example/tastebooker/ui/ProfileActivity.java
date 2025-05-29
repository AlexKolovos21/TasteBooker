package com.example.tastebooker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        loadUserData();
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
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        myBookingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MyBookingsActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            // Καθαρισμός των δεδομένων συνεδρίας
            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            // Επιστροφή στην οθόνη σύνδεσης
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
            
            Toast.makeText(this, "Έχετε αποσυνδεθεί επιτυχώς", Toast.LENGTH_SHORT).show();
            
            // Τερματισμός της εφαρμογής
            finishAffinity();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "");
        String email = prefs.getString("userEmail", "");
        
        if (!name.isEmpty()) {
            userName.setText(name);
        }
        if (!email.isEmpty()) {
            userEmail.setText(email);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Έλεγχος αν ο χρήστης είναι συνδεδεμένος
        if (!isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
            finishAffinity();
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.contains("userEmail") && !prefs.getString("userEmail", "").isEmpty();
    }
} 