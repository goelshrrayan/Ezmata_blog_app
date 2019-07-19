package com.example.vamsi.blogpost.chat.users;

public class Message {
    private String userName,time,message,uri;

    public Message(String userName, String time, String message,String uri) {
        this.userName = userName;
        this.time = time;
        this.message = message;
        this.uri=uri;
    }

    public Message(String userName, String time, String message) {
        this.userName = userName;
        this.time = time;
        this.message = message;
    }

    public Message() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
