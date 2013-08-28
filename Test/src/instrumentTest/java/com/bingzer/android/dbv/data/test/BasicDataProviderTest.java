package com.bingzer.android.dbv.data.test;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.content.ContentQuery;
import com.bingzer.android.dbv.content.IResolver;

/**
 * Created by Ricky Tobing on 8/28/13.
 */
public class BasicDataProviderTest extends AndroidTestCase {


    public void testConnect(){
        IResolver resolver = ContentQuery.resolve("content://com.bingzer.android.dbv.data.test/Album", getContext());
        resolver.getConfig().setAppendTableNameForId(true);
        resolver.getConfig().setIdNamingConvention("Id");

        Cursor cursor = resolver.select().query();
        assertTrue(cursor.getCount() > 0);
        cursor.close();
    }


    public void testConnect2(){
        IResolver resolver = ContentQuery.resolve("content://com.bingzer.android.dbv.data.test/Album", getContext());
        resolver.getConfig().setDefaultProjections("_Id", "Title");
        resolver.getConfig().setAppendTableNameForId(true);
        resolver.getConfig().setIdNamingConvention("Id");

        Cursor cursor = resolver.select().query();
        assertTrue(cursor.getCount() > 0);
        cursor.close();;
    }
}
