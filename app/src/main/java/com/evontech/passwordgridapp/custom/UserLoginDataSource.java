package com.evontech.passwordgridapp.custom;

import com.evontech.passwordgridapp.custom.models.AppUser;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public interface UserLoginDataSource {

    long registerUserLogin(AppUser user);

    AppUser userLogin(AppUser user);
}
