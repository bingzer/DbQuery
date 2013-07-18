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

import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.queries.Selectable;

import java.util.regex.Pattern;

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

        private CharSequence orderByString;
        private CharSequence columnString;
        private CharSequence selectString;

        SelectImpl(){
            this(false);
        }

        SelectImpl(boolean distinct){
            this(-1, distinct);
        }

        SelectImpl(int top, boolean distinct){
            if(distinct)
                selectString = "SELECT DISTINCT ";
            else selectString = "SELECT ";

            if(top > 0) selectString = "TOP " + top;

            columnString = "*";
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
            if(columns != null){
                columnString = Util.join(",", columns);
            }
            else{
                columnString = "*";
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
            if(columns != null){
                orderByString = "ORDER BY " + Util.join(",", columns) + " ASC";
            }
            else{
                orderByString = null;
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
            if(columns != null){
                orderByString = "ORDER BY " + Util.join(",", columns) + " DESC";
            }
            else{
                orderByString = null;
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
            // from and where
            sql.append(super.builder);
            // order by
            if(orderByString != null){
                sql.append(orderByString);
            }

            return sql.toString();
        }
    }


    /**
     * InsertImpl
     */
    static class InsertImpl implements IQuery.Insert {

        IQuery<Integer> query;
        String[] columnNames;

        InsertImpl(IQuery<Integer> query){
            this.query = query;
        }

        InsertImpl(IQueryableAppendable query, String... columnNames){
            this.query = query;
            this.columnNames = columnNames;
        }

        /**
         * Build the sql and return a cursor
         *
         * @return
         */
        @Override
        public Integer query() {
            return query.query();
        }

        /**
         * Values
         *
         * @return
         */
        @Override
        public IQuery values(Object... values) {
            ContentValues contentValues = new ContentValues();
            for(int i = 0; i < values.length; i++){
                // todo:
                contentValues.put(columnNames[i], values[i].toString());
            }

            ((IQueryableAppendable) query).onContentValuesSet(contentValues);

            return this;
        }


        public static interface IQueryableAppendable extends IQuery<Integer> {
            void onContentValuesSet(ContentValues contentValues);
        }
    }


    /**
     * UpdateImpl
     */
    static class UpdateImpl implements IQuery.Update {

        IQuery<Integer> query;

        UpdateImpl(IQuery<Integer> query){
            this.query = query;
        }

        /**
         * Build the sql and return a cursor
         *
         * @return
         */
        @Override
        public Integer query() {
            return query.query();
        }
    }


    static class DeleteImpl implements IQuery.Delete {

        IQuery<Integer> query;

        DeleteImpl(IQuery<Integer> query){
            this.query = query;
        }

        /**
         * Build the sql and return a cursor
         *
         * @return
         */
        @Override
        public Integer query() {
            return query.query();
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
        protected CharSequence joinBuilder;

        Join(Table table, String joinType, String tableNameToJoin, String onClause){
            this.table = table;
            this.joinBuilder = " " + joinType + " " + tableNameToJoin + " " + onClause;
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
            clear();
            append(table.select(top, condition));
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
            clear();
            append(table.select(condition));
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
            clear();
            append(table.select(id));
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
            clear();
            append(table.select(ids));
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
            clear();
            append(table.select(whereClause, args));
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
            clear();
            append(table.select(top, whereClause, args));
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
            clear();
            append(table.selectDistinct(condition));
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
            clear();
            append(table.selectDistinct(whereClause, args));
            return this;
        }

        /**
         *
         * @return
         */
        @Override
        public String toString(){
            return super.builder.toString() + " " + this.joinBuilder.toString();
        }

        /**
         * Clear the StringBuilder
         */
        private void clear(){
            super.builder.delete(0, super.builder.length());
        }
    }
}
