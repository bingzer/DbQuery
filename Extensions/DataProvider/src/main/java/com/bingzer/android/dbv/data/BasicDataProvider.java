package com.bingzer.android.dbv.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.ITable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ricky Tobing on 8/28/13.
 */
public abstract class BasicDataProvider extends ContentProvider{
    IDatabase db;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public abstract IDatabase openDatabase();
    public abstract String getAuthority();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreate() {
        // do nothing
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        int id = extractIdFromUri(uri);
        if(id > 0){
            selection = selection + " " + db.getConfig().getIdNamingConvention() + " = " + id;
        }

        Cursor cursor = getTable(uri)
                .select(selection, selectionArgs)
                .columns(projections)
                .orderBy(sortOrder)
                .query();
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int id = getTable(uri).insert(contentValues).query();
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numDeleted = getTable(uri)
                .delete(selection, selectionArgs)
                .query();

        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int numUpdated = getTable(uri)
                .update(contentValues, selection, selectionArgs)
                .query();

        return numUpdated;
    }

    @Override
    public String getType(Uri uri) {
        ITable table = getTable(uri);

        if(extractIdFromUri(uri) > 0) return "vnd.android.cursor.item/" + table.getName();
        if(extractIdFromUri(uri) > 0) return "vnd.android.cursor.item/" + table.getName();
        else return "vnd.android.cursor.dir/" + table.getName();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ITable getTable(Uri uri){
        ensureDbIsOpen();
        Matcher m = getUriPattern().matcher(uri.toString());
        if(m.find()){
            return db.get(m.group(3));
        }
        return null;
    }

    void ensureDbIsOpen(){
        if(db == null) db = openDatabase();
    }

    int extractIdFromUri(Uri uri){
        try{
            return (int) ContentUris.parseId(uri);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }

    Pattern getUriPattern(){
        return Pattern.compile(String.format("(content://)(%s)/(\\w+)/?((\\d+)|(#)|(\\*))?", getAuthority()));
    }
}
