package com.example.tastebooker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tastebooker.R;
import com.example.tastebooker.adapters.RestaurantAdapter;
import com.example.tastebooker.data.AppDatabase;
import com.example.tastebooker.data.RestaurantDao;
import com.example.tastebooker.models.Restaurant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestaurantListActivity extends AppCompatActivity {

    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter adapter;
    private EditText searchEditText;
    private FloatingActionButton menuFab;
    private AppDatabase database;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force login if not logged in
        android.content.SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_restaurant_list);

        // Initialize database and executor
        database = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        initViews();
        setupRecyclerView();
        setupSearch();
        setupMenuFab();
        
        // Load or insert sample data
        executorService.execute(() -> {
            if (database.restaurantDao().getAllRestaurants().isEmpty()) {
                insertSampleRestaurants();
            }
            updateRestaurantNameIfExists();
            loadRestaurants();
        });
    }

    private void insertSampleRestaurants() {
        List<Restaurant> sampleRestaurants = new ArrayList<>();
        
        sampleRestaurants.add(new Restaurant(
            "Το Καλό Ψαράκι",
            "Λεωφ. Συγγρού 123, Αθήνα",
            "Ελληνική",
            4.5f,
            "🍽️"
        ));
        
        sampleRestaurants.add(new Restaurant(
            "Sushi Master",
            "Ερμού 45, Αθήνα",
            "Ιαπωνική",
            4.8f,
            "🍱"
        ));
        
        sampleRestaurants.add(new Restaurant(
            "Pizza Express",
            "Πατησίων 78, Αθήνα",
            "Ιταλική",
            4.2f,
            "🍕"
        ));
        
        sampleRestaurants.add(new Restaurant(
            "Burger House",
            "Σόλωνος 56, Αθήνα",
            "Fast Food",
            4.0f,
            "🍔"
        ));
        
        sampleRestaurants.add(new Restaurant(
            "Το Μυστικό Κήπος",
            "Κηφισίας 234, Αθήνα",
            "Μεσογειακή",
            4.7f,
            "🌿"
        ));
        
        sampleRestaurants.add(new Restaurant(
            "Taste of India",
            "Ακαδημίας 89, Αθήνα",
            "Ινδική",
            4.3f,
            "🍛"
        ));

        database.restaurantDao().insertAll(sampleRestaurants);
    }

    private void updateRestaurantNameIfExists() {
        RestaurantDao dao = database.restaurantDao();
        Restaurant old = dao.getRestaurantByName("Το Μυστικό Κήπος");
        if (old != null) {
            old.setName("Ο Κήπος");
            dao.update(old);
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        menuFab = findViewById(R.id.menuFab);
    }

    private void setupRecyclerView() {
        adapter = new RestaurantAdapter(this);
        restaurantRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        restaurantRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRestaurants(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupMenuFab() {
        menuFab.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(this, menuFab);
            popup.getMenu().add("Οι κρατήσεις μου");
            popup.getMenu().add("Προφίλ");
            popup.getMenu().add("Νέα Κράτηση");
            popup.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                if (title.equals("Οι κρατήσεις μου")) {
                    startActivity(new Intent(this, MyBookingsActivity.class));
                    return true;
                } else if (title.equals("Προφίλ")) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
                } else if (title.equals("Νέα Κράτηση")) {
                    startActivity(new Intent(this, BookingActivity.class));
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void loadRestaurants() {
        executorService.execute(() -> {
            List<Restaurant> restaurants = database.restaurantDao().getAllRestaurants();
            runOnUiThread(() -> adapter.setRestaurants(restaurants));
        });
    }

    private void filterRestaurants(String query) {
        executorService.execute(() -> {
            List<Restaurant> filteredList;
            if (query.isEmpty()) {
                filteredList = database.restaurantDao().getAllRestaurants();
            } else {
                filteredList = database.restaurantDao().searchRestaurants(query);
            }
            runOnUiThread(() -> adapter.filterList(filteredList));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
} 