package com.example.vamsi.blogpost.chat;

public class User {
    private String url,name,email,mobile;
    boolean checked;

    public User() {
    }

    public User(String url, String name,boolean checked,String email,String mobile) {
        this.url = url;
        this.name = name;
        this.checked=checked;
        this.email=email;
        this.mobile=mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setthisChecked(boolean checked) {
        this.checked = checked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
