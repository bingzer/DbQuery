package com.bingzer.android.dbv.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.content.ContentQuery;
import com.bingzer.android.dbv.content.IResolver;

/**
 * Created by Ricky on 8/21/13.
 */
public class ContentTest extends AndroidTestCase {

    IResolver resolver;

    @Override
    public void setUp(){
        insertToDictionary("Balotelli");
        insertToDictionary("Pirlo");
        insertToDictionary("Kaka");
        insertToDictionary("Messi");
        insertToDictionary("Ronaldo");


        resolver = ContentQuery.resolve(UserDictionary.Words.CONTENT_URI, getContext());
    }

    void insertToDictionary(String word){
        Uri uri = UserDictionary.Words.CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(UserDictionary.Words.WORD, word);

        getContext().getContentResolver().insert(uri, values);
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public void testSelect(){
        boolean foundBaloteli = false;
        boolean foundPirlo = false;
        boolean foundKaka = false;
        boolean foundMessi = false;
        boolean foundRonaldo = false;
        Cursor cursor = resolver
                .select()
                .columns("word")
                .orderBy("word")
                .query();
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(UserDictionary.Words.WORD);
            String word = cursor.getString(index);

            if(!foundBaloteli) foundBaloteli = word.equals("Balotelli");
            if(!foundPirlo) foundPirlo = word.equals("Pirlo");
            if(!foundKaka) foundKaka = word.equals("Kaka");
            if(!foundMessi) foundMessi = word.equals("Messi");
            if(!foundRonaldo) foundRonaldo = word.equals("Ronaldo");
        }

        assertTrue(foundBaloteli && foundPirlo && foundKaka && foundMessi && foundRonaldo);
    }
}
