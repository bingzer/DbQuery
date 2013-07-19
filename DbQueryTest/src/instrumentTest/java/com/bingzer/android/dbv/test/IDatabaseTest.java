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

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;

/**
 * Created by Ricky Tobing on 7/18/13.
 */
public class IDatabaseTest extends AndroidTestCase {

    IDatabase db;

    public void setUp(){
        db = DbQuery.getDatabase("TestDb");
    }

    ////////////////////
    ////////////////////
    ////////////////////

    public void testGetName(){
        assertTrue(db.getName().equalsIgnoreCase("TestDb"));
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

}
