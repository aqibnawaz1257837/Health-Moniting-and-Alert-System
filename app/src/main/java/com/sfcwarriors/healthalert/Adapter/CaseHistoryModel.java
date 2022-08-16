package com.sfcwarriors.healthalert.Adapter;

public class CaseHistoryModel {

    String date,detail,dname,userID;

    public CaseHistoryModel(String date, String detail, String dname, String userID) {
        this.date = date;
        this.detail = detail;
        this.dname = dname;
        this.userID = userID;
    }

    public CaseHistoryModel() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
