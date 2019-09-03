package com.example.radiobe.database;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.radiobe.StreamDAO;
import com.example.radiobe.adapters.RadioItemsAdapter;
import com.example.radiobe.fragments.MainScreen;
import com.example.radiobe.models.Comment;
import com.example.radiobe.models.RadioItem;
import com.google.android.exoplayer2.Player;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FirebaseItemsDataSource{

    private WeakReference<RecyclerView> rvRadioItems;
    private WeakReference<ProgressBar> progressBar;
    List<RadioItem> fireBaseStreams;
    List<Boolean> isDoneAll = new ArrayList<>();
    Timer t;
    boolean once = true;
    public RecyclerView recyclerView = null;
    public RadioItemsAdapter adapter = null;
    private static FirebaseItemsDataSource instance;
    UpdateServer updateServer;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public static FirebaseItemsDataSource getInstance() {
        if (instance == null)
            instance = new FirebaseItemsDataSource();

//        else
//            instance.listener = null;

        return instance;
    }


    private FirebaseItemsDataSource(){
        t = new Timer();
        fireBaseStreams = new ArrayList<>();
    }

    public void setUpdateLikes(UpdateServer updateServer){
        this.updateServer = updateServer;
    }

//    public FirebaseItemsDataSource(RecyclerView rvRadioItems, ProgressBar progressBar, List<RadioItem> fireBaseStreams) {
//        this.rvRadioItems = new WeakReference<>(rvRadioItems);
//        this.progressBar = new WeakReference<>(progressBar);
//        this.fireBaseStreams = fireBaseStreams;
//        this.loadData();
//        t = new Timer();
//    }


//    public void initGeneral(RecyclerView rvRadioItems, ProgressBar progressBar){
//        this.rvRadioItems = new WeakReference<>(rvRadioItems);
//        this.progressBar = new WeakReference<>(progressBar);
//        this.loadData();
//        t = new Timer();
//    }

    public void setStreams(List<RadioItem> jsonStreams){
        fireBaseStreams = jsonStreams;
    }

    public List<RadioItem> getFireBaseStreams(){
        return this.fireBaseStreams;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;

    }

    public void loadData(DoneUpdatingLikes doneUpdatingLikes , ChangeProgress changeProgress){
        //boolean once = true;
        if (fireBaseStreams.size() > 0) {
            for (int i = 0; i < fireBaseStreams.size(); i++) {
                int finalI = i;
                //still empty. so won't crash but don't recognize.
                System.out.println("--------------------->" + fireBaseStreams.get(finalI).getUid());
                ref.child("likes").child(fireBaseStreams.get(finalI).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {
                            fireBaseStreams.get(finalI).setLikes(dataSnapshot.getChildrenCount());

                            if (once) {
                                //update adapter
                                isDoneAll.add(true);
                                changeProgress.change();
                            } else {
                                if (updateServer != null) {
                                    updateServer.updateLikes(fireBaseStreams.get(finalI));
                                }
                            }

                        } else {
                            fireBaseStreams.get(finalI).setLikes(0);
                            if(once) {
                                isDoneAll.add(true);
                                changeProgress.change();

                            }   else{
                                if(updateServer != null){
                                    updateServer.updateLikes(fireBaseStreams.get(finalI));
                                }
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //if can't read, no likes
//                        fireBaseStreams.get(finalI).setLikes(0);
//                        if(once) {
//                            isDoneAll.add(true);
//                        }   else{
//                            if(updateLikes != null){
//                                updateLikes.update(fireBaseStreams.get(finalI));
//                            }
//                        }
                    }
                });


                ref.child("views").child(fireBaseStreams.get(finalI).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            fireBaseStreams.get(finalI).setViews(dataSnapshot.getChildrenCount());

                            if (once) {
                                //update adapter
                                isDoneAll.add(true);
                                changeProgress.change();

                            } else {
                                if (updateServer != null) {
                                    updateServer.updateViews(fireBaseStreams.get(finalI));
                                }
                            }

                        } else {
                            fireBaseStreams.get(finalI).setViews(0);
                            if(once) {
                                isDoneAll.add(true);
                                changeProgress.change();

                            }   else{
                                if(updateServer != null){
                                    updateServer.updateViews(fireBaseStreams.get(finalI));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ref.child("comments").child(fireBaseStreams.get(finalI).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            fireBaseStreams.get(finalI).setComments(dataSnapshot.getChildrenCount());

                            if (once) {
                                //update adapter
                                isDoneAll.add(true);
                                changeProgress.change();

                            } else {
                                if (updateServer != null) {
                                    updateServer.updateComments(fireBaseStreams.get(finalI));
                                }
                            }

                        } else {
                            fireBaseStreams.get(finalI).setComments(0);
                            if(once) {
                                isDoneAll.add(true);
                                changeProgress.change();

                            }   else{
                                if(updateServer != null){
                                    updateServer.updateComments(fireBaseStreams.get(finalI));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }




            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    checkLoadingComplete(doneUpdatingLikes);
                }
            }, 0 , 250);
        }

    }




    public void addFavorites(RadioItem radioItem){
        ref.child("favorites").child(firebaseUser.getUid()).child(radioItem.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //at the moment it updates only minus, no plus for likes. need to figure out.
                if (dataSnapshot.getValue() != null){
                    //TODO:
//                    CurrentUser.getInstance().removeFavorite(radioItem.getUid());
//                    ref.child("favorites").child(firebaseUser.getUid()).setValue(CurrentUser.getInstance().getFavoritesID());
                    ref.child("favorites").child(firebaseUser.getUid()).child(radioItem.getUid()).removeValue();
                    System.out.println("Suppose to take off favorite");

                } else {
                    //TODO:
//                    CurrentUser.getInstance().addFavorite(radioItem.getUid());
//                    ref.child("favorites").child(firebaseUser.getUid()).setValue(CurrentUser.getInstance().getFavoritesID());
                    ref.child("favorites").child(firebaseUser.getUid()).child(radioItem.getUid()).setValue(ServerValue.TIMESTAMP);
                }



//
//                        //show on ui - suppose to happen to on the delegate call on the dataChange on top.
//                        if (updateLikes != null){
//                            updateLikes.update(radioItem);
//                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("DATA ERROR LIKES");
            }
        });
    }


    public void addView(RadioItem radioItem){
        ref.child("views").child(radioItem.getUid()).child(firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
    }

    public void addComment(Comment comment, RadioItem radioItem){
        String key = ref.child("comments").child(radioItem.getUid()).child(firebaseUser.getUid()).push().getKey();
        comment.setUid(key);
        ref.child("comments").child(radioItem.getUid()).child(comment.getUid()).setValue(comment);
    }

    public void addLikes(RadioItem radioItem){

        System.out.println("Inside new method addLikes");
        if (firebaseUser != null) {

            if (radioItem.getUid() != null) {
                System.out.println("ID ------ NOT NULL");

                //works the opposite.

                ref.child("likes").child(radioItem.getUid()).child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //at the moment it updates only minus, no plus for likes. need to figure out.
                        if (dataSnapshot.getValue() != null){
                            radioItem.setLikes(radioItem.getLikes() - 1);
                            //remove from likes
                            ref.child("likes").child(radioItem.getUid()).child(firebaseUser.getUid()).removeValue();
//                        ref.child("likes").child(radioItem.getUid()).child(firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
                            System.out.println("Suppose to take off like");
                            //update the stream
                            ref.child("streams").child(radioItem.getUid()).child("likes").setValue(radioItem.getLikes());

                        } else {
                            radioItem.setLikes(radioItem.getLikes() + 1);
                            ref.child("likes").child(radioItem.getUid()).child(firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
                            ref.child("streams").child(radioItem.getUid()).child("likes").setValue(radioItem.getLikes());
                            System.out.println("Suppose to add like!");
                        }



//
//                        //show on ui - suppose to happen to on the delegate call on the dataChange on top.
//                        if (updateLikes != null){
//                            updateLikes.update(radioItem);
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("DATA ERROR LIKES");
                    }
                });
            }  else {
                    System.out.println("THE ID is null!");
                }
        }
    }

    public void checkLoadingComplete(DoneUpdatingLikes doneUpdatingLikes){
        if (isDoneAll.size() == (fireBaseStreams.size() * 3)) {

            //stop timer
            t.cancel();
            t.purge();
            once = false;
            doneUpdatingLikes.done();


        }
    }

    }

    interface DoneUpdatingLikes{
    void done();
        }
