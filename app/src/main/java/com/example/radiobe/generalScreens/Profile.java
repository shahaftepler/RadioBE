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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radiobe.R;
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

        getImageStorageFrom("cover", coverImage);
        getImageStorageFrom("profile", profileImage);


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_COVER_IMAGE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    //get data as uri
                    filePath = data.getData();
                    //set the profileImage to the uri data
                    coverImage.setImageURI(filePath);
                    //upload the image to firebase storage im cover/ folder
                    uploadImage("cover");
                }
                break;
            case PICK_PROFILE_IMAGE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    //get data as uri
                    filePath = data.getData();
                    //set the profileImage to the uri data
                    profileImage.setImageURI(filePath);
                    //upload the image to firebase storage im profile/ folder
                    uploadImage("profile");
                }
                break;
        }

    }

    /**
     * Get the image that the user choose from the Storage and make sure it's not over 4 MB.
     * If success we turn the image to Bitmap and place it on the ImageView.
     *
     * @param folder     -> represent the folder on the FireBase Storage.
     * @param imagePlace -> represent the ImageView that going to display the picture.
     */
    private void getImageStorageFrom(String folder, ImageView imagePlace) {
        storageReference.child(folder).child(uid).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagePlace.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * UploadImage gave us the ability to choose or create a folder on the FireBase Storage
     * and upload the image that the user pick.
     *
     * @param folder -> represent the folder that we want create or upload to it.
     */
    private void uploadImage(String folder) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait Uploading...");
            progressDialog.show();


            StorageReference ref = storageReference.child(folder).child(uid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Profile.this, "Uploaded", Toast.LENGTH_LONG).show();
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
        positiveButton = viewForAlert.findViewById(R.id.positiveButton);
        negativeButton = viewForAlert.findViewById(R.id.negativeButton);
        datePicker = viewForAlert.findViewById(R.id.datePicker);
        datePicker.setMaxDate(new Date().getTime());
        builder.setView(viewForAlert);
        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        profileBirthDay.setText(String.format("%d/%d/%d", dayOfMonth, (month + 1), year));
                    }
                });


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

        negativeButton.setOnClickListener((view -> {
            alert.dismiss();
        }));
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

