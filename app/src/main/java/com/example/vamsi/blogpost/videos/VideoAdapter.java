package com.example.vamsi.blogpost.videos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vamsi.blogpost.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    List<YouTubeVideos> youtubeVideoList;
    Context context;
    private Button b;
    int po;




    public VideoAdapter() {
    }

    public VideoAdapter(List<YouTubeVideos> youtubeVideoList) {
        this.youtubeVideoList = youtubeVideoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.video_view, parent, false);
        context=parent.getContext();

        return new VideoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position)  {

      //  holder.videoWeb.loadData(youtubeVideoList.get(position).getVideoUrl(),"text/html; charset=utf-8",null);
        final String desc,imageuri,videouri;

    desc=youtubeVideoList.get(position).getDescription();
    imageuri=youtubeVideoList.get(position).getVideothumbnail();
    videouri=youtubeVideoList.get(position).getVideoUrl();
        int index=videouri.indexOf("?v=");
        final String newUrl=videouri.substring(index+3,index+14);
    holder.Description.setText(desc);

    if(imageuri!=null)
    {Glide.with(context).load(imageuri).into(holder.thumbnail);}
else {
        URL url;
        try {
            url = new URL("https://img.youtube.com/vi/" + newUrl + "/hqdefault.jpg");
            Log.i("me url", "https://img.youtube.com/vi/" + newUrl + "/hqdefault.jpg");

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.mipmap.ic_launcher_round);
            Glide.with(context).load(url).apply(options).into(holder.thumbnail);
            // new DownloadImageTask(holder.thumbnail)
            //       .execute(("https://img.youtube.com/vi/"+newUrl +"/1.jpg").trim());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }


        //Glide.with(context).load("https://img.youtube.com/vi/+"+newUrl +"/1.jpg").into(holder.thumbnail);

    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent=new Intent(context,PlayVideo.class);
            intent.putExtra("videouri",videouri);
            intent.putExtra("description",desc);
            context.startActivity(intent);
        }
    });


    //        WebChromeClient webChromeClient = new WebChromeClient();
//        holder.videoWeb.setWebChromeClient(webChromeClient);



    }

    @Override
    public int getItemCount() {
        return youtubeVideoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {


        private ImageView thumbnail;
        TextView Description;


        public VideoViewHolder(View itemView) {
            super(itemView);

            thumbnail =itemView.findViewById(R.id.videoImageView);
            Description=itemView.findViewById(R.id.video_view_description);

        }
    }


    public  String getTitleQuietly(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                        youtubeUrl + "&format=json"
                );

                return new JSONObject(IOUtils.toString(embededURL)).getString("title");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Log.i("bitmap",result+"");
            bmImage.setImageBitmap(result);
        }
    }
    }
