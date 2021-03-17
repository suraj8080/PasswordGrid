package com.evontech.passwordgridapp.custom.AppUser;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evontech.passwordgridapp.custom.UserLoginDataSource;
import com.evontech.passwordgridapp.custom.models.AppUser;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class LoginViewModel extends ViewModel {

    static abstract class LoginState {}
    static class Loading extends LoginState{
        private Loading(){
        }
    }
    static class RegistrationDone extends LoginState {
        public AppUser appUser;
        private RegistrationDone(AppUser appUser) {
            this.appUser = appUser;
        }
    }
    static class LoginDone extends LoginState {
        public AppUser appUser;
        private LoginDone(AppUser appUser) {
            this.appUser = appUser;
        }
    }
    private UserLoginDataSource loginDataSource;

    private LoginState mCurrentState = null;
    private MutableLiveData<LoginState> mOnLoginState;

    public LoginViewModel(UserLoginDataSource dataSource) {
        loginDataSource = dataSource;
        resetLiveData();
    }

    public void resetLiveData() {
        mOnLoginState = new MutableLiveData<>();
        setLoginState(null);
        //loadAccounts();
    }

    public void registerUser(AppUser user){
         setLoginState(new Loading());
         user.setId((int) loginDataSource.registerUserLogin(user));
         setLoginState(new RegistrationDone(user));
    }

    public void loginUser(AppUser appUser){
         setLoginState(new Loading());
         setLoginState(new  LoginDone(loginDataSource.userLogin(appUser)));
    }

    public LiveData<LoginState> getOnAccountState() {
        return mOnLoginState;
    }

    private void setLoginState(LoginState state) {
        mCurrentState = state;
        mOnLoginState.setValue(mCurrentState);
    }
}
