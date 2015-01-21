/**
 * Copyright 2014 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bingzer.android.dbv.providers;

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
 * Provides a very "basic" data provider implementation.
 * This provider will expose all tables in the specified
 * {@link IDatabase} defined in {@link com.bingzer.android.dbv.providers.DataProvider#openDatabase()}
 *
 * @see {@link IDatabase}
 */
public abstract class DataProvider extends ContentProvider implements IDataProvider{

    protected IDatabase db;

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

        return getTable(uri)
                .select(selection, (Object[])selectionArgs)
                .columns(projections)
                .orderBy(sortOrder)
                .query();
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = getTable(uri).insert(contentValues).query();
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return getTable(uri)
                .delete(selection,  (Object[]) selectionArgs)
                .query();
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return getTable(uri)
                .update(contentValues, selection,  (Object[]) selectionArgs)
                .query();
    }

    @Override
    public String getType(Uri uri) {
        ITable table = getTable(uri);

        if(extractIdFromUri(uri) > 0) return "vnd.android.cursor.item/" + table.getName();
        else return "vnd.android.cursor.dir/" + table.getName();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ITable getTable(Uri uri){
        ensureDbIsOpen();
        Matcher m = getUriPattern().matcher(uri.toString());
        if(m.find()){
            return db.from(m.group(3));
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
