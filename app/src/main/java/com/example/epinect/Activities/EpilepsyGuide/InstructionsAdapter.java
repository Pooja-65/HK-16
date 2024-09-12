package com.example.epinect.Activities.EpilepsyGuide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.epinect.R;

import java.util.ArrayList;

public class InstructionsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> instructionsList;

    public InstructionsAdapter(@NonNull Context context, ArrayList<String> instructionsList) {
        super(context, 0, instructionsList);
        this.context = context;
        this.instructionsList = instructionsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.steps_item, parent, false);
        }

        TextView tvStepNumber = convertView.findViewById(R.id.tvStepNumber);
        TextView tvInstructionContent = convertView.findViewById(R.id.tvInstructionContent);
        ImageView btnDeleteStep = convertView.findViewById(R.id.btnDeleteStep);

        String instruction = getItem(position);
        if (instruction != null) {
            tvStepNumber.setText(instruction.split(":")[0] + ":");
            tvInstructionContent.setText(instruction.substring(instruction.indexOf(":") + 2));
        }

        btnDeleteStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove item from list
                instructionsList.remove(position);
                notifyDataSetChanged();

                // Notify the Instructions activity to save the updated list
                ((Instructions) context).saveInstructionsToPreferences();
            }
        });

        return convertView;
    }
}
