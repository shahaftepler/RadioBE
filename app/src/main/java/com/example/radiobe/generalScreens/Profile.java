package com.example.radiobe.generalScreens;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile extends AppCompatActivity {
    private final int PICK_COVER_IMAGE = 71;
    private final int PICK_PROFILE_IMAGE = 72;
    private final long ONE_MEGABYTE = 2048 * 2048;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filePath;
    private ImageView editImageProfile;
    private ImageView editCoverImage;
    private ImageView coverImage;
    private ImageView editDetails;
    private ImageView editAboutMeButton;
    private EditText dialogAboutMeEditText;
    private CircleImageView profileImage;
    private TextView profileName;
    private TextView email;
    private TextView profileDescription;
    private TextView profileBirthDay;
    private TextView descriptionTitle;
    //dialog
    private EditText editDialogName;
    private EditText editDialogLastName;
    private EditText editDialogDescription;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private View viewForAlert;
    private View viewForAlertAboutMe;
    private String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private DatePicker datePicker;
    private Button positiveButton;
    private Button negativeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupViews();


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        setupInfo();

        editImageProfile.setOnClickListener((view -> {

            Toast.makeText(this, "The image MUST be Smaller then 4 MegaBytes(MB).", Toast.LENGTH_LONG).show();
            chooseImage(PICK_PROFILE_IMAGE);
        }));
        editCoverImage.setOnClickListener((view -> {
            Toast.makeText(this, "The image MUST be Smaller then 4 MegaBytes(MB).", Toast.LENGTH_LONG).show();
            chooseImage(PICK_COVER_IMAGE);

        }));

        editDetails.setOnClickListener((view -> {
            setEditDetails();
        }));

        editAboutMeButton.setOnClickListener((view -> {
            setAboutMe();

        }));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("User profile updated.");
                        }
                    }
                });
    }

    private void setupInfo() {
        profileImage.setImageBitmap(CurrentUser.getInstance().getProfileImage());
        coverImage.setImageBitmap(CurrentUser.getInstance().getCoverImage());
        profileName.setText(CurrentUser.getInstance().getFirstName() +" "+ CurrentUser.getInstance().getLastName());
        email.setText(CurrentUser.getInstance().getEmail());

        if (CurrentUser.getInstance().getDescription() != null){
            profileDescription.setText(CurrentUser.getInstance().getDescription());
        } else {
            profileDescription.setText("Hello my name is "+CurrentUser.getInstance().getFirstName() +" nice to meet you");

        }
        //todo: are we going to add description to the user registration? if so, get it from current user too.
        profileBirthDay.setText(CurrentUser.getInstance().getBirthDateString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_COVER_IMAGE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    //get data as uri
                    filePath = data.getData();
                    //set the profileImage to the uri data. transferred it to onSuccess of the upload, cause otherwise it doesn't worth it.
//                    coverImage.setImageURI(filePath);
                    //upload the image to firebase storage im cover/ folder
                    uploadImage("cover" , coverImage);
                } else {
                    Toast.makeText(this, "Something wasn't done right.", Toast.LENGTH_SHORT).show();

                }                break;
            case PICK_PROFILE_IMAGE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    //get data as uri
                    filePath = data.getData();
                    //set the profileImage to the uri data
//                    profileImage.setImageURI(filePath);
                    //upload the image to firebase storage im profile/ folder
                    uploadImage("profile" , profileImage);
                } else {
                    Toast.makeText(this, "Something wasn't done right.", Toast.LENGTH_SHORT).show();

                }
                break;
        }

    }


    /**
     * UploadImage gave us the ability to choose or create a folder on the FireBase Storage
     * and upload the image that the user pick.
     *
     * @param folder -> represent the folder that we want create or upload to it.
     */
    private void uploadImage(String folder , ImageView imagePlace) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait Uploading...");
            progressDialog.show();


            StorageReference ref = storageReference.child(folder).child(uid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //only after the photo was uploaded successfully to the server im setting in the UI
                            imagePlace.setImageURI(filePath);

                            //set the current user photo too.
                            updateCurrentUserPhotoChange(folder);

                            progressDialog.dismiss();
                            Toast.makeText(Profile.this, "Your photo was uploaded successfully!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Profile.this, "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading " + (int) progress + "%");
                    });
        }
    }


    /**
     * This method purpose is to update the current user object with the new photo being picked.
     * Its being called only if the upload to the server was a success.
     * @param folder -> represent the kind of photo to be uploaded.
     */

    private void updateCurrentUserPhotoChange(String folder){
        switch (folder){
            case "profile":
                try {
                    CurrentUser.getInstance().setProfileImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath));
                    System.out.println(CurrentUser.getInstance().getProfileImage());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("File wasn't uploaded to Current user profile");
                }
            case "cover":
                try {
                    CurrentUser.getInstance().setCoverImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath));
                    System.out.println(CurrentUser.getInstance().getCoverImage());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("File wasn't uploaded to Current user profile");
                }
        }

    }

    /**
     * ChooseImage gave us the ability to choose one image
     * from the gallery phone , downloads  or google drive.
     *
     * @param pickImageRequest -> it's a number that change.
     */
    private void chooseImage(int pickImageRequest) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageRequest);
    }

    /**
     * Let the user set the values of his personal details,
     * like name, description or birth day.
     */
    @SuppressLint("InflateParams")
    private void setEditDetails() {
        String newFirstname;
        String newLastname;
        String newEmail;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your new details");
        viewForAlert = LayoutInflater.from(this).inflate(R.layout.dialog_view, null);
        editDialogName = viewForAlert.findViewById(R.id.edit_dialog_firstName);
        editDialogLastName = viewForAlert.findViewById(R.id.edit_dialog_lastName);
//        editDialogDescription = viewForAlert.findViewById(R.id.edit_dialog_description);
//        editDialogDescription = viewForAlert.findViewById(R.id.edit_dialog_description);
//        editDialogDescription = viewForAlert.findViewById(R.id.edit_dialog_description);
        editEmail = viewForAlert.findViewById(R.id.edit_email);
        editPassword = viewForAlert.findViewById(R.id.edit_password);
        editConfirmPassword = viewForAlert.findViewById(R.id.edit_confirmPassword);

        EditText[] editTexts = new EditText[]{editDialogName, editDialogLastName , editEmail};

        editDialogName.setText(CurrentUser.getInstance().getFirstName());
        editDialogLastName.setText(CurrentUser.getInstance().getLastName());


//        if (CurrentUser.getInstance().getDescription() != null){
//            editDialogDescription.setText(CurrentUser.getInstance().getDescription());
//        } else {
//            editDialogDescription.setText(profileDescription.getText().toString());
//        }

//        editDialogDescription.setText(profileDescription.getText().toString());

        editDialogDescription.setText(profileDescription.getText().toString());
        editEmail.setText(CurrentUser.getInstance().getEmail());

        positiveButton = viewForAlert.findViewById(R.id.positiveButton);
        negativeButton = viewForAlert.findViewById(R.id.negativeButton);
        datePicker = viewForAlert.findViewById(R.id.datePicker);
        datePicker.setMaxDate(new Date().getTime());
        builder.setView(viewForAlert);

        viewForAlert.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                if(imm.isAcceptingText()) { // verify if the soft keyboard is open
                    imm.hideSoftInputFromWindow(viewForAlert.getWindowToken(), 0);
                }
                return false;
            }
        });

        //todo: init picker with the date of birth.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(CurrentUser.getInstance().getBirthDate()));
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);


        builder.setIcon(R.drawable.pan_edit);
        AlertDialog alert = builder.create();


        alert.show();


        positiveButton.setOnClickListener((v)->{
            boolean nameChanged = false;
            boolean lastNameChanged = false;
            boolean emailChanged = false;
            boolean passwordChanged = false;
            String name = editDialogName.getText().toString();
            String lastName = editDialogLastName.getText().toString();
//            String description = editDialogDescription.getText().toString();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            String confirmPassword = editConfirmPassword.getText().toString();
            Calendar dateOfBirth = Calendar.getInstance();
            int dayOfYear = dateOfBirth.DAY_OF_YEAR;
            dateOfBirth.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth() , 0 ,0 , 0);
            dateOfBirth.set(Calendar.MILLISECOND, 0);


            //if no changes, dismiss dialog
            if(checkForChanges(name, lastName , dateOfBirth , email , password , confirmPassword)) {
                System.out.println("IN NEW IF ???");
                Toast.makeText(this, "No changes were made!", Toast.LENGTH_SHORT).show();
                alert.dismiss();

            }

//            for (int i = 0; i < editTexts.length ; i++) {
//                String change = editTexts[i].getText().toString();
//                if(change.length() < 1){
//                    editTexts[i].setError("You must fill this box with more than 0 characters");
//                }
//            }



            if (name.length() < 1) {
                editDialogName.setError("Your name must be longer than 0 characters");
            }

            if(lastName.length() < 1){
                editDialogLastName.setError("Your last name must be longer than 0 characters");
            }

            if(!isEmailValid(email)){
                editEmail.setError("Your email must be according to a@a.com format");
            }

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (!email.equals(CurrentUser.getInstance().getEmail())) {
                if (firebaseUser != null) {
                    firebaseUser.updateEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "Your e-mail was successfully changed to " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                                        CurrentUser.getInstance().setEmail(email);
                                    } else {
                                        try {
                                            throw task.getException();
                                        } catch(FirebaseAuthWeakPasswordException e) {
                                            editPassword.setError("הסיסמה חלשה מדי, אנא הכנס סיסמה עם 6 תווים ומעלה");
                                            editPassword.requestFocus();
                                        } catch(FirebaseAuthInvalidCredentialsException e) {
                                            editEmail.setError("הפרטים אינם חוקיים");
                                            editEmail.requestFocus();
                                        } catch(FirebaseAuthUserCollisionException e) {
                                            editEmail.setError("המייל כבר נמצא במערכת");
                                            editEmail.requestFocus();
                                            Toast.makeText(Profile.this, "המייל כבר נמצא במערכת", Toast.LENGTH_SHORT).show();
                                        } catch (FirebaseAuthRecentLoginRequiredException e){
                                            editEmail.setError("Please re-confirm your email");
                                            AlertDialog.Builder builderApprove = new AlertDialog.Builder(Profile.this);
                                            builderApprove.setTitle("Re-confirmation Details");
                                            builderApprove.setMessage("Please re-confirm your soon to be changed details");
                                            View newViewForAlert = LayoutInflater.from(Profile.this).inflate(R.layout.change_email_dialog, null);
                                            EditText authEmail = newViewForAlert.findViewById(R.id.editEmailText);
                                            EditText authPassword = newViewForAlert.findViewById(R.id.editPasswordText);
                                            builderApprove.setPositiveButton("Done" , (d , v) ->{

                                                AuthCredential credential = EmailAuthProvider.getCredential(authEmail.getText().toString(), authPassword.getText().toString());
                                                firebaseUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    System.out.println("EMAIL SUPPOSE TO CHANGE" + firebaseUser.getEmail());
                                                                    Toast.makeText(Profile.this, "Your e-mail was successfully changed to " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                                                                    CurrentUser.getInstance().setEmail(email);
                                                                    editEmail.setError(null);
                                                                } else{
                                                                        try {
                                                                            throw task.getException();
                                                                        } catch(FirebaseAuthInvalidCredentialsException e) {
                                                                        editEmail.setError("הפרטים אינם חוקיים");
                                                                        editEmail.requestFocus();
                                                                    } catch(FirebaseAuthUserCollisionException e) {
                                                                            Toast.makeText(Profile.this, "המייל כבר נמצא במערכת", Toast.LENGTH_SHORT).show();
                                                                        editEmail.setError("המייל כבר נמצא במערכת");
                                                                        editEmail.requestFocus();
                                                                    } catch (Exception ex) {
                                                                            System.out.println(ex.getMessage());
                                                                        }
                                                                    System.out.println("JUST TEST FOR NOW");
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            });
                                            builderApprove.setView(newViewForAlert);
                                            builderApprove.show();
                                        } catch(Exception e) {
                                            System.out.println(e.getMessage());
                                        }






                                        System.out.println(task.getException().toString());
//                                        Toast.makeText(Profile.this, task.getException().toString() , Toast.LENGTH_SHORT).show();

//                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
//                                            Toast.makeText(Profile.this, "המייל כבר נמצא במערכת", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            System.out.println(task.getException().toString());
//                                        }
                                    }

                                    if (editDialogName.getError() != null || editDialogLastName.getError() != null  || editEmail.getError() != null){
                                        Toast.makeText(Profile.this, "Check Errors", Toast.LENGTH_SHORT).show();

                                    } else{
                                        User user = new User(name , lastName , CurrentUser.getInstance().getEmail(), CurrentUser.getInstance().getBirthDate() , CurrentUser.getInstance().getBirthDateString() ,  null , CurrentUser.getInstance().getFireBaseID());
                                        ref.child("users").child(CurrentUser.getInstance().getFireBaseID()).updateChildren(user.toMap());
                                        CurrentUser.getInstance().loadDetailsFromMap();
                                        profileName.setText(CurrentUser.getInstance().getFirstName() +" "+ CurrentUser.getInstance().getLastName());
                                        Profile.this.email.setText(CurrentUser.getInstance().getEmail());
                                        profileBirthDay.setText(CurrentUser.getInstance().getBirthDateString());
                                        alert.dismiss();
                                    }

                                }
                            });
                }


            } else {
                if (editDialogName.getError() != null || editDialogLastName.getError() != null  || editEmail.getError() != null){
                    Toast.makeText(Profile.this, "Check Errors", Toast.LENGTH_SHORT).show();

                } else{
                    User user = new User(name , lastName , CurrentUser.getInstance().getEmail(), CurrentUser.getInstance().getBirthDate() , CurrentUser.getInstance().getBirthDateString() ,  null , CurrentUser.getInstance().getFireBaseID());
                    ref.child("users").child(CurrentUser.getInstance().getFireBaseID()).updateChildren(user.toMap());
                    CurrentUser.getInstance().loadDetailsFromMap();
                    profileName.setText(CurrentUser.getInstance().getFirstName() +" "+ CurrentUser.getInstance().getLastName());
                    this.email.setText(CurrentUser.getInstance().getEmail());
                    profileBirthDay.setText(CurrentUser.getInstance().getBirthDateString());
                    alert.dismiss();
                }
            }

        });

        negativeButton.setOnClickListener((v)->{
            alert.dismiss();
            Toast.makeText(this, "No change were made!", Toast.LENGTH_SHORT).show();
        });

    }

    public void setAboutMe(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your new About Me");
        viewForAlertAboutMe = LayoutInflater.from(this).inflate(R.layout.dialog_about_me, null);
        dialogAboutMeEditText = viewForAlertAboutMe.findViewById(R.id.edit_about_me);
        positiveButton = viewForAlertAboutMe.findViewById(R.id.positiveButton);
        negativeButton = viewForAlertAboutMe.findViewById(R.id.negativeButton);

        viewForAlertAboutMe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                if(imm.isAcceptingText()) { // verify if the soft keyboard is open
                    imm.hideSoftInputFromWindow(viewForAlertAboutMe.getWindowToken(), 0);
                }
                return false;
            }
        });
        builder.setView(viewForAlertAboutMe);
        builder.setIcon(R.drawable.pan_edit);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();


        dialogAboutMeEditText.addTextChangedListener(new TextWatcher() {
                                                         @Override
                                                         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                         }

                                                         @Override
                                                         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                             if (charSequence.length() >= 300) {
                                                                 dialogAboutMeEditText.setError("Max limit 300 characters");
                                                             }
                                                         }

                                                         @Override
                                                         public void afterTextChanged(Editable editable) {

                                                         }
                                                     });

        positiveButton.setOnClickListener((v)->{
            String aboutMe = dialogAboutMeEditText.getText().toString();
//
//            if(aboutMe.length() > 300) {
//                int aboutMeLength = aboutMe.length();
//                int needToDelete = aboutMeLength - 300;
//                dialogAboutMeEditText.setError("Your comment is big then 300 characters please delete at least: "+needToDelete+"characters");
//
//            }else if (aboutMe.length() == 0){
//                dialogAboutMeEditText.setError("Enter Some words About You");
//            }else {
//                profileDescription.setText(dialogAboutMeEditText.getText());
//                alertDialog.dismiss();
//            }

            if (dialogAboutMeEditText.getError() != null){
                Toast.makeText(this, dialogAboutMeEditText.getError().toString(), Toast.LENGTH_SHORT).show();
            } else {
                CurrentUser.getInstance().setDescription(aboutMe);
                profileDescription.setText(aboutMe);
                alertDialog.dismiss();
            }
        });

        negativeButton.setOnClickListener((v)->{
            alertDialog.dismiss();
            Toast.makeText(this, "No change!", Toast.LENGTH_SHORT).show();
        });

    }

    /**
     * setup all the Views on Profile be equals them to there ID's
     */
    private void setupViews() {
        editImageProfile = findViewById(R.id.idProfileEditImage);
        profileImage = findViewById(R.id.idImageViewProfile);
        editCoverImage = findViewById(R.id.idCoverEditImage);
        coverImage = findViewById(R.id.idCoverTopProfile);
        profileName = findViewById(R.id.idFullNameProfile);
        profileDescription = findViewById(R.id.idDescriptionProfile);
        profileBirthDay = findViewById(R.id.idBirthDayProfile);
        editDetails = findViewById(R.id.idEditDetails);
        email = findViewById(R.id.idEmail);
//        descriptionTitle = findViewById(R.id.idAboutMe);
        editAboutMeButton = findViewById(R.id.idEditDetailsDescription);
        dialogAboutMeEditText = findViewById(R.id.edit_about_me);


    }

    //check if there was any change to the details.
    private boolean checkForChanges(String firstName, String lastName , Calendar dateOfBirth , String email , String password , String confirmPassword){
        return (firstName.equals(CurrentUser.getInstance().getFirstName()) &&
                lastName.equals(CurrentUser.getInstance().getLastName()) &&
                dateOfBirth.getTimeInMillis() == CurrentUser.getInstance().getBirthDate() &&
                password.isEmpty() &&
                (confirmPassword.equals(password) || confirmPassword.isEmpty()) &&
                email.equals(CurrentUser.getInstance().getEmail()));


        //dateOfBirth.getTimeInMillis() == CurrentUser.getInstance().getBirthDate().getTime()
    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

}

