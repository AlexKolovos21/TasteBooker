package com.example.tastebooker.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tastebooker.R;
import com.example.tastebooker.data.AppDatabase;
import com.example.tastebooker.data.RestaurantDao;
import com.example.tastebooker.models.Reservation;
import com.example.tastebooker.models.Restaurant;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyBookingsAdapter extends RecyclerView.Adapter<MyBookingsAdapter.BookingViewHolder> {
    private List<Reservation> bookings = new ArrayList<>();
    private Map<Integer, String> restaurantNames = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Context context;

    public MyBookingsAdapter(Context context) {
        this.context = context;
    }

    public void setBookings(List<Reservation> bookings, Map<Integer, String> restaurantNames) {
        this.bookings = bookings;
        this.restaurantNames = restaurantNames;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Reservation booking = bookings.get(position);
        holder.bind(booking, restaurantNames);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingDetailActivity.class);
            intent.putExtra("booking_id", booking.getId());
            intent.putExtra("restaurant_name", restaurantNames.get(booking.getRestaurantId()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final TextView restaurantName, dateTime, people, status;
        private final ImageView statusIcon;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.bookingRestaurantName);
            dateTime = itemView.findViewById(R.id.bookingDateTime);
            people = itemView.findViewById(R.id.bookingPeople);
            status = itemView.findViewById(R.id.bookingStatus);
            statusIcon = itemView.findViewById(R.id.bookingStatusIcon);
        }

        public void bind(Reservation booking, Map<Integer, String> restaurantNames) {
            String name = restaurantNames != null && restaurantNames.containsKey(booking.getRestaurantId()) ? restaurantNames.get(booking.getRestaurantId()) : "Εστιατόριο";
            restaurantName.setText("Εστιατόριο: " + name);
            dateTime.setText("Ημ/νία: " + (booking.getDate() != null ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(booking.getDate()) : "") + " " + booking.getTime());
            people.setText("Άτομα: " + booking.getGuests());
            status.setText(booking.getStatus());
            int color;
            int iconRes;
            switch (booking.getStatus()) {
                case "confirmed":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.success);
                    iconRes = R.drawable.ic_check;
                    break;
                case "cancelled":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.error);
                    iconRes = R.drawable.ic_close;
                    break;
                default:
                    color = ContextCompat.getColor(itemView.getContext(), R.color.warning);
                    iconRes = R.drawable.ic_pending;
            }
            status.setTextColor(color);
            statusIcon.setColorFilter(color);
            statusIcon.setImageResource(iconRes);
        }
    }
} 