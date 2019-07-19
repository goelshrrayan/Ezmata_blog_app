package com.example.vamsi.blogpost.chat;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.feed.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {

    FloatingActionButton addFlatFab;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    GroupListAdapter mAdapter;
    List<Group> mlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar setupToolbar = findViewById(R.id.groupchatToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Groups");
        firebaseDatabase=FirebaseDatabase.getInstance();
        mlist=new ArrayList<Group>();
        reference=firebaseDatabase.getReference("Groups");
        addFlatFab = findViewById(R.id.fab_add_group);
        recyclerView=findViewById(R.id.group_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 4. set adapter

       /* DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);*/
        addFlatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(AddGroupActivity.this, AddGroupDetails.class));
            }
        });


        showGroups();

    }

    public void showGroups()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               mlist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {Group g=ds.getValue(Group.class);
                mlist.add(g);
                }
                mAdapter = new GroupListAdapter(mlist,AddGroupActivity.this);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(AddGroupActivity.this,MainActivity.class));
        finish();
    }
}
