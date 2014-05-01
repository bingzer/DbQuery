package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.SQLiteBuilder;
import com.bingzer.android.dbv.queries.InsertInto;

/**
 * Created by Ricky Tobing on 8/9/13.
 */
public class EntityTest extends AndroidTestCase {

    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("EntityDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return EntityTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Person")
                        .addPrimaryKey("Id")
                        .add("Name", "String")
                        .add("Age", "Integer")
                        .addBlob("Address");
            }
        });

        db.get("Person").delete();

        InsertInto insert = db.get("Person").insertInto("Name", "Age", "Address");
        insert.val("John", 23, "Washington DC".getBytes());
        insert.val("Ronaldo", 40, "Madrid".getBytes());
        insert.val("Messi", 25, "Barcelona".getBytes());

    }


    public void testPerson(){
        long messId = db.get("Person").selectId("Name = ?", "Messi");

        Person person = new Person();
        db.get("Person").select(messId).query(person);
        assertTrue(person.getName().equals("Messi"));
        assertTrue(person.getAge() == 25);
        assertTrue(new String(person.getAddressBytes()).equalsIgnoreCase("Barcelona"));
    }


    public void testInsertEntity(){
        Person person = new Person();
        person.setName("Andrea Pirlo");
        person.setAge(100);
        person.setAddressBytes("Turin".getBytes());

        long pirloId = db.get("Person").insert(person).query();
        assertTrue(pirloId == person.getId());
        assertTrue(db.get("Person").count("Name = ?", "Andrea Pirlo") > 0);
    }

    public void testUpdateEntity(){
        Person person = new Person();
        person.setName("Messi");
        person.setAge(10000);
        person.setId(db.get("Person").selectId("Name = ?", "Messi"));
        person.setAddressBytes("Barcelona Updated".getBytes());

        db.get("Person").update(person);

        Person p2 = new Person();
        db.get("Person").select("Name = ?", "Messi").query(p2);

        assertTrue(p2.getName().equals("Messi"));
        assertTrue(p2.getAge() == 10000);
        assertTrue(new String(p2.getAddressBytes()).equalsIgnoreCase("Barcelona Updated"));
    }

    public void testUpdateEntity2(){
        Person person = new Person();
        person.setName("John");
        person.setAge(-1);
        person.setId(db.get("Person").selectId("Name = ?", "John"));
        person.setAddressBytes("Washington DC Updated".getBytes());

        assertEquals(1, (int) db.get("Person").update(person).query());

        Person p2 = new Person();
        db.get("Person").select("Name = ?", "John").query(p2);

        assertTrue(p2.getName().equals("John"));
        assertTrue(p2.getAge() == -1);
        assertTrue(new String(p2.getAddressBytes()).equalsIgnoreCase("Washington DC Updated"));
    }

    public void testUpdateEntity_Continue(){
        Person person = new Person();
        person.setName("Messi");
        person.setAge(10000);
        person.setId(db.get("Person").selectId("Name = ?", "Messi"));
        person.setAddressBytes("Barcelona Updated".getBytes());

        assertEquals(1, (int) db.get("Person").update(person).query());

        Person p2 = new Person();
        db.get("Person").select("Name = ?", "Messi").query(p2);

        assertTrue(p2.getName().equals("Messi"));
        assertTrue(p2.getAge() == 10000);
        assertTrue(new String(p2.getAddressBytes()).equalsIgnoreCase("Barcelona Updated"));
    }

    public void testEntity_Collection(){

        PersonList personList = new PersonList();
        db.get("Person").select().query(personList);

        assertTrue(personList.size() > 0);
    }



}

