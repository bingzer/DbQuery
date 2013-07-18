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
class Queryable<T> implements IQuery<T> {

    StringBuilder builder;

    Queryable(){
        this(null);
    }

    Queryable(Object any){
        builder = new StringBuilder();
        append(any);
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    Queryable append(Object any){
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
     * Select
     */
    static class Select extends Queryable<Cursor> implements IQuery.Select{

        private CharSequence orderBy;

        Select(){
            this(null);
        }

        Select(Object any){
            super(any);
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
            String all = super.builder.toString();

            if(columns != null){
                super.builder.delete(0, super.builder.length());
                super.builder = new StringBuilder();
                super.builder.append(all.replace("*", Util.join(",", columns)));
            }
            else{
                if(columns == null && all.toLowerCase().contains("select *")){
                    // we're good..
                }
                else if(columns == null && !all.toLowerCase().contains("select *")){
                    super.builder.delete(0, super.builder.length());
                    super.builder = new StringBuilder();

                    Pattern p = Pattern.compile("(SELECT\\s*).*(FROM)\\s");
                    all = p.matcher(all).replaceFirst("SELECT * FROM ");

                    super.builder.append(all.replace("*", Util.join(",", columns)));
                }
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
                orderBy = "ORDER BY " + Util.join(",", columns) + " ASC";
            }
            else{
                orderBy = null;
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
                orderBy = "ORDER BY " + Util.join(",", columns) + " DESC";
            }
            else{
                orderBy = null;
            }
            return this;
        }

        /**
         * To String
         * @return
         */
        public String toString(){
            String str = super.builder.toString();
            if(orderBy != null)
                return str + " " + orderBy;
            return str;
        }
    }


    /**
     * Insert
     */
    static class Insert implements IQuery.Insert {

        IQuery<Integer> query;
        String[] columnNames;

        Insert(IQuery<Integer> query){
            this.query = query;
        }

        Insert(IQueryableAppendable query, String... columnNames){
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
     * Update
     */
    static class Update implements IQuery.Update {

        IQuery<Integer> query;

        Update(IQuery<Integer> query){
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


    static class Delete implements IQuery.Delete {

        IQuery<Integer> query;

        Delete(IQuery<Integer> query){
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

    static class InnerJoin extends Join implements IQuery.InnerJoin {
        InnerJoin(Table table, String tableNameToJoin, String onClause) {
            super(table, "INNER JOIN", tableNameToJoin, onClause);
        }
    }

    static class OuterJoin extends Join implements IQuery.OuterJoin {
        OuterJoin(Table table, String tableNameToJoin, String onClause) {
            super(table, "OUTER JOIN", tableNameToJoin, onClause);
        }
    }



    private static class Join extends Select implements Selectable {

        protected final Table table;
        protected CharSequence joinBuilder;

        Join(Table table, String joinType, String tableNameToJoin, String onClause){
            this.table = table;
            this.joinBuilder = " " + joinType + " " + tableNameToJoin + " " + onClause;
        }

        /**
         * Select top (x) add the specified condition
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
         * Select some condition
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
         * Select add id
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
         * Select add whereClause
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
         * Select
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
         * Select distinct
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
         * Select distinct add condition
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
