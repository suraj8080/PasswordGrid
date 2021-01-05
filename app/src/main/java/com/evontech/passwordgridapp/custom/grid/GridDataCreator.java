package com.evontech.passwordgridapp.custom.grid;

import android.util.Log;

import com.evontech.passwordgridapp.custom.common.Util;
import com.evontech.passwordgridapp.custom.common.generator.StringListGridGenerator;
import com.evontech.passwordgridapp.custom.models.GridData;
import com.evontech.passwordgridapp.custom.models.Grid;
import com.evontech.passwordgridapp.custom.models.UsedWord;
import com.evontech.passwordgridapp.custom.models.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.evontech.passwordgridapp.custom.common.Util.getRandomIntRange;


/**
 * Created by Suraj Kumar on 17/12/20.
 */

public class GridDataCreator {
    public static String type;
    public static boolean isUpperCase;
    public static boolean isLowerCase;
    public static boolean isNumbers;
    public static boolean isSpecialCharacters;

    public static void setGridGenerationCriteria(boolean isUpperCase, boolean isLowerCase, boolean isNumbers, boolean isSpecialCharacters){
        GridDataCreator.isUpperCase = isUpperCase;
        GridDataCreator.isLowerCase = isLowerCase;
        GridDataCreator.isNumbers = isNumbers;
        GridDataCreator.isSpecialCharacters = isSpecialCharacters;
    }

    public GridData newGridData(final List<Word> words,
                                final int rowCount, final int colCount,
                                final String name) {
        type = name;
        final GridData gridData = new GridData();

        //if(type.equals("Play me"))
        Util.randomizeList(words);

        Grid grid = new Grid(rowCount, colCount);
        int maxCharCount = Math.min(rowCount, colCount);

        List<String> usedStrings =
                new StringListGridGenerator()
                        .setGrid(getStringListFromWord(words, 100, maxCharCount), grid.getArray());

        Log.d("GridDataCreator final usedStrings ", usedStrings.toString());

        gridData.addUsedWords(buildUsedWordFromString(usedStrings));
        gridData.setGrid(grid);
        if (name == null || name.isEmpty()) {
            String name1 = "Puzzle " +
                    new SimpleDateFormat("HH.mm.ss", Locale.ENGLISH)
                            .format(new Date(System.currentTimeMillis()));
            gridData.setName(name1);
        }
        else {
            gridData.setName(name);
        }
        return gridData;
    }

    private List<UsedWord> buildUsedWordFromString(List<String> strings) {
        int mysteryWordCount = getRandomIntRange(strings.size() / 2, strings.size());
        List<UsedWord> usedWords = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);

            UsedWord uw = new UsedWord();
            uw.setString(str);
            uw.setAnswered(false);
            if (mysteryWordCount > 0) {
                uw.setIsMystery(true);
                uw.setRevealCount(getRandomIntRange(0, str.length() - 1));
                mysteryWordCount--;
            }

