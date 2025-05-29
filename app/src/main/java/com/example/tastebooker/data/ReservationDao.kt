package com.example.tastebooker.data

import androidx.room.*
import com.example.tastebooker.models.Reservation

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY date DESC")
    fun getUserReservations(userId: Int): List<Reservation>

    @Query("SELECT * FROM reservations WHERE restaurantId = :restaurantId ORDER BY date DESC")
    fun getRestaurantReservations(restaurantId: Int): List<Reservation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: Reservation): Long

    @Update
    suspend fun updateReservation(reservation: Reservation)

    @Delete
    suspend fun deleteReservation(reservation: Reservation)

    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getReservationById(id: Long): Reservation?

    @Query("SELECT * FROM reservations WHERE id = :id")
    fun getReservationByIdSync(id: Long): Reservation?
} 