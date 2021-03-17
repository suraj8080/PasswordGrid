package com.evontech.passwordgridapp.custom.settings;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.evontech.passwordgridapp.custom.AppUser.LoginViewModel;
import com.evontech.passwordgridapp.custom.accounts.AccountsViewModel;
import com.evontech.passwordgridapp.custom.grid.GridViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private GridViewModel mGridViewModel;
    private AccountsViewModel mAccountsViewModel;
    private LoginViewModel mLoginViewModel;

    public ViewModelFactory(GridViewModel mGridViewModel, AccountsViewModel mAccountsViewModel, LoginViewModel mLoginViewModel) {
        //Log.d("ViewModelFactory", gridViewModel+"");
        this.mGridViewModel = mGridViewModel;
        this.mAccountsViewModel = mAccountsViewModel;
        this.mLoginViewModel = mLoginViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GridViewModel.class)) {
            return (T) mGridViewModel;
        } else if(modelClass.isAssignableFrom(AccountsViewModel.class))
            return (T) mAccountsViewModel;
        else if(modelClass.isAssignableFrom(LoginViewModel.class))
            return (T) mLoginViewModel;
        throw new IllegalArgumentException("Unknown view model");
    }
}
