package com.example.epinect.Activities.weather;

import com.example.epinect.Activities.weather.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    // Method to fetch current weather by latitude and longitude
    @GET("current.json")
    Call<WeatherResponse> getCurrentWeather(
            @Query("key") String apiKey,
            @Query("q") String location
    );

    // Method to fetch weather by city name (new method)
    @GET("current.json")
    Call<WeatherResponse> getWeatherByCity(
            @Query("key") String apiKey,
            @Query("q") String city
    );
}
