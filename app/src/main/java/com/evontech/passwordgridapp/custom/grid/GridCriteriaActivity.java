package com.evontech.passwordgridapp.custom.grid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.PasswordGridApp;
import com.evontech.passwordgridapp.custom.settings.Preferences;
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
    @BindView(R.id.btnGenerateGrid)
    Button btnGenerateGrid;
    @BindView(R.id.etPassword)
    AppCompatEditText etPassword;

    @Inject
    Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_criteria);
        ButterKnife.bind(this);
        ((PasswordGridApp) getApplication()).getAppComponent().inject(this);

        if(mPreferences.getGridCol()>0)
            etPassword.setText(String.valueOf(mPreferences.getGridCol()));
        btnGenerateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etPassword.getText().toString())) {
                    Toast.makeText(GridCriteriaActivity.this, "Enter password length", Toast.LENGTH_SHORT).show();
                }else if(Integer.parseInt(etPassword.getText().toString()) <8)
                    Toast.makeText(GridCriteriaActivity.this, "Password length cannot be less than 8", Toast.LENGTH_SHORT).show();
                else if(Integer.parseInt(etPassword.getText().toString()) >26)
                    Toast.makeText(GridCriteriaActivity.this, "Password length cannot be greater than 26", Toast.LENGTH_SHORT).show();
                else if(!mPreferences.showUpperCharacters() && !mPreferences.showLowerCharacters() && !mPreferences.showNumberCharacters() && !mPreferences.showSpecialCharacters())
                    Toast.makeText(GridCriteriaActivity.this, "Select atleast one checkbox", Toast.LENGTH_SHORT).show();
                else{
                    mPreferences.setPasswordLength(Integer.parseInt(etPassword.getText().toString()));
                    mPreferences.setGridCol(Integer.parseInt(etPassword.getText().toString()));
                    startGridPlay(Integer.parseInt(etPassword.getText().toString()));
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

        checkBox_grid_direction.setChecked(mPreferences.showgridDirection());
        checkBox_grid_direction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setGridDirection(isChecked);
                if(isChecked) {
                    mPreferences.setGridPattern(false);
                    mPreferences.setWordFromBorder(false);
                    updateCheckBox();
                }
            }
        });
        checkBox_pattern.setChecked(mPreferences.showGridPattern());
        checkBox_pattern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setGridPattern(isChecked);
                if(isChecked) {
                    mPreferences.setGridDirection(false);
                    mPreferences.setWordFromBorder(false);
                    updateCheckBox();
                }
            }
        });
        checkBox_word_from_border.setChecked(mPreferences.showWordFromBorder());
        checkBox_word_from_border.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPreferences.setWordFromBorder(isChecked);
                if(isChecked) {
                    mPreferences.setGridPattern(false);
                    mPreferences.setGridDirection(false);
                    updateCheckBox();
                }
            }
        });

    }

    private void updateCheckBox(){
        checkBox_word_from_border.setChecked(mPreferences.showWordFromBorder());
        checkBox_pattern.setChecked(mPreferences.showGridPattern());
        checkBox_grid_direction.setChecked(mPreferences.showgridDirection());
    }

    private void startGridPlay(int length){
        Intent intent = new Intent(this, GridActivity.class);
        intent.putExtra(GridActivity.EXTRA_ROW_COUNT, length);
        intent.putExtra(GridActivity.EXTRA_COL_COUNT, length);
        startActivity(intent);
    }
}