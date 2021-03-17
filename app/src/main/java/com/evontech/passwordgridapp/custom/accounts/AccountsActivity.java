package com.evontech.passwordgridapp.custom.accounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.AppUser.Login;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.common.Util;
import com.evontech.passwordgridapp.custom.grid.GridActivity;
import com.evontech.passwordgridapp.custom.grid.GridViewModel;
import com.evontech.passwordgridapp.custom.models.UserAccount;
import com.evontech.passwordgridapp.custom.settings.Preferences;
import com.evontech.passwordgridapp.custom.settings.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountsActivity extends FullscreenActivity implements OnAccountClickListner {

    private List<UserAccount> mUserAccounts;
    @BindView(R.id.recycler_account)
    RecyclerView recyclerView;
    @BindView(R.id.addAccount)
    FloatingActionButton addAccount;
    @BindView(R.id.loadingText)
    TextView loadingText;
    @Inject
    Preferences mPreferences;
    @Inject ViewModelFactory mViewModelFactory;
    private AccountAdapter adapter;
    private AccountsViewModel mViewModel;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    @BindView(R.id.et_account_name)
    TextInputEditText et_account_name;
    @BindView(R.id.et_account_username)
    TextInputEditText et_account_username;
    @BindView(R.id.et_account_url)
    TextInputEditText et_account_url;
    @BindView(R.id.buttonAdd)
    Button buttonAdd;
    @BindView(R.id.buttonCancel)
    Button buttonCancel;

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_mobile)
    TextView tv_mobile;
    @BindView(R.id.buttonLogout)
    ImageView buttonLogout;

    BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_accounts);
        ButterKnife.bind(this);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                       // btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                       // btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_account_name.getText().toString())) et_account_name.setError("Enter Account Name");
                else if(TextUtils.isEmpty(et_account_username.getText().toString())) et_account_username.setError("Enter User Name");
                else if(TextUtils.isEmpty(et_account_url.getText().toString())) et_account_url.setError("Enter Account Url");
                else {
                    UserAccount userAccount = new UserAccount(et_account_name.getText().toString(), et_account_username.getText().toString(), et_account_url.getText().toString());
                    int userId = (int) mViewModel.updateUserAccount(userAccount);
                    Log.d("userId ", ""+userId);
                    if(userId>0) {
                        userAccount.setId(userId);
                        mUserAccounts.add(userAccount);
                        adapter.notifyDataSetChanged();
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        loadingText.setVisibility(View.GONE);
                    }
                    else Toast.makeText(AccountsActivity.this, "Updating account failure ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        setUpRecyclerView();
        addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addNewAccountDialog();
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                   // btnBottomSheet.setText("Close sheet");
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                   // btnBottomSheet.setText("Expand sheet");
                }
            }
        });

        tv_name.setText(getPreferences().getName());
        tv_mobile.setText(getPreferences().getMObile());
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPreferences().setLoginStatus(false);
                getPreferences().setName("");
                getPreferences().setMobile("");
                getPreferences().setUserId("");
                getPreferences().setUserName("");
                startActivity(new Intent(AccountsActivity.this, Login.class));
                finish();
            }
        });

        String userId = getPreferences().getUserId();
        Log.d("Logged In UserId ", userId);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AccountsViewModel.class);
        mViewModel.setUserId(Integer.parseInt(userId));
        mViewModel.getOnAccountState().observe(this, this::onAccountStateChanged);
    }

    private void onAccountStateChanged(AccountsViewModel.AccountState accountState) {
        if (accountState instanceof AccountsViewModel.Loading) {
            Log.d("accountState: ", "Loading...");
        }else if(accountState instanceof AccountsViewModel.Loaded){
            mUserAccounts.addAll(((AccountsViewModel.Loaded) accountState).accountList);
            adapter.notifyDataSetChanged();
            if(mUserAccounts.size()>0) loadingText.setVisibility(View.GONE);
            else sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            Log.d("accountState: ", "Loaded...");
            Log.d("mUserAccounts Size : ", ""+mUserAccounts.size());
        }
    }

    private void setUpRecyclerView(){
        mUserAccounts = new ArrayList<>();
        adapter = new AccountAdapter(mUserAccounts, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(mUserAccounts.size()>0) loadingText.setVisibility(View.GONE);
    }

    private void addNewAccountDialog(){
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
    }

    @Override
    public void onAccountSelected(int position) {
        UserAccount userAccount = mUserAccounts.get(position);
        Log.d("Account: ", userAccount.getAccountName());
        Log.d("Account Id: ", ""+userAccount.getId());
        Log.d("Account GridId: ", ""+userAccount.getAccountGridId());
        if(userAccount.getAccountGridId()<=0)
        setDefaultCriteria();
        startGrid(userAccount);
    }

    private void setDefaultCriteria(){
        //set default selection method here.

        //if(!mPreferences.userSelectedDirection()) { //default direction
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
           // }
        }
        //if(!mPreferences.userSelectedChosenOption()){
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
            //}
        }
        if(!mPreferences.showSpecialCharacters() && !mPreferences.showUpperCharacters() && !mPreferences.showLowerCharacters() && !mPreferences.showNumberCharacters() ){
            mPreferences.setSpecialCharacters(true);
            mPreferences.setUpperCharacters(true);
            mPreferences.setLowerCharacters(true);
            mPreferences.setNumberCharacters(true);
        }
        //if(mPreferences.getPasswordLength()<=0){
            mPreferences.setPasswordLength(14);
            mPreferences.setGridRow(14);
            mPreferences.setGridCol(14);
        //}
        if(mPreferences.showWordFromBorder() ||  mPreferences.selectedTypeManually()) mPreferences.setGridCol(26);
    }
    private void startGrid(UserAccount userAccount){
        Intent intent = new Intent(this, GridActivity.class);
        intent.putExtra(GridActivity.EXTRA_ROW_COUNT, mPreferences.getGridRow());
        intent.putExtra(GridActivity.EXTRA_COL_COUNT, mPreferences.getGridCol());
        Log.d("AccountGridId ", userAccount.getAccountGridId()+" ");
        if(userAccount.getAccountGridId()>0)  intent.putExtra(GridActivity.EXTRA_GRID_ID, userAccount.getAccountGridId());
        intent.putExtra("account", userAccount);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserAccounts.clear();
        mViewModel.loadAccounts();
    }
}