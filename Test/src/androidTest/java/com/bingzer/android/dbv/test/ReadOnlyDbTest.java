package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

/**
 * Created by Ricky on 8/17/13.
 */
public class ReadOnlyDbTest extends AndroidTestCase{

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("ReadOnlyDb");
        db.getConfig().setReadOnly(true);
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return ReadOnlyDbTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Person")
                        .addPrimaryKey("Id")
                        .add("Name", "String")
                        .add("Age", "Integer")
                        .addBlob("Address");
            }
        });
    }

    public void testDbIsReadOnly(){
        assertTrue(db.getConfig().isReadOnly());
    }

    public void testQuery_ColumnCount(){
        assertTrue(db.get("Person").getColumnCount() == 4);
    }
}
