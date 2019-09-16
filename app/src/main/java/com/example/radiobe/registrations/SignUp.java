package com.example.radiobe.registrations;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.radiobe.R;
//import com.example.radiobe.StreamDAO;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.fragments.MainScreen;
import com.example.radiobe.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SignUp extends AppCompatActivity {
    EditText etFirst;
    EditText etLast;
    EditText etDate;
    EditText etEmail;
    EditText etPassword;
    EditText etPassAgain;
    Button btnSignUp;
    DatePicker datePicker;
    boolean isUserInDatabase;
    FirebaseUser firebaseUser;
    User user;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findView();

        btnSignUp.setOnClickListener(v -> {

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            Date birthDate = calendar.getTime();

            String firstName = etFirst.getText().toString();
            String lastName = etLast.getText().toString();
            String userName = etEmail.getText().toString();

//            String birthDay = getStringDate(year, month, day);
            String password = etPassword.getText().toString();
            String password2 = etPassAgain.getText().toString();


            if (firstName.length() < 1)
                etFirst.setError("Enter your first name");
            if (lastName.length() < 1)
                etLast.setError("Enter your last name");
            if (userName.length() < 1)
                etEmail.setError("Enter your user name");

            System.out.println("user over 16?: " + (currentYear - year));
            if ((currentYear - year) < 16) {
                Toast.makeText(this, "You must be over 16 years old to use this app.", Toast.LENGTH_SHORT).show();
            }

            if (password.length() < 6)
                etPassword.setError("Your password must be at least 6 characters");

            if (!(password2.equals(password)))
                etPassAgain.setError("Passwords must be the same");


            if (etFirst.getError() == null && etLast.getError() == null &&
                    etEmail.getError() == null && (currentYear - year) > 16 &&
                    etPassword.getError() == null &&
                    etPassAgain.getError() == null) {

                user = new User(etFirst.getText().toString(),
                        etLast.getText().toString(),
                        etEmail.getText().toString(),
                        birthDate,
                        etPassword.getText().toString()
                );
                createUser(user);
            }
            System.out.println("Created User: " + user);
        });


    }

//    private String getStringDate(int year, int month, int day) {
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month, day);
//        Date date = calendar.getTime();
//
//        return simpleDateFormat.format(date);
//    }

    public void createUser(User newUser) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword()).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
//                StreamDAO.getInstance().saveUserToDatabase(newUser);
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                newUser.setPassword(null);  //not saving the password on the server.
                newUser.setFireBaseID(firebaseUser.getUid());
                ref.child("users").child(newUser.getFireBaseID()).setValue(newUser);

                //todo: create listener
                CurrentUser.getInstance().setContext(getApplicationContext());
                CurrentUser.getInstance().createUser(newUser.getFireBaseID() , ()->{
                    Intent intent = new Intent(this, MainScreen.class);
                    startActivity(intent);
                    finish();
                });

            } else {
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "המייל כבר נמצא במערכת", Toast.LENGTH_SHORT).show();
                    return;
                }
                // If sign in fails, display a message to the user.
                //
                System.out.println("createUserWithEmail:failure" + task.getException());
                Toast.makeText(this, "ההרשמה נכשלה, אנא בדוק את השדות ונסה שנית", Toast.LENGTH_SHORT).show();
            }


        });

    }


    private void findView() {
        etFirst = findViewById(R.id.etFirst);
        etLast = findViewById(R.id.etLast);
        datePicker = findViewById(R.id.datePicker);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPassAgain = findViewById(R.id.etPassAgain);
        btnSignUp = findViewById(R.id.btnSignUp);
    }
}

//    public void checkUserinDatabase(User newUser) {
//        //how to query our database.
//        //get a ref to the users node(like table but no..)
//
//        //you can either use a single listener (just reading once) or you can go on a real time reading from db.
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("users");
//
//
//        //single update
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //every child is basically a user object (not yet)
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
////                    Object userObject = child.getValue();           //basically hashmap
////                    System.out.println(userObject);
//                    User user = child.getValue(User.class);
//                    if (user.getEmail().equals(newUser.getEmail())) {
//                        etEmail.setError("This email address already exists! Try another one");
//                        isUserInDatabase = true;
//                        return;
//                    }
//                    System.out.println(user);
//                }
//
//                isUserInDatabase = false;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Toast.makeText(SignUp.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


//                if (!checkUserinDatabase(user)) {
//
//                if (!isUserInDatabase) {
//
////                    new DatabaseUpdater(DatabaseUpdater.SAVE_USER , user).execute();
//                    StreamDAO.getInstance().saveUserToDatabase(user);
//
//                    try {
//                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword()); //create a new user int he data base.
//                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                        if (firebaseUser != null) {
//                            Intent intent = new Intent(this, MainScreen.class);
//                            startActivity(intent);
//                        }
//
//                    } catch (IllegalArgumentException e) {
//                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(this, "Didn't get in to the IF and created the user.", Toast.LENGTH_SHORT).show();
//                }
//            }

//changed to void from boolean
//    private void checkUserinDatabase(User newUser) {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
//        //single update
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //every child is basically a user object (not yet)
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
////                    Object userObject = child.getValue();           //basically hashmap
////                    System.out.println(userObject);
//                    User user = child.getValue(User.class);
//                    if (user != null && user.getEmail().equals(newUser.getEmail())) {
////                        etEmail.setError("This email address already exists! Try another one");
//                        Toast.makeText(SignUp.this, "This email address already exists! Try another one", Toast.LENGTH_SHORT).show();
//                        changeUserStatus();
//                        return;
//                    }
//                    System.out.println(user);
//                }
//
//                isUserInDatabase = false;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Toast.makeText(SignUp.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
////        return isUserInDatabase;
//    }
//
//    private void changeUserStatus() {
//        isUserInDatabase = true;
//    }