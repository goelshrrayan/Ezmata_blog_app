package com.example.vamsi.blogpost.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private Context context;
    private List<Group> mData;
    private LayoutInflater mInflater;
    // data is passed into the constructor
    public GroupListAdapter(List<Group> data,Context context) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    public GroupListAdapter()
    {}

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.group_recycler_item_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = mData.get(position).getGroupName();
        final String url = mData.get(position).getUrl();
        holder.groupname.setText(name);
        RequestOptions placeholderOption =new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile_placeholder);

        Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(url).into(holder.groupimage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatsActivity.class);
                intent.putExtra("groupname",name);
                intent.putExtra("groupimage",url);
                context.startActivity(intent);
            }
        });



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupname;
        CircleImageView groupimage;

        ViewHolder(View itemView) {
            super(itemView);
            groupname = itemView.findViewById(R.id.group_name);
            groupimage=itemView.findViewById(R.id.group_icon);
        }

    }
}
