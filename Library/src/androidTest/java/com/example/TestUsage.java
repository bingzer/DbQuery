package com.example;

import android.content.Context;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.Environment;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IEntity;
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
        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {

            }
        });
    }

    public class EntityTest implements IEntity {

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public void map(Mapper mapper) {
        }
    }
}
