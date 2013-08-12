package com.bingzer.android.dbv.test.pagination;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;
import com.bingzer.android.dbv.test.PersonList;

/**
 * Created by Ricky on 8/11/13.
 */
public class PaginationTest extends AndroidTestCase {

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("EntityDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return PaginationTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Person")
                        .addPrimaryKey("Id")
                        .add("Name", "String")
                        .add("Age", "Integer")
                        .add("Address", "Blob");
            }
        });

        db.get("Person").deleteAll();

        IQuery.InsertWith insert = db.get("Person").insert("Name", "Age", "Address");

        // must be 6 individuals for test to be a ok
        insert.val("John", 23, "Washington DC".getBytes());
        insert.val("Ronaldo", 40, "Madrid".getBytes());
        insert.val("Messi", 25, "Barcelona".getBytes());
        insert.val("Kaka", 30, "Madrid".getBytes());
        insert.val("Pirlo", 31, "Turin".getBytes());
        insert.val("Montolivo", 28, "Milan".getBytes());
    }

    public void testSimplePagination(){
        IQuery.Paging paging = db.get("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);

        PersonList personList = new PersonList();

        // #0
        paging.query(personList);
        assertTrue(personList.size() == 2);
        assertTrue(personList.get(0).getName().equalsIgnoreCase("John"));
        assertTrue(personList.get(1).getName().equalsIgnoreCase("Ronaldo"));
        assertTrue(paging.getPageNumber() == 1);
        // #2
        paging.query(personList);
        assertTrue(personList.size() == 4);
        assertTrue(personList.get(0).getName().equalsIgnoreCase("John"));
        assertTrue(personList.get(1).getName().equalsIgnoreCase("Ronaldo"));
        assertTrue(personList.get(2).getName().equalsIgnoreCase("Messi"));
        assertTrue(personList.get(3).getName().equalsIgnoreCase("Kaka"));
        assertTrue(paging.getPageNumber() == 2);
        // #3
        paging.query(personList);
        assertTrue(personList.size() == 6);
        assertTrue(personList.get(0).getName().equalsIgnoreCase("John"));
        assertTrue(personList.get(1).getName().equalsIgnoreCase("Ronaldo"));
        assertTrue(personList.get(2).getName().equalsIgnoreCase("Messi"));
        assertTrue(personList.get(3).getName().equalsIgnoreCase("Kaka"));
        assertTrue(personList.get(4).getName().equalsIgnoreCase("Pirlo"));
        assertTrue(personList.get(5).getName().equalsIgnoreCase("Montolivo"));
        assertTrue(paging.getPageNumber() == 3);

        paging.query(personList);
        paging.query();
        paging.query(5);
    }
}
