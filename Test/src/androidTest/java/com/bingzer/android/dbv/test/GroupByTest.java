package com.bingzer.android.dbv.test;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.SQLiteBuilder;
import com.bingzer.android.dbv.contracts.ColumnSelectable;
import com.bingzer.android.dbv.queries.InsertInto;
import com.bingzer.android.dbv.queries.Paging;

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
                        .foreignKey("name", "employee", "name", null);
            }
        });

        db.get("company").delete();
        db.get("employee").delete();

        InsertInto insert = db.get("employee").insertInto("name", "position");
        insert.val("Paul", "Guard");
        insert.val("Allen", "Manager");
        insert.val("Teddy", "Janitor");
        insert.val("Mark", "Watch");
        insert.val("David", "Manager");
        insert.val("Kim", "Guard");
        insert.val("James", "CEO");

        // schema get: http://www.tutorialspoint.com/sqlite/sqlite_group_by.htm
        // insert
        insert = db.get("company").insertInto("name", "age", "address", "salary");
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

    public void testSelectGroupBy_Query_ColumnIndex(){
        ColumnSelectable col = db.get("company").select().columns("name", "sum(salary)").groupBy("name");

        assertEquals("Allen", col.query(0));
        assertEquals((double) 15000, (Double) col.query(1), 0.01);
    }

    public void testSelectGroupBy_Query_ColumnName(){
        ColumnSelectable col = db.get("company").select().columns("name", "sum(salary)").groupBy("name");

        assertEquals("Allen", col.query("name"));
        assertEquals((double) 15000, (Double) col.query("sum(salary)"), 0.01);
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

    public void testSelectGroupBy_WIthJoin_Query_ColumnIndex(){
        /*
        Result:
        assertTrue(cursor.getString(0).equals("Allen"));
        assertTrue(cursor.getFloat(1) == 15000f);
        assertTrue(cursor.getString(2).equals("Manager"));
         */
        ColumnSelectable col = db.get("company c")
                .join("employee e", "e.name = c.name")
                .select()
                .columns("e.name", "sum(c.salary)", "e.position")
                .orderBy("e.name")
                .groupBy("e.name");

        assertEquals("Allen", col.query(0));
        assertEquals(15000, (Double) col.query(1), 0.01);
        assertEquals("Manager", col.query(2));
    }

    public void testSelectGroupBy_WIthJoin_Query_ColumnName(){
        /*
        Result:
        assertTrue(cursor.getString(0).equals("Allen"));
        assertTrue(cursor.getFloat(1) == 15000f);
        assertTrue(cursor.getString(2).equals("Manager"));
         */
        ColumnSelectable col = db.get("company c")
                .join("employee e", "e.name = c.name")
                .select()
                .columns("e.name", "sum(c.salary) as salary_total", "e.position")
                .orderBy("e.name")
                .groupBy("e.name");

        assertEquals("Allen", col.query("e.name"));
        assertEquals(15000, (Double) col.query("salary_total"), 0.01);
        assertEquals("Manager", col.query("e.position"));
    }


    public void testSelectGroupBy_WithJoin_Paging(){
        Paging paging = db.get("company c")
                .join("employee e", "e.name = c.name")
                .select()
                .columns("e.name", "sum(c.salary)", "e.position")
                .orderBy("e.name")
                .groupBy("e.name")
                .paging(2);
        assertTrue(paging.getPageNumber() == 0);
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

        //////////////////////////////
        // page #1
        Cursor cursor = paging.query();
        assertTrue(paging.getPageNumber() == 0);

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Allen"));
        assertTrue(cursor.getFloat(1) == 15000f);
        assertTrue(cursor.getString(2).equals("Manager"));

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
        assertTrue(cursor.getString(2).equals("Manager"));

        //////////////////////////////
        // page #2
        cursor = paging.next().query();
        assertTrue(paging.getPageNumber() == 1);
        assertTrue(paging.getRowLimit() == cursor.getCount());

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("James"));
        assertTrue(cursor.getFloat(1) == 10000f);
        assertTrue(cursor.getString(2).equals("CEO"));

        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Kim"));
        assertTrue(cursor.getFloat(1) == 45000f);
        assertTrue(cursor.getString(2).equals("Guard"));


        try{
            paging.getTotalPage();
            // not expected
            assertTrue(false);
        }
        catch (Exception e){
            // as expected
            assertTrue(true);
        }

        cursor.close();
    }

    public void testHaving_Simple(){
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
        Cursor cursor = db.get("company")
                .select()
                .columns("name", "sum(salary)")
                .orderBy("name")
                .groupBy("name")
                .having("sum(salary) > ?", 30000)
                .query();

        assertTrue(cursor.getCount() == 3);  // 3 people with salary over 30000

        // # 1
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
        // # 2
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Kim"));
        assertTrue(cursor.getFloat(1) == 45000);
        // # 3
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Mark"));
        assertTrue(cursor.getFloat(1) == 65000f);

        cursor.close();
    }

    public void testHaving_Query_ColumnIndex(){
        /*
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
         */
        ColumnSelectable col = db.get("company")
                .select()
                .columns("name", "sum(salary)")
                .orderBy("name")
                .groupBy("name")
                .having("sum(salary) > ?", 30000);

        assertEquals("David", col.query(0));
        assertEquals(85000, (Double) col.query(1), 0.1);
    }

    public void testHaving_Query_ColumnName(){
        /*
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
         */
        ColumnSelectable col = db.get("company")
                .select()
                .columns("name", "sum(salary)")
                .orderBy("name")
                .groupBy("name")
                .having("sum(salary) > ?", 30000);

        assertEquals("David", col.query("name"));
        assertEquals(85000, (Double) col.query("sum(salary)"), 0.1);
    }

    public void testHaving_Simple2(){
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
        Cursor cursor = db.get("company")
                .select()
                .columns("name", "sum(salary)")
                .orderBy("name")
                .groupBy("name")
                .having("sum(salary) > 30000")
                .query();

        assertTrue(cursor.getCount() == 3);  // 3 people with salary over 30000

        // # 1
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
        // # 2
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Kim"));
        assertTrue(cursor.getFloat(1) == 45000);
        // # 3
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Mark"));
        assertTrue(cursor.getFloat(1) == 65000f);

        cursor.close();
    }

    public void testHaving_SimpleJoin(){
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
        Cursor cursor = db.get("company c")
                .join("employee e", "e.name = c.name")
                .select()
                .columns("e.name", "sum(c.salary)", "e.position")
                .orderBy("e.name")
                .groupBy("c.name")
                .having("sum(c.salary) > ? AND e.position <> ?", 30000, "Guard")  // we take off "KIM
                .query();

        assertTrue(cursor.getCount() == 2);  // 2 people with salary over 30000 and not guard

        // # 1
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
        // # 2
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Mark"));
        assertTrue(cursor.getFloat(1) == 65000f);

        cursor.close();
    }


    public void testHaving_SimpleJoin2(){
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
Teddy       20000.0     Janitor  (ADDED 20000) TOTAL = 40000
         */
        db.get("Company").insertInto("name", "salary").val("Teddy", 20000);
        assertTrue(db.get("Company").count("name = ?", "Teddy") == 2);  // 2 records with Teddy

        Cursor cursor = db.get("company c")
                .join("employee e", "e.name = c.name")
                .select()
                .columns("e.name", "sum(c.salary)", "e.position")
                .orderBy("e.name")
                .groupBy("c.name")
                .having("sum(c.salary) > ? AND e.position <> ?", 30000, "Guard")  // we take off "KIM
                .query();

        assertTrue(cursor.getCount() == 3);  // 3 people with salary over 30000 and not guard + Teddy

        // # 1
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
        // # 2
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Mark"));
        assertTrue(cursor.getFloat(1) == 65000f);
        // # 2
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Teddy"));
        assertTrue(cursor.getFloat(1) == 40000f);  // 20k + 20k

        cursor.close();
    }

    public void testHaving_SimpleJoin_Paging(){
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
Teddy       20000.0     Janitor  (ADDED 20000) TOTAL = 40000
         */
        db.get("Company").insertInto("name", "salary").val("Teddy", 20000);
        assertTrue(db.get("Company").count("name = ?", "Teddy") == 2);  // 2 records with Teddy

        Paging paging = db.get("company c")
                .join("employee e", "e.name = c.name")
                .select()
                .columns("e.name", "sum(c.salary)", "e.position")
                .orderBy("e.name")
                .groupBy("c.name")
                .having("sum(c.salary) > ? AND e.position <> ?", 30000, "Guard")  // we take off "KIM
                .paging(2);
        assertTrue(paging.getPageNumber() == 0);

        try{
            paging.getTotalPage();
            // not expected
            assertTrue(false);
        }
        catch (Exception e){
            // as expected
            assertTrue(true);
        }

        // # PAGE 1
        Cursor cursor = paging.query();
        assertTrue(paging.getPageNumber() == 0);
        assertTrue(paging.getRowLimit() == cursor.getCount());
        // # 1
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("David"));
        assertTrue(cursor.getFloat(1) == 85000f);
        // # 2
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Mark"));
        assertTrue(cursor.getFloat(1) == 65000f);


        // # PAGE 2
        cursor = paging.next().query();
        assertTrue(paging.getPageNumber() == 1);
        cursor.moveToNext();
        assertTrue(cursor.getString(0).equals("Teddy"));
        assertTrue(cursor.getFloat(1) == 40000f);  // 20k + 20k

        cursor.close();
    }
}
