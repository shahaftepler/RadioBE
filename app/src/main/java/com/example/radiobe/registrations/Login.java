package com.example.radiobe.registrations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.database.DownloadFacebookProfileImage;
import com.example.radiobe.database.FacebookProfilePictureDownloadedListener;
import com.example.radiobe.fragments.MainScreen;
import com.example.radiobe.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class Login extends AppCompatActivity {
    //Properties
    private static final String EMAIL = "email";
    private static final int RC_SIGN_IN = 9001;
    private EditText etName;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;
    private LoginButton loginButtonFacebook;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    boolean isUserInDatabase = false;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();


    //CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupView();

        callbackManager = CallbackManager.Factory.create();
        setupView();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            CurrentUser.getInstance().setContext(getApplicationContext());
            CurrentUser.getInstance().createUser(firebaseUser.getUid(), ()->{
                Intent intent = new Intent(this, MainScreen.class);
                startActivity(intent);
            });

        }

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> { //TODO: query the data base and check if the user exits.
            String userName = etName.getText().toString();
            String password = etPassword.getText().toString();

            if (userName.length() < 1)
                etName.setError("Enter your user name");
            if (password.length() < 1)
                etPassword.setError("Enter your password");

            if (etName.getError() == null && etPassword.getError() == null) {
                userName = etName.getText().toString();
                password = etPassword.getText().toString();

                signIn(userName, password);
            }
        });

        loginButtonFacebook.setOnClickListener(v -> {
            FacebookLogIn();
            checkUserIsLoggedIn();
        });


    }


    private void checkUserIsLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
    }

    private void FacebookLogIn() {
        loginButtonFacebook.setReadPermissions("email", "public_profile");
        // Callback registration
        loginButtonFacebook.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signIn(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        System.out.println("signInWithEmail:success");
                        //handle getting UserCredentials from server;
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        CurrentUser.getInstance().setContext(getApplicationContext());
                        CurrentUser.getInstance().createUser(firebaseUser.getUid(), ()-> {
                            Intent intent = new Intent(this, MainScreen.class);
                            startActivity(intent);

                            finish();

                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        System.out.println("signInWithEmail:failure" + task.getException());
                        Toast.makeText(this, "התחברות נכשלה, שם משתמש או סיסמא לא נכונים.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //TODO: Remember that i have changed firebaseAuth to FIrebase.

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                        if(fbuser != null) {
                            System.out.println("*********************"+fbuser.getDisplayName());
                            checkFacebookUserInDatabase(fbuser);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }



    private void checkFacebookUserInDatabase(FirebaseUser fbuser){
        ref.child("users").child(fbuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    CurrentUser.getInstance().setContext(getApplicationContext());
                    CurrentUser.getInstance().createUser(fbuser.getUid(), () -> {
                        Intent intent = new Intent(Login.this, MainScreen.class);
                        startActivity(intent);
                    });
                } else {
                    boolean fromFacebook = false;
                    //write this user to data base he came from facebook.
                        for (UserInfo userInfo : fbuser.getProviderData()) {
                            if (userInfo.getProviderId().equals("facebook.com")) {
                                Log.d("TAG", "User is signed in with Facebook");
                                fromFacebook = true;
                            }
                        }

                        User user = new User(fbuser.getEmail());
                        if (fromFacebook) {
                            String displayName = fbuser.getDisplayName();
                            System.out.println("+++++++++++++++++" +displayName);
                            String lastName = "";
                            String firstName = "";
                            if (displayName.split("\\w+").length > 1) {

                                lastName = displayName.substring(displayName.lastIndexOf(" ") + 1);
                                firstName = displayName.substring(0, displayName.lastIndexOf(' '));
                            } else {
                                firstName = displayName;
                            }

                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setFireBaseID(fbuser.getUid());
                            if(fbuser.getPhotoUrl() != null) {
                                user.setFacebookURL(fbuser.getPhotoUrl().toString());
                                System.out.println(user.getFacebookURL());
                            }

                                ref.child("users").child(fbuser.getUid()).setValue(user).addOnSuccessListener(aVoid -> {
                                    CurrentUser.getInstance().setContext(getApplicationContext());
                                    CurrentUser.getInstance().createUser(fbuser.getUid(), () -> {
                                        Intent intent = new Intent(Login.this, MainScreen.class);
                                        startActivity(intent);
                                    });
                                });

                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setupView() {
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        loginButtonFacebook = findViewById(R.id.login_button);
    }
}

