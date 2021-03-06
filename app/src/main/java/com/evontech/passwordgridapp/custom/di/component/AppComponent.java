package com.evontech.passwordgridapp.custom.di.component;


import com.evontech.passwordgridapp.custom.AppUser.Login;
import com.evontech.passwordgridapp.custom.AppUser.Registration;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.accounts.AccountsActivity;
import com.evontech.passwordgridapp.custom.di.modules.AppModule;
import com.evontech.passwordgridapp.custom.di.modules.DataSourceModule;
import com.evontech.passwordgridapp.custom.grid.GridActivity;
import com.evontech.passwordgridapp.custom.grid.GridCriteriaActivity;
import com.evontech.passwordgridapp.custom.grid.MainActivity;
import com.evontech.passwordgridapp.custom.services.AuthActivity;
import com.evontech.passwordgridapp.custom.services.GridLockService;

import javax.inject.Singleton;

import dagger.BindsInstance;
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
    void inject(AccountsActivity accountsActivity);
    void inject(Registration registrationActivity);
    void inject(Login loginActivity);
    void inject(GridLockService app);
    void inject(AuthActivity authActivity);
}
