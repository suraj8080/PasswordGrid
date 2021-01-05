package com.evontech.passwordgridapp.custom.data;


import com.evontech.passwordgridapp.custom.models.Word;

import java.util.List;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

public interface WordDataSource {

    List<Word> getWords();

}
