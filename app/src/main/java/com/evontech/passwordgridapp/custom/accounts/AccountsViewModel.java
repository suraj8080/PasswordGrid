package com.evontech.passwordgridapp.custom.accounts;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.evontech.passwordgridapp.custom.data.AccountDataSource;
import com.evontech.passwordgridapp.custom.models.UserAccount;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class AccountsViewModel extends ViewModel {

    private int userId;
    private final AccountDataSource accountDataSource;
    private MutableLiveData<AccountState> mOnAccountState;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    static abstract class AccountState {}
    static class Loading extends AccountState {
        private Loading() {
        }
    }
    static class Loaded extends AccountState {
        List<UserAccount> accountList;
        private Loaded(List<UserAccount> userAccounts) {
            accountList = userAccounts;
        }
    }

    public AccountsViewModel(AccountDataSource dataSource) {
        accountDataSource = dataSource;
        resetLiveData();
    }

    private void resetLiveData() {
        mOnAccountState = new MutableLiveData<>();
        setAccountState(new Loading());
        //loadAccounts();
    }

    public void loadAccounts() {
        List<UserAccount> mAllAccountsData = new ArrayList<>();
            mAllAccountsData = accountDataSource.getAllAccountData(userId);
            setAccountState(new Loaded(mAllAccountsData));
    }

    public long updateUserAccount(UserAccount userAccount){
         userAccount.setUserId(userId);
        return accountDataSource.saveAccountData(userAccount);
    }

    public LiveData<AccountState> getOnAccountState() {
        return mOnAccountState;
    }

    private void setAccountState(AccountState state) {
        mOnAccountState.setValue(state);
    }
}
