package com.example.vamsi.blogpost.chat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.feed.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupDetails extends AppCompatActivity {

    CircleImageView circleImageView;
    Button creategrp_btn;
    EditText groupName;
    private Uri mainImageURI = null;
    ProgressDialog progressDialog;
String URL;
FirebaseDatabase firebaseDatabase;
DatabaseReference reference;
    private boolean isChanged = false;
FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_details);
        Toolbar setupToolbar = findViewById(R.id.groupsetupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Group Settings");
firebaseDatabase=FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
reference=firebaseDatabase.getReference("Groups");
        circleImageView=findViewById(R.id.group_setup_image);
        groupName=findViewById(R.id.group_setup_name);
        creategrp_btn=findViewById(R.id.group_setup_btn);

        creategrp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupName.getText().toString().trim().equals(""))
                    Toast.makeText(AddGroupDetails.this, "Add group Name", Toast.LENGTH_SHORT).show();
                else
                {
                uploadFile(mainImageURI,groupName.getText().toString());

                }
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(AddGroupDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(AddGroupDetails.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(AddGroupDetails.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {

                        BringImagePicker();
                    }
                } else {

                    BringImagePicker();

                }


            }
        });
    }


    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(AddGroupDetails.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                circleImageView.setImageURI(mainImageURI);
                isChanged=true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void uploadFile (final Uri file, final String groupName){

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Sending Request...");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();




        final String fileName = System.currentTimeMillis() + "";






        StorageReference storageReference = storage.getReference("Groups");
        storageReference.child(groupName).child(fileName).putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        URL = uri.toString();
                                        //createNewPost(imageUrl);
                                        Log.i("URL IS", URL + "");
                                        Group visitors_admin = new Group(URL + "",groupName);
                                        reference.child(groupName).setValue(visitors_admin)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(AddGroupDetails.this,AddGroupActivity.class));
                                                            finish();
                                                    }
                                                });
                                        // sendNotificationToUser("updates", flatno+","+id , "Master.Java");




                                    }
                                });
                            }
                        }


//                        DatabaseReference reference = database.getReference(flat.getText().toString().trim());
//                        databaseReference.child(flat.getText().toString().trim()).child(id).child(fileName).setValue(URL).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(getApplicationContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
//
//
//
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "File not uploaded successfully", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                {
                    Toast.makeText(getApplicationContext(), "File not uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });




    }

}
