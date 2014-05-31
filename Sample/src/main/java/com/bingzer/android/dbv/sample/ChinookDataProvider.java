package com.bingzer.android.dbv.sample;

import android.content.Context;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.SQLiteBuilder;
import com.bingzer.android.dbv.providers.DataProvider;

import java.io.File;
import java.io.IOException;

public class ChinookDataProvider extends DataProvider {

    @Override
    public IDatabase openDatabase() {
        IDatabase db;
        File dbFile = new File(getContext().getFilesDir(), "Chinook.sqlite");

        if(!dbFile.exists()){
            try{
                IOHelper.copyFile(getContext().getResources().getAssets().open("Chinook.sqlite"), dbFile);
            }
            catch (IOException e){
                throw new Error(e);
            }
        }

        db = DbQuery.getDatabase("Chinook");
        db.open(1, dbFile.getAbsolutePath(), new SQLiteBuilder.WithoutModeling(getContext()));

        return db;
    }

    @Override
    public String getAuthority() {
        return "com.bingzer.android.dbv.providers.sample";
    }
}
