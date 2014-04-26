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

import com.bingzer.android.dbv.IConfig;
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
    String alias;
    String name;

    final Database db;
    final List<String> columns;


    ////////////////////////////////////////////////////////////////////////////////////////

    Table (Database db, String name){
        this.name = name;
        this.db = db;
        this.columns = new LinkedList<String>();
        queryColumns();
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public List<String> getColumns() {
        return columns;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery.Select select(String condition) {
        return select(condition, (Object) null);
    }

    @Override
    public int selectId(String condition) {
        return selectId(condition, (Object) null);
    }

    @Override
    public int selectId(String whereClause, Object... args) {
        int id = -1;
        Cursor cursor = select(whereClause, args).columns(generateIdString()).query();
        if(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    @Override
    public IQuery.Select select(int top, String condition) {
        return select(top, condition, (Object) null);
    }

    @Override
    public IQuery.Select select(int id) {
        return select(generateParamId(id));
    }

    @Override
    public IQuery.Select select(int... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();
            whereClause.append(generateIdString()).append(" ");
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

    @Override
    public IQuery.Select select(String whereClause, Object... args) {
        return select(-1, whereClause, args);
    }

    @Override
    public IQuery.Select select(int top, String whereClause, Object... args) {
        return new QueryImpl.SelectImpl(db.getConfig(), this, top, false){
            @Override public Cursor query(){
                return db.sqLiteDb.rawQuery(toString(), null);
            }
        }.where(whereClause, args);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery.Select selectDistinct() {
        return selectDistinct(null);
    }

    @Override
    public IQuery.Select selectDistinct(String condition) {
        return selectDistinct(condition, (Object) null);
    }

    @Override
    public IQuery.Select selectDistinct(String whereClause, Object... args) {
        return selectDistinct(-1, whereClause, args);
    }

    @Override
    public IQuery.Select selectDistinct(int top) {
        return selectDistinct(top, null);
    }

    @Override
    public IQuery.Select selectDistinct(int top, String condition) {
        return selectDistinct(top, condition, (Object) null);
    }

    @Override
    public IQuery.Select selectDistinct(int top, String whereClause, Object... args) {
        return new QueryImpl.SelectImpl(db.getConfig(), this, top, true){
            @Override public Cursor query(){
                return db.sqLiteDb.rawQuery(toString(), null);
            }
        }.where(whereClause, args);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery.Insert insert(final ContentValues contents) {
        QueryImpl.InsertImpl query = new QueryImpl.InsertImpl();
        query.value = (int) db.sqLiteDb.insertOrThrow(getName(), null, contents);

        return query;
    }

    @Override
    public IQuery.Insert insert(String[] columns, Object[] values) {
        final ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            MappingUtil.mapContentValuesFromGenericObject(contentValues, columns[i], values[i]);
        }

        return insert(contentValues);
    }

    @Override
    public IQuery.InsertWith insert(String... columns) {
        return new QueryImpl.InsertWithImpl(new QueryImpl.ContentSet<QueryImpl.InsertWithImpl>() {
            @Override
            public void onContentValuesSet(QueryImpl.InsertWithImpl query, ContentValues contentValues) {
                query.value = (int) db.sqLiteDb.insertOrThrow(getName(), null, contentValues);
            }
        }, columns);
    }

    @Override
    @SuppressWarnings("unchecked")
    public IQuery.Insert insert(IEntity entity) {
        // build content values..
        final EntityMapper mapper = new EntityMapper(this);
        final ContentValues contentValues = new ContentValues();
        entity.map(mapper);

        Iterator<String> keys = mapper.keySet().iterator();
        String idString = generateIdString();
        IEntity.Action<Integer> idSetter = null;
        while(keys.hasNext()){
            String key = keys.next();
            IEntity.Action action = mapper.get(key);

            // ignore if column = "Id"
            if(key.equalsIgnoreCase(idString)) {
                idSetter = action;
            }
            else if(action != null){
                MappingUtil.mapContentValuesFromAction(contentValues, key, action);
            }
        }

        IQuery.Insert insert = insert(contentValues);
        // assign the newly inserted id
        if(idSetter != null){
            idSetter.set(insert.query());
        }

        return insert;
    }

    @Override
    public <E extends IEntity> IQuery.Insert insert(final IEntityList<E> entityList) {
        final QueryImpl.InsertImpl query = new QueryImpl.InsertImpl();
        query.value = 0;

        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                for(IEntity entity : entityList.getEntityList()){
                    insert(entity).query();
                    query.value++;
                }
            }
        }).execute();

        return query;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery.Update update(int id) {
        return update(generateParamId(id));
    }

    @Override
    public IQuery.Update update(int... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();
            whereClause.append(generateIdString()).append(" ");
            whereClause.append(" IN (");
            for(int i = 0; i < ids.length; i++){
                whereClause.append(ids[i]);
                if(i < ids.length - 1){
                    whereClause.append(",");
                }
            }
            whereClause.append(")");

            return update(whereClause.toString());
        }
        else{
            // select all
            return update((String) null);
        }
    }

    @Override
    public IQuery.Update update(String condition) {
        return update(condition, (Object) null);
    }

    @Override
    public IQuery.Update update(final String whereClause, final Object... whereArgs) {
        return new QueryImpl.UpdateImpl(new QueryImpl.ContentSet<QueryImpl.UpdateImpl>() {
            @Override
            public void onContentValuesSet(QueryImpl.UpdateImpl query, ContentValues contentValues) {
                query.value = update(contentValues, whereClause, whereArgs).query();
            }
        });
    }

    @Override
    public IQuery.Update update(IEntity entity) {
        if(entity.getId() < 0) throw new IllegalArgumentException("Id has to be over than 0");

        final EntityMapper mapper = new EntityMapper(this);
        final ContentValues contentValues = new ContentValues();
        entity.map(mapper);

        for (String key : mapper.keySet()) {
            // ignore if "Id"
            if (key.equalsIgnoreCase(generateIdString())) continue;

            IEntity.Action action = mapper.get(key);
            if (action != null) {
                MappingUtil.mapContentValuesFromAction(contentValues, key, action);
            }
        }

        return update(contentValues, entity.getId());
    }

    @Override
    public <E extends IEntity> IQuery.Update update(final IEntityList<E> entityList) {
        final QueryImpl.UpdateImpl query = new QueryImpl.UpdateImpl();
        query.value = 0;

        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                for(IEntity entity : entityList.getEntityList()){
                    query.value += update(entity).query();
                }
            }
        }).execute();

        return query;
    }

    @Override
    public IQuery.Update update(ContentValues contents, int id) {
        return update(contents, generateParamId(id));
    }

    @Override
    public IQuery.Update update(final ContentValues contents, final String whereClause, final Object... whereArgs) {
        QueryImpl.UpdateImpl query = new QueryImpl.UpdateImpl();
        String[] args = Util.toStringArray(whereArgs);

        // only update when content has something
        if(contents != null && contents.size() > 0)
            query.value = db.sqLiteDb.update(getName(), contents, whereClause, args);

        return query;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery.Delete delete(final int id) {
        return delete(generateParamId(id));
    }

    @Override
    public IQuery.Delete delete(int... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();

            whereClause.append(generateIdString()).append(" ");
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
            return delete("1 = 1");
        }
    }

    @Override
    public IQuery.Delete delete(Collection<Integer> ids) {
        int[] idz = new int[ids.size()];
        int counter = 0;
        for (Integer id : ids) {
            idz[counter++] = id;
        }

        return delete(idz);
    }

    @Override
    public IQuery.Delete delete(final String condition) {
        return delete(condition, (Object)null);
    }

    @Override
    public IQuery.Delete delete(final String whereClause, final Object... whereArgs) {
        QueryImpl.DeleteImpl query = new QueryImpl.DeleteImpl();
        query.value = db.sqLiteDb.delete(getName(), whereClause, Util.toStringArray((Object[]) whereArgs));

        return query;
    }

    @Override
    public IQuery.Delete delete(IEntity entity) {
        return delete(entity.getId());
    }

    @Override
    public <E extends IEntity> IQuery.Delete delete(IEntityList<E> entityList) {
        int[] ids = new int[entityList.getEntityList().size()];
        for(int i = 0; i < ids.length; i++){
            ids[i] = entityList.getEntityList().get(i).getId();
        }
        return  delete(ids);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean has(String condition) {
        return has(condition, (Object) null);
    }

    @Override
    public boolean has(int id) {
        return has(generateParamId(id));
    }

    @Override
    public boolean has(String whereClause, Object... whereArgs) {
        StringBuilder sql = new StringBuilder("SELECT 1 FROM ").append(getName())
                            .append(" WHERE ").append(Util.bindArgs(whereClause, whereArgs));
        Cursor cursor = null;
        try{
            cursor = raw(sql.toString()).query();
            if(cursor.moveToFirst()) return true;
        }
        finally {
            if(cursor != null) cursor.close();
        }

        // nope!
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int count(String condition) {
        return count(condition, (Object)null);
    }

    @Override
    public int count(String whereClause, Object... whereArgs) {
        int count = 0;

        StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM " + toString());
        if(whereClause != null){
            builder.append(" WHERE ");
            builder.append(Util.bindArgs(whereClause, whereArgs));
        }

        Cursor cursor = db.sqLiteDb.rawQuery(builder.toString(), null);
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

    @Override
    public int count() {
        return count(null);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery<Cursor> raw(final String sql) {
        return raw(sql, (String) null);
    }

    @Override
    public IQuery<Cursor> raw(final String sql, final Object... args) {
        return new QueryImpl<Cursor>(db.getConfig()){
            @Override public Cursor query(){
                if(args == null || args.length == 0)
                    return db.sqLiteDb.rawQuery(sql, null);
                else return db.sqLiteDb.rawQuery(sql, Util.toStringArray(args));
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

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

        if(query.value) db.removeTable(this);
        return query;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery.InnerJoin join(String tableName, String onClause) {
        return new QueryImpl.InnerJoinImpl(db.getConfig(), this, tableName, onClause){
            @Override public Cursor query(){
                return db.sqLiteDb.rawQuery(toString(), null);
            }
        };
    }

    @Override
    public IQuery.InnerJoin join(String tableName, String column1, String column2) {
        return join(tableName, name + "." + column1 + "=" + tableName + "." + column2);
    }

    @Override
    public IQuery.OuterJoin outerJoin(String tableName, String onClause) {
        return new QueryImpl.OuterJoinImpl(db.getConfig(), this, tableName, onClause){
            @Override public Cursor query(){
                return db.sqLiteDb.rawQuery(toString(), null);
            }
        };
    }

    @Override
    public IQuery.OuterJoin outerJoin(String tableName, String column1, String column2) {
        return outerJoin(tableName, name + "." + column1 + "=" + tableName + "." + column2);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery.Union union(IQuery.Select select) {
        return new QueryImpl.UnionImpl(select, this) {
            @Override
            public Cursor query() {
                return raw(toString()).query();
            }
        };
    }

    @Override
    public IQuery.Union unionAll(IQuery.Select select) {
        return new QueryImpl.UnionImpl(select, this, true) {
            @Override
            public Cursor query() {
                return raw(toString()).query();
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IFunction.Average avg(String columnName) {
        return avg(columnName, null);
    }

    @Override
    public IFunction.Average avg(String columnName, String condition) {
        FunctionImpl.AverageImpl fn = new FunctionImpl.AverageImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    @Override
    public IFunction.Average avg(String columnName, String whereClause, Object... args) {
        return avg(columnName, Util.bindArgs(whereClause, args));
    }

    @Override
    public IFunction.Sum sum(String columnName) {
        return sum(columnName, null);
    }

    @Override
    public IFunction.Sum sum(String columnName, String condition) {
        FunctionImpl.SumImpl fn = new FunctionImpl.SumImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    @Override
    public IFunction.Sum sum(String columnName, String whereClause, Object... args) {
        return sum(columnName, Util.bindArgs(whereClause, args));
    }

    @Override
    public IFunction.Total total(String columnName) {
        return total(columnName, null);
    }

    @Override
    public IFunction.Total total(String columnName, String condition) {
        FunctionImpl.TotalImpl fn = new FunctionImpl.TotalImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    @Override
    public IFunction.Total total(String columnName, String whereClause, Object... args) {
        return total(columnName, Util.bindArgs(whereClause, args));
    }

    @Override
    public IFunction.Max max(String columnName) {
        return max(columnName, null);
    }

    @Override
    public IFunction.Max max(String columnName, String condition) {
        FunctionImpl.MaxImpl fn = new FunctionImpl.MaxImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    @Override
    public IFunction.Max max(String columnName, String whereClause, Object... args) {
        return max(columnName, Util.bindArgs(whereClause, args));
    }

    @Override
    public IFunction.Min min(String columnName) {
        return min(columnName, null);
    }

    @Override
    public IFunction.Min min(String columnName, String condition) {
        FunctionImpl.MinImpl fn = new FunctionImpl.MinImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.value = cursor.getInt(0);
        }
        cursor.close();
        return fn;
    }

    @Override
    public IFunction.Min min(String columnName, String whereClause, Object... args) {
        return min(columnName, Util.bindArgs(whereClause, args));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Alter alter() {
        return new Alter(){
            @Override
            public Alter rename(String newName) {
                db.execSql("ALTER TABLE " + getName() + " RENAME TO " + newName);
                // quickly change our name
                name = newName;
                return this;
            }

            @Override
            public Alter addColumn(String columnName, String dataType) {
                return addColumn(columnName, dataType, null);
            }

            @Override
            public Alter addColumn(String columnName, String dataType, String columnDefinition) {
                Database.ColumnModel model = new Database.ColumnModel(columnName, dataType, columnDefinition);
                db.execSql("ALTER TABLE " + getName() + " ADD COLUMN " + model);
                // re-query columns
                queryColumns();
                return this;
            }

            @Override
            public Alter removeColumn(String columnName) {
                throw new UnsupportedOperationException("SQLite does not support removing columns");
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        return name.equals(table.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString(){
        if(alias != null && alias.length() > 0)
            return name + " " + alias;
        return name;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    IConfig getConfig(){
        return db.getConfig();
    }

    String generateParamId(int id){
        return generateIdString() + " = " + id;
    }

    String generateIdString(){
        if(db.getConfig().getAppendTableNameForId()){
            return getName() + db.getConfig().getIdNamingConvention();
        }
        return db.getConfig().getIdNamingConvention();
    }

    void queryColumns(){
        columns.clear();
        String pragmaSql = Util.bindArgs("PRAGMA table_info(?)", name);
        Cursor cursor = db.sqLiteDb.rawQuery(pragmaSql, null);
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
}
