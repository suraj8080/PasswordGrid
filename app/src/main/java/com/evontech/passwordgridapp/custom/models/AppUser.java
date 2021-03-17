package com.evontech.passwordgridapp.custom.models;

public class AppUser {
    private int id;
    private String name;
    private String mobile;
    private String userName;
    private String userPassword;

    public AppUser(int id, String name, String mobile, String userName, String userPassword) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public AppUser() {
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
