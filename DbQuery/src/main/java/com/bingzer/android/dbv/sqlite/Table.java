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

import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.Util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
class Table implements ITable {

    private String name;
    private String alias;
    private List<String> columns;
    private IDatabase db;
    private SQLiteDatabase sqlDb;

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    Table (Database db, SQLiteDatabase sqlDb, String name){
        this.name = name;
        this.db = db;
        this.sqlDb = sqlDb;
        this.columns = new LinkedList<String>();

        Cursor cursor = sqlDb.rawQuery("PRAGMA table_info(" + name + ")", null);
        try{
            int nameIdx = cursor.getColumnIndexOrThrow("name");
            while (cursor.moveToNext()) {
                columns.add(cursor.getString(nameIdx));
            }
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
     * Sets the current alias of this table
     *
     * @param alias
     */
    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * This table alias
     *
     * @return
     */
    @Override
    public String getAlias() {
        return alias;
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
     * SelectImpl some condition
     *
     * @param condition
     * @return
     */
    @Override
    public IQuery.Select select(String condition) {
        return select(condition, (Object)null);
    }

    /**
     * SelectImpl top (x) add the specified condition
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
     * SelectImpl add id
     *
     * @param id
     * @return
     */
    @Override
    public IQuery.Select select(int id) {
        return select(db.getConfig().getIdNamingConvention() + " = ?", id);
    }

    /**
     * SelectImpl multiple ids
     *
     * @param ids
     * @return
     */
    @Override
    public IQuery.Select select(int... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();
            whereClause.append(db.getConfig().getIdNamingConvention()).append(" ");
            whereClause.append(" IN (");
            for(int i = 0; i < ids.length; i++){
                whereClause.append(ids[i]);
                if(i < ids.length - 1){
                    whereClause.append(",");
                }
            }
            whereClause.append(")");

            return select(whereClause.toString());
        }
        else{
            // select all
            return select((String)null);
        }
    }

    /**
     * SelectImpl add whereClause
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
     * SelectImpl
     *
     * @param whereClause
     * @param args
     * @return
     */
    @Override
    public IQuery.Select select(int top, String whereClause, Object... args) {
        QueryImpl.SelectImpl query = new QueryImpl.SelectImpl(toString(), top, false){
            @Override public Cursor query(){
                return sqlDb.rawQuery(toString(), null);
            };
        };

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
     * SelectImpl distinct
     *
     * @param condition
     * @return
     */
    @Override
    public IQuery.Select selectDistinct(String condition) {
        return selectDistinct(condition, (Object)null);
    }

    /**
     * SelectImpl distinct add condition
     *
     * @param whereClause
     * @param args
     * @return
     */
    @Override
    public IQuery.Select selectDistinct(String whereClause, Object... args) {
        QueryImpl.SelectImpl query = new QueryImpl.SelectImpl(toString(), true){
            @Override public Cursor query(){
                return sqlDb.rawQuery(toString(), null);
            };
        };

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
        IQuery.Insert query = new QueryImpl.InsertImpl(new IQuery<Integer>() {
            @Override public Integer query() {
                return (int) sqlDb.insertOrThrow(getName(), null, contents);
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
        IQuery.Insert query = new QueryImpl.InsertImpl(new QueryImpl.InsertImpl.IQueryableAppendable() {
            private ContentValues contentValues;

            @Override
            public void onContentValuesSet(ContentValues contentValues) {
                this.contentValues = contentValues;
            }

            @Override
            public Integer query() {
                if(contentValues == null) {
                    throw new IllegalArgumentException("ContentValues are not specified. Use IQuery.Insert.values()");
                }
                return (int) sqlDb.insertOrThrow(getName(), null, contentValues);
            }
        }, columns);

        return query;
    }

    /**
     * UpdateImpl a column add its id
     *
     * @param column
     * @param value
     * @param id
     * @return
     */
    @Override
    public IQuery.Update update(String column, Object value, int id) {
        return update(column, value, db.getConfig().getIdNamingConvention() + " = ?", id);
    }

    /**
     * UpdateImpl a column add specified condition
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
     * UpdateImpl a column add specified condition
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

        return update(contentValues, whereClause, Util.toStringArray((Object[])whereArgs));
    }

    /**
     * UpdateImpl using the contentvalues
     *
     * @param contents
     * @return
     */
    @Override
    public IQuery.Update update(ContentValues contents) {
        return update(contents, null);
    }

    /**
     * UpdateImpl using contentvalues with specified id
     *
     * @param contents
     * @param id
     * @return
     */
    @Override
    public IQuery.Update update(ContentValues contents, int id) {
        return update(contents, db.getConfig().getIdNamingConvention() + " = ?", id);
    }

    /**
     * UpdateImpl using the contentvalues
     *
     * @param contents
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public IQuery.Update update(final ContentValues contents, final String whereClause, final Object... whereArgs) {
        IQuery.Update query = new QueryImpl.UpdateImpl(new IQuery<Integer>() {
            @Override
            public Integer query() {
                String[] args = Util.toStringArray(whereArgs);
                return sqlDb.update(getName(), contents, whereClause, args);
            }
        });

        return query;
    }

    /**
     * DeleteImpl by id
     *
     * @param id
     * @return
     */
    @Override
    public IQuery.Delete delete(final int id) {
        return delete(db.getConfig().getIdNamingConvention() + " = ?", id);
    }

    /**
     * Bulk-remove by multiple ids
     *
     * @param ids
     * @return
     */
    @Override
    public IQuery.Delete delete(int... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();

            whereClause.append(db.getConfig().getIdNamingConvention()).append(" ");
            whereClause.append("IN (");
            for(int i = 0; i < ids.length; i++){
                whereClause.append(ids[i]);
                if(i < ids.length - 1){
                    whereClause.append(",");
                }
            }
            whereClause.append(")");

            return delete(whereClause.toString(), (Object)null);
        }
        else{
            // delete all
            return deleteAll();
        }
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
     * DeleteImpl add specified condition
     *
     * @param condition
     * @return
     */
    @Override
    public IQuery.Delete delete(final String condition) {
        return delete(condition, (Object)null);
    }

    /**
     * DeleteImpl add sepcified where clause
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public IQuery.Delete delete(final String whereClause, final Object... whereArgs) {
        IQuery.Delete query = new QueryImpl.DeleteImpl(new IQuery<Integer>() {
            @Override
            public Integer query() {

                return sqlDb.delete(getName(), whereClause, Util.toStringArray((Object[]) whereArgs));
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
        return hasRow(db.getConfig().getIdNamingConvention() + " = ?", id);
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
        return count(whereClause, whereArgs) > 0;
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

        StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM " + toString());
        if(whereClause != null){
            builder.append(" WHERE ");
            builder.append(Util.prepareWhereClause(whereClause, whereArgs));
        }

        Cursor cursor = sqlDb.rawQuery(builder.toString(), null);
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
        IQuery<Cursor> query = new QueryImpl<Cursor>(){
            @Override public Cursor query(){
                return sqlDb.rawQuery(sql, selectionArgs);
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
        IQuery<Boolean> query = new QueryImpl<Boolean>(){
            @Override public Boolean query(){
                try{
                    sqlDb.rawQuery("DROP TABLE " + getName(), null);
                    return true;
                }
                catch (Exception e){
                    return false;
                }
            }
        };
        return query;
    }

    /**
     * Joins a table
     *
     * @param tableName
     * @param onClause
     * @return
     */
    @Override
    public IQuery.InnerJoin join(String tableName, String onClause) {
        IQuery.InnerJoin query = new QueryImpl.InnerJoinImpl(this, tableName, onClause){
            @Override public Cursor query(){
                return sqlDb.rawQuery(toString(), null);
            }
        };

        // returns
        return query;
    }

    /**
     * Joins a table
     *
     * @param tableName
     * @param column1
     * @param column2
     * @return
     */
    @Override
    public IQuery.InnerJoin join(String tableName, String column1, String column2) {
        return join(tableName, name + "." + column1 + " = " + tableName + "." + column2);
    }

    /**
     * Joins a table
     *
     * @param tableName
     * @param onClause
     * @return
     */
    @Override
    public IQuery.OuterJoin outerJoin(String tableName, String onClause) {
        IQuery.OuterJoin query = new QueryImpl.OuterJoinImpl(this, tableName, onClause){
            @Override public Cursor query(){
                return sqlDb.rawQuery(toString(), null);
            }
        };

        // returns
        return query;
    }

    /**
     * Joins a table
     *
     * @param tableName
     * @param column1
     * @param column2
     * @return
     */
    @Override
    public IQuery.OuterJoin outerJoin(String tableName, String column1, String column2) {
        return outerJoin(tableName, name + "." + column1 + " = " + tableName + "." + column2);
    }

    /**
     * To String.
     * If
     * @return
     */
    @Override
    public String toString(){
        if(getAlias() != null && getAlias().length() > 0)
            return getName() + " " + getAlias();
        return getName();
    }
}
