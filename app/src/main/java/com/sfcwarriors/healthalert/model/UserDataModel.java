package com.sfcwarriors.healthalert.model;

import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;

import soup.neumorphism.NeumorphButton;

public class UserDataModel {
    String uname,uemal,uage,uaddress,uphone;
    String upassword;
    String gender;
    int status;
    String userProfileIImage;
    String id;

    public UserDataModel(String uname, String uemal, String uage, String uaddress, String uphone, String upassword, String gender, int status, String userProfileIImage, String id) {
        this.uname = uname;
        this.uemal = uemal;
        this.uage = uage;
        this.uaddress = uaddress;
        this.uphone = uphone;
        this.upassword = upassword;
        this.gender = gender;
        this.status = status;
        this.userProfileIImage = userProfileIImage;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserDataModel() {
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUemal() {
        return uemal;
    }

    public void setUemal(String uemal) {
        this.uemal = uemal;
    }

    public String getUage() {
        return uage;
    }

    public void setUage(String uage) {
        this.uage = uage;
    }

    public String getUaddress() {
        return uaddress;
    }

    public void setUaddress(String uaddress) {
        this.uaddress = uaddress;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserProfileIImage() {
        return userProfileIImage;
    }

    public void setUserProfileIImage(String userProfileIImage) {
        this.userProfileIImage = userProfileIImage;
    }
}
