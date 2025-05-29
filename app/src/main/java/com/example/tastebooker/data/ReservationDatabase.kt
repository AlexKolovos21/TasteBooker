package com.example.tastebooker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tastebooker.models.Reservation

@Database(entities = [Reservation::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReservationDatabase : RoomDatabase() {
    abstract fun reservationDao(): ReservationDao

    companion object {
        @Volatile
        private var INSTANCE: ReservationDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): ReservationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReservationDatabase::class.java,
                    "reservation_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 