package com.bingzer.android.dbv;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.queries.ISequence;
import com.bingzer.android.dbv.queries.InsertInto;
import com.bingzer.android.dbv.queries.Paging;

import java.util.LinkedList;

/**
 * Created by Ricky on 8/11/13.
 */
public class PaginationTest extends AndroidTestCase {

    IDatabase db;
    long managerId, janitorId, guardId, supervisorId;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("PaginationTest");
        db.getConfig().setForeignKeySupport(true);
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return PaginationTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Jobs")
                        .addPrimaryKey("Id")
                        .add("Position", "string");

                modeling.add("Person")
                        .addPrimaryKey("Id")
                        .add("Name", "String")
                        .add("Age", "Integer")
                        .add("Address", "Blob")
                        .add("JobId", "integer")
                        .foreignKey("JobId", "Jobs", "Id", null);
            }
        });

        db.from("Person").delete();
        db.from("Jobs").delete();

        InsertInto insert = db.from("Jobs").insertInto("Position");
        managerId = insert.val("Manager").query();
        janitorId = insert.val("Janitor").query();
        guardId = insert.val("Guard").query();
        supervisorId = insert.val("Supervisor").query();

        insert = db.from("Person").insertInto("Name", "Age", "Address", "JobId");
        // must be 6 individuals for test to be a ok
        insert.val("John", 23, "Washington DC".getBytes(), managerId);
        insert.val("Ronaldo", 40, "Madrid".getBytes(), guardId);
        insert.val("Messi", 25, "Barcelona".getBytes(), guardId);
        insert.val("Kaka", 30, "Madrid".getBytes(), guardId);
        insert.val("Pirlo", 31, "Turin".getBytes(), supervisorId);
        insert.val("Montolivo", 28, "Milan".getBytes(), janitorId);
    }

    public void testPaging_Simple(){
        Paging paging = db.from("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getPageNumber() == 0);

        PersonWithJobList personList = new PersonWithJobList();

        // #1
        paging.query(personList);
        assertTrue(paging.getPageNumber() == 0);
        assertTrue(personList.size() == 2);
        assertEquals(personList.get(0).getName(), "John");
        assertEquals(personList.get(1).getName(),"Ronaldo");
        // #2
        paging.next().query(personList);
        assertTrue(paging.getPageNumber() == 1);
        assertTrue(personList.size() == 4);
        assertEquals(personList.get(0).getName(),"John");
        assertEquals(personList.get(1).getName(),"Ronaldo");
        assertEquals(personList.get(2).getName(),"Messi");
        assertEquals(personList.get(3).getName(),"Kaka");
        // #3
        paging.next().query(personList);
        assertTrue(paging.getPageNumber() == 2);
        assertTrue(personList.size() == 6);
        assertEquals(personList.get(0).getName(),"John");
        assertEquals(personList.get(1).getName(),"Ronaldo");
        assertEquals(personList.get(2).getName(),"Messi");
        assertEquals(personList.get(3).getName(),"Kaka");
        assertEquals(personList.get(4).getName(),"Pirlo");
        assertEquals(personList.get(5).getName(),"Montolivo");

        paging.next().query(personList);
        assertTrue(paging.getPageNumber() == 3);
        assertTrue(personList.size() == 6);

        paging.next().query();
        assertTrue(paging.getPageNumber() == 4);

        paging.next().query(5);
        assertTrue(paging.getPageNumber() == 5);

        paging.previous().query(5);
        assertTrue(paging.getPageNumber() == 5);

        paging.previous().query();
        assertTrue(paging.getPageNumber() == 4);

        paging.previous().query(personList);
        assertTrue(paging.getPageNumber() == 3);

        personList = new PersonWithJobList();
        paging.previous().query(personList);
        assertTrue(paging.getPageNumber() == 2);
        assertTrue(personList.size() == 2);  // because the rowlimit = 2
    }

    public void testPaging_Query_Enumerable(){
        Paging paging = db.from("Person").select().orderBy("Id").paging(2);

        paging.query(new ISequence<Cursor>() {
            int counter = 0;
            @Override
            public boolean next(Cursor object) {
                if(counter == 0)
                    assertEquals("John", object.getString(object.getColumnIndex("Name")));
                else
                    assertEquals("Ronaldo", object.getString(object.getColumnIndex("Name")));

                counter++;

                return true;
            }
        });
    }

    public void testPaging_Query_PageNumber(){
        Paging paging = db.from("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getPageNumber() == 0);

        Cursor cursor = paging.query(1);
        assertTrue(cursor.getCount() == 2);

        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getString(1), "Messi");
        assertTrue(cursor.moveToNext());
        assertEquals(cursor.getString(1), "Kaka");
        assertFalse(cursor.moveToNext());

        assertTrue(paging.getPageNumber() == 1);
        cursor.close();
    }

    public void testPaging_Query_PageNumber_IEntityList(){
        Paging paging = db.from("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getPageNumber() == 0);

        PersonWithJobList personList = new PersonWithJobList();

        // #2
        paging.query(1, personList);
        assertTrue(personList.size() == 2);
        assertEquals(personList.get(0).getName(), "Messi");
        assertEquals(personList.get(1).getName(), "Kaka");
        assertTrue(paging.getPageNumber() == 1);
    }

    public void testPaging_SetPageNumber(){
        Paging paging = db.from("Person").select().orderBy("Id").paging(3);
        assertTrue(paging.getRowLimit() == 3);
        assertTrue(paging.getPageNumber() == 0);

        paging.setPageNumber(1);
        assertTrue(paging.getPageNumber() == 1);
        Cursor cursor = paging.query();
        assertTrue(paging.getPageNumber() == 1);
        assertTrue(cursor.moveToNext());
        assertEquals("Kaka", cursor.getString(1));
        assertTrue(cursor.moveToNext());
        assertEquals("Pirlo", cursor.getString(1));
        assertTrue(cursor.moveToNext());
        assertEquals("Montolivo", cursor.getString(1));
        assertFalse(cursor.moveToNext());
        cursor.close();
    }

    public void testPaging_SetPageNumber2(){
        Paging paging = db.from("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getPageNumber() == 0);

        paging.setPageNumber(1);
        assertTrue(paging.getPageNumber() == 1);
        Cursor cursor = paging.query();
        assertTrue(paging.getPageNumber() == 1);
        assertTrue(cursor.moveToNext());
        assertEquals("Messi", cursor.getString(1));
        assertTrue(cursor.moveToNext());
        assertEquals("Kaka", cursor.getString(1));
        assertFalse(cursor.moveToNext());
        assertTrue(paging.getPageNumber() == 1);
        cursor.close();
    }


    public void testPaging_WithJoin(){
        Paging paging = db.from("Person P")
                .join("Jobs J", "J.Id = P.JobId")
                .select()
                .columns("P.*", "J.Position")
                .orderBy("P.Id")
                .paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getPageNumber() == 0);

        PersonWithJobList personList = new PersonWithJobList();

        // #1
        paging.query(personList);
        assertTrue(paging.getPageNumber() == 0);
        assertTrue(personList.size() == 2);
        assertEquals(personList.get(0).getName(), "John");
        assertTrue(personList.get(0).getJobId() == managerId);
        assertEquals(personList.get(1).getName(), "Ronaldo");
        assertTrue(personList.get(1).getJobId() == guardId);
        // #2
        paging.next().query(personList);
        assertTrue(paging.getPageNumber() == 1);
        assertTrue(personList.size() == 4);
        assertEquals(personList.get(0).getName(), "John");
        assertEquals(personList.get(1).getName(), "Ronaldo");
        assertEquals(personList.get(2).getName(), "Messi");
        assertTrue(personList.get(2).getJobId() == guardId);
        assertEquals(personList.get(3).getName(), "Kaka");
        assertTrue(personList.get(3).getJobId() == guardId);
        // #3
        paging.next().query(personList);
        assertTrue(paging.getPageNumber() == 2);
        assertTrue(personList.size() == 6);
        assertEquals(personList.get(0).getName(), "John");
        assertEquals(personList.get(1).getName(), "Ronaldo");
        assertEquals(personList.get(2).getName(), "Messi");
        assertEquals(personList.get(3).getName(), "Kaka");
        assertEquals(personList.get(4).getName(), "Pirlo");
        assertTrue(personList.get(4).getJobId() == supervisorId);
        assertEquals(personList.get(5).getName(), "Montolivo");
        assertTrue(personList.get(5).getJobId() == janitorId);

        paging.next().query(personList);
        assertTrue(paging.getPageNumber() == 3);
        assertTrue(personList.size() == 6);

        paging.next().query();
        assertTrue(paging.getPageNumber() == 4);

        paging.next().next().query(5);
        assertTrue(paging.getPageNumber() == 5);
    }

    public void testGetTotalPage(){
        Paging paging = db.from("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getPageNumber() == 0);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getTotalPage() == 3);
    }

    public void testGetTotalPage_2(){
        Paging paging = db.from("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getTotalPage() == 3);

        // insert one so now row count should be 7
        db.from("Person").insertInto("Name", "Age", "Address").val("S", 22, "Bytes".getBytes());

        // recheck
        paging = db.from("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getTotalPage() == 4);
    }


    static class PersonWithJob extends Person {


        int jobId;

        int getJobId() {
            return jobId;
        }

        void setJobId(int jobId) {
            this.jobId = jobId;
        }

        @Override
        public void map(Mapper mapper) {
            super.map(mapper);
            mapper.map("JobId", new Delegate.TypeInteger(){

                /**
                 * Sets the value
                 *
                 * @param value the value to set
                 */
                @Override
                public void set(Integer value) {
                    setJobId(value);
                }

                /**
                 * Returns the value
                 *
                 * @return the value
                 */
                @Override
                public Integer get() {
                    return getJobId();
                }
            });
        }
    }

    static class PersonWithJobList extends LinkedList<PersonWithJob> implements IEntityList<PersonWithJob> {

        @Override
        public PersonWithJob newEntity() {
            return new PersonWithJob();
        }
    }

}
