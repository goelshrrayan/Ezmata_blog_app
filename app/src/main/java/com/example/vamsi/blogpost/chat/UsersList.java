package com.example.vamsi.blogpost.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vamsi.blogpost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity {

    RecyclerView recyclerView;
    UsersListAdapter usersListAdapter;
    private FirebaseFirestore firebaseFirestore;
    List<User> usersList, userList2,userList3;
    List<UserInfo> userInfoList;
    int c = 0;
    TextView AddToGroup;
    FirebaseDatabase database,database4;
    DatabaseReference reference, reference2,reference3,reference4;
    Bundle bundle;
    String gp_name, url;
    List<String> gp_names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Toolbar setupToolbar = findViewById(R.id.userssetupToolbar);
        setSupportActionBar(setupToolbar);
        userList2 = new ArrayList<User>();
        userList3=new ArrayList<>();
        userInfoList=new ArrayList<UserInfo>();
        getSupportActionBar().setTitle("");
        AddToGroup = findViewById(R.id.Add_to_group);
        database = FirebaseDatabase.getInstance();
        bundle = getIntent().getExtras();
        database4=FirebaseDatabase.getInstance();
        reference4=database4.getReference("Groups");
        if (bundle != null) {
            gp_name = bundle.getString("groupname");
            url = bundle.getString("groupimage");
        }
gp_names= new ArrayList<String>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.user_list_rv);
        usersListAdapter = new UsersListAdapter();
        usersList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);



        setAlreadyChecked();



        AddToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userList2.clear();
                for (int i = 0; i < usersListAdapter.getItemCount(); i++) {
                    User user = usersListAdapter.getUser(i);
                    reference3 = database.getReference("Groups").child(gp_name);
                    reference3.child("groupmembers").child(user.getMobile()).setValue(user);
                }

                Log.i("userlist value", userList2 + "");


                                Intent intent = new Intent(UsersList.this, ChatsActivity.class);
                                intent.putExtra("groupname", gp_name);
                                intent.putExtra("groupimage", url);
                                startActivity(intent);
                                finish();






            }
        });

    }

    private void showUsers() {


        firebaseFirestore.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usersList.clear();
                            userList2.clear();
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                String name = documentSnapshot.getString("name");
                                String image = documentSnapshot.getString("image");
                                String email = documentSnapshot.getString("email");
                                String mobile=documentSnapshot.getString("mobile");

                                User user = new User(image, name, false, email,mobile);
                                userList2.add(user);

                                c++;
                            }

                            usersListAdapter = new UsersListAdapter(usersList, UsersList.this,gp_name);
                            usersListAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(usersListAdapter);


                        }
                    }
                });



    }

    public void setAlreadyChecked() {

        reference2 = database.getReference("Groups").child(gp_name).child("groupmembers");
        Log.i("reference",reference2+"");

            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    usersList.clear();
                    int count = 0;
                    Log.i("count",count+"");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                       usersList.add(user);

                    }


                        if((usersList+"").equals("[]"))
                        {showUsers();}
                        else
                        { Log.i("Show User list",usersList+"");
                            usersListAdapter = new UsersListAdapter(usersList, UsersList.this, gp_name);
                            usersListAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(usersListAdapter);}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





    }


}
