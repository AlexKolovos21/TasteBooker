package com.example.tastebooker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tastebooker.R
import com.example.tastebooker.data.AppDatabase
import com.example.tastebooker.models.Reservation
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView

class BookingActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private var selectedDate: Date? = null
    private var selectedHour: Int? = null
    private var restaurantId: Int = -1
    private var restaurantName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        database = AppDatabase.getDatabase(this)
        
        // Get restaurant data from intent
        restaurantId = intent.getIntExtra("restaurant_id", -1)
        restaurantName = intent.getStringExtra("restaurant_name") ?: "Εστιατόριο"
        
        if (restaurantId == -1) {
            Toast.makeText(this, "Λάθος στοιχεία εστιατορίου", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViews()
    }

    private fun setupViews() {
        val restaurantNameView = findViewById<TextView>(R.id.restaurant_name)
        val dateButton = findViewById<Button>(R.id.date_button)
        val hourPicker = findViewById<NumberPicker>(R.id.hour_picker)
        val guestsPicker = findViewById<NumberPicker>(R.id.guests_picker)
        val bookButton = findViewById<Button>(R.id.book_button)

        restaurantNameView.text = restaurantName

        guestsPicker.minValue = 1
        guestsPicker.maxValue = 20

        // Set up hour picker for 12–23, 0
        val hours = (12..23).toList() + 0
        val hourLabels = hours.map { String.format("%02d:00", it) }.toTypedArray()
        hourPicker.minValue = 0
        hourPicker.maxValue = hourLabels.size - 1
        hourPicker.displayedValues = hourLabels
        hourPicker.wrapSelectorWheel = true
        hourPicker.setOnValueChangedListener { _, _, newVal ->
            selectedHour = hours[newVal]
        }
        // Default selection
        selectedHour = hours[hourPicker.value]

        dateButton.setOnClickListener {
            showDatePicker()
        }

        bookButton.setOnClickListener {
            if (validateInput()) {
                createReservation(guestsPicker.value)
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.time
                updateDateButton()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDateButton() {
        selectedDate?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            findViewById<Button>(R.id.date_button).text = dateFormat.format(it)
        }
    }

    private fun validateInput(): Boolean {
        if (selectedDate == null) {
            Toast.makeText(this, "Παρακαλώ επιλέξτε ημερομηνία", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedHour == null) {
            Toast.makeText(this, "Παρακαλώ επιλέξτε ώρα", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun createReservation(guests: Int) {
        lifecycleScope.launch {
            val userId = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getInt("userId", -1)

            if (userId == -1) {
                Toast.makeText(this@BookingActivity, "Παρακαλώ συνδεθείτε πρώτα", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val reservation = Reservation(
                userId = userId,
                restaurantId = restaurantId,
                date = selectedDate!!,
                time = String.format("%02d:00", selectedHour),
                guests = guests
            )

            try {
                database.reservationDao().insertReservation(reservation)
                Toast.makeText(this@BookingActivity, "Η κράτηση δημιουργήθηκε επιτυχώς", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@BookingActivity, "Σφάλμα κατά τη δημιουργία κράτησης: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 