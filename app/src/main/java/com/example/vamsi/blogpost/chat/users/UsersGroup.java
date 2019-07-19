package com.example.vamsi.blogpost.chat.users;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.chat.Group;
import com.example.vamsi.blogpost.chat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersGroup extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Group> mlist;
    UsersGroupListAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_group);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Groups");
        Toolbar setupToolbar = findViewById(R.id.userChatGroupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("User Groups");
        recyclerView = findViewById(R.id.user_group_recycler_view);
        mlist = new ArrayList<>();



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 4. set adapter

        adapter = new UsersGroupListAdapter(mlist, UsersGroup.this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        showCurrentUserGroups();

    }

    private void showCurrentUserGroups() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        email = currentUser.getEmail();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
mlist.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.i("Values", ds.getKey() + "");
                    Group group = ds.getValue(Group.class);
                    final String child1 = group.getGroupName();
                    final String child1uri = group.getUrl();
                    databaseReference.child(child1).child("groupmembers")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                        User user = ds.getValue(User.class);
                                        Log.i("Values inner", user.getEmail() + "");
                                        if (email.equals(user.getEmail())) {
                                            if(user.isChecked()) {
                                                mlist.add(new Group(child1uri, child1));
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
