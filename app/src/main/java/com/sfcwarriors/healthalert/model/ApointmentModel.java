package com.sfcwarriors.healthalert.model;

public class ApointmentModel
{
    String doctName,purpose,date,time,userID;

    public ApointmentModel(String doctName, String purpose, String date, String time, String userID) {
        this.doctName = doctName;
        this.purpose = purpose;
        this.date = date;
        this.time = time;
        this.userID = userID;
    }

    public ApointmentModel() {
    }

    public String getDoctName() {
        return doctName;
    }

    public void setDoctName(String doctName) {
        this.doctName = doctName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
