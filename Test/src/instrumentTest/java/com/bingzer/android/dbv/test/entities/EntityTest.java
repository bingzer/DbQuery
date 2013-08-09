package com.bingzer.android.dbv.test.entities;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

import java.util.LinkedList;
import java.util.List;

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
                        .add("Age", "Integer");
            }
        });

        db.get("Person").deleteAll();

        IQuery.InsertWith insert = db.get("Person").insert("Name", "Age");
        insert.val("John", 23);
        insert.val("Ronaldo", 40);
        insert.val("Messi", 25);

    }


    public void testPerson(){
        int messId = db.get("Person").selectId("Name = ?", "Messi");

        Person person = new Person();
        db.get("Person").select(messId).query(person);
        assertTrue(person.getName().equals("Messi"));
        assertTrue(person.getAge() == 25);
    }


    public void testInsertEntity(){
        Person person = new Person();
        person.setName("Andrea Pirlo");
        person.setAge(100);

        int pirloId = db.get("Person").insert(person).query();
        assertTrue(pirloId > 0);
        assertTrue(db.get("Person").count("Name = ?", "Andrea Pirlo") > 0);
    }


    public void testUpdateEntity(){
        Person person = new Person();
        person.setName("Messi");
        person.setAge(10000);
        person.id = db.get("Person").selectId("Name = ?", "Messi");

        db.get("Person").update(person);

        Person p2 = new Person();
        db.get("Person").select("Name = ?", "Messi").query(p2);

        assertTrue(p2.getName().equals("Messi"));
        assertTrue(p2.getAge() == 10000);
    }

    public void testEntity_Collection(){

        PersonList personList = new PersonList();
        db.get("Person").select().query(personList);

        assertTrue(personList.size() > 0);
    }


    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    public static class PersonList extends LinkedList<Person> implements IEntityList<Person>{

        @Override
        public List<Person> getEntityList() {
            return this;
        }

        @Override
        public Person newEntity() {
            return new Person();
        }
    }

    public static class Person implements IEntity {
        private int id;
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        /**
         * Returns the id
         *
         * @return the id
         */
        @Override
        public int getId() {
            return id;
        }

        @Override
        public void map(Mapper mapper) {
            mapper.map("Name", new Action<String>(String.class) {

                @Override
                public void set(String value) {
                    setName(value);
                }

                @Override
                public String get() {
                    return getName();
                }
            });

            mapper.map("Age", new Action<Integer>(Integer.class){

                @Override
                public void set(Integer value) {
                    setAge(value);
                }

                @Override
                public Integer get() {
                    return getAge();
                }
            });

            mapper.mapId(new Action<Integer>(Integer.class){

                /**
                 * Sets the value
                 *
                 * @param value the value to set
                 */
                @Override
                public void set(Integer value) {
                    id = value;
                }

                /**
                 * Returns the value
                 *
                 * @return the value
                 */
                @Override
                public Integer get() {
                    return getId();
                }
            });

        }

    }
}

