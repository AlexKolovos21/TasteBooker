package com.example.tastebooker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tastebooker.R;
import com.example.tastebooker.data.AppDatabase;
import com.example.tastebooker.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout phoneLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private TextInputEditText nameInput;
    private TextInputEditText phoneInput;
    private Button registerButton;
    private AppDatabase database;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Αρχικοποίηση βάσης δεδομένων και εκτελεστή
        database = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        // Αρχικοποίηση των views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        nameLayout = findViewById(R.id.name_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        nameInput = findViewById(R.id.name_input);
        phoneInput = findViewById(R.id.phone_input);
        registerButton = findViewById(R.id.register_button);
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        // Επαναφορά σφαλμάτων
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        nameLayout.setError(null);
        phoneLayout.setError(null);

        // Λήψη τιμών
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        // Έλεγχος εισόδου
        if (email.isEmpty()) {
            emailLayout.setError("Το email είναι υποχρεωτικό");
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Ο κωδικός είναι υποχρεωτικός");
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Η επιβεβαίωση κωδικού είναι υποχρεωτική");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Οι κωδικοί δεν ταιριάζουν");
            return;
        }

        if (name.isEmpty()) {
            nameLayout.setError("Το όνομα είναι υποχρεωτικό");
            return;
        }

        if (phone.isEmpty()) {
            phoneLayout.setError("Το τηλέφωνο είναι υποχρεωτικό");
            return;
        }

        // Έλεγχος αν το email υπάρχει ήδη
        executorService.execute(() -> {
            User existingUser = database.userDao().getUserByEmail(email);
            if (existingUser != null) {
                runOnUiThread(() -> {
                    emailLayout.setError("Το email χρησιμοποιείται ήδη");
                });
                return;
            }

            // Δημιουργία νέου χρήστη
            User newUser = new User(email, password, name, phone);
            long userId = database.userDao().insert(newUser);

            if (userId > 0) {
                // Αποθήκευση συνεδρίας χρήστη
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .edit()
                    .putInt("userId", (int) userId)
                    .putString("userName", name)
                    .apply();

                // Μετάβαση στη λίστα εστιατορίων
                runOnUiThread(() -> {
                    Toast.makeText(this, "Η εγγραφή ολοκληρώθηκε με επιτυχία!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, RestaurantListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Σφάλμα κατά την εγγραφή", Toast.LENGTH_SHORT).show();
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