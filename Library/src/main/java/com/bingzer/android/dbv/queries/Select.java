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

import com.bingzer.android.dbv.contracts.ColumnSelectable;
import com.bingzer.android.dbv.contracts.CursorEnumerable;
import com.bingzer.android.dbv.contracts.EntitySelectable;
import com.bingzer.android.dbv.contracts.Groupable;
import com.bingzer.android.dbv.contracts.Pagination;

/**
 * For select statement
 * <p>
 *     Find a complete <code>Wiki</code> and documentation here:<br>
 *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
 * </p>
 */
public interface Select extends IQuery<Cursor>, EntitySelectable,
                                    CursorEnumerable, ColumnSelectable,
                                    Pagination, Groupable /*,Unionable*/ {

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
     *
     * <pre><code>
     *   db.from("Table").select().orderBy("Id", "Name", "Price DESC");
     * </code>
     * </pre>
     * @param columns column names
     * @return {@link com.bingzer.android.dbv.queries.Select}
     */
    OrderBy orderBy(String... columns);

    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Order By
     */
    public static interface OrderBy extends IQuery<Cursor>, EntitySelectable,
                                                CursorEnumerable, ColumnSelectable,
                                                Pagination, Groupable {

    }

}
