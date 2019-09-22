package com.example.radiobe.database;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
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

public class FireBaseParseJson extends AsyncTask<Void, Void, List<RadioItem>> {

    private DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
    private List<RadioItem> streams = new ArrayList<>();
    private ParseJsonListener listener;
    private int idCount = 0;
    private ChangeProgress changeProgress;
    private List<RadioItem> serverStreams = new ArrayList<>();
    private long newDuration = -1;
    private Context context;

    public FireBaseParseJson(ParseJsonListener listener, ChangeProgress changeProgress , Context context) {
        this.listener = listener;
        this.changeProgress = changeProgress;
        this.context = context;
    }

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
                    Long creationDate = radioItem.getLong("creationDate");
                    String creationDateString = DateFormat.format("dd/MM/yyyy", new Date(creationDate)).toString();

                    String filePath = "https://be.repoai.com:5443/LiveApp/streams/vod/" + vodName;

                    String mUid = radioItem.getString("vodId");
                    RadioItem item = new RadioItem(vodName , itemName, creationDate , creationDateString , filePath , mUid);
                    streams.add(item);

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
    protected void onPostExecute(List<RadioItem> jsonStreams){

        FirebaseItemsDataSource.getInstance().setContext(context);
        FirebaseItemsDataSource.getInstance().setStreams(jsonStreams);
        serverStreams = null;


        // listener that get transffered from the datasource to notify that it finished to load data so you can do intent
        FirebaseItemsDataSource.getInstance().loadData(()->{
            if (listener != null) {
                listener.done();

//        new FirebaseItemsDataSource(null, null, jsonStreams);
            }
        }, ()->{            //listener that get transferred from the data source on every load being successful another +1 progress in the splash screen.
            if(changeProgress != null){
                changeProgress.change();
            }
        } );

    }


    private void saveItemsToDataBase(List<RadioItem> jsonStreams) {
        myDatabase.child("streams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                        for (RadioItem jsonStream : jsonStreams) {
                            boolean shouldWrite = true;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (jsonStream.getUid().equals(snapshot.getValue(RadioItem.class).getUid())) {
                                    shouldWrite = false;
                                    newDuration = snapshot.getValue(RadioItem.class).getDuration();
                                    System.out.println("Don't Save the item.");
                                    idCount++;
                                }
                            }

                        if (shouldWrite){
                            System.out.println("Save the item!");
                            setDurationFromFile(jsonStream);
                            myDatabase.child("streams").child(jsonStream.getUid()).setValue(jsonStream);
                        } else {
                            if (newDuration != -1) {
                                jsonStream.setDuration(newDuration);
                            }
                        }

                    }
                }
                    else{
                        setDurationsForList(jsonStreams);
                        for (RadioItem jsonStream : jsonStreams) {
                            serverStreams.add(jsonStream);
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

    private void setDurationsForList(List<RadioItem> streams){
        MediaMetadataRetriever newRetriever = new MediaMetadataRetriever();
        for (RadioItem stream : streams) {
            newRetriever.setDataSource(stream.getFilePath() , new HashMap<>());
            stream.setDuration(Long.parseLong(newRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        }
        newRetriever.release();
    }

    private void setDurationFromFile(RadioItem radioItem) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(radioItem.getFilePath(), new HashMap<>());
        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        radioItem.setDuration(duration);
        retriever.release();
    }

}

