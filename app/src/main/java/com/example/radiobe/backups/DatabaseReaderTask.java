//package com.example.radiobe.database;
//
//import android.os.AsyncTask;
//
//import com.cloudant.client.api.ClientBuilder;
//import com.cloudant.client.api.CloudantClient;
//import com.cloudant.client.api.Database;
//import com.example.radiobe.models.RadioItem;
//
//import java.util.List;
//
//public class DatabaseReaderTask extends AsyncTask<Void,Void, List<RadioItem>> {
//
//    private ChangeLikesListener changeLikesListener;
//
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
//    private final String API_KEY = "wooducessideresedlyinchi";
//    private final String API_PASS = "7ac241ffc5b2876163373ff4f273570a3191a184";
//    private final String DB_USER_NAME = "2115411f-0004-4f8b-93f0-0e70c716455b-bluemix";
//    private final String DB_STREAMS = "streams";
//    private final String DB_USERS = "users";
//    DatabaseReaderListener listener;
//
//    public DatabaseReaderTask(DatabaseReaderListener listener){
//        this.listener = listener;
//    }
//
//    @Override
//    protected List<RadioItem> doInBackground(Void... voids) {
//        accessDB();
//        return streamsDataBase.findByIndex(query, RadioItem.class);
//    }
//
//    @Override
//    protected void onPostExecute(List<RadioItem> radioItems) {
//        listener.done(radioItems);
//    }
//
//    private void accessDB(){
//        client = ClientBuilder.account(DB_USER_NAME)
//                .username(API_KEY)
//                .password(API_PASS)
//                .build();
//        streamsDataBase = client.database(DB_STREAMS, false);
//    }
//}
