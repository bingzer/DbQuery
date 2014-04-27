package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.internal.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 8/12/13.
 */
public class EntityListTest extends AndroidTestCase{

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("EntityDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return EntityListTest.this.getContext();
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

        db.get("Person").delete();

        IQuery.InsertWith insert = db.get("Person").insert("Name", "Age", "Address");
        insert.val("John", 23, "Washington DC".getBytes());
        insert.val("Ronaldo", 40, "Madrid".getBytes());
        insert.val("Messi", 25, "Barcelona".getBytes());
        insert.val("Kaka", 30, "Madrid".getBytes());
        insert.val("Pirlo", 31, "Turin".getBytes());
        insert.val("Montolivo", 28, "Milan".getBytes());

    }

    public void testSelectEntityList(){
        PersonList personList = new PersonList();
        db.get("Person").select().query(personList);

        assertTrue(personList.size() > 0);
        assertTrue(personList.get(0).getName().equals("John"));
        assertTrue(personList.get(1).getName().equals("Ronaldo"));
        assertTrue(personList.get(2).getName().equals("Messi"));
        assertTrue(personList.get(3).getName().equals("Kaka"));
        assertTrue(personList.get(4).getName().equals("Pirlo"));
        assertTrue(personList.get(5).getName().equals("Montolivo"));
    }

    public void testBulkUpdate(){
        PersonList personList = new PersonList();
        db.get("Person").select().query(personList);

        assertTrue(personList.size() > 0);
        personList.get(0).setAge(1000); // john
        personList.get(1).setAddressBytes("Modified".getBytes());
        personList.get(2).setName("This is Number 3");

        assertTrue(db.get("Person").update(personList).query() == 6); // 6 updates..

        // re create object
        personList = new PersonList();
        db.get("Person").select().query(personList);
        assertTrue(personList.get(0).getAge() == 1000);
        assertTrue("Modified".equals(new String(personList.get(1).getAddressBytes())));
        assertTrue(personList.get(2).getName().equals("This is Number 3"));
    }

    public void testBulkInsert(){
        PersonList personList = new PersonList();
        personList.add(new Person("Person7", 77, "Whatever".getBytes()));
        personList.add(new Person("Person8", 88, "Whatever too".getBytes()));

        assertTrue(db.get("Person").insert(personList).query() == 2);
        personList = new PersonList();

        db.get("Person").select().query(personList);
        assertTrue(personList.get(personList.size() - 2).getName().equals("Person7"));
        assertTrue(personList.get(personList.size() - 1).getName().equals("Person8"));
    }

    public void testBulkDelete(){
        PersonList personList = new PersonList();
        db.get("Person").select().query(personList);

        // delete all
        assertTrue(db.get("Person").delete(personList).query() == personList.size());
        assertTrue(db.get("Person").count() == 0);
    }
}
