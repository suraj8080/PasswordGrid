package com.evontech.passwordgridapp.custom;

import android.app.Application;
import android.util.Log;

import com.evontech.passwordgridapp.custom.di.component.AppComponent;

import com.evontech.passwordgridapp.custom.di.component.DaggerAppComponent;
import com.evontech.passwordgridapp.custom.di.modules.AppModule;


/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class PasswordGridApp extends Application {
    private static final String TAG = "PasswordGridApp";

    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        //Log.d(TAG, "onCreate: "+mAppComponent);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
