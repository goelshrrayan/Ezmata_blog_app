package com.example.vamsi.blogpost.blogs;

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
import android.widget.Button;
import android.widget.Toast;

import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.feed.BlogPost;
import com.example.vamsi.blogpost.feed.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminBlogsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    int val = -1;
    FloatingActionButton addFlatFab;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference reference;
        int c=0;
    List<BlogPost> mlist;
    AdminBlogRecyclerAdapter adminBlogRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_blogs);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Blogs");

        mlist=new ArrayList<>();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        sharedPreferences=getSharedPreferences("Status",MODE_PRIVATE);
        if (sharedPreferences.getInt("type", -1) == 1) {
            val = 1;
            Toast.makeText(this, "Admin", Toast.LENGTH_SHORT).show();
        } else {
            val = 0;
            Toast.makeText(this, " Not Admin", Toast.LENGTH_SHORT).show();

        }


        Toolbar setupToolbar = findViewById(R.id.adminBlogsToolbar);

        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Blogs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addFlatFab = findViewById(R.id.fab_admin_blogs);
        recyclerView=findViewById(R.id.admin_blogs_recycler_view);
        if(val==0){
            addFlatFab.hide();
        }
        addFlatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminBlogsActivity.this,AddAdminPost.class);
                startActivity(intent);
            }
        });

        adminBlogRecyclerAdapter=new AdminBlogRecyclerAdapter(mlist,val);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminBlogRecyclerAdapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mlist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    BlogPost bp=ds.getValue(BlogPost.class);
                    mlist.add(0,bp);
                    adminBlogRecyclerAdapter.notifyDataSetChanged();
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
        startActivity(new Intent(AdminBlogsActivity.this, MainActivity.class));
        finish();
    }
}
