package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class setDp extends AppCompatActivity {

    private ImageView userDp;
    private Button btn_cancel, btn_setDP;
    private StorageReference storageRef;
    private final int PICK_IMAGE_REQUEST = 22;
    final int PIC_CROP = 2;
    private Uri filePath;
    private final String SAMPLE_CROPPED_IMG = "SampleCrop";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_dp);
        getSupportActionBar().setTitle("Change Dp !!");
        userDp = findViewById(R.id.avatar);
        btn_cancel = findViewById(R.id.cancel);
        btn_setDP = findViewById(R.id.changeDp);
        storageRef = FirebaseStorage.getInstance().getReference().child("images/userDP/" + FirebaseAuth.getInstance().getUid()+ ".jpg");
        fun_setDP();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),profile.class));
            }
        });
        btn_setDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Changing DP", Toast.LENGTH_LONG).show();
                SelectImage();
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
                                Glide.with(getApplicationContext()).load(localFile).into(userDp);
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

    // Select Image method
    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("crop", true);
        // indicate aspect of desired crop
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK  && data != null  && data.getData() != null) {
            filePath = data.getData();
            startCrop(filePath);
            /*filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                userDp.setImageBitmap(bitmap);
            }catch (IOException e) {
                e.printStackTrace();
            }
            performCrop();*/
        }
        /*else if (requestCode == PIC_CROP  && resultCode == RESULT_OK ) {
            Bundle extras = data.getExtras();
            Bitmap thePic = extras.getParcelable("data");
            userDp.setImageBitmap(thePic);
        }*/
        else if(requestCode == UCrop.REQUEST_CROP){
            Uri imageResultCrop = UCrop.getOutput(data);
            if(imageResultCrop != null){
                userDp.setImageURI(imageResultCrop);
            }
        }
    }

    private void performCrop(){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(filePath, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("scale", true);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            if (cropIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cropIntent, PIC_CROP);
            }
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void startCrop(@NonNull Uri uri){
        String destFileName = SAMPLE_CROPPED_IMG +".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(),destFileName)));
        uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(300,300);
        uCrop.withOptions(getCropOptions());

        uCrop.start(setDp.this);
    }

    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        //options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        //options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        options.setToolbarTitle("Image");

        return options;

    }
}
