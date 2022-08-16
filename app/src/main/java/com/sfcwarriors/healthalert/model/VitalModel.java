package com.sfcwarriors.healthalert.model;

public class VitalModel {

    String cat,date,time,value,userID;
    int weekday;




    public VitalModel(String cat, String date, String time, String value, String userID, int weekday) {
        this.cat = cat;
        this.date = date;
        this.time = time;
        this.value = value;
        this.userID = userID;
        this.weekday = weekday;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public VitalModel() {
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
