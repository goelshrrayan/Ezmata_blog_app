package com.example.vamsi.blogpost.videos;

public class YouTubeVideos {
    String videoUrl,description,videothumbnail;

    public YouTubeVideos() {
    }

    public YouTubeVideos(String videoUrl, String description, String videothumbnail) {
        this.videoUrl = videoUrl;
        this.description = description;
        this.videothumbnail = videothumbnail;
    }

    public YouTubeVideos(String videoUrl,String description) {
        this.videoUrl = videoUrl;
        this.description=description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideothumbnail() {
        return videothumbnail;
    }

    public void setVideothumbnail(String videothumbnail) {
        this.videothumbnail = videothumbnail;
    }
}
