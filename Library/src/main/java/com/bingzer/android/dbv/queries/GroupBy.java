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

import com.bingzer.android.dbv.contracts.CursorEnumerable;
import com.bingzer.android.dbv.contracts.EntitySelectable;
import com.bingzer.android.dbv.contracts.Pagination;

/**
 * Represents a group by statement
 * <p>
 *     Find a complete <code>Wiki</code> and documentation here:<br/>
 *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
 * </p>
 *
 * @see Having
 */
public interface GroupBy extends IQuery<Cursor>, EntitySelectable, Pagination, CursorEnumerable {

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
