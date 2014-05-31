package com.example;

import android.content.Context;

import com.bingzer.android.dbv.Environment;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IEnvironment;
import com.bingzer.android.dbv.SQLiteBuilder;

@SuppressWarnings("ALL")
public class TestUsage {

    private void init(){
        IDatabase db = null;
        IEnvironment customEnvironment = new Environment(db);

        db.open(1, new SQLiteBuilder(customEnvironment) {
            @Override
            public Context getContext() {
                return null;
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {

            }
        });
    }
}
