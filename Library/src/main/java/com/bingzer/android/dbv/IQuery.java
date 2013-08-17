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

package com.bingzer.android.dbv;

import android.database.Cursor;

import com.bingzer.android.dbv.queries.EntitySelectable;
import com.bingzer.android.dbv.queries.Groupable;
import com.bingzer.android.dbv.queries.Joinable;
import com.bingzer.android.dbv.queries.Pagination;
import com.bingzer.android.dbv.queries.Selectable;
import com.bingzer.android.dbv.queries.Unionable;

/**
 * <p>
 *     Find a complete <code>Wiki</code> and documentation here:<br/>
 *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
 * </p>
 * Created by Ricky Tobing on 7/16/13.
 */
public interface IQuery<T> {

    /**
     * Build the sql and return a cursor
     * @return T
     */
    T query();

    //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////

    /**
     * Represents an insert statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     *
     * @see InsertWith
     */
    public static interface Insert extends IQuery<Integer> {
    }

    /**
     * Represents an insert "with" statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     */
    public static interface InsertWith extends IQuery<Integer> {

        /**
         * Values to set
         * @return {@link IQuery}
         */
        IQuery<Integer> val(Object... values);
    }

    //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////

    /**
     * Represents a delete statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     */
    public static interface Delete extends IQuery<Integer> {

    }

    /**
     * Represents an update statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     */
    public static interface Update extends IQuery<Integer> {

    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

    /**
     * For select statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     */
    public static interface Select
            extends IQuery<Cursor>, EntitySelectable, Pagination, Groupable /*,Unionable*/ {

        /**
         * Specified the column to return.
         * default or null will produce SELECT * FROM
         * @param columns column names
         * @return {@link Select}
         */
        Select columns(String... columns);

        /**
         * Order by. To create multiple orderBy ASC or DESC or both,
         * this is possible
         * <code>
         * <pre>
         *   db.get("Table").select().orderBy("Id", "Name", "Price DESC");
         * </pre>
         * </code>
         * @param columns column names
         * @return {@link Select}
         */
        OrderBy orderBy(String... columns);

        /**
         * Order By
         */
        public static interface OrderBy extends IQuery<Cursor>, EntitySelectable, Pagination, Groupable {

        }

    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

    /**
     * Represents an inner join statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     *
     * @see OuterJoin
     */
    public static interface InnerJoin extends
            Joinable, Joinable.Inner, Joinable.Outer, Selectable, Select{

    }

    /**
     * Represents an outer join statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     *
     * @see InnerJoin
     */
    public static interface OuterJoin extends
            Joinable, Joinable.Inner, Joinable.Outer, Selectable, Select{

    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

    /**
     * Represents a union
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     */
    public static interface Union extends IQuery<Cursor>, EntitySelectable{

    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

    /**
     * Represents a group by statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     *
     * @see Having
     */
    public static interface GroupBy extends IQuery<Cursor>, EntitySelectable, Pagination {

        /**
         * Adds a <code>HAVING</code> statement
         * with the specified condition
         * @param condition the condition to set
         * @return Having object
         */
        Having having(String condition);

        /**
         * Adds a <code>HAVING</code> statement
         * with the specified condition
         * @param clause clause
         * @param args args
         * @return Having object
         */
        Having having(String clause, Object... args);

    }

    /**
     * Represents <code>HAVING</code>
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     *
     * @see GroupBy
     */
    public static interface Having extends IQuery<Cursor>, EntitySelectable, Pagination {

    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

    /**
     * Represents a paging and select statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     */
    public static interface Paging extends IQuery<Cursor>, EntitySelectable {

        /**
         * Returns the number of row set in the beginning.
         * This number is final
         * @return the number of row
         */
        int getRowLimit();

        /**
         * Returns the current page number.
         *
         * @return the current page number
         * @see #setPageNumber(int)
         */
        int getPageNumber();

        /**
         * Sets the page number.
         * If the pageNumber is under than zero it will throw an IllegalArgumentException.
         *
         * @param pageNumber the page number to set
         */
        void setPageNumber(int pageNumber);

        /**
         * Returns the number of page available.
         * This method will run SQL <code>"SELECT COUNT(*)"</code> query
         * once called. This is very expensive call, but it's useful
         * if you want to know ahead of time how many pages are available.
         * <p>
         * <b>Warning:</b>
         * This method will throw an <code>UnsupportedException</code>,
         * when using <code>Paging</code> with <code>OrderBy</code>
         * or <code>Having</code>.
         * </p>
         * @return the number of pages available with the given query
         */
        int getTotalPage();

        /**
         * Returns the current cursor.
         * This also append the pageNumber by one
         *
         * @return current cursor
         */
        Cursor query();

        /**
         * Returns the cursor on the <code>pageNumber</code>.
         * If pageNumber is under than zero it will throw an IllegalArgumentException.
         * If pageNumber is not found, cursor will be null.
         * If called, then {@link #getPageNumber()} will return pageNumber.
         *
         * @param pageNumber the number
         * @return cursor
         */
        Cursor query(int pageNumber);
    }

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

}
