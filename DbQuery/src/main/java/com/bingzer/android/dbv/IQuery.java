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