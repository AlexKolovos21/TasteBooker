package com.example.tastebooker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tastebooker.R;
import com.example.tastebooker.data.AppDatabase;
import com.example.tastebooker.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputLayout nameLayout, emailLayout, phoneLayout;
    private TextInputEditText nameInput, emailInput, phoneInput;
    private MaterialButton saveButton;
    private AppDatabase database;
    private ExecutorService executorService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        saveButton = findViewById(R.id.save_button);
        database = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Παρακαλώ συνδεθείτε ξανά.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserData();
        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        executorService.execute(() -> {
            User user = database.userDao().getUserById(userId);
            runOnUiThread(() -> {
                if (user != null) {
                    nameInput.setText(user.getName());
                    emailInput.setText(user.getEmail());
                    phoneInput.setText(user.getPhone());
                }
            });
        });
    }

    private void saveProfile() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
            return;
        }
        executorService.execute(() -> {
            User user = database.userDao().getUserById(userId);
            if (user != null) {
                user.setName(name);
                user.setEmail(email);
                user.setPhone(phone);
                database.userDao().update(user);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Τα στοιχεία ενημερώθηκαν!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
} 