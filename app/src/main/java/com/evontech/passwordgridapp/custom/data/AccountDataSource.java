package com.evontech.passwordgridapp.custom.data;

import com.evontech.passwordgridapp.custom.data.entity.GridDataEntity;
import com.evontech.passwordgridapp.custom.models.GridDataInfo;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.UserAccount;

import java.util.List;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public interface AccountDataSource {

    long saveAccountData(UserAccount userAccount);

    UserAccount getAccountData(int accountId, int userId);

    List<UserAccount> getAllAccountData(int userId);
}
