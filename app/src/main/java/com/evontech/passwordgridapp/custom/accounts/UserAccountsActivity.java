package com.evontech.passwordgridapp.custom.accounts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.models.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class UserAccountsActivity extends AppCompatActivity {

    private List<UserAccount> mUserAccounts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_accounts);
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_account);
        mUserAccounts.add(new UserAccount("Facebook", "sks321@gmail.com", "htttps:\\www.facebook.com"));
        AccountAdapter adapter = new AccountAdapter(mUserAccounts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}