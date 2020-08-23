package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    EditText mName, mEmail, mPass;
    RadioButton mGender;
    Button mSignUpButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fDb;
    ProgressBar mPBar;
    TextView mGotoLogin;
    RadioGroup radioGroup;
    DatePicker mBirthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.username);
        mPass = findViewById(R.id.password);
        mSignUpButton = findViewById(R.id.signup);
        mGotoLogin = findViewById(R.id.loginNav);
        radioGroup = (RadioGroup) findViewById(R.id.gender);
        mBirthDate = findViewById(R.id.datePicker);

        fAuth = FirebaseAuth.getInstance();
        fDb = FirebaseFirestore.getInstance();
        mPBar = findViewById(R.id.loading);

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), home.class));
            finish();
        }


        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mName.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                final String password = mPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPass.setError("Password is required");
                    return;
                }

                //Get Gender
                int selectedId = radioGroup.getCheckedRadioButtonId();
                mGender = (RadioButton) findViewById(selectedId);
                String gender = mGender.getText().toString().trim();
                // Create a new user with a first and last name

                //Get Birthdate
                int day = mBirthDate.getDayOfMonth();
                int month = mBirthDate.getMonth();
                int year =  mBirthDate.getYear();

                Calendar birthday = Calendar.getInstance();
                birthday.set(year, month, day, 0, 0 ,0);

                final Map<String, Object> user = new HashMap<>();
                user.put("Name", name);
                user.put("Email", email);
                user.put("Gender", gender);
                user.put("Birth Date",birthday.getTime());

                mPBar.setVisibility(View.VISIBLE);

                fDb.collection("Users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(signup.this, "User added successfully", Toast.LENGTH_SHORT);

                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(signup.this, "Successfully Signed Up", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(signup.this, "Signed Up failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(signup.this, "Could not add user to the db", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        });
    }
}
