package com.evontech.passwordgridapp.custom.grid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.accounts.AccountsActivity;
import com.evontech.passwordgridapp.custom.common.Util;
import com.evontech.passwordgridapp.custom.settings.Preferences;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridCriteriaActivity extends AppCompatActivity {

    @BindView(R.id.cb_uppercaese)
    AppCompatCheckBox checkBox_uppercaese;
    @BindView(R.id.cb_lowercaese)
    AppCompatCheckBox checkBox_lowercaese;
    @BindView(R.id.cb_number)
    AppCompatCheckBox checkBox_number;
    @BindView(R.id.cb_special)
    AppCompatCheckBox checkBox_special;
    @BindView(R.id.cb_grid_direction)
    AppCompatCheckBox checkBox_grid_direction;
    @BindView(R.id.cb_pattern)
    AppCompatCheckBox checkBox_pattern;
    @BindView(R.id.cb_word_from_border)
    AppCompatCheckBox checkBox_word_from_border;
    @BindView(R.id.cb_draging_manually)
    AppCompatCheckBox checkBox_drag_manually;
    @BindView(R.id.cb_start_end_grid)
    AppCompatCheckBox checkBox_start_end_grid;
    @BindView(R.id.btnGenerateGrid)
    Button btnGenerateGrid;
    @BindView(R.id.cb_type_manually)
    AppCompatCheckBox checkBox_type_manually;
    @BindView(R.id.etPassword)
    TextInputEditText etPassword;
    @BindView(R.id.cb_password)
    AppCompatCheckBox checkBox_password;
    @BindView(R.id.cb_pin)
    AppCompatCheckBox checkBox_pin;
    @BindView(R.id.cb_apply_words)
    AppCompatCheckBox checkBoxApplyWords;
    @Inject
    Preferences mPreferences;
    private boolean isSettingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_criteria);
        ButterKnife.bind(this);
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);

        if(getIntent()!=null && getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("onRegistration")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            btnGenerateGrid.setText("Next");
        }
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void initCriteria(){
        checkBox_password.setChecked(mPreferences.isPasswordSelected());
        checkBox_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setPasswordSelection(isChecked);
                if(isChecked) {
                    passwordModeEnabled();
                    mPreferences.setPinSelection(false);
                    checkBox_pin.setChecked(false);
                }
            }
        });

        checkBox_pin.setChecked(mPreferences.isPinSelected());
        checkBox_pin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setPinSelection(isChecked);
                if(isChecked) {
                    pinModeEnabled();
                    mPreferences.setPasswordSelection(false);
                    checkBox_password.setChecked(false);
                }
            }
        });

        checkBoxApplyWords.setChecked(mPreferences.getApplyWordStatus());
        checkBoxApplyWords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setApplyWordStatus(isChecked);
                if(isChecked) {
                    checkBox_type_manually.setChecked(true);
                }
            }
        });

        if(!mPreferences.isPinSelected() && !mPreferences.isPasswordSelected()) checkBox_password.setChecked(true);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s)) {
                    int pLength = Integer.parseInt(String.valueOf(s));
                    if(pLength<8 && mPreferences.isPasswordSelected()) pLength = 8;
                    else if(pLength<4 && mPreferences.isPinSelected()) pLength = 4;
                    if(mPreferences.isPasswordSelected())
                    mPreferences.setPasswordLength(pLength);
                    else mPreferences.setPinLength(pLength);
                    if(pLength>26) pLength = 26;
                    //mPreferences.setPasswordLength(pLength);
                    //if(mPreferences.isPasswordSelected()) {
                        mPreferences.setGridCol(pLength);
                        mPreferences.setGridRow(pLength);
                    //}
                    Log.d("Editable ", String.valueOf(pLength));
                }else {
                    if(mPreferences.isPasswordSelected())
                        mPreferences.setPasswordLength(8);
                    else if(mPreferences.isPinSelected())
                        mPreferences.setPasswordLength(4);
                }
            }
        });

        Log.d("isPasswordSelected ", ""+mPreferences.isPasswordSelected());
        Log.d("isPinSelected ", ""+mPreferences.isPinSelected());

        Log.d("pinLength ", String.valueOf(mPreferences.getPinLength()));
        if(mPreferences.isPasswordSelected() && mPreferences.getPasswordLength()>0)
            etPassword.setText(String.valueOf(mPreferences.getPasswordLength()));
        else if(mPreferences.isPinSelected() && mPreferences.getPinLength()>0)
            etPassword.setText(String.valueOf(mPreferences.getPinLength()));
        btnGenerateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getIntent()!=null && getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("onRegistration")) {
                    startActivity(new Intent(GridCriteriaActivity.this, AccountsActivity.class));
                    finish();
                } else if(isSettingRequest){
                    backPressed();
                }else {
                    if (mPreferences.isPasswordSelected() && mPreferences.getPasswordLength() < 8) {
                        Toast.makeText(GridCriteriaActivity.this, "Password length cannot be less than 8", Toast.LENGTH_SHORT).show();
                    } else if (mPreferences.isPinSelected() && mPreferences.getPasswordLength() < 4)
                        Toast.makeText(GridCriteriaActivity.this, "Password length cannot be less than 4", Toast.LENGTH_SHORT).show();
                    else if (mPreferences.getPasswordLength() > 26)
                        Toast.makeText(GridCriteriaActivity.this, "Password length cannot be greater than 26", Toast.LENGTH_SHORT).show();
                    else if (!mPreferences.showUpperCharacters() && !mPreferences.showLowerCharacters() && !mPreferences.showNumberCharacters() && !mPreferences.showSpecialCharacters())
                        Toast.makeText(GridCriteriaActivity.this, "Select atleast one criteria", Toast.LENGTH_SHORT).show();
                    else {
                        //mPreferences.setPasswordLength(Integer.parseInt(etPassword.getText().toString()));
                        mPreferences.setGridCol(mPreferences.getPasswordLength());
                        mPreferences.setGridRow(mPreferences.getPasswordLength());
                        startGrid(mPreferences.getPasswordLength());
                    }
                }
            }
        });

        checkBox_special.setChecked(mPreferences.showSpecialCharacters());
        checkBox_special.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setSpecialCharacters(isChecked);
            }
        });
        checkBox_uppercaese.setChecked(mPreferences.showUpperCharacters());
        checkBox_uppercaese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setUpperCharacters(isChecked);
            }
        });
        checkBox_lowercaese.setChecked(mPreferences.showLowerCharacters());
        checkBox_lowercaese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setLowerCharacters(isChecked);
            }
        });
        checkBox_number.setChecked(mPreferences.showNumberCharacters());
        checkBox_number.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setNumberCharacters(isChecked);
            }
        });
        //set default selection method here.
        if(!mPreferences.showSpecialCharacters() && !mPreferences.showUpperCharacters() && !mPreferences.showLowerCharacters() && !mPreferences.showNumberCharacters() ){
            checkBox_special.setChecked(true);
            checkBox_uppercaese.setChecked(true);
            checkBox_lowercaese.setChecked(true);
            checkBox_number.setChecked(true);
        }
        if(mPreferences.getPasswordLength()<1){
            mPreferences.setPasswordLength(14);
            mPreferences.setGridCol(14);
            etPassword.setText(String.valueOf(mPreferences.getPasswordLength()));
        }


        checkBox_grid_direction.setChecked(mPreferences.showgridDirection());
        checkBox_grid_direction.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Log.d("buttonView ", buttonView.getText()+"");
            mPreferences.setGridDirection(isChecked);
            if(isChecked) {
                mPreferences.setGridPattern(false);
                mPreferences.setWordFromBorder(false);
                mPreferences.setDragManually(false);
                mPreferences.setStartEndGrid(false);
                mPreferences.setTypeManually(false);
                showDirectionDialog();
            }
            updateCheckBox();
        });
        checkBox_pattern.setChecked(mPreferences.showGridPattern());
        checkBox_pattern.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //Log.d("buttonView ", buttonView.getText()+"");
            mPreferences.setGridPattern(isChecked);
            if(isChecked) {
                mPreferences.setGridDirection(false);
                mPreferences.setWordFromBorder(false);
                mPreferences.setDragManually(false);
                mPreferences.setStartEndGrid(false);
                mPreferences.setTypeManually(false);
            }
            updateCheckBox();
        });
        checkBox_word_from_border.setChecked(mPreferences.showWordFromBorder());
        checkBox_word_from_border.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //Log.d("buttonView ", buttonView.getText()+"");
            mPreferences.setWordFromBorder(isChecked);
            if(isChecked) {
                mPreferences.setGridPattern(false);
                mPreferences.setGridDirection(false);
                mPreferences.setDragManually(false);
                mPreferences.setStartEndGrid(false);
                mPreferences.setTypeManually(false);
            }
            updateCheckBox();
        });

        checkBox_drag_manually.setChecked(mPreferences.selectedDragManually());
        checkBox_drag_manually.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Log.d("buttonView ", buttonView.getText()+"");
            mPreferences.setDragManually(isChecked);
            if(isChecked) {
                mPreferences.setGridPattern(false);
                mPreferences.setGridDirection(false);
                mPreferences.setWordFromBorder(false);
                mPreferences.setStartEndGrid(false);
                mPreferences.setTypeManually(false);
            }
            updateCheckBox();
        });
        checkBox_start_end_grid.setChecked(mPreferences.selectedStartEndGrid());
        checkBox_start_end_grid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //Log.d("buttonView ", buttonView.getText()+"");
            mPreferences.setStartEndGrid(isChecked);
            if(isChecked) {
                mPreferences.setGridPattern(false);
                mPreferences.setGridDirection(false);
                mPreferences.setWordFromBorder(false);
                mPreferences.setDragManually(false);
                mPreferences.setTypeManually(false);
            }
            updateCheckBox();
        });
        checkBox_type_manually.setChecked(mPreferences.selectedTypeManually());
        checkBox_type_manually.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPreferences.setTypeManually(isChecked);
            if(isChecked) {
                mPreferences.setGridPattern(false);
                mPreferences.setGridDirection(false);
                mPreferences.setWordFromBorder(false);
                mPreferences.setDragManually(false);
                mPreferences.setStartEndGrid(false);
            }
            updateCheckBox();
        });

        setDefaultChosenOption();
    }

    private void setDefaultChosenOption(){
        //set default selection method here.
        if(!mPreferences.userSelectedChosenOption()){
            int randomSelectionOption = Util.getRandomIntRange(1,6);
            switch (randomSelectionOption){
                case 1:
                    mPreferences.setUserSelectedChosenOption(false);
                    checkBox_drag_manually.setChecked(true);
                    // Log.d("onRandom ", mPreferences.userSelectedChosenOption()+"");
                    break;
                case 2:
                    mPreferences.setUserSelectedChosenOption(false);
                    checkBox_start_end_grid.setChecked(true);
                    // Log.d("onRandom ", mPreferences.userSelectedChosenOption()+"");
                    break;
                case 3:
                    mPreferences.setUserSelectedChosenOption(false);
                    checkBox_grid_direction.setChecked(true);
                    //Log.d("onRandom ", mPreferences.userSelectedChosenOption()+"");
                    break;
                case 4:
                    mPreferences.setUserSelectedChosenOption(false);
                    checkBox_pattern.setChecked(true);
                    //Log.d("onRandom ", mPreferences.userSelectedChosenOption()+"");
                    break;
                case 5:
                    mPreferences.setUserSelectedChosenOption(false);
                    checkBox_word_from_border.setChecked(true);
                    //Log.d("onRandom ", mPreferences.userSelectedChosenOption()+"");
                    break;
                case 6:
                    mPreferences.setUserSelectedChosenOption(false);
                    checkBox_type_manually.setChecked(true);
                    //Log.d("onRandom ", mPreferences.userSelectedChosenOption()+"");
                    break;
            }

        }
    }

    private void updateCheckBox(){
        checkBox_word_from_border.setChecked(mPreferences.showWordFromBorder());
        checkBox_pattern.setChecked(mPreferences.showGridPattern());
        checkBox_grid_direction.setChecked(mPreferences.showgridDirection());
        checkBox_drag_manually.setChecked(mPreferences.selectedDragManually());
        checkBox_start_end_grid.setChecked(mPreferences.selectedStartEndGrid());
        checkBox_type_manually.setChecked(mPreferences.selectedTypeManually());
        mPreferences.setUserSelectedChosenOption(true);
        if(!mPreferences.showWordFromBorder() && !mPreferences.showGridPattern() && !mPreferences.showgridDirection()
                && !mPreferences.selectedDragManually() && !mPreferences.selectedStartEndGrid() && !mPreferences.selectedTypeManually()){
            Log.d("unselected all ", "true");
            mPreferences.setUserSelectedChosenOption(false);
            setDefaultChosenOption();
        }

        //Log.d("onSelected ", mPreferences.userSelectedChosenOption()+"");

    }

    private RadioButton cbHorizontal;
    private RadioButton cbHorizontalReverse;
    private RadioButton cbVertical;
    private RadioButton cbVerticalReverse;
    private RadioButton cbDiagonal;
    private RadioButton cbDiagonalReverse;
    private void showDirectionDialog(){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.direction_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radio_group_direction);
        radioGroup.clearCheck();
         cbHorizontal = (RadioButton) radioGroup.findViewById(R.id.cbHorizontal);
         cbHorizontalReverse = (RadioButton) radioGroup.findViewById(R.id.cbHorizontalReverse);
         cbVertical = (RadioButton) radioGroup.findViewById(R.id.cbVertical);
         cbVerticalReverse = (RadioButton) radioGroup.findViewById(R.id.cbVerticalReverse);
         cbDiagonal = (RadioButton) radioGroup.findViewById(R.id.cbDiagonal);
         cbDiagonalReverse = (RadioButton) radioGroup.findViewById(R.id.cbDiagonalReverse);

         ramdomiseDirection();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mPreferences.setUserSelectedDirection(true);
            RadioButton rb = (RadioButton) group.findViewById(checkedId);
            //Log.d("checkId ", rb.getText().toString());
            if (checkedId==R.id.cbHorizontal)
               mPreferences.selectDirection("EAST");
            else if (checkedId==R.id.cbHorizontalReverse)
                mPreferences.selectDirection("WEST");
            else if (checkedId==R.id.cbVertical)
                mPreferences.selectDirection("SOUTH");
            else if (checkedId==R.id.cbVerticalReverse)
                mPreferences.selectDirection("NORTH");
            else if (checkedId==R.id.cbDiagonal)
                mPreferences.selectDirection("SOUTH_EAST");
            else if (checkedId==R.id.cbDiagonalReverse)
                mPreferences.selectDirection("NORTH_WEST");
            alertDialog.dismiss();
           // onDirectionSelection();
        });
        AppCompatButton buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void ramdomiseDirection(){
        if(!mPreferences.userSelectedDirection()) { //default direction
            int randomSelectionOption = Util.getRandomIntRange(1,6);
            switch (randomSelectionOption){
                case 1:
                    cbHorizontal.setChecked(true);
                    mPreferences.selectDirection("EAST");
                    mPreferences.setUserSelectedDirection(false);
                    break;
                case 2:
                    cbHorizontalReverse.setChecked(true);
                    mPreferences.selectDirection("WEST");
                    mPreferences.setUserSelectedDirection(false);
                    break;
                case 3:
                    cbVertical.setChecked(true);
                    mPreferences.selectDirection("SOUTH");
                    mPreferences.setUserSelectedDirection(false);
                    break;
                case 4:
                    cbVerticalReverse.setChecked(true);
                    mPreferences.selectDirection("NORTH");
                    mPreferences.setUserSelectedDirection(false);
                    break;
                case 5:
                    cbDiagonal.setChecked(true);
                    mPreferences.selectDirection("SOUTH_EAST");
                    mPreferences.setUserSelectedDirection(false);
                    break;
                case 6:
                    cbDiagonalReverse.setChecked(true);
                    mPreferences.selectDirection("NORTH_WEST");
                    mPreferences.setUserSelectedDirection(false);
                    break;
            }
        }else {
            String selectedDirection = mPreferences.getSelectedDirection();
            if (!TextUtils.isEmpty(selectedDirection)) {
                switch (selectedDirection) {
                    case "EAST":
                        cbHorizontal.setChecked(true);
                        break;
                    case "WEST":
                        cbHorizontalReverse.setChecked(true);
                        break;
                    case "SOUTH":
                        cbVertical.setChecked(true);
                        break;
                    case "NORTH":
                        cbVerticalReverse.setChecked(true);
                        break;
                    case "SOUTH_EAST":
                        cbDiagonal.setChecked(true);
                        break;
                    case "NORTH_WEST":
                        cbDiagonalReverse.setChecked(true);
                        break;
                }

            }
        }
    }

    private StringBuilder beforeMatcherString;
    private StringBuilder afterMatcherString;

    @Override
    protected void onResume() {
        super.onResume();
        initCriteria();
        if(mPreferences.showgridDirection() && !mPreferences.userSelectedDirection() && cbHorizontal!=null){
            ramdomiseDirection();
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            if(extras.getBoolean("settingRequest")) {
                beforeMatcherString = new StringBuilder();
                afterMatcherString = new StringBuilder();
                btnGenerateGrid.setText("Back");
                isSettingRequest = true;
                if(mPreferences.isPasswordSelected()) beforeMatcherString.append("isPasswordSelected");
                if(mPreferences.isPinSelected()) beforeMatcherString.append("isPinSelected");
                if(mPreferences.showUpperCharacters()) beforeMatcherString.append("showUpperCharacters");
                if(mPreferences.showLowerCharacters()) beforeMatcherString.append("showLowerCharacters");
                if(mPreferences.showNumberCharacters()) beforeMatcherString.append("showNumberCharacters");
                if(mPreferences.showSpecialCharacters()) beforeMatcherString.append("showSpecialCharacters");
                beforeMatcherString.append(mPreferences.getPasswordLength());
                beforeMatcherString.append(mPreferences.getPinLength());
                if(mPreferences.selectedDragManually()) beforeMatcherString.append("selectedDragManually");
                if(mPreferences.selectedStartEndGrid()) beforeMatcherString.append("selectedStartEndGrid");
                if(mPreferences.showgridDirection()) beforeMatcherString.append("showgridDirection");
                if(mPreferences.showGridPattern()) beforeMatcherString.append("showGridPattern");
                if(mPreferences.showWordFromBorder()) beforeMatcherString.append("showWordFromBorder");
                if(mPreferences.selectedTypeManually()) beforeMatcherString.append("selectedTypeManually");
            }
        }
    }

    private void startGrid(int length){
        Intent intent = new Intent(this, GridActivity.class);
        intent.putExtra(GridActivity.EXTRA_ROW_COUNT, mPreferences.getGridRow());
        intent.putExtra(GridActivity.EXTRA_COL_COUNT, mPreferences.getGridCol());
        if(mPreferences.showWordFromBorder() ||  mPreferences.selectedTypeManually()){
            mPreferences.setGridCol(26);
            intent.putExtra(GridActivity.EXTRA_COL_COUNT, 26);
        }
        //intent.putExtra(GridActivity.EXTRA_GRID_ID, 619);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void pinModeEnabled(){
        checkBox_uppercaese.setChecked(false);
        checkBox_lowercaese.setChecked(false);
        checkBox_special.setChecked(false);
        int pinLength = mPreferences.getPinLength();
        if(pinLength<=0) pinLength = 6;
        etPassword.setText(String.valueOf(pinLength));
        mPreferences.setGridCol(pinLength);
        mPreferences.setGridRow(pinLength);
        mPreferences.setPinLength(pinLength);
    }

    private void passwordModeEnabled(){
        checkBox_uppercaese.setChecked(true);
        checkBox_lowercaese.setChecked(true);
        checkBox_special.setChecked(true);
        etPassword.setText("14");
        mPreferences.setGridCol(14);
        mPreferences.setGridRow(14);
        mPreferences.setPasswordLength(14);
    }

    @Override
    public void onBackPressed() {
        if(isSettingRequest) backPressed();
        super.onBackPressed();
    }

    private void backPressed(){
        isSettingRequest = true;
        if(mPreferences.isPasswordSelected()) afterMatcherString.append("isPasswordSelected");
        if(mPreferences.isPinSelected()) afterMatcherString.append("isPinSelected");
        if(mPreferences.showUpperCharacters()) afterMatcherString.append("showUpperCharacters");
        if(mPreferences.showLowerCharacters()) afterMatcherString.append("showLowerCharacters");
        if(mPreferences.showNumberCharacters()) afterMatcherString.append("showNumberCharacters");
        if(mPreferences.showSpecialCharacters()) afterMatcherString.append("showSpecialCharacters");
        afterMatcherString.append(mPreferences.getPasswordLength());
        afterMatcherString.append(mPreferences.getPinLength());
        if(mPreferences.selectedDragManually()) afterMatcherString.append("selectedDragManually");
        if(mPreferences.selectedStartEndGrid()) afterMatcherString.append("selectedStartEndGrid");
        if(mPreferences.showgridDirection()) afterMatcherString.append("showgridDirection");
        if(mPreferences.showGridPattern()) afterMatcherString.append("showGridPattern");
        if(mPreferences.showWordFromBorder()) afterMatcherString.append("showWordFromBorder");
        if(mPreferences.selectedTypeManually()) afterMatcherString.append("selectedTypeManually");
        Log.d("beforeMatcherString ", beforeMatcherString.toString());
        Log.d("afterMatcherString ", afterMatcherString.toString());
            Intent intent = new Intent();
            if(!beforeMatcherString.toString().equals(afterMatcherString.toString())) {
              //  Log.d("EXTRA_ROW_COUNT ", mPreferences.getGridRow()+"");
               // Log.d("EXTRA_COL_COUNT ", mPreferences.getGridCol()+"");
               // Log.d("passwordLength ", mPreferences.getPasswordLength()+"");
                intent.putExtra("changeInGridGeneration", true);
                intent.putExtra(GridActivity.EXTRA_ROW_COUNT, mPreferences.getGridRow());
                intent.putExtra(GridActivity.EXTRA_COL_COUNT, mPreferences.getGridCol());
                if(mPreferences.showWordFromBorder() || mPreferences.selectedTypeManually()) {
                    mPreferences.setGridCol(26);
                    intent.putExtra(GridActivity.EXTRA_COL_COUNT, 26);
                }else {
                    mPreferences.setGridCol(mPreferences.getGridRow());
                    intent.putExtra(GridActivity.EXTRA_COL_COUNT, mPreferences.getGridRow());
                }
                /*intent.setClass(this, GridActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
            }//else {
                setResult(RESULT_OK, intent);
                finish();
           // }
    }
}