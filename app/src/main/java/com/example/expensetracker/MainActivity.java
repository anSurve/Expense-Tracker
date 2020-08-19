package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button mLogout;
    TextView mWelcometext;
    FirebaseFirestore fDb;

    private final int PICK_IMAGE_REQUEST = 22;
    // view for image view
    private ImageView imageView;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    private ImageButton btnSelect;
    private Button btnUpload;

    StorageReference storageReference;
    ProgressBar mPBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogout = findViewById(R.id.logout_button);
        mWelcometext = findViewById(R.id.welcomeText);

        fDb = FirebaseFirestore.getInstance();

        fDb.collection("Users")
            //.document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
            .whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            mWelcometext.setText("Welcome "+ document.get("Name").toString());
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_LONG);
                    }
                }
            });

            mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        });

        // initialise views
        btnSelect = findViewById(R.id.selectDP);
        btnUpload = findViewById(R.id.uploadDP);
        mPBar = findViewById(R.id.loading);
        imageView = findViewById(R.id.avatar);

        // get the Firebase  storage reference
        //storage = FirebaseStorage.getInstance();
       // storageReference = storage.getReference();

        // on pressing btnSelect SelectImage() is called
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    // Select Image method
    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    // UploadImage method
    private void uploadImage()
    {
        storageReference = FirebaseStorage.getInstance().getReference();
        mPBar = findViewById(R.id.loading);
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            mPBar.setVisibility(View.VISIBLE);
            StorageReference ref = storageReference.child("images/userDP/" + FirebaseAuth.getInstance().getUid() + ".jpg");
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {

                    mPBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this,"Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), home.class));
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    // Error, Image not uploaded
                    mPBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this,"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK  && data != null  && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
