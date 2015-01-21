package com.bingzer.android.dbv;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.UUID;

import static org.mockito.Mockito.mock;

public class BaseEntityTest extends AndroidTestCase {

    IDatabase db;
    long person1Id = -1, person2Id = -1;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        db = DbQuery.getDatabase("BaseEntityTest");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return BaseEntityTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Person")
                        .addPrimaryKey("Id")
                        .addText("Name")
                        .addInteger("Age");
            }
        });

        if(!db.from("Person").has("Name = ?", "Person1"))
            db.from("Person").insert(new OrmPerson("Person1", 1));
        if(!db.from("Person").has("Name = ?", "Person2"))
            db.from("Person").insert(new OrmPerson("Person2", 2));

        person1Id = db.from("Person").selectId("Name = ?", "Person1");
        person2Id = db.from("Person").selectId("Name = ?", "Person2");
    }

    public void test_getsetId(){
        OrmPerson person = new OrmPerson();
        person.setId(90890);
        assertEquals(90890, person.getId());
    }

    public void test_getEnvironment(){
        OrmPerson person = new OrmPerson();
        assertNotNull(person.getEnvironment());

        IEnvironment env = mock(IEnvironment.class);
        person = new OrmPerson(env);
        assertNotNull(person.getEnvironment());
        assertTrue(env == person.getEnvironment());
    }

    public void test_getEnvironemnt_getDatabase(){
        OrmPerson person = new OrmPerson();
        assertNotNull(person.getEnvironment().getDatabase());
        assertTrue(db == person.getEnvironment().getDatabase());
    }

    public void test_save(){
        String name = UUID.randomUUID().toString();
        OrmPerson person = new OrmPerson(name, 30);
        assertEquals(-1, person.getId());

        assertTrue(person.save());
        assertNotSame(-1, person.getId());
    }

    public void test_delete(){
        OrmPerson person = new OrmPerson();
        person.load(person1Id);
        assertTrue(person.getId() > 0);

        assertTrue(person.delete());
        assertEquals(-1, person.getId());
        assertFalse(person.load(person1Id));

        assertFalse(person.delete());
    }

    public void test_load(){
        OrmPerson person = new OrmPerson();
        person.load(person1Id);

        assertEquals("Person1", person.getName());
        assertEquals(1, person.getAge());
        assertTrue(person.getId() > 0);

        // modify
        person.setName("Something else");
        person.setAge(123);
        assertNotSame("Person1", person.getName());
        assertNotSame(1, person.getAge());

        // refresh from db
        person.load();
        assertEquals("Person1", person.getName());
        assertEquals(1, person.getAge());
    }

    public void test_load_cursor(){
        OrmPerson person = new OrmPerson();
        Cursor cursor = db.from("Person").select("Name = ?", "Person2").query();
        if(cursor.moveToNext()){
            person.load(cursor);
        }

        assertEquals("Person2", person.getName());
        assertEquals(2, person.getAge());
    }

    /////////////////////////////////////////////////////////////////

    int insertCallbackCounter = 0;
    public void test_insert_callbak(){
        OrmPerson person = new OrmPerson(){
            @Override
            protected void onBeforeInsert() {
                insertCallbackCounter++;
            }

            @Override
            protected void onAfterInsert() {
                insertCallbackCounter++;
            }
        };

        assertTrue(person.save());
        assertEquals(2, insertCallbackCounter);
    }

    int updateCallbackCounter = 0;
    public void test_update_callbak(){
        OrmPerson person = new OrmPerson(){
            @Override
            protected void onBeforeUpdate() {
                updateCallbackCounter++;
            }

            @Override
            protected void onAfterUpdate() {
                updateCallbackCounter++;
            }
        };

        assertTrue(person.load(person1Id));
        assertTrue(person.save());
        assertEquals(2, updateCallbackCounter);
    }

    int deleteCallbackCounter = 0;
    public void test_delete_callback(){
        OrmPerson person = new OrmPerson(){
            @Override
            protected void onBeforeDelete() {
                deleteCallbackCounter++;
            }

            @Override
            protected void onAfterDelete() {
                deleteCallbackCounter++;
            }
        };

        assertTrue(person.load(person1Id));
        assertTrue(person.delete());
        assertEquals(2, deleteCallbackCounter);
    }

    int loadCallbackCounter = 0;
    public void test_load_callback(){
        OrmPerson person = new OrmPerson(){
            @Override
            protected void onBeforeLoad() {
                loadCallbackCounter++;
            }

            @Override
            protected void onAfterLoad() {
                loadCallbackCounter++;
            }
        };
        assertTrue(person.load(person1Id));
        assertEquals(2, loadCallbackCounter);
    }

}
