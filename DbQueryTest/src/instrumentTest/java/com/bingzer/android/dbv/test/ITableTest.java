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
        Cursor c = db.get("Orders").select(2, "CustomerId = ?", getCustomerId("Christiano Ronaldo")).query();
        c.moveToFirst();
        assertTrue(c.getCount() == 2);
        c.close();
    }




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
    // ------------------ Helper methods ----------------//
    private int getCustomerId(String name){
        Cursor c =  DbQuery.getDatabase("TestDb").get("Customers").select("Name = ?", name).columns("Id").query();
        try{
            if(c.moveToFirst()) return c.getInt(0);
            assertFalse("No customer found insert name " + name, false);
            return -1;
        }
        finally {
            c.close();
        }
    }

    private int getProductId(String name){
        Cursor c = DbQuery.getDatabase("TestDb").get("Products").select("Name = ?", name).columns("Id").query();
        try{
            if(c.moveToFirst()) return c.getInt(0);
            assertFalse("No product found insert name " + name, false);
            return -1;
        }
        finally {
            c.close();
        }
    }
}
