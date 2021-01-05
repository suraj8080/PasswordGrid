package com.evontech.passwordgridapp.custom.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.WordDataSource;
import com.evontech.passwordgridapp.custom.grid.GridViewModel;
import com.evontech.passwordgridapp.custom.settings.ViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

@Module
public class AppModule {

    private Application mApp;

    public AppModule(Application application) {
        mApp = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(GridDataSource gridDataSource,
                                             WordDataSource wordDataSource) {
        return new ViewModelFactory(
                new GridViewModel(gridDataSource, wordDataSource)
        );
    }
}
