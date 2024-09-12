package com.example.epinect.Activities.Reminder;

import android.content.Context;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epinect.Activities.Emergencysos.Contacts.ContactModel;
import com.example.epinect.R;
import java.util.List;
import com.example.epinect.Activities.Emergencysos.Contacts.DbHelper;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;
    private static List<ContactModel> emergencyContacts;
    private DbHelper dbHelper; // DbHelper instance

    public ReminderAdapter(Context context, List<Reminder> reminders) {
        this.reminders = reminders;
        this.dbHelper = new DbHelper(context); // Initialize DbHelper
        this.emergencyContacts = dbHelper.getAllContacts(); // Retrieve emergency contacts
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(itemView, reminders);
    }


    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.bind(reminder); // Pass the reminders list to the ViewHolder
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;
        private TextView textViewTime;
        private CheckBox rememberMeCheckbox; // New attribute
        private List<Reminder> reminders; // Add this attribute

        ReminderViewHolder(View itemView, List<Reminder> reminders) {
            super(itemView);
            this.reminders = reminders;
            textViewMessage = itemView.findViewById(R.id.textView1);
            textViewTime = itemView.findViewById(R.id.textView21);
            rememberMeCheckbox = itemView.findViewById(R.id.rememberMeCheckbox);

            rememberMeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Update tabletTaken attribute of the corresponding reminder
                    Reminder reminder = reminders.get(position);
                    reminder.setTabletTaken(isChecked);
                    // You can perform any additional actions here based on checkbox state change
                    if (isChecked) {
                        sendSMSTabletTaken(itemView.getContext());
                        rememberMeCheckbox.setEnabled(false);
                    }
                }
            });

// Start a delayed task to check if the tablet has not been taken after 1 minute
            new Handler().postDelayed(() -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Reminder reminder = reminders.get(position);
                    if (!reminder.isTabletTaken()) {
                        // Send SMS indicating tablet has not been taken after 1 minute delay
                        sendSMSNotTaken(itemView.getContext());
                    }
                }
            }, 1 * 60 * 1000); // 1 minute delay

        }

        void bind(Reminder reminder) {
            textViewMessage.setText(reminder.getMessage());
            textViewTime.setText(reminder.getTime());
            rememberMeCheckbox.setChecked(reminder.isTabletTaken());
        }

        private void sendSMSTabletTaken(Context context) {
            SmsManager smsManager = SmsManager.getDefault();
            for (ContactModel contact : emergencyContacts) {
                smsManager.sendTextMessage(contact.getPhoneNo(), null, "USER HAS TAKEN THE TABLET", null, null);
            }
        }

        private void sendSMSNotTaken(Context context) {
            SmsManager smsManager = SmsManager.getDefault();
            for (ContactModel contact : emergencyContacts) {
                smsManager.sendTextMessage(contact.getPhoneNo(), null, "USER DID NOT TAKE THE TABLET!! CALL AND REMIND USER", null, null);
            }
        }
    }

}