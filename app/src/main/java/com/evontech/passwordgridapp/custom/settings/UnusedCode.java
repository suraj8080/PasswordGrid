package com.evontech.passwordgridapp.custom.settings;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.models.UserAccount;

public class UnusedCode {
    /*private void addNewAccountDialog(){
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
                UserAccount userAccount = new UserAccount(et_account_name.getText().toString(), et_account_username.getText().toString(), et_account_url.getText().toString(),et_account_category.getText().toString());
                int userId = (int) mViewModel.updateUserAccount(userAccount);
                Log.d("userId ", ""+userId);
                if(userId>0) {
                    userAccount.setId(userId);
                    mUserAccounts.add(userAccount);
                    adapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                    loadingText.setVisibility(View.GONE);
                }
                else Toast.makeText(this, "Updating account failure ", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private int indexOf(String[] arr, String object){
        if (arr == null) return -1;
        int len = arr.length;
        int i = 0;
        while (i < len) {
            if (arr[i].equals(object)) return i;
            else i = i + 1;
        }
        return -1;
    }
}
