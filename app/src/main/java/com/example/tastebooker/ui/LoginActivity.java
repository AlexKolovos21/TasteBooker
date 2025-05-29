package com.example.tastebooker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tastebooker.R;
import com.example.tastebooker.data.AppDatabase;
import com.example.tastebooker.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private AppDatabase database;
    private ExecutorService executorService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Αρχικοποίηση βάσης δεδομένων και εκτελεστή
        database = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        initViews();
        setupClickListeners();
        
        // Έλεγχος αν ο χρήστης είναι ήδη συνδεδεμένος
        if (isUserLoggedIn()) {
            startMainActivity();
            return;
        }
        
        // Δημιουργία admin χρήστη αν δεν υπάρχει
        createAdminIfNotExists();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void createAdminIfNotExists() {
        executorService.execute(() -> {
            // Έλεγχος αν υπάρχει ήδη ο admin στη βάση
            final User admin = database.userDao().getUserByEmail("admin@gmail.com");
            
            if (admin == null) {
                // Δημιουργία admin χρήστη αν δεν υπάρχει
                final User newAdmin = new User("admin@gmail.com", "pass1234", "Admin", "1234567890");
                database.userDao().insert(newAdmin);
            }
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            User user = database.userDao().login(email, password);
            runOnUiThread(() -> {
                if (user != null) {
                    saveUserSession(user);
                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, 
                        "Λάθος email ή κωδικός", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void saveUserSession(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", user.getId());
        editor.putString("userName", user.getName());
        editor.putString("userEmail", user.getEmail());
        editor.apply();
        
        // Debug message
        Toast.makeText(this, "Saved user ID: " + user.getId(), Toast.LENGTH_SHORT).show();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        String userEmail = prefs.getString("userEmail", "");
        return userId != -1 && !userEmail.isEmpty();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, RestaurantListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Έλεγχος αν ο χρήστης είναι ήδη συνδεδεμένος
        if (isUserLoggedIn()) {
            startMainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
} 