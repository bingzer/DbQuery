/**
 * Copyright 2014 Ricky Tobing
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
package com.bingzer.android.dbv.queries;

import android.database.Cursor;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.contracts.ColumnSelectable;
import com.bingzer.android.dbv.contracts.CursorEnumerable;
import com.bingzer.android.dbv.contracts.EntitySelectable;

/**
 * Represents a paging and select statement
 * <p>
 *     Find a complete <code>Wiki</code> and documentation here:<br>
 *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
 * </p>
 */
public interface Paging extends IQuery<Cursor>, EntitySelectable, CursorEnumerable {

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
     * Go to the next page
     * @return this
     */
    Paging next();

    /**
     * Go to the previous page.
     * If the previous page is less than 0. It will return page 0 (first page)
     * @return this
     */
    Paging previous();

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

    /**
     * Query and store the result to an {@link com.bingzer.android.dbv.IEntityList}.
     * Equivalent to calling
     * <pre>
     *     <code>
     *   IPaging paging = ...
     *   IEntityList list = ...
     *
     *   paging.setPageNumber(pageNumber);
     *   paging.query(list);
     *     </code>
     * </pre>
     * @see com.bingzer.android.dbv.IEntityList
     * @param pageNumber the page number to set
     * @param entityList IEntityList
     * @param <E> IEntity
     */
    <E extends IEntity> void query(int pageNumber, IEntityList<E> entityList);
}
