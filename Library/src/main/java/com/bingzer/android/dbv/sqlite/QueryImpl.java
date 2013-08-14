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
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.queries.Selectable;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
abstract class QueryImpl<T> implements IQuery<T> {

    IConfig config;
    StringBuilder builder;

    QueryImpl(IConfig config){
        this(config, null);
    }

    QueryImpl(IConfig config, Object any){
        this.config = config;
        builder = new StringBuilder();
        append(any);
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    QueryImpl append(Object any){
        if(any != null) builder.append(any);
        return this;
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    /**
     * Build the sql and return a cursor
     *
     * @return
     */
    @Override
    public T query() {
        return null;
    }

    /**
     * SelectImpl
     */
    static class SelectImpl extends QueryImpl<Cursor> implements IQuery.Select, Select.OrderBy{

        protected StringBuilder selectString;
        protected StringBuilder columnString;
        protected StringBuilder fromString;
        protected StringBuilder limitString;
        protected StringBuilder orderByString;
        protected Table table;

        SelectImpl(IConfig config, Table table){
            this(config, table, false);
        }

        SelectImpl(IConfig config, Table table, boolean distinct){
            this(config, table, -1, distinct);
        }

        SelectImpl(IConfig config, Table table, int top, boolean distinct){
            super(config);

            this.table = table;

            selectString = new StringBuilder();
            columnString = new StringBuilder();
            fromString = new StringBuilder();
            limitString = new StringBuilder();
            orderByString = new StringBuilder();

            if(distinct)
                selectString.append("SELECT DISTINCT ");
            else selectString.append("SELECT ");

            columnString.append("*");
            fromString.append("FROM " + table.toString());

            if(top > 0){
                limitString.append(" LIMIT " + top);
            }
        }

        /**
         * Specified the column to return.
         * default or null will produce SELECT * FROM
         *
         * @param columns
         * @return
         */
        @Override
        public Select columns(String... columns) {
            columnString.delete(0, columnString.length());
            if(columns != null){
                columnString.append(Util.join(",", columns));
            }
            else{
                columnString.append("*");
            }

            return this;
        }

        /**
         * Orders by
         *
         * @param columns
         * @return
         */
        @Override
        public OrderBy orderBy(String... columns) {
            orderByString.delete(0, columnString.length());
            if(columns != null){
                orderByString.append("ORDER BY ").append(Util.join(",", columns)).append(" ASC");
            }
            return this;
        }

        /**
         * Order by descending
         * @param columns
         * @return
         */
        @Override
        public OrderBy orderByDesc(String... columns) {
            orderByString.delete(0, columnString.length());
            if(columns != null){
                orderByString.append("ORDER BY ").append(Util.join(",", columns)).append(" DESC");
            }
            return this;
        }

        /**
         * Select to an entity
         *
         * @param entity the target entity
         */
        @Override
        public void query(IEntity entity) {
            final Cursor cursor = query();
            final EntityMapper mapper = new EntityMapper(table);

            ContentUtil.mapEntityFromCursor(mapper, entity, cursor);

            cursor.close();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <E extends IEntity> void query(IEntityList<E> entityList) {
            final Cursor cursor = query();
            final EntityMapper mapper = new EntityMapper(table);

            ContentUtil.mapEntityListFromCursor(mapper, entityList, cursor);

            cursor.close();
        }

        /**
         * To String
         * @return
         */
        public String toString(){
            StringBuilder sql = new StringBuilder();
            sql.append(selectString).append(" ");
            sql.append(columnString).append(" ");
            sql.append(fromString).append(" ");
            // where
            sql.append(" ").append(super.builder);
            // order by
            if(orderByString.length() > 0){
                sql.append(" ").append(orderByString);
            }
            // limit
            if(limitString.length() > 0){
                sql.append(" ").append(limitString);
            }

            return sql.toString();
        }

        /**
         * Added paging to a query.
         *
         * @param row the row number
         * @return paging
         * @see com.bingzer.android.dbv.IQuery.Paging
         */
        @Override
        public Paging paging(int row) {
            return new PagingImpl(config, this, row);
        }
    }


    /**
     * InsertImpl
     */
    static class InsertImpl implements IQuery.Insert {
        Integer value;

        /**
         * Build the sql and return a cursor
         *
         * @return
         */
        @Override
        public Integer query() {
            return value;
        }
    }

    static class InsertWithImpl extends InsertImpl implements InsertWith {

        IQuery<Integer> query;
        String[] columnNames;

        InsertWithImpl(IQueryableAppendable query, String... columnNames){
            this.query = query;
            this.columnNames = columnNames;
        }

        /**
         * Values
         *
         * @return
         */
        @Override
        public IQuery val(Object... values) {
            ContentValues contentValues = new ContentValues();
            for(int i = 0; i < columnNames.length; i++){
                ContentUtil.mapContentValuesFromGenericObject(contentValues, columnNames[i], values[i]);
            }

            ((IQueryableAppendable) query).onContentValuesSet(this, contentValues);

            return this;
        }

        static interface IQueryableAppendable extends IQuery<Integer> {

            void onContentValuesSet(InsertWithImpl query, ContentValues contentValues);

        }
    }


    /**
     * UpdateImpl
     */
    static class UpdateImpl implements IQuery.Update {
        Integer value;

        /**
         * Build the sql and return a cursor
         *
         * @return
         */
        @Override
        public Integer query() {
            return value;
        }
    }


    static class DeleteImpl implements IQuery.Delete {

        Integer value;

        /**
         * Build the sql and return a cursor
         *
         * @return
         */
        @Override
        public Integer query() {
            return value;
        }
    }

    static class DropImpl implements IQuery<Boolean>{
        Boolean value;


        /**
         * Build the sql and return a cursor
         *
         * @return
         */
        @Override
        public Boolean query() {
            return value;
        }
    }

    static class InnerJoinImpl extends Join implements IQuery.InnerJoin {
        InnerJoinImpl(IConfig config, Table table, String tableNameToJoin, String onClause) {
            super(config, table, "INNER JOIN", tableNameToJoin, onClause);
        }
    }

    static class OuterJoinImpl extends Join implements IQuery.OuterJoin {
        OuterJoinImpl(IConfig config, Table table, String tableNameToJoin, String onClause) {
            super(config, table, "OUTER JOIN", tableNameToJoin, onClause);
        }
    }



    private static class Join extends SelectImpl implements IQuery.InnerJoin, IQuery.OuterJoin, Selectable {

        protected final Table table;
        protected StringBuilder joinBuilder;

        Join(IConfig config, Table table, String joinType, String tableNameToJoin, String onClause){
            super(config, table);
            this.table = table;
            this.joinBuilder = new StringBuilder();

            if(onClause.toLowerCase().startsWith("on "))
                this.joinBuilder.append(" ").append(joinType).append(" ").append(tableNameToJoin).append(" ").append(onClause);
            else
                this.joinBuilder.append(" ").append(joinType).append(" ").append(tableNameToJoin).append(" ON ").append(onClause);
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
            consume(table.select(top, condition));
            return this;
        }

        /**
         * SelectImpl some condition
         *
         * @param condition
         * @return
         */
        @Override
        public IQuery.Select select(String condition) {
            consume(table.select(condition));
            return this;
        }

        /**
         * SelectImpl add id
         *
         * @param id
         * @return
         */
        @Override
        public IQuery.Select select(int id) {
            consume(table.select(id));
            return this;
        }

        /**
         * SelectImpl multiple ids
         *
         * @param ids
         * @return
         */
        @Override
        public IQuery.Select select(int... ids) {
            consume(table.select(ids));
            return this;
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
            consume(table.select(whereClause, args));
            return this;
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
            consume(table.select(top, whereClause, args));
            return this;
        }

        /**
         * Select distinct all.
         * Equivalent of calling <code>selectDistinct(null)</code>
         *
         * @return
         */
        @Override
        public Select selectDistinct() {
            consume(table.selectDistinct(null));
            return this;
        }

        /**
         * SelectImpl distinct
         *
         * @param condition
         * @return
         */
        @Override
        public IQuery.Select selectDistinct(String condition) {
            consume(table.selectDistinct(condition));
            return this;
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
            consume(table.selectDistinct(whereClause, args));
            return this;
        }

        /**
         *
         * @return
         */
        @Override
        public String toString(){
            StringBuilder sql = new StringBuilder();
            sql.append(selectString).append(" ");
            sql.append(columnString).append(" ");
            // from what table?
            sql.append(fromString).append(" ");
            // join builder
            sql.append(joinBuilder).append(" ");
            // where
            if(super.builder.length() > 0){
                sql.append(super.builder);
            }
            // order by
            if(orderByString != null){
                sql.append(orderByString);
            }
            // limit
            if(limitString != null){
                sql.append(limitString);
            }

            return sql.toString();
        }

        /**
         * Consume parent's select statement
         * @param select
         */
        private void consume(Select select){
            // clear first..
            super.builder.delete(0, super.builder.length());
            // consume
            selectString = ((SelectImpl)select).selectString;
            columnString = ((SelectImpl)select).columnString;
            fromString = ((SelectImpl)select).fromString;
            orderByString = ((SelectImpl)select).orderByString;
            limitString = ((SelectImpl)select).limitString;
            // the whereClause part
            append(((SelectImpl) select).builder);
        }

        /**
         * Inner join a table
         *
         * @param tableName
         * @param onClause
         * @return
         */
        @Override
        public InnerJoin join(String tableName, String onClause) {
            if(onClause.toLowerCase().startsWith("on "))
                this.joinBuilder.append(" ").append("INNER JOIN").append(" ").append(tableName).append(" ").append(onClause);
            else
                this.joinBuilder.append(" ").append("INNER JOIN").append(" ").append(tableName).append(" ON ").append(onClause);
            return this;
        }

        /**
         * Inner join a table
         *
         * @param tableName
         * @param column1
         * @param column2
         * @return
         */
        @Override
        public InnerJoin join(String tableName, String column1, String column2) {
            return join(tableName, column1 + " = " + column2);
        }

        /**
         * Joins a table
         *
         * @param tableName
         * @param onClause
         * @return
         */
        @Override
        public OuterJoin outerJoin(String tableName, String onClause) {
            if(onClause.toLowerCase().startsWith("on "))
                this.joinBuilder.append(" ").append("OUTER JOIN").append(" ").append(tableName).append(" ").append(onClause);
            else
                this.joinBuilder.append(" ").append("OUTER JOIN").append(" ").append(tableName).append(" ON ").append(onClause);
            return this;
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
        public OuterJoin outerJoin(String tableName, String column1, String column2) {
            return outerJoin(tableName, column1 + " = " + column2);
        }
    }

    static class PagingImpl extends QueryImpl<Cursor> implements Paging{

        private final int rowLimit;
        private final SelectImpl select;
        private int pageNumber = 0;

        PagingImpl(IConfig config, SelectImpl select, int rowLimit){
            super(config);
            this.select = select;
            this.rowLimit = rowLimit;
        }

        /**
         * Returns the number of row set in the beginning.
         * This number is final
         *
         * @return the number of row
         */
        @Override
        public int getRowLimit() {
            return rowLimit;
        }

        /**
         * Returns the current page number
         *
         * @return the current page number
         */
        @Override
        public int getPageNumber() {
            return pageNumber;
        }

        /**
         * Sets the page number.
         * If the pageNumber is under than zero it will throw an IllegalArgumentException.
         *
         * @param pageNumber the page number to set
         */
        @Override
        public void setPageNumber(int pageNumber) {
            ensurePageNumberValid(pageNumber);
        }

        /**
         * Returns query and up the page number by one.
         * Page number is upped only when there's row in the cursor
         * @return cursor
         */
        @Override
        public Cursor query(){
            Cursor cursor = null;
            try{
                cursor = select.table.raw(toString()).query();
                return cursor;
            }
            finally {
                if (cursor != null && cursor.getCount() > 0)
                    pageNumber++;
            }
        }

        /**
         * Returns the cursor on the <code>pageNumber</code>.
         * If pageNumber is under than zero it will throw an IllegalArgumentException.
         * If pageNumber is not found, cursor will be null.
         * If called, then {@link #getPageNumber()} will return pageNumber.
         *
         * @param pageNumber the number
         * @return cursor
         */
        @Override
        public Cursor query(int pageNumber) {
            ensurePageNumberValid(pageNumber);
            return query();
        }

        /**
         * Query and store the result to an {@link com.bingzer.android.dbv.IEntity}
         *
         * @param entity the entity
         */
        @Override
        public void query(IEntity entity) {
            final Cursor cursor = query();
            final EntityMapper mapper = new EntityMapper(select.table);

            ContentUtil.mapEntityFromCursor(mapper, entity, cursor);

            cursor.close();
        }

        /**
         * Query and store the result to an {@link com.bingzer.android.dbv.IEntityList}
         *
         * @param entityList the entity list
         * @param <E> extends IEntity
         */
        @Override
        public <E extends IEntity> void query(IEntityList<E> entityList) {
            final Cursor cursor = query();
            final EntityMapper mapper = new EntityMapper(select.table);

            ContentUtil.mapEntityListFromCursor(mapper, entityList, cursor);

            cursor.close();
        }

        @Override
        public String toString(){
            StringBuilder sql = new StringBuilder();
            sql.append(select.selectString).append(" ");
            sql.append(select.columnString).append(" ");
            sql.append(select.fromString).append(" ");
            // where
            sql.append(" ").append(select.builder);
            // order by
            if(select.orderByString.length() > 0){
                sql.append(" ").append(select.orderByString);
            }
            // Pagination
            sql.append(" LIMIT ").append(rowLimit).append(" OFFSET ").append(getOffset());

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
}
