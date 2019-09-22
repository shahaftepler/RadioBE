package com.example.radiobe.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.example.radiobe.R;
import com.example.radiobe.models.NotificationItem;
import com.example.radiobe.models.RadioItem;
import com.example.radiobe.models.User;
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
import java.util.Timer;
import java.util.TimerTask;

public class CurrentUser extends User implements NotificationsSubject , FavoritesSubject , SubjectUsernameRefresh , SubjectProfilePictureRefresh {
    @SuppressLint("StaticFieldLeak")
    private static CurrentUser instance;
    private List<String> favoritesID;
    private HashMap<String, User> notificationSenders;
    private List<RadioItem> favorites;
    private List<NotificationItem> notifications;
    private Context context;
    private long favoriteCount = -1;
    private long notificationsCount = -1;
    private Timer timer = new Timer();
    private Timer newTimer = new Timer();
    private Timer favoritesTimer = new Timer();
    private boolean once = true;
    private List<Boolean> photosUpload;
    private List<RefreshNotificationsListener> notificationsListeners;
    private List<RefreshFavorites> favoritesListeners;
    private List<RefreshUserName> userNameListeners;
    private List<RefreshProfilePicture> profilePictureListeners;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    public HashMap<String, User> getNotificationSenders() {
        return notificationSenders;
    }
    public List<RadioItem> getFavorites() {
        return instance.favorites;
    }
    public List<NotificationItem> getNotifications() {
        return instance.notifications;
    }
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
        photosUpload = new ArrayList<>();
        notificationsListeners = new ArrayList<>();
        favoritesListeners = new ArrayList<>();
        userNameListeners = new ArrayList<>();
        profilePictureListeners = new ArrayList<>();
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public void createUser(String fireBaseID, FinishedCurrentUserInit finishedCurrentUserInit) {
        System.out.println("Started Create User");
        ref.child("users").child(fireBaseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
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

                            } else {
                                System.out.println("NO FAVORITES");
                                favoriteCount = 0;
                                instance.favoritesID.clear();
                                instance.favorites.clear();
                                System.out.println("NEW NOTIFICATIONS SIZE " + instance.favorites.size());
                                System.out.println("NEW SENDERS SIZE " + instance.favoritesID.size());
                                initFavorites();
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

                                if (!once)
                                    instance.notifications.clear();
//                                    instance.notificationSenders.clear();

                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                    instance.notifications.add(snap.getValue(NotificationItem.class));
                                    System.out.println("Notification Added");
                                }

                                initNotifications();

                            } else {
                                notificationsCount = 0;
                                instance.notifications.clear();
                                instance.notificationSenders.clear();
                                System.out.println("NEW NOTIFICATIONS SIZE " + instance.notifications.size());
                                System.out.println("NEW SENDERS SIZE " + instance.notificationSenders.size());
                                initNotifications();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    storageRef.child("profile").child(fireBaseID)
                            .getBytes(2048 * 2048)
                            .addOnSuccessListener(bytes -> {
                                instance.setProfileImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                System.out.println("Got Current User Profile Image");
                                photosUpload.add(true);
                            }).addOnFailureListener(e -> {

                                //try facebook image if not put fake.
                                if(instance.getFacebookURL() != null){
                                    System.out.println("**** instance facebook"+instance.getFacebookURL());
                                    new DownloadFacebookProfileImage(instance.getFacebookURL(), (bitmap)-> {
                                        if(bitmap!= null) {
                                            System.out.println("BITMAP IS NOT NULL");
                                            instance.setProfileImage(bitmap);
                                            photosUpload.add(true);
                                        } else {
                                            System.out.println("BITMAP IS NULL");
                                            instance.setProfileImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image));
                                            System.out.println("Got fake profile picture");
                                            photosUpload.add(true);
                                        }
                                    }).execute();

                                }

                                else {
                                    instance.setProfileImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image));
                                    System.out.println("Got fake profile picture");
                                    photosUpload.add(true);
                                }

                            });

                    storageRef.child("cover").child(fireBaseID)
                            .getBytes(2048 * 2048)
                            .addOnSuccessListener(bytes -> {
                                instance.setCoverImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                System.out.println("COVER");
                                photosUpload.add(true);

                            }).addOnFailureListener(e -> {
                                instance.setCoverImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.color_blue));
                                System.out.println("Got fake COVER");
                                photosUpload.add(true);

                            });

                }

                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        checkLoadingComplete(finishedCurrentUserInit);
                    }
                }, 0, 250);

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

        if(instance.favoritesID.size() > 0) {

            for (String key : instance.favoritesID) {
                for (RadioItem stream : FirebaseItemsDataSource.getInstance().getFireBaseStreams()) {
                    if (key.equals(stream.getUid())) {
                        System.out.println("ABOUT TO ADD FAVORITE" + instance.favorites.size());
                        instance.favorites.add(stream);
                    }
                }

            }
        }


    }

    private void checkFavoriteLoading() {
        System.out.println("IN FAVORITE LOADING");
        System.out.println(instance.favoritesID.size() + "FAVORITES ID");
        System.out.println(instance.favorites.size() + "FAVORITES");
        System.out.println(favoriteCount + "COUNT");
        if (instance.favoritesID.size() == instance.favorites.size() && instance.favorites.size() == favoriteCount) {
            System.out.println("HELLO!");
            System.out.println("Favorites Timer");
            favoritesTimer.cancel();
            favoritesTimer.purge();
            notifyFavoriteObservers();
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

            notifyNotificationObservers();
        }
    }


    public void checkLoadingComplete(FinishedCurrentUserInit finishedCurrentUserInit) {
        System.out.println(photosUpload.size() + "BOOLEAN");

            System.out.println(instance.favorites.size() + "FAVORITES SIZe");
            System.out.println(favoriteCount + "FAVORITE COUNT");
            System.out.println(instance.notificationSenders.keySet().size() + "SENDERS SIZE");

            if ((instance.favorites.size() == favoriteCount) && (instance.notifications.size() == notificationsCount) &&
                    (instance.notificationSenders.keySet().size() == notificationsCount) && (photosUpload.size() == 2)) {
                timer.cancel();
                timer.purge();
                once = false;
                if (finishedCurrentUserInit != null)
                    finishedCurrentUserInit.done();

            }
        }

    public void loadDetailsFromMap(){
        ref.child("users").child(instance.getFireBaseID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    User user = dataSnapshot.getValue(User.class);
                    instance.setFirstName(user.getFirstName());
                    instance.setLastName(user.getLastName());
                    instance.setBirthDate(user.getBirthDate());
                    instance.setBirthDateString(user.getBirthDateString());
                    instance.setDescription(user.getDescription());
                    System.out.println(CurrentUser.getInstance().toString());
                    notifyUsernameObservers();
                } else{
                    System.out.println("NULL");
                }            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ERROR");
            }
        });
    }

    private void initNotifications() {

        if (!once) {
            newTimer = new Timer();
            newTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    checkNotificationsLoading();
                }

            }, 0, 250);
        }


        if (instance.notifications.size() > 0) {
            for (NotificationItem notification : instance.notifications) {
                System.out.println(notification);
                ref.child("users").child(notification.getSenderID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User sender = dataSnapshot.getValue(User.class);
                        System.out.println("Sender created!" + sender.getFirstName());
                        storageRef.child("profile").child(sender.getFireBaseID())
                                .getBytes(2048 * 2048)
                                .addOnSuccessListener(bytes -> {
                                    sender.setProfileImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                    System.out.println("Got sender Profile image");
                                }).addOnFailureListener(e -> {
                                    sender.setProfileImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image));
                                    System.out.println("Got fake picture");
                                });

                        //putting in a dictionary.
                        instance.notificationSenders.put(notification.getUid(), sender);
                        System.out.println(instance.notificationSenders.keySet().size());
                        System.out.println("User added to dictionary");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "Cancel: "+databaseError, Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    }


    //new observers!
    @Override
    public void registerFavoriteObserver(RefreshFavorites refreshFavoritesObserver) {
        if(!favoritesListeners.contains(refreshFavoritesObserver)) {
            System.out.println("FAVORITE LISTENER ADDED");
            favoritesListeners.add(refreshFavoritesObserver);
        }
    }

    @Override
    public void removeFavoriteObserver(RefreshFavorites refreshFavoritesObserver) {
        if(favoritesListeners.contains(refreshFavoritesObserver)) {
            System.out.println("FAVORITE LISTENER REMOVED");
            favoritesListeners.remove(refreshFavoritesObserver);
        }
    }

    @Override
    public void notifyFavoriteObservers() {
        for (RefreshFavorites observer: instance.favoritesListeners) {
            System.out.println("NOTIFY FAVORITE OBSERVER");
            observer.refresh(instance.favorites);
        }
    }

    @Override
    public void registerNotificationObserver(RefreshNotificationsListener refreshNotificationsListener) {
        if(!notificationsListeners.contains(refreshNotificationsListener)) {
            System.out.println("NOTIFICATION LISTENER ADDED");
            notificationsListeners.add(refreshNotificationsListener);
        }
    }

    @Override
    public void removeNotificationObserver(RefreshNotificationsListener refreshNotificationsListener) {
        if(notificationsListeners.contains(refreshNotificationsListener)) {
            System.out.println("NOTIFICATION LISTENER REmoved");
            notificationsListeners.remove(refreshNotificationsListener);
        }
    }

    @Override
    public void notifyNotificationObservers() {
        for (RefreshNotificationsListener observer: instance.notificationsListeners) {
            System.out.println("NOTIFICATION LISTENER notified");
            observer.refresh(instance.notifications , instance.notificationSenders);
        }
    }


    @Override
    public void registerUsernameObserver(RefreshUserName refreshUserName) {
        if(!userNameListeners.contains(refreshUserName)) {
            System.out.println("username LISTENER ADDED");
            userNameListeners.add(refreshUserName);
        }
    }

    @Override
    public void removeUsernameObserver(RefreshUserName refreshUserName) {
        if(userNameListeners.contains(refreshUserName)) {
            System.out.println("username LISTENER ADDED");
            userNameListeners.remove(refreshUserName);
        }
    }

    @Override
    public void notifyUsernameObservers() {
        for(RefreshUserName refreshUserName : userNameListeners){
            System.out.println("USER NAME NOTIFIED");
            refreshUserName.refresh();
        }
    }

    @Override
    public void registerProfilePictureObserver(RefreshProfilePicture refreshProfilePicture) {
        if(!profilePictureListeners.contains(refreshProfilePicture)){
            System.out.println("PROFILE LISTENER ADDED");
            profilePictureListeners.add(refreshProfilePicture);
        }
    }

    @Override
    public void removeProfilePictureObserver(RefreshProfilePicture refreshProfilePicture) {
        if(profilePictureListeners.contains(refreshProfilePicture)){
            System.out.println("PROFILE LISTENER ADDED");
            profilePictureListeners.remove(refreshProfilePicture);
        }
    }

    @Override
    public void notifyProfilePictureObservers() {
        for (RefreshProfilePicture refreshProfilePicture : profilePictureListeners) {
            refreshProfilePicture.refreshPicture();
        }
    }
}

