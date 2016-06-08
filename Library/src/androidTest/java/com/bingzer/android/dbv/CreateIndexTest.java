package com.bingzer.android.dbv;

import android.content.Context;
import android.test.AndroidTestCase;

/**
 * Created by Ricky Tobing on 8/12/13.
 */
public class CreateIndexTest extends AndroidTestCase {

    IDatabase db;
    ITable.Model tableModel;

    @Override
    public void setUp(){

        db = DbQuery.getDatabase("CreateIndexDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return CreateIndexTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                tableModel = modeling.add("Person")
                        .addPrimaryKey("Id")
                        .add("Name", "String")
                        .add("Age", "Integer")
                        .add("Address", "Blob")
                        .index("Id", "Name");

            }
        });

        db.from("Person").delete();
    }

    @Override
    protected void tearDown() throws Exception {
        db.close();
        getContext().deleteDatabase("CreateIndexDb");
    }

    public void testTableModel_ShouldContainIndex_OnId(){
        assertTrue(tableModel != null);
        assertTrue(tableModel.toString().toLowerCase().contains("person_id_idx"));
    }

    public void testTableModel_ShouldContainIndex_OnName(){
        assertTrue(tableModel != null);
        assertTrue(tableModel.toString().toLowerCase().contains("person_name_idx"));
    }

    public void testTableModel_ShouldContainIndex_OnAddress(){
        assertTrue(tableModel != null);
        assertTrue(!tableModel.toString().toLowerCase().contains("person_address_idx"));
    }

}
