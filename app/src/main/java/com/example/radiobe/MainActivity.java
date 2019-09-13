package com.example.radiobe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.fragments.MainScreen;
import com.example.radiobe.generalScreens.ActivityRadio;
import com.example.radiobe.registrations.Login;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button btnLive;
    private Button btnLogin;
    FirebaseUser firebaseUser;

    //new change test - deleted func

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {   //TODO : try to understand why it didn't work from the splash screen itself.
            CurrentUser.getInstance().setContext(getApplicationContext());
            CurrentUser.getInstance().createUser(firebaseUser.getUid(), ()->{
                Intent intent = new Intent(this, MainScreen.class);
                startActivity(intent);
            }); // todo: create a listener for that.

        }

        setupViews();



        //Gif of the MainActivity
        ImageView gifRadio = findViewById(R.id.ivRadioGif);
        Glide.with(this).asGif().load(R.drawable.loading_radio).into(gifRadio);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);

        });

        btnLive.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityRadio.class);
            startActivity(intent);
        });



    }

    private void setupViews() {
        btnLive = findViewById(R.id.btnLive);
        btnLogin = findViewById(R.id.btnLogin);


    }
}
