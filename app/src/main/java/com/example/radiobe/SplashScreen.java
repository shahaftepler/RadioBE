package com.example.radiobe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.database.FireBaseParseJson;
import com.example.radiobe.fragments.MainScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreen extends AppCompatActivity {
    FirebaseUser firebaseUser;
    ProgressBar progressBar;

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

        new FireBaseParseJson(() -> {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {   //TODO : try to understand why it didn't work from the splash screen itself.
                System.out.println("DISPLAY"+firebaseUser.getDisplayName());
                System.out.println("URLLLLLLLLLLLLLLLLLLLLL"+firebaseUser.getPhotoUrl());
                CurrentUser.getInstance().setContext(getApplicationContext());
                CurrentUser.getInstance().createUser(firebaseUser.getUid(), ()->{
                    Intent intent = new Intent(this, MainScreen.class);
                    startActivity(intent);
                }); // todo: create a listener for that.

            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, () -> progressBar.setProgress(progressBar.getProgress() + 1), getApplicationContext()).execute();


    }


}
