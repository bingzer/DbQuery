package com.bingzer.android.dbv.test;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.IView;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 8/19/13.
 */
public class ViewTest extends AndroidTestCase {

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("ViewTest");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return ViewTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Orders")
                        .addPrimaryKey("Id")
                        .add("Quantity", "INTEGER")
                        .add("ProductId", "INTEGER")
                        .add("CustomerId", "INTEGER");

                modeling.add("Products")
                        .addPrimaryKey("Id")
                        .add("Name", "TEXT")
                        .add("Price", "FLOAT");

                modeling.add("Customers")
                        .addPrimaryKey("Id")
                        .add("Name", "TEXT")
                        .add("Address", "TEXT");

                modeling.addView("CustomerView")
                        .as("SELECT * FROM Customers")
                        .ifNotExists();

                modeling.addView("OrderView")
                        .as("SELECT O.Id, O.Quantity, P.Name AS ProductName, C.Name AS CustomerName")
                        .append("FROM Orders O")
                        .append("JOIN Products P ON P.Id = O.ProductId")
                        .append("JOIN Customers C ON C.Id = O.CustomerId")
                        .ifNotExists();
            }
        });

        db.get("Orders").delete();
        db.get("Products").delete();
        db.get("Customers").delete();

        // initial value
        IQuery.InsertWith insert = db.get("Customers").insert("Name", "Address");
        insert.val("Baloteli", "Italy");
        insert.val("Pirlo", "Italy");
        insert.val("Ronaldo", "Portugal");
        insert.val("Messi", "Argentina");

        insert = db.get("Products").insert("Name", "Price");
        insert.val("Computer", 600);
        insert.val("Smartphone", 450);
        insert.val("Car", 20000);
        insert.val("House", 500000);
        insert.val("Monitor", 120);

        insert = db.get("Orders").insert("Quantity", "ProductId", "CustomerId");
        insert.val(2, db.get("Products").selectId("Name = ?", "Computer"), db.get("Customers").selectId("Name = ?", "Baloteli"));
        insert.val(1, db.get("Products").selectId("Name = ?", "House"), db.get("Customers").selectId("Name = ?", "Ronaldo"));
        insert.val(5, db.get("Products").selectId("Name = ?", "Monitor"), db.get("Customers").selectId("Name = ?", "Messi"));
        insert.val(1, db.get("Products").selectId("Name = ?", "Computer"), db.get("Customers").selectId("Name = ?", "Messi"));
    }

    public void testTableNull(){
        assertNotNull(db.getView("CustomerView"));
        assertNotNull(db.getView("OrderView"));
        assertNull(db.getView("ProductView"));
    }

    public void testTableAliases(){
        IView view = db.getView("CustomerView C");
        assertNotNull(view);
        assertEquals(view.getName(), "CustomerView");
        assertEquals(view.getAlias(), "C");
        assertEquals(view.toString(), "CustomerView C");

        view = db.getView("OrderView O");
        assertNotNull(view);
        assertEquals(view.getName(), "OrderView");
        assertEquals(view.getAlias(), "O");
        assertEquals(view.toString(), "OrderView O");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    public void testSelect_Simple(){
        Cursor cursor = db.getView("CustomerView").select("Address = ?", "Italy").orderBy("Id").query();
        assertTrue(cursor.getCount() == 2);
        if(cursor.moveToNext()){
            assertTrue(cursor.getString(1).equals("Baloteli"));
            assertTrue(cursor.getString(2).equals("Italy"));
        }
        if(cursor.moveToNext()){
            assertTrue(cursor.getString(1).equals("Pirlo"));
            assertTrue(cursor.getString(2).equals("Italy"));
        }
        cursor.close();
    }

    public void testSelect_Simple_OrderBy(){
        Cursor cursor = db.getView("CustomerView").select("Address = ?", "Italy").orderBy("Name DESC").query();
        assertTrue(cursor.getCount() == 2);
        if(cursor.moveToNext()){
            assertTrue(cursor.getString(1).equals("Pirlo"));
            assertTrue(cursor.getString(2).equals("Italy"));
        }
        if(cursor.moveToNext()){
            assertTrue(cursor.getString(1).equals("Baloteli"));
            assertTrue(cursor.getString(2).equals("Italy"));
        }
        cursor.close();
    }

    public void testSelect_Complex(){
        Cursor cursor = db.getView("OrderView").select("CustomerName IN (?,?)", "Messi", "Ronaldo").orderBy("CustomerName").query();

        assertTrue(cursor.getCount() == 3);
        if(cursor.moveToNext()){
            assertEquals(cursor.getString(3), ("Messi"));
            assertEquals(cursor.getString(2), ("Monitor"));
        }
        if(cursor.moveToNext()){
            assertEquals(cursor.getString(3), ("Messi"));
            assertEquals(cursor.getString(2), ("Computer"));
        }
        if(cursor.moveToNext()){
            assertEquals(cursor.getString(3), ("Ronaldo"));
            assertEquals(cursor.getString(2), ("House"));
        }
        cursor.close();
    }
}
