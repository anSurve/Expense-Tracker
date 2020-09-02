package com.example.expensetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView mProfilePic;
    private TextView mName, mEmail;
    private StorageReference storageRef;
    private NavigationView navigationView;
    FirebaseFirestore fDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_e_e, R.id.nav_investments,
                R.id.nav_lent_borrowed,R.id.nav_categories, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        fDb = FirebaseFirestore.getInstance();
        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        mName = navHeader.findViewById(R.id.name);
        mEmail = navHeader.findViewById(R.id.email);
        mProfilePic = navHeader.findViewById(R.id.profile);
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageRef.child("images/userDP/" + FirebaseAuth.getInstance().getUid()+ ".jpg");
        try {
            final File localFile = File.createTempFile("images","jpg");
           // Toast.makeText(getApplicationContext(),"Local file created",Toast.LENGTH_LONG).show();
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                Glide.with(getApplicationContext()).load(localFile).into(mProfilePic);
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

        fDb.collection("Users")
                //.document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                mName.setText(document.get("Name").toString());
                                mEmail.setText(document.get("Email").toString());
                            }
                        } else {
                            Toast.makeText(home.this, "Failed to fetch data", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),profile.class));
            }
        });
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),profile.class));
            }
        });
        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),profile.class));
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
