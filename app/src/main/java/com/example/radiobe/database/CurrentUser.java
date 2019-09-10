package com.example.radiobe.database;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.radiobe.R;
import com.example.radiobe.models.NotificationItem;
import com.example.radiobe.models.RadioItem;
import com.example.radiobe.models.User;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class CurrentUser extends User {
       private static CurrentUser instance;
        List<String> favoritesID;
        List<String> messagesID;
        HashMap<String, User> notificationSenders;
        List<RadioItem> favorites;
        List<NotificationItem> notifications;
        Context context;
        RefreshNotificationsListener refreshNotificationsListener;
        RefreshFavorites refreshFavoritesListener;

        long favoriteCount = -1;
        long notificationsCount = -1;
        Timer t = new Timer();
        Timer newTimer = new Timer();
        Timer favoritesTimer = new Timer();
        boolean once = true;

    public HashMap<String, User> getNotificationSenders() {
        return notificationSenders;
    }

    public List<RadioItem> getFavorites() {
        return instance.favorites;
    }

    public List<NotificationItem> getNotifications() {
        return instance.notifications;
    }

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }


    private CurrentUser() {
        favorites = new ArrayList<>();
        favoritesID = new ArrayList<>();
        notifications = new ArrayList<>();
        notificationSenders = new HashMap<>();
    }

    public void setContext(Context context) {
        this.context = context;
    }


        public void createUser(String fireBaseID , FinishedCurrentUserInit finishedCurrentUserInit){
            System.out.println("Started Create User");
            ref.child("users").child(fireBaseID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        instance = dataSnapshot.getValue(CurrentUser.class);
                        System.out.println("Created instance");
                        System.out.println(instance);


                        ref.child("favorites").child(fireBaseID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getChildrenCount() > 0) {
                                    favoriteCount = dataSnapshot.getChildrenCount();

                                        System.out.println("CLEAR");
                                        instance.favoritesID.clear();
                                        instance.favorites.clear();

                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                        instance.favoritesID.add(snap.getKey());
//                                        favorites.add(snap.getValue(RadioItem.class));
                                        System.out.println("Favorite Added");
                                    }


                                    System.out.println("--------------------- BEFORE INIT FAVORITES");
                                    System.out.println("FAVORITES" + instance.favorites.size());
                                    System.out.println("FAVORITES ID" + instance.favoritesID.size());
                                    initFavorites();

                                } else{
                                    System.out.println("NO FAVORITES");
                                }

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        ref.child("notifications").child(fireBaseID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getChildrenCount() > 0) {
                                    notificationsCount = dataSnapshot.getChildrenCount();
                                    System.out.println(notificationsCount);

                                    if(!once)
                                        instance.notifications.clear();
                                        instance.notificationSenders.clear();
                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                        instance.notifications.add(snap.getValue(NotificationItem.class));
                                        System.out.println("Notification Added");
                                    }

                                    initNotifications();

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
                            checkLoadingComplete(finishedCurrentUserInit);
                        }
                    }, 0 , 250);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    private void initFavorites() {
        instance.favorites = new ArrayList<>();
        System.out.println(once);

        if (!once) {
            favoritesTimer = new Timer();
            favoritesTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    checkFavoriteLoading();
                }

            }, 0, 250);
        }

        for (String key : instance.favoritesID){
            for (RadioItem stream : FirebaseItemsDataSource.getInstance().getFireBaseStreams()){
                if (key.equals(stream.getUid())){
                    System.out.println("ABOUT TO ADD FAVORITE" + instance.favorites.size());
                    instance.favorites.add(stream);
                }
            }





//            ref.child("streams").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.getValue() != null) {
//
//                        System.out.println("ABOUT TO ADD FAVORITE" + instance.favorites.size());
//
//                        instance.favorites.add(dataSnapshot.getValue(RadioItem.class));
//                        System.out.println("Favorite converted");
//                    } else {
//                        System.out.println("Something is wrong");
//                    }
//
//
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }


    }

    private void checkFavoriteLoading() {
        System.out.println("IN FAVORITE LOADING");
        System.out.println(instance.favoritesID.size() + "FAVORITES ID");
        System.out.println(instance.favorites.size() + "FAVORITES");
        System.out.println(favoriteCount + "COUNT");
        if (instance.favoritesID.size() == instance.favorites.size() && instance.favorites.size() == favoriteCount) {
            System.out.println(instance.refreshFavoritesListener);
            System.out.println("HELLO!");
            System.out.println("Favorites Timer");
            favoritesTimer.cancel();
            favoritesTimer.purge();
            if (instance.refreshFavoritesListener != null) {     //todo: find out why null.
                instance.refreshFavoritesListener.refresh(instance.favorites);
            }
        }
    }

    private void checkNotificationsLoading() {

        System.out.println("Inside Notifications Timer");
        System.out.println(instance.notifications.size() + "NOTIFICATIONS");
        System.out.println(instance.notificationSenders.keySet().size() + "Senders");
        System.out.println(notificationsCount);
        if (instance.notificationSenders.keySet().size() == instance.notifications.size() && instance.notifications.size() == notificationsCount) {
            System.out.println("Sizes are good");
            newTimer.cancel();
            newTimer.purge();
            if (instance.refreshNotificationsListener != null) {
                System.out.println("listener not null");
                instance.refreshNotificationsListener.refresh(instance.notifications, instance.notificationSenders);

            }
        }
    }


    //todo: check if new arraylist size is 0
        public void checkLoadingComplete(FinishedCurrentUserInit finishedCurrentUserInit){

            if(instance.notifications != null) {
                if (instance.favorites.size() == favoriteCount && instance.notifications.size() == notificationsCount &&
                        instance.notificationSenders.keySet().size() == notificationsCount) {
                    t.cancel();
                    t.purge();
                    once = false;
                    if (finishedCurrentUserInit != null)
                        finishedCurrentUserInit.done();
                    //stop the timer and update th elistener

                }
            }else {
                System.out.println("WTF");

            }
        }

        public void setNotificationsListener(RefreshNotificationsListener refreshNotificationsListener ){
            this.refreshNotificationsListener = refreshNotificationsListener;
        }

    public void setRefreshFavoritesListener(RefreshFavorites refreshFavoritesListener) {
        this.refreshFavoritesListener = refreshFavoritesListener;
        System.out.println(this.refreshFavoritesListener + "Current User listener");
    }

    public void initNotifications(){

        if (!once) {
            newTimer = new Timer();
            newTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    checkNotificationsLoading();
                }

            }, 0, 250);
        }

            for (NotificationItem notification : instance.notifications) {
                System.out.println(notifications);
                System.out.println(notification);
                //get sender profile and name for the notification
                ref.child("users").child(notification.getSenderID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User sender = dataSnapshot.getValue(User.class);
                        System.out.println("Sender created!"+sender.getFirstName());
                        storageRef.child("profile/").child(sender.getFireBaseID())
                                .getBytes(2048*2048)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        sender.setProfileImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                        System.out.println("Got sender Profile image");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            sender.setProfileImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image));
                                            System.out.println("Got fake picture");
                                        }
                        });

                        //putting in a dictionary.
                        instance.notificationSenders.put(notification.getUid() , sender);
                        System.out.println("User added to dictionary");
//                        System.out.println(notificationSenders.get(notification.getUid()));

//
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

        }

}

