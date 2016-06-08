package com.bingzer.android.dbv;

import android.content.Context;
import android.test.AndroidTestCase;

/**
 * Created by Ricky Tobing on 8/15/13.
 */
public class ForeignKeyTest extends AndroidTestCase {

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("ForeignKeytest");
        db.getConfig().setForeignKeySupport(true);
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return ForeignKeyTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Customers")
                        .addPrimaryKey("Id")
                        .add("Name", "String")
                        .add("Address", "Text")
                        .index("Name")
                        .index("Address");

                modeling.add("Products")
                        .addPrimaryKey("Id")
                        .add("Name", "text")
                        .addNumeric("Price")
                        .index("Name");

                modeling.add("Orders")
                        .addPrimaryKey("Id")
                        .add("Quantity", "Integer")
                        .add("CustomerId", "Integer")
                        .add("ProductId", "Integer")
                        .index("CustomerId")
                        .index("ProductId")
                        .foreignKey("CustomerId", "Customers.Id")
                        .foreignKey("ProductId", "Products", "Id", null);
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                assertFalse("Error should never be thrown out", true);
            }
        });

        db.getConfig().setForeignKeySupport(true);
        db.from("Orders").delete();
        db.from("Products").delete();
        db.from("Customers").delete();

        // two customers
        db.from("Customers").insertInto("Name", "Address").val("Baloteli", "Italy");
        db.from("Customers").insertInto("Name", "Address").val("Pirlo", "Italy");
        // two products
        db.from("Products").insertInto("Name", "Price").val("Computer", 1000);
        db.from("Products").insertInto("Name", "Price").val("Cellphone", 500);
    }

    // test away..
    public void testInsertOrder_Fail(){
        // right customerId wrong productId
        long custId = db.from("Customers").selectId("Name = ?", "Baloteli");
        int prodId = 99999;

        try{
            long id = db.from("Orders").insertInto("Quantity", "CustomerId", "ProductId").val(10, custId, prodId).query();
            assertTrue(id < 0);
            assertTrue("Should throw error", false);
        }
        catch (Exception e){
            assertTrue(true);
        }
    }
}
