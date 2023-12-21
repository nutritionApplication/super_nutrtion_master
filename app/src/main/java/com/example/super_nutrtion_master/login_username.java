package com.example.super_nutrtion_master;

//用來保存使用者帳號，讓所有的activity與fragment可以獲取帳號字串
public class login_username {
    private static login_username instance;
    private String username;

    private login_username() {

    }

    public static synchronized login_username getInstance() {
        if (instance == null) {
            instance = new login_username();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userInput) {
        this.username = userInput;
    }
}
