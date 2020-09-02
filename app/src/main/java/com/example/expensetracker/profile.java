package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class profile extends AppCompatActivity {

    private ImageView userDP;
    private ImageButton btn_changeDP;
    private TextView username, useremail;
    private StorageReference storageRef;
    FirebaseFirestore fDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
        userDP = findViewById(R.id.userDp);
        btn_changeDP = findViewById(R.id.changeDP);
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.useremail);
        fDb = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference().child("images/userDP/" + FirebaseAuth.getInstance().getUid()+ ".jpg");
        fun_setDP();
        setUserDetails();
        btn_changeDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),setDp.class));
            }
        });
    }

    private void fun_setDP(){
        try {
            final File localFile = File.createTempFile("images","jpg");
            // Toast.makeText(getApplicationContext(),"Local file created",Toast.LENGTH_LONG).show();
            storageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                Glide.with(getApplicationContext()).load(localFile).into(userDP);
                                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), localFile);
                                //mProfilePic.setImageBitmap(bitmap);
                            }catch (Exception e) {
                                Toast.makeText(getApplicationContext(),"Exception occurred",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(),"Failed to retrieve",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (IOException e){
            Toast.makeText(this,"Exception occurred",Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserDetails(){
        fDb.collection("Users")
                //.document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                username.setText(document.get("Name").toString());
                                useremail.setText(document.get("Email").toString());
                            }
                        } else {
                            Toast.makeText(profile.this, "Failed to fetch data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
