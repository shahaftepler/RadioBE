//package com.example.radiobe.models;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
////public class RadioItemBackup
//
//public class RadioItem {
//
//    //props
//    private String itemName;
//    private String filePath;
//    private Long creationDate;
//    private long duration;
//    private int likes;
//    private int views;
//    private int comments;
//    private int resImage;
//    private String _id;
//    private String _rev;
//    private String durationString;
//    private String vodName;
//    private String mUid;
//
//
//    //new fields
//    private List<User> usersThatLiked = new ArrayList<>();
//    private List<User> usersThatViewed = new ArrayList<>();
//    private List<Comment> commentsArray = new ArrayList<>();
//
//
//    private String creationDateString;
//
//
//
//
//    //empty ctor?
//    public RadioItem(){}
//
//
//    //from Database ctor.
//    public RadioItem(long duration, String creationDateString, int likes, int views, int comments, int resImage , String _id, String _rev, String itemName , String filePath, String durationString ) {
//        this.duration = duration;
//        this.creationDateString = creationDateString;
//        this.likes = likes;
//        this.views = views;
//        this.comments = comments;
//        this.resImage = resImage;
//        this._id = _id;
//        this._rev = _rev;
//        this.itemName = itemName;
//        this.filePath = filePath;
//        this.durationString = durationString;
//
//
//    }
//
//    //from firebase
//    public RadioItem(long duration, String creationDateString, int likes, int views, int comments, int resImage , String mUid, String itemName , String filePath, String durationString ) {
//        this.duration = duration;
//        this.creationDateString = creationDateString;
//        this.likes = likes;
//        this.views = views;
//        this.comments = comments;
//        this.resImage = resImage;
//        this.mUid = mUid;
//        this.itemName = itemName;
//        this.filePath = filePath;
//        this.durationString = durationString;
//
//
//    }
//
//
//    //from Api ctor.
//    public RadioItem(long duration, String vodName ,String itemName, Long creationDate, String filePath) {
//        this.vodName = vodName;
//        this.duration = duration;
//        this.itemName = itemName;
//        this.creationDate = creationDate;
//        this.filePath = filePath;
//    }
//
//    //from api after convert to string
//    public RadioItem(long duration, String vodName ,String itemName, Long creationDate , String creationDateString, String filePath, String durationString) {
//        this.vodName = vodName;
//        this.duration = duration;
//        this.itemName = itemName;
//        this.creationDate = creationDate;
//        this.creationDateString = creationDateString;
//        this.filePath = filePath;
//        this.durationString = durationString;
//    }
//
//
//    //getters setters
//    public long getDuration() {
//        return duration;
//    }
//    public void setDuration(long duration) {
//        this.duration = duration;
//    }
//
//    public int getLikes() {
//        return likes;
//    }
//    public void setLikes(int likes) {
//        this.likes = likes;
//    }
//    public int getViews() {
//        return views;
//    }
//    public void setViews(int views) {
//        this.views = views;
//    }
//    public int getComments() {
//        return comments;
//    }
//    public void setComments(int comments) {
//        this.comments = comments;
//    }
//    public int getResImage() {
//        return resImage;
//    }
//    public void setResImage(int resImage) {
//        this.resImage = resImage;
//    }
//
//
//    public String getCreationDateString() {
//        return creationDateString;
//    }
//
//    public void setCreationDateString(String creationDateString) {
//        this.creationDateString = creationDateString;
//    }
//
//    //getters and setters for api
//    public String getItemName() {
//        return itemName;
//    }
//
//    public void setItemName(String itemName) {
//        this.itemName = itemName;
//    }
//
//    public String getFilePath() {
//        return filePath;
//    }
//
//    public void setFilePath(String filePath) {
//        this.filePath = filePath;
//    }
//
//    public Long getCreationDate() {
//        return creationDate;
//    }
//
//    public void setCreationDate(Long creationDate) {
//        this.creationDate = creationDate;
//    }
//
//    public String getVodName() {
//        return vodName;
//    }
//
//    public void setVodName(String vodName) {
//        this.vodName = vodName;
//    }
//
//    //getters and setters for player
//
//
//    public String getDurationString() {
//        return durationString;
//    }
//
//    public void setDurationString(String durationString) {
//        this.durationString = durationString;
//    }
//
//    public List<User> getUsersThatLiked() {
//        return usersThatLiked;
//    }
//    public void setUsersThatLiked(List<User> usersThatLiked) {
//        this.usersThatLiked = usersThatLiked;
//    }
//
////    public void changeLikeStatus(User user, boolean addLike){
////
////        if(addLike)
////            usersThatLiked.add(user);
////        else {
////            usersThatLiked.remove(user);
////        }
////    }
//
//    //for fire base
//    public String getUid() {
//        return mUid;
//    }
//
//    public void setUid(String mUid) {
//        this.mUid = mUid;
//    }
//
//
//    public void addLike(User newUser){
////        for (User user : usersThatLiked) {
////            if(user.getEmail().equals(newUser.getEmail()))
////                return;
////        }
//        usersThatLiked.add(newUser);
//        System.out.println("Liked User added");
//    }
//
//    public void removeLike(User user){
//        usersThatLiked.remove(user);
//        System.out.println("Like user removed!");
//    }
//
//    public void addViewedUser(User user){
//        usersThatViewed.add(user);
//    }
//
//    public List<User> getUsersThatViewed() {
//        return usersThatViewed;
//    }
//
//    public void setUsersThatViewed(List<User> usersThatViewed) {
//        this.usersThatViewed = usersThatViewed;
//    }
//
//    public List<Comment> getCommentsArray() {
//        return commentsArray;
//    }
//
//    public void setCommentsArray(List<Comment> commentsArray) {
//        this.commentsArray = commentsArray;
//    }
//
//    public void addComment(Comment comment){ commentsArray.add(comment); }
//
//    //getters setters for cloud
//    public String get_id() {
//        return _id;
//    }
//    public void set_id(String _id) {
//        this._id = _id;
//    }
//    public String get_rev() {
//        return _rev;
//    }
//    public void set_rev(String _rev) {
//        this._rev = _rev;
//    }
//    static int count = 0;
//    public static RadioItem getItemFromHashMap(HashMap<String, Object> snapshot){
//        count++;
////        int comments = (int) snapshot.get("comments");
////        int likes = (Integer) snapshot.get("likes");
////        int resImage = (Integer) snapshot.get("resImage");
////        int views = (Integer) snapshot.get("views");
//        long creationDate = (long) snapshot.get("creationDate");
//        String creationDateString = (String) snapshot.get("creationDateString");
//        long duration = (long) snapshot.get("duration");
//        String durationString = (String) snapshot.get("durationString");
//        String filePath = (String) snapshot.get("filePath");
//        String itemName = (String) snapshot.get("itemName");
//        String vodName = (String) snapshot.get("vodName");
//
//        System.out.println("NEW STATIC METHOD---> "+String.valueOf(count)+ itemName);
//        return  new RadioItem(duration, vodName, itemName, creationDate, creationDateString, filePath,durationString);
////        return new RadioItem(duration, creationDateString, likes, views, comments, resImage , null, itemName, filePath, durationString);
//    }
//
//
//
//    @Override
//    public String toString() {
//        return "RadioItem{" + '\'' +
//                ", itemName='" + itemName + '\'' +
//                ", filePath='" + filePath + '\'' +
//                ", creationDate=" + creationDate +
//                ", duration=" + duration +
//                ", likes=" + likes +
//                ", views=" + views +
//                ", comments=" + comments +
//                ", resImage=" + resImage +
//                ", _id='" + _id + '\'' +
//                '}';
//    }
//}
//
//
