package com.evontech.passwordgridapp.custom.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.AppUser.Login;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.grid.GridActivity;
import com.evontech.passwordgridapp.custom.models.UserAccount;
import com.evontech.passwordgridapp.custom.settings.Preferences;
import com.evontech.passwordgridapp.custom.settings.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountsActivity extends FullscreenActivity {

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;
    @BindView(R.id.addAccount)
    FloatingActionButton addAccount;
    @BindView(R.id.loadingText)
    TextView loadingText;
    @Inject Preferences mPreferences;
    @Inject ViewModelFactory mViewModelFactory;
    private AccountsViewModel mViewModel;
    @BindView(R.id.iconFilter)
    ImageView filterIcon;
    @BindView(R.id.tv_filter)
    TextView tv_filter;
    @BindView(R.id.iconSearch)
    ImageView searchIcon;
    @BindView(R.id.et_search_accounts)
    AutoCompleteTextView et_search_accounts;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    @BindView(R.id.et_account_name)
    AutoCompleteTextView et_account_name;
    @BindView(R.id.et_account_username)
    AutoCompleteTextView et_account_username;
    @BindView(R.id.et_account_url)
    AutoCompleteTextView et_account_url;
    @BindView(R.id.et_category)
    AutoCompleteTextView et_account_category;
    @BindView(R.id.buttonAdd)
    Button buttonAdd;
    @BindView(R.id.buttonCancel)
    Button buttonCancel;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_mobile)
    TextView tv_mobile;
    @BindView(R.id.iconLogout)
    ImageView iconLogout;

    private BottomSheetBehavior sheetBehavior;
    private final ArrayList<String> search_accounts_array = new ArrayList<>();
    private final ArrayList<String> accounts_name_array = new ArrayList<>();
    private final ArrayList<String> accounts_url_array = new ArrayList<>();
    private final ArrayList<String> accounts_category_array = new ArrayList<>();
    private final ArrayList<String> accounts_username_array = new ArrayList<>();
    private List<UserAccount> mUserAccounts;
    private List<UserAccount> mTempUserAccounts;
    private int expandedPosition = -1;
    private String filterOption = "";
    private List<String> expandableListTitle = new ArrayList<>();
    private HashMap<String, List<UserAccount>> expandableListDetail = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_accounts);
        ButterKnife.bind(this);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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
                else if(TextUtils.isEmpty(et_account_category.getText().toString())) et_account_category.setError("Enter Account Category");
                else {
                    UserAccount userAccount = new UserAccount(et_account_name.getText().toString(), et_account_username.getText().toString(), et_account_url.getText().toString(),et_account_category.getText().toString());
                    int userId = (int) mViewModel.updateUserAccount(userAccount);
                    Log.d("userId ", ""+userId);
                    if(userId>0) {
                        userAccount.setId(userId);
                        mUserAccounts.add(userAccount);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        loadingText.setVisibility(View.GONE);
                        onAccountLoaded();
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

        setUpExpandableView();
        addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_account_name.setText("");
                et_account_username.setText("");
                et_account_url.setText("");
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
        String[] fName = getPreferences().getName().split(" ");
        tv_name.setText(fName[0]);
        tv_mobile.setText(getPreferences().getMObile());
        iconLogout.setOnClickListener(new View.OnClickListener() {
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

        et_account_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = accounts_name_array.indexOf(et_account_name.getText().toString());
                Log.d("index ", ""+index);
                et_account_url.setText(accounts_url_array.get(index));
                et_account_category.setText(accounts_category_array.get(index));
            }
        });
        et_account_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("textChanged ", ""+s);
                if(TextUtils.isEmpty(s) || !accounts_name_array.contains(String.valueOf(s))) {
                    et_account_url.setText("");
                    et_account_category.setText("");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(AccountsActivity.this, filterIcon);
                popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        tv_filter.setText(item.getTitle());
                        filterOption = item.getTitle().toString();
                        applySearchFilter();
                        //Toast.makeText(AccountsActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popup.show();
            }
        });

        et_search_accounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               applySearch(et_search_accounts.getText().toString().toLowerCase());
            }
        });
        et_search_accounts.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("textChanged ", ""+s);
                if(TextUtils.isEmpty(s)){
                    expandedPosition = -1;
                    expandableListView.setVisibility(View.VISIBLE);
                    mUserAccounts.clear();
                    mViewModel.loadAccounts();
                }else if(!search_accounts_array.contains(String.valueOf(s))) {
                    searchIcon.setImageDrawable(ContextCompat.getDrawable(AccountsActivity.this,R.drawable.ic_twotone_cancel_24));
                    expandableListView.setVisibility(View.GONE);
                    loadingText.setText("No record found.");
                    loadingText.setVisibility(View.VISIBLE);
                }else {
                    searchIcon.setImageDrawable(ContextCompat.getDrawable(AccountsActivity.this,R.drawable.ic_twotone_cancel_24));
                    loadingText.setVisibility(View.GONE);
                    expandableListView.setVisibility(View.VISIBLE);
                    applySearch(String.valueOf(s).toLowerCase());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search_accounts.setText("");
                searchIcon.setImageDrawable(ContextCompat.getDrawable(AccountsActivity.this,R.drawable.ic_baseline_search_24));
            }
        });
    }

    private void applySearch(String searchItem){
        Log.d("searchItem ", searchItem);
        List<UserAccount> tempUserAccounts = new ArrayList<>();
        for (UserAccount account:mTempUserAccounts){
            if(account.getAccountName().toLowerCase().contains(searchItem) || account.getAccountCategory().toLowerCase().contains(searchItem)) {
                tempUserAccounts.add(account);
            }
        }
        if(tempUserAccounts.size()>0) {
            Log.d("tempUserAccount ", tempUserAccounts.size()+"");
            mUserAccounts.clear();
            mUserAccounts.addAll(tempUserAccounts);
            expandedPosition = 0;
            applySearchFilter();
        }
    }

    private void applySearchFilter(){
        expandableListTitle.clear();
        expandableListDetail.clear();
        search_accounts_array.clear();
        List<String> headerList = new ArrayList<>();
        List<UserAccount> childList;
        for(int i=0;i<mUserAccounts.size();i++){
            if(!headerList.contains(mUserAccounts.get(i).getAccountCategory())) {
                childList = new ArrayList<>();
                headerList.add(mUserAccounts.get(i).getAccountCategory());
                childList.add(mUserAccounts.get(i));
                expandableListDetail.put(mUserAccounts.get(i).getAccountCategory(), childList);
            }else {
                childList = expandableListDetail.get(mUserAccounts.get(i).getAccountCategory());
                childList.add(mUserAccounts.get(i));
                expandableListDetail.put(mUserAccounts.get(i).getAccountCategory(), childList);
            }
            search_accounts_array.add(mUserAccounts.get(i).getAccountName().toLowerCase());
            search_accounts_array.add(mUserAccounts.get(i).getAccountCategory().toLowerCase());
        }
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, search_accounts_array.stream().distinct().collect(Collectors.toList()));
        et_search_accounts.setAdapter(searchAdapter);

        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        if(filterOption.equals("a-z"))
        Collections.sort(expandableListTitle);
        else if(filterOption.equals("z-a"))
        Collections.sort(expandableListTitle, Collections.reverseOrder());
        else {
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            //convert HashMap into List
            List<Map.Entry<String, List<UserAccount>>> list = new LinkedList<>(expandableListDetail.entrySet());
            //sorting the list elements
            Collections.sort(list, new Comparator<Map.Entry<String, List<UserAccount>>>()
            {
                public int compare(Map.Entry<String, List<UserAccount>> o1, Map.Entry<String, List<UserAccount>> o2)
                {
                    Integer obj1 = 0;
                    Integer obj2 = 0;
                    for (UserAccount  account: o1.getValue())
                        if(obj1<account.getId()) obj1 = account.getId();
                    for (UserAccount  account: o2.getValue())
                        if(obj2<account.getId()) obj2 = account.getId();

                        Log.d("obj1 "+obj1, " obj2 "+obj2);
                   return obj1.compareTo(obj2);

                    /*if (order) {//compare two object and return an integer
                        return o1.getValue().compareTo(o2.getValue());}
                    else {
                        return o2.getValue().compareTo(o1.getValue());
                    }*/
                }
            });
            expandableListDetail.clear();
            expandableListTitle.clear();
            for (Map.Entry<String, List<UserAccount>> entry : list) {
                Log.d("keys "+entry.getKey(), "value "+entry.getValue().toString());
                expandableListDetail.put(entry.getKey(), entry.getValue());
            }
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        }

        ExpandableListAdapter expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        if(expandedPosition>=0)
            expandableListView.expandGroup(expandedPosition);
    }

    private void onAccountStateChanged(AccountsViewModel.AccountState accountState) {
        if (accountState instanceof AccountsViewModel.Loading) {
            Log.d("accountState: ", "Loading...");
        }else if(accountState instanceof AccountsViewModel.Loaded){
            mTempUserAccounts.clear();
            mUserAccounts.addAll(((AccountsViewModel.Loaded) accountState).accountList);
            mTempUserAccounts.addAll(((AccountsViewModel.Loaded) accountState).accountList);
            if(mUserAccounts.size()>0) loadingText.setVisibility(View.GONE);
            else sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            Log.d("accountState: ", "Loaded...");
            Log.d("mUserAccounts Size : ", ""+mUserAccounts.size());
            if(TextUtils.isEmpty(et_search_accounts.getText().toString()))
            onAccountLoaded();
            else applySearch(et_search_accounts.getText().toString());
        }
    }

    private void onAccountLoaded(){
        applySearchFilter();
        accounts_name_array.clear();
        accounts_category_array.clear();
        accounts_url_array.clear();
        accounts_username_array.clear();
        String[] name_array = getResources().getStringArray(R.array.account_name_array);
        String[] url_array = getResources().getStringArray(R.array.account_url_array);
        String[] category_array = getResources().getStringArray(R.array.account_category_array);
        accounts_name_array.addAll(Arrays.asList(name_array));
        accounts_url_array.addAll(Arrays.asList(url_array));
        accounts_category_array.addAll(Arrays.asList(category_array));
        for(int i=0;i<mUserAccounts.size();i++){
            if(!accounts_name_array.contains(mUserAccounts.get(i).getAccountName())) {
                Log.d("adding account ", mUserAccounts.get(i).getAccountName());
                accounts_name_array.add(mUserAccounts.get(i).getAccountName());
                accounts_url_array.add(mUserAccounts.get(i).getAccountUrl());
                accounts_category_array.add(mUserAccounts.get(i).getAccountCategory());
            }
            if(!accounts_username_array.contains(mUserAccounts.get(i).getUserName()))
            accounts_username_array.add(mUserAccounts.get(i).getUserName());
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accounts_username_array);
        et_account_username.setAdapter(adapter1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accounts_name_array);
        et_account_name.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accounts_url_array.stream().distinct().collect(Collectors.toList()));
        et_account_url.setAdapter(adapter2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accounts_category_array.stream().distinct().collect(Collectors.toList()));
        et_account_category.setAdapter(adapter3);
    }

    private void setUpExpandableView(){
        expandableListDetail =  new HashMap<>();
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(), expandableListTitle.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(), expandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                expandedPosition = groupPosition;
                UserAccount userAccount = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
               // Toast.makeText(getApplicationContext(), expandableListTitle.get(groupPosition) + " -> " + userAccount.getAccountName(), Toast.LENGTH_SHORT).show();
                //if(userAccount.getAccountGridId()<=0) setDefaultCriteria();
                startGrid(userAccount);
                return false;
            }
        });
        mUserAccounts = new ArrayList<>();
        mTempUserAccounts = new ArrayList<>();
        if(mUserAccounts.size()>0) loadingText.setVisibility(View.GONE);
    }

    private void startGrid(UserAccount userAccount){
        Intent intent = new Intent(this, GridActivity.class);
        intent.putExtra(GridActivity.EXTRA_ROW_COUNT, mPreferences.getGridRow());
        intent.putExtra(GridActivity.EXTRA_COL_COUNT, mPreferences.getGridCol());
        Log.d("AccountGridId ", userAccount.getAccountGridId()+" ");
        if(userAccount.getAccountGridId()>0)  intent.putExtra(GridActivity.EXTRA_GRID_ID, userAccount.getAccountGridId());
        intent.putExtra("account", userAccount);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
            mUserAccounts.clear();
            mTempUserAccounts.clear();
            mViewModel.loadAccounts();
    }
}