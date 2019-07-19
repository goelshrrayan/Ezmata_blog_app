package com.example.vamsi.blogpost.feed;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.chat.Group;
import com.example.vamsi.blogpost.chat.User;
import com.example.vamsi.blogpost.chat.users.Message;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private EditText setupName, setUpMobile;

    private Button setupBtn;
    private String user_id, mobile_field;
    String myemail, mobile_global;
    private boolean isChanged = false;
    private ProgressBar setupProgress;
    Bundle bundle;
    int flag = 0;
    boolean checked = false;
    int type;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    FirebaseDatabase database, database2;
    FirebaseDatabase databaseUsers;
    DatabaseReference databaseUsersReference,databaseUsersReference2,databaseUsersReference3;
    String global_name;
    SharedPreferences sharedPreferences;

    DatabaseReference reference, ref2, ref3, ref4,ref5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        bundle = getIntent().getExtras();
        sharedPreferences = getSharedPreferences("Email", MODE_PRIVATE);
        Log.i("value of bundle", bundle + "");
        if (bundle != null) {
            myemail = bundle.getString("email");
        }
        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Settings");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Groups");

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
databaseUsers=FirebaseDatabase.getInstance();
databaseUsersReference=databaseUsers.getReference("Users");
databaseUsersReference2=databaseUsers.getReference("Users");
        databaseUsersReference3=databaseUsers.getReference("Users");

        setUpMobile = findViewById(R.id.setup_mobile);
        setupName = findViewById(R.id.setup_name);
        setupImage = findViewById(R.id.setup_image);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);
        setupProgress.setVisibility(View.VISIBLE);
        ref2 = database.getReference("Groups");
        setupBtn.setEnabled(false);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        global_name = name;
                        String mobile = task.getResult().getString("mobile");
                        mobile_global = mobile;
                        mainImageURI = Uri.parse(image);

                        setupName.setText(name);
                        setUpMobile.setText(mobile);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(SetupActivity.this).
                                setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "firestore Retrieval Error " + error, Toast.LENGTH_SHORT).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString().trim();
                final String user_mobile = setUpMobile.getText().toString().trim();

                if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(user_mobile) && mainImageURI != null) {
                    setupProgress.setVisibility(View.VISIBLE);


                    if (isChanged) {


                        user_id = firebaseAuth.getCurrentUser().getUid();


                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name, user_mobile);
                                } else {
                                    setupProgress.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SetupActivity.this, " Image Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        });


                    } else {
                        storeFirestore(null, user_name, user_mobile);
                    }
                }
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(SetupActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {

                        BringImagePicker();
                    }
                } else {

                    BringImagePicker();

                }


            }
        });

    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, final String user_name, final String user_mobile) {
        final Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mainImageURI;
        }


        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());
        myemail = sharedPreferences.getString("email", "");
        userMap.put("email", myemail);
        userMap.put("mobile", user_mobile);


        database2 = FirebaseDatabase.getInstance();
        ref3 = database2.getReference("Groups");
        ref4 = ref3;
        ref3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Group group = ds.getValue(Group.class);
                    final String child1 = group.getGroupName();
                    ref4.child(child1).child("groupmembers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                User user = dataSnapshot1.getValue(User.class);
                                if (mobile_global.equals(user.getMobile())) {
                                    checked = user.isChecked();
                                    flag = 1;
                                    if (user_mobile.equals(mobile_global)) {
                                        User user1 = new User(download_uri.toString(), user_name, checked, myemail, user_mobile);
                                        addtogroups(user1);
                                    } else {
                                        DatabaseReference databaseReference = database2.getReference("Groups").child(child1)
                                                .child("groupmembers").child(mobile_global);
                                        databaseReference.removeValue();
                                        User user2 = new User(download_uri.toString(), user_name, checked, myemail, user_mobile);
                                        addtogroups(user2);
                                    }
                                    break;
                                }

                            }

                            if (flag == 0) {
                                User user2 = new User(download_uri.toString(), user_name, false, myemail, user_mobile);
                                addtogroups(user2);
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

        Log.i("gloabal name username",global_name+"  "+user_name);

        if (!global_name.equals(user_name)) {

            databaseUsersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot ds:dataSnapshot.getChildren())
                    {Log.i("ds.getkeys",ds.getKey().indexOf(global_name.replaceAll(" ","")+"")+"");

                        if(ds.getKey().contains(global_name.replaceAll(" ","")+""))
                        {Log.i("Into the loop of keys","Into the loop");
                            final String newKey=ds.getKey().replace(global_name.replaceAll(" ",""),user_name.replaceAll(" ",""));
                            Log.i("newKey",newKey);

                            databaseUsersReference2.child(ds.getKey()).child("chats")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                            {Log.i("Into the 2 loop of keys","Into the 2 loop");
                                                com.example.vamsi.blogpost.chat.users.Message message = dataSnapshot1.getValue(Message.class);
                                               message.setUserName(user_name);
                                              databaseUsersReference3.child(newKey).child("chats").child(System.currentTimeMillis()+"").setValue(message);


                                            }
                                                databaseUsersReference3.child(ds.getKey()).setValue(null);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(SetupActivity.this, "User Settings Updated", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(SetupActivity.this, "Fire base Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);
                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void addtogroups(final User user) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Group group = ds.getValue(Group.class);
                    final String child1 = group.getGroupName();
                    final String child1uri = group.getUrl();
                    ref2.child(child1).child("groupmembers").child(user.getMobile()).
                            setValue(user);
                    ref2.child(child1).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                            {Message message=dataSnapshot1.getValue(Message.class);
                            if(message.getUserName().equals(global_name))
                            {
                                message.setUserName(user.getName());
                                ref2.child(child1).child("chats").child(dataSnapshot1.getKey()).setValue(message);
                            }}
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

        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
