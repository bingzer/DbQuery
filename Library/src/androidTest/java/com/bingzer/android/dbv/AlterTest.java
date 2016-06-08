package com.bingzer.android.dbv;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.utils.CollectionUtils;

/**
 * Created by Ricky Tobing on 8/16/13.
 */
public class AlterTest extends AndroidTestCase {

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("AlterDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return AlterTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Person")
                        .addPrimaryKey("Id")
                        .addText("Name");
            }
        });
    }

    @Override
    public void tearDown(){
        db.close();
        getContext().deleteDatabase("AlterDb");
    }

    public void testRenameColumn(){
        assertNotNull(db.from("Person"));
        // alter
        ITable table = db.from("Person");
        table.alter().rename("PersonAltered");
        assertTrue(table.getName().equals("PersonAltered"));

        // re check person
        assertNull(db.from("Person"));
        // should not be null
        assertNotNull(db.from("PersonAltered"));

        // renamed to Person
        table.alter().rename("Person");
    }

    public void testAddColumn(){
        // don't have address yet
        assertFalse(CollectionUtils.contains(db.from("Person").getColumns(), "Address"));

        // we'll add address column
        db.from("Person").alter().addColumn("Address", "Text");

        assertTrue(CollectionUtils.contains(db.from("Person").getColumns(), "Address"));
    }

    public void testRemoveColumn(){
        try{
            // should throw exception
            db.from("Person").alter().removeColumn("Name");
            assertTrue(false);
        }
        catch (Exception e){
            assertTrue(true);
        }
    }
}
