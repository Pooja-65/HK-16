package com.example.epinect.Activities.Post;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import android.os.Handler;
import android.os.Looper;

// Add the following imports at the top of your file
import android.widget.ProgressBar;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.preference.PreferenceManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;

import com.example.epinect.R;
import com.example.epinect.Utils.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MakeRequestActivity extends AppCompatActivity {

  private static final int PICK_IMAGE_REQUEST = 101;
  private EditText messageText;
  private TextView chooseImageText;
  private ImageView postImage;
  private Button submitButton;
  private Uri imageUri;
  private FirebaseStorage firebaseStorage;
  private FirebaseFirestore firestore;
  private ProgressBar progressBar;
  private TextView progressText;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_request);

    firebaseStorage = FirebaseStorage.getInstance();
    firestore = FirebaseFirestore.getInstance();

    messageText = findViewById(R.id.message);
    chooseImageText = findViewById(R.id.choose_text);
    postImage = findViewById(R.id.post_image);
    submitButton = findViewById(R.id.submit_button);
    progressBar = findViewById(R.id.progress_bar);
    progressText = findViewById(R.id.progress_text);

    submitButton.setOnClickListener(view -> {
      if (isValid()) {
        uploadRequest();
      }
    });

    chooseImageText.setOnClickListener(view -> permission());

    // Initially hide progress bar and text
    progressBar.setVisibility(View.GONE);
    progressText.setVisibility(View.GONE);
  }

  private void pickImage() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("image/*");
    startActivityForResult(intent, PICK_IMAGE_REQUEST);
  }

  private void permission() {
    if (PermissionChecker.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
      requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 401);
    } else {
      pickImage();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 401 && grantResults.length > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
      pickImage();
    } else {
      showMessage("Permission Declined");
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
      imageUri = data.getData();
      Glide.with(getApplicationContext()).load(imageUri).into(postImage);
    }
  }

  private void uploadRequest() {
    if (imageUri != null) {
      progressBar.setVisibility(View.VISIBLE);
      progressText.setVisibility(View.VISIBLE);

      // Create a reference to the storage
      StorageReference storageRef = firebaseStorage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");
      UploadTask uploadTask = storageRef.putFile(imageUri);

      // Attach a progress listener to track upload progress
      uploadTask.addOnProgressListener(taskSnapshot -> {
        // Calculate progress percentage
        int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");
      }).addOnPausedListener(taskSnapshot -> {
        // Handle pause state if needed
        showMessage("Upload paused");
      }).addOnFailureListener(e -> {
        // Handle failure
        showMessage("Upload failed: " + e.getMessage());
      }).addOnSuccessListener(taskSnapshot -> {
        // Get the download URL and save to Firestore
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
          String imageUrl = uri.toString();
          String message = messageText.getText().toString();
          saveToFirestore(message, imageUrl);
        }).addOnFailureListener(e -> showMessage("Failed to get image URL: " + e.getMessage()));
      });
    } else {
      showMessage("No image selected");
    }
  }


  private void saveToFirestore(String message, String imageUrl) {
    Map<String, Object> post = new HashMap<>();
    post.put("message", message);
    post.put("imageUrl", imageUrl);

    firestore.collection("posts").add(post).addOnSuccessListener(documentReference -> {
      showMessage("Post uploaded successfully");
      finish();
    }).addOnFailureListener(e -> showMessage("Failed to upload post: " + e.getMessage()));
  }

  private boolean isValid() {
    if (messageText.getText().toString().isEmpty()) {
      showMessage("Message shouldn't be empty");
      return false;
    } else if (imageUri == null) {
      showMessage("Pick an image");
      return false;
    }
    return true;
  }

  private void showMessage(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }



  @SuppressLint("NewApi")
  private String getPath(Uri uri) throws URISyntaxException {
    final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
    String selection = null;
    String[] selectionArgs = null;
    // Uri is different in versions after KITKAT (Android 4.4), we need to
    // deal with different Uris.
    if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        return Environment.getExternalStorageDirectory() + "/" + split[1];
      } else if (isDownloadsDocument(uri)) {
        final String id = DocumentsContract.getDocumentId(uri);
        uri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
      } else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        if ("image".equals(type)) {
          uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        selection = "_id=?";
        selectionArgs = new String[]{
            split[1]
        };
      }
    }
    if ("content".equalsIgnoreCase(uri.getScheme())) {
      String[] projection = {
          MediaStore.Images.Media.DATA
      };
      Cursor cursor = null;
      try {
        cursor = getContentResolver()
            .query(uri, projection, selection, selectionArgs, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
          return cursor.getString(column_index);
        }
      } catch (Exception e) {
      }
    } else if ("file".equalsIgnoreCase(uri.getScheme())) {
      return uri.getPath();
    }
    return null;
  }


  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }


  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }


  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }


}
