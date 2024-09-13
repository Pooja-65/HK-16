package com.example.epinect.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epinect.R;
import com.example.epinect.weather.model.Forecastday;
import java.util.List;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {

    private List<Forecastday> forecastData;

    public void setForecastData(List<Forecastday> forecastData) {
        this.forecastData = forecastData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Forecastday forecast = forecastData.get(position);
        holder.dateTextView.setText(forecast.date);
        holder.conditionTextView.setText(forecast.day.condition.text);
        holder.tempTextView.setText(String.format("%.1f°C", forecast.day.temp_c));
    }

    @Override
    public int getItemCount() {
        return forecastData == null ? 0 : forecastData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView conditionTextView;
        TextView tempTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.forecast_date);
            conditionTextView = itemView.findViewById(R.id.forecast_condition);
            tempTextView = itemView.findViewById(R.id.forecast_temp);
        }
    }
}
