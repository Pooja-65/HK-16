package com.example.weather_alerts_for_epilepsy.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weather_alerts_for_epilepsy.R;
import com.example.weather_alerts_for_epilepsy.weather.model.WeatherResponse;
import com.example.weather_alerts_for_epilepsy.weather.model.WeatherForecastResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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
    private TextView rainTextView, windSpeedTextView, humidityTextView;
    private ImageView weatherImageView;
    private RecyclerView forecastRecyclerView;
    private WeatherForecastAdapter forecastAdapter;
    private FusedLocationProviderClient fusedLocationClient;

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
        rainTextView = findViewById(R.id.textView5);
        windSpeedTextView = findViewById(R.id.textView7);
        humidityTextView = findViewById(R.id.textView9);
        weatherImageView = findViewById(R.id.imageView);
        forecastRecyclerView = findViewById(R.id.recyclerViewForecast); // Updated ID

        // Setup RecyclerView
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        forecastAdapter = new WeatherForecastAdapter();
        forecastRecyclerView.setAdapter(forecastAdapter);

        // Initialize FusedLocationProviderClient to get real-time location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location permissions and fetch weather data based on current location
        requestLocationPermissions();
    }

    //Request Location Permissions
    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            fetchCurrentLocation(); // Permission already granted, fetch location
        }
    }

    //Fetch the current GPS coordinates
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

    // Step 4: Handle the result of location permission request
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

        String apiKey = "ca24e06c2bd64238b1d121239240809";
        String location = lat + "," + lon;

        // Fetch current weather
        Call<WeatherResponse> callCurrent = apiService.getCurrentWeather(apiKey, location);
        callCurrent.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    updateCurrentWeatherUI(weather);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Handle failure
            }
        });

        // Fetch weather forecast
        Call<WeatherForecastResponse> callForecast = apiService.getWeatherForecast(apiKey, location, 3);
        callForecast.enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(Call<WeatherForecastResponse> call, retrofit2.Response<WeatherForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherForecastResponse forecast = response.body();
                    forecastAdapter.setForecastData(forecast.forecastday);
                }
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
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

        // Fetch and update rain, wind speed, and humidity
        fetchAdditionalData(weather.location.lat, weather.location.lon);
        Log.d("WeatherActivity","latitude"+weather.location.lat);
        Log.d("WeatherActivity","longitude"+weather.location.lon);
    }

    // Fetch additional weather data (rain, wind speed, humidity)
    private void fetchAdditionalData(double lat, double lon) {
        String apiKey = "9824e39035f7721658880646da8a29f6";  // OpenWeatherMap API key
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s", lat, lon, apiKey);

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
                        JSONObject main = jsonObject.getJSONObject("main");
                        JSONObject rain = jsonObject.optJSONObject("rain");
                        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                        double humidity = main.getDouble("humidity");

                        double rainPercentage = (rain != null) ? rain.optDouble("1h", 0.0) : 0.0;

                        runOnUiThread(() -> {
                            if (rain != null) {
                                rainTextView.setText(String.format("%.1f%%", rainPercentage));
                            } else {
                                rainTextView.setText("No rain reported");
                            }
                            windSpeedTextView.setText(String.format("%.1f km/h", windSpeed * 3.6));  // Convert m/s to km/h
                            humidityTextView.setText(String.format("%.1f%%", humidity));
                        });
                        Log.d("WeatherActivity", responseData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
