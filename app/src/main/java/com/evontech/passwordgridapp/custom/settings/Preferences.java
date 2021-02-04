package com.evontech.passwordgridapp.custom.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.evontech.passwordgridapp.R;
import com.evontech.passwordgridapp.custom.mcustom.StreakView;

import javax.inject.Inject;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class Preferences {
    private static String KEY_ENABLE_FULLSCREEN;
    private static String KEY_TYPE_PASSWORD;
    private static String KEY_TYPE_PIN;
    private static String KEY_SHOW_GRID_LINE;
    private static String KEY_UPPERCASE_LETTERS;
    private static String KEY_LOWERCASE_LETTERS;
    private static String KEY_SPECIAL_CHARACTERS;
    private static String KEY_NUMBERS;
    private static String KEY_GRID_DIRECTION;
    private static String KEY_DEFAULT_SELECTED_DIRECTION;
    private static String KEY_USER_SELECTED_DIRECTION;
    private static String KEY_GRID_PATTERN;
    private static String KEY_DRAG_MANUALLY;
    private static String KEY_START_END_GRID;
    private static String KEY_TYPE_MANUALLY;
    private static String KEY_WORD_FROM_BORDER;
    private static String KEY_USER_SELECTED_CHOSEN_OPTION;
    private static String KEY_ROW;
    private static String KEY_COL;
    private static String KEY_PASSWORD_LENGTH;
    private static String KEY_APPLY_WORD_PASSWORD;
    private static String KEY_APPLY_WORD_SELECTION;

    private SharedPreferences mPreferences;

    @Inject
    public Preferences(Context context, SharedPreferences preferences) {
        mPreferences = preferences;
        KEY_ENABLE_FULLSCREEN = context.getString(R.string.pref_enableFullscreen);
        KEY_TYPE_PASSWORD = context.getString(R.string.pref_typePassword);
        KEY_TYPE_PIN = context.getString(R.string.pref_typePin);
        KEY_SHOW_GRID_LINE = context.getString(R.string.pref_showGridLine);
        KEY_UPPERCASE_LETTERS = context.getString(R.string.pref_upperCaseLetters);
        KEY_LOWERCASE_LETTERS = context.getString(R.string.pref_lowerCaseLetters);
        KEY_SPECIAL_CHARACTERS = context.getString(R.string.pref_specialCharacters);
        KEY_NUMBERS = context.getString(R.string.pref_numbercharacters);
        KEY_GRID_DIRECTION = context.getString(R.string.pref_grid_direction);
        KEY_DEFAULT_SELECTED_DIRECTION = context.getString(R.string.pref_default_selected_direction);
        KEY_USER_SELECTED_DIRECTION = context.getString(R.string.pref_user_selected_direction);
        KEY_GRID_PATTERN = context.getString(R.string.pref_grid_pattern);
        KEY_WORD_FROM_BORDER = context.getString(R.string.pref_word_from_border);
        KEY_USER_SELECTED_CHOSEN_OPTION = context.getString(R.string.pref_user_selected_chosen_option);
        KEY_DRAG_MANUALLY = context.getString(R.string.pref_drag_manually);
        KEY_START_END_GRID = context.getString(R.string.pref_start_end_grid);
        KEY_TYPE_MANUALLY = context.getString(R.string.pref_type_manually);
        KEY_APPLY_WORD_PASSWORD = context.getString(R.string.pref_Apply_Word_Password);
        KEY_APPLY_WORD_SELECTION = context.getString(R.string.pref_Apply_Word_Status);

        KEY_ROW = context.getString(R.string.pref_row);
        KEY_COL = context.getString(R.string.pref_col);
        KEY_PASSWORD_LENGTH = context.getString(R.string.pref_password_length);
    }

    public void setApplyWordPassword(String word){
        mPreferences.edit()
                .putString(KEY_APPLY_WORD_PASSWORD, word)
                .apply();
    }

    public String getApplyWordPassword(){
        return mPreferences.getString(KEY_APPLY_WORD_PASSWORD, "");
    }

    public void setApplyWordStatus(boolean status){
        mPreferences.edit()
                .putBoolean(KEY_APPLY_WORD_SELECTION, status)
                .apply();
    }

    public boolean getApplyWordStatus(){
        return mPreferences.getBoolean(KEY_APPLY_WORD_SELECTION, false);
    }

    public boolean showUpperCharacters() {
        return mPreferences.getBoolean(KEY_UPPERCASE_LETTERS, false);
    }
    public void setUpperCharacters(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_UPPERCASE_LETTERS, status)
                .apply();
    }

    public boolean showLowerCharacters() {
        return mPreferences.getBoolean(KEY_LOWERCASE_LETTERS, false);
    }
    public void setLowerCharacters(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_LOWERCASE_LETTERS, status)
                .apply();
    }

    public boolean showSpecialCharacters() {
        return mPreferences.getBoolean(KEY_SPECIAL_CHARACTERS, false);
    }
    public void setSpecialCharacters(boolean status) {
        //Log.d("special character selection ", status+"");
        mPreferences.edit()
                .putBoolean(KEY_SPECIAL_CHARACTERS, status)
                .apply();
    }

    public boolean isPasswordSelected() {
        return mPreferences.getBoolean(KEY_TYPE_PASSWORD, false);
    }
    public void setPasswordSelection(boolean status) {
        //Log.d("setPasswordSelection ", status+"");
        mPreferences.edit()
                .putBoolean(KEY_TYPE_PASSWORD, status)
                .apply();
    }
    public boolean isPinSelected() {
        return mPreferences.getBoolean(KEY_TYPE_PIN, false);
    }
    public void setPinSelection(boolean status) {
        //Log.d("setPasswordSelection ", status+"");
        mPreferences.edit()
                .putBoolean(KEY_TYPE_PIN, status)
                .apply();
    }

    public boolean showNumberCharacters() {
        return mPreferences.getBoolean(KEY_NUMBERS, false);
    }
    public void setNumberCharacters(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_NUMBERS, status)
                .apply();
    }

    public boolean showgridDirection() {
        return mPreferences.getBoolean(KEY_GRID_DIRECTION, false);
    }
    public void setGridDirection(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_GRID_DIRECTION, status)
                .apply();
    }

    public String getSelectedDirection() {
        return mPreferences.getString(KEY_DEFAULT_SELECTED_DIRECTION, "");
    }
    public void selectDirection(String  direction) {
        mPreferences.edit()
                .putString(KEY_DEFAULT_SELECTED_DIRECTION, direction)
                .apply();
    }

    public boolean userSelectedDirection() {
        return mPreferences.getBoolean(KEY_USER_SELECTED_DIRECTION, false);
    }
    public void setUserSelectedDirection(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_USER_SELECTED_DIRECTION, status)
                .apply();
    }

    public boolean showGridPattern() {
        return mPreferences.getBoolean(KEY_GRID_PATTERN, false);
    }
    public void setGridPattern(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_GRID_PATTERN, status)
                .apply();
    }

    public boolean selectedStartEndGrid() {
        return mPreferences.getBoolean(KEY_START_END_GRID, false);
    }
    public void setStartEndGrid(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_START_END_GRID, status)
                .apply();
    }

    public boolean selectedTypeManually() {
        return mPreferences.getBoolean(KEY_TYPE_MANUALLY, false);
    }
    public void setTypeManually(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_TYPE_MANUALLY, status)
                .apply();
    }

    public boolean userSelectedChosenOption() {
        return mPreferences.getBoolean(KEY_USER_SELECTED_CHOSEN_OPTION, false);
    }
    public void setUserSelectedChosenOption(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_USER_SELECTED_CHOSEN_OPTION, status)
                .apply();
    }

    public boolean showWordFromBorder() {
        return mPreferences.getBoolean(KEY_WORD_FROM_BORDER, false);
    }
    public void setWordFromBorder(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_WORD_FROM_BORDER, status)
                .apply();
    }

    public boolean selectedDragManually() {
        return mPreferences.getBoolean(KEY_DRAG_MANUALLY, false);
    }
    public void setDragManually(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_DRAG_MANUALLY, status)
                .apply();
    }

    public int getGridRow() {
        return mPreferences.getInt(KEY_ROW, 0);
    }
    public void setGridRow(int row) {
        mPreferences.edit()
                .putInt(KEY_ROW, row)
                .apply();
    }

    public int getGridCol() {
        return mPreferences.getInt(KEY_COL, 0);
    }
    public void setGridCol(int col) {
        mPreferences.edit()
                .putInt(KEY_COL, col)
                .apply();
    }

    public int getPasswordLength() {
        return mPreferences.getInt(KEY_PASSWORD_LENGTH, 0);
    }
    public void setPasswordLength(int length) {
        mPreferences.edit()
                .putInt(KEY_PASSWORD_LENGTH, length)
                .apply();
    }

    public boolean showGridLine() {
        return mPreferences.getBoolean(KEY_SHOW_GRID_LINE, false);
    }
    public void setGridLine(boolean status) {
        mPreferences.edit()
                .putBoolean(KEY_SHOW_GRID_LINE, status)
                .apply();
    }

    public void resetSaveGameDataCount() {
        /*mPreferences.edit()
                .putInt(KEY_PREV_SAVE_GAME_DATA_COUNT, 0)
                .apply();*/
    }

    public boolean enableFullscreen() {
        return true;
        //return mPreferences.getBoolean(KEY_ENABLE_FULLSCREEN, false);
    }
}
