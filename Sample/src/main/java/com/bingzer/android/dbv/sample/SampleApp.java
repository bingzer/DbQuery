package com.bingzer.android.dbv.sample;

import android.app.Application;
import android.content.Context;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.internal.SQLiteBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ricky on 8/17/13.
 */
public class SampleApp extends Application {

    File dbFile = null;

    @Override
    public void onCreate() {
        super.onCreate();

        extractDbFile();
        IDatabase db = DbQuery.getDatabase("Chinook");
        db.getConfig().setReadOnly(true);
        db.getConfig().setAppendTableNameForId(true);
        db.open(1, dbFile.getAbsolutePath(), new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return getApplicationContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                // do nothing
            }
        });
    }

    private void extractDbFile(){
        // transfer the db file to sd card
        dbFile = new File(getBaseContext().getFilesDir(), "DbQuery-Sample.sqlite");
        if(!dbFile.exists()){
            try{
                IOHelper.copyFile(getResources().getAssets().open("Chinook.sqlite"), dbFile);
            }
            catch (IOException e){
                throw new Error(e);
            }
        }
    }
}
