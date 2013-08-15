package com.bingzer.android.dbv.test;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 8/15/13.
 */
public class GroupByTest extends AndroidTestCase {

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("GroupByTest");
        db.getConfig().setForeignKeySupport(true);
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return GroupByTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {

                modeling.add("employee")
                        .addPrimaryKey("id")
                        .add("name", "text", "unique")
                        .add("position", "text")
                        .index("name");

                modeling.add("company")
                        .addPrimaryKey("id")
                        .add("name", "text")
                        .add("age", "int")
                        .add("address", "string")
                        .add("salary", "float")
                        .index("name")
                        .foreignKey("name", "employee", "name");
            }
        });

        db.get("company").delete();
        db.get("employee").delete();

        IQuery.InsertWith insert = db.get("employee").insert("name", "position");
        insert.val("Paul", "Guard");
        insert.val("Allen", "Manager");
        insert.val("Teddy", "Janitor");
        insert.val("Mark", "Watch");
        insert.val("David", "Manager");
        insert.val("Kim", "Guard");
        insert.val("James", "CEO");

        // schema from: http://www.tutorialspoint.com/sqlite/sqlite_group_by.htm
        // insert
        insert = db.get("company").insert("name", "age", "address", "salary");
        insert.val("Paul", 32, "California", 20000f);
        insert.val("Allen", 25, "Texas", 15000f);
        insert.val("Teddy", 23, "Norway", 20000f);
        insert.val("Mark", 25, "Rich-Mond", 65000f);
        insert.val("David", 27, "Texas", 85000f);
        insert.val("Kim", 22, "South-Hall", 45000f);
        insert.val("James", 24, "Houston", 10000f);
    }

    public void testSelectGroupBy(){
        Cursor cursor = db.get("company").select().columns("name", "sum(salary)").groupBy("name").query();
        /*
        Should produce
        NAME        SUM(SALARY)
----------  -----------
Allen       15000.0
David       85000.0
James       10000.0
Kim         45000.0
Mark        65000.0
Paul        20000.0
Teddy       20000.0

         */

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Allen"));
        assertTrue(cursor.getFloat(1) == 15000f);

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("James"));
        assertTrue(cursor.getFloat(1) == 10000f);
    }

    public void testSelectGroupBy_WithJoin(){
        Cursor cursor = db.get("company c")
                            .join("employee e", "e.name = c.name")
                            .select()
                            .columns("e.name", "sum(c.salary)", "e.position")
                            .orderBy("e.name")
                            .groupBy("e.name")
                            .query();
        /*

        insert.val("Paul", "Guard");
        insert.val("Allen", "Manager");
        insert.val("Teddy", "Janitor");
        insert.val("Mark", "Watch");
        insert.val("David", "Manager");
        insert.val("Kim", "Guard");
        insert.val("James", "CEO");
         */

        /*
        Should produce
NAME        SUM(SALARY) position
----------  ----------- ----------  -----------
Allen       15000.0     Manager
David       85000.0     Manager
James       10000.0     CEO
Kim         45000.0     Guard
Mark        65000.0     Watch
Paul        20000.0     Guard
Teddy       20000.0     Janitor

         */

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Allen"));
        assertTrue(cursor.getFloat(1) == 15000f);
        assertTrue(cursor.getString(2).equals("Manager"));

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
        assertTrue(cursor.getString(2).equals("Manager"));

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("James"));
        assertTrue(cursor.getFloat(1) == 10000f);
        assertTrue(cursor.getString(2).equals("CEO"));

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Kim"));
        assertTrue(cursor.getFloat(1) == 45000f);
        assertTrue(cursor.getString(2).equals("Guard"));
    }
}
