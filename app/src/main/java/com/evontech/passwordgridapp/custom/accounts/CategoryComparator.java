package com.evontech.passwordgridapp.custom.accounts;

import com.evontech.passwordgridapp.custom.models.UserAccount;

import java.util.Comparator;

public class CategoryComparator implements Comparator {
    public int compare(Object o1,Object o2){
        UserAccount u1=(UserAccount)o1;
        UserAccount u2=(UserAccount)o2;

        return u1.getAccountCategory().compareTo(u2.getAccountCategory());
    }
}
