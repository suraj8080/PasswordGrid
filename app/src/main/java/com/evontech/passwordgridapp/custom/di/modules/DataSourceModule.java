package com.evontech.passwordgridapp.custom.di.modules;

import android.content.Context;

import com.evontech.passwordgridapp.custom.UserLoginDataSource;
import com.evontech.passwordgridapp.custom.data.AccountDataSource;
import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.sqlite.DbHelper;
import com.evontech.passwordgridapp.custom.data.sqlite.GridDataSQLiteDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

@Module
public class DataSourceModule {

    @Provides
    @Singleton
    DbHelper provideDbHelper(Context context) {
        return new DbHelper(context);
    }

    @Provides
    @Singleton
    GridDataSource provideGridRoundDataSource(DbHelper dbHelper) {
        return new GridDataSQLiteDataSource(dbHelper);
    }

    @Provides
    @Singleton
    AccountDataSource provideAccountDataSource(DbHelper dbHelper) {
        return new GridDataSQLiteDataSource(dbHelper);
    }

    @Provides
    @Singleton
    UserLoginDataSource provideLoginDataSource(DbHelper dbHelper) {
        return new GridDataSQLiteDataSource(dbHelper);
    }


}
