package com.example.tastebooker.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tastebooker.models.Restaurant;

import java.util.List;

@Dao
public interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    List<Restaurant> getAllRestaurants();

    @Query("SELECT * FROM restaurants WHERE name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' OR cuisine LIKE '%' || :query || '%'")
    List<Restaurant> searchRestaurants(String query);

    @Query("SELECT * FROM restaurants WHERE id = :id")
    Restaurant getRestaurantById(int id);

    @Query("SELECT * FROM restaurants WHERE name = :name LIMIT 1")
    Restaurant getRestaurantByName(String name);

    @Insert
    void insert(Restaurant restaurant);

    @Insert
    void insertAll(List<Restaurant> restaurants);

    @Update
    void update(Restaurant restaurant);

    @Query("DELETE FROM restaurants")
    void deleteAll();
} 