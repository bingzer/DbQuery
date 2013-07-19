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

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.MigrationMode;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This test must be first test to run
 * Created by Ricky Tobing on 7/18/13.
 */
public class AASetup extends AndroidTestCase{

    IDatabase db;
    @Override
    public void setUp(){
        db = DbQuery.getDatabase("TestDb");
        db.create(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return AASetup.this.getContext();
            }

            @Override
            public MigrationMode getMigrationMode() {
                return MigrationMode.DropIfExists;
            }

            @Override
            public void onCreate(IDatabase.Modeling modeling) {
                createDatabaseModeling(modeling);
            }
        });

        db.get("Customers").delete();
        db.get("Products").delete();
        db.get("Orders").delete();
        populateData();
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

    /**
     * Do modeling here
     * @param modeling
     */
    private void createDatabaseModeling(IDatabase.Modeling modeling){
        modeling.add("Customers")
                .addColumnId("Id")
                .add("Name", "TEXT", "not null")
                .add("Address", "TEXT")
                .add("City", "TEXT")
                .add("PostalCode", "TEXT")
                .add("Country", "TEXT");

        modeling.add("Products")
                .addColumnId("Id")
                .add("Name", "TEXT")
                .add("Price", "REAL");

        modeling.add("Orders")
                .addColumnId("Id")
                .add("CustomerId", "INTEGER")
                .add("ProductId", "INTEGER")
                .add("Date", "TEXT");
    }


    private void populateData(){
        IQuery.InsertWith insert = db.get("Customers").insert("Name", "Address", "City", "PostalCode", "Country");

        insert.val("Wayne Rooney", "10 Manchester United", "Manchester", 9812, "UK");
        insert.val("Lionel Messi", "10 Barcelona st.", "Barcelona", 70, "Spain");
        insert.val("Christiano Ronaldo", "7 Real Madrid", "Madrid", 5689, "Spain");
        insert.val("Mario Baloteli", "9 Ac Milan St.", "Milan", 1899, "Italy");
        insert.val("Kaka", "15 Ac Milan St.", "Milan", 1899, "Italy");
        insert.val("Andrea Pirlo", "21 Juventus St.", "Turin", 1899, "Italy");


        insert = db.get("Products").insert("Name", "Price");
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

        insert = db.get("Orders").insert("CustomerId", "ProductId", "Date");
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

    private int getCustomerId(String name){
        Cursor c =db.get("Customers").select("Name = ?", name).columns("Id").query();
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
        Cursor c =db.get("Products").select("Name = ?", name).columns("Id").query();
        try{
            if(c.moveToFirst()) return c.getInt(0);
            assertFalse("No product found insert name " + name, false);
            return -1;
        }
        finally {
            c.close();
        }
    }

    private String getRandomDate(){
        long now = Helper.now();
        now += Helper.getRandom(-1000, 0);

        Date date = new Date(now);
        return new SimpleDateFormat("MM/DD/yyyy").format(date);
    }
}
