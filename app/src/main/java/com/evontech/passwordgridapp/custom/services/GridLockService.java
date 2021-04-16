package com.evontech.passwordgridapp.custom.services;

import android.app.PendingIntent;
import android.app.assist.AssistStructure;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.text.TextUtils;
import android.util.Log;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.accounts.AccountsViewModel;
import com.evontech.passwordgridapp.custom.data.AccountDataSource;
import com.evontech.passwordgridapp.custom.data.GridDataSource;
import com.evontech.passwordgridapp.custom.data.entity.GridDataMapper;
import com.evontech.passwordgridapp.custom.grid.GridDataCreator;
import com.evontech.passwordgridapp.custom.grid.GridViewModel;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.UserAccount;
import com.evontech.passwordgridapp.custom.models.Word;
import com.evontech.passwordgridapp.custom.settings.Preferences;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GridLockService extends AutofillService {
    private ParsedStructure parsedStructure;
    private UserData userData;

    @Inject
    AccountDataSource accountDataSource;
    @Inject
    GridDataSource gridDataSource;
    @Inject
    Preferences mPreference;
    public static final String MY_EXTRA_DATASET_NAME = "my_extra_dataset_name";
    private final AutofillId[] autofillIds = new AutofillId[2];

    @Override
    public void onCreate() {
        super.onCreate();
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);
        Log.d("onCreate ", "called");
        Log.d("login status ", mPreference.getLoginStatus()+"");
    }

    @Override
    public void onFillRequest(@NonNull FillRequest request, @NonNull CancellationSignal cancellationSignal, @NonNull FillCallback callback) {
    // Get the structure from the request
        List<FillContext> context = request.getFillContexts();
        AssistStructure structure = context.get(context.size() - 1).getStructure();

        // Traverse the structure looking for nodes to fill out.
        parsedStructure = new ParsedStructure();
        UserData userData = null;
        ParsedStructure parsedStructure = traverseStructure(structure, "onFillRequest");
        if(!structure.getActivityComponent().getPackageName().equals(getPackageName())) {
            Log.d("structre class ", structure.getActivityComponent().getClassName());
            Log.d("structre package ", structure.getActivityComponent().getPackageName());
            Log.d("app package ", getPackageName());

            // Fetch user data that matches the fields.
            if (parsedStructure != null)
                userData = fetchUserData(structure);

            // Build the presentation of the datasets
            RemoteViews usernamePresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
            RemoteViews passwordPresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);


            RemoteViews authPresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
            authPresentation.setTextViewText(android.R.id.text1, "requires authentication");
            Intent authIntent = new Intent(this, AuthActivity.class);

// Send any additional data required to complete the request.
            authIntent.putExtra(MY_EXTRA_DATASET_NAME, "my_dataset");
            authIntent.putExtra(EXTRA_ASSIST_STRUCTURE, structure);
            IntentSender intentSender = PendingIntent.getActivity(
                    this,
                    1001,
                    authIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            ).getIntentSender();

            // Add a dataset to the response
            if (userData == null && parsedStructure != null) {
                FillResponse fillResponse = new FillResponse.Builder()
                        .addDataset(new Dataset.Builder()
                                .setValue(parsedStructure.usernameId,
                                        null, usernamePresentation)
                                .setValue(parsedStructure.passwordId,
                                        null, passwordPresentation)
                                .build())
                        .setSaveInfo(new SaveInfo.Builder(
                                SaveInfo.SAVE_DATA_TYPE_USERNAME | SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                                new AutofillId[]{parsedStructure.usernameId, parsedStructure.passwordId})
                                .build())
                        .build();
                callback.onSuccess(fillResponse);
            } else if (userData != null) {
                usernamePresentation.setTextViewText(android.R.id.text1, "my_username");
                passwordPresentation.setTextViewText(android.R.id.text1, "Password for my_username");
                autofillIds[0] = parsedStructure.usernameId;
                autofillIds[1] = parsedStructure.passwordId;
                Log.d("parsedStructure u ", parsedStructure.usernameId.toString());
                Log.d("parsedStructure p ", parsedStructure.passwordId.toString());
                FillResponse fillResponse = new FillResponse.Builder()
                        .setAuthentication(autofillIds, intentSender, authPresentation)
                        .addDataset(new Dataset.Builder()
                                .setValue(parsedStructure.usernameId,
                                        AutofillValue.forText(userData.username), usernamePresentation)
                                .setValue(parsedStructure.passwordId,
                                        AutofillValue.forText(userData.password), passwordPresentation)
                                .build())
                        /*.addDataset(new Dataset.Builder()
                                .setValue(parsedStructure.usernameId,
                                        AutofillValue.forText("priyanka@gmail.com"*//*userData.username*//*), usernamePresentation)
                            .setValue(parsedStructure.passwordId,
                                    AutofillValue.forText("priyanka@12345"*//*userData.password*//*), passwordPresentation)
                            .build())*/
                        .setSaveInfo(new SaveInfo.Builder(
                                SaveInfo.SAVE_DATA_TYPE_USERNAME | SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                                new AutofillId[]{parsedStructure.usernameId, parsedStructure.passwordId})
                                .build())
                        .build();

                // If there are no errors, call onSuccess() and pass the response
                callback.onSuccess(fillResponse);
            } else {
                Log.d("parsedStructure ", "else part");
                callback.onSuccess(null);
            }
        }
    }

    @Override
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {
        Log.d("onSaveRequest ", "called");
        // Get the structure from the request
        List<FillContext> context = request.getFillContexts();
        AssistStructure structure = context.get(context.size() - 1).getStructure();
        parsedStructure = new ParsedStructure();
        // Traverse the structure looking for data to save
        userData = new UserData();
        parsedStructure = traverseStructure(structure, "onSaveRequest");
        if(parsedStructure!=null)
            saveUserData(structure);
        // Persist the data, if there are no errors, call onSuccess()
        callback.onSuccess();
    }

    public static class ParsedStructure {
        AutofillId usernameId;
        AutofillId passwordId;
    }

    static class UserData {
        String username;
        String password;
    }

    public ParsedStructure traverseStructure(AssistStructure structure, String action) {
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

    public UserData fetchUserData(AssistStructure structure){
        int userId = 0;
        if(!TextUtils.isEmpty(mPreference.getUserId())) userId = Integer.parseInt(mPreference.getUserId());
        UserData userData = null;
        List<UserAccount> accountList = accountDataSource.getAllAccountData(userId);
        for (UserAccount account: accountList) {
            if (structure.getActivityComponent().getPackageName().contains(account.getAccountName())) {
                userData = new UserData();
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
// loginsampe, suraj@gmail.com, H2=IPd7#qWeVy6
    private void saveUserData(AssistStructure structure){
        boolean isAccountMatched = false;
        int userId = Integer.parseInt(mPreference.getUserId());
        List<UserAccount> accountList = accountDataSource.getAllAccountData(userId);
        for (UserAccount account: accountList) {
            if (structure.getActivityComponent().getPackageName().contains(account.getAccountName())) {
                isAccountMatched = true;
                account.setUserName(userData.username);
                account.setAccountUpdatedPwd(userData.password);
                accountDataSource.saveAccountData(account);
                /*if(account.getAccountGridId()>0) {
                    gridDataSource.getGridData(account.getAccountGridId(), userId, gridRound -> {
                        GridData mCurrentGridData = new GridDataMapper().map(gridRound);
                        String pwd = "";
                        for (UsedWord word : mCurrentGridData.getUsedWords()) {
                            if (word.isAnswered()) pwd = pwd.concat(word.getString());
                        }
                        Log.d("account name "+ account.getAccountName(), " username "+account.getUserName());
                        Log.d("autofill pwd " + account.getAccountPwd(), " stored grid pwd " + pwd);
                        if(!account.getAccountPwd().equals(pwd)){
                            gridDataSource.deleteAllLines(mCurrentGridData.getId(), userId);
                            mCurrentGridData.setUpdatedPassword(account.getAccountPwd());
                            gridDataSource.saveGridData(new GridDataMapper().revMap(mCurrentGridData),userId);
                        }
                    });
                }*/
            }
        }
        if(!isAccountMatched) {
            UserAccount account = new UserAccount();
            String[] packageName = structure.getActivityComponent().getPackageName().split("\\.");
            account.setAccountName(packageName[packageName.length-1]);
            account.setAccountCategory(packageName[packageName.length-1]);
            account.setUserId(userId);
            account.setUserName(userData.username);
            account.setAccountUpdatedPwd(userData.password);
            accountDataSource.saveAccountData(account);
        }
    }

}
