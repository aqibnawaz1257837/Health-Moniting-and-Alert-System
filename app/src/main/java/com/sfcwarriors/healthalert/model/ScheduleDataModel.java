package com.sfcwarriors.healthalert.model;

public class ScheduleDataModel
{
    String medCatName,medName,startDate,endDate,noRepeat,gapeTime,userID;

    public ScheduleDataModel() {
    }

    public ScheduleDataModel(String medCatName, String medName, String startDate, String endDate, String noRepeat, String gapeTime, String userID) {
        this.medCatName = medCatName;
        this.medName = medName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.noRepeat = noRepeat;
        this.gapeTime = gapeTime;
        this.userID = userID;
    }

    public String getMedCatName() {
        return medCatName;
    }

    public void setMedCatName(String medCatName) {
        this.medCatName = medCatName;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNoRepeat() {
        return noRepeat;
    }

    public void setNoRepeat(String noRepeat) {
        this.noRepeat = noRepeat;
    }

    public String getGapeTime() {
        return gapeTime;
    }

    public void setGapeTime(String gapeTime) {
        this.gapeTime = gapeTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
