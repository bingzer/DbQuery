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

import android.database.Cursor;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.queries.ContentSelectable;

/**
 * Created by Ricky on 8/20/13.
 */
public abstract class ContentSelectImpl implements ContentSelectable.Select, ContentSelectable.Select.OrderBy {
    final ContentConfig config;
    StringBuilder columnString;
    StringBuilder limitString;
    String orderByString;
    String whereString;
    Object[] whereArgs;

    public ContentSelectImpl(ContentConfig config, int top){
        this.config = config;
        this.columnString = new StringBuilder();
        this.columnString.append(Util.join(", ", config.getDefaultProjections()));

        if(top > 0) {
            limitString = new StringBuilder();
            limitString.append(" LIMIT ").append(top);
        }
    }

    @Override
    public ContentSelectImpl columns(String... columns) {
        columnString.delete(0, columnString.length());
        if(columns != null){
            columnString.append(Util.join(", ", columns));
        }
        else{
            columnString.append("*");
        }

        return this;
    }

    @Override
    public OrderBy orderBy(String... columns) {
        orderByString = Util.join(",", columns);
        return this;
    }

    @Override
    public Paging paging(int row) {
        return new PagingImpl(config, this, row);
    }

    public ContentSelectImpl where(String whereClause, Object... args){
        this.whereString = whereClause;
        this.whereArgs = args;
        return this;
    }

    /**
     * This is columns
     * @return array of columns names
     */
    public String[] getProjections(){
        String[] projections =  columnString.toString().split(",");
        for(int i = 0; i < projections.length; i++){
            projections[i] = projections[i].trim();
        }
        return projections;
    }

    /**
     * This is the where string
     * @return where clause
     */
    public String getSelection(){
        return whereString;
    }

    /**
     * Where args
     * @return selection args
     */
    public String[] getSelectionArgs(){
        return Util.toStringArray(whereArgs);
    }

    /**
     * Order by
     * @return sorting order as string
     */
    public String getSortingOrder(){
        StringBuilder sortingOrder = new StringBuilder();

        // without the 'ORDER BY'
        if(orderByString != null) sortingOrder.append(orderByString);

        // add limit
        if(limitString != null && limitString.length() > 0){
            if(orderByString == null) sortingOrder.append(config.getIdNamingConvention());
            sortingOrder.append(" ").append(limitString);
        }

        return sortingOrder.toString();
    }


    static class PagingImpl implements IQuery.Paging, IQuery<Cursor> {
        final IConfig config;
        final int rowLimit;
        private final ContentSelectImpl select;
        private int pageNumber = -1;

        PagingImpl(IConfig config, ContentSelectImpl select, int rowLimit){
            this.config = config;
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
        public int getTotalPage() {
            Cursor cursor = select.query();
            float row = cursor.getCount();
            cursor.close();

            // calculate total page
            return  (int) Math.ceil(row / (float) rowLimit);
        }

        @Override
        public Cursor query(){
            ++pageNumber;
            return runQuery();
        }

        @Override
        public Cursor query(int pageNumber) {
            ensurePageNumberValid(pageNumber);
            return runQuery();
        }

        @Override
        public <E extends IEntity> void query(int pageNumber, IEntityList<E> entityList) {
            ensurePageNumberValid(pageNumber);
            select.limitString = new StringBuilder();
            select.limitString.append(" LIMIT ").append(rowLimit).append(" OFFSET ").append(getOffset());
            select.query(entityList);
        }

        @Override
        public void query(IEntity entity) {
            select.limitString = new StringBuilder();
            select.limitString.append(" LIMIT ").append(rowLimit).append(" OFFSET ").append(getOffset());
            select.query(entity);
        }

        @Override
        public <E extends IEntity> void query(IEntityList<E> entityList) {
            query(++pageNumber, entityList);
        }

        int getOffset(){
            return pageNumber * rowLimit;
        }

        void ensurePageNumberValid(int pageNumber){
            if(pageNumber < 0)
                throw new IllegalArgumentException("PageNumber must be over 0");
            this.pageNumber = pageNumber;
        }

        Cursor runQuery(){
            select.limitString = new StringBuilder();
            select.limitString.append(" LIMIT ").append(rowLimit).append(" OFFSET ").append(getOffset());
            return select.query();
        }
    }
}
