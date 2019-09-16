package com.example.radiobe.models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RadioItem {

    //props
    private String itemName;
    private String filePath;
    private Long creationDate;
    private long duration;
    private long likes;
    private long views;
    private long comments;
    private int resImage;
    private String _id;
    private String _rev;
    private String durationString;
    private String vodName;
    private String mUid;


    //new fields
    private List<User> usersThatLiked = new ArrayList<>();
    private List<User> usersThatViewed = new ArrayList<>();
    private List<Comment> commentsArray = new ArrayList<>();

    Map<String , User> commentSenders = new HashMap<>();

    public Map<String, User> getCommentSenders() {
        return commentSenders;
    }

    public void setCommentSenders(Map<String, User> commentSenders) {
        this.commentSenders = commentSenders;
    }

    public void addSender(String commentId , User sender){
        System.out.println("INSIDE ADD SENDER");
        commentSenders.put(commentId , sender);
    }

    private String creationDateString;




    //empty ctor?
    public RadioItem(){}


    //from Database ctor.
    public RadioItem(long duration, String creationDateString, long likes, long views, long comments, int resImage , String _id, String _rev, String itemName , String filePath, String durationString ) {
        this.duration = duration;
        this.creationDateString = creationDateString;
        this.likes = likes;
        this.views = views;
        this.comments = comments;
        this.resImage = resImage;
        this._id = _id;
        this._rev = _rev;
        this.itemName = itemName;
        this.filePath = filePath;
        this.durationString = durationString;


    }

    //from firebase
    public RadioItem(long duration, String creationDateString, long likes, long views, long comments , String mUid, String itemName , String filePath, String durationString ) {
        this.duration = duration;
        this.creationDateString = creationDateString;
        this.likes = likes;
        this.views = views;
        this.comments = comments;
        this.mUid = mUid;
        this.itemName = itemName;
        this.filePath = filePath;
        this.durationString = durationString;


    }


    //from Api ctor.
    public RadioItem(long duration, String vodName ,String itemName, Long creationDate, String filePath) {
        this.vodName = vodName;
        this.duration = duration;
        this.itemName = itemName;
        this.creationDate = creationDate;
        this.filePath = filePath;
    }

    //from api after convert to string
    public RadioItem(long duration, String vodName ,String itemName, Long creationDate , String creationDateString, String filePath, String durationString, String mUid) {
        this.vodName = vodName;
        this.duration = duration;
        this.itemName = itemName;
        this.creationDate = creationDate;
        this.creationDateString = creationDateString;
        this.filePath = filePath;
        this.durationString = durationString;
        this.mUid = mUid;
    }

    //from api after convert to string
    public RadioItem(String vodName ,String itemName, Long creationDate , String creationDateString, String filePath, String mUid) {
        this.vodName = vodName;
        this.itemName = itemName;
        this.creationDate = creationDate;
        this.creationDateString = creationDateString;
        this.filePath = filePath;
        this.mUid = mUid;
    }


    //getters setters
    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
        String durationString;
        if (duration > 3_600_000) {
            durationString = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        } else {
            durationString = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }
        setDurationString(durationString);
    }

    public long getLikes() {
        return likes;
    }
    public void setLikes(long likes) {
        this.likes = likes;
    }
    public long getViews() {
        return views;
    }
    public void setViews(long views) {
        this.views = views;
    }
    public long getComments() {
        return comments;
    }
    public void setComments(long comments) {
        this.comments = comments;
    }
    public int getResImage() {
        return resImage;
    }
    public void setResImage(int resImage) {
        this.resImage = resImage;
    }


    public String getCreationDateString() {
        return creationDateString;
    }

    public void setCreationDateString(String creationDateString) {
        this.creationDateString = creationDateString;
    }

    //getters and setters for api
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getVodName() {
        return vodName;
    }

    public void setVodName(String vodName) {
        this.vodName = vodName;
    }

    //getters and setters for player


    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public List<User> getUsersThatLiked() {
        return usersThatLiked;
    }
    public void setUsersThatLiked(List<User> usersThatLiked) {
        this.usersThatLiked = usersThatLiked;
    }


    //for fire base
    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }


    public void addLike(User newUser){
//        for (User user : usersThatLiked) {
//            if(user.getEmail().equals(newUser.getEmail()))
//                return;
//        }
        usersThatLiked.add(newUser);
        System.out.println("Liked User added");
    }

    public void removeLike(User user){
        usersThatLiked.remove(user);
        System.out.println("Like user removed!");
    }

    public void addViewedUser(User user){
        usersThatViewed.add(user);
    }

    public List<User> getUsersThatViewed() {
        return usersThatViewed;
    }

    public void setUsersThatViewed(List<User> usersThatViewed) {
        this.usersThatViewed = usersThatViewed;
    }

    public List<Comment> getCommentsArray() {
        return commentsArray;
    }

    public void setCommentsArray(List<Comment> commentsArray) {
        this.commentsArray = commentsArray;
    }


    public void removeAllComments(){
        commentsArray.clear();
    }

    public void removeCommentSenders(){
        commentSenders.clear();
    }

    public void addComment(Comment comment){ commentsArray.add(comment); }

    //getters setters for cloud
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
    static int count = 0;
    public static RadioItem getItemFromHashMap(HashMap<String, Object> snapshot){
        count++;
//        int comments = (int) snapshot.get("comments");
//        int likes = (Integer) snapshot.get("likes");
//        int resImage = (Integer) snapshot.get("resImage");
//        int views = (Integer) snapshot.get("views");
        long creationDate = (long) snapshot.get("creationDate");
        String creationDateString = (String) snapshot.get("creationDateString");
        long duration = (long) snapshot.get("duration");
        String durationString = (String) snapshot.get("durationString");
        String filePath = (String) snapshot.get("filePath");
        String itemName = (String) snapshot.get("itemName");
        String vodName = (String) snapshot.get("vodName");
        String mUid = (String) snapshot.get("uid");

        System.out.println("NEW STATIC METHOD---> "+String.valueOf(count)+ itemName);
        return  new RadioItem(duration, vodName, itemName, creationDate, creationDateString, filePath,durationString, mUid);
//        return new RadioItem(duration, creationDateString, likes, views, comments, resImage , null, itemName, filePath, durationString);
    }



    @Override
    public String toString() {
        return "RadioItem{" + '\'' +
                ", itemName='" + itemName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", creationDate=" + creationDate +
                ", duration=" + duration +
                ", likes=" + likes +
                ", views=" + views +
                ", comments=" + comments +
                ", resImage=" + resImage +
                ", _id='" + _id + '\'' +
                '}';
    }
}

//1) boolean isFavorite in model
//2) listener in radio adapter
//3) update boolean in change favorites
//4) change ui in listener method

// maybe only listener?