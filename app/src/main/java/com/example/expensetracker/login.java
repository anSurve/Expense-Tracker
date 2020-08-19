package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    EditText mEmail,mPass;
    Button mLogin;
    ProgressBar mPBar;
    FirebaseAuth fAuth;
    TextView mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.username);
        mPass = findViewById(R.id.password);
        mLogin = findViewById(R.id.login);
        mPBar = findViewById(R.id.loading);
        fAuth = FirebaseAuth.getInstance();
        mSignUp = findViewById(R.id.signup);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEmail.getText().toString().trim();
                String password = mPass.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    mEmail.setError("Username is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPass.setError("Password is required");
                    return;
                }
                mPBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(login.this,"Successfully logged in", Toast.LENGTH_SHORT);
                            startActivity(new Intent(getApplicationContext(),home.class));
                        }else{
                            Toast.makeText(login.this,"Login failed", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });


        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),signup.class));
                finish();
            }
        });
    }
}
