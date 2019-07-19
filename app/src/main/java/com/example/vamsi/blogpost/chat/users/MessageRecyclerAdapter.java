package com.example.vamsi.blogpost.chat.users;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.chat.Group;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Message> mData;
    private LayoutInflater mInflater;
    private String username;
    private int flag;
    String filename;
    // data is passed into the constructor
    public MessageRecyclerAdapter(List<Message> data,Context context,String username,int flag) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.username=username;
        this.flag=flag;
    }

    public MessageRecyclerAdapter()
    {}

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.my_chat_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(MessageRecyclerAdapter.ViewHolder holder, final int position) {
        final String userName = mData.get(position).getUserName();
        final String time = mData.get(position).getTime();
        final String message = mData.get(position).getMessage();
        String uri=mData.get(position).getUri();

        if(flag==0) {
if(uri!=null)
{holder.chatbox_attach_linearlayout.setVisibility(View.VISIBLE);
holder.chatbox_attach_linearlayout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        filename=mData.get(position).getUserName();
        new DownloadFile().execute(mData.get(position).getUri().trim());
    }
});
}
            if (userName.equals(username)) {
                Log.i("Usernames of adapter", userName);
                holder.linearlayout.setGravity(Gravity.RIGHT);
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCC99"));
                holder.userName.setTextColor(Color.parseColor("#FFFFFF"));
                holder.time.setTextColor(Color.parseColor("#123456"));
                holder.message.setTextColor(Color.parseColor("#000000"));
            } else {
                Log.i("Usernames of adapter", userName);
                holder.linearlayout.setGravity(Gravity.LEFT);
                holder.cardView.setCardBackgroundColor(Color.parseColor("#2196F3"));
                holder.userName.setTextColor(Color.parseColor("#FFFFFF"));
            }
            holder.userName.setText(userName);
            holder.time.setText(time);
            holder.message.setText(message);
        }
        else if(flag==1)
        {   if(uri!=null)
        {holder.chatbox_attach_linearlayout.setVisibility(View.VISIBLE);
            holder.chatbox_attach_linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filename=mData.get(position).getUserName();
                    new DownloadFile().execute(mData.get(position).getUri().trim());
                }
            });
        }
            if (userName.equals(username)) {
            Log.i("Usernames of adapter", userName);
            holder.linearlayout.setGravity(Gravity.RIGHT);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCC99"));
            holder.userName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.time.setTextColor(Color.parseColor("#123456"));
            holder.message.setTextColor(Color.parseColor("#000000"));
        } else {
            Log.i("Usernames of adapter", userName);
            holder.linearlayout.setGravity(Gravity.LEFT);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#2196F3"));
            holder.userName.setTextColor(Color.parseColor("#FFFFFF"));
        }
            holder.userName.setText(userName);
            holder.time.setText(time);
            holder.message.setText(message);}

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName,time,message;
        CardView cardView;
        LinearLayout linearlayout,chatbox_attach_linearlayout;



        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.chatbox_username);
            time =itemView.findViewById(R.id.chatbox_time);
            message = itemView.findViewById(R.id.chatbox_message);
            cardView=itemView.findViewById(R.id.chatbox_cardview);
            chatbox_attach_linearlayout=itemView.findViewById(R.id.chatbox_attachment_liner_layout);
            linearlayout=itemView.findViewById(R.id.linear_chat);
        }

    }


    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setTitle("Downloading content...Please Wait");
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "CompanyDocuments/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + filename+".pdf");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d("TAG", "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(context,
                    "Check Company Documents folder in Files Manager", Toast.LENGTH_LONG).show();
        }
    }



}
