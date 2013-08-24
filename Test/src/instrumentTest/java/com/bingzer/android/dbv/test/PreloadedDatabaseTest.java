package com.bingzer.android.dbv.test;

import android.content.Context;
import android.os.Environment;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ricky on 8/16/13.
 */
public class PreloadedDatabaseTest extends AndroidTestCase {
    IDatabase db;
    File dbFile;

    @Override
    public void setUp(){
        // http://chinookdatabase.codeplex.com/wikipage?title=Chinook_Schema&referringTitle=Home
        // chinook sample db
        dbFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Chinook.sqlite");

        if(!dbFile.exists()){
            try{
                IOHelper.copyFile(getContext().getResources().getAssets().open("Chinook.sqlite"), dbFile);
            }
            catch (IOException e){
                throw new Error(e);
            }
        }

        db = DbQuery.getDatabase("Chinook.sqlitex");
        db.open(1, dbFile.getAbsolutePath(), new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return PreloadedDatabaseTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {

            }
        });
    }

    public void testDatabaseContent(){
        assertTrue(db.getTables().size() > 0);
        assertTrue(db.getVersion() == 1);
    }

    public void testGetName(){
        assertEquals("Chinook.sqlitex", db.getName());
    }

    @Override
    public void tearDown(){
        db.close();
        assertTrue("Cannot delete file", dbFile.delete());
    }
}
