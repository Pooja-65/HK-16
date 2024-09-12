package com.example.epinect.Activities.Post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.common.Method;
import com.example.epinect.Adapters.RequestAdapter;
import com.example.epinect.DataModels.RequestDataModel;
import com.example.epinect.R;
import com.example.epinect.Utils.Endpoints;
import com.example.epinect.Utils.VolleySingleton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public class Post extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<RequestDataModel> requestDataModels;
    private RequestAdapter requestAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        firestore = FirebaseFirestore.getInstance();
        TextView makeRequestButton = findViewById(R.id.make_request_button);
        makeRequestButton.setOnClickListener(view -> startActivity(new Intent(Post.this, MakeRequestActivity.class)));

        requestDataModels = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new RequestAdapter(requestDataModels, this);
        recyclerView.setAdapter(requestAdapter);

        fetchPosts();
    }

    private void fetchPosts() {
        firestore.collection("posts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String message = document.getString("message");
                    String imageUrl = document.getString("imageUrl");

                    RequestDataModel requestDataModel = new RequestDataModel();
                    requestDataModel.setMessage(message);
                    requestDataModel.setUrl(imageUrl);

                    requestDataModels.add(requestDataModel);
                }
                requestAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(Post.this, "Error getting posts: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
