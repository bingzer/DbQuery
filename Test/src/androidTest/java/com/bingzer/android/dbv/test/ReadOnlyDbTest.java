package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.SQLiteBuilder;

/**
 * Created by Ricky on 8/17/13.
 */
public class ReadOnlyDbTest extends AndroidTestCase{

    IDatabase readonlyDb;

    @Override
    public void setUp(){
        readonlyDb = DbQuery.getDatabase("ReadOnlyDb");
        readonlyDb.getConfig().setReadOnly(true);
        readonlyDb.open(1, new SQLiteBuilder() {
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
        assertTrue(readonlyDb.getConfig().isReadOnly());
    }

    public void testQuery_ColumnCount(){
        assertTrue(readonlyDb.get("Person").getColumnCount() == 4);
    }

    /*
    public void testInsert_AndFailed(){
        int newId = readonlyDb.get("Person").insert("Name").val("Ricky").query();
        assertEquals(-1, newId);
        newId = readonlyDb.get("Person").insert("Name").val("Ricky").query();
        assertEquals(-1, newId);

        readonlyDb.close();


        final IDatabase writeableDb = DbQuery.getDatabase("ReadOnlyDb");
        writeableDb.getConfig().setReadOnly(false);
        writeableDb.open(1, new SQLiteBuilder.WithoutModeling(getContext()));

        newId = writeableDb.get("Person").insert("Name").val("Ricky").query();
        assertTrue(newId > 0);
        newId = writeableDb.get("Person").insert("Name").val("Ricky").query();
        assertTrue(newId > 0);

        writeableDb.close();
    }
    */
}
