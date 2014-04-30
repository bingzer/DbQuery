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
package com.bingzer.android.dbv;

import com.bingzer.android.dbv.contracts.Countable;
import com.bingzer.android.dbv.contracts.Distinguishable;
import com.bingzer.android.dbv.contracts.Droppable;
import com.bingzer.android.dbv.contracts.Function;
import com.bingzer.android.dbv.contracts.RawQueryable;
import com.bingzer.android.dbv.contracts.SelectIdentifiable;
import com.bingzer.android.dbv.contracts.Selectable;
import com.bingzer.android.dbv.contracts.Tangible;

/**
 * Represents a <code>View</code>.
 *
 * <p>
 * {@link com.bingzer.android.dbv.IView} object, once created,
 * is automatically cached inside the {@link com.bingzer.android.dbv.IView}
 * So there's no need to reference it outside. Most of the time
 * <code>db.fromView("tableName").<someMethod>()</code> is more preferable to use.
 * </p>
 *
 * <p>
 * {@link com.bingzer.android.dbv.IView} does not store any view structure or
 * any data whatsoever. It only stores its name, alias (if any) and column names.
 * So it's easy on the memory usage.
 * </p>
 *
 * <p>
 * <b>Supported operations:</b>
 * <ul>
 *     <li>{@link com.bingzer.android.dbv.queries.Select Select}</li>
 * </ul>
 * </p>
 *
 * @version 2.0
 * @see com.bingzer.android.dbv.IDatabase
 * @see com.bingzer.android.dbv.queries.Select
 */
public interface IView extends
        Selectable, Distinguishable,
        RawQueryable, Countable, Droppable,
        SelectIdentifiable, Function, Tangible
        // in the future we may support these...
        /*Joinable.Inner, Joinable.Outer,
        Alterable, Unionable  */{

    /**
     * Returns the name of this table
     * @return the name of the table
     */
    String getName();

    /**
     * Sets the current alias of this table
     * @param alias sets the alias (maybe null)
     */
    void setAlias(String alias);

    /**
     * This table alias
     * @return returns the alias (null if none)
     */
    String getAlias();

    /**
     * The model of a <code>View</code>
     */
    public static interface Model {

        /**
         * Returns the name of this view
         * @return the name of this view
         */
        String getName();

        /**
         * Select statement
         * @param selectSql select statement
         * @return a Statement
         */
        Statement as(String selectSql);

        /**
         * Flags to append "IF NOT EXISTS" before create
         * @return this
         */
        Model ifNotExists();

    }

    /**
     * SQL Statement for <code>View</code>
     */
    public static interface Statement extends Model{

        /**
         * Append the following sql
         * @param sql the sql to append
         * @return this
         */
        Statement append(String sql);

    }
}
