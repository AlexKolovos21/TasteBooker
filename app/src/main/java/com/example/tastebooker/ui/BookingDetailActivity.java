package com.example.tastebooker.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tastebooker.R;
import com.example.tastebooker.data.AppDatabase;
import com.example.tastebooker.models.Reservation;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingDetailActivity extends AppCompatActivity {
    private AppDatabase database;
    private ExecutorService executorService;
    private TextView restaurantNameTextView;
    private TextView dateTimeTextView;
    private TextView guestsTextView;
    private TextView statusTextView;
    private long bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        database = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();
        bookingId = getIntent().getLongExtra("booking_id", -1);

        if (bookingId == -1) {
            Toast.makeText(this, "Λάθος αναγνωριστικό κράτησης", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        initViews();
        loadBookingDetails();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Λεπτομέρειες Κράτησης");
    }

    private void initViews() {
        restaurantNameTextView = findViewById(R.id.restaurantNameTextView);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        guestsTextView = findViewById(R.id.guestsTextView);
        statusTextView = findViewById(R.id.statusTextView);

        String restaurantName = getIntent().getStringExtra("restaurant_name");
        if (restaurantName != null) {
            restaurantNameTextView.setText("Εστιατόριο: " + restaurantName);
        }
    }

    private void loadBookingDetails() {
        executorService.execute(() -> {
            try {
                Reservation booking = database.reservationDao().getReservationByIdSync(bookingId);
                if (booking != null) {
                    runOnUiThread(() -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateTimeTextView.setText("Ημ/νία: " + dateFormat.format(booking.getDate()) + " " + booking.getTime());
                        guestsTextView.setText("Άτομα: " + booking.getGuests());
                        
                        String statusText;
                        switch (booking.getStatus()) {
                            case "confirmed":
                                statusText = "Επιβεβαιωμένη";
                                break;
                            case "cancelled":
                                statusText = "Ακυρωμένη";
                                break;
                            default:
                                statusText = "Σε εκκρεμότητα";
                        }
                        statusTextView.setText("Κατάσταση: " + statusText);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Δεν βρέθηκε η κράτηση", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Σφάλμα κατά τη φόρτωση της κράτησης", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 