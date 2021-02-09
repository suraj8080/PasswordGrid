package com.evontech.passwordgridapp.custom.data.sqlite;

import android.provider.BaseColumns;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

abstract class DbContract {

    static class WordBank implements BaseColumns {
        static final String TABLE_NAME = "word_bank";

        static final String COL_STRING = "string";
    }

    static class GRID implements BaseColumns {
        static final String TABLE_NAME = "grid";

        static final String COL_NAME = "name";
        static final String COL_DURATION = "duration";
        static final String COL_GRID_ROW_COUNT = "grid_row_count";
        static final String COL_GRID_COL_COUNT = "grid_col_count";
        static final String COL_GRID_DATA = "grid_data";
    }

    static class UsedWord implements BaseColumns {
        static final String TABLE_NAME = "used_words";

        static final String COL_GRID_ID = "grid_round_id";
        static final String COL_WORD_STRING = "word_id";
        static final String COL_ANSWER_LINE_DATA = "answer_line_data";
        static final String COL_LINE_COLOR = "line_color";
        static final String COL_IS_MYSTERY = "mystery";
        static final String COL_REVEAL_COUNT = "reveal_count";
    }

    static class UserAccounts implements BaseColumns {
        static final String TABLE_NAME = "user_account";

        static final String COL_ACCOUNT_NAME = "account_name";
        static final String COL_ACCOUNT_USER_NAME = "user_name";
        static final String COL_ACCOUNT_URL = "account_url";
        static final String COL_ACCOUNT_GRID_ID = "account_grid_id";
    }
}
