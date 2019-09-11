package com.example.radiobe.generalScreens;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile extends AppCompatActivity {
    private final int PICK_COVER_IMAGE = 71;
    private final int PICK_PROFILE_IMAGE = 72;
    private final long ONE_MEGABYTE = 2048 * 2048;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filePath;
    private ImageView editImageProfile;
    private ImageView editCoverImage;
    private ImageView coverImage;
    private ImageView editDetails;
    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileDescription;
    private TextView profileBirthDay;
    //dialog
    private EditText editDialogName;
    private EditText editDialogDescription;
    private View viewForAlert;
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
                    CurrentUser.getInstance().setProfileImage(MediaStore.Images.Media.getBitmap(Profile.this.getContentResolver(), filePath));
                    System.out.println(CurrentUser.getInstance().getProfileImage());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("File wasn't uploaded to Current user profile");
                }
            case "cover":
                try {
                    CurrentUser.getInstance().setCoverImage(MediaStore.Images.Media.getBitmap(Profile.this.getContentResolver(), filePath));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your new details");
        viewForAlert = LayoutInflater.from(this).inflate(R.layout.dialog_view, null);
        editDialogName = viewForAlert.findViewById(R.id.edit_dialog_name);
        editDialogDescription = viewForAlert.findViewById(R.id.edit_dialog_description);

        editDialogName.setText(profileName.getText().toString());
        editDialogDescription.setText(profileDescription.getText().toString());


        positiveButton = viewForAlert.findViewById(R.id.positiveButton);
        negativeButton = viewForAlert.findViewById(R.id.negativeButton);
        datePicker = viewForAlert.findViewById(R.id.datePicker);
        datePicker.setMaxDate(new Date().getTime());
        builder.setView(viewForAlert);

        //todo: init picker with the date of birth.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(CurrentUser.getInstance().getBirthDate());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);


        builder.setIcon(R.drawable.pan_edit);
        AlertDialog alert = builder.create();


        alert.show();


        positiveButton.setOnClickListener((v)->{
            String name = editDialogName.getText().toString();
            String description = editDialogDescription.getText().toString();
            Calendar dateOfBirth = Calendar.getInstance();
            int dayOfYear = dateOfBirth.DAY_OF_YEAR;
            dateOfBirth.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

            if(dateOfBirth.getTimeInMillis() < new Date().getTime()){
                //todo: create some kind of error.
            }

            if (name.length() < 1) {
                editDialogName.setError("Enter A Name");
            }

            if (description.length() < 1) {
                editDialogDescription.setError("Choose Description");
            }

            if (editDialogName.getError() != null || editDialogDescription.getError() != null ){
                Toast.makeText(this, "Check Errors", Toast.LENGTH_SHORT).show();
            } else{
                alert.dismiss();
            }
        });

        negativeButton.setOnClickListener((v)->{
            alert.dismiss();
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
        profileName = findViewById(R.id.idNameProfile);
        profileDescription = findViewById(R.id.idDescriptionProfile);
        profileBirthDay = findViewById(R.id.idBirthDayProfile);
        editDetails = findViewById(R.id.idEditDetails);

    }


}

