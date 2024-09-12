package com.example.epinect.Activities.calenderData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epinect.R;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<String> dates;
    private OnDeleteClickListener onDeleteClickListener;

    public DateAdapter(List<String> dates, OnDeleteClickListener onDeleteClickListener) {
        this.dates = dates;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        String date = dates.get(position);
        holder.dateTextView.setText(date);
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public void updateDates(List<String> newDates) {
        dates.clear();
        dates.addAll(newDates);
        notifyDataSetChanged();
    }

    public List<String> getDates() {
        return dates;
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        ImageView deleteButton;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.text_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
