/**
 * Copyright 2014 Ricky Tobing
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
package com.bingzer.android.dbv.content.contracts;

import android.database.Cursor;

import com.bingzer.android.dbv.contracts.EntitySelectable;
import com.bingzer.android.dbv.contracts.Pagination;
import com.bingzer.android.dbv.queries.IQuery;

/**
 * Created by Ricky on 8/22/13.
 */
public interface Selectable {

    /**
     * Select top (x) add the specified condition
     * @param top number to return
     * @param condition the condition
     * @return {@link com.bingzer.android.dbv.queries.Select}
     */
    Select select(int top, String condition);

    /**
     * Select some condition
     * @param condition the condition
     * @return {@link com.bingzer.android.dbv.queries.Select}
     */
    Select select(String condition);

    /**
     * Select id. Id column must be defined in the naming convention
     * specified in {@link com.bingzer.android.dbv.IConfig}
     * @param id id to search
     * @see com.bingzer.android.dbv.IConfig#setIdNamingConvention(String)
     * @return {@link com.bingzer.android.dbv.queries.Select}
     */
    Select select(long id);

    /**
     * Select multiple ids
     * @param ids array id
     * @return {@link com.bingzer.android.dbv.queries.Select}
     */
    Select select(long... ids);

    /**
     * Select along with whereClause
     * @param whereClause 'where' clause
     * @param args arguments
     * @return {@link com.bingzer.android.dbv.queries.Select}
     */
    Select select(String whereClause, Object... args);

    /**
     * Select
     * @param whereClause 'where' clause
     * @param args arguments
     * @return {@link com.bingzer.android.dbv.queries.Select}
     */
    Select select(int top, String whereClause, Object... args);

    /**
     * For select statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     */
    public static interface Select extends IQuery<Cursor>, EntitySelectable, Pagination {

        /**
         * Specified the column to return.
         * default or null will produce SELECT * FROM
         * @param columns column names
         * @return {@link com.bingzer.android.dbv.queries.Select}
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
         * @return {@link com.bingzer.android.dbv.queries.Select}
         */
        OrderBy orderBy(String... columns);

        /**
         * Order By
         */
        public static interface OrderBy extends IQuery<Cursor>, EntitySelectable, Pagination {

        }

    }
}
