package com.bingzer.android.dbv.test.migration;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

/**
 * Created by Ricky on 8/5/13.
 */
public class AASetup extends AndroidTestCase {

    @Override
    public void setUp(){
        getContext().deleteDatabase("MigrationDb");

        IDatabase db = DbQuery.getDatabase("MigrationDb");
        db.close();
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return AASetup.this.getContext();
            }

            @Override
            public boolean onDowngrade(IDatabase database, int oldVersion, int newVersion) {
                for(int i = 0; i < database.getTables().size(); i++)
                    database.getTables().get(i).drop();
                return true;
            }

            @Override
            public boolean onUpgrade(IDatabase database, int oldVersion, int newVersion) {
                for(int i = 0; i < database.getTables().size(); i++)
                    database.getTables().get(i).drop();
                return true;
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Table1")
                        .addPrimaryKey("Id")
                        .add("Column2", "INTEGER")
                        .add("Column3", "INTEGER");
            }
        });

        assertTrue(db.getVersion() == 1);
        assertNotNull(db.get("Table1"));
        assertNull(db.get("Table2"));

        //db.close();
    }

    public void testUpgrade(){
        IDatabase db = DbQuery.getDatabase("MigrationDb");
        db.close();
        db.open(2, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return AASetup.this.getContext();
            }

            @Override
            public boolean onUpgrade(IDatabase database, int oldVersion, int newVersion) {
                super.onUpgrade(database, oldVersion, newVersion);

                ITable table = database.get("Table1");
                table.drop();

                assertNull(database.get("Table1"));

                return true;
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Table2")
                        .addPrimaryKey("Id")
                        .add("Column2", "INTEGER")
                        .add("Column4", "TEXT");
            }
        });

        assertTrue(db.getVersion() == 2);
        assertNull(db.get("Table1"));
        assertNotNull(db.get("Table2"));

        db.close();
    }
}
