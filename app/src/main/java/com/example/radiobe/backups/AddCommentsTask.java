//package com.example.radiobe.database;
//
//import android.os.AsyncTask;
//import android.provider.MediaStore;
//
//import com.cloudant.client.api.ClientBuilder;
//import com.cloudant.client.api.CloudantClient;
//import com.cloudant.client.api.Database;
//import com.example.radiobe.models.Comment;
//import com.example.radiobe.models.RadioItem;
//import com.example.radiobe.models.User;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.List;
//
//public class AddCommentsTask extends AsyncTask<Void,Void,Void> {
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
//    private final String API_KEY = "wooducessideresedlyinchi";
//    private final String API_PASS = "7ac241ffc5b2876163373ff4f273570a3191a184";
//    private final String DB_USER_NAME = "2115411f-0004-4f8b-93f0-0e70c716455b-bluemix";
//    private final String DB_STREAMS = "streams";
//    private final String DB_USERS = "users";
//
//    private boolean newUserInitialized = false;
//    private FirebaseUser firebaseUser;
//    private RadioItem radioItem;
//    private User newUser;
//    private Comment comment;
//    private AddCommentListener addCommentListener;
//    List<RadioItem> radioItemList;
//
//
//    public AddCommentsTask(FirebaseUser firebaseUser, RadioItem radioItem, Comment comment, AddCommentListener addCommentListener, List<RadioItem> radioItemList) {
//        this.firebaseUser = firebaseUser;
//        this.radioItem = radioItem;
//        this.comment = comment;
//        this.addCommentListener = addCommentListener;
//        this.radioItemList = radioItemList;
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        accessDb();
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
//        if(!newUserInitialized){
//            newUser = new User(firebaseUser.getEmail());
//        }
//
//
//        for (RadioItem item : radioItemList) {
//            if (radioItem.get_id().equals(item.get_id()) && radioItem.get_rev().equals(item.get_rev())) {
//                List<Comment> streamComments = radioItem.getCommentsArray();
//                streamComments.add(comment);
//                radioItem.setCommentsArray(streamComments);
//                radioItem.setComments(radioItem.getComments() + 1);
//                streamsDataBase.update(radioItem);
//                System.out.println("Comment Added!");
//            }
//        }
////                streamsDataBase.remove(item);
//
//
//
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        addCommentListener.done();
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
