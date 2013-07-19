package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbEngine;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.MigrationMode;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;


/**
 * This test must be first test to run
 * Created by 11856 on 7/18/13.
 */
public class AASetup extends AndroidTestCase{

    IDatabase db;
    public void setUp(){
        db = DbEngine.getDatabase("TestDb");
        db.create(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return AASetup.this.getContext();
            }

            @Override
            public MigrationMode getMigrationMode() {
                return MigrationMode.DropIfExists;
            }

            @Override
            public void onCreate(IDatabase.Modeling modeling) {
                modeling.add("Persons")
                        .add("Id", "INTEGER", "primary key autoincrement not null")
                        .add("Name", "TEXT", "not null")
                        .add("Age", "INTEGER")
                        .add("JobId", "INTEGER");

                modeling.add("Jobs")
                        .add("Id", "INTEGER", "primary key autoincrement not null")
                        .add("Name", "TEXT", "not null");
            }
        });

        // empty all tables
        db.get("Persons").delete().query();
        db.get("Jobs").delete().query();

        ITable jobTable = db.get("Jobs");
        // populate jobs..
        jobTable.insert("Name").values("Driver").query();
        jobTable.insert("Name").values("Cook").query();
        jobTable.insert("Name").values("Comedian").query();
        jobTable.insert("Name").values("Programmer").query();
        jobTable.insert("Name").values("Cop").query();


        ITable personTable = db.get("Persons");
        personTable.insert("Name", "Age", "JobId").values("John Doe", 21, Util.getJobId(db, "Driver")).query();
        personTable.insert("Name", "Age", "JobId").values("Jane Doe", 25, Util.getJobId(db, "Walker")).query();
    }


    public void testTableNull(){
        assertTrue(db.get("Jobs") != null);
        assertTrue(db.get("Persons") != null);
    }
}
