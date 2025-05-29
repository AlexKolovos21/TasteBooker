package com.example.tastebooker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tastebooker.R;
import com.example.tastebooker.data.AppDatabase;
import com.example.tastebooker.models.Reservation;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

public class MyBookingsActivity extends AppCompatActivity {
    private RecyclerView bookingsRecyclerView;
    private TextView emptyTextView;
    private AppDatabase database;
    private ExecutorService executorService;
    private MyBookingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        database = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();
        adapter = new MyBookingsAdapter(this);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingsRecyclerView.setAdapter(adapter);

        loadBookings();
    }

    private void loadBookings() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        android.widget.Toast.makeText(this, "Αναγνωριστικό χρήστη για κρατήσεις: " + userId, android.widget.Toast.LENGTH_LONG).show();
        if (userId == -1) {
            emptyTextView.setText("Παρακαλώ συνδεθείτε για να δείτε τις κρατήσεις σας.");
            emptyTextView.setVisibility(View.VISIBLE);
            bookingsRecyclerView.setVisibility(View.GONE);
            return;
        }
        executorService.execute(() -> {
            List<Reservation> bookings = database.reservationDao().getUserReservations(userId);
            List<com.example.tastebooker.models.Restaurant> restaurants = database.restaurantDao().getAllRestaurants();
            Map<Integer, String> restaurantNames = new HashMap<>();
            for (com.example.tastebooker.models.Restaurant r : restaurants) {
                restaurantNames.put(r.getId(), r.getName());
            }
            runOnUiThread(() -> {
                if (bookings.isEmpty()) {
                    android.widget.Toast.makeText(this, "Δεν βρέθηκαν κρατήσεις για το αναγνωριστικό χρήστη: " + userId, android.widget.Toast.LENGTH_LONG).show();
                    emptyTextView.setVisibility(View.VISIBLE);
                    bookingsRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                    bookingsRecyclerView.setVisibility(View.VISIBLE);
                    adapter.setBookings(bookings, restaurantNames);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
} 