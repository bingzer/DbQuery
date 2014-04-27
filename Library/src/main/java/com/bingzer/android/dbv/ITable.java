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

import com.bingzer.android.dbv.contracts.Alterable;
import com.bingzer.android.dbv.contracts.Countable;
import com.bingzer.android.dbv.contracts.Deletable;
import com.bingzer.android.dbv.contracts.Distinguishable;
import com.bingzer.android.dbv.contracts.Droppable;
import com.bingzer.android.dbv.contracts.Function;
import com.bingzer.android.dbv.contracts.SelectIdentifiable;
import com.bingzer.android.dbv.contracts.Insertable;
import com.bingzer.android.dbv.contracts.Joinable;
import com.bingzer.android.dbv.contracts.RawQueryable;
import com.bingzer.android.dbv.contracts.Selectable;
import com.bingzer.android.dbv.contracts.Tangible;
import com.bingzer.android.dbv.contracts.Unionable;
import com.bingzer.android.dbv.contracts.Updatable;

import java.util.List;

/**
 * Represents a table. <code>ITable</code> provides full access to achieve
 * common <code>CRUD</code> tasks.
 * <p>
 *     Find a complete <code>Wiki</code> and documentation here:<br/>
 *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
 * </p>
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
        Selectable, SelectIdentifiable, Distinguishable,
        Insertable, Deletable, Updatable,
        Joinable.Inner, Joinable.Outer,
        RawQueryable, Countable, Tangible,
        Droppable, Function,
        Alterable, Unionable {

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

    /**
     * Returns the Column Id string for this table
     * @return the string
     */
    String getColumnIdName();

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

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
         * Create index on the specified column name. The index name will always be
         * <code>[TABLE_NAME]_[COLUMN_NAME]_IDX</code>
         * <code>
         * <pre>
         *     Model.add("Person")
         *          .addPrimaryKey("Id")         // Column Id
         *          .addText("Name", "not null") // Column Name
         *          .addInteger("Age")           // Column Age
         *          .addText("Address")          // Column Address
         *          .index("Name", "Age")        // Index of Column Name and Age
         * </pre>
         * </code>
         * @param columnNames array of column names in this table to index
         * @return this
         */
        Model index(String... columnNames);

        /**
         * Flags to append "IF NOT EXISTS" before create
         * @return this
         */
        Model ifNotExists();

        /**
         * Foreign key. Create a foreign key references from a column from this current table
         * to another column on another table. Note that when you call this method,
         * the referenced table and column needs to exists.
         * <code>targetColumn</code> must be defined as <code>[TableName].[ColumnName]</code>
         *
         * @param columnName the referencing column name (from this table)
         * @param targetColumn the referenced column name (from the referenced table)
         */
        Model foreignKey(String columnName, String targetColumn);

        /**
         * Foreign key. Create a foreign key references from a column from this current table
         * to another column on another table. Note that when you call this method,
         * the referenced table and column needs to exists.
         *
         * @param columnName the referencing column name (from this table)
         * @param targetTable the referenced table
         * @param targetColumn the referenced column name (from the referenced table)
         */
        Model foreignKey(String columnName, String targetTable, String targetColumn);

        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        /**
         * Convenient way to adding primary key column.
         * Primary key always have
         * <code>primary key autoincrement</code> column definition
         * @param columnName column name
         * @return this
         */
        Model addPrimaryKey(String columnName);

        /**
         * Convenient method to calling
         * <code>add(columnName, "TEXT")</code>
         * @param columnName the column name
         * @return this
         */
        Model addText(String columnName);

        /**
         * Convenient method to calling
         * <code>add(columnName, "TEXT", columnDefinition)</code>
         * @param columnName the column name
         * @param columnDefinition column definition
         * @return this
         */
        Model addText(String columnName, String columnDefinition);

        /**
         * Convenient method to calling
         * <code>add(columnName, "INTEGER")</code>
         * @param columnName the column name
         * @return this
         */
        Model addInteger(String columnName);

        /**
         * Convenient method to calling
         * <code>add(columnName, "INTEGER", columnDefinition)</code>
         * @param columnName the column name
         * @param columnDefinition column definition
         * @return this
         */
        Model addInteger(String columnName, String columnDefinition);

        /**
         * Convenient method to calling
         * <code>add(columnName, "REAL")</code>
         * @param columnName the column name
         * @return this
         */
        Model addReal(String columnName);

        /**
         * Convenient method to calling
         * <code>add(columnName, "REAL", columnDefinition)</code>
         * @param columnName the column name
         * @param columnDefinition column definition
         * @return this
         */
        Model addReal(String columnName, String columnDefinition);

        /**
         * Convenient method to calling
         * <code>add(columnName, "NUMERIC")</code>
         * @param columnName the column name
         * @return this
         */
        Model addNumeric(String columnName);

        /**
         * Convenient method to calling
         * <code>add(columnName, "NUMERIC", columnDefinition)</code>
         * @param columnName the column name
         * @param columnDefinition column definition
         * @return this
         */
        Model addNumeric(String columnName, String columnDefinition);

        /**
         * Convenient method to calling
         * <code>add(columnName, "BLOB")</code>
         * @param columnName the column name
         * @return this
         */
        Model addBlob(String columnName);

        /**
         * Convenient method to calling
         * <code>add(columnName, "BLOB", columnDefinition)</code>
         * @param columnName the column name
         * @param columnDefinition column definition
         * @return this
         */
        Model addBlob(String columnName, String columnDefinition);
    }

    /////////////////////////////////////////////////
    /////////////////////////////////////////////////

    /**
     * Handles all alteration (renaming table, add columns, etc...)
     */
    public static interface Alter {

        /**
         * Rename current table to the <code>newName</code>
         * @param newName new name of this table
         * @return this
         */
        Alter rename(String newName);

        /**
         * Adds a new column to this table.
         * @param columnName new column name
         * @param dataType data type
         * @return this
         */
        Alter addColumn(String columnName, String dataType);

        /**
         * Adds a new column to this table.
         * @param columnName new column name
         * @param dataType data type
         * @param columnDefinition column definition
         * @return this
         */
        Alter addColumn(String columnName, String dataType, String columnDefinition);

        /**
         * Removes a column (Not supported).
         * Will throw {@link UnsupportedOperationException}
         * <b>SQLite does not support this method</b>.
         * @param columnName columnName to remove
         * @return this
         */
        Alter removeColumn(String columnName);

        ////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
    }
}
