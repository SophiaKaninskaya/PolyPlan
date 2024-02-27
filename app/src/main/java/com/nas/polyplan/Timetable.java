package com.nas.polyplan;

public class Timetable {

    private String user;
    private String day_of_the_week;
    private String text;

    public Timetable(String user, String day_of_the_week, String text) {
        this.user = user;
        this.day_of_the_week = day_of_the_week;
        this.text = text;
    }

    public Timetable() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDay_of_the_week() {
        return day_of_the_week;
    }

    public void setDay_of_the_week(String day_of_the_week) {
        this.day_of_the_week = day_of_the_week;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
