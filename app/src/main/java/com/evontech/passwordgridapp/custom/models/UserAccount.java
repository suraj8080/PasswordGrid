package com.evontech.passwordgridapp.custom.models;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private int id;
    private String accountName;
    private String userName;
    private String accountUrl;
    private String accountPwd;
    private int accountGridId;

    public UserAccount() {
    }

    public UserAccount(String accountName, String userName, String accountUrl) {
        this.accountName = accountName;
        this.userName = userName;
        this.accountUrl = accountUrl;
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
