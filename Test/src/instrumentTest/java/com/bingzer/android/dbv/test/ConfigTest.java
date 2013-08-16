package com.bingzer.android.dbv.test;

import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;

/**
 * Created by Ricky Tobing on 8/16/13.
 */
public class ConfigTest extends AndroidTestCase {

    IDatabase db = DbQuery.getDatabase("X");

    public void testIdNamingConvention(){
        // default
        assertTrue(db.getConfig().getIdNamingConvention().equals("Id"));

        db.getConfig().setIdNamingConvention("OID");
        assertTrue(db.getConfig().getIdNamingConvention().equals("OID"));
        db.getConfig().setIdNamingConvention("LOL");
        assertTrue(db.getConfig().getIdNamingConvention().equals("LOL"));
    }

    public void testAppendTableNameForId(){
        // default
        assertTrue(!db.getConfig().getAppendTableNameForId());

        db.getConfig().setAppendTableNameForId(true);
        assertTrue(db.getConfig().getAppendTableNameForId());
        db.getConfig().setAppendTableNameForId(false);
        assertTrue(!db.getConfig().getAppendTableNameForId());
    }

    public void testSetForeignKeySupport(){
        // default
        assertTrue(!db.getConfig().getForeignKeySupport());

        db.getConfig().setForeignKeySupport(true);
        assertTrue(db.getConfig().getForeignKeySupport());
        db.getConfig().setForeignKeySupport(false);
        assertTrue(!db.getConfig().getForeignKeySupport());
    }

    public void testSetDebug(){
        // default
        assertFalse(db.getConfig().getDebug());

        db.getConfig().setDebug(true);
        assertTrue(db.getConfig().getDebug());
        db.getConfig().setDebug(false);
        assertTrue(!db.getConfig().getDebug());
    }
}
