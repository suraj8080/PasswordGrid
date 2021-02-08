package com.evontech.passwordgridapp.custom.settings;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.evontech.passwordgridapp.custom.accounts.AccountsViewModel;
import com.evontech.passwordgridapp.custom.grid.GridViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private GridViewModel mGridViewModel;
    private AccountsViewModel mAccountsViewModel;

    public ViewModelFactory(GridViewModel mGridViewModel) {
        //Log.d("ViewModelFactory", gridViewModel+"");
        this.mGridViewModel = mGridViewModel;
    }

    public ViewModelFactory(AccountsViewModel accountsViewModel) {
        //Log.d("ViewModelFactory", gridViewModel+"");
        this.mAccountsViewModel = accountsViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GridViewModel.class)) {
            return (T) mGridViewModel;
        } else if(modelClass.isAssignableFrom(AccountsViewModel.class))
            return (T) mAccountsViewModel;
        throw new IllegalArgumentException("Unknown view model");
    }
}
