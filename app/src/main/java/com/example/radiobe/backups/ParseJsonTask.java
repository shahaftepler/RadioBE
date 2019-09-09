//package com.example.radiobe.database;
//
//import android.media.MediaMetadataRetriever;
//import android.os.AsyncTask;
//import android.text.format.DateFormat;
//
//import com.cloudant.client.api.ClientBuilder;
//import com.cloudant.client.api.CloudantClient;
//import com.cloudant.client.api.Database;
//import com.example.radiobe.models.RadioItem;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class ParseJsonTask extends AsyncTask<Void,Void,Void> {
//
//    private List<RadioItem> streams = new ArrayList<>();
//    private final String API_KEY = "wooducessideresedlyinchi";
//    private final String API_PASS = "7ac241ffc5b2876163373ff4f273570a3191a184";
//    private final String DB_USER_NAME = "2115411f-0004-4f8b-93f0-0e70c716455b-bluemix";
//    private final String DB_STREAMS = "streams";
//    private final String DB_USERS = "users";
//
//    private CloudantClient client;
//    private Database streamsDataBase;
//    private String streamsQuery = "{\n" +
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
//    ParseJsonListener listener;
//
//    public ParseJsonTask(ParseJsonListener listener){
//        this.listener = listener;
//    }
//
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//
//        accessDb();
////        String dataBaseLink = "http://be.repoai.com:5080/WebRTCAppEE/rest/broadcast/getVodList/0/100?fbclid=IwAR2rtbhZkchHYZ3rbREpP0gaqNVPkvNn7phnQthgM4OO1gl5elu5zZjlQrk";
//        String dataBaseLink = "https://be.repoai.com:5443/LiveApp/rest/broadcast/getVodList/0/100?fbclid=IwAR30CQkxhzJOKMIadGufFhaVJvKnus-KjgYkfOki5GcEeSLU9yDlER8MJ_0";
//        try {
//            URL url = new URL(dataBaseLink);
//            URLConnection con = url.openConnection();
//
//            InputStream in = con.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            StringBuilder builder = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//            }
//
//            String json = builder.toString();
//
//            try {
//                JSONArray resultsArray = new JSONArray(json);
//
//
//                for (int i = 0; i < resultsArray.length(); i++) {
//                    JSONObject radioItem = resultsArray.getJSONObject(i);
//
//                    String vodName = radioItem.getString("vodName");
//                    String change = vodName.replace('_', ' ');
//                    String itemName = change.replace(".mp4", "");
//                    long creationDate = radioItem.getLong("creationDate");
//
//                    String creationDateString = DateFormat.format("dd/MM/yyyy", new Date(creationDate)).toString();
//
//
////                    long duration = radioItem.getInt("duration");
////                    String filePath = "http://be.repoai.com:5080/WebRTCAppEE/streams/home/" + vodName;              //todo: notice that i changed the duration to long if there's any problem
//                    String filePath = "https://be.repoai.com:5443/LiveApp/streams/vod/" + vodName;
//
//
//                    long duration = getDurationFromFile(filePath);
//                    String durationString = convertDuration(duration);
//
//
//                    RadioItem item = new RadioItem(duration, vodName, itemName, creationDate, creationDateString, filePath, durationString, null);
//                    //            streams.add(item);
//
//                    streams.add(item);
////                    new DatabaseUpdater(DatabaseUpdater.SAVE_STREAM, item).execute();
////                    saveToDataBase(item); //todo check how to save outside the FOR loop
//                }
//
//                saveItemsToDataBase(streams);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private long getDurationFromFile(String filePath) {
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(filePath, new HashMap<>());
//        long l = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//        retriever.release();
//        return l;
//    }
//
//    private String convertDuration(long duration){
//        String durationString;
//        if (duration > 3_600_000) {
//            durationString = String.format("%02d:%02d:%02d",
//                    TimeUnit.MILLISECONDS.toHours(duration),
//                    TimeUnit.MILLISECONDS.toMinutes(duration) -
//                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), // The change is in this line
//                    TimeUnit.MILLISECONDS.toSeconds(duration) -
//                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
//        } else {
//            durationString = String.format("%02d:%02d",
//                    TimeUnit.MILLISECONDS.toMinutes(duration) -
//                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), // The change is in this line
//                    TimeUnit.MILLISECONDS.toSeconds(duration) -
//                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
//        }
//
//        return durationString;
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        if (listener != null)
//            listener.done();
//    }
//
//    private void saveItemsToDataBase(List<RadioItem> jsonStreams) {
//        List<RadioItem> items = streamsDataBase.findByIndex(streamsQuery, RadioItem.class);
//        for (RadioItem jsonItem : jsonStreams) {
//            for (RadioItem item : items) {
//                if (jsonItem.getVodName().equals(item.getVodName()))
//                    return;
//
//            }
//            streamsDataBase.save(jsonItem);
//        }
//
//    }
//
//    private void accessDb() {
//        client = ClientBuilder.account(DB_USER_NAME)
//                .username(API_KEY)
//                .password(API_PASS)
//                .build();
//        streamsDataBase = client.database(DB_STREAMS, false);
//    }
//
//}
//
//
