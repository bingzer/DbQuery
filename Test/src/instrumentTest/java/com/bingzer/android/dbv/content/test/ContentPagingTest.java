package com.bingzer.android.dbv.content.test;

import android.database.Cursor;
import android.provider.UserDictionary;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.content.ContentQuery;
import com.bingzer.android.dbv.content.IResolver;

/**
 * Created by Ricky on 8/22/13.
 */
public class ContentPagingTest extends AndroidTestCase {

    IResolver resolver;
    int baloteliId, pirloId, kakaId, messiId, ronaldoId;

    @Override
    public void setUp(){
        resolver = ContentQuery.resolve(UserDictionary.Words.CONTENT_URI, getContext());
        resolver.setDefaultProjections("_id", "word");
        resolver.setDefaultAuthority(UserDictionary.AUTHORITY);
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

    @Override
    public void tearDown(){
        resolver.delete(baloteliId, pirloId, kakaId, messiId, ronaldoId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    public void testPaging_Simple(){
        IQuery.Paging paging = resolver
                .select(baloteliId, pirloId, kakaId, messiId, ronaldoId)
                .orderBy("_id")
                .paging(2);

        Cursor cursor = paging.query();
        assertEquals(cursor.getCount(), 2);
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), baloteliId);
        assertEquals(cursor.getString(1), "Baloteli");
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), pirloId);
        assertEquals(cursor.getString(1), "Pirlo");
        cursor.close();

        cursor = paging.query();
        assertEquals(cursor.getCount(), 2);
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), kakaId);
        assertEquals(cursor.getString(1), "Kaka");
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), messiId);
        assertEquals(cursor.getString(1), "Messi");
        cursor.close();

        cursor = paging.query();
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), ronaldoId);
        assertEquals(cursor.getString(1), "Ronaldo");
        cursor.close();
    }

    public void testPaging_GetTotalPage(){
        IQuery.Paging paging = resolver
                .select(baloteliId, pirloId, kakaId, messiId, ronaldoId)
                .orderBy("_id")
                .paging(2);

        assertEquals(paging.getTotalPage(), 3);

    }
}
