package com.example.radiobe.database;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.radiobe.StreamDAO;
import com.example.radiobe.R;
import com.example.radiobe.adapters.RadioItemsAdapter;
import com.example.radiobe.fragments.MainScreen;
import com.example.radiobe.models.Comment;
import com.example.radiobe.models.RadioItem;
import com.example.radiobe.models.User;
import com.google.android.exoplayer2.Player;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FirebaseItemsDataSource implements SubjectServerUpdates{

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
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser;
    List<Comment> comments;
    Context context;
    Map<String, User> commentSenders;
    long commentsCount = -1;
    Timer commentsTimer;
    List<UpdateServer> updateServerListeners;
    final String UPDATE_LIKES = "updateLikes";
    final String UPDATE_COMMENTS = "updateComments";
    final String UPDATE_VIEWS = "updateViews";




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
        comments = new ArrayList<>();
        commentSenders = new HashMap<>();
        updateServerListeners = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
                RadioItem item = fireBaseStreams.get(i);
                //still empty. so won't crash but don't recognize.
                System.out.println("--------------------->" + item.getUid());
                ref.child("likes").child(item.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {
                            item.setLikes(dataSnapshot.getChildrenCount());
                            ref.child("streams").child(item.getUid()).child("likes").setValue(item.getLikes());

                            if (once) {
                                //update adapter
                                isDoneAll.add(true);
                                changeProgress.change();
                            } else {
//                                if (updateServer != null) {
//                                    updateServer.updateLikes(item);
                                    notifyServerObservers(item , UPDATE_LIKES);
//                                }
                            }

                        } else {
                            item.setLikes(0);
                            ref.child("streams").child(item.getUid()).child("likes").setValue(item.getLikes());

                            if(once) {
                                isDoneAll.add(true);
                                changeProgress.change();

                            }   else{
//                                if(updateServer != null){
//                                    updateServer.updateLikes(item);
                                    notifyServerObservers(item , UPDATE_LIKES);
//                                }
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("ERROR");

                    }
                });


                ref.child("views").child(item.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            item.setViews(dataSnapshot.getChildrenCount());
                            ref.child("streams").child(item.getUid()).child("views").setValue(item.getViews());


                            if (once) {
                                //update adapter
                                isDoneAll.add(true);
                                changeProgress.change();

                            } else {
//                                if (updateServer != null) {
//                                    updateServer.updateViews(item);
                                    notifyServerObservers(item , UPDATE_VIEWS);
//                                }
                            }

                        } else {
                            item.setViews(0);
                            ref.child("streams").child(item.getUid()).child("views").setValue(item.getViews());

                            if(once) {
                                isDoneAll.add(true);
                                changeProgress.change();

                            }   else{
//                                if(updateServer != null){
//                                    updateServer.updateViews(item);
                                    notifyServerObservers(item , UPDATE_VIEWS);
//                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ref.child("comments").child(item.getUid()).orderByChild("creationDate").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            item.setComments(dataSnapshot.getChildrenCount());
                            ref.child("streams").child(item.getUid()).child("comments").setValue(item.getComments());
                            commentsCount = dataSnapshot.getChildrenCount();

                            item.removeCommentSenders();
                            item.removeAllComments();
                            for (DataSnapshot snap : dataSnapshot.getChildren()){
                                item.addComment(snap.getValue(Comment.class));
                                System.out.println("Comment added!");
                            }



                            initComments(item.getCommentsArray() , item , changeProgress);


                        } else {
                            item.setComments(0);
                            ref.child("streams").child(item.getUid()).child("comments").setValue(item.getComments());
                            if(once) {
                                isDoneAll.add(true);
                                changeProgress.change();

                            }   else{
//                                if(updateServer != null){
//                                    updateServer.updateComments(item);
                                    notifyServerObservers(item , UPDATE_COMMENTS);
//                                }
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

    private void initComments(List<Comment> itemCommentsList , RadioItem item , ChangeProgress changeProgress) {

        if (!once) {
            commentsTimer = new Timer();
            commentsTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    checkCommentsLoading(item, itemCommentsList, changeProgress);

                }
            }, 0, 250);

        }

        if(itemCommentsList.size() > 0){
            for(Comment comment : itemCommentsList){
                System.out.println("********************************************" + comment.getUser());
                ref.child("users").child(comment.getUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            User sender = dataSnapshot.getValue(User.class);
                            System.out.println("Sender created!" + sender.getFirstName());
                            storageRef.child("profile").child(sender.getFireBaseID()).
                                    getBytes(2048 * 2048).
                                    addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            sender.setProfileImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                            System.out.println("GOT SENDER PROFILE IMAGE");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(sender.getFacebookURL() != null){
                                        System.out.println("**** instance facebook"+sender.getFacebookURL());
                                        new DownloadFacebookProfileImage(sender.getFacebookURL(), (bitmap)-> {
                                            if(bitmap!= null) {
                                                System.out.println("BITMAP IS NOT NULL");
                                                sender.setProfileImage(bitmap);
                                            } else {
                                                System.out.println("BITMAP IS NULL");
                                                sender.setProfileImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image));
                                                System.out.println("Got fake profile picture");
                                            }
                                        }).execute();

                                    } else {
                                        sender.setProfileImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image));
                                        System.out.println("Got fake picture");
                                    }





//
//

                                }
                            });

                            item.addSender(comment.getUid() , sender);
                            System.out.println(commentSenders.keySet().size());
                            System.out.println("USER ADDED TO DIC");


                        } else {
                            System.out.println("NULL");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            if (once) {
                //update adapter
                System.out.println("BOOLEAN ADDED");
                isDoneAll.add(true);
                changeProgress.change();

            }
        }
    }

    private void checkCommentsLoading(RadioItem item , List<Comment> comments , ChangeProgress changeProgress) {
        System.out.println("INSIDE COMMENTS TIMER");
        System.out.println(item.getCommentsArray().size() + "COMMENTS SIZE FOR ITEM");
        System.out.println(item.getCommentSenders().size() + "SENDERS SIZE");
        System.out.println(item.getComments() + "COUNT");

        if(item.getCommentsArray().size() == item.getCommentSenders().size() &&
            item.getCommentsArray().size() == item.getComments()){
            commentsTimer.cancel();
            commentsTimer.purge();


            if (once) {
                //update adapter
//                isDoneAll.add(true);
//                changeProgress.change();

            } else {
                System.out.println("INSIDE ELSE");
//                if (updateServer != null) {
//                    updateServer.updateComments(item);
                    notifyServerObservers(item, UPDATE_COMMENTS);
//                }
            }

        }
    }


    public void addFavorites(RadioItem radioItem){
        FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();
        if(newUser != null) {
            ref.child("favorites").child(newUser.getUid()).child(radioItem.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        ref.child("favorites").child(newUser.getUid()).child(radioItem.getUid()).removeValue();
                        System.out.println("Suppose to take off favorite");

                    } else {
                        ref.child("favorites").child(newUser.getUid()).child(radioItem.getUid()).setValue(ServerValue.TIMESTAMP);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("DATA ERROR LIKES");
                }
            });
        }
    }


    public void addView(RadioItem radioItem){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref.child("views").child(radioItem.getUid()).child(firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
//        ref.child("streams").child(radioItem.getUid()).child("views").setValue(radioItem.getViews());

    }

    public void addComment(Comment comment, RadioItem radioItem){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String key = ref.child("comments").child(radioItem.getUid()).child(firebaseUser.getUid()).push().getKey();
        comment.setUid(key);
        ref.child("comments").child(radioItem.getUid()).child(comment.getUid()).setValue(comment);
//        ref.child("streams").child(radioItem.getUid()).child("comments").setValue(radioItem.getComments());

    }

    public void addLikes(RadioItem radioItem){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void registerServerObserver(UpdateServer updateServerObserver) {
        if(!updateServerListeners.contains(updateServerObserver)) {
            System.out.println("SERVER LISTENER ADDED");
            updateServerListeners.add(updateServerObserver);
        }
    }

    @Override
    public void removeServerObserver(UpdateServer updateServerObserver) {
        if(updateServerListeners.contains(updateServerObserver)) {
            System.out.println("SERVER LISTENER REMOVED");
            updateServerListeners.remove(updateServerObserver);
        }
    }

    @Override
    public void notifyServerObservers(RadioItem item , String method) {

        for (UpdateServer serverListener : updateServerListeners) {
            System.out.println("NOTIFY SERVER OBSERVER");

            switch (method){
                case UPDATE_LIKES:
                    serverListener.updateLikes(item);
                    break;
                case UPDATE_COMMENTS:
                    serverListener.updateComments(item);
                    break;

                case UPDATE_VIEWS:
                    serverListener.updateViews(item);
                    break;
            }


        }
    }
}

    interface DoneUpdatingLikes{
    void done();
        }






