package com.evontech.passwordgridapp.custom.AppUser;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.accounts.AccountsActivity;
import com.evontech.passwordgridapp.custom.models.AppUser;
import com.evontech.passwordgridapp.custom.services.GridLockService;
import com.evontech.passwordgridapp.custom.settings.ViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends FullscreenActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    @Inject
    ViewModelFactory mViewModelFactory;
    private LoginViewModel mViewModel;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(Login.this,
                R.style.AppTheme_Dark_Dialog);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        Log.d("LoginStatus ", getPreferences().getLoginStatus() +"");
        if(getPreferences().getLoginStatus()) {
            finish();
            startActivity(new Intent(Login.this, AccountsActivity.class));
        }else {
            mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
            mViewModel.resetLiveData();
            mViewModel.getOnAccountState().observe(this, this::onAccountStateChanged);
        }
    }

    private static final int REQUEST_CODE_SET_DEFAULT = 10;
    @TargetApi(Build.VERSION_CODES.O)
    private void checkAutofillService(){
        AutofillManager mAutofillManager = getSystemService(AutofillManager.class);
        if (mAutofillManager != null && !mAutofillManager.hasEnabledAutofillServices()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE);
            intent.setData(Uri.parse("package:com.example.android"));
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT);
        }
    }

    private void onAccountStateChanged(LoginViewModel.LoginState loginState) {
        if(loginState instanceof LoginViewModel.Loading){
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loging Account...");
            progressDialog.show();
        } else if(loginState instanceof LoginViewModel.LoginDone){
            progressDialog.dismiss();
            AppUser user = ((LoginViewModel.LoginDone) loginState).appUser;
            if(user==null) onLoginFailed();
            else onLoginSuccess(user);
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);


        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        AppUser user = new AppUser();
        user.setUserName(email);
        user.setUserPassword(password);
       mViewModel.loginUser(user);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(AppUser user) {
        _loginButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "Welcome "+user.getName(), Toast.LENGTH_LONG).show();
        Log.d("User Id "+user.getId() ," user password "+user.getUserPassword());
        Log.d("User name "+user.getUserName() ," user mobile "+user.getMobile());
        startActivity(new Intent(Login.this, AccountsActivity.class));
        getPreferences().setLoginStatus(true);
        getPreferences().setName(user.getName());
        getPreferences().setMobile(user.getMobile());
        getPreferences().setUserId(String.valueOf(user.getId()));
        getPreferences().setUserName(user.getUserName());
        finish();
        // set user logged in here
    }



    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!getPreferences().getLoginStatus())
        checkAutofillService();
    }
}