package com.example.vamsi.blogpost.chat;

public class UserInfo {
    String name,url;


    public UserInfo()
    {}
    public UserInfo(String name, String url) {
        this.name = name;

        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
