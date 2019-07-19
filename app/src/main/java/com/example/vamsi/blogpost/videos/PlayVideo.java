package com.example.vamsi.blogpost.videos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.vamsi.blogpost.R;
import com.example.vamsi.blogpost.feed.MainActivity;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayVideo extends YouTubeBaseActivity {
Bundle bundle;
    Toolbar playVideoToolbar;
    String title,url;
    YouTubePlayerView yb;
    TextView desc,Title;
    ImageView back_button;
    private YouTubePlayer.OnInitializedListener ylistener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view_original);
        bundle=getIntent().getExtras();
        desc=findViewById(R.id.playvideo_desc);
        Title=findViewById(R.id.video_title);
        back_button = findViewById(R.id.play_video_back);

        if(bundle!=null) {
            title=bundle.getString("description");
            Title.setText(title);
            url=bundle.getString("videouri");
            yb=findViewById(R.id.videoWebView);
           int index=url.indexOf("?v=");
            final String newUrl=url.substring(index+3,index+14);
            desc.setText(title);

            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PlayVideo.this, AdminVideosActivity.class));
                    finish();
                }
            });

            ylistener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.loadVideo(newUrl );
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            };

            yb.initialize("AIzaSyCXENOwbi4rA98mS7k9FvDfSIU5-7DqnIs",ylistener);

        }
    }
}
