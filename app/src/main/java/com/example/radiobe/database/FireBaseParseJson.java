package com.example.radiobe.database;

import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;

import com.example.radiobe.models.RadioItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FireBaseParseJson extends AsyncTask<Void, Void, List<RadioItem>> {

    DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
    private List<RadioItem> streams = new ArrayList<>();
    ParseJsonListener listener;
    int idCount = 0;
    ChangeProgress changeProgress;
    private List<RadioItem> noDurationStreams = new ArrayList<>();

    @Override
    protected List<RadioItem> doInBackground(Void... voids) {
        String dataBaseLink = "https://be.repoai.com:5443/LiveApp/rest/broadcast/getVodList/0/100?fbclid=IwAR30CQkxhzJOKMIadGufFhaVJvKnus-KjgYkfOki5GcEeSLU9yDlER8MJ_0";
        try {
            URL url = new URL(dataBaseLink);
            URLConnection con = url.openConnection();

            InputStream in = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String json = builder.toString();

            try {
                JSONArray resultsArray = new JSONArray(json);


                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject radioItem = resultsArray.getJSONObject(i);

                    String vodName = radioItem.getString("vodName");
                    String change = vodName.replace('_', ' ');
                    String itemName = change.replace(".mp4", "");
//                    long creationDate = radioItem.getLong("creationDate");
                    Long creationDate = radioItem.getLong("creationDate");
                    String creationDateString = DateFormat.format("dd/MM/yyyy", new Date(creationDate)).toString();



//                    String filePath = "http://be.repoai.com:5080/WebRTCAppEE/streams/home/" + vodName;              //todo: notice that i changed the duration to long if there's any problem
                    String filePath = "https://be.repoai.com:5443/LiveApp/streams/vod/" + vodName;

                    String mUid = radioItem.getString("vodId");
//                    RadioItem item = new RadioItem(duration, vodName, itemName, creationDate, creationDateString, filePath, durationString, mUid);
                    RadioItem item = new RadioItem(vodName , itemName, creationDate , creationDateString , filePath , mUid);
                    streams.add(item);

//                    myDatabase.child("streams").setValue(item);
//                    streams.add(item);
//                    new DatabaseUpdater(DatabaseUpdater.SAVE_STREAM, item).execute();
//                    saveToDataBase(item); //todo check how to save outside the FOR loop
                }

                saveItemsToDataBase(streams);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return streams;
    }

    @Override
    protected void onPostExecute(List<RadioItem> jsonStreams) {

//        if (idCount == jsonStreams.size() - 1)
            FirebaseItemsDataSource.getInstance().setStreams(jsonStreams);

        FirebaseItemsDataSource.getInstance().loadData(()->{
            if (listener != null) {
                listener.done();

//        new FirebaseItemsDataSource(null, null, jsonStreams);
            }
        }, ()->{
            if(changeProgress != null){
                changeProgress.change();
            }
        } );

    }

    public FireBaseParseJson(ParseJsonListener listener, ChangeProgress changeProgress) {
        this.listener = listener;
        this.changeProgress = changeProgress;
//        idCount = 0;
    }

    //}
//
//
//
    private void saveItemsToDataBase(List<RadioItem> jsonStreams) {
//        myDatabase.child("streams").setValue(jsonStreams);

        myDatabase.child("streams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                        for (RadioItem jsonStream : jsonStreams) {
                            boolean shouldWrite = true;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                RadioItem item = RadioItem.getItemFromHashMap((HashMap<String, Object>) snapshot.getValue());
                                    RadioItem item = snapshot.getValue(RadioItem.class);
                                if (jsonStream.getUid().equals(item.getUid())) {
                                    shouldWrite = false;
                                    System.out.println("Don't Save the item.");

                                    //it does have an id! so why in the datasource the items doesn't have??
                                    System.out.println("NEW ITEM ID -----> :::: )))) "+ item.getUid());
//                                    jsonStream.setUid(item.getUid());
                                    idCount++;

                            }
                        }

                        if (shouldWrite){
                            System.out.println("Save the item!");
                            String key = myDatabase.child("streams").push().getKey();
//                            jsonStream.setUid(key);
                            setDurationFromFile(jsonStream);
                            System.out.println(key);
                            myDatabase.child("streams").child(jsonStream.getUid()).setValue(jsonStream);
                        }
                    }
                }
                    else{
//                        setDurationsForList(jsonStreams);
                        for (RadioItem jsonStream : jsonStreams) {
                            setDurationFromFile(jsonStream);
                            String key = myDatabase.child("streams").push().getKey();
//                            jsonStream.setUid(key);
                            System.out.println(key);
                            myDatabase.child("streams").child(jsonStream.getUid()).setValue(jsonStream);
                        }
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ERROR");
            }
        });


    }

//    private void setDurationsForList(List<RadioItem> streams){
//        MediaMetadataRetriever newRetriever = new MediaMetadataRetriever();
//        for (RadioItem stream : streams) {
//            newRetriever.setDataSource(stream.getFilePath() , new HashMap<>());
//            stream.setDuration(Long.parseLong(newRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
//
//        }
//
//        newRetriever.release();
//    }

    private void setDurationFromFile(RadioItem radioItem) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(radioItem.getFilePath(), new HashMap<>());
        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        radioItem.setDuration(duration);
        retriever.release();
    }


}

