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
        private Loaded() {
        }
    }
    private AccountDataSource accountDataSource;
    private List<UserAccount> mAllAccountsData;

    private AccountState mCurrentState = null;
    private MutableLiveData<AccountState> mOnGridState;

    public AccountsViewModel(AccountDataSource dataSource) {
        accountDataSource = dataSource;
        resetLiveData();
    }

    private void resetLiveData() {
        mOnGridState = new MutableLiveData<>();
    }

    public void loadAccounts() {
            setGridState(new Loading());
            mAllAccountsData = new ArrayList<>();
            setGridState(new Loaded());
            mAllAccountsData = accountDataSource.getAllAccountData();
    }

    public void updateUserAccount(UserAccount userAccount){
        accountDataSource.saveAccountData(userAccount);
    }

    public LiveData<AccountState> getOnGridState() {
        return mOnGridState;
    }

    private void setGridState(AccountState state) {
        mCurrentState = state;
        mOnGridState.setValue(mCurrentState);
    }
}
