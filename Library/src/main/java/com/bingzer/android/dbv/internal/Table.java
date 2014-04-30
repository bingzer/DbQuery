/**
 * Copyright 2014 Ricky Tobing
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
package com.bingzer.android.dbv.internal;

import android.content.ContentValues;
import android.database.Cursor;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.internal.queries.AverageImpl;
import com.bingzer.android.dbv.internal.queries.ContentSet;
import com.bingzer.android.dbv.internal.queries.DeleteImpl;
import com.bingzer.android.dbv.internal.queries.DropImpl;
import com.bingzer.android.dbv.internal.queries.InnerJoinImpl;
import com.bingzer.android.dbv.internal.queries.InsertImpl;
import com.bingzer.android.dbv.internal.queries.InsertIntoImpl;
import com.bingzer.android.dbv.internal.queries.MaxImpl;
import com.bingzer.android.dbv.internal.queries.MinImpl;
import com.bingzer.android.dbv.internal.queries.OuterJoinImpl;
import com.bingzer.android.dbv.internal.queries.QueryImpl;
import com.bingzer.android.dbv.internal.queries.SelectImpl;
import com.bingzer.android.dbv.internal.queries.SumImpl;
import com.bingzer.android.dbv.internal.queries.TotalImpl;
import com.bingzer.android.dbv.internal.queries.UnionImpl;
import com.bingzer.android.dbv.internal.queries.UpdateImpl;
import com.bingzer.android.dbv.queries.Average;
import com.bingzer.android.dbv.queries.Delete;
import com.bingzer.android.dbv.queries.IQuery;
import com.bingzer.android.dbv.queries.InnerJoin;
import com.bingzer.android.dbv.queries.Insert;
import com.bingzer.android.dbv.queries.InsertInto;
import com.bingzer.android.dbv.queries.Max;
import com.bingzer.android.dbv.queries.Min;
import com.bingzer.android.dbv.queries.OuterJoin;
import com.bingzer.android.dbv.queries.Select;
import com.bingzer.android.dbv.queries.Sum;
import com.bingzer.android.dbv.queries.Total;
import com.bingzer.android.dbv.queries.Union;
import com.bingzer.android.dbv.queries.Update;
import com.bingzer.android.dbv.utils.CollectionUtils;
import com.bingzer.android.dbv.utils.ContentValuesUtils;
import com.bingzer.android.dbv.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
public class Table implements ITable {
    private String alias;
    private String name;
    private final Collection<String> columns;

    protected final Database db;

    public Table (Database db, String name){
        this.name = name;
        this.db = db;
        this.columns = new ArrayList<String>();
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
    public Iterable<String> getColumns() {
        return columns;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getPrimaryKeyColumn(){
        if(db.getConfig().getAppendTableNameForId()){
            return getName() + db.getConfig().getIdNamingConvention();
        }
        return db.getConfig().getIdNamingConvention();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Select select(String condition) {
        return select(condition, (Object) null);
    }

    @Override
    public long selectId(String condition) {
        return selectId(condition, (Object) null);
    }

    @Override
    public long selectId(String whereClause, Object... args) {
        long id = -1;
        Cursor cursor =
                select(whereClause, args)
                .columns(getPrimaryKeyColumn())
                .query();

        if(cursor.moveToNext()){
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    @Override
    public Select select(int top, String condition) {
        return select(top, condition, (Object) null);
    }

    @Override
    public Select select(long id) {
        return select(generateParamId(id));
    }

    @Override
    public Select select(long... ids) {
        return select(generateParamInIds(ids));
    }

    @Override
    public Select select(String whereClause, Object... args) {
        return select(-1, whereClause, args);
    }

    @Override
    public Select select(int top, String whereClause, Object... args) {
        return new SelectImpl(this, top, false){
            @Override public Cursor query(){
                return db.sqLiteDb.rawQuery(toString(), null);
            }
        }.where(whereClause, args);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Select selectDistinct() {
        return selectDistinct(null);
    }

    @Override
    public Select selectDistinct(String condition) {
        return selectDistinct(condition, (Object) null);
    }

    @Override
    public Select selectDistinct(String whereClause, Object... args) {
        return selectDistinct(-1, whereClause, args);
    }

    @Override
    public Select selectDistinct(int top) {
        return selectDistinct(top, null);
    }

    @Override
    public Select selectDistinct(int top, String condition) {
        return selectDistinct(top, condition, (Object) null);
    }

    @Override
    public Select selectDistinct(int top, String whereClause, Object... args) {
        return new SelectImpl(this, top, true){
            @Override public Cursor query(){
                return db.sqLiteDb.rawQuery(toString(), null);
            }
        }.where(whereClause, args);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Insert insert(final ContentValues contents) {
        db.enforceReadOnly();

        InsertImpl query = new InsertImpl();
        query.setValue( db.sqLiteDb.insertOrThrow(getName(), null, contents) );

        return query;
    }

    @Override
    public Insert insert(String[] columns, Object[] values) {
        db.enforceReadOnly();

        final ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            ContentValuesUtils.mapContentValuesFromGenericObject(contentValues, columns[i], values[i]);
        }

        return insert(contentValues);
    }

    @Override
    public Insert insert(String column, Object value) {
        db.enforceReadOnly();

        final ContentValues contentValues = new ContentValues();
        ContentValuesUtils.mapContentValuesFromGenericObject(contentValues, column, value);

        return insert(contentValues);
    }

    @Override
    public InsertInto insertInto(String... columns) {
        db.enforceReadOnly();

        return new InsertIntoImpl(new ContentSet<InsertIntoImpl>() {
            @Override
            public void onContentValuesSet(InsertIntoImpl query, ContentValues contentValues) {
                query.setValue( db.sqLiteDb.insertOrThrow(getName(), null, contentValues) );
            }
        }, columns);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Insert insert(IEntity entity) {
        db.enforceReadOnly();

        // build content values..
        final ContentValues contentValues = new ContentValues();
        final Delegate.Mapper mapper = ContentValuesUtils.mapContentValuesFromEntity(contentValues, this, entity);

        // do not insert the primary column
        contentValues.remove(getPrimaryKeyColumn());
        Insert insert = insert(contentValues);

        // assign the newly inserted id
        Delegate<Long> pkDelegate = mapper.get(getPrimaryKeyColumn());
        if(pkDelegate != null){
            pkDelegate.set(insert.query());
        }

        return insert;
    }

    @Override
    public <E extends IEntity> Insert insert(final IEntityList<E> entityList) {
        db.enforceReadOnly();

        final InsertImpl query = new InsertImpl();
        query.setValue(0l);

        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                for(IEntity entity : entityList){
                    insert(entity).query();
                    query.setValue(query.query() + 1);
                }
            }
        }).execute();

        return query;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Update update(long id) {
        db.enforceReadOnly();

        return update(generateParamId(id));
    }

    @Override
    public Update update(long... ids) {
        db.enforceReadOnly();

        return update(generateParamInIds(ids));
    }

    @Override
    public Update update(String condition) {
        db.enforceReadOnly();

        return update(condition, (Object) null);
    }

    @Override
    public Update update(final String whereClause, final Object... whereArgs) {
        db.enforceReadOnly();

        return new UpdateImpl(new ContentSet<UpdateImpl>() {
            @Override
            public void onContentValuesSet(UpdateImpl query, ContentValues contentValues) {
                query.setValue(update(contentValues, whereClause, whereArgs).query());
            }
        });
    }

    @Override
    public Update update(IEntity entity) {
        db.enforceReadOnly();

        if(entity.getId() < 0) throw new IllegalArgumentException("Id has to be over than 0");

        final ContentValues contentValues = ContentValuesUtils.generateContentValuesFromEntity(this, entity);
        contentValues.remove(getPrimaryKeyColumn());

        return update(contentValues, entity.getId());
    }

    @Override
    public <E extends IEntity> Update update(final IEntityList<E> entityList) {
        db.enforceReadOnly();

        final UpdateImpl query = new UpdateImpl();

        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                for(IEntity entity : entityList){
                    query.setValue( query.query() + update(entity).query() );
                }
            }
        }).execute();

        return query;
    }

    @Override
    public Update update(ContentValues contents, long id) {
        db.enforceReadOnly();

        return update(contents, generateParamId(id));
    }

    @Override
    public Update update(final ContentValues contents, final String whereClause, final Object... whereArgs) {
        db.enforceReadOnly();

        UpdateImpl query = new UpdateImpl();
        String[] args = Utils.toStringArray(whereArgs);

        // only update when content has something
        if(contents != null && contents.size() > 0)
            query.setValue( db.sqLiteDb.update(getName(), contents, whereClause, args) );

        return query;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Delete delete(final long id) {
        db.enforceReadOnly();

        return delete(generateParamId(id));
    }

    @Override
    public Delete delete(long... ids) {
        db.enforceReadOnly();

        return delete(generateParamInIds(ids));
    }

    @Override
    public Delete delete(Iterable<Long> ids) {
        db.enforceReadOnly();

        long[] idz = new long[CollectionUtils.size(ids)];
        int counter = 0;
        for (long id : ids) {
            idz[counter++] = id;
        }

        return delete(idz);
    }

    @Override
    public Delete delete(final String condition) {
        db.enforceReadOnly();

        return delete(condition, (Object)null);
    }

    @Override
    public Delete delete(final String whereClause, final Object... whereArgs) {
        db.enforceReadOnly();

        DeleteImpl query = new DeleteImpl();
        query.setValue(db.sqLiteDb.delete(getName(), whereClause, Utils.toStringArray((Object[]) whereArgs)));

        return query;
    }

    @Override
    public Delete delete(IEntity entity) {
        db.enforceReadOnly();

        return delete(entity.getId());
    }

    @Override
    public <E extends IEntity> Delete delete(IEntityList<E> entityList) {
        db.enforceReadOnly();

        long[] ids = new long[CollectionUtils.size(entityList)];
        int counter = 0;
        for(E entity : entityList){
            ids[counter++] = entity.getId();
        }
        return delete(ids);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean has(String condition) {
        return has(condition, (Object) null);
    }

    @Override
    public boolean has(long id) {
        return has(generateParamId(id));
    }

    @Override
    public boolean has(String whereClause, Object... whereArgs) {
        StringBuilder sql = new StringBuilder("SELECT 1 FROM ").append(getName())
                            .append(" WHERE ").append(Utils.bindArgs(whereClause, whereArgs));
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
            builder.append(Utils.bindArgs(whereClause, whereArgs));
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
        return new QueryImpl<Cursor>(){
            @Override public Cursor query(){
                if(args == null || args.length == 0)
                    return db.sqLiteDb.rawQuery(sql, null);
                else return db.sqLiteDb.rawQuery(sql, Utils.toStringArray(args));
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery<Boolean> drop() {
        db.enforceReadOnly();

        DropImpl query = new DropImpl();
        try{
            db.execSql("DROP TABLE " + getName());
            query.setValue( true );
        }
        catch (Exception e){
            query.setValue( false );
        }

        if(query.query()) db.removeTable(this);
        return query;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public InnerJoin join(String tableName, String onClause) {
        return new InnerJoinImpl(this, tableName, onClause){
            @Override public Cursor query(){
                return db.sqLiteDb.rawQuery(toString(), null);
            }
        };
    }

    @Override
    public InnerJoin join(String tableName, String column1, String column2) {
        return join(tableName, name + "." + column1 + "=" + tableName + "." + column2);
    }

    @Override
    public OuterJoin outerJoin(String tableName, String onClause) {
        return new OuterJoinImpl(this, tableName, onClause){
            @Override public Cursor query(){
                return db.sqLiteDb.rawQuery(toString(), null);
            }
        };
    }

    @Override
    public OuterJoin outerJoin(String tableName, String column1, String column2) {
        return outerJoin(tableName, name + "." + column1 + "=" + tableName + "." + column2);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Union union(Select select) {
        return new UnionImpl(select, this) {
            @Override
            public Cursor query() {
                return raw(toString()).query();
            }
        };
    }

    @Override
    public Union unionAll(Select select) {
        return new UnionImpl(select, this, true) {
            @Override
            public Cursor query() {
                return raw(toString()).query();
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Average avg(String columnName) {
        return avg(columnName, null);
    }

    @Override
    public Average avg(String columnName, String condition) {
        AverageImpl fn = new AverageImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.setValue(cursor.getDouble(0));
        }
        cursor.close();
        return fn;
    }

    @Override
    public Average avg(String columnName, String whereClause, Object... args) {
        return avg(columnName, Utils.bindArgs(whereClause, args));
    }

    @Override
    public Sum sum(String columnName) {
        return sum(columnName, null);
    }

    @Override
    public Sum sum(String columnName, String condition) {
        SumImpl fn = new SumImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.setValue(cursor.getDouble(0));
        }
        cursor.close();
        return fn;
    }

    @Override
    public Sum sum(String columnName, String whereClause, Object... args) {
        return sum(columnName, Utils.bindArgs(whereClause, args));
    }

    @Override
    public Total total(String columnName) {
        return total(columnName, null);
    }

    @Override
    public Total total(String columnName, String condition) {
        TotalImpl fn = new TotalImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.setValue(cursor.getDouble(0));
        }
        cursor.close();
        return fn;
    }

    @Override
    public Total total(String columnName, String whereClause, Object... args) {
        return total(columnName, Utils.bindArgs(whereClause, args));
    }

    @Override
    public Max max(String columnName) {
        return max(columnName, null);
    }

    @Override
    public Max max(String columnName, String condition) {
        MaxImpl fn = new MaxImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.setValue(cursor.getDouble(0));
        }
        cursor.close();
        return fn;
    }

    @Override
    public Max max(String columnName, String whereClause, Object... args) {
        return max(columnName, Utils.bindArgs(whereClause, args));
    }

    @Override
    public Min min(String columnName) {
        return min(columnName, null);
    }

    @Override
    public Min min(String columnName, String condition) {
        MinImpl fn = new MinImpl(toString(), columnName, condition);
        Cursor cursor = raw(fn.toString()).query();
        if(cursor.moveToNext()){
            fn.setValue(cursor.getDouble(0));
        }
        cursor.close();
        return fn;
    }

    @Override
    public Min min(String columnName, String whereClause, Object... args) {
        return min(columnName, Utils.bindArgs(whereClause, args));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Alter alter() {
        db.enforceReadOnly();

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

    private String generateParamId(long id){
        return getPrimaryKeyColumn() + " = " + id;
    }

    private void queryColumns(){
        columns.clear();
        String pragmaSql = Utils.bindArgs("PRAGMA table_info(?)", name);
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

    /**
     * Generate IN(ID,ID,ID) params. if ids is empty or null
     * then it will return null
     * @param ids ids to generate params with
     * @return string rep of IN(...) statements, null if ids is empty or null
     */
    private String generateParamInIds(long... ids){
        if(ids != null && ids.length > 0) {
            StringBuilder builder = new StringBuilder();

            builder.append(getPrimaryKeyColumn())
                    .append(" IN (");
            for (int i = 0; i < ids.length; i++) {
                builder.append(ids[i]);
                if (i < ids.length - 1) {
                    builder.append(",");
                }
            }
            builder.append(")");

            return builder.toString();
        }

        return null;
    }

}
