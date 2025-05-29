package com.example.tastebooker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Int,
    val restaurantId: Int,
    val date: Date,
    val time: String,
    val guests: Int,
    val status: String = "pending", // pending, confirmed, cancelled
    val createdAt: Date = Date()
) 