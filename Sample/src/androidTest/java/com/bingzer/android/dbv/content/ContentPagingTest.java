package com.bingzer.android.dbv.content;

import android.database.Cursor;
import android.provider.UserDictionary;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.queries.Paging;

/**
 * Created by Ricky on 8/22/13.
 */
public class ContentPagingTest extends AndroidTestCase {

    IResolver resolver;
    long baloteliId, pirloId, kakaId, messiId, ronaldoId;

    @Override
    public void setUp(){
        resolver = ContentQuery.resolve(UserDictionary.Words.CONTENT_URI, getContext());
        resolver.getConfig().setDefaultProjections("_id", "word");
        resolver.getConfig().setDefaultAuthority(UserDictionary.AUTHORITY);
        resolver.delete("word IN (?,?,?,?,?)", "Baloteli", "Pirlo", "Kaka", "Messi", "Ronaldo").query();

        baloteliId = insertToDictionary("Baloteli");
        pirloId = insertToDictionary("Pirlo");
        kakaId = insertToDictionary("Kaka");
        messiId = insertToDictionary("Messi");
        ronaldoId = insertToDictionary("Ronaldo");
    }

    long insertToDictionary(String word){
        long id = resolver.insert("word", word).query();
        assertTrue(id > 0);
        return id;
    }

    @Override
    public void tearDown(){
        resolver.delete(baloteliId, pirloId, kakaId, messiId, ronaldoId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    public void testPaging_Simple(){
        Paging paging = resolver
                .select(baloteliId, pirloId, kakaId, messiId, ronaldoId)
                .orderBy("_id")
                .paging(2);
        assertTrue(paging.getPageNumber() == 0);

        Cursor cursor = paging.query();
        assertTrue(paging.getPageNumber() == 0);

        assertEquals(cursor.getCount(), 2);
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), baloteliId);
        assertEquals(cursor.getString(1), "Baloteli");
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), pirloId);
        assertEquals(cursor.getString(1), "Pirlo");
        cursor.close();

        cursor = paging.next().query();
        assertTrue(paging.getPageNumber() == 1);

        assertEquals(cursor.getCount(), 2);
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), kakaId);
        assertEquals(cursor.getString(1), "Kaka");
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), messiId);
        assertEquals(cursor.getString(1), "Messi");
        cursor.close();

        cursor = paging.next().query();
        assertTrue(paging.getPageNumber() == 2);

        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getInt(0), ronaldoId);
        assertEquals(cursor.getString(1), "Ronaldo");
        cursor.close();
    }

    public void testPaging_IEntity(){
        Paging paging = resolver
                .select(baloteliId, pirloId, kakaId, messiId, ronaldoId)
                .orderBy("_id")
                .paging(2);
        assertTrue(paging.getPageNumber() == 0);

        WordList list = new WordList();

        paging.query(list);
        assertTrue(paging.getPageNumber() == 0);

        assertEquals(list.size(), 2);
        assertEquals(list.get(0).getId(), baloteliId);
        assertEquals(list.get(0).getWord(), "Baloteli");
        assertEquals(list.get(1).getId(), pirloId);
        assertEquals(list.get(1).getWord(), "Pirlo");

        paging.next().query(list);
        assertTrue(paging.getPageNumber() == 1);

        assertEquals(list.size(), 4);
        assertEquals(list.get(2).getId(), kakaId);
        assertEquals(list.get(2).getWord(), "Kaka");
        assertEquals(list.get(3).getId(), messiId);
        assertEquals(list.get(3).getWord(), "Messi");


        paging.next().query(list);
        assertTrue(paging.getPageNumber() == 2);

        assertEquals(list.size(), 5);
        assertEquals(list.get(4).getId(), ronaldoId);
        assertEquals(list.get(4).getWord(), "Ronaldo");
    }

    public void testPaging_GetTotalPage(){
        Paging paging = resolver
                .select(baloteliId, pirloId, kakaId, messiId, ronaldoId)
                .orderBy("_id")
                .paging(2);
        assertTrue(paging.getPageNumber() == 0);

        assertEquals(paging.getTotalPage(), 3);

    }

    public void testPaging_SetPageNumber(){
        Paging paging = resolver
                .select(baloteliId, pirloId, kakaId, messiId, ronaldoId)
                .orderBy("_id")
                .paging(2);
        assertTrue(paging.getPageNumber() == 0);
    }
}
