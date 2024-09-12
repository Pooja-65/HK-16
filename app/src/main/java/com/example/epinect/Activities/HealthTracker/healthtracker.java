package com.example.epinect.Activities.HealthTracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epinect.R;
import com.example.epinect.Activities.Reminder.DatabaseHelper;
import com.example.epinect.Activities.Reminder.Reminder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class healthtracker extends AppCompatActivity {
    private ListView remindersListView;
    private ArrayAdapter<String> adapter;
    private List<Reminder> remindersList;
    private DatabaseHelper dbHelper;
    private EditText doctorNameEditText;
    private EditText doctorNumberEditText;
    private Button addDoctorButton;
    private ListView doctorListView;
    private List<String> doctorList;
    private ArrayAdapter<String> doctorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthtracker);
        // Initialize views
        doctorNameEditText = findViewById(R.id.doctorNameEditText);
        doctorNumberEditText = findViewById(R.id.doctorNumberEditText);
        addDoctorButton = findViewById(R.id.addDoctorButton);
        doctorListView = findViewById(R.id.doctorListView);

        // Initialize list of doctors
        doctorList = new ArrayList<>();
        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("DoctorData", Context.MODE_PRIVATE);

        // Retrieve doctor list from SharedPreferences
        Set<String> doctorSet = sharedPreferences.getStringSet("doctorSet", new HashSet<>());
        doctorList.addAll(doctorSet);
        // Initialize and set adapter for ListView
        doctorAdapter = new DoctorAdapter(this, doctorList);
        doctorListView.setAdapter(doctorAdapter);
        // Set onClickListener for the addDoctorButton
        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the doctor name and number from EditText fields
                String name = doctorNameEditText.getText().toString();
                String number = doctorNumberEditText.getText().toString();

                // Add the doctor to the list and notify the adapter
                if (!name.isEmpty() && !number.isEmpty()) {
                    String doctorInfo = name + " - " + number;
                    doctorList.add(doctorInfo);
                    doctorAdapter.notifyDataSetChanged();
                    // Save updated doctor list to SharedPreferences
                    sharedPreferences.edit().putStringSet("doctorSet", new HashSet<>(doctorList)).apply();

                    // Clear EditText fields after adding doctor
                    doctorNameEditText.getText().clear();
                    doctorNumberEditText.getText().clear();
                }
            }
        });
        // Initialize views
        remindersListView = findViewById(R.id.remindersListView);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Fetch reminders from database
        fetchRemindersFromDatabase();
    }
    // DoctorAdapter class to customize ArrayAdapter for doctor list
    private static class DoctorAdapter extends ArrayAdapter<String> {
        private List<String> doctorList;
        private LayoutInflater inflater;
        private Context context; // Add context field

        public DoctorAdapter(Context context, List<String> doctorList) {
            super(context, R.layout.simple_list_item, doctorList);
            this.context = context;
            this.doctorList = doctorList;
            this.inflater = LayoutInflater.from(context);
        }



        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.simple_list_item, parent, false);
            }

            // Bind data to views in your custom layout
            TextView doctorNameTextView = view.findViewById(R.id.doctorNameTextView);
            TextView doctorNumberTextView = view.findViewById(R.id.doctorNumberTextView);
            ImageButton callDoctorButton = view.findViewById(R.id.callDoctorButton);
            String doctorInfo = doctorList.get(position);
            String[] parts = doctorInfo.split(" - ");
            String name = parts[0];
            String number = parts[1];

            doctorNameTextView.setText(name);
            doctorNumberTextView.setText(number);

            // Set click listener for the call button
            callDoctorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implement calling functionality here
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number));
                    context.startActivity(intent);
                }
            });

            return view;
        }
    }

    private void fetchRemindersFromDatabase() {
        // Fetch reminders from the database
        remindersList = dbHelper.getAllReminders();

        // Convert reminders to strings
        ArrayList<String> reminderStrings = new ArrayList<>();
        for (Reminder reminder : remindersList) {
            String tabletTakenStatus = reminder.isTabletTaken() ? "Tablet  NOT Taken" : "Tablet  Taken";

            reminderStrings.add(reminder.getMessage() + " - " + reminder.getTime()+ " - " + tabletTakenStatus);
        }

        // Initialize and set adapter for ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminderStrings);
        remindersListView.setAdapter(adapter);
    }
    // Doctor class to represent a doctor
    private static class Doctor {
        private String name;
        private String number;

        public Doctor(String name, String number) {
            this.name = name;
            this.number = number;
        }

        @Override
        public String toString() {
            return name + " - " + number;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database helper to avoid memory leaks
        dbHelper.close();
    }
}
