package com.example.radiobe.database;

import androidx.annotation.NonNull;

import com.example.radiobe.models.RadioItem;
import com.example.radiobe.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CurrentUser extends User {
   private static CurrentUser instance;
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String birthDate; //TODO: check about Date in 21 api
//    private String fireBaseID;
//
//    private String[] favoritesID = null;
    boolean once = true;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    public static CurrentUser getInstance(){
        if (instance == null){
            instance = new CurrentUser();
        }
        return instance;
    }

    private CurrentUser(){}

    public void createUser(String fireBaseID){
        ref.child("users").child(fireBaseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    instance = dataSnapshot.getValue(CurrentUser.class);

//                    instance.firstName = user.getFirstName();
//                    instance.lastName = user.getLastName();
//                    instance.email = user.getEmail();
//                    instance.birthDate = user.getBirthDate();
//                    instance.fireBaseID = user.getFireBaseID();
//                    instance.favoritesID = user.getFavoritesID();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
