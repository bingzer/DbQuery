package com.bingzer.android.dbv;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.queries.InsertInto;

/**
 * Created by Ricky Tobing on 8/13/13.
 */
public class IdNamingConventionTest extends AndroidTestCase {

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("NamingConventionDb");
        db.getConfig().setIdNamingConvention("IDX");
        db.getConfig().setAppendTableNameForId(true);
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return IdNamingConventionTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Person")
                        .addPrimaryKey("PersonIDX")
                        .add("Name", "String")
                        .add("Age", "Integer")
                        .add("Address", "Blob");
            }
        });

        db.from("Person").delete();


        InsertInto insert = db.from("Person").insertInto("Name", "Age", "Address");
        insert.val("John", 23, "Washington DC".getBytes());
        insert.val("Ronaldo", 40, "Madrid".getBytes());
        insert.val("Messi", 25, "Barcelona".getBytes());
    }

    public void testSelect(){
        Person person = new Person();

        db.from("Person").select("Name = ?", "John").query(person);
        assertTrue(person.getId() > 0);
        assertTrue(person.getName().equals("John"));
        db.from("Person").select("Name = ?", "Messi").query(person);
        assertTrue(person.getId() > 0);
        assertTrue(new String(person.getAddressBytes()).equals("Barcelona"));
    }

    public void testUpdate(){
        Person person = new Person();
        db.from("Person").select("Name = ?", "John").query(person);  // john
        long id = person.getId();
        // modify
        person.setName("MOD");
        person.setAge(20);
        db.from("Person").update(person);
        // reget
        db.from("Person").select(id).query(person);
        assertTrue(person.getName().equals("MOD"));
        assertTrue(person.getAge() == 20);
    }
}
