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
import com.bingzer.android.dbv.sqlite.Database;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 7/18/13.
 */
public class DatabaseTest extends AndroidTestCase {

    IDatabase db;

    public void setUp(){
        db = DbQuery.getDatabase("DatabaseTestDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return DatabaseTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Customers")
                        .addPrimaryKey("Id")
                        .addText("Name", "not null")
                        .addText("Address")
                        .addText("City")
                        .addText("PostalCode")
                        .addText("Country");
            }
        });
    }

    ////////////////////
    ////////////////////
    ////////////////////

    public void testGetName(){
        assertTrue(db.getName().equalsIgnoreCase("DatabaseTestDb"));
    }

    public void testGetVersion(){
        assertTrue(db.getVersion() == 1);
    }

    public void testGetTables(){
        assertTrue(db.getTables() != null);
        assertTrue(db.getTables().size() >= 2);
    }

    public void testRaw(){
        String sql = "SELECT * FROM Customers";
        Cursor cursor = db.raw(sql).query();

        assertTrue(cursor != null);

        cursor.close();
    }

    public void testExecSql(){
        String sql = "SELECT 1 FROM Customers";
        try{
            db.execSql(sql);
            assertTrue("Good", true);
        }
        catch (Exception e){
            assertTrue("Bad", false);
        }
    }

    public void testGetSQLiteOpenHelper(){
        try{
            // should trow exception
            // we haven't open the db
            ((Database)db).getSQLiteOpenHelper();
            assertTrue("Good", true);
        }
        catch (Exception e){
            assertTrue("Bad", false);
        }
    }

    public void testGetSQLiteDatabase(){
        try{
            // should trow exception
            // we haven't open the db
            ((Database)db).getSQLiteDatabase();
            assertTrue("Good", true);
        }
        catch (Exception e){
            assertTrue("Bad", false);
        }
    }

}
