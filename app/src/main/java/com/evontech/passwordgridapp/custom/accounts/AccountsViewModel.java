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
    private AccountDataSource accountDataSource;
    private List<UserAccount> mAllAccountsData;

    private AccountState mCurrentState = null;
    private MutableLiveData<AccountState> mOnAccountState;

    public AccountsViewModel(AccountDataSource dataSource) {
        accountDataSource = dataSource;
        resetLiveData();
    }

    private void resetLiveData() {
        mOnAccountState = new MutableLiveData<>();
        setAccountState(new Loading());
        loadAccounts();
    }

    public void loadAccounts() {
            mAllAccountsData = new ArrayList<>();
            mAllAccountsData = accountDataSource.getAllAccountData();
            setAccountState(new Loaded(mAllAccountsData));
    }

    public long updateUserAccount(UserAccount userAccount){
        return accountDataSource.saveAccountData(userAccount);
    }

    public LiveData<AccountState> getOnAccountState() {
        return mOnAccountState;
    }

    private void setAccountState(AccountState state) {
        mCurrentState = state;
        mOnAccountState.setValue(mCurrentState);
    }
}
