package com.bingzer.android.dbv.test;

import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbEngine;
import com.bingzer.android.dbv.ITable;

/**
 * Created by Ricky Tobing on 7/18/13.
 */
public class ITableTest extends AndroidTestCase{

    ITable table;
    public void setUp(){
        table = DbEngine.getDatabase("TestDb").get("Persons");
    }

    public void testGetName() throws Exception {
        assertTrue(table.getName().equalsIgnoreCase("Persons"));
    }


}
