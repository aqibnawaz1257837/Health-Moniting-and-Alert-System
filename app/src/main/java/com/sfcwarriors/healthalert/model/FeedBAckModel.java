package com.sfcwarriors.healthalert.model;

import android.widget.EditText;

public class FeedBAckModel {

    String name,email,phone,text;

    public FeedBAckModel(String name, String email, String phone, String text) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
