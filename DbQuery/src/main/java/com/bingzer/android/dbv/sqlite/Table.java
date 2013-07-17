/**
 * Copyright 2013 Ricky Tobing
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

package com.bingzer.android.dbv.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.Util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 11856 on 7/16/13.
 */
class Table implements ITable {

    private String name;
    private List<String> columns;
    private SQLiteDatabase db;

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    Table (SQLiteDatabase sqLiteDatabase, String name){
        this.name = name;
        this.db = sqLiteDatabase;
        this.columns = new LinkedList<String>();

        Cursor cursor = db.rawQuery("PRAGMA table_info(" + name + ")", null);
        try{
            int nameIdx = cursor.getColumnIndexOrThrow("name");
            /*
            int typeIdx = cursor.getColumnIndexOrThrow("type");
            int notNullIdx = cursor.getColumnIndexOrThrow("notnull");
            int dfltValueIdx = cursor.getColumnIndexOrThrow("dflt_value");
            ArrayList<String> integerDefault1NotNull = new ArrayList<String>();
            */
            while (cursor.moveToNext()) {
                columns.add(cursor.getString(nameIdx));

                /*
                String type = cursor.getString(typeIdx);
                if ("INTEGER".equals(type)) {
                    // Integer column
                    if (cursor.getInt(notNullIdx) == 1) {
                        // NOT NULL
                        String defaultValue = cursor.getString(dfltValueIdx);
                        if ("1".equals(defaultValue)) {
                            integerDefault1NotNull.add(cursor.getString(nameIdx));
                        }
                    }
                }
                */
            }
        }
        catch (Throwable e){
            e.printStackTrace();
        }
        finally {
            cursor.close();
        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    /**
     * Returns the name
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the column name
     *
     * @return
     */
    @Override
    public List<String> getColumns() {
        return columns;
    }

    /**
     * Returns the column count
     *
     * @return
     */
    @Override
    public int getColumnCount() {
        if(columns != null){
            return columns.size();
        }
        return  0;
    }

    /**
     * Select some condition
     *
     * @param condition
     * @return
     */
    @Override
    public IQuery.Select select(String condition) {
        return select(condition, (Object)null);
    }

    /**
     * Select top (x) add the specified condition
     *
     * @param top
     * @param condition
     * @return
     */
    @Override
    public IQuery.Select select(int top, String condition) {
        return select(top, condition, (Object)null);
    }

    /**
     * Select add id
     *
     * @param id
     * @return
     */
    @Override
    public IQuery.Select select(int id) {
        return select("Id=?", id);
    }

    /**
     * Select add whereClause
     *
     * @param whereClause
     * @param args
     * @return
     */
    @Override
    public IQuery.Select select(String whereClause, Object... args) {
        return select(-1, whereClause, args);
    }

    /**
     * Select
     *
     * @param whereClause
     * @param args
     * @return
     */
    @Override
    public IQuery.Select select(int top, String whereClause, Object... args) {
        Queryable.Select query = new Queryable.Select("SELECT "){
            @Override public Cursor query(){
                return db.rawQuery(toString(), null);
            };
        };

        if(top > 0) query.append(" TOP ").append(top);
        else query.append(" * ");

        query.append(" FROM ").append(getName());
        if(whereClause != null){
            // append where if necessary
            if(!whereClause.toLowerCase().startsWith("where"))
                query.append(" WHERE ");
            // safely prepare the where part
            query.append(Util.prepareWhereClause(whereClause, args));
        }

        return query;
    }

    /**
     * Select distinct
     *
     * @param condition
     * @return
     */
    @Override
    public IQuery.Select selectDistinct(String condition) {
        return selectDistinct(condition, (Object)null);
    }

    /**
     * Select distinct add condition
     *
     * @param whereClause
     * @param args
     * @return
     */
    @Override
    public IQuery.Select selectDistinct(String whereClause, Object... args) {
        Queryable.Select query = new Queryable.Select("SELECT DISTINCT "){
            @Override public Cursor query(){
                return db.rawQuery(toString(), null);
            };
        };

        query.append(" FROM ").append(getName());
        if(whereClause != null){
            // append where if necessary
            if(!whereClause.toLowerCase().startsWith("where"))
                query.append(" WHERE ");
            // safely prepare the where part
            query.append(Util.prepareWhereClause(whereClause, args));
        }

        return query;
    }

    /**
     * InsertWith content values
     *
     * @param contents
     * @return
     */
    @Override
    public IQuery.Insert insert(final ContentValues contents) {
        Queryable.Insert query = new Queryable.Insert(new IQuery<Integer>() {
            @Override public Integer query() {
                return (int) db.insert(getName(), null, contents);
            }
        });

        return query;
    }

    /**
     * InsertWith
     *
     * @param columns
     * @param values
     * @return
     */
    @Override
    public IQuery.Insert insert(String[] columns, Object[] values) {
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            // TODO: fix data type
            contentValues.put(columns[i], values[i].toString());
        }

        return insert(contentValues);
    }

    /**
     * InsertWith
     *
     * @param columns
     * @return
     */
    @Override
    public IQuery.Insert insert(String... columns) {
        Queryable.Insert query = new Queryable.Insert(new Queryable.Insert.IQueryableAppendable() {
            private ContentValues contentValues;

            @Override
            public void onContentValuesSet(ContentValues contentValues) {
                this.contentValues = contentValues;
            }

            @Override
            public Integer query() {
                if(contentValues == null) {
                    // todo: contentValues not set should display error here
                }
                return (int) db.insert(getName(), null, contentValues);
            }
        }, columns);

        return query;
    }

    /**
     * Update a column add its id
     *
     * @param column
     * @param value
     * @param id
     * @return
     */
    @Override
    public IQuery.Update update(String column, Object value, int id) {
        return update(column, value, "Id=?", id);
    }

    /**
     * Update a column add specified condition
     *
     * @param column
     * @param value
     * @param condition
     * @return
     */
    @Override
    public IQuery.Update update(String column, Object value, String condition) {
        return update(new String[]{column}, new Object[]{value}, condition);
    }

    /**
     * Update a column add specified condition
     *
     * @param column
     * @param value
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public IQuery.Update update(String column, Object value, String whereClause, Object... whereArgs) {
        return update(new String[]{column}, new Object[]{value}, whereClause, whereArgs);
    }

    /**
     * Bulk-update columns add their values add specified condition.
     *
     * @param columns
     * @param values
     * @param condition
     * @return
     */
    @Override
    public IQuery.Update update(String[] columns, Object[] values, String condition) {
        return update(columns, values, condition, (Object)null);
    }

    /**
     * Bulk-update columns add their values add specified condition.
     *
     * @param columns
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public IQuery.Update update(String[] columns, Object[] values, String whereClause, Object... whereArgs) {
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            // todo:
            contentValues.put(columns[i], values[i].toString());
        }

        return update(contentValues, whereClause, Util.toStringArray(whereArgs));
    }

    /**
     * Update using the contentvalues
     *
     * @param contents
     * @return
     */
    @Override
    public IQuery.Update update(ContentValues contents) {
        return update(contents, null);
    }

    /**
     * Update using contentvalues with specified id
     *
     * @param contents
     * @param id
     * @return
     */
    @Override
    public IQuery.Update update(ContentValues contents, int id) {
        return update(contents, "Id=?", id);
    }

    /**
     * Update using the contentvalues
     *
     * @param contents
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public IQuery.Update update(final ContentValues contents, final String whereClause, final Object... whereArgs) {
        Queryable.Update query = new Queryable.Update(new IQuery<Integer>() {
            @Override
            public Integer query() {
                String[] args = Util.toStringArray(whereArgs);
                return db.update(getName(), contents, whereClause, args);
            }
        });

        return query;
    }

    /**
     * Delete by id
     *
     * @param id
     * @return
     */
    @Override
    public IQuery.Delete delete(final int id) {
        return delete("Id=?", id);
    }

    /**
     * Bulk-remove by multiple ids
     *
     * @param ids
     * @return
     */
    @Override
    public IQuery.Delete delete(int... ids) {
        StringBuilder whereClause = new StringBuilder().append("IN (");
        for(int i = 0; i < ids.length; i++){
            whereClause.append(ids[i]);
            if(i < ids.length - 1){
                whereClause.append(",");
            }
        }
        whereClause.append(")");

        return delete(whereClause.toString(), (Object)null);
    }

    /**
     * Bulk-remove by multiple ids
     *
     * @param ids
     * @return
     */
    @Override
    public IQuery.Delete delete(Collection<Integer> ids) {
        int[] idz = new int[ids.size()];
        int counter = 0;
        java.util.Iterator<Integer> iterator = ids.iterator();
        while (iterator.hasNext()){
            idz[counter++] = iterator.next();
        }

        return delete(idz);
    }

    /**
     * Delete add specified condition
     *
     * @param condition
     * @return
     */
    @Override
    public IQuery.Delete delete(final String condition) {
        return delete(condition, (Object)null);
    }

    /**
     * Delete add sepcified where clause
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public IQuery.Delete delete(final String whereClause, final Object... whereArgs) {
        IQuery.Delete query = new Queryable.Delete(new IQuery<Integer>() {
            @Override
            public Integer query() {
                return db.delete(getName(), whereClause, Util.toStringArray(whereArgs));
            }
        });

        return query;
    }

    /**
     * Empty the table
     *
     * @return
     */
    @Override
    public IQuery.Delete deleteAll() {
        return delete("1=1");
    }

    /**
     * Check to see if this table has row add the spcified condition
     *
     * @param condition
     * @return
     */
    @Override
    public boolean hasRow(String condition) {
        return hasRow(condition, (Object)null);
    }

    /**
     * has row add id
     *
     * @param id
     * @return
     */
    @Override
    public boolean hasRow(int id) {
        return hasRow("Id=?", id);
    }

    /**
     * Check to see if this table has row add the specified clause and condition
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public boolean hasRow(String whereClause, Object... whereArgs) {
        Cursor cursor = select(whereClause, whereArgs).query();
        try{
            if(cursor.moveToNext()){
                return true;
            }
        }
        finally {
            cursor.close();
        }
        return false;
    }

    /**
     * Returns the count of the specified condition
     *
     * @param condition
     * @return
     */
    @Override
    public int count(String condition) {
        return count(condition, (Object)null);
    }

    /**
     * Returns the count of row from the whereClause
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public int count(String whereClause, Object... whereArgs) {
        int count = 0;

        StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM " + getName());
        if(whereClause != null){
            builder.append(" WHERE ");
            builder.append(Util.prepareWhereClause(whereClause, whereArgs));
        }

        Cursor cursor = db.rawQuery(builder.toString(), null);
        try{
            if(cursor.moveToNext()){
                count = cursor.getInt(0);
            }
        }
        finally {
            cursor.close();
        }
        return count;
    }

    /**
     * Returns the total row available in this table
     *
     * @return
     */
    @Override
    public int count() {
        return count(null);
    }

    /**
     * Build raw sql
     *
     * @param sql
     * @return
     */
    @Override
    public IQuery<Cursor> raw(final String sql) {
        return raw(sql, (String)null);
    }

    /**
     * Build raw sql
     *
     * @param sql
     * @return
     */
    @Override
    public IQuery<Cursor> raw(final String sql, final String... selectionArgs) {
        IQuery<Cursor> query = new Queryable<Cursor>(){
            @Override public Cursor query(){
                return db.rawQuery(sql, selectionArgs);
            }
        };
        return query;
    }

    /**
     * Drop sql
     *
     * @return
     */
    @Override
    public IQuery<Boolean> drop() {
        IQuery<Boolean> query = new Queryable<Boolean>(){
            @Override public Boolean query(){
                try{
                    db.rawQuery("DROP TABLE " + getName(), null);
                    return true;
                }
                catch (Exception e){
                    return false;
                }
            }
        };
        return query;
    }
}
