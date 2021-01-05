package com.evontech.passwordgridapp.custom.di.component;


import com.evontech.passwordgridapp.custom.grid.GridActivity;
import com.evontech.passwordgridapp.custom.grid.GridCriteriaActivity;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.di.modules.AppModule;
import com.evontech.passwordgridapp.custom.di.modules.DataSourceModule;
import com.evontech.passwordgridapp.custom.grid.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

@Singleton
@Component(modules = {AppModule.class, DataSourceModule.class})
public interface AppComponent {
    void inject(GridActivity activity);
    void inject(FullscreenActivity activity);
    void inject(MainActivity activity);
    void inject(GridCriteriaActivity gridCriteriaActivity);
}
