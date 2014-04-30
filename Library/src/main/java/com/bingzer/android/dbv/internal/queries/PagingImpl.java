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
package com.bingzer.android.dbv.internal.queries;

import android.database.Cursor;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.queries.ISequence;
import com.bingzer.android.dbv.internal.Database;
import com.bingzer.android.dbv.queries.Paging;
import com.bingzer.android.dbv.utils.EntityUtils;

/**
* Created by Ricky on 4/26/2014.
*/
public class PagingImpl extends QueryImpl<Cursor> implements Paging {

    private final int rowLimit;
    private final SelectImpl select;
    private int pageNumber = 0;

    public PagingImpl(SelectImpl select, int rowLimit){
        this.select = select;
        this.rowLimit = rowLimit;
    }

    @Override
    public int getRowLimit() {
        return rowLimit;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public void setPageNumber(int pageNumber) {
        ensurePageNumberValid(pageNumber);
    }

    @Override
    public Paging next() {
        pageNumber++;
        return this;
    }

    @Override
    public Paging previous() {
        if(pageNumber - 1 > 0) pageNumber--;
        return this;
    }

    @Override
    public int getTotalPage() {
        // we can't count row when orderBy or having is included
        // TODO: there's gotta be a way to do this
        if(select.groupByString != null || select.havingString != null)
            throw new UnsupportedOperationException("Cannot count row when querying using 'GroupBy' or 'Having'");

        float row = -1;

        String sql = generateSql(true);
        Cursor cursor = null;
        try{
            cursor = select.table.raw(sql).query();
            if(cursor.moveToFirst()){
                row = cursor.getInt(0);
            }
        }
        finally {
            if(cursor != null) cursor.close();
        }

        // calculate total page
        return  (int) Math.ceil(row / (float) rowLimit);
    }

    @Override
    public Cursor query(){
        return select.table.raw(toString()).query();
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
    public Cursor query(int pageNumber) {
        ensurePageNumberValid(pageNumber);
        return query();
    }

    @Override
    public <E extends IEntity> void query(int pageNumber, IEntityList<E> entityList) {
        ensurePageNumberValid(pageNumber);

        final Cursor cursor = query();
        EntityUtils.mapEntityListFromCursor(select.table, entityList, cursor);

        cursor.close();
    }

    @Override
    public void query(IEntity entity) {
        final Cursor cursor = query();
        EntityUtils.mapEntityFromCursor(select.table, entity, cursor);

        cursor.close();
    }

    @Override
    public <E extends IEntity> void query(IEntityList<E> entityList) {
        query(pageNumber, entityList);
    }

    @Override
    public String toString(){
        return generateSql(false);
    }

    String generateSql(boolean asRowCount){
        StringBuilder sql = new StringBuilder();

        sql.append(select.selectString).append(Database.SPACE);

        // columns
        if(asRowCount) sql.append(" COUNT(*) AS FN ");
        else sql.append(select.columnString).append(Database.SPACE);
        // from
        sql.append(select.fromString).append(Database.SPACE);
        // join builder
        if(select instanceof JoinImpl){
            sql.append(((JoinImpl)select).joinBuilder).append(Database.SPACE);
        }
        // where
        sql.append(Database.SPACE).append(select.whereString);

        // group by + having (Only when not to count)
        if(select.groupByString != null) sql.append(Database.SPACE).append(select.groupByString);
        if(select.havingString != null) sql.append(Database.SPACE).append(select.havingString);

        // order by
        if(select.orderByString != null) sql.append(Database.SPACE).append(select.orderByString);

        // pagination only when it's not a row count
        if(!asRowCount){
            sql.append(" LIMIT ").append(rowLimit).append(" OFFSET ").append(getOffset());
        }

        return sql.toString();
    }

    int getOffset(){
        return pageNumber * rowLimit;
    }

    void ensurePageNumberValid(int pageNumber){
        if(pageNumber < 0)
            throw new IllegalArgumentException("PageNumber must be over 0");
        this.pageNumber = pageNumber;
    }

}
