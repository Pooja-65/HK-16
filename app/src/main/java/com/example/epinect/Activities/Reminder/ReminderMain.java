package com.example.epinect.Activities.Reminder;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epinect.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderMain extends AppCompatActivity {

    private EditText reminderMessage;
    private TextView time;
    private Button selectTimeButton;
    private Button addButton;
    private Button cancelButton;
    private FloatingActionButton floatingButton;
    private Button deleteAll;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList; // Define reminderList here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        reminderList = new ArrayList<>(); // Initialize reminderList
        floatingButton = findViewById(R.id.floatingButton);
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReminderAdapter(this, reminderList); // Pass context and reminderList to the adapter        recyclerView.setAdapter(adapter);

        Button deleteAllButton = findViewById(R.id.deleteAllButton);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllReminders();
            }
        });
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open floating_popup.xml
                setContentView(R.layout.floating_popup);
                initializeFloatingPopup();
            }
        });
        // Display added reminders
        displayAddedReminders();
    }
    public void deleteReminder(int position) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Reminder reminderToDelete = reminderList.get(position);

        // Convert the ID to long if necessary
        long reminderId = Long.parseLong(reminderToDelete.getId());

        // Delete the reminder from the database
        dbHelper.deleteReminder(reminderId);

        // Remove the reminder from the list and notify the adapter
        reminderList.remove(position);
        adapter.notifyItemRemoved(position);
    }
    private void initializeFloatingPopup() {
        reminderMessage = findViewById(R.id.reminderMessage);
        time = findViewById(R.id.time);
        selectTimeButton = findViewById(R.id.selectTime);
        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);

        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTimeSelection();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder();
                // Navigate back to MainActivity after adding reminder
                Intent intent = new Intent(ReminderMain.this, AddedReminders.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputFields();
            }
        });
    }
    private void displayAddedReminders() {
        // Fetch reminders from the database
        // Clear the existing list
        reminderList.clear(); // Clear the existing list
        reminderList.addAll(dbHelper.getAllReminders()); // Add new reminders
        adapter.notifyDataSetChanged(); // Notify adapter of data change
        logDatabaseContent();

        TextView emptyTextView = findViewById(R.id.empty);
        if (reminderList.isEmpty()) {
            // If the list is empty, make the empty text view visible
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            // If the list is not empty, hide the empty text view
            emptyTextView.setVisibility(View.GONE);
        }
    }
    public void logDatabaseContent() {
        DatabaseHelper dbHelper = new DatabaseHelper(this); // Replace 'context' with your application context
        List<Reminder> reminders = dbHelper.getAllReminders();

        for (Reminder reminder : reminders) {
            Log.d("DatabaseContent", "ID: " + reminder.getId() + ", Message: " + reminder.getMessage() + ", Time: " + reminder.getTime());
        }
    }
    private void handleTimeSelection() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderMain.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void addReminder() {
        String message = reminderMessage.getText().toString();
        String selectedTime = time.getText().toString();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long id = dbHelper.addReminderWithAlarm(this, message, selectedTime);

        Log.d("ReminderMain", "Added reminder - ID: " + id + ", Message: " + message + ", Time: " + selectedTime);
        clearInputFields();
    }

    private void deleteAllReminders() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.deleteAllReminders();

        // Clear the reminderList and notify the adapter
        reminderList.clear();
        adapter.notifyDataSetChanged();
    }

    private void clearInputFields() {
        reminderMessage.setText("");
        time.setText("");
    }

}

