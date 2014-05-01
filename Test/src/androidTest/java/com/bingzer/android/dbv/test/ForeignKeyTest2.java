package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 8/15/13.
 */
public class ForeignKeyTest2 extends AndroidTestCase {

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("ForeignKeytest2");
        db.getConfig().setForeignKeySupport(true);
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return ForeignKeyTest2.this.getContext();
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
                        .foreignKey("CustomerId", "Customers.Id", "ON DELETE CASCADE")
                        .foreignKey("ProductId", "Products", "Id", "ON DELETE CASCADE");
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                assertFalse("Error should never be thrown out", true);
            }
        });

        db.get("Orders").delete();
        db.get("Products").delete();
        db.get("Customers").delete();

        // two customers
        db.get("Customers").insertInto("Name", "Address").val("Baloteli", "Italy");
        db.get("Customers").insertInto("Name", "Address").val("Pirlo", "Italy");
        // two products
        db.get("Products").insertInto("Name", "Price").val("Computer", 1000);
        db.get("Products").insertInto("Name", "Price").val("Cellphone", 500);

        long baloteliId = db.get("Customers").selectId("Name = ?", "Baloteli");
        long computerId = db.get("Products").selectId("Name = ?", "Computer");
        // orders
        db.get("Orders").insertInto("Quantity", "CustomerId", "ProductId")
                .val(10, baloteliId, computerId);
    }

    // test away..
    public void testDeleteCascade(){
        long baloteliId = db.get("Customers").selectId("Name = ?", "Baloteli");
        long computerId = db.get("Products").selectId("Name = ?", "Computer");

        assertTrue(db.get("Orders").has("CustomerId = ? AND ProductId = ?", baloteliId, computerId));

        // delete baloteli
        assertEquals(1, (int) db.get("Customers").delete("Name = ?", "Baloteli").query());
        // baloteli should be deleted
        assertFalse(db.get("Customers").has(baloteliId));

        assertFalse(db.get("Orders").has("CustomerId = ? AND ProductId = ?", baloteliId, computerId));
    }
}
