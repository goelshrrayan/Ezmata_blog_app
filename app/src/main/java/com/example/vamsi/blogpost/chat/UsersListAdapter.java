package com.example.vamsi.blogpost.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {
    private Context context;
    private List<User> mData;
    private LayoutInflater mInflater;
    private  UsersListAdapter.ViewHolder viewHolder;
    private String gp_name;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;


    int c=0;


    // data is passed into the constructor
    public UsersListAdapter(List<User> data,Context context,String gp_name) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.gp_name=gp_name;
    }

    public UsersListAdapter()
    {}

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.user_recycler_item_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final UsersListAdapter.ViewHolder holder, final int position) {
        final String name = mData.get(position).getName();
        final String url = mData.get(position).getUrl();
        final boolean checked=mData.get(position).isChecked();

        holder.username.setText(name);
        holder.checkBox.setChecked(checked);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked())
                { holder.checkBox.setChecked(true);
                mData.get(position).setthisChecked(true);}
                else
                {   holder.checkBox.setChecked(false);
                    mData.get(position).setthisChecked(false);}
            }
        });


        RequestOptions placeholderOption =new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile_placeholder);
        Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(url).into(holder.userimage);


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                checkBox.setVisibility(View.VISIBLE);
//                if(!checkBox.isChecked())
//                checkBox.setChecked(true);
//                if (checkBox.isChecked())
//                    checkBox.setChecked(false);
//            }
//        });


        /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatsActivity.class);
                intent.putExtra("groupname",name);
                intent.putExtra("groupimage",url);
                context.startActivity(intent);
            }
        });*/



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  User getUser(int position)
    {return mData.get(position);}



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        CircleImageView userimage;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            userimage=itemView.findViewById(R.id.user_image);
            checkBox=itemView.findViewById(R.id.checkbox_users);

        }

    }


}
