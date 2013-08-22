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
    int baloteliId, pirloId, kakaId, messiId, ronaldoId;

    @Override
    public void setUp(){
        resolver = ContentQuery.resolve(UserDictionary.Words.CONTENT_URI, getContext());
        resolver.setReturnedColumns("_id", "word");
        resolver.delete("word IN (?,?,?,?,?)", "Baloteli", "Pirlo", "Kaka", "Messi", "Ronaldo").query();

        baloteliId = insertToDictionary("Baloteli");
        pirloId = insertToDictionary("Pirlo");
        kakaId = insertToDictionary("Kaka");
        messiId = insertToDictionary("Messi");
        ronaldoId = insertToDictionary("Ronaldo");
    }

    int insertToDictionary(String word){
        int id = resolver.insert("word").val(word).query();
        assertTrue(id > 0);
        return id;
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public void testSelectId(){
        Cursor cursor = resolver.select(baloteliId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Baloteli"));
        }
        cursor.close();

        cursor = resolver.select(pirloId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Pirlo"));
        }
        cursor.close();

        cursor = resolver.select(kakaId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Kaka"));
        }
        cursor.close();

        cursor = resolver.select(messiId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Messi"));
        }
        cursor.close();

        cursor = resolver.select(ronaldoId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Ronaldo"));
        }
        cursor.close();
    }

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

            if(!foundBaloteli) foundBaloteli = word.equals("Baloteli");
            if(!foundPirlo) foundPirlo = word.equals("Pirlo");
            if(!foundKaka) foundKaka = word.equals("Kaka");
            if(!foundMessi) foundMessi = word.equals("Messi");
            if(!foundRonaldo) foundRonaldo = word.equals("Ronaldo");
        }

        assertTrue(foundBaloteli && foundPirlo && foundKaka && foundMessi && foundRonaldo);
    }
}
