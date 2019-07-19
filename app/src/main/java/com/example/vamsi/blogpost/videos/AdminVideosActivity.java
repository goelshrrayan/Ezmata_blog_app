package com.example.vamsi.blogpost.videos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.blogs.AddAdminPost;
import com.example.vamsi.blogpost.blogs.AdminBlogsActivity;
import com.example.vamsi.blogpost.feed.MainActivity;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.Vector;

public class AdminVideosActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    int val = -1;
    FloatingActionButton addFlatFab;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference reference;
    int c=0;
    VideoAdapter videoAdapter;
    Vector<YouTubeVideos> youtubeVideos = new Vector<YouTubeVideos>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_videos);

        Toolbar setupToolbar = findViewById(R.id.adminVideosToolbar);

        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Videos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Videos");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        sharedPreferences = getSharedPreferences("Status", MODE_PRIVATE);
        if (sharedPreferences.getInt("type", -1) == 1) {
            val = 1;
            Toast.makeText(this, "Admin", Toast.LENGTH_SHORT).show();
        } else {
            val = 0;
            Toast.makeText(this, " Not Admin", Toast.LENGTH_SHORT).show();
        }





        addFlatFab = findViewById(R.id.fab_admin_videos);
        recyclerView=findViewById(R.id.admin_videos_recycler_view);
        if(val==0){
            addFlatFab.hide();
        }
        addFlatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminVideosActivity.this, AddVideo.class);
                startActivity(intent);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(youtubeVideos);

        recyclerView.setAdapter(videoAdapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                youtubeVideos.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {YouTubeVideos videos=ds.getValue(YouTubeVideos.class);
                    youtubeVideos.add(0,videos);
                    videoAdapter.notifyDataSetChanged();
                if(c==0)
                    progressDialog.dismiss();
                c++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminVideosActivity.this, MainActivity.class));
        finish();
    }






}
