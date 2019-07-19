package com.example.vamsi.blogpost.blogs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.feed.BlogPost;
import com.example.vamsi.blogpost.feed.MainActivity;
import com.example.vamsi.blogpost.feed.PostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class AddAdminPost extends AppCompatActivity {
    private Toolbar newPostToolbar;
    private ImageView newPostImage;
    private EditText newPostDesc,newPostLink;
    ProgressDialog progressDialog;
    private Button newPostBtn;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    private Uri postImageuri = null;
    String URL;
    Uri file;

        FirebaseStorage storage;
    private ProgressBar newPostProgress;

    private StorageReference storageReference;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin_post);

        newPostToolbar = findViewById(R.id.admin_new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        storage=FirebaseStorage.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        newPostImage = findViewById(R.id.admin_new_post_image);
        newPostDesc = findViewById(R.id.admin_new_post_desc);
        newPostBtn = findViewById(R.id.admin_post_btn);
        newPostLink=findViewById(R.id.admin_new_post_link);
        newPostProgress = findViewById(R.id.admin_new_post_progress);
        storageReference = FirebaseStorage.getInstance().getReference();

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Blogs");

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(1040, 800)
                        .setAspectRatio(1, 1)
                        .start(AddAdminPost.this);

            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postImageuri==null)
                    Toast.makeText(AddAdminPost.this, "Please select an image for the blog", Toast.LENGTH_SHORT).show();
                else
                uploadFile(postImageuri);
            }
        });
    }


    private void uploadFile (Uri files){

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();




        final String fileName = System.currentTimeMillis() + "";






        StorageReference storageReference = storage.getReference("Blogs");
        storageReference.child(fileName).putFile(files)
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
                                        DateFormat df = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                                        final String date = df.format(Calendar.getInstance().getTime());
                                        BlogPost blogPost;
                                        if(newPostLink.getText().toString().equals(""))
                                     blogPost = new BlogPost( URL + "",newPostDesc.getText().toString(),date);
                                       else
                                        {blogPost=new BlogPost( URL + "",newPostDesc.getText().toString(),date,newPostLink.getText().toString());}
                                        databaseReference.child(fileName).setValue(blogPost)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        Toast.makeText(AddAdminPost.this, "Check the user response in the flat list.", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                        newPostDesc.setText("");
                                                        newPostImage.setImageResource(R.mipmap.action_add_photo);
                                                        startActivity(new Intent(AddAdminPost.this,AdminBlogsActivity.class));
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
Log.i("requestCoandimagecode",requestCode+" "+CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE+" "+resultCode);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageuri = result.getUri();
                newPostImage.setImageURI(postImageuri);
            }




            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        }
    }

