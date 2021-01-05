package com.evontech.passwordgridapp.custom.data.xml;


import com.evontech.passwordgridapp.custom.models.Word;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suraj Kumar on 17/12/20.
 */

class SaxWordBankHandler extends DefaultHandler {

    private static final String XML_ITEM_TAG_NAME = "item";
    private static final String XML_STR_ATTRIB_NAME = "str";

    private List<Word> mWordList;

    @Override
    public void startDocument() throws SAXException {
        mWordList = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase(XML_ITEM_TAG_NAME)) {
            Word word = new Word(mWordList.size(), attributes.getValue(XML_STR_ATTRIB_NAME));
            mWordList.add(word);
        }
    }

    List<Word> getWords() {
        return mWordList;
    }
}
