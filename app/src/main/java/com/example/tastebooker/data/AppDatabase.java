package com.example.tastebooker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.tastebooker.models.Restaurant;
import com.example.tastebooker.models.User;
import com.example.tastebooker.models.Reservation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Restaurant.class, User.class, Reservation.class}, version = 2)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract RestaurantDao restaurantDao();
    public abstract UserDao userDao();
    public abstract ReservationDao reservationDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "tastebooker_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        // Δημιουργία προεπιλεγμένου διαχειριστή
                                        UserDao userDao = INSTANCE.userDao();
                                        User adminUser = new User("admin@tastebooker.com", "1234", "Διαχειριστής", "1234567890");
                                        userDao.insert(adminUser);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
} 