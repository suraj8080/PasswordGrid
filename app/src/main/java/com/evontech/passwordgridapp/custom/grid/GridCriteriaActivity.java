package com.evontech.passwordgridapp.custom.grid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import android.content.Intent;
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
import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.FullscreenActivity;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.common.Util;
import com.evontech.passwordgridapp.custom.settings.Preferences;

import java.util.Random;

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
    @BindView(R.id.etPassword)
    AppCompatEditText etPassword;
    @Inject
    Preferences mPreferences;
    private int changeInGridGeneration;
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

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s)) {
                    int pLength = Integer.parseInt(String.valueOf(s));
                    if(pLength<8) pLength = 8;
                    if(pLength>26) pLength = 26;
                    mPreferences.setPasswordLength(pLength);
                    mPreferences.setGridCol(pLength);
                    mPreferences.setGridRow(pLength);
                    Log.d("Editable ", String.valueOf(pLength));
                }else mPreferences.setPasswordLength(8);
            }
        });

        if(mPreferences.getPasswordLength()>0)
            etPassword.setText(String.valueOf(mPreferences.getPasswordLength()));
        btnGenerateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSettingRequest){
                    backPressed();
                }else {
                    if (mPreferences.getPasswordLength() < 8) {
                        Toast.makeText(GridCriteriaActivity.this, "Enter password length", Toast.LENGTH_SHORT).show();
                    } else if (mPreferences.getPasswordLength() < 8)
                        Toast.makeText(GridCriteriaActivity.this, "Password length cannot be less than 8", Toast.LENGTH_SHORT).show();
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
        checkBox_grid_direction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               // Log.d("buttonView ", buttonView.getText()+"");
                mPreferences.setGridDirection(isChecked);
                if(isChecked) {
                    mPreferences.setGridPattern(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setDragManually(false);
                    mPreferences.setStartEndGrid(false);
                    showDirectionDialog();
                }
                updateCheckBox();
            }
        });
        checkBox_pattern.setChecked(mPreferences.showGridPattern());
        checkBox_pattern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.d("buttonView ", buttonView.getText()+"");
                mPreferences.setGridPattern(isChecked);
                if(isChecked) {
                    mPreferences.setGridDirection(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setDragManually(false);
                    mPreferences.setStartEndGrid(false);
                }
                updateCheckBox();
            }
        });
        checkBox_word_from_border.setChecked(mPreferences.showWordFromBorder());
        checkBox_word_from_border.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.d("buttonView ", buttonView.getText()+"");
                mPreferences.setWordFromBorder(isChecked);
                if(isChecked) {
                    mPreferences.setGridPattern(false);
                    mPreferences.setGridDirection(false);
                    mPreferences.setDragManually(false);
                    mPreferences.setStartEndGrid(false);
                }
                updateCheckBox();
            }
        });

        checkBox_drag_manually.setChecked(mPreferences.selectedDragManually());
        checkBox_drag_manually.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               // Log.d("buttonView ", buttonView.getText()+"");
                mPreferences.setDragManually(isChecked);
                if(isChecked) {
                    mPreferences.setGridPattern(false);
                    mPreferences.setGridDirection(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setStartEndGrid(false);
                }
                updateCheckBox();
            }
        });
        checkBox_start_end_grid.setChecked(mPreferences.selectedStartEndGrid());
        checkBox_start_end_grid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.d("buttonView ", buttonView.getText()+"");
                mPreferences.setStartEndGrid(isChecked);
                if(isChecked) {
                    mPreferences.setGridPattern(false);
                    mPreferences.setGridDirection(false);
                    mPreferences.setWordFromBorder(false);
                    mPreferences.setDragManually(false);
                }
                updateCheckBox();
            }
        });

        setDefaultChosenOption();
    }

    private void setDefaultChosenOption(){
        //set default selection method here.
        if(!mPreferences.userSelectedChosenOption()){
            int randomSelectionOption = Util.getRandomIntRange(1,5);
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
            }

        }
    }

    private void updateCheckBox(){
        checkBox_word_from_border.setChecked(mPreferences.showWordFromBorder());
        checkBox_pattern.setChecked(mPreferences.showGridPattern());
        checkBox_grid_direction.setChecked(mPreferences.showgridDirection());
        checkBox_drag_manually.setChecked(mPreferences.selectedDragManually());
        checkBox_start_end_grid.setChecked(mPreferences.selectedStartEndGrid());
        mPreferences.setUserSelectedChosenOption(true);
        if(!mPreferences.showWordFromBorder() && !mPreferences.showGridPattern() && !mPreferences.showgridDirection() && !mPreferences.selectedDragManually() && !mPreferences.selectedStartEndGrid()){
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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
            }
        });
        AppCompatButton buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        if(mPreferences.showgridDirection() && !mPreferences.userSelectedDirection() && cbHorizontal!=null){
            ramdomiseDirection();
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            if(extras.getBoolean("settingRequest")) {
                btnGenerateGrid.setText("Back");
                isSettingRequest = true;
                if(mPreferences.showUpperCharacters()) changeInGridGeneration ++;
                if(mPreferences.showLowerCharacters()) changeInGridGeneration ++;
                if(mPreferences.showNumberCharacters()) changeInGridGeneration ++;
                if(mPreferences.showSpecialCharacters()) changeInGridGeneration ++;
                changeInGridGeneration = changeInGridGeneration + mPreferences.getPasswordLength();
            }
        }
    }

    private void startGrid(int length){
        Intent intent = new Intent(this, GridActivity.class);
        intent.putExtra(GridActivity.EXTRA_ROW_COUNT, mPreferences.getGridRow());
        intent.putExtra(GridActivity.EXTRA_COL_COUNT, mPreferences.getGridCol());
        if(mPreferences.showWordFromBorder()) intent.putExtra(GridActivity.EXTRA_COL_COUNT, 26);
        //intent.putExtra(GridActivity.EXTRA_GRID_ID, 619);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBackPressed() {
        if(isSettingRequest) backPressed();
        super.onBackPressed();
    }

    private void backPressed(){
            if(mPreferences.showUpperCharacters()) changeInGridGeneration --;
            if(mPreferences.showLowerCharacters()) changeInGridGeneration --;
            if(mPreferences.showNumberCharacters()) changeInGridGeneration --;
            if(mPreferences.showSpecialCharacters()) changeInGridGeneration --;
            changeInGridGeneration = changeInGridGeneration - mPreferences.getPasswordLength();
            Log.d("changeInGridGeneration ", ""+changeInGridGeneration);
            Intent intent = new Intent();
            if(changeInGridGeneration!=0) {
                Log.d("EXTRA_ROW_COUNT ", mPreferences.getGridRow()+"");
                Log.d("EXTRA_COL_COUNT ", mPreferences.getGridCol()+"");
                Log.d("passwordLength ", mPreferences.getPasswordLength()+"");
                intent.putExtra("changeInGridGeneration", true);
                intent.putExtra(GridActivity.EXTRA_ROW_COUNT, mPreferences.getGridRow());
                intent.putExtra(GridActivity.EXTRA_COL_COUNT, mPreferences.getGridCol());
                if(mPreferences.showWordFromBorder()) intent.putExtra(GridActivity.EXTRA_COL_COUNT, 26);
            }
            setResult(RESULT_OK,intent);
            finish();
    }
}