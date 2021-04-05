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

    /*adapter = new AccountAdapter(mUserAccounts, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));*/

   /* @Override
    public void onAccountSelected(int position) {
        UserAccount userAccount = mUserAccounts.get(position);
        Log.d("Account: ", userAccount.getAccountName());
        Log.d("Account Id: ", ""+userAccount.getId());
        Log.d("Account GridId: ", ""+userAccount.getAccountGridId());
        if(userAccount.getAccountGridId()<=0)
            setDefaultCriteria();
        startGrid(userAccount);
    }*/

        /*
        private void setDefaultCriteria(){
        //set default selection method here.

        if(!mPreferences.userSelectedDirection()) { //default direction
            int randomdirections = Util.getRandomIntRange(1,6);
            switch (randomdirections){
                case 1:
                    mPreferences.selectDirection("EAST");
                    break;
                case 2:
                    mPreferences.selectDirection("WEST");
                    break;
                case 3:
                    mPreferences.selectDirection("SOUTH");
                    break;
                case 4:
                    mPreferences.selectDirection("NORTH");
                    break;
                case 5:
                    mPreferences.selectDirection("SOUTH_EAST");
                    break;
                case 6:
                    mPreferences.selectDirection("NORTH_WEST");
                    break;
            }
        }
        if(!mPreferences.userSelectedChosenOption()){
            int randomSelectionOption = Util.getRandomIntRange(1,6);
            Log.d("randomSelectionOption ", ""+randomSelectionOption);
            switch (randomSelectionOption){
                case 1:
                    mPreferences.setDragManually(true);

                    mPreferences.setGridPattern(false);
                    mPreferences.setGridDirection(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setStartEndGrid(false);
                    mPreferences.setTypeManually(false);
                    break;
                case 2:
                    mPreferences.setStartEndGrid(true);

                    mPreferences.setGridPattern(false);
                    mPreferences.setGridDirection(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setDragManually(false);
                    mPreferences.setTypeManually(false);
                    break;
                case 3:
                    mPreferences.setGridDirection(true);

                    mPreferences.setGridPattern(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setDragManually(false);
                    mPreferences.setStartEndGrid(false);
                    mPreferences.setTypeManually(false);
                    break;
                case 4:
                    mPreferences.setGridPattern(true);

                    mPreferences.setGridDirection(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setDragManually(false);
                    mPreferences.setStartEndGrid(false);
                    mPreferences.setTypeManually(false);
                    break;
                case 5:
                    mPreferences.setWordFromBorder(true);

                    mPreferences.setGridPattern(false);
                    mPreferences.setGridDirection(false);
                    mPreferences.setDragManually(false);
                    mPreferences.setStartEndGrid(false);
                    mPreferences.setTypeManually(false);
                    break;
                case 6:
                    mPreferences.setTypeManually(true);

                    mPreferences.setGridPattern(false);
                    mPreferences.setGridDirection(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setDragManually(false);
                    mPreferences.setStartEndGrid(false);
                    break;
            }
        }
        if(!mPreferences.showSpecialCharacters() && !mPreferences.showUpperCharacters() && !mPreferences.showLowerCharacters() && !mPreferences.showNumberCharacters() ){
            mPreferences.setSpecialCharacters(true);
            mPreferences.setUpperCharacters(true);
            mPreferences.setLowerCharacters(true);
            mPreferences.setNumberCharacters(true);
        }
        if(mPreferences.isPasswordSelected() && mPreferences.getPasswordLength()<=0){
            mPreferences.setPasswordLength(14);
            mPreferences.setGridRow(14);
            mPreferences.setGridCol(14);
        }else if(mPreferences.isPinSelected() && mPreferences.getPinLength()<=0){
            mPreferences.setPinLength(6);
            mPreferences.setGridRow(6);
            mPreferences.setGridCol(6);
        }
        if(mPreferences.showWordFromBorder() ||  mPreferences.selectedTypeManually()) mPreferences.setGridCol(26);
    }
     */

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
