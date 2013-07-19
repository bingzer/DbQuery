/**
 * Copyright 2013 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

        db.get("Customers").delete().query();
        db.get("Products").delete().query();
        db.get("Orders").delete().query();
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
        IQuery.Insert insert = db.get("Customers").insert("Name", "Address", "City", "PostalCode", "Country");

        insert.values("Wayne Rooney", "10 Manchester United", "Manchester", 9812, "UK").query();
        insert.values("Lionel Messi", "10 Barcelona st.", "Barcelona", 70, "Spain").query();
        insert.values("Christiano Ronaldo", "7 Real Madrid", "Madrid", 5689, "Spain").query();
        insert.values("Mario Baloteli", "9 Ac Milan St.", "Milan", 1899, "Italy").query();
        insert.values("Kaka", "15 Ac Milan St.", "Milan", 1899, "Italy").query();
        insert.values("Andrea Pirlo", "21 Juventus St.", "Turin", 1899, "Italy").query();


        insert = db.get("Products").insert("Name", "Price");
        insert.values("Car", 20000).query();
        insert.values("Motorcycle", 5000).query();
        insert.values("Computer", 1000).query();
        insert.values("Monitor", 500).query();
        insert.values("Keyboard and Mouse", 10).query();
        insert.values("Cellphone", 200).query();
        insert.values("Sunglasses", 50).query();
        insert.values("Desk", 100).query();
        insert.values("Lamp", 20).query();
        insert.values("Candy", 1).query();

        insert = db.get("Orders").insert("CustomerId", "ProductId", "Date");
        insert.values(getCustomerId("Wayne Rooney"), getProductId("Computer"), getRandomDate()).query();
        insert.values(getCustomerId("Wayne Rooney"), getProductId("Monitor"), getRandomDate()).query();
        insert.values(getCustomerId("Wayne Rooney"), getProductId("Cellphone"),  getRandomDate()).query();
        insert.values(getCustomerId("Wayne Rooney"), getProductId("Desk"),  getRandomDate()).query();
        insert.values(getCustomerId("Lionel Messi"), getProductId("Car"),  getRandomDate()).query();
        insert.values(getCustomerId("Lionel Messi"), getProductId("Desk"), getRandomDate()).query();
        insert.values(getCustomerId("Lionel Messi"), getProductId("Computer"), getRandomDate()).query();
        insert.values(getCustomerId("Lionel Messi"), getProductId("Lamp"), getRandomDate()).query();
        insert.values(getCustomerId("Christiano Ronaldo"), getProductId("Candy"), getRandomDate()).query();
        insert.values(getCustomerId("Christiano Ronaldo"), getProductId("Lamp"),  getRandomDate()).query();
        insert.values(getCustomerId("Christiano Ronaldo"), getProductId("Sunglasses"),  getRandomDate()).query();
        insert.values(getCustomerId("Christiano Ronaldo"), getProductId("Candy"), getRandomDate()).query();
        insert.values(getCustomerId("Christiano Ronaldo"), getProductId("Keyboard and Mouse"),  getRandomDate()).query();
        insert.values(getCustomerId("Christiano Ronaldo"), getProductId("Sunglasses"),  getRandomDate()).query();
        insert.values(getCustomerId("Mario Baloteli"), getProductId("Candy"),  getRandomDate()).query();
        insert.values(getCustomerId("Mario Baloteli"), getProductId("Monitor"),  getRandomDate()).query();
        insert.values(getCustomerId("Mario Baloteli"), getProductId("Computer"),  getRandomDate()).query();
        insert.values(getCustomerId("Mario Baloteli"), getProductId("Sunglasses"),  getRandomDate()).query();
        insert.values(getCustomerId("Mario Baloteli"), getProductId("Car"),  getRandomDate()).query();
        insert.values(getCustomerId("Kaka"), getProductId("Car"),  getRandomDate()).query();
        insert.values(getCustomerId("Kaka"), getProductId("Computer"),  getRandomDate()).query();
        insert.values(getCustomerId("Kaka"), getProductId("Cellphone"),  getRandomDate()).query();
        insert.values(getCustomerId("Kaka"), getProductId("Sunglasses"),  getRandomDate()).query();
        insert.values(getCustomerId("Kaka"), getProductId("Car"),  getRandomDate()).query();
        insert.values(getCustomerId("Andrea Pirlo"), getProductId("Car"),  getRandomDate()).query();
        insert.values(getCustomerId("Andrea Pirlo"), getProductId("Computer"),  getRandomDate()).query();
        insert.values(getCustomerId("Andrea Pirlo"), getProductId("Cellphone"),  getRandomDate()).query();
        insert.values(getCustomerId("Andrea Pirlo"), getProductId("Sunglasses"),  getRandomDate()).query();
        insert.values(getCustomerId("Andrea Pirlo"), getProductId("Computer"),  getRandomDate()).query();


    }

    private int getCustomerId(String name){
        Cursor c =db.get("Customers").select("Name = ?", name).columns("Id").query();
        try{
            if(c.moveToFirst()) return c.getInt(0);
            assertFalse("No customer found with name " + name, false);
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
            assertFalse("No product found with name " + name, false);
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
