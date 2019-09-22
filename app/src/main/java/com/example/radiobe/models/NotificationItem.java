package com.example.radiobe.models;


public class NotificationItem {
    /*Properties*/
    private String uid;
    private String senderID;
    private long creationDate;
    private String title;
    private String description;


    public NotificationItem(){}

    /*Constructor*/
    public NotificationItem(String uid, String senderID, long creationDate, String title, String description) {
        this.uid = uid;
        this.senderID = senderID;
        this.creationDate = creationDate;
        this.title = title;
        this.description = description;
    }

    /*Getters*/

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*ToString*/

    @Override
    public String toString() {
        return "NotificationItem{" +
                "uid='" + uid + '\'' +
                ", senderID='" + senderID + '\'' +
                ", creationDate=" + creationDate +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
