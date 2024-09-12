package com.example.epinect.Activities.StartScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.epinect.Activities.LoginActivity;
import com.example.epinect.Activities.RegisterActivity;
import com.example.epinect.R;

public class Index extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        TypingTextView introText = findViewById(R.id.introText);
        introText.setTextWithAnimation("Empower Your Journey: Together Against Epilepsy");

        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Index.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Index.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}