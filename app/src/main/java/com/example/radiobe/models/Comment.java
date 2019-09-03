package com.example.radiobe.models;

import android.text.format.DateFormat;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.Date;

public class Comment {
    String user;
    String creationDateString;
    long creationDate;
    String description;
    String commentId;
    String uid;

//    public Comment(String user, long creationDate, String description) {
//        this.user = user;
//        this.creationDate = creationDate;
//        this.description = description;
//        this.creationDateString =  DateFormat.format("dd/MM/yyyy", new Date(creationDate)).toString();
//    }


    public Comment(String user , long creationDate, String description) {
        this.user = user;
        this.description = description;
        this.creationDate = creationDate;
        setCreationDateString(creationDate);

    }
//SimpleDateFormat s1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//    SimpleDateFormat s2 = new SimpleDateFormat("ddMMyyyyHHmm");
//    Date d = s1.parse("02/11/2012 23:11");
//    String s3 = s2.format(d);
//    System.out.println(s3);
//    long l = Long.parseLong(s3);
//    System.out.println(l);



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public void setCreationDateString(long creationDate) {
//        this.creationDateString = creationDateString;
        creationDateString = DateFormat.format("dd/MM/yyyy", new Date(creationDate)).toString();
        System.out.println(creationDateString);

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

