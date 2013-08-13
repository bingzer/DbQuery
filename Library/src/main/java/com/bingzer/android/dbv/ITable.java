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

import com.bingzer.android.dbv.queries.Countable;
import com.bingzer.android.dbv.queries.Deletable;
import com.bingzer.android.dbv.queries.Droppable;
import com.bingzer.android.dbv.queries.Function;
import com.bingzer.android.dbv.queries.SelectIdentifiable;
import com.bingzer.android.dbv.queries.Insertable;
import com.bingzer.android.dbv.queries.Joinable;
import com.bingzer.android.dbv.queries.RawQueryable;
import com.bingzer.android.dbv.queries.Selectable;
import com.bingzer.android.dbv.queries.Updatable;

import java.util.List;

/**
 * Represents a table. <code>ITable</code> provides full access to achieve
 * common <code>CRUD</code> tasks.
 * <p>
 *     <b>Warning:</b><br/>
 *     <code>DbQuery</code> will <code>assume</code>
 *     that every table will follow a naming convention for
 *     their identifier scheme. By default, "Id" is assigned
 *     automatically. For more information see {@link IConfig}
 * </p>
 *
 * @version 1.0
 * @see IDatabase
 * @author Ricky Tobing
 */
public interface ITable extends
        Selectable, Insertable, Deletable, Updatable,
        Joinable.Inner, Joinable.Outer,
        RawQueryable, Countable, Droppable,
        SelectIdentifiable, Function {

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
     * Returns the column name
     * @return the list of columns
     */
    List<String> getColumns();

    /**
     * Returns the column count
     * @return the column count
     */
    int getColumnCount();

    /////////////////////////////////////////////////
    /////////////////////////////////////////////////
    /////////////////////////////////////////////////

    /**
     * Check to see if this table has row add the specified condition
     * @param condition any condition
     * @return true if it returns any row false otherwise
     */
    boolean has(String condition);

    /**
     * has row add id
     * @param id id
     * @return true if it returns any row false otherwise
     */
    boolean has(int id);

    /**
     * Check to see if this table has row add the specified clause and condition
     * @param whereClause whereClause
     * @param whereArgs arguments
     * @return true if it returns any row false otherwise
     */
    boolean has(String whereClause, Object... whereArgs);

    /////////////////////////////////////////////////
    /////////////////////////////////////////////////

    /**
     * The model of this table
     */
    public static interface Model {

        /**
         * Returns the name of this table
         * @return the name of this table
         */
        String getName();

        /**
         * Adds a column
         * @param columnName the column name
         * @param dataType data type (i.e: INTEGER, TEXT, BLOB, etc..)
         * @return this
         */
        Model add(String columnName, String dataType);

        /**
         * Adds a column. See SQLite documentation for columnDefinition
         * @param columnName the column name
         * @param dataType data type (i.e: INTEGER, TEXT, BLOB, etc..)
         * @param columnDefinition column definiation (i.e: nullable, primary key, autoincrement, etc...)
         * @return this
         */
        Model add(String columnName, String dataType, String columnDefinition);

        /**
         * Convenient way to adding primary key column
         * @param columnName column name
         * @return this
         */
        Model addPrimaryKey(String columnName);

        /**
         * Create index on the specified column name. The index name will always be
         * <code>[TABLE_NAME]_[COLUMN_NAME]_IDX</code>
         * @param columnName column name
         * @return this
         */
        Model index(String columnName);
    }
}
