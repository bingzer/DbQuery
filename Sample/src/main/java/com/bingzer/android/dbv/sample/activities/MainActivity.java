package com.bingzer.android.dbv.sample.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.sample.R;

/**
 * Chinook sample db is based on
 *
 * http://chinookdatabase.codeplex.com/wikipage?title=Chinook_Schema&referringTitle=Home
 */
public class MainActivity extends Activity {

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), AlbumDetail.class);
                intent.putExtra("ArtistId", id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        populate(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_all)
            return populate(false);
        if(item.getItemId() == R.id.action_album_only)
            return populate(true);
        return false;
    }


    private boolean populate(boolean filtered){
        IDatabase db = DbQuery.getDatabase("Chinook");
        Cursor cursor;

        if(!filtered)
            cursor = db.from("Artist")
                    .select()
                    .columns("*", "rowid as _id")
                    .orderBy("Name")
                    .query();
        else
            cursor = db.from("Artist A")
                .join("Album B", "A.ArtistId = B.ArtistId")
                .select("B.Title is not null")
                .columns("A.Name", "A.ArtistId as _id")
                .groupBy("A.Name")
                .query();

        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_1,
                        cursor,
                        new String[]{"Name"},
                        new int[]{ android.R.id.text1});
        mListView.setAdapter(adapter);

        return true;
    }
}
