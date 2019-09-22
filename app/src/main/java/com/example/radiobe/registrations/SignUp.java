package com.example.radiobe.registrations;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.fragments.MainScreen;
import com.example.radiobe.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity {
    EditText etFirst;
    EditText etLast;
    EditText etEmail;
    EditText etPassword;
    EditText etPassAgain;
    Button btnSignUp;
    DatePicker datePicker;
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
            calendar.set(year, month, day, 0 , 0 , 0);
            calendar.set(Calendar.MILLISECOND , 0);

            long birthDate = calendar.getTimeInMillis();

            String firstName = etFirst.getText().toString();
            String lastName = etLast.getText().toString();
            String userName = etEmail.getText().toString();

            String password = etPassword.getText().toString();
            String password2 = etPassAgain.getText().toString();


            if (firstName.length() < 1)
                etFirst.setError("Enter your first name");
            if (lastName.length() < 1)
                etLast.setError("Enter your last name");
            if(!isEmailValid(userName)){
                etEmail.setError("Your email must be according to a@a.com format!");
            }

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

    public void createUser(User newUser) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword()).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
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
                System.out.println("createUserWithEmail:failure" + task.getException());
                Toast.makeText(this, "ההרשמה נכשלה, אנא בדוק את השדות ונסה שנית", Toast.LENGTH_SHORT).show();
            }


        });

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


    private void findView() {
        etFirst = findViewById(R.id.etFirst);
        etLast = findViewById(R.id.etLast);
        datePicker = findViewById(R.id.datePicker);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPassAgain = findViewById(R.id.etPassAgain);
        btnSignUp = findViewById(R.id.btnSignUp);

        View view = findViewById(R.id.container);

        if(view != null) {
            view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if(getCurrentFocus() != null) {
                        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    }
                    return false;
                }
            });
        }
    }
}
