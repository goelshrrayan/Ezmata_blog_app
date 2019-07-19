package com.example.vamsi.blogpost.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.chat.users.GroupInfo;
import com.example.vamsi.blogpost.chat.users.Message;
import com.example.vamsi.blogpost.chat.users.MessageRecyclerAdapter;
import com.example.vamsi.blogpost.chat.users.MyChatActivity;
import com.example.vamsi.blogpost.feed.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsActivity extends AppCompatActivity {
    Bundle bundle;
    String name, url;
    CircleImageView icon;
    TextView Title;
    ImageView backButton;
    RecyclerView recyclerView;
    List<Message> mlist;
    MessageRecyclerAdapter adapter;
    EditText message;
    private String user_id, username,username2;
    CircleImageView send_btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);
//        Toolbar setupToolbar = findViewById(R.id.chatToolbar);
//
//        setupToolbar.setTitle("");
//        setSupportActionBar(setupToolbar);

        Toolbar setupToolbar = findViewById(R.id.myChatToolbar);
        backButton = findViewById(R.id.back_button_toolbar_chatbox);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatsActivity.this, AddGroupActivity.class));
                finish();
            }
        });

        title = findViewById(R.id.chatbox_toolbar_title);
        icon = findViewById(R.id.chatbox_toolbar_icon);
        send_btn = findViewById(R.id.mychat_send_btn);
        message = findViewById(R.id.mychat_edittext);
        bundle = getIntent().getExtras();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Groups");
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Chat Group");


        title.setText(bundle.getString("groupname"));
        Glide.with(this).load(bundle.getString("groupimage")).into(icon);
        recyclerView = findViewById(R.id.mychat_rv);
        mlist = new ArrayList<>();







    name = bundle.getString("groupname");
    url = bundle.getString("groupimage");



        title.setText(name);

        System.out.print("123");
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatsActivity.this, GroupInfo.class);
                intent.putExtra("groupname",bundle.getString("groupname"));
                intent.putExtra("groupimage",bundle.getString("groupimage"));
                startActivity(intent);
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usermessage = message.getText().toString();
                message.setText("");
                DateFormat df=new SimpleDateFormat("h:mm a");
                Date currenttime= Calendar.getInstance().getTime();

                String usertime =  df.format(currenttime);
                String myusername = username;
                Message message = new Message(myusername, usertime, usermessage);
                databaseReference.child(bundle.getString("groupname")).child("chats").child(System.currentTimeMillis()+"").setValue(message);
            }
        });

        getUserame();
    }

    private void getUserame() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        username = task.getResult().getString("name");
                        Log.i("Username value",username);


                    }
                } else {
                    String error = task.getException().getMessage();
                }
                databaseReference.child(name).child("chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mlist.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Message message = ds.getValue(Message.class);
                            mlist.add(message);

                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatsActivity.this));
                        adapter = new MessageRecyclerAdapter(mlist, ChatsActivity.this,username,0);
                        recyclerView.setAdapter(adapter);

                        adapter.notifyDataSetChanged();
                        if(adapter.getItemCount()>0)
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }


        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_group_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_group_member) {
            Intent intent = new Intent(ChatsActivity.this,UsersList.class);
            intent.putExtra("groupname",name);
            intent.putExtra("groupimage",url);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}
