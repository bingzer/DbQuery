package com.bingzer.android.dbv.test;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 8/12/13.
 */
public class CreateIndexTest extends AndroidTestCase {

    IDatabase db;
    @Override
    public void setUp(){

        getContext().deleteDatabase("CreateIndexDb");

        db = DbQuery.getDatabase("CreateIndexDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return CreateIndexTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Person")
                        .addPrimaryKey("Id")
                        .add("Name", "String")
                        .add("Age", "Integer")
                        .add("Address", "Blob")
                        .index("Id")
                        .index("Name");
            }
        });

        db.get("Person").deleteAll();
    }

}
