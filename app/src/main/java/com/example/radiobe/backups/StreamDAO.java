//package com.example.radiobe;
//
//
//
//import androidx.annotation.NonNull;
//
//import com.cloudant.client.api.Database;
//import com.example.radiobe.database.AddCommentListener;
//import com.example.radiobe.database.AddCommentsTask;
//import com.example.radiobe.database.ChangeLikesListener;
//import com.example.radiobe.database.ChangeLikesStatusTask;
//import com.example.radiobe.database.ChangeProgress;
//import com.example.radiobe.database.ChangeViewsListener;
//import com.example.radiobe.database.ChangeViewsTask;
//import com.example.radiobe.database.DatabaseReaderListener;
//import com.example.radiobe.database.DatabaseReaderTask;
////import com.example.radiobe.database.FireBaseParseJson;
//import com.example.radiobe.database.FireBaseParseJson;
//import com.example.radiobe.database.ParseJsonListener;
//import com.example.radiobe.database.ParseJsonTask;
//import com.example.radiobe.database.SaveUserToDataBaseTask;
//import com.example.radiobe.models.Comment;
//import com.example.radiobe.models.RadioItem;
//import java.util.ArrayList;
//import java.util.List;
//import com.cloudant.client.api.ClientBuilder;
//import com.cloudant.client.api.CloudantClient;
//import com.example.radiobe.models.User;
//import com.google.firebase.auth.FirebaseUser;
//
//
//
////1) get streamsQuery
////2) write to database
////3) update database/
////4) delete database for favorites
////5) read from database
////6) write new data to database
//
//public class StreamDAO {
//    private final String API_KEY = "wooducessideresedlyinchi";
//    private final String API_PASS = "7ac241ffc5b2876163373ff4f273570a3191a184";
//    private final String DB_USER_NAME = "2115411f-0004-4f8b-93f0-0e70c716455b-bluemix";
//    private final String DB_STREAMS = "streams";
//    private final String DB_USERS = "users";
//
//
//    //new api for users data base.
////    Key:ctsestteakethistargellds
////    Password:645fc1fe96fb6252dc1da4bb5601c1e7768041f7
//
//
//    private final String query = "{\n" +
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
//
//    private static StreamDAO instance;
//    CloudantClient client;
//    //    private static com.cloudant.client.api.Database streamsDataBase;
//    private Database streamsDataBase;
//    private Database usersDataBase;
////    ParseJsonListener listener;
////    ChangeLikesListener changeLikesListener;
//
//    private List<RadioItem> streams = new ArrayList<>();
//
//    public static StreamDAO getInstance() {
//        if (instance == null)
//            instance = new StreamDAO();
//
////        else
////            instance.listener = null;
//
//        return instance;
//    }
//
//    //listener for json
////    public static StreamDAO getInstance(ParseJsonListener jsonListener) {
////
////        StreamDAO instance = getInstance();
////
////        instance.listener = jsonListener;
////
////        instance.jsonParse();
////
////        return instance;
////    }
//
//
//    private StreamDAO(){}
//
//
//    public void jsonParse(ParseJsonListener listener , ChangeProgress changeProgress){
//        new FireBaseParseJson(listener, changeProgress).execute();
////        new ParseJsonTask(listener).execute();
//    }
//
//    public void handleLikes(FirebaseUser firebaseUser, RadioItem radioItem, ChangeLikesListener changeLikesListener){
//
//        new DatabaseReaderTask(new DatabaseReaderListener() {
//            @Override
//            public void done(List<RadioItem> streams) {
//                 new ChangeLikesStatusTask(firebaseUser,radioItem, changeLikesListener, streams).execute();
//            }
//        }).execute();
//
//    }
//
//    public void handleViews(FirebaseUser firebaseUser, RadioItem radioItem, ChangeViewsListener changeViewsListener){
//        new ChangeViewsTask(firebaseUser,radioItem, changeViewsListener).execute();
//    }
//
//    public void handleComments(FirebaseUser firebaseUser, RadioItem radioItem, Comment comment, AddCommentListener addCommentListener){
//
//        new DatabaseReaderTask(new DatabaseReaderListener() {
//            @Override
//            public void done(List<RadioItem> streams) {
//                new AddCommentsTask(firebaseUser,radioItem,comment, addCommentListener, streams).execute();
//
//            }
//        }).execute();
//    }
//
//    public void saveUserToDatabase(User newUser){
//        new SaveUserToDataBaseTask(newUser).execute();
//    }
//
//
////    only use in an async task!
//    public List<RadioItem> getStreamsFromDataBase(){
//        accessDb();
//        return streamsDataBase.findByIndex(query, RadioItem.class);
//    }
//
//    public void accessDb() {
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
