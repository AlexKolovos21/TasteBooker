package com.example.tastebooker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tastebooker.R;
import com.example.tastebooker.models.Restaurant;
import com.example.tastebooker.ui.BookingActivity;

public class RestaurantDetailActivity extends AppCompatActivity {

    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Get restaurant data from intent
        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        if (restaurant == null) {
            finish();
            return;
        }

        setupToolbar();
        setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(restaurant.getName());
    }

    private void setupViews() {
        TextView emojiTextView = findViewById(R.id.restaurantEmoji);
        TextView nameTextView = findViewById(R.id.restaurantName);
        TextView ratingTextView = findViewById(R.id.restaurantRating);
        TextView locationTextView = findViewById(R.id.restaurantLocation);
        TextView cuisineTextView = findViewById(R.id.restaurantCuisine);
        TextView descriptionTextView = findViewById(R.id.restaurantDescription);
        TextView openingHoursTextView = findViewById(R.id.restaurantOpeningHours);
        TextView phoneTextView = findViewById(R.id.restaurantPhone);
        TextView websiteTextView = findViewById(R.id.restaurantWebsite);
        Button bookButton = findViewById(R.id.bookButton);

        emojiTextView.setText(restaurant.getImage());
        nameTextView.setText(restaurant.getName());
        ratingTextView.setText(String.format("%.1f ⭐", restaurant.getRating()));
        locationTextView.setText(restaurant.getLocation());
        cuisineTextView.setText(restaurant.getCuisine());
        descriptionTextView.setText(restaurant.getDescription());
        openingHoursTextView.setText(restaurant.getOpeningHours());
        phoneTextView.setText(restaurant.getPhoneNumber());
        websiteTextView.setText(restaurant.getWebsite());

        bookButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("restaurant_id", restaurant.getId());
            intent.putExtra("restaurant_name", restaurant.getName());
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_more) {
            showMoreOptions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMoreOptions() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.action_more));
        popup.getMenuInflater().inflate(R.menu.restaurant_detail_popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_share) {
                shareRestaurant();
                return true;
            } else if (itemId == R.id.action_favorite) {
                toggleFavorite();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void shareRestaurant() {
        String shareText = String.format("Check out %s at %s!", restaurant.getName(), restaurant.getLocation());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void toggleFavorite() {
        // TODO: Implement favorite functionality
        Toast.makeText(this, "Favorite functionality coming soon!", Toast.LENGTH_SHORT).show();
    }
} 