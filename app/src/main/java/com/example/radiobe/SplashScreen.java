package com.example.radiobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.radiobe.database.ChangeProgress;
import com.example.radiobe.database.FireBaseParseJson;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SplashScreen extends AppCompatActivity {
    FirebaseUser firebaseUser;
    AccessToken accessToken;
    boolean isLoggedIn;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
//    ChangeProgress changeProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = findViewById(R.id.progressBar);

        ImageView gifImageView = findViewById(R.id.ivLoadingGif);
        Glide.with(this).
                asGif().
                load(R.drawable.loading_radio).
                into(gifImageView);


        //the listener is passed to stream dao method json parse and then to the task. as soon as the task finished it returns done, and it goes all the way back.
//        StreamDAO.getInstance().jsonParse(
//                () -> {
//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }, new ChangeProgress() {
//                    @Override
//                    public void change() {
//                        progressBar.setProgress(progressBar.getProgress() + 1);
//                    }
//                }
//
//        );

        new FireBaseParseJson(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }, new ChangeProgress() {
            @Override
            public void change() {
                progressBar.setProgress(progressBar.getProgress() + 1);
            }
        }).execute();


    }


}


//    private boolean checkFacebookUserIsLoggedIn() {
//        accessToken = AccessToken.getCurrentAccessToken();
//        isLoggedIn = accessToken != null && !accessToken.isExpired();
//
//        return isLoggedIn;
////        if(isLoggedIn){
////            handleFacebookToken(accessToken);
////        }
//    }
//
//    private void handleFacebookToken(AccessToken accessToken) {
//        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            firebaseUser = firebaseAuth.getCurrentUser();
//
//                            updateUI(firebaseUser);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(SplashScreen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//
//                    }
//                });
//    }

//    private void updateUI(FirebaseUser firebaseUser) {
//        Toast.makeText(SplashScreen.this, "Facebook user is already logged in",
//                Toast.LENGTH_SHORT).show();
//        if(firebaseUser != null){
//            Intent intent = new Intent(this, MainScreen.class);
//            startActivity(intent);
//        }
//    }
//}


//     TODO: try to understand why it didn't work here in the jsonToDatabase execution.
//              firebaseAuth = FirebaseAuth.getInstance();
//            firebaseUser = firebaseAuth.getCurrentUser();
//            if (firebaseUser != null) {
//                checkFacebookUserIsLoggedIn();
//                Intent intent = new Intent(this, MainScreen.class);
//                startActivity(intent);
//                finish();
//            }

