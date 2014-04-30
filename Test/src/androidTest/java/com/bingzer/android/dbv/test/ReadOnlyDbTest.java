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

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("ReadOnlyDbTest");
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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        db.close();
    }

    public void testDbIsReadOnly(){
        assertTrue(db.getConfig().isReadOnly());
    }

    public void testQuery_ColumnCount(){
        assertTrue(db.get("Person").getColumnCount() == 4);
    }

    public void testInsert_Throw(){
        try{
            db.get("Person").insert("Name", "Ricky").query();
            fail("Should throw error");
        }
        catch (IllegalAccessError e){
            // good
        }
    }

    public void testUpdate_Throw(){
        try{
            db.get("Person").update("Name = ? ", "Ricky").columns("Name").val().query();
            fail("Should throw error");
        }
        catch (IllegalAccessError e){
            // good
        }
    }

    public void testDelete_Throw(){
        try{
            db.get("Person").delete();
            fail("Should throw error");
        }
        catch (IllegalAccessError e){
            // good
        }
    }

    public void testDrop_Throw(){
        try{
            db.get("Person").drop();
            fail("Should throw error");
        }
        catch (IllegalAccessError e){
            // good
        }
    }


    public void testInsert_Update_Delete_OK(){
        db.getConfig().setReadOnly(false);

        try{
            long id = db.get("Person").insert("Name", "Ricky").query();
            assertTrue(db.get("Person").has(id));

            int numUpdated = db.get("Person").update("Name = ? ", "Ricky").columns("Name").val("Edited").query();
            assertTrue(numUpdated > 0);
            assertTrue(db.get("Person").has("Name = ?", "Edited"));

            int numDeleted = db.get("Person").delete("Name = ?", "Edited").query();
            assertTrue(numDeleted > 0);
            assertFalse(db.get("Person").has("Name = ?", "Edited"));
        }
        catch (IllegalAccessError e){
            fail("Should NOT throw error");
        }
    }

    public void testInsert_OK(){
        db.getConfig().setReadOnly(false);
        long newId = db.get("Person").insert("Name", "Ricky").query();
        assertTrue(newId > 0);
        newId = db.get("Person").insert("Name", "Ricky").query();
        assertTrue(newId > 0);

        db.close();
    }
}
