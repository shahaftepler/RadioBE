package com.example.radiobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.radiobe.fragments.MainScreen;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreen extends AppCompatActivity {
    FirebaseUser firebaseUser;
    AccessToken accessToken;
    boolean isLoggedIn;
    FirebaseAuth firebaseAuth;
//    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        AndroidThreeTen.init(this);
//        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        ImageView gifImageView = findViewById(R.id.ivLoadingGif);
        Glide.with(this).
                asGif().
                load(R.drawable.loading_radio).
                into(gifImageView);


//        new JsonToDatabase(() -> {       //using my call back to change activity
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }).execute();


        //the listener is passed to stream dao method json parse and then to the task. as soon as the task finished it returns done, and it goes all the way back.
        StreamDAO.getInstance().jsonParse(
                () -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
        );
    }


    private boolean checkFacebookUserIsLoggedIn() {
        accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();

        return isLoggedIn;
//        if(isLoggedIn){
//            handleFacebookToken(accessToken);
//        }
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseUser = firebaseAuth.getCurrentUser();

                            updateUI(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SplashScreen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser firebaseUser) {
        Toast.makeText(SplashScreen.this, "Facebook user is already logged in",
                Toast.LENGTH_SHORT).show();
        if(firebaseUser != null){
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }
    }
}


//     TODO: try to understand why it didn't work here in the jsonToDatabase execution.
//              firebaseAuth = FirebaseAuth.getInstance();
//            firebaseUser = firebaseAuth.getCurrentUser();
//            if (firebaseUser != null) {
//                checkFacebookUserIsLoggedIn();
//                Intent intent = new Intent(this, MainScreen.class);
//                startActivity(intent);
//                finish();
//            }