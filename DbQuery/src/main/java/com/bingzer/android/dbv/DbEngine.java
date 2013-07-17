package com.bingzer.android.dbv;

import com.bingzer.android.dbv.sqlite.Database;

/**
 * Created by 11856 on 7/16/13.
 */
public class DbEngine {

    public static IDatabase getDatabase(String databaseName){
        return new Database(databaseName);
    }
}
