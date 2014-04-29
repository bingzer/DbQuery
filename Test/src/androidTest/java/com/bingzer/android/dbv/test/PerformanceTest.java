package com.bingzer.android.dbv.test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.internal.Database;
import com.bingzer.android.dbv.SQLiteBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ricky on 8/18/13.
 */
public class PerformanceTest extends AndroidTestCase{

    IDatabase db;
    SQLiteDatabase sqLiteDatabase;
    File dbFile;

    @Override
    public void setUp(){
        // http://chinookdatabase.codeplex.com/wikipage?title=Chinook_Schema&referringTitle=Home
        // chinook sample readonlyDb
        dbFile = new File(getContext().getFilesDir(), "Chinook.sqlite");

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
                return PerformanceTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {

            }

            @Override
            public void onReady(IDatabase db){
            }
        });

        sqLiteDatabase = ((Database)db).getSQLiteDatabase();
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public void testPerformance_SelectAll(){
        long rawNano = System.nanoTime();
        inspectCursor(sqLiteDatabase.rawQuery("SELECT * FROM Track", null));
        rawNano = System.nanoTime() - rawNano;

        long rawDbQuery = System.nanoTime();
        inspectCursor(db.from("Track").select().query());
        rawDbQuery = System.nanoTime() - rawDbQuery;

        long diff = rawDbQuery - rawNano;
        System.out.println("Raw nano     : " + rawNano);
        System.out.println("Db Query nano: " + rawDbQuery);
        System.out.println("Differences  : " + diff);
        System.out.println();

        if(rawDbQuery > rawNano){
            checkNano(diff);
        }
        assertTrue(true);
    }

    public void testPerformance_SelectJoinAll(){
        long rawNano = System.nanoTime();
        inspectCursor(sqLiteDatabase.rawQuery(
                "SELECT * FROM Track " +
                "JOIN Album  ON Album.AlbumId = Track.AlbumId " +
                "JOIN Artist ON Album.ArtistId = Artist.ArtistId ",
                null));
        rawNano = System.nanoTime() - rawNano;

        long rawDbQuery = System.nanoTime();
        inspectCursor(db.from("Track")
                .join("Album", "Album.AlbumId = Track.AlbumId")
                .join("Artist", "Album.ArtistId = Artist.ArtistId")
                .select()
                .query());
        rawDbQuery = System.nanoTime() - rawDbQuery;

        long diff = rawDbQuery - rawNano;
        System.out.println("Raw nano     : " + rawNano);
        System.out.println("Db Query nano: " + rawDbQuery);
        System.out.println("Differences  : " + diff);
        System.out.println();

        if(rawDbQuery > rawNano){
            checkNano(diff);
        }
        assertTrue(true);
    }

    // TODO: More tests

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    private void inspectCursor(Cursor cursor){
        if(cursor != null){
            while(cursor.moveToNext()){
                // do nothing
                ;
            }
            cursor.close();
        }
    }

    private void checkNano(long nano){
        assertTrue("Slow performance", (Math.abs(nano)*1E+6) > 100);
    }
}
