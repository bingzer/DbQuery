package com.bingzer.android.dbv.sample.activities;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;

/**
 * Created by Ricky on 8/17/13.
 */
public class AlbumDetail extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long artistId = getIntent().getExtras().getLong("ArtistId");

        IDatabase db = DbQuery.getDatabase("Chinook");

        Cursor cursor = db.from("Album").select("ArtistId = ?", artistId).columns("*", "rowid as _id").query();
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_1,
                        cursor,
                        new String[]{"Title"},
                        new int[]{ android.R.id.text1});

        // Bind to our new adapter.
        setListAdapter(adapter);
    }
}
