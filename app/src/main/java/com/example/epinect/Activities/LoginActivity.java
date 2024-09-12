package com.example.epinect.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.epinect.Dashboard;
import com.example.epinect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

  private EditText inputField, passwordEt;
  private Button submit_button;
  private TextView signUpText;
  private SharedPreferences sharedPreferences;
  private FirebaseAuth mAuth;
  private ImageView logoImageView;
  private LottieAnimationView lottieAnimationView;
  private Handler handler = new Handler();

  private Runnable switchRunnable;
  private boolean showingLottie = false;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    inputField = findViewById(R.id.input_field); // This can be either email or number
    passwordEt = findViewById(R.id.password);
    submit_button = findViewById(R.id.submit_button);
    signUpText = findViewById(R.id.sign_up_text);
    logoImageView = findViewById(R.id.logo_image);
    lottieAnimationView = findViewById(R.id.lottie_animation);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    mAuth = FirebaseAuth.getInstance();

    boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);

    if (rememberMe) {
      String savedInput = sharedPreferences.getString("input", "");
      String savedPassword = sharedPreferences.getString("password", "");
      inputField.setText(savedInput);
      passwordEt.setText(savedPassword);
    }

       signUpText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
      }
    });

    submit_button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        inputField.setError(null);
        passwordEt.setError(null);
        String input = inputField.getText().toString();
        String password = passwordEt.getText().toString();
        if (isValid(input, password)) {
          login(input, password);
          sharedPreferences.edit()
                  .putBoolean("rememberMe", true)
                  .putString("input", input)
                  .putString("password", password)
                  .apply();
        }
      }
    });
    // Initialize Handler and Runnable for logo/Lottie switch
    switchRunnable = new Runnable() {
      @Override
      public void run() {
        if (showingLottie) {
          lottieAnimationView.setVisibility(View.GONE);
          logoImageView.setVisibility(View.VISIBLE);
        } else {
          lottieAnimationView.setVisibility(View.VISIBLE);
          logoImageView.setVisibility(View.GONE);
          lottieAnimationView.playAnimation(); // Play the animation when it's visible
        }
        showingLottie = !showingLottie;
        handler.postDelayed(this, 3000); // Switch every 3 seconds
      }
    };

    handler.post(switchRunnable); // Start the switching process
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // Remove callbacks to prevent memory leaks
    handler.removeCallbacks(switchRunnable);
  }


  // Show the Lottie animation and then revert to the logo
  private void showLottieAnimation() {
    // Hide the static logo
    logoImageView.setVisibility(View.GONE);
    // Show the Lottie animation and play it
    lottieAnimationView.setVisibility(View.VISIBLE);
    lottieAnimationView.playAnimation();

    // When the animation ends, revert back to the static logo
    lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        // Do nothing when the animation starts
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        // After the animation ends, hide Lottie and show the logo
        lottieAnimationView.setVisibility(View.GONE);
        logoImageView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        // Optional: Handle animation cancellation if needed
      }

      @Override
      public void onAnimationRepeat(Animator animation) {
        // Optional: Handle animation repeat if needed
      }
    });
  }
  private boolean isValid(String input, String password) {
    if (input.isEmpty()) {
      showMessage("Empty Email/Number");
      inputField.setError("Empty Email/Number");
      return false;
    } else if (password.isEmpty()) {
      showMessage("Empty Password");
      passwordEt.setError("Empty Password");
      return false;
    }
    return true;
  }

  private void login(String input, String password) {
    if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
      // Login using email
      mAuth.signInWithEmailAndPassword(input, password)
              .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                  // Sign in success
                  FirebaseUser user = mAuth.getCurrentUser();
                  startActivity(new Intent(LoginActivity.this, Dashboard.class));
                  LoginActivity.this.finish();
                } else {
                  // If sign in fails
                  showMessage("Authentication failed.");
                  Log.e("FirebaseAuth", "Login failed", task.getException());
                }
              });
    } else {
      // Handle case for phone number authentication if needed
      showMessage("Invalid email address.");
    }
  }

  private void showMessage(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }
}
