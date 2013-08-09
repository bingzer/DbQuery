package com.bingzer.android.dbv.test.entities;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IEntity;
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


    public void testPersonList(){
        List<Person> personList = new LinkedList<Person>();
        personList.add(new Person());
        personList.add(new Person());

        db.get("Person").select("Name = ? OR Name = ?", "Messi", "Ronaldo").query(personList);

        for(Person person : personList){
            assertTrue(person.getName().equals("Messi") || person.getName().equals("Ronaldo"));
        }
    }



    public static class Person implements IEntity {
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

        @Override
        public void map(Mapper mapper) {
            mapper.map("Name", new Action<String>(String.class){

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

        }

    }
}

