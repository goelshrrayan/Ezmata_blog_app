package com.example.vamsi.blogpost.videos;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.blogs.AddAdminPost;
import com.example.vamsi.blogpost.blogs.AdminBlogsActivity;
import com.example.vamsi.blogpost.feed.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddVideo extends AppCompatActivity {
EditText videolink,videoDesc;
Button Upload;
FirebaseDatabase database;
DatabaseReference reference;
ImageView videoThumbnail;
Toolbar videoToolbar;
    private Uri videoImageuri = null;
    String URL;
String newUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        videoToolbar=findViewById(R.id.video_toolbar);
        setSupportActionBar(videoToolbar);
        getSupportActionBar().setTitle("Add New Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        videoDesc=findViewById(R.id.video_desc);
        videoThumbnail=findViewById(R.id.video_image);
        videolink=findViewById(R.id.video_url);
        Upload=findViewById(R.id.upload_btn);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Videos");
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if((videolink.getText().toString().equals("")||videoDesc.getText().toString().equals(""))&&videoImageuri==null)
               {
                   Toast.makeText(AddVideo.this, "Enter the link and description atleast", Toast.LENGTH_SHORT).show();
               }
                   else if(!videolink.getText().toString().equals("")&&!videoDesc.getText().toString().equals("")&&videoImageuri==null)
                {
                    uploadVideo(videolink.getText().toString(),videoDesc.getText().toString(),"");
                }
                else
                {newUrl="<iframe width=\"100%\" height=\"100%\" src=\"" +videolink.getText().toString()+ "\" frameborder=\"0\" allowfullscreen></iframe>";
                    uploadVideo(videolink.getText().toString(),videoDesc.getText().toString(),videoImageuri.toString());}
            }
        });


        videoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(1040, 800)
                        .setAspectRatio(1, 1)
                        .start(AddVideo.this);

            }
        });
    }

    public void uploadVideo(String url,String description,String imageuri)
    {YouTubeVideos video;
        if(imageuri.equals(""))
        video=new YouTubeVideos(url,description);
    else
            video=new YouTubeVideos(url,description,imageuri);
        reference.child(System.currentTimeMillis()+"").setValue(video);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("requestCoandimagecode",requestCode+" "+CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE+" "+resultCode);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                videoImageuri = result.getUri();
                videoThumbnail.setImageURI(videoImageuri);
            }




        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddVideo.this, AdminVideosActivity.class));
        finish();
    }
}
