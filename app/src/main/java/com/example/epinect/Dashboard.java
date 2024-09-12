package com.example.epinect;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.epinect.Activities.Emergencysos.SmsActivity;
import com.example.epinect.Activities.EpilepsyGuide.Instructions;
import com.example.epinect.Activities.HealthTracker.healthtracker;
import com.example.epinect.Activities.Medibot.chatbot;
import com.example.epinect.Activities.Post.Post;
import com.example.epinect.Activities.Reminder.ReminderMain;
import com.example.epinect.Activities.location.location;
import com.example.epinect.Activities.weather.WeatherActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class Dashboard extends AppCompatActivity {
    TextView greetingTextView;

    // Declare TextViews and ImageView for the profile section
    TextView userName, userAge, userCity;
    ImageView profileImage;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // Find the CardViews by their IDs
        CardView weatherCard = findViewById(R.id.weather_Card);
        CardView emergencyCard = findViewById(R.id.emergencyCard);
        CardView guideCard = findViewById(R.id.guideCard);
        CardView scheduleCard = findViewById(R.id.scheduleCard);
        CardView askCard = findViewById(R.id.askCard);
        CardView healthCard = findViewById(R.id.healthCard);
        CardView locationCard = findViewById(R.id.locationCard);
        CardView recordCard = findViewById(R.id.seizureeventCard);
        CardView dataCard = findViewById(R.id.health_Card);
        // Initialize views
        userName = findViewById(R.id.user_name);
        userAge = findViewById(R.id.user_age);
        userCity = findViewById(R.id.user_city);
        profileImage = findViewById(R.id.profile_image);
        greetingTextView = findViewById(R.id.greeting_text);

        // Initialize Firebase Firestore and Auth
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Fetch user details from Firestore
        String userId = mAuth.getCurrentUser().getUid();
        firestore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Fetch user data from Firestore
                        String name = document.getString("name");
                        String city = document.getString("city");
                        Long ageLong = document.getLong("age"); // Long instead of long to handle null
                        String profileImageUrl = document.getString("profileImage");

                        // Handle null for age
                        String age = (ageLong != null) ? String.valueOf(ageLong) : "N/A";
                        Log.d(TAG, "Age retrieved: " + age);

                        // Set user data to views
                        userName.setText(name);
                        userAge.setText("Age: " + age); // Set with "Age: " label
                        userCity.setText("City: " + city); // Set with "City: " label

                        // Load profile image using Picasso
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Picasso.get().load(profileImageUrl).into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.profile);  // Set default image
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "Fetch failed: ", task.getException());
                }
            }
        });
        setGreetingMessage();
        // Set click listeners for each CardView
        weatherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Community CardView
                openActivity(WeatherActivity.class);
                vibrate();
            }
        });

        // Set click listeners for each CardView
        emergencyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Community CardView
                openActivity(SmsActivity.class);
                vibrate();
            }
        });

        guideCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Medicine Reminder CardView
                      openActivity(Instructions.class);
                    vibrate();
            }
        });

        scheduleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Emergency SOS CardView
                openActivity(ReminderMain.class);
                vibrate();
            }
        });

        askCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Magnifying Glass CardView
                   openActivity(chatbot.class);
                vibrate();
            }
        });
       healthCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Magnifying Glass CardView
                openActivity(Post.class);
                vibrate();
            }
        });
        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Magnifying Glass CardView
                   openActivity(location.class);
                vibrate();
            }
        });
//        recordCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle click for Magnifying Glass CardView
//                openActivity(calender.class);
//                vibrate();
//            }
//        });
        dataCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Magnifying Glass CardView
                openActivity(healthtracker.class);
                vibrate();
            }
        });


    }
    private void setGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greetingMessage;

        if (hourOfDay >= 0 && hourOfDay < 12) {
            greetingMessage = "Hi, Good Morning";
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            greetingMessage = "Hi, Good Afternoon";
        } else {
            greetingMessage = "Hi, Good Evening";
        }

        // Set the greeting message to the TextView
        greetingTextView.setText(greetingMessage);
    }
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Check if the device has a vibrator
        if (vibrator != null) {
            // Vibrate for 500 milliseconds (adjust duration as needed)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26
                vibrator.vibrate(500);
            }
        }
    }
}