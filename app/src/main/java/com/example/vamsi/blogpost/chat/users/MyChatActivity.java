package com.example.vamsi.blogpost.chat.users;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.feed.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Message> mlist;
    MessageRecyclerAdapter adapter;
    EditText Mymessage;
    int flag2;
    FirebaseStorage storage;
    LinearLayout attachment_ll;
    String usermessage;
    Button removeattachment;
    private String user_id, username, username2;
    CircleImageView send_btn, icon;
    private FirebaseAuth firebaseAuth;
    String URL;
    Message message3;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    ImageView attachment;
    LinearLayout chatbox_ll;
    ProgressDialog progressDialog;
    Uri File;

    FirebaseDatabase database;
    DatabaseReference Reference;


    private FirebaseFirestore firebaseFirestore;
        String key;
        int flag=0;
    Bundle bundle;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);
        bundle = getIntent().getExtras();
if(bundle!=null) {
    if (bundle.getString("parent").equals("GroupInfo")) {
        flag = 1;
        key = bundle.getString("key");
    }

}

    storage=FirebaseStorage.getInstance();
        Toolbar setupToolbar = findViewById(R.id.myChatToolbar);
        title = findViewById(R.id.chatbox_toolbar_title);
        icon = findViewById(R.id.chatbox_toolbar_icon);
        send_btn = findViewById(R.id.mychat_send_btn);
        chatbox_ll=findViewById(R.id.chatbox_attachment_liner_layout);
        Mymessage = findViewById(R.id.mychat_edittext);
        attachment=findViewById(R.id.attach_attachment);
        attachment_ll=findViewById(R.id.attachment_linear_layout);
        removeattachment=findViewById(R.id.remove_attachment);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        if(flag==0)
        databaseReference = firebaseDatabase.getReference("Groups");
        else if(flag==1)
            databaseReference=firebaseDatabase.getReference("Users");
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Chat Group");


        title.setText(bundle.getString("groupname"));
        Glide.with(this).load(bundle.getString("groupimage")).into(icon);
        recyclerView = findViewById(R.id.mychat_rv);
        mlist = new ArrayList<>();
Log.i("Myflag val",flag+"");
flag2=flag;
        getUserame(flag);

        removeattachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachment_ll.setVisibility(View.GONE);
                File=null;
            }
        });






        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 4. set adapter
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyChatActivity.this,GroupInfo.class);
                intent.putExtra("groupname",title.getText().toString());
                intent.putExtra("groupimage",bundle.getString("groupimage"));
                startActivity(intent);
            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFile();
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usermessage = Mymessage.getText().toString();

                DateFormat df = new SimpleDateFormat("h:mm a");
                Date currenttime = Calendar.getInstance().getTime();

                String usertime = df.format(currenttime);
                String myusername = username;
                if(File==null) {
                    Message message = new Message(myusername, usertime, usermessage);
                    if (flag == 0)
                        databaseReference.child(bundle.getString("groupname")).child("chats").child(System.currentTimeMillis() + "").setValue(message);
                    else if (flag == 1)
                        databaseReference.child(key.replaceAll(" ", "")).child("chats").child(System.currentTimeMillis() + "").setValue(message);
                }
                else if(File!=null)
                {Log.i("In Upload file","True");
                    uploadFile(File);}
                }
        });


    }

    private void getUserame(final int myflag) {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        username = task.getResult().getString("name");
                        Log.i("Username value", username);


                    }
                } else {
                    String error = task.getException().getMessage();
                }
                if(myflag==0)
                { databaseReference.child(bundle.getString("groupname")).child("chats")
                .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mlist.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message = ds.getValue(Message.class);
                                Log.i("messages are"," "+message.getUserName()+" "+message.getMessage());
                                mlist.add(message);


                            }

                            Log.i("What is the error",username+"");
                            adapter = new MessageRecyclerAdapter(mlist, MyChatActivity.this, username,flag);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            if (adapter.getItemCount() > 0)
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if (myflag==1) {

                   databaseReference.child(key.replaceAll(" ","")).child("chats")
                .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mlist.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message = ds.getValue(Message.class);
                                Log.i("messages are"," "+message.getUserName()+" "+message.getMessage());
                                mlist.add(message);
                            }

                            Log.i("What is the error",username+"");
                            adapter = new MessageRecyclerAdapter(mlist, MyChatActivity.this, username,flag);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            if (adapter.getItemCount() > 0)
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


            }


        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    File = data.getData();
                    attachment_ll.setVisibility(View.VISIBLE);

                }
                break;

        }
    }


    public void attachFile()
   {  if (ContextCompat.checkSelfPermission(MyChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
       Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       intent.setType("application/pdf");

       startActivityForResult(intent, 1);
   } else {
       ActivityCompat.requestPermissions(MyChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
   }}

    private void uploadFile(final Uri file) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();
if(flag2==0)
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
else if(flag2==1)
    databaseReference = FirebaseDatabase.getInstance().getReference("Users");




        final String fileName = username;
        DateFormat df = new SimpleDateFormat("h:mm a");
        final Date currenttime = Calendar.getInstance().getTime();


        StorageReference storageReference = storage.getReference("Attachments");
        storageReference.child(fileName).putFile(file)
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
                                        if(Mymessage.getText().toString().equals(""))
                                       message3 =new Message(username,currenttime+"","",URL);
                                        else
                                            message3=new Message(username,currenttime+"",usermessage,URL);

                                      if(flag2==0) {
                                          Log.i("Flag2",flag2+"");
                                          message3.setMessage(Mymessage.getText().toString());
                                          databaseReference.child(bundle.getString("groupname")).child("chats")
                                                  .child(System.currentTimeMillis() + "").setValue(message3)
                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<Void> task) {
                                                          Toast.makeText(MyChatActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                 Mymessage.setText("");
                                                 File=null;
                                                          attachment_ll.setVisibility(View.GONE);
                                                      }
                                                  });
                                          // sendNotificationToUser("updates", flatno+","+id , "Master.Java");
                                      }

                                      else if(flag2==1)
                                      {   Log.i("Flag2",flag2+"");
                                          Log.i("My message value",Mymessage.getText().toString());
                                      message3.setMessage(Mymessage.getText().toString().trim());
                                          databaseReference.child(key.replaceAll(" ","")).child("chats")
                                              .child(System.currentTimeMillis() + "").setValue(message3)
                                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<Void> task) {
                                                      Toast.makeText(MyChatActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                             Mymessage.setText("");
                                             File=null;
                                             attachment_ll.setVisibility(View.GONE);
                                                  }
                                              });}



                                    }
                                });
                            }
                        }


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
                if(currentProgress==100)
                    progressDialog.dismiss();
            }
        });

    }
}
