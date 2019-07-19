package com.example.vamsi.blogpost.chat.users;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.chat.User;
import com.example.vamsi.blogpost.feed.Comments;
import com.example.vamsi.blogpost.feed.CommentsRecyclerAdapter;
import com.example.vamsi.blogpost.feed.MainActivity;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfoRecyclerAdapter extends RecyclerView.Adapter<GroupInfoRecyclerAdapter.ViewHolder> {

    public List<User> userList;
    public Context context;
    String my_user_name;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public GroupInfoRecyclerAdapter(List<User> userList) {

        this.userList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GroupInfoRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        if(userList.get(position).isChecked()) {
            final String commentMessage = userList.get(position).getMobile();
            holder.setComment_message(commentMessage);


            final String userName = userList.get(position).getName();
            final String userImage = userList.get(position).getUrl();
            holder.setData(userName, userImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Activity activity=(Activity) context;
                    final Dialog dialog = new Dialog(context);
                    Rect displayRectangle = new Rect();
                    Window window = (activity).getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                    LayoutInflater inflater2= (LayoutInflater) (activity).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater2.inflate(R.layout.userdetails, null);
                    layout.setMinimumWidth((int)(displayRectangle.width() * 0.8f));
                    layout.setMinimumHeight((int)(displayRectangle.height() * 0.8f));
                    dialog.setContentView(layout);
                    ImageView imageView=dialog.findViewById(R.id.userdetails_image);
                    TextView name=dialog.findViewById(R.id.username_userdetails);
                    TextView mobile=dialog.findViewById(R.id.usermobile_userdetails);

                    name.setText(userName);
                    mobile.setText(commentMessage);

                    Glide.with(activity).load(userImage).into(imageView);
                   window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    dialog.show();
                    dialog.setCancelable(false);

                    Button message,close;
                    message=layout.findViewById(R.id.sendMessage_btn);
                    close=layout.findViewById(R.id.Decline_request);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String user_id = firebaseAuth.getCurrentUser().getUid();
                            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        my_user_name = task.getResult().getString("name");
                                        String key;
                                    if(my_user_name.compareTo(userName)>=0)
                                    {key=my_user_name+userName;
                                        Toast.makeText(context, key.trim(), Toast.LENGTH_SHORT).show();}
                                        else
                                    {
                                        key=userName+my_user_name;
                                        Toast.makeText(context,key.trim(), Toast.LENGTH_SHORT).show();
                                    }

//                                        FirebaseDatabase database=FirebaseDatabase.getInstance();
//                                        DatabaseReference reference=database.getReference("Users");
//                                        reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });

                                        Intent intent=new Intent(context,MyChatActivity.class);
                                        intent.putExtra("groupname",userName);
                                        intent.putExtra("groupimage",userImage);
                                        intent.putExtra("parent","GroupInfo");
                                        intent.putExtra("key",key);
                                        context.startActivity(intent);
                                        dialog.dismiss();
                                        ((Activity) context).finish();

                                    }
                                }
                            });


                        }
                    });

                }
            });

        }

    }


    @Override
    public int getItemCount() {

        if (userList != null) {

            return userList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;

        public TextView commentUserName;
        public CircleImageView commentUserImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message) {

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }

        public void setData(String name, String image) {
            commentUserName = mView.findViewById(R.id.comment_username);
            commentUserImage = mView.findViewById(R.id.comment_image);
            commentUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(commentUserImage);

        }

    }
}
