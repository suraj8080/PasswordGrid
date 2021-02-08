package com.evontech.passwordgridapp.custom.accounts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.grid.GridViewModel;
import com.evontech.passwordgridapp.custom.models.UserAccount;
import com.evontech.passwordgridapp.custom.settings.Preferences;
import com.evontech.passwordgridapp.custom.settings.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountsActivity extends FullscreenActivity {

    private List<UserAccount> mUserAccounts;
    @BindView(R.id.recycler_account)
    RecyclerView recyclerView;
    @BindView(R.id.addAccount)
    FloatingActionButton addAccount;
    @BindView(R.id.loadingText)
    TextView loadingText;
    @Inject
    Preferences mPreferences;
    @Inject
    ViewModelFactory mViewModelFactory;
    private AccountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_accounts);
        ButterKnife.bind(this);

        setUpRecyclerView();
        addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewAccount();
            }
        });
    }

    private void setUpRecyclerView(){
        mUserAccounts = new ArrayList<>();
        adapter = new AccountAdapter(mUserAccounts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(mUserAccounts.size()>0) loadingText.setVisibility(View.GONE);
    }

    private void addNewAccount(){
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.add_new_account, viewGroup, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            EditText et_account_name = dialogView.findViewById(R.id.et_account_name);
            EditText et_account_username = dialogView.findViewById(R.id.et_account_username);
            EditText et_account_url = dialogView.findViewById(R.id.et_account_url);
            AppCompatButton buttonCancel = dialogView.findViewById(R.id.buttonCancel);
            buttonCancel.setOnClickListener(v -> alertDialog.dismiss());
            AppCompatButton buttonAdd = dialogView.findViewById(R.id.buttonAdd);
            buttonAdd.setOnClickListener(v -> {
                if(TextUtils.isEmpty(et_account_name.getText().toString())) et_account_name.setError("Enter Account Name");
                else if(TextUtils.isEmpty(et_account_username.getText().toString())) et_account_username.setError("Enter User Name");
                else if(TextUtils.isEmpty(et_account_url.getText().toString())) et_account_url.setError("Enter Account Url");
                else {
                    UserAccount userAccount = new UserAccount(et_account_name.getText().toString(), et_account_username.getText().toString(), et_account_url.getText().toString());
                    mUserAccounts.add(userAccount);

                    adapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                    loadingText.setVisibility(View.GONE);
                }
            });
    }
}