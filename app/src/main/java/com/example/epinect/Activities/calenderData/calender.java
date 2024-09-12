//package com.example.epinect.Activities.calenderData;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.epinect.R;
//
//import org.naishadhparmar.zcustomcalendar.CustomCalendar;
//import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
//import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
//import org.naishadhparmar.zcustomcalendar.Property;
//
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Map;
//
//public class calender extends AppCompatActivity {
//CustomCalendar customCalendar;
//    private HashMap<Integer, Object> dateHashMap = new HashMap<>();
//    private static final String PREFS_NAME = "MyPrefs";
//    private static final String SELECTED_DATES_KEY = "selectedDates";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        customCalendar=findViewById(R.id.custom_calendar);
//        HashMap<Object, Property> descHashMap=new HashMap<>();
//        Property defaultProperty=new Property();
//        defaultProperty.layoutResource=R.layout.default_view;
//        defaultProperty.dateTextViewResource=R.id.text_view;
//        descHashMap.put("default",defaultProperty);
//        Property currentProperty=new Property();
//        currentProperty.layoutResource=R.layout.current_view;
//        currentProperty.dateTextViewResource=R.id.text_view;
//        descHashMap.put("current",currentProperty);
//        Property presentProperty=new Property();
//        presentProperty.layoutResource=R.layout.present_view;
//        presentProperty.dateTextViewResource=R.id.text_view;
//        descHashMap.put("present",presentProperty);
//        Property absentProperty=new Property();
//        absentProperty.layoutResource=R.layout.absent_view;
//        absentProperty.dateTextViewResource=R.id.text_view;
//        descHashMap.put("absent",absentProperty);
//        customCalendar.setMapDescToProp(descHashMap);
//        HashMap<Integer,Object> dateHashMap = new HashMap<>();
//        Calendar calendar = Calendar.getInstance();
//        dateHashMap.put(calendar.get(Calendar.DAY_OF_MONTH),"current");
////        dateHashMap.put(1,"present");
////        dateHashMap.put(2,"absent");
////        dateHashMap.put(3,"present");
////        dateHashMap.put(4,"absent");
////        dateHashMap.put(20,"present");
////        dateHashMap.put(30,"absent");
//
//        // Load selected dates from SharedPreferences
//        loadSelectedDates();
//// Initialize CustomCalendar with selected dates
//        customCalendar.setDate(Calendar.getInstance(), dateHashMap);
//        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
//                // Show interface for adding event for the selected date
//                toggleHighlightState(selectedDate);            }
//        });
//        // Set navigation button listeners
//        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, new OnNavigationButtonClickedListener() {
//            @Override
//            public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
//                // Decrement month by 1 to go to previous month
//                newMonth.add(Calendar.MONTH, -1);
//                // Update CustomCalendar with the new month
//                customCalendar.setDate(newMonth);
//                return null;
//            }
//        });
//        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, new OnNavigationButtonClickedListener() {
//            @Override
//            public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
//                // Increment month by 1 to go to next month
//                newMonth.add(Calendar.MONTH, 1);
//                // Update CustomCalendar with the new month
//                customCalendar.setDate(newMonth);
//                return null;
//            }
//        });
//    }
//
//    // Method to toggle highlight state for the selected date
//    private void toggleHighlightState(Calendar selectedDate) {
//        Integer dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);
//
//        // Check if the date is already marked
//        if (dateHashMap.containsKey(dayOfMonth)) {
//            // If already marked, remove the highlight
//            dateHashMap.remove(dayOfMonth);
//            Log.d("Calendar", "Date " + dayOfMonth + " removed from HashMap");
//        } else {
//            // If not marked, mark the date with the highlight
//            dateHashMap.put(dayOfMonth, "absent");
//            Log.d("Calendar", "Date " + dayOfMonth + " added to HashMap");
//        }
//
//        // Save selected dates to SharedPreferences
//        saveSelectedDates();
//        // Update calendar view
//        customCalendar.setDate(selectedDate, dateHashMap);
//    }
//
//    // Method to show interface for adding event
//    // Method to show interface for adding event
//    private void showAddEventInterface(final Calendar selectedDate) {
//        Integer dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);
//
//        // Get SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("MyCalendarPrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // Check if the date is already marked
//        if (dateHashMap.containsKey(dayOfMonth)) {
//            // If already marked, remove the highlight
//            dateHashMap.remove(dayOfMonth);
//            // Remove from SharedPreferences
//            editor.remove(String.valueOf(dayOfMonth));
//            Log.d("Calendar", "Date " + dayOfMonth + " removed from SharedPreferences");
//        } else {
//            // If not marked, mark the date with the highlight
//            dateHashMap.put(dayOfMonth, "absent");
//            // Save to SharedPreferences
//            editor.putString(String.valueOf(dayOfMonth), "absent");
//            Log.d("Calendar", "Date " + dayOfMonth + " added to SharedPreferences");
//        }
//
//        // Apply changes to SharedPreferences
//        editor.apply();
//
//        // Update calendar view
//        customCalendar.setDate(selectedDate, dateHashMap);
//    }
//
//    // Method to mark selected date with an event (red color)
//    private void markDateWithEvent(Calendar selectedDate) {
//        // Mark selected date with red color
//        dateHashMap.put(selectedDate.get(Calendar.DAY_OF_MONTH), "absent");
//
//        // Save selected dates to SharedPreferences
//        saveSelectedDates();
//        // Update calendar view
//        customCalendar.setDate(selectedDate, dateHashMap);
//    }
//    // Method to save selected dates to SharedPreferences
//    private void saveSelectedDates() {
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        StringBuilder selectedDatesString = new StringBuilder();
//        for (Map.Entry<Integer, Object> entry : dateHashMap.entrySet()) {
//            selectedDatesString.append(entry.getKey()).append(",");
//        }
//
//        editor.putString(SELECTED_DATES_KEY, selectedDatesString.toString());
//        editor.apply();
//    }
//    // Method to load selected dates from SharedPreferences
//    private void loadSelectedDates() {
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        String selectedDatesString = sharedPreferences.getString(SELECTED_DATES_KEY, "");
//
//        String[] selectedDatesArray = selectedDatesString.split(",");
//        for (String date : selectedDatesArray) {
//            if (!date.isEmpty()) {
//                dateHashMap.put(Integer.parseInt(date), "absent");
//            }
//        }
//    }
//
//}
