package com.example.radiobe.models;

import android.annotation.TargetApi;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;


public class User {
    /*Properties*/
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate; //TODO: check about Date in 21 api
    private String password;
    private String _id;
    private String _rev;
    private String fireBaseID;
    private List<String> favoritesID;

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
    public User(String firstName, String lastName, String email, String birthDate, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
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
        this.birthDate = birthDate;
        this.password = password;
        this.fireBaseID = fireBaseID;
    }


    /*full Constructor with database data*/
    public User(String firstName, String lastName, String email, String birthDate, String password, String _id, String _rev) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.password = password;
        this._id = _id;
        this._rev = _rev;
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
    public String getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    //getters setters for database
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String get_rev() {
        return _rev;
    }
    public void set_rev(String _rev) {
        this._rev = _rev;
    }


    /*ToString*/
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDate +
                ", password='" + password + '\'' +
                '}';
    }


}
