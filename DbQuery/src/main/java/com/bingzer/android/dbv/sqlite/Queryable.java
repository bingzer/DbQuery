package com.bingzer.android.dbv.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.Util;

import java.util.regex.Pattern;

/**
 * Created by 11856 on 7/16/13.
 */
class Queryable<T> implements IQuery<T> {

    private StringBuilder builder;

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
         * @param column
         * @return
         */
        @Override
        public IQuery<Cursor> orderBy(String column) {
            throw new UnsupportedOperationException("Not implemented");
        }

        /**
         * To String
         * @return
         */
        public String toString(){
            return super.builder.toString();
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

}
