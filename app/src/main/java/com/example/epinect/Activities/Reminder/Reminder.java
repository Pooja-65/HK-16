package com.example.epinect.Activities.Reminder;

public class Reminder {
    private String id;
    private String message;
    private String time;
    private boolean tabletTaken; // New attribute

    public Reminder(String id, String message, String time, boolean tabletTaken) {
        this.id = id;
        this.message = message;
        this.time = time;
        this.tabletTaken = tabletTaken;
    }

    // Add getters and setters for id, message, time, and tabletTaken
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isTabletTaken() {
        return tabletTaken;
    }

    public void setTabletTaken(boolean tabletTaken) {
        this.tabletTaken = tabletTaken;
    }
}
