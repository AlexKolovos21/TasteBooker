package com.example.tastebooker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tastebooker.R;
import com.example.tastebooker.models.Restaurant;
import com.example.tastebooker.ui.RestaurantDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private Context context;

    public RestaurantAdapter(Context context) {
        this.context = context;
        this.restaurants = new ArrayList<>();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }

    public void filterList(List<Restaurant> filteredList) {
        this.restaurants = filteredList;
        notifyDataSetChanged();
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private TextView emojiTextView;
        private TextView nameTextView;
        private TextView ratingTextView;
        private TextView locationTextView;
        private TextView cuisineTextView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiTextView = itemView.findViewById(R.id.restaurantEmoji);
            nameTextView = itemView.findViewById(R.id.restaurantName);
            ratingTextView = itemView.findViewById(R.id.restaurantRating);
            locationTextView = itemView.findViewById(R.id.restaurantLocation);
            cuisineTextView = itemView.findViewById(R.id.restaurantCuisine);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Restaurant restaurant = restaurants.get(position);
                    Intent intent = new Intent(context, RestaurantDetailActivity.class);
                    intent.putExtra("restaurant", restaurant);
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Restaurant restaurant) {
            emojiTextView.setText(restaurant.getImage());
            nameTextView.setText(restaurant.getName());
            ratingTextView.setText(String.format("%.1f ⭐", restaurant.getRating()));
            locationTextView.setText(restaurant.getLocation());
            cuisineTextView.setText(restaurant.getCuisine());
        }
    }
} 