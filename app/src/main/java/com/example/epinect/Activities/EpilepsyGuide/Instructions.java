package com.example.epinect.Activities.EpilepsyGuide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.epinect.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Instructions extends AppCompatActivity {
    private int stepNumber = 1;
    private ListView listViewInstructions;
    private ArrayList<String> instructionsList;
    private InstructionsAdapter instructionsAdapter;
    private FloatingActionButton fabAddInstruction;
    private Button clearAllButton; // Add this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        listViewInstructions = findViewById(R.id.listViewInstructions);
        fabAddInstruction = findViewById(R.id.fabAddInstruction);
        clearAllButton = findViewById(R.id.clearAllButton); // Initialize button

        instructionsList = loadInstructionsFromPreferences();
        instructionsAdapter = new InstructionsAdapter(this, instructionsList);
        listViewInstructions.setAdapter(instructionsAdapter);

        fabAddInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddInstructionDialog();
            }
        });

        clearAllButton.setOnClickListener(new View.OnClickListener() { // Add this
            @Override
            public void onClick(View v) {
                clearAllInstructions();
            }
        });
    }

    private void showAddInstructionDialog() {
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.add_instruction, null);

        final EditText editTextInstruction = dialogView.findViewById(R.id.editTextDescription);
        Button btnAdd = dialogView.findViewById(R.id.addButton);
        Button btnCancel = dialogView.findViewById(R.id.cancelButton);

        final androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String instructionContent = editTextInstruction.getText().toString().trim();
                if (!instructionContent.isEmpty()) {
                    String instruction = "Step " + stepNumber + ": " + instructionContent;
                    instructionsList.add(instruction);
                    instructionsAdapter.notifyDataSetChanged();
                    stepNumber++; // Increment the step number for the next instruction
                    dialog.dismiss();
                    saveInstructionsToPreferences();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void deleteInstruction(int position) {
        instructionsList.remove(position);
        instructionsAdapter.notifyDataSetChanged();
        saveInstructionsToPreferences();
    }

    public void clearAllInstructions() {
        instructionsList.clear(); // Clear the list
        instructionsAdapter.notifyDataSetChanged(); // Notify adapter of data change
        saveInstructionsToPreferences(); // Save the empty list
    }

    public void saveInstructionsToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("InstructionsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(instructionsList);
        editor.putStringSet("instructions", set);
        editor.apply();
    }

    private ArrayList<String> loadInstructionsFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("InstructionsPrefs", Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("instructions", new HashSet<String>());
        return new ArrayList<>(set);
    }
}
