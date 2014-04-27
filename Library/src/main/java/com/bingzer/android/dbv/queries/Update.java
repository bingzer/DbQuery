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

import android.content.ContentValues;

/**
 * Represents an update statement
 * <p>
 *     Find a complete <code>Wiki</code> and documentation here:<br/>
 *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
 * </p>
 */
public interface Update extends IQuery<Integer> {

    /**
     * Multiple columns
     * @param columns column names
     * @return num updated
     */
    Columns columns(String... columns);

    /**
     * ContentValues
     * @param values content values
     * @return num updated
     */
    IQuery<Integer> val(ContentValues values);

    /**
     * For a single column update
     * @param column the column name
     * @param value the value
     * @return integer
     */
    IQuery<Integer> val(String column, Object value);

    /**
     * For a multiple column update
     * @param columnNames column names
     * @param values values
     * @return integer
     */
    IQuery<Integer> val(String[] columnNames, Object[] values);

    /**
     * The columns
     */
    static interface Columns {
        IQuery<Integer> val(Object... values);
    }
}
