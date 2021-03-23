package com.evontech.passwordgridapp.custom.AppUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.accounts.AccountsActivity;
import com.evontech.passwordgridapp.custom.grid.GridCriteriaActivity;
import com.evontech.passwordgridapp.custom.models.AppUser;
import com.evontech.passwordgridapp.custom.settings.ViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Registration extends FullscreenActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    @Inject ViewModelFactory mViewModelFactory;
    private LoginViewModel mViewModel;

    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(Registration.this,
                R.style.AppTheme_Dark_Dialog);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        mViewModel.getOnAccountState().observe(this, this::onAccountStateChanged);
    }

    private void onAccountStateChanged(LoginViewModel.LoginState loginState) {
        if(loginState instanceof LoginViewModel.Loading){
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();
        } else if (loginState instanceof LoginViewModel.RegistrationDone) {
            Log.d("accountState: ", "Registering...");
            progressDialog.dismiss();
            AppUser user = ((LoginViewModel.RegistrationDone) loginState).appUser;
            if(user.getId()<=0) onSignupFailed();
            else onSignupSuccess(user);
        }
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        String name = _nameText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        AppUser user = new AppUser();
        user.setName(name);
        user.setMobile(mobile);
        user.setUserName(email);
        user.setUserPassword(password);
        mViewModel.registerUser(user);
    }


    public void onSignupSuccess(AppUser user) {
        _signupButton.setEnabled(true);
        getPreferences().setLoginStatus(true);
        getPreferences().setName(user.getName());
        getPreferences().setMobile(user.getMobile());
        getPreferences().setUserId(String.valueOf(user.getId()));
        getPreferences().setUserName(user.getUserName());
        Toast.makeText(getBaseContext(), "Registration Sucessful", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Registration.this, GridCriteriaActivity.class);
        intent.putExtra("action", "onRegistration");
        startActivity(intent);
        finish();
        // set user logged in here
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if(mobile.isEmpty() || mobile.length()<10){
            _mobileText.setError("invalid mobile number");
        }else {
            _mobileText.setError(null);
        }

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
    }
}
