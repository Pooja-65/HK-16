package com.example.epinect.Activities.calenderData;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epinect.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalenderActivity extends AppCompatActivity {

    private static final String TAG = "CalenderActivity";
    private static final String PREFS_NAME = "YourSharedPreferencesName"; // Replace with your SharedPreferences name
    private static final String KEY_SELECTED_DATES = "selectedDates";
    private DateAdapter dateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup DatePicker button
        Button datePickerButton = findViewById(R.id.date_picker_button);
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
    }

    // Move loadSelectedDates() to onResume() to ensure dates are reloaded every time the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        // Load previously selected dates from SharedPreferences every time the activity comes into view
        loadSelectedDates();
    }

    // Load previously selected dates from SharedPreferences
    private void loadSelectedDates() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> defaultDates = new HashSet<>();
        Set<String> selectedDates = sharedPreferences.getStringSet(KEY_SELECTED_DATES, defaultDates);

        // If there are previously saved dates, update the RecyclerView data
        if (dateAdapter != null) {
            dateAdapter.updateDates(new ArrayList<>(selectedDates));
            Log.d(TAG, "Loaded dates from SharedPreferences: " + selectedDates.toString());
        }
    }

    // Setup the RecyclerView to display selected dates
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_dates); // Make sure this is your RecyclerView ID
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty data and delete listener
        dateAdapter = new DateAdapter(new ArrayList<>(), position -> {
            // Handle delete action
            List<String> dates = dateAdapter.getDates();
            if (position >= 0 && position < dates.size()) {
                dates.remove(position);
                dateAdapter.notifyItemRemoved(position);

                // Save updated dates to SharedPreferences
                saveSelectedDates(new HashSet<>(dates));
            }
        });

        recyclerView.setAdapter(dateAdapter);
    }

    // Save selected dates to SharedPreferences
    private void saveSelectedDates(Set<String> dates) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_SELECTED_DATES, dates);
        editor.apply();
    }

    // Show DatePickerDialog and add selected date
    private void showDatePickerDialog() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Handle selected date
                    String selectedDate = String.format("%d-%d-%d", selectedDay, selectedMonth + 1, selectedYear);
                    addDateToList(selectedDate);
                },
                year, month, day
        );

        // Show the dialog
        datePickerDialog.show();
    }

    // Add the selected date to the list and save it
    private void addDateToList(String date) {
        List<String> dates = dateAdapter.getDates();
        if (!dates.contains(date)) {
            dates.add(date);
            dateAdapter.notifyItemInserted(dates.size() - 1);
            // Save updated dates to SharedPreferences
            saveSelectedDates(new HashSet<>(dates));
        }
    }
}
