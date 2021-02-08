package com.evontech.passwordgridapp.custom.di.modules;

import android.content.Context;

import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.WordDataSource;
import com.evontech.passwordgridapp.custom.data.sqlite.DbHelper;
import com.evontech.passwordgridapp.custom.data.sqlite.GridDataSQLiteDataSource;
import com.evontech.passwordgridapp.custom.data.xml.WordXmlDataSource;

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

//    @Provides
//    @Singleton
//    WordDataSource provideWordDataSource(DbHelper dbHelper) {
//        return new WordSQLiteDataSource(dbHelper);
//    }

    @Provides
    @Singleton
    WordDataSource provideWordDataSource(Context context) {
        return new WordXmlDataSource(context);
    }

}
