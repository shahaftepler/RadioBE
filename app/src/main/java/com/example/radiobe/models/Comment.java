package com.example.radiobe.models;

import android.text.format.DateFormat;
import java.util.Date;

public class Comment {
    private String user;
    private String creationDateString;
    private long creationDate;
    private String description;
    private String uid;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Comment(){}


    public Comment(String user , long creationDate, String description) {
        this.user = user;
        this.description = description;
        this.creationDate = creationDate;
        convertCreationDateToString(creationDate);
        Date d = new Date(creationDate);
        setDate(d);
}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        return creationDateString;
    }

    private void setCreationDateString(String creationDateString) {
        this.creationDateString = creationDateString;

    }

    private void convertCreationDateToString(long creationDate){
        String newDate = DateFormat.format("dd/MM/yyyy", new Date(creationDate)).toString();
        setCreationDateString(newDate);
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

