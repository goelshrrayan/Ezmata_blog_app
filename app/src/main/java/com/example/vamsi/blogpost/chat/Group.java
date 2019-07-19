package com.example.vamsi.blogpost.chat;

public class Group {
    String url,groupName;

    public Group(){}

    public Group(String url, String groupName) {
        this.url = url;
        this.groupName = groupName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
