package com.bingzer.android.dbv.test;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bingzer.android.dbv.DbEngine;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.MigrationMode;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

public class MainActivity extends Activity {

    TextView textView;
    IDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_start){
            textView.setText("");
            new Thread(new Runnable(){
                public void run(){
                    initDb();
                    populateData();
                }
            }).start();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDb(){
        appendText("Initializing db");
        db = DbEngine.getDatabase("TestDb");
        db.create(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return getApplicationContext();
            }

            @Override
            public MigrationMode getMigrationMode() {
                return MigrationMode.DropIfExists;
            }

            @Override
            public void onCreate(IDatabase.Modeling modeling) {
                appendText("SQLiteBuilder.onCreate()");
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

        appendText("** Emptying out tables");
        // empty all tables
        db.get("Persons").delete().query();
        appendText("-- Deleting Table PERSONS. Count = " + db.get("Persons").count());
        db.get("Jobs").delete().query();
        appendText("-- Deleting Table JOBS. Count = " + db.get("Jobs").count());
    }

    private void populateData(){
        appendText("** Populating with data");
        ITable jobTable = db.get("Jobs");
        // populate jobs..
        jobTable.insert("Name").values("Driver").query();
        jobTable.insert("Name").values("Cook").query();
        jobTable.insert("Name").values("Comedian").query();
        jobTable.insert("Name").values("Programmer").query();
        jobTable.insert("Name").values("Cop").query();
        appendText("-- Job Table count = " + jobTable.count());


        ITable personTable = db.get("Persons");
        personTable.insert("Name", "Age", "JobId").values("John Doe", 21, getJobId("Driver")).query();
        personTable.insert("Name", "Age", "JobId").values("Jane Doe", 25, getJobId("Walker")).query();
        appendText("-- Job Table count = " + personTable.count());
    }

    private int getJobId(String jobName){
        Cursor c = db.get("Jobs").select("Name = ?", jobName).columns("Id").query();
        try{
            if(c.moveToFirst()) return c.getInt(0);
        }
        finally {
            c.close();
        }
        return -1;
    }

    private void appendText(final CharSequence text){
        textView.post(new Runnable(){
            public void run(){
                textView.append(text);
                textView.append("\n");
            }
        });
    }
}
