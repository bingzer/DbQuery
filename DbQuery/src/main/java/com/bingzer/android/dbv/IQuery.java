package com.bingzer.android.dbv;

import android.database.Cursor;

/**
 * Created by 11856 on 7/16/13.
 */
public interface IQuery<T> {

    /**
     * Build the sql and return a cursor
     * @return
     */
    T query();

    /**
     * For insert
     */
    public static interface Insert extends IQuery<Integer> {

        /**
         * Values
         * @return
         */
        IQuery<Integer> values(Object... values);
    }

    /**
     * For select statement
     */
    public static interface Select extends IQuery<Cursor> {

        /**
         * Specified the column to return.
         * default or null will produce SELECT * FROM
         * @param columns
         * @return
         */
        IQuery<Cursor> columns(String... columns);

        /**
         * Orders by
         * @param column
         * @return
         */
        IQuery<Cursor> orderBy(String column);
    }


    public static interface Delete extends IQuery<Integer> {

    }

    public static interface Update extends IQuery<Integer> {

    }


}
