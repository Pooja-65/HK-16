package com.example.epinect.Activities.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.epinect.R;
import com.example.epinect.Activities.weather.model.WeatherResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class WeatherActivity extends AppCompatActivity {

    private TextView conditionTextView, dateTimeTextView, tempTextView, locationTextView, regionTextView;
    private TextView aqiStatusTextView;
    private TextView uvTextView, windSpeedTextView, humidityTextView;
    private ImageView weatherImageView;
    private FusedLocationProviderClient fusedLocationClient;
    private ImageView infoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Initialize UI elements
        conditionTextView = findViewById(R.id.textView);
        dateTimeTextView = findViewById(R.id.textView2);
        tempTextView = findViewById(R.id.textView3);
        locationTextView = findViewById(R.id.textView4);
        regionTextView = findViewById(R.id.textView12);
        uvTextView = findViewById(R.id.textView5);
        windSpeedTextView = findViewById(R.id.textView7);
        humidityTextView = findViewById(R.id.textView9);
        weatherImageView = findViewById(R.id.imageView);
        aqiStatusTextView = findViewById(R.id.textView14); // Initialize AQI TextView
        infoImageView = findViewById(R.id.info);


        // Initialize FusedLocationProviderClient to get real-time location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location permissions and fetch weather data based on current location
        requestLocationPermissions();



        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });
    }

    // Request Location Permissions
    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            fetchCurrentLocation(); // Permission already granted, fetch location
        }
    }

    // Fetch the current GPS coordinates
    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Log.d("WeatherActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
                                fetchWeatherData(latitude, longitude);  // Use these coordinates to fetch weather data
                            } else {
                                Log.d("WeatherActivity", "Location is null.");
                            }
                        }
                    });
        }
    }

    // Handle the result of location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch current location
                fetchCurrentLocation();
            } else {
                // Permission denied, handle accordingly
                Log.d("WeatherActivity", "Location permission denied.");
            }
        }
    }

    // Fetch weather data based on latitude and longitude
    private void fetchWeatherData(double lat, double lon) {
        WeatherApiService apiService = RetrofitClient.getClient().create(WeatherApiService.class);

        String apiKey = "ca24e06c2bd64238b1d121239240809";  // WeatherAPI key
        String location = lat + "," + lon;

        // Fetch current weather
        Call<WeatherResponse> callCurrent = apiService.getCurrentWeather(apiKey, location);
        callCurrent.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    updateCurrentWeatherUI(weather);
                    fetchAirQuality(lat, lon); // Fetch air quality after getting weather data
                    fetchWeatherAlerts(lat, lon);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Handle failure
            }
        });


    }

    // Update UI with current weather data
    private void updateCurrentWeatherUI(WeatherResponse weather) {
        // Update UI elements
        conditionTextView.setText(weather.current.condition.text);
        tempTextView.setText(String.format(Locale.getDefault(), "%.1f°C", weather.current.temp_c));

        String currentDateTime = new SimpleDateFormat("E MMM dd | hh:mm a", Locale.getDefault()).format(new Date());
        dateTimeTextView.setText(currentDateTime);

        locationTextView.setText(weather.location.name);
        regionTextView.setText(weather.location.region);
        Log.d("WeatherActivity", "Location Name: " + weather.location.name);
        Log.d("WeatherActivity", "Region: " + weather.location.region);

        // Update image based on condition
        String iconUrl = "https:" + weather.current.condition.icon;
        Glide.with(this).load(iconUrl).into(weatherImageView);

        // Fetch and update UV index, wind speed, and humidity
        fetchAdditionalData(weather.location.lat, weather.location.lon);
    }

    // Fetch additional weather data (UV index, wind speed, humidity)
    private void fetchAdditionalData(double lat, double lon) {
        String apiKey = "ca24e06c2bd64238b1d121239240809";  // WeatherAPI key
        String url = String.format("https://api.weatherapi.com/v1/current.json?key=%s&q=%f,%f", apiKey, lat, lon);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject current = jsonObject.getJSONObject("current");
                        double uvIndex = current.getDouble("uv");  // Fetch UV index
                        double windSpeed = current.getDouble("wind_kph");
                        double humidity = current.getDouble("humidity");

                        runOnUiThread(() -> {
                            uvTextView.setText(String.format("%.1f", uvIndex));  // Display UV index
                            windSpeedTextView.setText(String.format("%.1f km/h", windSpeed));
                            humidityTextView.setText(String.format("%.1f%%", humidity));
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Fetch air quality data
    private void fetchAirQuality(double lat, double lon) {
        String apiKey = "9824e39035f7721658880646da8a29f6";  // OpenWeatherMap API key
        String url = String.format("https://api.openweathermap.org/data/2.5/air_pollution?lat=%f&lon=%f&appid=%s", lat, lon, apiKey);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("AQI", "Failed to fetch AQI: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("AQI", "Response Data: " + responseData); // Log the response data to check what is received
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray listArray = jsonObject.getJSONArray("list");
                        JSONObject firstEntry = listArray.getJSONObject(0);
                        JSONObject main = firstEntry.getJSONObject("main");
                        int aqiValue = main.getInt("aqi");

                        // Map AQI value to a text status
                        String aqiStatus;
                        switch (aqiValue) {
                            case 1:
                                aqiStatus = "Good";
                                break;
                            case 2:
                                aqiStatus = "Fair";
                                break;
                            case 3:
                                aqiStatus = "Moderate";
                                break;
                            case 4:
                                aqiStatus = "Poor";
                                break;
                            case 5:
                                aqiStatus = "Very Poor";
                                break;
                            default:
                                aqiStatus = "Unknown";
                                break;
                        }

                        // Update UI on the main thread
                        runOnUiThread(() -> aqiStatusTextView.setText(aqiStatus));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("AQI", "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    Log.d("AQI", "Unsuccessful response: " + response.message());
                }
            }
        });
    }
    // Fetch weather alerts for the given latitude and longitude
    private void fetchWeatherAlerts(double lat, double lon) {
        String apiKey = "ca24e06c2bd64238b1d121239240809";  // OpenWeatherMap API key
        String url = String.format("https://api.weatherapi.com/v1/forecast.json?key=%s&q=%f,%f&alerts=yes", apiKey, lat, lon);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("WeatherAlert", "Failed to fetch weather alerts: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("WeatherAlert", "Response Data: " + responseData); // Log the response data

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray alertsArray = jsonObject.optJSONArray("alerts");

                        // Check if there are any alerts
                        if (alertsArray != null && alertsArray.length() > 0) {
                            JSONObject firstAlert = alertsArray.getJSONObject(0);
                            String alertDescription = firstAlert.getString("description");

                            // Update UI with the alert description on the main thread
                            runOnUiThread(() -> {
                                TextView alertTextView = findViewById(R.id.textView16);
                                alertTextView.setText(alertDescription);
                            });
                        } else {
                            // No alerts found, update UI with a default message
                            runOnUiThread(() -> {
                                TextView alertTextView = findViewById(R.id.textView16);
                                alertTextView.setText("No weather alerts");
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("WeatherAlert", "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    Log.d("WeatherAlert", "Unsuccessful response: " + response.message());
                }
            }
        });
    }

}

