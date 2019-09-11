package com.example.radiobe.models;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class User {
    /*Properties*/
    private String firstName;
    private String lastName;
    private String email;
    private String birthDateString; //TODO: check about Date in 21 api
    private String password;
    private String _id;
    private String _rev;
    private String fireBaseID;
    private List<String> favoritesID;
    Bitmap profileImage;
    Bitmap coverImage;
    private Date birthDate;
    String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public Bitmap getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }

    public List<String> getFavoritesID() {
        return favoritesID;
    }

    public void setFavoritesID(List<String> favoritesID) {
        this.favoritesID = favoritesID;
    }

    public User(){}

    //ctor to check if this user already liked this post.
    public User(String email){
        this.email = email;
    }

    //ctor for firebase
    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    /*full Constructor*/
    public User(String firstName, String lastName, String email, Date birthDate, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        String birth = DateFormat.format("dd/MM/yyyy", birthDate).toString();
        setBirthDateString(birth);
        this.password = password;
    }

    public String getFireBaseID() {
        return fireBaseID;
    }

    public void setFireBaseID(String fireBaseID) {
        this.fireBaseID = fireBaseID;
    }

    public User(String firstName, String lastName, String email, String birthDate, String password, String fireBaseID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDateString = birthDate;
        this.password = password;
        this.fireBaseID = fireBaseID;
    }

    /*Getters and Setters*/

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        String birth = DateFormat.format("dd/MM/yyyy", birthDate).toString();
        setBirthDateString(birth);
    }

    public String getBirthDateString() {
        return birthDateString;
    }
    public void setBirthDateString(String birthDate) {
        this.birthDateString = birthDate;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    /*ToString*/
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDateString +
                ", password='" + password + '\'' +
                '}';
    }


}
