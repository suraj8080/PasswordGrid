package com.evontech.passwordgridapp.custom.accounts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.models.UserAccount;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<UserAccount> mAccounts;
    private OnAccountClickListner mAccountClickListner;
    public AccountAdapter(List<UserAccount> accounts, OnAccountClickListner listner) {
        mAccounts = accounts;
        mAccountClickListner = listner;
        Log.d("mAccounts ", ""+mAccounts.size() );
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.user_account_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserAccount userAccount = mAccounts.get(position);
        holder.account_name.setText(userAccount.getAccountName());
        holder.account_username.setText(userAccount.getUserName());
        holder.account_url.setText(userAccount.getAccountUrl());
        holder.linear_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountClickListner.onAccountSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView account_name, account_username, account_url;
        public LinearLayout linear_account;

        public ViewHolder(View itemView) {
            super(itemView);
            account_name = (TextView) itemView.findViewById(R.id.tv_account_name);
            account_username = (TextView) itemView.findViewById(R.id.tv_account_username);
            account_url = (TextView) itemView.findViewById(R.id.tv_account_url);
            linear_account = (LinearLayout) itemView.findViewById(R.id.linear_account);
        }
    }
}