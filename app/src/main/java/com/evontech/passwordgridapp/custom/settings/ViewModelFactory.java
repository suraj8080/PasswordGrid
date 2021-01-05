package com.evontech.passwordgridapp.custom.settings;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.evontech.passwordgridapp.custom.grid.GridViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private GridViewModel mGamePlayViewModel;

    public ViewModelFactory(GridViewModel gamePlayViewModel) {
        //Log.d("ViewModelFactory", gamePlayViewModel+"");
        mGamePlayViewModel = gamePlayViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GridViewModel.class)) {
            return (T) mGamePlayViewModel;
        }
        throw new IllegalArgumentException("Unknown view model");
    }
}
