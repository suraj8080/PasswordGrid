package com.evontech.passwordgridapp.custom.settings;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.evontech.passwordgridapp.custom.grid.GridViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private GridViewModel mGridViewModel;

    public ViewModelFactory(GridViewModel mGridViewModel) {
        //Log.d("ViewModelFactory", gamePlayViewModel+"");
        this.mGridViewModel = mGridViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GridViewModel.class)) {
            return (T) mGridViewModel;
        }
        throw new IllegalArgumentException("Unknown view model");
    }
}
