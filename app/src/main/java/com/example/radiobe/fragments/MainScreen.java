package com.example.radiobe.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.radiobe.MainActivity;
import com.example.radiobe.R;
import com.example.radiobe.adapters.MainScreenAdapter;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.database.RefreshProfilePicture;
import com.example.radiobe.database.RefreshUserName;
import com.example.radiobe.generalScreens.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;


public class MainScreen extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener , RefreshUserName , RefreshProfilePicture {

    FragmentManager fm;
    ViewPager viewPager;
    MainScreenAdapter mMainScreenAdapter;
    BottomNavigationView navigation;
    Toolbar toolbar;
    de.hdodenhof.circleimageview.CircleImageView imageProfileTBar;
    TextView userNameTV;
    ImageButton logOutBtn;
    FirebaseUser firebaseUser;
    String fileName;
    String filePath;
    SimpleExoPlayer simpleExoPlayer;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra("stream_url");
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(filePath))
                    .build();

            ShareDialog.show(MainScreen.this, content);

        }
    };

    BroadcastReceiver mediaBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            fileName = intent.getStringExtra("stream_name");
            filePath = intent.getStringExtra("stream_url");
            boolean play = intent.getBooleanExtra("play", false);
            System.out.println("Got Broadcast" + play);

            if (play) {
                loadDataToplayer(fileName, filePath);
                System.out.println("PLAY");
            } else {
                simpleExoPlayer.stop();
                System.out.println("STOP");
            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("share_facebook"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mediaBroadcastReceiver, new IntentFilter("play_song"));
        CurrentUser.getInstance().registerProfilePictureObserver(this);

        setContentView(R.layout.activity_mainscreen);
        generalSetup();

        Bitmap img = CurrentUser.getInstance().getProfileImage();
        imageProfileTBar.setImageBitmap(img);
        logOutBtn.setImageResource(R.drawable.logout_icon);

        imageProfileTBar.setOnClickListener(view -> {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        });
        userNameTV.setText(String.format("Hello, %s", CurrentUser.getInstance().getFirstName()));
        logOutBtn.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.logOutDialog))
                        .setPositiveButton(getString(R.string.Ok), (dialog, id) -> {
                            LoginManager.getInstance().logOut();
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(MainScreen.this, getString(R.string.logOutSuccess), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, id) -> Toast.makeText(MainScreen.this, "No change!", Toast.LENGTH_SHORT).show());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                Toast.makeText(this, "There is no user currently logged in!", Toast.LENGTH_SHORT).show();
            }
        });

        if (firebaseUser != null) {
            Toast.makeText(this, firebaseUser.getEmail() + " login successful", Toast.LENGTH_SHORT).show();
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 2:
                        navigation.setSelectedItemId(R.id.navigation_home);
                        break;

                    case 1:
                        navigation.setSelectedItemId(R.id.navigation_favorites);
                        break;

                    case 0:
                        navigation.setSelectedItemId(R.id.navigation_notifications);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void generalSetup() {
        CurrentUser.getInstance().registerUsernameObserver(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        navigation = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.container);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageProfileTBar = findViewById(R.id.idImageToolBar);
        userNameTV = findViewById(R.id.idUserNameTV);
        logOutBtn = findViewById(R.id.idLogOutBtn);


        PlayerView playerView = findViewById(R.id.playerView);
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setControllerHideOnTouch(false);
        playerView.setControllerShowTimeoutMs(0);

        navigation.setOnNavigationItemSelectedListener(this);
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, new AllPrograms()).commit();
        mMainScreenAdapter = new MainScreenAdapter(fm);
        viewPager.setAdapter(mMainScreenAdapter);
        viewPager.setCurrentItem(3);
    }

    private void loadDataToplayer(String fileName, String filePath) {
        ExtractorMediaSource uriMediaSource =
                new ExtractorMediaSource.Factory(
                        new DefaultHttpDataSourceFactory("Radio be")).setTag(fileName).
                        createMediaSource(Uri.parse(filePath));

        System.out.println(uriMediaSource.getTag());

        simpleExoPlayer.prepare(uriMediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                viewPager.setCurrentItem(2);
                return true;

            case R.id.navigation_favorites:
                viewPager.setCurrentItem(1);

                return true;

            case R.id.navigation_notifications:
                viewPager.setCurrentItem(0);
                return true;
        }

        return false;
    }

    @Override
    public void refresh() {
        userNameTV.setText(String.format("Hello, %s", CurrentUser.getInstance().getFirstName()));
    }

    @Override
    public void refreshPicture() {
        Bitmap img = CurrentUser.getInstance().getProfileImage();
        imageProfileTBar.setImageBitmap(img);
    }
}