package com.example.vamsi.blogpost.chat.users;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.chat.User;
import com.example.vamsi.blogpost.feed.Comments;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupInfo extends AppCompatActivity {

    Bundle bundle;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String groupName,imageString;
    List<Comments> mlistcomment;
    private RecyclerView recyclerView;
    private GroupInfoRecyclerAdapter groupInfoRecyclerAdapter;
    List<User> mlist;
    ImageView imageView;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        mlist= new ArrayList<User>();
        imageView=findViewById(R.id.groupinfo_image);
        name=findViewById(R.id.groupinfo_name);
        recyclerView = findViewById(R.id.group_members_rv);
        bundle = getIntent().getExtras();
        groupName = bundle.getString("groupname");
        imageString = bundle.getString("groupimage");
        name.setText(groupName);
        Glide.with(this).load(imageString).into(imageView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupInfoRecyclerAdapter = new GroupInfoRecyclerAdapter(mlist);
        recyclerView.setAdapter(groupInfoRecyclerAdapter);
        // 4. set adapter

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Groups");

        reference.child(groupName).child("groupmembers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             mlist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
             {
                 User user=ds.getValue(User.class);
                 mlist.add(user);
                 groupInfoRecyclerAdapter.notifyDataSetChanged();

             }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
