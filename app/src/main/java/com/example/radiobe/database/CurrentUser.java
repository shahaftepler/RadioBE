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

        long favoriteCount = 0;
        long notificationsCount = 0;
        Timer t = new Timer();
        Timer newTimer = new Timer();
        boolean once = true;

    public HashMap<String, User> getNotificationSenders() {
        return notificationSenders;
    }

    public void setNotificationSenders(HashMap<String, User> notificationSenders) {
        this.notificationSenders = notificationSenders;
    }


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
        instance.favorites.add(item);
    }
    public void removeFavorite(RadioItem item){instance.favorites.remove(item);}



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
                        ref.child("favorites").child(fireBaseID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    favoriteCount = dataSnapshot.getChildrenCount();
                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                        ref.child("streams").child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                instance.favorites.add(dataSnapshot.getValue(RadioItem.class));
                                                System.out.println("Favorite Added");
                                                System.out.println(favorites);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });





//                                        favorites.add(snap.getValue(RadioItem.class));
//                                        System.out.println("Favorite Added");
                                    }

                                }


                                    ref.child("notifications").child(fireBaseID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getChildrenCount() > 0) {
                                                notificationsCount = dataSnapshot.getChildrenCount();
                                                System.out.println(notificationsCount);
                                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                    instance.notifications.add(snap.getValue(NotificationItem.class));
                                                    System.out.println("Notification Added");
                                                    System.out.println(notifications);
                                                }

                                                initNotifications();

//                                                if(once) {
//
//                                                } else {
//                                                    initNotifications();//todo: update listener for notifications adapter.
//                                                }


                                            }

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


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

            if(favorites != null && notifications != null && !notificationSenders.isEmpty()){
            if (favorites.size() == favoriteCount  && notifications.size() == notificationsCount && notificationSenders.keySet().size() == notificationsCount){
                t.cancel();
                t.purge();
                once = false;
                if (finishedCurrentUserInit != null)
                    finishedCurrentUserInit.done();
                //stop the timer and update th elistener

            }
        }
        }

        public void setNotificationsListener(RefreshNotificationsListener refreshNotificationsListener ){
            this.refreshNotificationsListener = refreshNotificationsListener;
        }

        public void initNotifications(){
            for (NotificationItem notification : notifications) {
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
                                            //todo: setProfileImage Place Holder.
                                            sender.setProfileImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image));
                                            System.out.println("Got fake picture");
                                        }
                        });

                        //putting in a dictionary.
                        notificationSenders.put(notification.getUid() , sender);
                        System.out.println("User added to dictionary");
//                        System.out.println(notificationSenders.get(notification.getUid()));

//
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

            if (!once) {

                newTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        if (notificationSenders.keySet().size() == notifications.size()) {
                            if (refreshNotificationsListener != null) {
                                refreshNotificationsListener.refresh(notifications, notificationSenders);
                                newTimer.cancel();
                                newTimer.purge();
                            }
                        }
                    }

                }, 0, 250);
            }
        }

}

