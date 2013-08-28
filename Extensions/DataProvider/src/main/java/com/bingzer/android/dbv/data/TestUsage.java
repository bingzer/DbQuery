package com.bingzer.android.dbv.data;

import android.content.Context;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 8/28/13.
 */
class TestUsage {

    public void test(){

    }

    static class BasicProvider extends BasicDataProvider {

        @Override
        public IDatabase openDatabase() {
            IDatabase db = DbQuery.getDatabase("MyDb");
            db.open(1, new SQLiteBuilder() {
                @Override
                public Context getContext() {
                    return BasicProvider.this.getContext();
                }

                @Override
                public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                    // do modeling and stuffs
                }
            });

            return db;
        }

        @Override
        public String getAuthority() {
            return "com.bingzer.android.dbv.data";
        }
    }
}
