//package com.example.radiobe.database;
//
//import android.os.AsyncTask;
//import android.provider.MediaStore;
//
//import com.cloudant.client.api.ClientBuilder;
//import com.cloudant.client.api.CloudantClient;
//import com.cloudant.client.api.Database;
//import com.example.radiobe.StreamDAO;
//import com.example.radiobe.models.RadioItem;
//import com.example.radiobe.models.User;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChangeLikesStatusTask extends AsyncTask<Void, Void, Void> {
//
//    private boolean newUserInitialized = false;
//    private FirebaseUser firebaseUser;
//    private RadioItem radioItem;
//
//    private final String API_KEY = "wooducessideresedlyinchi";
//    private final String API_PASS = "7ac241ffc5b2876163373ff4f273570a3191a184";
//    private final String DB_USER_NAME = "2115411f-0004-4f8b-93f0-0e70c716455b-bluemix";
//    private final String DB_STREAMS = "streams";
//    private final String DB_USERS = "users";
//    private boolean isMatchingRev;
//
//    boolean addLike;
//    private User newUser;
//
//    private ChangeLikesListener changeLikesListener;
//    private List<RadioItem> radioItemsList = new ArrayList<>();
//    private CloudantClient client;
//    private Database streamsDataBase;
//    private Database usersDataBase;
//    private String query = "{\n" +
//            "   \"selector\": {\n" +
//            "      \"_id\": {\n" +
//            "         \"$gt\": \"0\"\n" +
//            "      }\n" +
//            "   },\n" +
//            "   \"fields\": [\n" +
//            "      \"_id\",\n" +
//            "      \"_rev\"\n" +
//            "   ],\n" +
//            "   \"sort\": [\n" +
//            "      {\n" +
//            "         \"_id\": \"asc\"\n" +
//            "      }\n" +
//            "   ]\n" +
//            "}";
//
//    public ChangeLikesStatusTask(FirebaseUser firebaseUser, RadioItem radioItem, ChangeLikesListener changeLikesListener, List<RadioItem> radioItemsList) {
//        this.firebaseUser = firebaseUser;
//        this.radioItem = radioItem;
//        this.changeLikesListener = changeLikesListener;
//        this.radioItemsList = radioItemsList;
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        accessDb();
//////
////        if (firebaseUser != null) {
//
////            User user = new User(firebaseUser.getEmail());
//
//
////        List<RadioItem> streams = streamsDataBase.findByIndex(query, RadioItem.class);
//        List<User> users = usersDataBase.findByIndex(query, User.class);
//
//
//        for (User user : users) {
//            if (firebaseUser.getEmail().equals(user.getEmail())) {
//                newUser = user;
//                newUserInitialized = true;
//            }
//        }
//
//        if (!newUserInitialized) {
//            newUser = new User(firebaseUser.getEmail());
//            StreamDAO.getInstance().saveUserToDatabase(newUser);
//        }
//
////        && radioItem.get_rev().equals(item.get_rev())
//
//        for (RadioItem item : radioItemsList) {
//            if (radioItem.get_id().equals(item.get_id())) {
//                List<User> streamsLikes = radioItem.getUsersThatLiked();
//                for (int i = 0; i < streamsLikes.size(); i++) {
//                    if (streamsLikes.get(i).getEmail().equals(newUser.getEmail())) {
//                        radioItem.setLikes(radioItem.getLikes() - 1);
//                        streamsLikes.remove(i);
//                        radioItem.setUsersThatLiked(streamsLikes);
//                        System.out.println("Removed?!");
//                        streamsDataBase.update(radioItem);
//                        return null;
//
//                    }
//                }
//                    radioItem.setLikes(radioItem.getLikes() + 1);
//                    streamsLikes.add(newUser);
//                    radioItem.setUsersThatLiked(streamsLikes);
////                    radioItem.addLike(newUser);
////                    addLike = true;
//                    System.out.println("Data liked");
//                    streamsDataBase.update(radioItem);
//
//            }
//        }
//
//        return null;
//    }
//
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        changeLikesListener.done();
//    }
//
//    private void accessDb() {
//        client = ClientBuilder.account(DB_USER_NAME)
//                .username(API_KEY)
//                .password(API_PASS)
//                .build();
//        streamsDataBase = client.database(DB_STREAMS, false);
//        usersDataBase = client.database(DB_USERS, false);
//    }
//
//}
//
//
////                for (User user : radioItem.getUsersThatLiked()) {
////                    if (user.getEmail().equals(newUser.getEmail())) {
////                        radioItem.setLikes(radioItem.getLikes() - 1);
////                        int position = radioItem.getUsersThatLiked().indexOf(newUser);
////                        radioItem.getUsersThatLiked().remove(position);
//////                        radioItem.removeLike(newUser);
////                        List<User> david = new ArrayList<>();
////                        List<User> streamLikes = new ArrayList<>(radioItem.getUsersThatLiked());
//////                        streamLikes =  radioItem.getUsersThatLiked();
////                        radioItem.setUsersThatLiked(streamLikes);
////                        System.out.println("returning!");
////                        streamsDataBase.update(radioItem);
////                        addLike = false;
//
////                        return null;
////
////                    }
////                }
