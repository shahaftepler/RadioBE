package com.example.radiobe.generalScreens;

import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.example.radiobe.R;

public class ActivityRadio extends AppCompatActivity {

    private PlayerView playerView;
    public SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        playerView = findViewById(R.id.playerView);
        // Setup Exoplayer instance
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setControllerHideOnTouch(false);
        playerView.setControllerShowTimeoutMs(0);

//
//        ExoPlayerView fragment = new ExoPlayerView();
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//        transaction.replace(R.id.containerRadio, fragment).commit();
    }


}
