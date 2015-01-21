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
package com.bingzer.android.dbv.internal;

import android.database.Cursor;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.content.contracts.Selectable;
import com.bingzer.android.dbv.queries.ISequence;
import com.bingzer.android.dbv.queries.IQuery;
import com.bingzer.android.dbv.utils.Utils;
import com.bingzer.android.dbv.content.contracts.IBaseResolver;
import com.bingzer.android.dbv.queries.Paging;

/**
 * Created by Ricky on 8/20/13.
 */
abstract class ContentSelectImpl implements Selectable.Select, Selectable.Select.OrderBy {
    final IBaseResolver resolver;
    StringBuilder columnString;
    StringBuilder limitString;
    String orderByString;
    String whereString;
    Object[] whereArgs;

    public ContentSelectImpl(IBaseResolver resolver, int top){
        this.resolver = resolver;
        this.columnString = new StringBuilder();
        this.columnString.append(Utils.join(", ", generateDefaultProjections()));

        if(top > 0) {
            limitString = new StringBuilder();
            limitString.append(" LIMIT ").append(top);
        }
    }

    @Override
    public ContentSelectImpl columns(String... columns) {
        columnString.delete(0, columnString.length());
        if(columns != null){
            columnString.append(Utils.join(", ", columns));
        }
        else{
            columnString.append(Utils.join(", ", generateDefaultProjections()));
        }

        return this;
    }

    @Override
    public OrderBy orderBy(String... columns) {
        orderByString = Utils.join(",", columns).trim();
        return this;
    }

    @Override
    public Paging paging(int row) {
        return new PagingImpl(resolver, this, row);
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
        return Utils.toStringArray(whereArgs);
    }

    /**
     * Order by
     * @return sorting order as string
     */
    public String getSortingOrder(){
        StringBuilder sortingOrder = new StringBuilder();

        // without the 'ORDER BY'
        if(orderByString != null && orderByString.length() > 0) sortingOrder.append(orderByString);

        // add limit
        if(limitString != null && limitString.length() > 0){
            if(orderByString == null) sortingOrder.append(resolver.getPrimaryKeyColumn());
            sortingOrder.append(" ").append(limitString);
        }

        if(sortingOrder.length() > 0) return sortingOrder.toString();
        return null;
    }

    String[] generateDefaultProjections(){
        String[] projections = resolver.getContentConfig().getDefaultProjections();
        for(int i = 0; i < projections.length; i++){
            if(projections[i].equals(resolver.getContentConfig().getIdNamingConvention())){
                projections[i] = resolver.getPrimaryKeyColumn();
            }
        }

        return projections;
    }


    static class PagingImpl implements Paging, IQuery<Cursor> {
        final IBaseResolver resolver;
        final int rowLimit;
        private final ContentSelectImpl select;
        private int pageNumber = 0;

        PagingImpl(IBaseResolver resolver, ContentSelectImpl select, int rowLimit){
            this.resolver = resolver;
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
            Cursor cursor = select.query();
            float row = cursor.getCount();
            cursor.close();

            // calculate total page
            return  (int) Math.ceil(row / (float) rowLimit);
        }

        @Override
        public Cursor query(){
            return fixSelect().query();
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
            fixSelect().query(entityList);
        }

        @Override
        public void query(IEntity entity) {
            fixSelect().query(entity);
        }

        @Override
        public <E extends IEntity> void query(IEntityList<E> entityList) {
            query(pageNumber, entityList);
        }

        int getOffset(){
            return pageNumber * rowLimit;
        }

        void ensurePageNumberValid(int pageNumber){
            if(pageNumber < 0)
                throw new IllegalArgumentException("PageNumber must be over 0");
            this.pageNumber = pageNumber;
        }

        ContentSelectImpl fixSelect(){
            select.limitString = new StringBuilder();
            select.limitString.append(" LIMIT ").append(rowLimit).append(" OFFSET ").append(getOffset());
            return select;
        }
    }
}
