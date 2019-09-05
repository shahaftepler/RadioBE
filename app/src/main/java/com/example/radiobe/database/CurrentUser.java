package com.example.radiobe.database;

import androidx.annotation.NonNull;

import com.example.radiobe.models.NotificationItem;
import com.example.radiobe.models.RadioItem;
import com.example.radiobe.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CurrentUser extends User {
       private static CurrentUser instance;
    //    private String firstName;
    //    private String lastName;
    //    private String email;
    //    private String birthDate; //TODO: check about Date in 21 api
    //    private String fireBaseID;
    //
    //    private String[] favoritesID = null;
        List<String> favoritesID;
        List<String> messagesID;

        List<RadioItem> favorites = new ArrayList<>();
        List<NotificationItem> notifications = new ArrayList<>();

        long favoriteCount = 0;
        long notificationsCount = 0;
        Timer t = new Timer();
        boolean once = true;

    public List<RadioItem> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<RadioItem> favorites) {
        this.favorites = favorites;
    }

    public List<NotificationItem> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }


    //todo: maybe find a different way, or combine with the addFavorite method in the datasource
    public void addFavorite(RadioItem item){
        favorites.add(item);
    }

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        public static CurrentUser getInstance(){
            if (instance == null){
                instance = new CurrentUser();
            }
            return instance;
        }

        private CurrentUser(){}

        public void createUser(String fireBaseID , FinishedCurrentUserInit finishedCurrentUserInit){
            ref.child("users").child(fireBaseID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        instance = dataSnapshot.getValue(CurrentUser.class);
                        ref.child("favorites").child(fireBaseID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0){
                                    favoriteCount = dataSnapshot.getChildrenCount();
                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                        favorites.add(snap.getValue(RadioItem.class));
                                    }


                                    System.out.println(favoritesID);
                                    ref.child("notifications").child(fireBaseID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getChildrenCount() > 0) {
                                                notificationsCount = dataSnapshot.getChildrenCount();
                                                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                                                    if(once) {
                                                        notifications.add(snap.getValue(NotificationItem.class));
                                                    } else {
                                                        //todo: update listener for notifications adapter.
                                                    }
                                                }

                                            }

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    checkLoadingComplete(finishedCurrentUserInit);
                }
            }, 0 , 250);
        }


        //todo: check if new arraylist size is 0
        public void checkLoadingComplete(FinishedCurrentUserInit finishedCurrentUserInit){
            if (favorites.size() == favoriteCount  && notifications.size() == notificationsCount){
                t.cancel();
                t.purge();
                once = false;
                if (finishedCurrentUserInit != null)
                    finishedCurrentUserInit.done();
                //stop the timer and update th elistener

            }
        }

    //    public void addFavorite(String itemID){
    //        List<String> fav = this.getFavoritesID();
    //        if (fav !=null) {
    //            fav.add(itemID);
    //            this.setFavoritesID(fav);
    //        } else {
    //            fav = new ArrayList<>();
    //            fav.add(itemID);
    //            this.setFavoritesID(fav);
    //        }
    //    }
    //
    //    public void removeFavorite(String itemID){
    //        List<String> fav = this.getFavoritesID();
    //        if (fav != null) {
    //            fav.remove(itemID);
    //            this.setFavoritesID(fav);
    //        }
    //    }
}

