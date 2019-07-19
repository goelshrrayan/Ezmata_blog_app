package com.example.vamsi.blogpost.blogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.feed.BlogPost;
import com.example.vamsi.blogpost.feed.Comments;
import com.example.vamsi.blogpost.feed.CommentsRecyclerAdapter;
import com.example.vamsi.blogpost.feed.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminBlogRecyclerAdapter extends RecyclerView.Adapter<AdminBlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> adminBlogList;
    public Context context;
    int val;
    FirebaseDatabase database;
    DatabaseReference reference;

    public AdminBlogRecyclerAdapter() {

    }

    public AdminBlogRecyclerAdapter(List<BlogPost> adminBlogList, int val) {

        this.adminBlogList = adminBlogList;
        this.val = val;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_blog_recycler_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdminBlogRecyclerAdapter.ViewHolder holder, final int position) {
        final String uri = adminBlogList.get(position).getImage_url();
        database = FirebaseDatabase.getInstance();
        if (val == 1) {
            holder.delete_blog_button.setVisibility(View.VISIBLE);
            holder.delete_blog_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure you want to delete the blog?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adminBlogList.remove(position);
                            reference = database.getReference("Blogs");
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        BlogPost bp = ds.getValue(BlogPost.class);
                                        if (uri.equals(bp.getImage_url())) {
                                            reference.child(ds.getKey()).removeValue();
                                            notifyDataSetChanged();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
        final String date, desc;
        date = adminBlogList.get(position).getDati();
        desc = adminBlogList.get(position).getDesc();


        holder.dati.setText(date);
        holder.description.setText(desc);

        Glide.with(context).load(uri).into(holder.admin_post_image);


        holder.admin_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!adminBlogList.get(position).getLink().equals("")) {
                    Intent intent = new Intent(context, BlogWebView.class);
                    intent.putExtra("weblink",adminBlogList.get(position).getLink());
                    intent.putExtra("title",desc);
                    context.startActivity(intent);
                }
                }
        });

    }


    @Override
    public int getItemCount() {

        if (adminBlogList != null) {

            return adminBlogList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView dati, description;
        private ImageView admin_post_image;
        private Button delete_blog_button;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            dati = mView.findViewById(R.id.admin_blog_user_name);
            description = mView.findViewById(R.id.admin_blog_desc);
            admin_post_image = mView.findViewById(R.id.admin_blog_image);
            delete_blog_button = mView.findViewById(R.id.delete_admin_post);
        }


    }


}