            usedWords.add(uw);
        }

        Util.randomizeList(usedWords);
        return usedWords;
    }

    private List<String> getStringListFromWord(List<Word> words, int count, int maxCharCount) {
        count = Math.min(count, words.size());
        List<String> stringList = new ArrayList<>();
        String temp;

        if(type.equals("Play me")) {
            Log.d("count "+count, " maxCharCount "+maxCharCount +" word size "+words.size());
            /*if (isUpperCase && isLowerCase) {
                int randomCase = getRandomIntRange(1,2);
                if(randomCase==1)
                    stringList.addAll(getRandomUpperCaseWords(maxCharCount));
                else
                    stringList.addAll(getRandomLowercaseWords(maxCharCount));
            }else {
                if (isUpperCase)
                    stringList.addAll(getRandomUpperCaseWords(maxCharCount));
                else stringList.addAll(getRandomLowercaseWords(maxCharCount));
            }
            if (isSpecialCharacters)
                stringList.addAll(getRandomSpecialWords(maxCharCount));
            if (isNumbers)
                stringList.addAll(getRandomNumberWords(maxCharCount));*/

        //if(isUpperCase || isLowerCase){
        for (int i = 0; i < words.size(); i++) {
            if (stringList.size() >= count) break;

            //temp = words.get(i).getString();  //get random words with criteria set by user using a single method
            temp = getRandomWords(maxCharCount-1);
            if (temp.length() <= maxCharCount) {
                stringList.add(temp);
                /*if(isUpperCase && isLowerCase){
                    int randomCase = getRandomIntRange(1,2);
                    if(randomCase==1)
                        stringList.add(temp.toUpperCase());
                    else stringList.add(temp.toLowerCase());
                }else {
                    if (isUpperCase)
                        stringList.add(temp.toUpperCase());
                    else stringList.add(temp.toLowerCase());
                }*/
            }
        }
          //  }
            Log.d("GridDataCreator stringList to attempts ", stringList.toString());
        }
        return stringList;
    }

    public static String getRandomWords(int maxCharCount){
        StringBuilder mString = new StringBuilder();
            int randomCharCount = maxCharCount-1;//getRandomIntRange(1, maxCharCount-1);
            int randomChar = 0;
            if(isUpperCase) randomChar ++;
            if(isLowerCase) randomChar ++;
            if(isNumbers) randomChar ++;
            if(isSpecialCharacters) randomChar ++;
            for (int index = 0; index < randomCharCount; index++) {
                int characterCase =  getRandomIntRange(1, randomChar);
                //Log.d("characterCase "+characterCase, "randomChar "+randomChar);
                    if (index == 1) {
                        if(isUpperCase)
                        mString = mString.append((char) getRandomIntRange(65, 90));
                        else if(isLowerCase)
                            mString = mString.append((char) getRandomIntRange(97, 122));
                        else if(isSpecialCharacters){
                            char c = (char) (new Random().nextInt(4) + 35);
                            mString = mString.append(c);
                        }else if(isNumbers)
                            mString = mString.append(Character.forDigit(new Random().nextInt(10), 10));
                    } else if (index == 2) {
                        if(isLowerCase)
                            mString = mString.append((char) getRandomIntRange(97, 122));
                        else if(isUpperCase)
                            mString = mString.append((char) getRandomIntRange(65, 90));
                        else if(isSpecialCharacters){
                            char c = (char) (new Random().nextInt(4) + 35);
                            mString = mString.append(c);
                        }else if(isNumbers)
                            mString = mString.append(Character.forDigit(new Random().nextInt(10), 10));
                    } else if (index == 3) {
                        if(isSpecialCharacters){
                            char c = (char) (new Random().nextInt(4) + 35);
                            mString = mString.append(c);
                        }else if(isNumbers)
                            mString = mString.append(Character.forDigit(new Random().nextInt(10), 10));
                        else if(isLowerCase)
                            mString = mString.append((char) getRandomIntRange(97, 122));
                        else if(isUpperCase)
                            mString = mString.append((char) getRandomIntRange(65, 90));
                    } else if (index == 4) {
                        if(isNumbers)
                            mString = mString.append(Character.forDigit(new Random().nextInt(10), 10));
                        if(isSpecialCharacters){
                            char c = (char) (new Random().nextInt(4) + 35);
                            mString = mString.append(c);
                        }else if(isLowerCase)
                            mString = mString.append((char) getRandomIntRange(97, 122));
                        else if(isUpperCase)
                            mString = mString.append((char) getRandomIntRange(65, 90));
                    }else {
                        if(isUpperCase && characterCase==1)
                            mString = mString.append((char) getRandomIntRange(65, 90));
                        else if(isLowerCase && characterCase==2)
                            mString = mString.append((char) getRandomIntRange(97, 122));
                        else if(isSpecialCharacters && characterCase==3){
                            char c = (char) (new Random().nextInt(4) + 35);
                            mString = mString.append(c);
                        }else if(isNumbers && characterCase==4)
                            mString = mString.append(Character.forDigit(new Random().nextInt(10), 10));
                    }
            }
        return mString.toString();
    }

    public static boolean isFullfilCriteria(String word){
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        boolean symbolFlag = false;
        for(int i=0;i < word.length();i++) {
            ch = word.charAt(i);
            if(isNumbers && Character.isDigit(ch)) {
                numberFlag = true;
            } else if (isUpperCase && Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (isLowerCase && Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }else {
                if(isSpecialCharacters)
                symbolFlag = true;
            }
            if(numberFlag && capitalFlag && lowerCaseFlag && symbolFlag)
                return true;
        }
        return false;
    }


    private List<String> getRandomUpperCaseWords(int maxCharCount){
        List<String> stringUpperList = new ArrayList<>();
        // ASCII A = 65 - Z = 90
        StringBuilder mString = new StringBuilder();
        for(int i = 0; i<2;i++) {
            int randomCharCount = getRandomIntRange(1, maxCharCount-1);
            for (int index = 0; index < randomCharCount; index++) {
                mString = mString.append((char) getRandomIntRange(65, 90));
            }
            stringUpperList.add(mString.toString());
            mString = new StringBuilder();
        }
        return stringUpperList;
    }
    private List<String> getRandomLowercaseWords(int maxCharCount){
        List<String> stringLowerList = new ArrayList<>();
        // ASCII a = 97 - z = 122
        StringBuilder mString = new StringBuilder();
        for(int i = 0; i<2;i++) {
            int randomCharCount = getRandomIntRange(1, maxCharCount-1);
            for (int index = 0; index < randomCharCount; index++) {
                mString = mString.append((char) getRandomIntRange(97, 122));
            }
            stringLowerList.add(mString.toString());
            mString = new StringBuilder();
        }
        return stringLowerList;
    }
    private List<String> getRandomSpecialWords(int maxCharCount){
        List<String> stringSpecialList = new ArrayList<>();
        // ASCII A = 65 - Z = 90
        StringBuilder mString = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i<3;i++) {
            int randomCharCount = getRandomIntRange(1, maxCharCount-1);
            for (int index = 0; index < randomCharCount; index++) {
                char c = (char) (random.nextInt(4) + 35);
                mString = mString.append(c);
            }
            stringSpecialList.add(mString.toString());
            mString = new StringBuilder();
        }
        return stringSpecialList;
    }
    private List<String> getRandomNumberWords(int maxCharCount){
        List<String> stringNumbersList = new ArrayList<>();
        // ASCII A = 65 - Z = 90
        StringBuilder mString = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i<3;i++) {
            int randomCharCount = getRandomIntRange(1, maxCharCount-1);
            for (int index = 0; index < randomCharCount; index++) {
                mString = mString.append(Character.forDigit(random.nextInt(10), 10));
            }
            stringNumbersList.add(mString.toString());
            mString = new StringBuilder();
        }
        return stringNumbersList;
    }
}
