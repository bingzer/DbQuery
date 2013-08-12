/**
 * Copyright 2013 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance insert the License.
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
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IFunction;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.Util;

import java.util.Collection;
import java.util.Iterator;
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

        String pragmaSql = new StringBuilder("PRAGMA table_info(").append(name).append(")").toString();
        Cursor cursor = sqlDb.rawQuery(pragmaSql, null);
        try{
            // this will throw IllegalArgumentException if not found
            // meaning that this table does not exist
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
     * Returns id
     *
     * @param condition
     * @return
     */
    @Override
    public int selectId(String condition) {
        return selectId(condition, (Object)null);
    }

    /**
     * Returns id
     *
     * @param whereClause
     * @param args
     * @return
     */
    @Override
    public int selectId(String whereClause, Object... args) {
        int id = -1;
        Cursor cursor = select(whereClause, args).columns(db.getConfig().getIdNamingConvention()).query();
        if(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
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
        return select(generateParamId(id));
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
        QueryImpl.SelectImpl query = new QueryImpl.SelectImpl(db.getConfig(), this, top, false){
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
     * Select distinct all.
     * Equivalent of calling <code>selectDistinct(null)</code>
     *
     * @return
     */
    @Override
    public IQuery.Select selectDistinct() {
        return selectDistinct(null);
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
        QueryImpl.SelectImpl query = new QueryImpl.SelectImpl(db.getConfig(), this, true){
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
     * InsertWith content val
     *
     * @param contents
     * @return
     */
    @Override
    public IQuery.Insert insert(final ContentValues contents) {
        QueryImpl.InsertImpl query = new QueryImpl.InsertImpl();
        query.value = (int) sqlDb.insertOrThrow(getName(), null, contents);

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
        final ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            ContentUtil.mapContentValuesFromGenericObject(contentValues, columns[i], values[i]);
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
    public IQuery.InsertWith insert(String... columns) {
        QueryImpl.InsertWithImpl query = new QueryImpl.InsertWithImpl(new QueryImpl.InsertWithImpl.IQueryableAppendable() {
            private ContentValues contentValues;
            private QueryImpl.InsertWithImpl query;

            @Override
            public void onContentValuesSet(QueryImpl.InsertWithImpl query, ContentValues contentValues) {
                this.query = query;
                this.contentValues = contentValues;
                this.query.value = (int) sqlDb.insertOrThrow(getName(), null, contentValues);
            }

            @Override
            public Integer query() {
                if(contentValues == null)
                    throw new IllegalArgumentException("ContentValues are not specified. Use IQuery.InsertWith.val()");
                // return
                return this.query.value;
            }
        }, columns);

        return query;
    }

    /**
     * Insert an entity.
     *
     * @param entity entity
     * @return an Insert object
     */
    @Override
    public IQuery.Insert insert(IEntity entity) {
        // build content values..
        final EntityMapper mapper = new EntityMapper(db.getConfig());
        final ContentValues contentValues = new ContentValues();
        entity.map(mapper);

        Iterator<String> keys = mapper.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
            // ignore if column = "Id"
            if(key.equalsIgnoreCase(db.getConfig().getIdNamingConvention())) continue;

            IEntity.Action action = mapper.get(key);
            if(action != null){
                ContentUtil.mapContentValuesFromAction(contentValues, key, action);
            }
        }

        return insert(contentValues);
    }

    /**
     * Bulk-insert an entity list
     *
     * @param entityList the entity list to insert
     * @param <E>        extends IEntity
     * @return an Insert object
     */
    @Override
    public <E extends IEntity> IQuery.Insert insert(final IEntityList<E> entityList) {
        final QueryImpl.InsertImpl query = new QueryImpl.InsertImpl();

        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                for(IEntity entity : entityList.getEntityList()){
                    insert(entity);
                    query.value++;
                }
            }
        }).execute();

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
        return update(column, value, generateParamId(id));
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
     * Bulk-update columns add their val add specified condition.
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
     * Bulk-update columns add their val add specified condition.
     *
     * @param columns
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public IQuery.Update update(String[] columns, Object[] values, String whereClause, Object... whereArgs) {
        final ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            ContentUtil.mapContentValuesFromGenericObject(contentValues, columns[i], values[i]);
        }

        return update(contentValues, whereClause, (Object[]) Util.toStringArray(whereArgs));
    }

    /**
     * Update using an {@link com.bingzer.android.dbv.IEntity} object
     *
     * @param entity the entity to update
     * @return
     */
    @Override
    public IQuery.Update update(IEntity entity) {
        if(entity.getId() < 0) throw new IllegalArgumentException("Id has to be over than 0");

        final EntityMapper mapper = new EntityMapper(db.getConfig());
        final ContentValues contentValues = new ContentValues();
        entity.map(mapper);

        Iterator<String> keys = mapper.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
            // ignore if "Id"
            if(key.equalsIgnoreCase(db.getConfig().getIdNamingConvention())) continue;

            IEntity.Action action = mapper.get(key);
            if(action != null){
                ContentUtil.mapContentValuesFromAction(contentValues, key, action);
            }
        }

        return update(contentValues, entity.getId());
    }

    /**
     * Bulk-update using {@link com.bingzer.android.dbv.IEntityList} object
     *
     * @param entityList IEntityList object
     * @param <E>        extends IEntity
     * @return Update object
     */
    @Override
    public <E extends IEntity> IQuery.Update update(final IEntityList<E> entityList) {
        final QueryImpl.UpdateImpl query = new QueryImpl.UpdateImpl();

        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                for(IEntity entity : entityList.getEntityList()){
                    update(entity);
                    query.value++;
                }
            }
        }).execute();

        return query;
    }

    /**
     * UpdateImpl using contentvalues insert specified id
     *
     * @param contents
     * @param id
     * @return
     */
    @Override
    public IQuery.Update update(ContentValues contents, int id) {
        return update(contents, generateParamId(id));
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
        QueryImpl.UpdateImpl query = new QueryImpl.UpdateImpl();
        String[] args = Util.toStringArray(whereArgs);
        query.value = sqlDb.update(getName(), contents, whereClause, args);

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
        return delete(generateParamId(id));
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
        QueryImpl.DeleteImpl query = new QueryImpl.DeleteImpl();
        query.value = sqlDb.delete(getName(), whereClause, Util.toStringArray((Object[]) whereArgs));

        return query;
    }

    /**
     * Delete an entity.
     * This is equivalent of calling
     * <code>delete(entity.getId())</code>
     *
     * @param entity entity to delete
     * @return
     */
    @Override
    public IQuery.Delete delete(IEntity entity) {
        return delete(entity.getId());
    }

    /**
     * Bulk-delete several entities.
     * This is equivalent of calling
     * <code>delete(list-of-ids)</code>
     *
     * @param entityList the entity list
     * @param <E>        extends IEntity
     * @return
     */
    @Override
    public <E extends IEntity> IQuery.Delete delete(IEntityList<E> entityList) {
        int[] ids = new int[entityList.getEntityList().size()];
        return  delete(ids);
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
    public boolean has(String condition) {
        return has(condition, (Object) null);
    }

    /**
     * has row add id
     *
     * @param id
     * @return
     */
    @Override
    public boolean has(int id) {
        return has(generateParamId(id));
    }

    /**
     * Check to see if this table has row add the specified clause and condition
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public boolean has(String whereClause, Object... whereArgs) {
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
        IQuery<Cursor> query = new QueryImpl<Cursor>(db.getConfig()){
            @Override public Cursor query(){
                if(selectionArgs == null || selectionArgs.length == 1 || selectionArgs[0] == null)
                    return sqlDb.rawQuery(sql, null);
                else return sqlDb.rawQuery(sql, selectionArgs);
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
        QueryImpl.DropImpl query = new QueryImpl.DropImpl();
        try{
            db.execSql("DROP TABLE " + getName());
            query.value = true;
        }
        catch (Exception e){
            query.value = false;
        }

        if(query.value) ((Database)db).removeTable(this);
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
        IQuery.InnerJoin query = new QueryImpl.InnerJoinImpl(db.getConfig(), this, tableName, onClause){
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
        String onClause = new StringBuilder(name).append(".").append(column1).append("=")
                                .append(tableName).append(".").append(column2).toString();
        return join(tableName, onClause);
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
        IQuery.OuterJoin query = new QueryImpl.OuterJoinImpl(db.getConfig(), this, tableName, onClause){
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
        String onClause = new StringBuilder(name).append(".").append(column1).append("=")
                .append(tableName).append(".").append(column2).toString();
        return outerJoin(tableName, onClause);
    }

    /**
     * To String.
     * If
     * @return
     */
    @Override
    public String toString(){
        if(getAlias() != null && getAlias().length() > 0)
            return new StringBuilder(getName()).append(" ").append(getAlias()).toString();
        return getName();
    }

    /**
     * Returns average
     *
     * @param columnName
     * @return
     */
    @Override
    public IFunction.Average avg(String columnName) {
        FunctionImpl.AverageImpl fn = new FunctionImpl.AverageImpl(toString(), columnName);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    /**
     * Sum
     *
     * @param columnName
     * @return
     */
    @Override
    public IFunction.Sum sum(String columnName) {
        FunctionImpl.SumImpl fn = new FunctionImpl.SumImpl(toString(), columnName);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    /**
     * Max
     *
     * @param columnName
     * @return
     */
    @Override
    public IFunction.Max max(String columnName) {
        FunctionImpl.MaxImpl fn = new FunctionImpl.MaxImpl(toString(), columnName);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    /**
     * Min
     *
     * @param columnName
     * @return
     */
    @Override
    public IFunction.Min min(String columnName) {
        FunctionImpl.MinImpl fn = new FunctionImpl.MinImpl(toString(), columnName);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        if (!name.equals(table.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////

    private String generateParamId(int id){
        return new StringBuilder(db.getConfig().getIdNamingConvention()).append(" = ").append(id).toString();
    }
}
