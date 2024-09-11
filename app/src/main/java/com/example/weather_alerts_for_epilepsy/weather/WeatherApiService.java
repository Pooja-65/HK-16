package com.example.weather_alerts_for_epilepsy.weather;

import com.example.weather_alerts_for_epilepsy.weather.model.WeatherResponse;
import com.example.weather_alerts_for_epilepsy.weather.model.WeatherForecastResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("current.json")
    Call<WeatherResponse> getCurrentWeather(@Query("key") String apiKey, @Query("q") String location);

    @GET("forecast.json")
    Call<WeatherForecastResponse> getWeatherForecast(@Query("key") String apiKey, @Query("q") String location, @Query("days") int days);
}
