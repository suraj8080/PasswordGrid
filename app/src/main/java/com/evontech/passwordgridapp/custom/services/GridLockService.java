package com.evontech.passwordgridapp.custom.services;

import android.app.assist.AssistStructure;
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

import com.evontech.passwordgridapp.R;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GridLockService extends AutofillService {
    ParsedStructure parsedStructure;
    @Override
    public void onFillRequest(@NonNull FillRequest request, @NonNull CancellationSignal cancellationSignal, @NonNull FillCallback callback) {
// Get the structure from the request
        List<FillContext> context = request.getFillContexts();
        AssistStructure structure = context.get(context.size() - 1).getStructure();

        // Traverse the structure looking for nodes to fill out.
        parsedStructure = new ParsedStructure();
        ParsedStructure parsedStructure = traverseStructure(structure);
        Log.d("structre class ", structure.getActivityComponent().getClassName());
        Log.d("structre package ", structure.getActivityComponent().getPackageName());
        Log.d("structre class ", structure.getActivityComponent().getShortClassName());

        // Fetch user data that matches the fields.
        UserData userData = fetchUserData(parsedStructure);

        // Build the presentation of the datasets
        RemoteViews usernamePresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
        usernamePresentation.setTextViewText(android.R.id.text1, "my_username");
        /*RemoteViews usernamePresentation = new RemoteViews(getPackageName(), R.layout.autofill_suggenstion);
        usernamePresentation.setTextViewText(R.id.suggestion_item, "my_username");*/
        RemoteViews passwordPresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
        passwordPresentation.setTextViewText(android.R.id.text1, "Password for my_username");

        // Add a dataset to the response
        if(parsedStructure==null)
            callback.onSuccess(null);
        else {
            Log.d("parsedStructure ", parsedStructure.usernameId.toString());
            FillResponse fillResponse = new FillResponse.Builder()
                    .addDataset(new Dataset.Builder()
                            .setValue(parsedStructure.usernameId,
                                    AutofillValue.forText("suraj@gmail.com"/*userData.username*/), usernamePresentation)
                            .setValue(parsedStructure.passwordId,
                                    AutofillValue.forText("sk@12345"/*userData.password*/), passwordPresentation)
                            .build())
                    .addDataset(new Dataset.Builder()
                            .setValue(parsedStructure.usernameId,
                                    AutofillValue.forText("priyanka@gmail.com"/*userData.username*/), usernamePresentation)
                            .setValue(parsedStructure.passwordId,
                                    AutofillValue.forText("priyanka@12345"/*userData.password*/), passwordPresentation)
                            .build())
                    .setSaveInfo(new SaveInfo.Builder(
                            SaveInfo.SAVE_DATA_TYPE_USERNAME | SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                            new AutofillId[] {parsedStructure.usernameId, parsedStructure.passwordId})
                            .build())
                    .build();

            // If there are no errors, call onSuccess() and pass the response
            callback.onSuccess(fillResponse);
        }
    }

    @Override
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {
        // Get the structure from the request
        List<FillContext> context = request.getFillContexts();
        AssistStructure structure = context.get(context.size() - 1).getStructure();
        parsedStructure = new ParsedStructure();
        // Traverse the structure looking for data to save
        parsedStructure = traverseStructure(structure);
        if(parsedStructure!=null)
            saveUserData(parsedStructure);
        // Persist the data, if there are no errors, call onSuccess()
        callback.onSuccess();
    }

    public static class ParsedStructure {
        AutofillId usernameId;
        AutofillId passwordId;
    }

    class UserData {
        String username;
        String password;
    }

    public ParsedStructure traverseStructure(AssistStructure structure) {
        int nodes = structure.getWindowNodeCount();

        for (int i = 0; i < nodes; i++) {
            AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
            AssistStructure.ViewNode viewNode = windowNode.getRootViewNode();
            traverseNode(viewNode);
        }
        if(parsedStructure.usernameId==null || parsedStructure.passwordId==null)
            return null;
        return parsedStructure;
    }

    public void traverseNode(AssistStructure.ViewNode viewNode) {
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
                if(!TextUtils.isEmpty(viewNode.getText().toString()))
                Log.d("usernName ", viewNode.getText().toString());
            }else if(viewId!=null && viewId.contains("password")) {
                parsedStructure.passwordId = viewNode.getAutofillId();
                Log.d("viewId ", viewId);
                if(!TextUtils.isEmpty(viewNode.getText().toString()))
                    Log.d("password ", viewNode.getText().toString());
            }
        }

        for(int i = 0; i < viewNode.getChildCount(); i++) {
            AssistStructure.ViewNode childNode = viewNode.getChildAt(i);
            traverseNode(childNode);
        }
    }

    private UserData fetchUserData(ParsedStructure structure){
        UserData userData = new UserData();
        return userData;
    }

    private void saveUserData(ParsedStructure structure){

    }
}
