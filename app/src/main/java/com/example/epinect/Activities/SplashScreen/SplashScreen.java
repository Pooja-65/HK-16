package com.example.epinect.Activities.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.epinect.Activities.StartScreen.Index;
import com.example.epinect.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
        TextView animatedText = findViewById(R.id.animatedText);

        // Start Lottie animation
        lottieAnimationView.playAnimation();

        // Animate the text after Lottie animation starts
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeInAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade_in); // or slide_up
                animatedText.setVisibility(TextView.VISIBLE);
                animatedText.startAnimation(fadeInAnimation);
            }
        }, 500); // Delay before starting the text animation


        // Navigate to the main activity after a delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, Index.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // Adjust the delay as needed
    }
}
