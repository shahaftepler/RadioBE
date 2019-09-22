package com.example.radiobe.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radiobe.MainActivity;
import com.example.radiobe.R;
import com.example.radiobe.adapters.MainScreenAdapter;
import com.example.radiobe.adapters.NotificationsAdapter;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.database.RefreshNotificationsListener;
import com.example.radiobe.database.RefreshProfilePicture;
import com.example.radiobe.database.RefreshUserName;
import com.example.radiobe.generalScreens.Profile;
import com.example.radiobe.generalScreens.Settings;
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

import de.hdodenhof.circleimageview.CircleImageView;

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
    private PlayerView playerView;
    public SimpleExoPlayer simpleExoPlayer;

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
            }else {
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
        userNameTV.setText("Hello, "+CurrentUser.getInstance().getFirstName());
        logOutBtn.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.logOutDialog))
                        .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LoginManager.getInstance().logOut();
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(MainScreen.this, getString(R.string.logOutSuccess), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainScreen.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(MainScreen.this, "No change!", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                Toast.makeText(this, "There is no user currently logged in!", Toast.LENGTH_SHORT).show();
            }
        });

//        getSupportActionBar().setIcon(d);
//        LocalBroadcastManager.getInstance(this).registerReceiver(new ExoPlayerView().broadcastReceiver, new IntentFilter("play_song"));

//        navigation.setSelectedItemId(R.id.navigation_home);


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
//        logOutBtn = findViewById(R.id.logOutBtn);
        navigation = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.container);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageProfileTBar = findViewById(R.id.idImageToolBar);
        userNameTV = findViewById(R.id.idUserNameTV);
        logOutBtn = findViewById(R.id.idLogOutBtn);


        playerView = findViewById(R.id.playerView);
        // Setup Exoplayer instance
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
//        newFilePath = getArguments().getString("filePath");

//        filePath = "https://be.repoai.com:5443/LiveApp/streams/vod/אנשים_תכנית_שניה.mp4";
        //Load DataSorce Uri
        ExtractorMediaSource uriMediaSource =
                new ExtractorMediaSource.Factory(
                        new DefaultHttpDataSourceFactory("Radio be")).setTag(fileName).
                        createMediaSource(Uri.parse(filePath));


        System.out.println(uriMediaSource.getTag());
        //Prepare the exoPlayerInstance with the source and play when he Ready

        simpleExoPlayer.prepare(uriMediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
//                fm.beginTransaction().replace(R.id.container, new AllPrograms()).commit();
                viewPager.setCurrentItem(2);
                return true;

            case R.id.navigation_favorites:
//                fm.beginTransaction().replace(R.id.container, new Favorites()).commit();
                viewPager.setCurrentItem(1);

                return true;

            case R.id.navigation_notifications:
//                fm.beginTransaction().replace(R.id.container, new Notifications()).commit();
                viewPager.setCurrentItem(0);
                return true;
        }

//        throw new IllegalArgumentException("No such Button!");
        return false;
    }

    @Override
    public void refresh() {
        userNameTV.setText("Hello, "+CurrentUser.getInstance().getFirstName());
    }

    @Override
    public void refreshPicture() {
        Bitmap img = CurrentUser.getInstance().getProfileImage();
        imageProfileTBar.setImageBitmap(img);
    }

//    //    menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.profile_menu) {
//            Intent intent = new Intent(this, Profile.class);
//            startActivity(intent);
//            return true;
//        } else if (id == R.id.logout_menu) {
//            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage(getString(R.string.logOutDialog))
//                        .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                LoginManager.getInstance().logOut();
//                                FirebaseAuth.getInstance().signOut();
//                                Toast.makeText(MainScreen.this, getString(R.string.logOutSuccess), Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(MainScreen.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        })
//                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                Toast.makeText(MainScreen.this, "No change!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            } else {
//                Toast.makeText(this, "There is no user currently logged in!", Toast.LENGTH_SHORT).show();
//            }
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}

