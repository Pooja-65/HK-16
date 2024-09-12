package com.example.epinect.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epinect.Dashboard;
import com.example.epinect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
  private Uri profileImageUri;
  private EditText nameEt, cityEt, emailEt, passwordEt, mobileEt,ageEt;
  private Button registerButton;
  private ImageView profileImageView;
  private ProgressBar progressBar;

  private FirebaseAuth mAuth;
  private FirebaseFirestore mFirestore;
  private StorageReference mStorageRef;

  private static final int PICK_IMAGE_REQUEST = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);


    nameEt = findViewById(R.id.name);
    emailEt = findViewById(R.id.email);
    cityEt = findViewById(R.id.city);
    passwordEt = findViewById(R.id.password);
    mobileEt = findViewById(R.id.number);
    ageEt = findViewById(R.id.age);  // Add this line
    registerButton = findViewById(R.id.register_button);
    profileImageView = findViewById(R.id.profile_image);
    progressBar = findViewById(R.id.progress_bar);

    mAuth = FirebaseAuth.getInstance();
    mFirestore = FirebaseFirestore.getInstance(); // Firestore instance
    mStorageRef = FirebaseStorage.getInstance().getReference("profile_images");

    registerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String name = nameEt.getText().toString().trim();
        String city = cityEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String mobile = mobileEt.getText().toString().trim();
        String ageString = ageEt.getText().toString().trim();  // Retrieve age

        if (isValid(name, city, email, password, mobile, ageString)) {
          int age = Integer.parseInt(ageString);  // Convert age to integer
          registerUser(name, city, email, password, mobile, age);
        }
      }
    });

    profileImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openFileChooser();
      }
    });
  }

  private void registerUser(final String name, final String city, final String email,
                            final String password, final String mobile, final int age) {  // Add age parameter
    progressBar.setVisibility(View.VISIBLE);

    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
          String userId = mAuth.getCurrentUser().getUid();
          Map<String, Object> userMap = new HashMap<>();
          userMap.put("name", name);
          userMap.put("city", city);
          userMap.put("email", email);
          userMap.put("mobile", mobile);
          userMap.put("age", age);  // Add age to the map

          if (profileImageUri != null) {
            String fileExtension = getFileExtension(profileImageUri);
            StorageReference fileRef = mStorageRef.child(userId + "." + fileExtension);
            fileRef.putFile(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                  fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                      if (task.isSuccessful()) {
                        userMap.put("profileImage", task.getResult().toString());
                        saveUserData(userId, userMap);
                      } else {
                        showError("Image upload failed: " + task.getException().getMessage());
                      }
                    }
                  });
                } else {
                  showError("Image upload failed: " + task.getException().getMessage());
                }
              }
            });
          } else {
            saveUserData(userId, userMap);
          }
        } else {
          showError("Registration failed: " + task.getException().getMessage());
        }
      }
    });
  }

  private String getFileExtension(Uri uri) {
    ContentResolver contentResolver = getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(contentResolver.getType(uri));
  }

  private void saveUserData(String userId, Map<String, Object> userMap) {
    // Save user data in Firestore instead of Realtime Database
    mFirestore.collection("users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        progressBar.setVisibility(View.GONE);
        if (task.isSuccessful()) {
          Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
          startActivity(new Intent(RegisterActivity.this, Dashboard.class));
          finish();
        } else {
          showError("Data saving failed: " + task.getException().getMessage());
        }
      }
    });
  }

  private boolean isValid(String name, String city, String email, String password, String mobile, String age) {
    if (name.isEmpty()) {
      showMessage("Name is empty");
      return false;
    } else if (email.isEmpty()) {
      showMessage("Email is required");
      return false;
    } else if (city.isEmpty()) {
      showMessage("City name is required");
      return false;
    } else if (mobile.length() != 10) {
      showMessage("Invalid mobile number, number should be 10 digits");
      return false;
    } else if (age.isEmpty()) {
      showMessage("Age is required");
      return false;
    }
    return true;
  }

  private void showMessage(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }

  private void showError(String msg) {
    progressBar.setVisibility(View.GONE);
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }

  private void openFileChooser() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
      profileImageUri = data.getData();
      profileImageView.setImageURI(profileImageUri);
    }
  }
}