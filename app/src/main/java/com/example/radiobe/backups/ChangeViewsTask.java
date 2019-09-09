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
//import java.util.List;
//
//public class ChangeViewsTask extends AsyncTask<Void, Void, Void> {
//
//
//    private boolean newUserInitialized;
//    private FirebaseUser firebaseUser;
//    private RadioItem radioItem;
//
//    private final String API_KEY = "wooducessideresedlyinchi";
//    private final String API_PASS = "7ac241ffc5b2876163373ff4f273570a3191a184";
//    private final String DB_USER_NAME = "2115411f-0004-4f8b-93f0-0e70c716455b-bluemix";
//    private final String DB_STREAMS = "streams";
//    private final String DB_USERS = "users";
//
//    private User newUser;
//    private ChangeViewsListener changeViewsListener;
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
//    public ChangeViewsTask(FirebaseUser firebaseUser, RadioItem radioItem, ChangeViewsListener changeViewsListener) {
//        this.firebaseUser = firebaseUser;
//        this.radioItem = radioItem;
//        this.changeViewsListener = changeViewsListener;
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//
//        accessDb();
//
//        if (firebaseUser != null) {
//
////            User user = new User(firebaseUser.getEmail());
//            List<RadioItem> streams = streamsDataBase.findByIndex(query, RadioItem.class);
//            List<User> users = usersDataBase.findByIndex(query, User.class);
//
//
//            for (User user : users) {
//                if (firebaseUser.getEmail().equals(user.getEmail())) {
//                    newUser = user;
//                    newUserInitialized = true;
//                }
//            }
//
//            if(!newUserInitialized){
//                newUser = new User(firebaseUser.getEmail());
//            }
//
//
//            for (RadioItem item : streams) {
//                if (radioItem.get_id().equals(item.get_id())) {
//                    List<User> streamViews = item.getUsersThatViewed();
//                    for (User streamViewedUser : streamViews) {
//                        if (streamViewedUser.getEmail().equals(newUser.getEmail())) {
//                            return null;
//                        }
//                    }
//
//                    //if the stream doesn't have this user to like him, set likes and update database.
//                    item.addViewedUser(newUser);
//                    item.setViews(item.getViews() + 1);
//                    streamsDataBase.update(item);
//                    System.out.println("Data Viewed!");
//                }
//            }
////                streamsDataBase.remove(item);
//
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        changeViewsListener.done();
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
//}
//
