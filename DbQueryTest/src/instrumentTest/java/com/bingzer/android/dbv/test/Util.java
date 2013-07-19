package com.bingzer.android.dbv.test;

import android.database.Cursor;

import com.bingzer.android.dbv.IDatabase;

/**
 * Created by Ricky Tobing on 7/18/13.
 */
public class Util {

    static int getJobId(IDatabase db, String jobName){
        Cursor c = db.get("Jobs").select("Name = ?", jobName).columns("Id").query();
        try{
            if(c.moveToFirst()) return c.getInt(0);
        }
        finally {
            c.close();
        }
        return -1;
    }
}
