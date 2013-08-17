package com.bingzer.android.dbv.test;

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
 * Created by Ricky on 8/11/13.
 */
public class PaginationTest extends AndroidTestCase {

    IDatabase db;
    int managerId, janitorId, guardId, supervisorId;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("PaginationDb");
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
                        .foreignKey("JobId", "Jobs", "Id");
            }
        });

        db.get("Person").delete();
        db.get("Jobs").delete();

        IQuery.InsertWith insert = db.get("Jobs").insert("Position");
        managerId = insert.val("Manager").query();
        janitorId = insert.val("Janitor").query();
        guardId = insert.val("Guard").query();
        supervisorId = insert.val("Supervisor").query();

        insert = db.get("Person").insert("Name", "Age", "Address", "JobId");
        // must be 6 individuals for test to be a ok
        insert.val("John", 23, "Washington DC".getBytes(), managerId);
        insert.val("Ronaldo", 40, "Madrid".getBytes(), guardId);
        insert.val("Messi", 25, "Barcelona".getBytes(), guardId);
        insert.val("Kaka", 30, "Madrid".getBytes(), guardId);
        insert.val("Pirlo", 31, "Turin".getBytes(), supervisorId);
        insert.val("Montolivo", 28, "Milan".getBytes(), janitorId);
    }

    public void testSimplePagination(){
        IQuery.Paging paging = db.get("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);

        PersonWithJobList personList = new PersonWithJobList();

        // #1
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
        assertTrue(paging.getPageNumber() == 3);
        assertTrue(personList.size() == 6);

        paging.query();
        assertTrue(paging.getPageNumber() == 3);

        paging.query(5);
        assertTrue(paging.getPageNumber() == 5);
    }

    public void testPaginationWithJoin(){
        IQuery.Paging paging = db.get("Person P")
                .join("Jobs J", "J.Id = P.JobId")
                .select()
                .columns("P.*", "J.Position")
                .orderBy("P.Id")
                .paging(2);
        assertTrue(paging.getRowLimit() == 2);

        PersonWithJobList personList = new PersonWithJobList();

        // #1
        paging.query(personList);
        assertTrue(personList.size() == 2);
        assertTrue(personList.get(0).getName().equalsIgnoreCase("John"));
        assertTrue(personList.get(0).getJobId() == managerId);
        assertTrue(personList.get(1).getName().equalsIgnoreCase("Ronaldo"));
        assertTrue(personList.get(1).getJobId() == guardId);
        assertTrue(paging.getPageNumber() == 1);
        // #2
        paging.query(personList);
        assertTrue(personList.size() == 4);
        assertTrue(personList.get(0).getName().equalsIgnoreCase("John"));
        assertTrue(personList.get(1).getName().equalsIgnoreCase("Ronaldo"));
        assertTrue(personList.get(2).getName().equalsIgnoreCase("Messi"));
        assertTrue(personList.get(2).getJobId() == guardId);
        assertTrue(personList.get(3).getName().equalsIgnoreCase("Kaka"));
        assertTrue(personList.get(3).getJobId() == guardId);
        assertTrue(paging.getPageNumber() == 2);
        // #3
        paging.query(personList);
        assertTrue(personList.size() == 6);
        assertTrue(personList.get(0).getName().equalsIgnoreCase("John"));
        assertTrue(personList.get(1).getName().equalsIgnoreCase("Ronaldo"));
        assertTrue(personList.get(2).getName().equalsIgnoreCase("Messi"));
        assertTrue(personList.get(3).getName().equalsIgnoreCase("Kaka"));
        assertTrue(personList.get(4).getName().equalsIgnoreCase("Pirlo"));
        assertTrue(personList.get(4).getJobId() == supervisorId);
        assertTrue(personList.get(5).getName().equalsIgnoreCase("Montolivo"));
        assertTrue(personList.get(5).getJobId() == janitorId);
        assertTrue(paging.getPageNumber() == 3);

        paging.query(personList);
        assertTrue(paging.getPageNumber() == 3);
        assertTrue(personList.size() == 6);

        paging.query();
        assertTrue(paging.getPageNumber() == 3);

        paging.query(5);
        assertTrue(paging.getPageNumber() == 5);
    }





    public void testGetTotalPage(){
        IQuery.Paging paging = db.get("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getTotalPage() == 3);
    }

    public void testGetTotalPage_2(){
        IQuery.Paging paging = db.get("Person").select().orderBy("Id").paging(2);
        assertTrue(paging.getRowLimit() == 2);
        assertTrue(paging.getTotalPage() == 3);

        // insert one so now row count should be 7
        db.get("Person").insert("Name", "Age", "Address").val("S", 22, "Bytes".getBytes());

        // recheck
        paging = db.get("Person").select().orderBy("Id").paging(2);
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
        public void map(IEntity.Mapper mapper) {
            super.map(mapper);
            mapper.map("JobId", new Action<Integer>(Integer.class){

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
        public List<PersonWithJob> getEntityList() {
            return this;
        }

        @Override
        public PersonWithJob newEntity() {
            return new PersonWithJob();
        }
    }

}
