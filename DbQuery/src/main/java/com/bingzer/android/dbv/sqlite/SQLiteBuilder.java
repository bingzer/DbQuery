package com.bingzer.android.dbv.sqlite;

import android.content.Context;

import com.bingzer.android.dbv.IDatabase;

/**
 * Created by 11856 on 7/17/13.
 */
public interface SQLiteBuilder extends IDatabase.Builder {

    public Context getContext();

}
