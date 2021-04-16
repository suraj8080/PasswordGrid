package com.evontech.passwordgridapp.custom.services;

import android.app.ProgressDialog;
import android.app.assist.AssistStructure;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.AppUser.Login;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.UserLoginDataSource;
import com.evontech.passwordgridapp.custom.data.AccountDataSource;
import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.entity.GridDataMapper;
import com.evontech.passwordgridapp.custom.models.AppUser;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.UserAccount;
import com.evontech.passwordgridapp.custom.settings.Preferences;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE;
import static android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT;
import static com.evontech.passwordgridapp.custom.services.GridLockService.MY_EXTRA_DATASET_NAME;

public class AuthActivity extends FullscreenActivity {

    @Inject
    UserLoginDataSource userLoginDataSource;
    @Inject
    AccountDataSource accountDataSource;
    @Inject
    GridDataSource gridDataSource;
    @Inject
    Preferences mPreference;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    private ProgressDialog progressDialog;
    private GridLockService.ParsedStructure parsedStructure;
    private GridLockService.UserData userData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        parsedStructure = new GridLockService.ParsedStructure();
        GridLockService.UserData userData = null;
        progressDialog = new ProgressDialog(AuthActivity.this,
                R.style.AppTheme_Dark_Dialog);
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }



    private void login(){
        if(!validate()) {
        onLoginFailed();
        return;
        }
    _loginButton.setEnabled(false);

    String email = _emailText.getText().toString();
    String password = _passwordText.getText().toString();
    AppUser user = new AppUser();
    user.setUserName(email);
    user.setUserPassword(password);
    AppUser appUser = userLoginDataSource.userLogin(user);
    if(appUser!=null && appUser.getId()>0){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpAuthentication();
        }
    }else {
        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
        _loginButton.setEnabled(true);
    }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpAuthentication(){
        Intent intent = getIntent();

// The data sent by the service and the structure are included in the intent.
        String datasetName = intent.getStringExtra(MY_EXTRA_DATASET_NAME);
        AssistStructure structure = intent.getParcelableExtra(EXTRA_ASSIST_STRUCTURE);
        parsedStructure = traverseStructure(structure, "onFillRequest");
        userData = fetchUserData(structure);

        if(userData!=null)
        Log.d("userData username "+userData.username, " password "+userData.password);

// Build the presentation of the datasets.
        RemoteViews usernamePresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
        usernamePresentation.setTextViewText(android.R.id.text1, "my_username");
        RemoteViews passwordPresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
        passwordPresentation.setTextViewText(android.R.id.text1, "Password for my_username");

// Add the dataset to the response.
        FillResponse fillResponse = new FillResponse.Builder()
                .addDataset(new Dataset.Builder()
                        .setValue(parsedStructure != null ? parsedStructure.usernameId : null,
                                AutofillValue.forText(userData.username), usernamePresentation)
                        .setValue(parsedStructure != null ? parsedStructure.passwordId : null,
                                AutofillValue.forText(userData.password), passwordPresentation)
                        .build())
                .setSaveInfo(new SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_USERNAME | SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        new AutofillId[]{parsedStructure.usernameId, parsedStructure.passwordId})
                        .build())
                .build();

        Intent replyIntent = new Intent();

// Send the data back to the service.
        replyIntent.putExtra(MY_EXTRA_DATASET_NAME, datasetName);
        replyIntent.putExtra(EXTRA_AUTHENTICATION_RESULT, fillResponse);

        setResult(RESULT_OK, replyIntent);
        finish();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GridLockService.ParsedStructure traverseStructure(AssistStructure structure, String action) {
        int nodes = structure.getWindowNodeCount();

        for (int i = 0; i < nodes; i++) {
            AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
            AssistStructure.ViewNode viewNode = windowNode.getRootViewNode();
            traverseNode(viewNode, action);
        }
        if(parsedStructure.usernameId==null || parsedStructure.passwordId==null)
            return null;
        return parsedStructure;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void traverseNode(AssistStructure.ViewNode viewNode, String action) {
        if(viewNode.getAutofillHints() != null && viewNode.getAutofillHints().length > 0) {
            // If the client app provides autofill hints, you can obtain them using:
            // viewNode.getAutofillHints();
            String[] viewId = viewNode.getAutofillHints();
            if((viewId[0].contains("email")
                    || viewId[0].contains("username") || viewId[0].contains("mobile"))) {
                parsedStructure.usernameId = viewNode.getAutofillId();
                Log.d("viewId ", viewId[0]);
            }else if(viewId[0].contains("password")) {
                parsedStructure.passwordId = viewNode.getAutofillId();
                Log.d("viewId ", viewId[0]);
            }
        } else {
            // Or use your own heuristics to describe the contents of a view
            // using methods such as getText() or getHint().

            String viewId = viewNode.getIdEntry();
            if(viewId!=null && (viewId.contains("email")
                    || viewId.contains("username") || viewId.contains("mobile"))) {
                parsedStructure.usernameId = viewNode.getAutofillId();
                Log.d("viewId ", viewId);
                if(!TextUtils.isEmpty(viewNode.getText().toString())) {
                    Log.d("usernName ", viewNode.getText().toString());
                    userData.username = viewNode.getText().toString();
                }
            }else if(viewId!=null && viewId.contains("password")) {
                parsedStructure.passwordId = viewNode.getAutofillId();
                Log.d("viewId ", viewId);
                if(!TextUtils.isEmpty(viewNode.getText().toString())) {
                    Log.d("password ", viewNode.getText().toString());
                    userData.password = viewNode.getText().toString();
                }
            }
        }

        for(int i = 0; i < viewNode.getChildCount(); i++) {
            AssistStructure.ViewNode childNode = viewNode.getChildAt(i);
            traverseNode(childNode,action);
        }
    }

    public GridLockService.UserData fetchUserData(AssistStructure structure){
        int userId = 0;
        if(!TextUtils.isEmpty(mPreference.getUserId())) userId = Integer.parseInt(mPreference.getUserId());
        GridLockService.UserData userData = null;
        List<UserAccount> accountList = accountDataSource.getAllAccountData(userId);
        for (UserAccount account: accountList) {
            if (structure.getActivityComponent().getPackageName().contains(account.getAccountName())) {
                userData = new GridLockService.UserData();
                if(account.getAccountGridId()>0) {
                    gridDataSource.getGridData(account.getAccountGridId(), userId, gridRound -> {
                        GridData mCurrentGridData = new GridDataMapper().map(gridRound);
                        String pwd = "";
                        for (UsedWord word : mCurrentGridData.getUsedWords()) {
                            if (word.isAnswered()) pwd = pwd.concat(word.getString());
                        }
                        account.setAccountPwd(pwd);
                        Log.d("account name ", account.getAccountName());
                        Log.d("username " + account.getUserName(), " pwd " + pwd);
                    });
                }
                userData.username = account.getUserName();
                userData.password = account.getAccountPwd();
                return userData;
            }
        }
        return userData;
    }
}
