package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.SQLiteBuilder;

/**
 * Created by Ricky on 8/17/13.
 */
public class CreateTableIfNotExistsTest extends AndroidTestCase{
    IDatabase db;
    final String dbName = "CreateTableIfNotExistsDb";

    @Override
    protected void setUp() throws Exception {
        db = DbQuery.getDatabase(dbName);
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return CreateTableIfNotExistsTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Table1")
                        .addPrimaryKey("Id");
            }
        });

        // table 2 must be null since we're dropping every tear down()
        assertNull(db.from("Table2"));

        db.close();
    }

    @Override
    protected void tearDown() throws Exception {
        getContext().deleteDatabase(dbName);
    }

    ///////////////////////////////////////////////////////////////////

    public void testCreate_WithIfNotExists(){
        db = DbQuery.getDatabase(dbName);

        // check version
        assertTrue(db.getVersion() == 1);
        assertNotNull(db.from("Table1"));

        db.open(2, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return CreateTableIfNotExistsTest.this.getContext();
            }

            @Override
            public boolean onUpgrade(IDatabase database, int oldVersion, int newVersion) {
                return true; // go to onModelCreate
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Table1")
                        .addPrimaryKey("Id")
                        .ifNotExists();
                modeling.add("Table2")
                        .addPrimaryKey("Id")
                        .ifNotExists();
            }

            @Override
            public void onError(Throwable error) {
                assertTrue("Error shouldn't be thrown", false);
            }
        });

        assertTrue(db.getVersion() == 2);
        assertNotNull(db.from("Table1"));
        assertNotNull(db.from("Table2"));

        db.close();
    }


    public void testCreate_WithoutIfNotExists(){
        db = DbQuery.getDatabase(dbName);

        // check version
        assertTrue(db.getVersion() == 1);
        assertNotNull(db.from("Table1"));

        db.open(2, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return CreateTableIfNotExistsTest.this.getContext();
            }

            @Override
            public boolean onUpgrade(IDatabase database, int oldVersion, int newVersion) {
                return true; // go to onModelCreate
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Table1")
                        .addPrimaryKey("Id");
                modeling.add("Table2")
                        .addPrimaryKey("Id");
            }

            @Override
            public void onError(Throwable error) {
                assertTrue("Error shouldn't be thrown:", true);
            }
        });
        db.close();
    }
}
