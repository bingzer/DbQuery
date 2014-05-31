package com.bingzer.android.dbv.sample;

import android.app.Application;
import android.content.Context;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.SQLiteBuilder;

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
        db.open(1, dbFile.getAbsolutePath(), new SQLiteBuilder.WithoutModeling(this));
    }

    private void extractDbFile(){
        // transfer the db file to sd card
        dbFile = new File(getBaseContext().getFilesDir(), "Chinook.sqlite");
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
