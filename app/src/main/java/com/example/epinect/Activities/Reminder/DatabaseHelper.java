package com.example.epinect.Activities.Reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "reminders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_TABLET_TAKEN = "tablet_taken"; // New column for tablet taken status

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MESSAGE + " TEXT," +
                COLUMN_TIME + " TEXT," +
                COLUMN_TABLET_TAKEN + " INTEGER DEFAULT 0" + // Default value is 0 for not taken
                ")";
        db.execSQL(createTableQuery);
    }
    // Add methods to handle the tablet taken status, such as updating and querying
    public void updateTabletTakenStatus(String reminderId, boolean tabletTaken) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TABLET_TAKEN, tabletTaken ? 1 : 0);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{reminderId});
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to add a new reminder to the database
    public long addReminderWithAlarm(Context context, String message, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_TIME, time);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        // Set alarm for the reminder
        setAlarmForReminder(context, id, message, time);

        return id;
    }

    private void setAlarmForReminder(Context context, long reminderId, String message, String time) {
        // Parse the time to get hour and minute
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Create an intent for the AlarmReceiver class
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("message", message);

        // Create a PendingIntent to be triggered at the specified time
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) reminderId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm using AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Ensure that the alarm is set for a time in the future
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

            // Check if the cursor is not null and contains data
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Extract data from the cursor and create a new Reminder object
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                    @SuppressLint("Range")  String message = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE));
                    @SuppressLint("Range")   String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                    @SuppressLint("Range")   int tabletTakenInt = cursor.getInt(cursor.getColumnIndex(COLUMN_TABLET_TAKEN));
                    boolean tabletTaken = (tabletTakenInt == 1);

                    // Add the Reminder object to the list
                    reminderList.add(new Reminder(id, message, time, tabletTaken));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the cursor and database connection
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return reminderList;
    }



    public void deleteAllReminders() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("reminders", null, null);
        Log.d("DatabaseHelper", "Deleted " + deletedRows + " reminders");
        db.close();
    }

    public void deleteReminder(long reminderId) {
        Log.d("DatabaseHelper", "Deleting all reminders...");
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows=db.delete("reminders", "id=?", new String[]{String.valueOf(reminderId)});
        Log.d("DatabaseHelper", "Deleted " + deletedRows + " reminders");
        db.close();
    }

}


