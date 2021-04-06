package com.evontech.passwordgridapp.custom.models;

import java.io.Serializable;

public class UserAccount implements Serializable{
    private int id;
    private int userId;
    private String accountName;
    private String userName;
    private String accountUrl;
    private String accountPwd;
    private String accountCategory;
    private int accountGridId;

    public UserAccount() {
    }

    public UserAccount(String accountName, String userName, String accountUrl, String accountCategory) {
        this.accountName = accountName;
        this.userName = userName;
        this.accountUrl = accountUrl;
        this.accountCategory = accountCategory;
    }

    public String getAccountCategory() {
        return accountCategory;
    }

    public void setAccountCategory(String accountCategory) {
        this.accountCategory = accountCategory;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountGridId() {
        return accountGridId;
    }

    public void setAccountGridId(int accountGridId) {
        this.accountGridId = accountGridId;
    }

    public String getAccountPwd() {
        return accountPwd;
    }

    public void setAccountPwd(String accountPwd) {
        this.accountPwd = accountPwd;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccountUrl() {
        return accountUrl;
    }

    public void setAccountUrl(String accountUrl) {
        this.accountUrl = accountUrl;
    }
}
