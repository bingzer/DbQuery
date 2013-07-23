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

import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.queries.Selectable;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
class QueryImpl<T> implements IQuery<T> {

    StringBuilder builder;

    QueryImpl(){
        this(null);
    }

    QueryImpl(Object any){
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
    static class SelectImpl extends QueryImpl<Cursor> implements IQuery.Select{

        protected StringBuilder selectString;
        protected StringBuilder columnString;
        protected StringBuilder fromString;
        protected StringBuilder limitString;
        protected StringBuilder orderByString;

        SelectImpl(String tableName){
            this(tableName, false);
        }

        SelectImpl(String tableName, boolean distinct){
            this(tableName, -1, distinct);
        }

        SelectImpl(String tableName, int top, boolean distinct){
            selectString = new StringBuilder();
            columnString = new StringBuilder();
            fromString = new StringBuilder();
            limitString = new StringBuilder();
            orderByString = new StringBuilder();


            if(distinct)
                selectString.append("SELECT DISTINCT ");
            else selectString.append("SELECT ");

            //if(top > 0) selectString = selectString + " TOP " + top;

            columnString.append("*");

            fromString.append("FROM " + tableName);

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
        public IQuery<Cursor> columns(String... columns) {
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
        public IQuery<Cursor> orderBy(String... columns) {
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
        public IQuery<Cursor> orderByDesc(String... columns) {
            orderByString.delete(0, columnString.length());
            if(columns != null){
                orderByString.append("ORDER BY ").append(Util.join(",", columns)).append(" DESC");
            }
            return this;
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
            sql.append(super.builder);
            // limit
            if(limitString.length() > 0){
                sql.append(limitString);
            }
            // order by
            if(orderByString.length() > 0){
                sql.append(orderByString);
            }

            return sql.toString();
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
            for(int i = 0; i < values.length; i++){
                if(values[i] == null){
                    contentValues.putNull(columnNames[i]);
                }
                else{
                    contentValues.put(columnNames[i], values[i].toString());
                }
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

    static class InnerJoinImpl extends Join implements IQuery.InnerJoin {
        InnerJoinImpl(Table table, String tableNameToJoin, String onClause) {
            super(table, "INNER JOIN", tableNameToJoin, onClause);
        }
    }

    static class OuterJoinImpl extends Join implements IQuery.OuterJoin {
        OuterJoinImpl(Table table, String tableNameToJoin, String onClause) {
            super(table, "OUTER JOIN", tableNameToJoin, onClause);
        }
    }



    private static class Join extends SelectImpl implements Selectable {

        protected final Table table;
        protected StringBuilder joinBuilder;

        Join(Table table, String joinType, String tableNameToJoin, String onClause){
            super(table.toString());
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
            // limit
            if(limitString != null){
                sql.append(limitString);
            }
            // order by
            if(orderByString != null){
                sql.append(orderByString);
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
    }
}
