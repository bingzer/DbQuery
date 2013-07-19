package com.bingzer.android.dbv.test;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbEngine;
import com.bingzer.android.dbv.IDatabase;

/**
 * Created by Ricky Tobing on 7/18/13.
 */
public class IDatabaseTest extends AndroidTestCase {

    IDatabase db;

    public void setUp(){
        db = DbEngine.getDatabase("TestDb");
    }

    ////////////////////
    ////////////////////
    ////////////////////

    public void testGetName(){
        assertTrue(db.getName().equalsIgnoreCase("TestDb"));
    }

    public void testGetVersion(){
        assertTrue(db.getVersion() == 1);
    }

    public void testGetTables(){
        assertTrue(db.getTables() != null);
        assertTrue(db.getTables().size() >= 2);
    }

    public void testRaw(){
        String sql = "SELECT * FROM Jobs";
        Cursor cursor = db.raw(sql).query();

        assertTrue(cursor != null);

        cursor.close();
    }

}
