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
package com.bingzer.android.dbv.internal;

import android.database.Cursor;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.queries.ISequence;
import com.bingzer.android.dbv.queries.GroupBy;
import com.bingzer.android.dbv.queries.Having;
import com.bingzer.android.dbv.utils.CursorUtils;
import com.bingzer.android.dbv.utils.Utils;
import com.bingzer.android.dbv.queries.Paging;
import com.bingzer.android.dbv.queries.Select;
import com.bingzer.android.dbv.utils.EntityUtils;

import java.util.Locale;

/**
* Created by Ricky on 4/26/2014.
*/
abstract class SelectImpl extends QueryImpl<Cursor> implements Select, Select.OrderBy, GroupBy, Having {

    protected final Table table;
    protected StringBuilder selectString;
    protected StringBuilder columnString;
    protected StringBuilder fromString;
    protected StringBuilder limitString;
    protected StringBuilder orderByString;
    protected StringBuilder groupByString;
    protected StringBuilder havingString;
    protected StringBuilder whereString;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public SelectImpl(Table table){
        this(table, false);
    }

    public SelectImpl(Table table, boolean distinct){
        this( table, -1, distinct);
    }

    public SelectImpl(Table table, int top, boolean distinct){
        this.table = table;
        this.selectString = new StringBuilder("SELECT ");
        this.columnString = new StringBuilder("* ");
        this.fromString = new StringBuilder("FROM ").append(table);
        this.limitString = new StringBuilder();
        this.whereString = new StringBuilder();

        if(distinct) selectString.append("DISTINCT ");
        if(top > 0) {
            limitString = new StringBuilder();
            limitString.append(" LIMIT ").append(top);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Select where(String whereClause, Object... args){
        if(whereClause != null){
            // append where if necessary
            if(!whereClause.toLowerCase(Locale.getDefault()).startsWith("where"))
                whereString.append(" WHERE ");
            // safely prepare the where part
            whereString.append(Utils.bindArgs(whereClause, args));
        }
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Select columns(String... columns) {
        columnString.delete(0, columnString.length());
        if(columns != null){
            columnString.append(Utils.join(", ", columns));
        }
        else{
            columnString.append("*");
        }

        return this;
    }

    @Override
    public OrderBy orderBy(String... columns) {
        if(orderByString == null) orderByString = new StringBuilder();
        else orderByString.delete(0, orderByString.length());

        if(columns != null){
            String joined = Utils.join(",", columns);
            if(joined.length() > 0){
                orderByString.append("ORDER BY ").append(Utils.join(",", columns));
            }
        }
        return this;
    }

    @Override
    public void query(ISequence<Cursor> sequence) {
        final Cursor cursor = query();

        while(cursor.moveToNext()){
            if(!sequence.next(cursor))
                break;
        }

        cursor.close();
    }

    @Override
    public void query(IEntity entity) {
        final Cursor cursor = query();
        if(cursor.moveToNext()){
            EntityUtils.mapEntityFromCursor(table, entity, cursor);
        }

        cursor.close();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends IEntity> void query(IEntityList<E> entityList) {
        final Cursor cursor = query();
        EntityUtils.mapEntityListFromCursor(table, entityList, query());

        cursor.close();
    }

    @Override
    public Paging paging(int row) {
        return new PagingImpl(this, row);
    }

    @Override
    public GroupBy groupBy(String... columns) {
        if(groupByString == null) groupByString = new StringBuilder();
        else groupByString.delete(0, groupByString.length());
        if(columns != null){
            groupByString.append("GROUP BY ").append(Utils.join(",", columns));
        }

        return this;
    }

    @Override
    public Having having(String condition) {
        return having(condition, (Object) null);
    }

    @Override
    public Having having(String clause, Object... args) {
        if(havingString == null) havingString = new StringBuilder();
        else havingString.delete(0, havingString.length());
        if(clause != null){
            havingString.append("HAVING ").append(Utils.bindArgs(clause, args));
        }

        return this;
    }

    @Override
    public <T> T query(int columnIndex) {
        T value = null;
        final Cursor cursor = query();
        if(cursor.moveToNext()) {
            value = CursorUtils.getValueFromCursor(cursor, columnIndex);
        }
        cursor.close();

        return value;
    }

    @Override
    public <T> T query(String columnName) {
        T value = null;
        final Cursor cursor = query();
        if(cursor.moveToNext()) {
            value = CursorUtils.getValueFromCursor(cursor, columnName);
        }
        cursor.close();

        return value;
    }

    @Override
    public String toString(){
        StringBuilder sql = new StringBuilder();
        sql.append(selectString);
        sql.append(columnString).append(Database.SPACE);
        sql.append(fromString).append(Database.SPACE);
        sql.append(whereString).append(Database.SPACE);

        // group by + having
        if(groupByString != null) sql.append(Database.SPACE).append(groupByString);
        if(havingString != null) sql.append(Database.SPACE).append(havingString);
        // order by
        if(orderByString != null) sql.append(Database.SPACE).append(orderByString);
        // limit
        if(limitString != null) sql.append(Database.SPACE).append(limitString);

        return sql.toString();
    }
}
