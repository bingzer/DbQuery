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

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.ITable;

/**
 * Created by Ricky Tobing on 7/18/13.
 */
public class ITableTest extends AndroidTestCase{

    ITable table;
    IDatabase db;

    public void setUp(){
        db = DbQuery.getDatabase("TestDb");
        table = db.get("Customers");
    }

    public void testGetName() throws Exception {
        assertTrue(table.getName().equalsIgnoreCase("Customers"));
    }

    public void testGetColumns(){
        for(String s : table.getColumns()){
            assertTrue(s != null);
        }
    }

    public void testGetColumnCount() {
        assertTrue(table.getColumnCount() > 0);
        assertTrue(table.getColumnCount() == table.getColumns().size());
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ SELECT ----------------//

    public void testSelect_Id(){
        int messiId = getCustomerId("Lionel Messi");

        Cursor c = table.select(messiId).query();
        c.moveToFirst();
        assertTrue(c != null);
        assertTrue(c.getString(c.getColumnIndex("Name")) != null);  // name never null
        c.close();
    }

    public void testSelect_Ids(){
        int messiId = getCustomerId("Lionel Messi");
        int crId = getCustomerId("Christiano Ronaldo");
        Cursor c = table.select(messiId, crId).query();
        assertTrue(c.getCount() == 2);
        while(c.moveToNext()){
            assertTrue(
                c.getString(c.getColumnIndex("Name")).equalsIgnoreCase("Lionel Messi") ||
                c.getString(c.getColumnIndex("Name")).equalsIgnoreCase("Christiano Ronaldo")
            );
        }
        c.close();
    }

    public void testSelect_WhereClause(){
        Cursor c =table.select("Name = ?", "Lionel Messi").columns("Name").query();
        c.moveToFirst();

        assertTrue(c != null);
        assertTrue(c.getString(0).equals("Lionel Messi"));

        c = table.select("Name = ? AND Country = ?", "Mario Baloteli", "Italy").columns("Name", "Country").query();
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
        int customerId = getCustomerId("Christiano Ronaldo");
        Cursor c = db.get("Orders")
                .select(3, "CustomerId = " + customerId).query();
        c.moveToFirst();
        assertTrue(c.getCount() == 3);
        c.close();
    }

    public void testSelect_Top_WhereClause(){
        int top = 2;
        int customerId = getCustomerId("Christiano Ronaldo");
        Cursor c = db.get("Orders")
                .select(top, "CustomerId = ?", customerId).query();
        c.moveToFirst();
        assertTrue(c.getCount() == top);
        c.close();
    }

    public void testSelectDistinct_Condition(){
        int pirloId = getCustomerId("Andrea Pirlo");
        int kakaId = getCustomerId("Kaka");
        Cursor cursor = db.get("Orders")
                .selectDistinct("CustomerId = " + kakaId + " OR CustomerId = " + pirloId)
                .columns("CustomerId")
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 2);
        cursor.close();
    }

    public void testSelectDistinct_WhereClause(){
        int pirloId = getCustomerId("Andrea Pirlo");
        int baloteliId = getCustomerId("Mario Baloteli");
        Cursor cursor = db.get("Orders")
                .selectDistinct("CustomerId IN (?,?)", pirloId, baloteliId)
                .columns("CustomerId")
                .query();
        cursor.moveToFirst();
        assertTrue(cursor.getCount() == 2);
        cursor.close();
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Insert ----------------//

    public void testInsert(){
    }



    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Update ----------------//


    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Delete ----------------//


    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ JOIN ----------------//

    public void testJoin(){
        Cursor c = db.get("Orders").join("Customers", "Customers.Id = Orders.CustomerId").query();
        assertTrue(c.getCount() > 0);
        c.close();

        c = db.get("Orders").join("Customers", "Customers.Id = Orders.CustomerId").select("Customers.Name LIKE ?", "%Messi%").query();
        assertTrue(c.getCount() > 0);
        c.close();
    }

    public void testJoin_WithAlias(){
        Cursor c = db.get("Orders O").join("Customers", "Customers.Id = O.CustomerId").select("Customers.Name LIKE ?", "%Messi%").query();
        assertTrue(c.getCount() > 0);
        c.close();

        c = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId").select("C.Name LIKE ?", "%Messi%").query();
        assertTrue(c.getCount() > 0);
        c.close();
    }

    public void testJoin_Select(){
        Cursor cursor = db.get("Orders O").join("Customers C", "C.Id = O.CustomerId").select(3, "C.Name = ?", "Christiano Ronaldo").query();
        assertTrue(cursor.getCount() == 3);
        cursor.close();
    }


    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Functions ----------------//

    public void testAvg(){
        ITable productTable = db.get("Products");

        Object average = productTable.avg("Price").asDouble();
        assertEquals(average, (double) 2688); // this number may change

        average = productTable.avg("Price").asFloat();
        assertEquals(average, (float) 2688);

        average = productTable.avg("Price").asInt();
        assertEquals(average, 2688);

        average = productTable.avg("Price").asLong();
        assertEquals(average, (long) 2688);

        average = productTable.avg("Price").value();
        assertEquals(average, 2688);

        average = productTable.avg("Price").asString();
        assertEquals(average, "2688");
    }

    public void testSum(){
        ITable productTable = db.get("Products");

        Object average = productTable.sum("Price").asDouble();
        assertEquals(average, (double) 26881); // this number may change

        average = productTable.sum("Price").asFloat();
        assertEquals(average, (float) 26881);

        average = productTable.sum("Price").asInt();
        assertEquals(average, 26881);

        average = productTable.sum("Price").asLong();
        assertEquals(average, (long) 26881);

        average = productTable.sum("Price").value();
        assertEquals(average, 26881);

        average = productTable.sum("Price").asString();
        assertEquals(average, "26881");
    }

    public void testMax(){
        ITable productTable = db.get("Products");

        Object average = productTable.max("Price").asDouble();
        assertEquals(average, (double) 20000); // this number may change

        average = productTable.max("Price").asFloat();
        assertEquals(average, (float) 20000);

        average = productTable.max("Price").asInt();
        assertEquals(average, 20000);

        average = productTable.max("Price").asLong();
        assertEquals(average, (long) 20000);

        average = productTable.max("Price").value();
        assertEquals(average, 20000);

        average = productTable.max("Price").asString();
        assertEquals(average, "20000");
    }

    public void testMin(){
        ITable productTable = db.get("Products");

        Object average = productTable.min("Price").asDouble();
        assertEquals(average, (double) 1); // this number may change

        average = productTable.min("Price").asFloat();
        assertEquals(average, (float) 1);

        average = productTable.min("Price").asInt();
        assertEquals(average, 1);

        average = productTable.min("Price").asLong();
        assertEquals(average, (long) 1);

        average = productTable.min("Price").value();
        assertEquals(average, 1);

        average = productTable.min("Price").asString();
        assertEquals(average, "1");
    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////
    // ------------------ Helper methods ----------------//
    private int getCustomerId(String name){
        return db.get("Customers").selectId("Name = ?", name);
    }

    private int getProductId(String name){
        return db.get("Products").selectId("Name = ?", name);
    }
}
