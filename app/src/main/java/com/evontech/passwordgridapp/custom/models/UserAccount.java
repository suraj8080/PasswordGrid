package com.evontech.passwordgridapp.custom.models;

public class UserAccount {
    private String accountName;
    private String userName;
    private String accountUrl;

    public UserAccount(String accountName, String userName, String accountUrl) {
        this.accountName = accountName;
        this.userName = userName;
        this.accountUrl = accountUrl;
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
