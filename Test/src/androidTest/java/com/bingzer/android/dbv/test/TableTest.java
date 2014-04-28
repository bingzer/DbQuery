/**
 * Copyright 2013 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance insert the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bingzer.android.dbv.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.queries.IEnumerable;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.SQLiteBuilder;
import com.bingzer.android.dbv.queries.InsertInto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Ricky Tobing on 7/18/13.
 */
public class TableTest extends AndroidTestCase{

    static boolean populated = false;
    IDatabase db;
    ITable customerTable;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("TableTest");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return TableTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                createDatabaseModeling(modeling);
            }
        });

        if(!populated) {
            db.get("Customers").delete();
            db.get("Products").delete();
            db.get("Orders").delete();
            populateData();
            populated = true;
        }

        customerTable = db.get("Customers");
    }

    @Override
    public void tearDown(){
        db.close();
    }


    public void testTableNull(){
        assertTrue(db.get("Customers") != null);
        assertTrue(db.get("Products") != null);
        assertTrue(db.get("Orders") != null);
    }

    public void testTableAliases(){
        ITable table = db.get("Customers C");
        assertTrue(table != null);
        assertTrue(table.getAlias().equals("C"));

        table = db.get("Products P");
        assertTrue(table != null);
        assertTrue(table.getAlias().equals("P"));

        table = db.get("Orders O");
        assertTrue(table != null);
        assertTrue(table.getAlias().equals("O"));
    }

    public void testGetCustomerId(){
        assertTrue(getCustomerId("Andrea Pirlo") > 0);
        assertTrue(getCustomerId("Christiano Ronaldo") > 0);
        assertTrue(getCustomerId("Kaka") > 0);

        assertTrue(getCustomerId("Kiki") < 0);
        assertTrue(getCustomerId("Glass") < 0);
    }
    public void testGetName() throws Exception {
        assertTrue(customerTable.getName().equalsIgnoreCase("Customers"));
    }

    public void testGetColumns(){
        assertTrue(customerTable.getColumns().size() > 0);
        for(String s : customerTable.getColumns()){
            assertTrue(s != null);
        }
    }

    public void testGetColumnCount() {
        assertTrue(customerTable.getColumnCount() > 0);
        assertTrue(customerTable.getColumnCount() == customerTable.getColumns().size());
        assertTrue(db.get("Customers C").count() > 0);
    }

    ///////////////////////////////////////////////
    // ----------------- HAS() ------------------//
    public void testHas_Id(){
        long id = getCustomerId("Lionel Messi");
        assertTrue(db.get("Customers").has(id));

        assertFalse(db.get("Customers").has(-1));
        assertFalse(db.get("Customers").has(-89));
    }

    public void testHas_Condition(){
        assertTrue(db.get("Customers").has("Name LIKE '%Messi'"));
        assertTrue(db.get("Customers").has("Name LIKE '%Pirlo'"));

        assertFalse(db.get("Customers").has("Name LIKE '%YoMama'"));
        assertFalse(db.get("Customers").has("Name IS NULL"));
    }

    public void testHas_WhereClause(){
        assertTrue(db.get("Customers").has("Name LIKE ?", "%Messi"));
        assertTrue(db.get("Customers").has("Name LIKE ?", "%Ronaldo"));
        assertTrue(db.get("Orders").has("CustomerId = ?", db.get("Customers").selectId("Name LIKE ?", "%Messi")));

        assertFalse(db.get("Orders").has("CustomerId IS NULL"));
        assertFalse(db.get("Customers").has("Name = ?", "Sanatan"));
    }

    ///////////////////////////////////////////////
    // ----------------- count() ------------------//
    public void testCount(){
        assertEquals(7, db.get("Customers").count());
        assertEquals(10, db.get("Products").count());
    }

    public void testCount_Condition(){
        assertEquals(2, db.get("Customers").count("Name = 'Lionel Messi' or Name = 'Christiano Ronaldo'"));
        assertEquals(2, db.get("Customers").count("Name = 'Lionel Messi' or Name = 'Christiano Ronaldo' or Name = 'NONE'"));
    }

    public void testCount_WhereClause(){
        assertEquals(7, db.get("Customers").count("Name = ? or Id is not null", "Lionel Messi"));
        assertEquals(1, db.get("Products").count("Name = ?", "Computer"));
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ SELECT ----------------//

    public void testSelect_SimpleWithAlias(){
        Cursor c = db.get("Customers c").select().query();
        assertTrue(c != null);
        c.moveToNext();
        assertTrue(c.getCount() > 0);
        c.close();
    }

    public void testSelect_Id(){
        long messiId = getCustomerId("Lionel Messi");

        Cursor c = customerTable.select(messiId).query();
        c.moveToFirst();
        assertTrue(c != null);
        assertTrue(c.getString(c.getColumnIndex("Name")) != null);  // name never null
        c.close();
    }

    public void testSelect_Ids(){
        long messiId = getCustomerId("Lionel Messi");
        long crId = getCustomerId("Christiano Ronaldo");
        Cursor c = customerTable.select(messiId, crId).query();
        assertTrue(c.getCount() == 2);
        while(c.moveToNext()){
            assertTrue(
                    c.getString(c.getColumnIndex("Name")).equalsIgnoreCase("Lionel Messi") ||
                            c.getString(c.getColumnIndex("Name")).equalsIgnoreCase("Christiano Ronaldo")
            );
        }
        c.close();
    }

    public void testSelect_all(){
        Cursor c = customerTable.select().query();
        assertTrue(c.getCount() == 7);
        if(c.moveToNext()) assertEquals(c.getString(1), "Wayne Rooney");
        if(c.moveToNext()) assertEquals(c.getString(1), "Lionel Messi");
        if(c.moveToNext()) assertEquals(c.getString(1), "Christiano Ronaldo");
        if(c.moveToNext()) assertEquals(c.getString(1), "Mario Baloteli");
        if(c.moveToNext()) assertEquals(c.getString(1), "Kaka");
        if(c.moveToNext()) assertEquals(c.getString(1), "Andrea Pirlo");
        if(c.moveToNext()) assertEquals(c.getString(1), "Null Player");

        c.close();
    }

    public void testSelect_EnumerableCursor(){
        long kakaId = getCustomerId("Kaka");
        long pirloId = getCustomerId("Andrea Pirlo");

        customerTable.select(kakaId, pirloId).orderBy("Name").query(new IEnumerable<Cursor>() {
            int counter = 0;

            @Override
            public void next(Cursor cursor) {
                // pirlo goes first
                if(counter == 0)
                    assertEquals("Andrea Pirlo", cursor.getString(cursor.getColumnIndex("Name")));
                else
                    assertEquals("Kaka", cursor.getString(cursor.getColumnIndex("Name")));

                counter++;
            }
        });
    }

    public void testSelect_Condition(){
        Cursor c = customerTable.select("Name = 'Lionel Messi'").columns("Name").query();
        c.moveToFirst();

        assertTrue(c != null);
        assertTrue(c.getString(0).equals("Lionel Messi"));

        c = customerTable.select("Name = 'Mario Baloteli' AND Country = 'Italy'").columns("Name", "Country").query();
        c.moveToFirst();

        assertTrue(c != null);
        assertTrue(c.getString(0).equals("Mario Baloteli"));
        assertTrue(c.getString(1).equals("Italy"));

        c.close();
    }

    public void testSelect_WhereClause(){
        Cursor c = customerTable.select("Name = ?", "Lionel Messi").columns("Name").query();
        c.moveToFirst();

        assertTrue(c != null);
        assertTrue(c.getString(0).equals("Lionel Messi"));

        c = customerTable.select("Name = ? AND Country = ?", "Mario Baloteli", "Italy").columns("Name", "Country").query();
        c.moveToFirst();

        assertTrue(c != null);
        assertTrue(c.getString(0).equals("Mario Baloteli"));
        assertTrue(c.getString(1).equals("Italy"));

        c.close();
    }

    public void testSelect_Top(){
        Cursor c = db.get("Orders")
                .select(2, "CustomerId = ?", getCustomerId("Christiano Ronaldo")).query();
        c.moveToFirst();
        assertTrue(c.getCount() == 2);
        c.close();
    }

    public void testSelect_Top_Condition(){
        int top = 3;
        long customerId = getCustomerId("Christiano Ronaldo");
        Cursor c = db.get("Orders")
                .select(3, "CustomerId = " + customerId).query();
        c.moveToFirst();
        assertTrue(c.getCount() == 3);
        c.close();
    }

    public void testSelect_Top_WhereClause(){
        int top = 2;
        long customerId = getCustomerId("Christiano Ronaldo");
        Cursor c = db.get("Orders")
                .select(top, "CustomerId = ?", customerId).query();
        c.moveToFirst();
        assertTrue(c.getCount() == top);
        c.close();
    }

    public void testSelectDistinct(){
        Cursor cursor = db.get("Customers")
                .selectDistinct()
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() >= 6);
        cursor.close();
    }

    public void testSelectDistinct_Condition(){
        long pirloId = getCustomerId("Andrea Pirlo");
        long kakaId = getCustomerId("Kaka");
        Cursor cursor = db.get("Orders")
                .selectDistinct("CustomerId = " + kakaId + " OR CustomerId = " + pirloId)
                .columns("CustomerId")
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 2);
        cursor.close();
    }

    public void testSelectDistinct_WhereClause(){
        long pirloId = getCustomerId("Andrea Pirlo");
        long baloteliId = getCustomerId("Mario Baloteli");
        Cursor cursor = db.get("Orders")
                .selectDistinct("CustomerId IN (?,?)", pirloId, baloteliId)
                .columns("CustomerId")
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 2);
        cursor.close();
    }

    public void testSelectDistinct_Top(){
        Cursor cursor = db.get("Customers")
                .selectDistinct(2)
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 2);
        cursor.close();
    }

    public void testSelectDistinct_TopCondition(){
        long pirloId = getCustomerId("Andrea Pirlo");
        long kakaId = getCustomerId("Kaka");
        Cursor cursor = db.get("Orders")
                .selectDistinct(1, "CustomerId = " + kakaId + " OR CustomerId = " + pirloId)
                .columns("CustomerId")
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 1);
        cursor.close();

        cursor = db.get("Orders")
                .selectDistinct(3, "CustomerId not null")
                .columns("CustomerId")
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 3);
        cursor.close();
    }

    public void testSelectDistinct_TopWhereClause(){
        long pirloId = getCustomerId("Andrea Pirlo");
        long baloteliId = getCustomerId("Mario Baloteli");
        Cursor cursor = db.get("Orders")
                .selectDistinct(1, "CustomerId IN (?,?)", pirloId, baloteliId)
                .columns("CustomerId")
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 1);
        cursor.close();

        cursor = db.get("Orders")
                .selectDistinct(3, "CustomerId <> ?", -1)
                .columns("CustomerId")
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 3);
        cursor.close();
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ---------Insert And Delete ----------------//
    public void testOrderBy(){
        List<Integer> list = new LinkedList<Integer>();
        Cursor cursor = db.get("Orders").selectDistinct(null).columns("CustomerId").orderBy("CustomerId").query();
        while(cursor.moveToNext()){
            list.add(cursor.getInt(0));
        }
        cursor.close();

        for(int i = 1; i < list.size(); i++){
            assertTrue(list.get(i-1) < list.get(i));
        }
    }

    public void testOrderBy_2(){
        List<Integer> list = new LinkedList<Integer>();
        Cursor cursor = db.get("Orders").selectDistinct("CustomerId not null").columns("CustomerId").orderBy("CustomerId").query();
        while(cursor.moveToNext()){
            list.add(cursor.getInt(0));
        }
        cursor.close();

        for(int i = 1; i < list.size(); i++){
            assertTrue(list.get(i-1) < list.get(i));
        }
    }

    public void testOrderDescendingBy(){
        List<Integer> list = new LinkedList<Integer>();
        Cursor cursor = db.get("Products").selectDistinct(null).columns("Price").orderBy("Price DESC").query();
        while(cursor.moveToNext()){
            list.add(cursor.getInt(0));
        }
        cursor.close();

        for(int i = 1; i < list.size(); i++){
            assertTrue(list.get(i-1) > list.get(i));
        }
    }

    public void testOrderDescendingBy_2(){
        List<Integer> list = new LinkedList<Integer>();
        Cursor cursor = db.get("Products").selectDistinct("Price is not ?", (Object)null).columns("Price").orderBy("Price DESC").query();
        while(cursor.moveToNext()){
            list.add(cursor.getInt(0));
        }
        cursor.close();

        for(int i = 1; i < list.size(); i++){
            assertTrue(list.get(i-1) > list.get(i));
        }
    }


    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ---------Insert And Delete ----------------//

    long dodolId = -1;
    public void testInsert_Columns(){
        dodolId = db.get("Products")
                .insertInto("Name", "Price")
                .val("Dodol", 22)
                .query();
        assertTrue(dodolId > 0);
        assertTrue(db.get("Products").delete("Name = ?", "Dodol").query() > 0);
    }

    public void testInsert_ContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", "Dodol");
        contentValues.put("Price", 22);

        dodolId = db.get("Products").insert(contentValues).query();
        assertTrue(dodolId > 0);
        assertTrue(db.get("Products").delete("Name = ?", "Dodol").query() > 0);
    }

    public void testInsert_UsingArray(){
        String[] columns = new String[]{"Name", "Price"};
        Object[] values = new Object[]{"Dodol", 33};

        dodolId = db.get("Products").insert(columns, values).query();
        assertTrue(dodolId > 0);
        assertTrue(db.get("Products").delete("Name = ?", "Dodol").query() > 0);
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Update ----------------//

    public void testUpdate_Empty(){
        assertEquals(0, (int) db.get("Customers").update().query());
    }

    public void testUpdate_Id(){
        long ronaldoId = getCustomerId("Christiano Ronaldo");

        int updated = db.get("Customers").update(ronaldoId).columns("PostalCode").val(123).query();
        assertTrue(1 == updated);

        Cursor cursor = db.get("Customers").select(ronaldoId).columns("PostalCode").query();
        cursor.moveToNext();
        assertEquals(123, cursor.getInt(0));
    }

    public void testUpdate_WhereClause(){
        // update 2 countries customers
        assertEquals(2, (int) db.get("Customers").update("Name = ? OR Name = ?", "Christiano Ronaldo", "Lionel Messi").columns("Country").val("US").query());

        // change them back
        assertEquals(1, (int) db.get("Customers").update("Name = ?", "Christiano Ronaldo").columns("Country").val("Spain").query());
        assertEquals(1, (int) db.get("Customers").update("Name = ?", "Lionel Messi").columns("Country").val("Spain").query());
    }

    public void testUpdate_Condition(){
        // update 2 countries customers
        assertEquals(2, (int) db.get("Customers").update("Name = 'Christiano Ronaldo' OR Name = 'Lionel Messi'").columns("Country").val("US").query());

        // change them back
        assertEquals(1, (int) db.get("Customers").update("Name = 'Christiano Ronaldo'").columns("Country").val("Spain").query());
        assertEquals(1, (int) db.get("Customers").update("Name = 'Lionel Messi'").columns("Country").val("Spain").query());
    }

    public void testUpdate_WhereClause_ContentValues(){
        ContentValues v = new ContentValues();
        v.put("Country", "US");
        v.put("City", "City");

        long ronaldoId = getCustomerId("Christiano Ronaldo");
        long messiId = getCustomerId("Lionel Messi");

        // update 2 countries customers
        assertEquals(2, (int) db.get("Customers").update("Name = ? OR Name = ?", "Christiano Ronaldo", "Lionel Messi").val(v).query());

        // check the updated
        Cursor c = db.get("Customers").select(ronaldoId).columns("Country", "City").query();
        c.moveToNext();
        assertEquals("US", c.getString(0));
        assertEquals("City", c.getString(1));
        c.close();

        // change them back
        assertEquals(1, (int) db.get("Customers").update("Name = 'Christiano Ronaldo'").columns("Country", "City").val("Spain", "Madrid").query());
        assertEquals(1, (int) db.get("Customers").update("Name = 'Lionel Messi'").columns("Country", "City").val("Spain", "Barcelona").query());
    }

    public void testUpdate_Ints(){
        long ronaldoId = getCustomerId("Christiano Ronaldo");
        long messiId = getCustomerId("Lionel Messi");

        assertEquals(2, (int) db.get("Customers").update(ronaldoId, messiId).columns("Country").val("US").query());

        // change them back
        assertEquals(1, (int) db.get("Customers").update("Name = 'Christiano Ronaldo'").columns("Country").val("Spain").query());
        assertEquals(1, (int) db.get("Customers").update("Name = 'Lionel Messi'").columns("Country").val("Spain").query());
    }

    public void testUpdate_ContentValues_And_WithId(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("PostalCode", 123456);
        contentValues.put("Address", "Whatever Street");

        long crId = db.get("Customers").selectId("Name = ?", "Christiano Ronaldo");
        int numUpdated = db.get("Customers").update(contentValues, "Name = ?", "Christiano Ronaldo").query();
        assertTrue(numUpdated > 0);
    }

    public void testUpdate_SingleColumns(){
        long ronaldoId = getCustomerId("Christiano Ronaldo");

        assertEquals(1, (int) db.get("Customers").update(ronaldoId).val("Country", "US").query());
        assertEquals(0, (int) db.get("Customers").update(-1).val("Country", "US").query());
    }

    public void testUpdate_MultipleColumns(){
        long ronaldoId = getCustomerId("Christiano Ronaldo");

        assertEquals(1, (int) db.get("Customers").update(ronaldoId).val(new String[]{"Country", "City"}, new Object[]{"Country", "City"}).query());
        assertEquals(0, (int) db.get("Customers").update(-1).val(new String[]{"Country", "City"}, new Object[]{"Country", "City"}).query());
    }

    public void testDelete_All(){
        assertTrue(db.get("Customers").count() > 0);
        assertTrue(db.get("Products").count() > 0);
        assertTrue(db.get("Orders").count() > 0);

        db.get("Customers").delete();
        db.get("Products").delete();
        db.get("Orders").delete();

        assertEquals(0, db.get("Customers").count());
        assertEquals(0, db.get("Products").count());
        assertEquals(0, db.get("Orders").count());

        populated = false;  // repopulated
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Null tests ----------------//
    public void testNullValues_AllTests(){
        // select id
        assertTrue(db.get("Customers").selectId("Name is not null And Address is null") > 0);
        assertTrue(db.get("Customers").selectId("Name LIKE ? AND Address is ?", "%player%", null) > 0);

        // count
        assertTrue(db.get("Customers").count("Name is not null And Address is null") > 0);
        assertTrue(db.get("Customers").count("Name LIKE ? AND Address is ?", "%player%", null) > 0);
        // has row
        assertTrue(db.get("Customers").has("Name is not null And Address is null"));
        assertTrue(db.get("Customers").has("Name like ? And Address is null", "%player%", null));

        // select
        testNullCursor(db.get("Customers").select("Address is null").query());
        testNullCursor(db.get("Customers").select("Address is ?", (Object)null).query());
        testNullCursor(db.get("Customers").select("Name LIKE ? AND Address is null", "%player%").query());
        testNullCursor(db.get("Customers").select("Name LIKE ? AND Address is ?", "%player%", null).query());
        // select distinct
        testNullCursor(db.get("Customers").selectDistinct("Address is null").query());
        testNullCursor(db.get("Customers").selectDistinct("Address is ?", (Object)null).query());
        testNullCursor(db.get("Customers").selectDistinct("Name LIKE ? AND Address is null", "%player%").query());
        testNullCursor(db.get("Customers").selectDistinct("Name LIKE ? AND Address is ?", "%player%", null).query());


        long rowNullId = db.get("Customers").insertInto("Name", "Address").val("TestNull", null).query();
        assertTrue(rowNullId > 0);
        assertTrue(db.get("Customers").delete("Name = ? AND Address is ?", "TestNull", null).query() > 0);
        assertFalse(db.get("Customers").has(rowNullId));

    }

    private void testNullCursor(Cursor cursor){
        cursor.moveToFirst();
        assertTrue(cursor != null);
        assertTrue(cursor.getCount() == 1);
        cursor.close();
    }


    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ JOIN ----------------//

    public void testJoin(){
        Cursor c = db.get("Orders").join("Customers", "Customers.Id = Orders.CustomerId")
                .query();
        assertTrue(c.getCount() > 0);
        c.close();

        c = db.get("Orders").join("Customers", "Customers.Id = Orders.CustomerId")
                .select("Customers.Name LIKE ?", "%Messi%").query();
        assertTrue(c.getCount() > 0);
        c.close();
    }

    public void testJoin_WithAlias(){
        Cursor c = db.get("Orders O").join("Customers", "Customers.Id = O.CustomerId")
                .select("Customers.Name LIKE ?", "%Messi%").query();
        assertTrue(c.getCount() > 0);
        c.close();

        c = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .select("C.Name LIKE ?", "%Messi%").query();
        assertTrue(c.getCount() > 0);
        c.close();
    }

    public void testJoin_Select(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .select(3, "C.Name = ?", "Christiano Ronaldo").query();
        assertTrue(cursor.getCount() == 3);
        cursor.close();
    }

    public void testJoin_SelectWithColumns(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .select(3, "C.Name = ?", "Christiano Ronaldo")
                .columns("C.Name", "O.Date")
                .query();
        assertTrue(cursor.getCount() == 3);

        // iterate throught
        while(cursor.moveToNext()){
            assertTrue(cursor.getString(0).equalsIgnoreCase("Christiano Ronaldo"));
        }

        cursor.close();
    }

    public void testJoin_Select_Id(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .select(3, "C.Name = ?", "Christiano Ronaldo")
                .columns("O.Id, C.Id")
                .query();
        assertTrue(cursor.getCount() == 3);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();

        cursor = db.get("Orders").select(id).columns("Id", "CustomerId").query();
        assertTrue(cursor.getCount() == 1);

        cursor.moveToFirst();
        assertEquals(cursor.getInt(0), id);
        assertEquals(cursor.getInt(1), getCustomerId("Christiano Ronaldo"));
    }

    public void testJoin_Select_Top_Condition(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .select(2, "C.Name = 'Christiano Ronaldo'").query();
        assertTrue(cursor.getCount() == 2);
        cursor.close();
    }

    public void testJoin_Select_Top_WhereClause(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .select(3, "C.Name = ?", "Lionel Messi").query();
        assertTrue(cursor.getCount() == 3);
        cursor.close();
    }

    public void testJoin_Select_Condition(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .select("C.Name = 'Mario Baloteli'")
                .columns("C.Name AS CustomerName")
                .query();
        assertTrue(cursor.getCount() > 0);
        while(cursor.moveToNext()){
            assertEquals(cursor.getString(0), "Mario Baloteli");
        }
        cursor.close();
    }

    public void testJoin_Select_WhereClause(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .select("C.Name = ?", "Lionel Messi")
                .columns("C.Name AS CustomerName")
                .query();
        assertTrue(cursor.getCount() > 0);
        while(cursor.moveToNext()){
            assertEquals(cursor.getString(0), "Lionel Messi");
        }
        cursor.close();
    }

    public void testJoin_SelectDistinct_All(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .selectDistinct()
                .columns("C.Name AS CustomerName")
                .orderBy("O.Id")
                .query();
        assertTrue(cursor.getCount() == 6);
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Wayne Rooney");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Lionel Messi");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Christiano Ronaldo");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Mario Baloteli");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Kaka");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Andrea Pirlo");
        cursor.close();
    }

    public void testJoin_SelectDistinct_Condition(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .selectDistinct("Name <> 'Kaka'")
                .columns("C.Name AS CustomerName")
                .orderBy("O.Id")
                .query();
        assertTrue(cursor.getCount() == 5);
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Wayne Rooney");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Lionel Messi");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Christiano Ronaldo");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Mario Baloteli");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Andrea Pirlo");
        cursor.close();
    }

    public void testJoin_SelectDistinct_WhereClause(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .selectDistinct("Name NOT IN (?,?)", "Wayne Rooney", "Kaka")
                .columns("C.Name AS CustomerName")
                .orderBy("O.Id")
                .query();
        assertTrue(cursor.getCount() == 4);
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Lionel Messi");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Christiano Ronaldo");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Mario Baloteli");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Andrea Pirlo");
        cursor.close();
    }

    public void testJoin_SelectDistinct_Top(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .selectDistinct(3)
                .columns("C.Name AS CustomerName")
                .orderBy("O.Id")
                .query();
        assertTrue(cursor.getCount() == 3);
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Wayne Rooney");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Lionel Messi");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Christiano Ronaldo");
        cursor.close();
    }

    public void testJoin_SelectDistinct_Top_Condition(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .selectDistinct(3, "Name <> 'Wayne Rooney'")
                .columns("C.Name AS CustomerName")
                .orderBy("O.Id")
                .query();
        assertTrue(cursor.getCount() == 3);
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Lionel Messi");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Christiano Ronaldo");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Mario Baloteli");
        cursor.close();
    }

    public void testJoin_SelectDistinct_Top_WhereClause(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId")
                .selectDistinct(3, "Name <> ?", "Christiano Ronaldo")
                .columns("C.Name AS CustomerName")
                .orderBy("O.Id")
                .query();
        assertTrue(cursor.getCount() == 3);
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Wayne Rooney");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Lionel Messi");
        if(cursor.moveToNext()) assertEquals(cursor.getString(0), "Mario Baloteli");
        cursor.close();
    }

    public void testRaw(){
        // inception
        Cursor c = db.get("Orders O").raw("SELECT * FROM Orders").query();
        assertTrue(c.getCount() > 0);
        c.close();

        try{
            c = db.get("Orders O").raw("SELECT * FROM Orders WHERE Name is invalidy syntax").query();
            assertFalse("Should throw an exception", true);
        }
        catch (Exception e){
            assertTrue(true);
        }
        finally {
            c.close();
        }
    }

    public void testRaw_Args(){
        // inception
        Cursor c = db.get("Orders O").raw("SELECT * FROM Customers WHERE Name = ?", "Lionel Messi").query();
        assertTrue(c.getCount() > 0);
        c.close();

        // inception
        c = db.get("Orders O").raw("SELECT * FROM Customers WHERE Name = ?", "Lionel Baloteli").query();
        assertTrue(c.getCount() == 0);
        c.close();
    }


    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Functions ----------------//

    public void testAvg(){
        ITable productTable = db.get("Products");

        Object average = productTable.avg("Price").asDouble();
        assertEquals((Double) average, 2688.1, 0.01); // this number may change

        average = productTable.avg("Price").asFloat();
        assertEquals((Float) average, 2688.1, 0.01);

        average = productTable.avg("Price").asInt();
        assertEquals(average, 2688);

        average = productTable.avg("Price").asLong();
        assertEquals(average, (long) 2688);

        average = productTable.avg("Price").value();
        assertEquals((Double) average, 2688.1, 0.01);

        average = productTable.avg("Price").asString();
        assertEquals(average, "2688.1");
    }

    public void testAvg_Condition(){
        ITable productTable = db.get("Products");

        Object average = productTable.avg("Price", "Price > 100").asDouble();
        assertEquals((Double) average, (double) 5340, 0.01); // this number may change

        average = productTable.avg("Price", "Price > 100").asFloat();
        assertEquals((Float) average, (float) 5340, 0.01);

        average = productTable.avg("Price", "Price > 100").asInt();
        assertEquals(average, 5340);

        average = productTable.avg("Price", "Price > 100").asLong();
        assertEquals(average, (long) 5340);

        average = productTable.avg("Price", "Price > 100").value();
        assertEquals((Double) average, 5340, 0.1);

        average = productTable.avg("Price", "Price > 100").asString();
        assertEquals(average, "5340.0");
    }

    public void testAvg_WhereClause(){
        ITable productTable = db.get("Products");

        Object average = productTable.avg("Price", "Price > ?", 100).asDouble();
        assertEquals((Double) average, (double) 5340, 0.01); // this number may change

        average = productTable.avg("Price", "Price > ?", 100).asFloat();
        assertEquals((Float) average, (float) 5340, 0.01);

        average = productTable.avg("Price", "Price > ?", 100).asInt();
        assertEquals(average, 5340);

        average = productTable.avg("Price", "Price > ?", 100).asLong();
        assertEquals(average, (long) 5340);

        average = productTable.avg("Price", "Price > ?", 100).value();
        assertEquals((Double) average, 5340, 0.1);

        average = productTable.avg("Price", "Price > ?", 100).asString();
        assertEquals(average, "5340.0");
    }

    public void testSum(){
        ITable productTable = db.get("Products");

        Object average = productTable.sum("Price").asDouble();
        assertEquals((Double) average, 26881.0, 0.01); // this number may change

        average = productTable.sum("Price").asFloat();
        assertEquals((Float) average, (float) 26881, 0.01);

        average = productTable.sum("Price").asInt();
        assertEquals(average, 26881);

        average = productTable.sum("Price").asLong();
        assertEquals(average, (long) 26881);

        average = productTable.sum("Price").value();
        assertEquals((Double) average, 26881, 0.1);

        average = productTable.sum("Price").asString();
        assertEquals(average, "26881.0");
    }

    public void testSum_Condition(){
        ITable productTable = db.get("Products");

        Object average = productTable.sum("Price", "Name like 'C%'").asDouble();
        assertEquals((Double) average, 21201.0, 0.01); // this number may change

        average = productTable.sum("Price", "Name like 'C%'").asFloat();
        assertEquals((Float) average, (float) 21201, 0.01);

        average = productTable.sum("Price", "Name like 'C%'").asInt();
        assertEquals(average, 21201);

        average = productTable.sum("Price", "Name like 'C%'").asLong();
        assertEquals(average, (long) 21201);

        average = productTable.sum("Price", "Name like 'C%'").value();
        assertEquals((Double) average, 21201, 0.1);

        average = productTable.sum("Price", "Name like 'C%'").asString();
        assertEquals(average, "21201.0");
    }

    public void testSum_WhereClause(){
        ITable productTable = db.get("Products");

        Object average = productTable.sum("Price", "Name like ?", "C%").asDouble();
        assertEquals((Double) average, 21201.0, 0.01); // this number may change

        average = productTable.sum("Price", "Name like ?", "C%").asFloat();
        assertEquals((Float) average, (float) 21201, 0.01);

        average = productTable.sum("Price", "Name like ?", "C%").asInt();
        assertEquals(average, 21201);

        average = productTable.sum("Price", "Name like ?", "C%").asLong();
        assertEquals(average, (long) 21201);

        average = productTable.sum("Price", "Name like ?", "C%").value();
        assertEquals((Double) average, 21201, 0.1);

        average = productTable.sum("Price", "Name like ?", "C%").asString();
        assertEquals(average, "21201.0");
    }


    public void testTotal(){
        ITable productTable = db.get("Products");

        Object average = productTable.total("Price").asDouble();
        assertEquals((Double) average, 26881.0, 0.01); // this number may change

        average = productTable.total("Price").asFloat();
        assertEquals((Float) average, (float) 26881, 0.01);

        average = productTable.total("Price").asInt();
        assertEquals(average, 26881);

        average = productTable.total("Price").asLong();
        assertEquals(average, (long) 26881);

        average = productTable.total("Price").value();
        assertEquals((Double) average, 26881, 0.1);

        average = productTable.total("Price").asString();
        assertEquals(average, "26881.0");
    }

    public void testTotal_Condition(){
        ITable productTable = db.get("Products");

        Object average = productTable.total("Price", "Name like 'C%'").asDouble();
        assertEquals((Double) average, 21201.0, 0.01); // this number may change

        average = productTable.total("Price", "Name like 'C%'").asFloat();
        assertEquals((Float) average, (float) 21201, 0.01);

        average = productTable.total("Price", "Name like 'C%'").asInt();
        assertEquals(average, 21201);

        average = productTable.total("Price", "Name like 'C%'").asLong();
        assertEquals(average, (long) 21201);

        average = productTable.total("Price", "Name like 'C%'").value();
        assertEquals((Double) average, 21201, 0.1);

        average = productTable.total("Price", "Name like 'C%'").asString();
        assertEquals(average, "21201.0");
    }

    public void testTotal_WhereClause(){
        ITable productTable = db.get("Products");

        Object average = productTable.total("Price", "Name like ?", "C%").asDouble();
        assertEquals((Double) average, 21201.0, 0.01); // this number may change

        average = productTable.total("Price", "Name like ?", "C%").asFloat();
        assertEquals((Float) average, (float) 21201, 0.01);

        average = productTable.total("Price", "Name like ?", "C%").asInt();
        assertEquals(average, 21201);

        average = productTable.total("Price", "Name like ?", "C%").asLong();
        assertEquals(average, (long) 21201);

        average = productTable.total("Price", "Name like ?", "C%").value();
        assertEquals((Double) average, 21201, 0.1);

        average = productTable.sum("Price", "Name like ?", "C%").asString();
        assertEquals(average, "21201.0");
    }

    public void testMax(){
        ITable productTable = db.get("Products");

        Object average = productTable.max("Price").asDouble();
        assertEquals((Double) average, 20000.0, 0.01); // this number may change

        average = productTable.max("Price").asFloat();
        assertEquals((Float) average, (float) 20000, 0.01);

        average = productTable.max("Price").asInt();
        assertEquals(average, 20000);

        average = productTable.max("Price").asLong();
        assertEquals(average, (long) 20000);

        average = productTable.max("Price").value();
        assertEquals((Double) average, 20000, 0.1);

        average = productTable.max("Price").asString();
        assertEquals(average, "20000.0");
    }

    public void testMax_Condition(){
        ITable productTable = db.get("Products");

        Object average = productTable.max("Price", "Price < 2").asDouble();
        assertEquals((Double) average, 1.0, 0.01); // this number may change

        average = productTable.max("Price", "Price < 2").asFloat();
        assertEquals((Float) average, (float) 1, 0.01);

        average = productTable.max("Price", "Price < 2").asInt();
        assertEquals(average, 1);

        average = productTable.max("Price", "Price < 2").asLong();
        assertEquals(average, (long) 1);

        average = productTable.max("Price", "Price < 2").value();
        assertEquals((Double) average, 1, 0.1);

        average = productTable.max("Price", "Price < 2").asString();
        assertEquals(average, "1.0");
    }

    public void testMax_Where(){
        ITable productTable = db.get("Products");

        Object average = productTable.max("Price", "Price < ?", 2).asDouble();
        assertEquals((Double)average, 1.0, 0.1); // this number may change

        average = productTable.max("Price", "Price < ?", 2).asFloat();
        assertEquals((Float)average, (float) 1, 0.1);

        average = productTable.max("Price", "Price < ?", 2).asInt();
        assertEquals(average, 1);

        average = productTable.max("Price", "Price < ?", 2).asLong();
        assertEquals(average, (long) 1);

        average = productTable.max("Price", "Price < ?", 2).value();
        assertEquals((Double) average, 1, 0.1);

        average = productTable.max("Price", "Price < ?", 2).asString();
        assertEquals(average, "1.0");
    }

    public void testMin(){
        ITable productTable = db.get("Products");

        Object average = productTable.min("Price").asDouble();
        assertEquals((Double) average, 1.0, 0.01); // this number may change

        average = productTable.min("Price").asFloat();
        assertEquals((Float) average, (float) 1, 0.01);

        average = productTable.min("Price").asInt();
        assertEquals(average, 1);

        average = productTable.min("Price").asLong();
        assertEquals(average, (long) 1);

        average = productTable.min("Price").value();
        assertEquals((Double) average, 1, 0.1);

        average = productTable.min("Price").asString();
        assertEquals(average, "1.0");
    }

    public void testMin_Condition(){
        ITable productTable = db.get("Products");

        Object average = productTable.min("Price", "Price > 101").asDouble();
        assertEquals((Double) average, 200.0, 0.01); // this number may change

        average = productTable.min("Price", "Price > 101").asFloat();
        assertEquals((Float) average, (float) 200, 0.01);

        average = productTable.min("Price", "Price > 101").asInt();
        assertEquals(average, 200);

        average = productTable.min("Price", "Price > 101").asLong();
        assertEquals(average, (long) 200);

        average = productTable.min("Price", "Price > 101").value();
        assertEquals((Double) average, 200, 0.1);

        average = productTable.min("Price", "Price > 101").asString();
        assertEquals(average, "200.0");
    }

    public void testMin_WhereClause(){
        ITable productTable = db.get("Products");

        Object average = productTable.min("Price", "Price > ?", 101).asDouble();
        assertEquals((Double) average, 200.0, 0.01); // this number may change

        average = productTable.min("Price", "Price > ?", 101).asFloat();
        assertEquals((Float) average, (float) 200, 0.01);

        average = productTable.min("Price", "Price > ?", 101).asInt();
        assertEquals(average, 200);

        average = productTable.min("Price", "Price > ?", 101).asLong();
        assertEquals(average, (long) 200);

        average = productTable.min("Price", "Price > ?", 101).value();
        assertEquals((Double) average, 200, 0.1);

        average = productTable.min("Price", "Price > ?", 101).asString();
        assertEquals(average, "200.0");
    }

    public void testDrop(){
        if(db.get("TableToDrop") != null){
            assertTrue(db.get("TableToDrop").drop().query());
            assertNull(db.get("TableToDrop"));
        }
    }


    /////////////////////////////////////////////////////////////////////

    /**
     * Do modeling here
     * @param modeling
     */
    private void createDatabaseModeling(IDatabase.Modeling modeling){
        modeling.add("Customers")
                .addPrimaryKey("Id")
                .addText("Name", "not null")
                .addText("Address")
                .addText("City")
                .addText("PostalCode")
                .addText("Country");

        modeling.add("Products")
                .addPrimaryKey("Id")
                .addText("Name")
                .addReal("Price");

        modeling.add("Orders")
                .addPrimaryKey("Id")
                .addInteger("CustomerId")
                .addInteger("ProductId")
                .add("Date", "TEXT");

        modeling.add("TableToDrop")
                .addPrimaryKey("Id");
    }


    private void populateData(){
        InsertInto insert = db.get("Customers").insertInto("Name", "Address", "City", "PostalCode", "Country");

        insert.val("Wayne Rooney", "10 Manchester United", "Manchester", 9812, "UK");
        insert.val("Lionel Messi", "10 Barcelona st.", "Barcelona", 70, "Spain");
        insert.val("Christiano Ronaldo", "7 Real Madrid", "Madrid", 5689, "Spain");
        insert.val("Mario Baloteli", "9 Ac Milan St.", "Milan", 1899, "Italy");
        insert.val("Kaka", "15 Ac Milan St.", "Milan", 1899, "Italy");
        insert.val("Andrea Pirlo", "21 Juventus St.", "Turin", 1899, "Italy");
        insert.val("Null Player", null, null, 22111, "US");


        insert = db.get("Products").insertInto("Name", "Price");
        insert.val("Car", 20000);
        insert.val("Motorcycle", 5000);
        insert.val("Computer", 1000);
        insert.val("Monitor", 500);
        insert.val("Keyboard and Mouse", 10);
        insert.val("Cellphone", 200);
        insert.val("Sunglasses", 50);
        insert.val("Desk", 100);
        insert.val("Lamp", 20);
        insert.val("Candy", 1);

        insert = db.get("Orders").insertInto("CustomerId", "ProductId", "Date");
        insert.val(getCustomerId("Wayne Rooney"), getProductId("Computer"), getRandomDate());
        insert.val(getCustomerId("Wayne Rooney"), getProductId("Monitor"), getRandomDate());
        insert.val(getCustomerId("Wayne Rooney"), getProductId("Cellphone"), getRandomDate());
        insert.val(getCustomerId("Wayne Rooney"), getProductId("Desk"), getRandomDate());
        insert.val(getCustomerId("Lionel Messi"), getProductId("Car"), getRandomDate());
        insert.val(getCustomerId("Lionel Messi"), getProductId("Desk"), getRandomDate());
        insert.val(getCustomerId("Lionel Messi"), getProductId("Computer"), getRandomDate());
        insert.val(getCustomerId("Lionel Messi"), getProductId("Lamp"), getRandomDate());
        insert.val(getCustomerId("Christiano Ronaldo"), getProductId("Candy"), getRandomDate());
        insert.val(getCustomerId("Christiano Ronaldo"), getProductId("Lamp"), getRandomDate());
        insert.val(getCustomerId("Christiano Ronaldo"), getProductId("Sunglasses"), getRandomDate());
        insert.val(getCustomerId("Christiano Ronaldo"), getProductId("Candy"), getRandomDate());
        insert.val(getCustomerId("Christiano Ronaldo"), getProductId("Keyboard and Mouse"), getRandomDate());
        insert.val(getCustomerId("Christiano Ronaldo"), getProductId("Sunglasses"), getRandomDate());
        insert.val(getCustomerId("Mario Baloteli"), getProductId("Candy"), getRandomDate());
        insert.val(getCustomerId("Mario Baloteli"), getProductId("Monitor"), getRandomDate());
        insert.val(getCustomerId("Mario Baloteli"), getProductId("Computer"), getRandomDate());
        insert.val(getCustomerId("Mario Baloteli"), getProductId("Sunglasses"), getRandomDate());
        insert.val(getCustomerId("Mario Baloteli"), getProductId("Car"), getRandomDate());
        insert.val(getCustomerId("Kaka"), getProductId("Car"), getRandomDate());
        insert.val(getCustomerId("Kaka"), getProductId("Computer"), getRandomDate());
        insert.val(getCustomerId("Kaka"), getProductId("Cellphone"), getRandomDate());
        insert.val(getCustomerId("Kaka"), getProductId("Sunglasses"), getRandomDate());
        insert.val(getCustomerId("Kaka"), getProductId("Car"), getRandomDate());
        insert.val(getCustomerId("Andrea Pirlo"), getProductId("Car"), getRandomDate());
        insert.val(getCustomerId("Andrea Pirlo"), getProductId("Computer"), getRandomDate());
        insert.val(getCustomerId("Andrea Pirlo"), getProductId("Cellphone"), getRandomDate());
        insert.val(getCustomerId("Andrea Pirlo"), getProductId("Sunglasses"), getRandomDate());
        insert.val(getCustomerId("Andrea Pirlo"), getProductId("Computer"), getRandomDate());
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Helper methods ----------------//
    private long getCustomerId(String name){
        return db.get("Customers").selectId("Name = ?", name);
    }

    private long getProductId(String name){
        return db.get("Products").selectId("Name = ?", name);
    }

    private String getRandomDate(){
        long now = Helper.now();
        now += Helper.getRandom(-1000, 0);

        Date date = new Date(now);
        return new SimpleDateFormat("MM/DD/yyyy").format(date);
    }
}
