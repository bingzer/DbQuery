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

/**
 * Defines a configuration interface.
 * Note that you should always configure your database before opening it.
 * <p>
 * <pre><code>
 * ...
 * IDatabase db = ...
 * // configure first
 * db.getConfig().setForeignKeySupport(true/false);
 * db.getConfig().setIdNamingConvention(..);
 * // open
 * db.open(1, new SQLiteBuilder());
 * </code></pre>
 *
 * <p>
 *     <b>Warning:</b><br>
 *     <code>DbQuery</code> will <code>assume</code>
 *     that every table will follow a naming convention for
 *     their identifier scheme. By default, "Id" is assigned
 *     automatically. For more information see {@link IConfig}
 *
 * <p>
 *     For example, if you call:
 *     <code>db.getConfig().setIdNamingConvention("IDX")</code>
 *     <code>DbQuery</code> will expect <b>every table</b> to have "IDX"
 *     as their "Id" column.
 *     If there's a table or two that doesn't follow this convention,
 *     some methods will not work. Especially, the one that uses "Id"
 *     (i.e: {@link ITable#select(long)} ,{@link ITable#update(android.content.ContentValues, long)},
 *     and many more
 *
 *
 * @author Ricky
 * @version 2.0
 * @see com.bingzer.android.dbv.IDatabase
 */
public interface IConfig {

    /**
     * Sets naming convention for Id
     * @param id naming convention for Id.
     */
    void setIdNamingConvention(String id);

    /**
     * Returns naming convention for id
     * @return the naming convention
     */
    String getIdNamingConvention();

    /**
     * Sets append table name as prefix in front of the "Id".
     * The Id is defined by {@link #setIdNamingConvention(String)}.
     * The default is <code>false</code>.
     * @param appendTableName true to append
     */
    void setAppendTableNameForId(boolean appendTableName);

    /**
     * Returns true if DbQuery should always append the table name
     * in front of the Id.
     * @return true if DbQuery should append table name, else if otherwise
     */
    boolean getAppendTableNameForId();

    /**
     * Turn on/off debug.
     * @param on true to turn on. false to turn off
     */
    void setDebug(boolean on);

    /**
     * Returns true if it's in debug mode
     * @return true if it's in debug mode, false if otherwise
     */
    boolean getDebug();

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// Pre-open configs ///////////////////////////
    //////////// This needs to be called before open() /////////////////////

    /**
     * Turn on/off foreign key support. By default foreign
     * key should be off.
     * <b>Warning</b>
     * This should be called before opening {@link IDatabase}
     * @param on true to turn on, false to turn off
     */
    void setForeignKeySupport(boolean on);

    /**
     * Returns on/off foreign key support
     * @return true if turned one, false otherwise
     */
    boolean getForeignKeySupport();

    /**
     * Set this database to be a read-only database.
     * <b>Warning</b>
     * This should be called before opening {@link IDatabase}
     * @param readOnly true to make the database readonly, false otherwise
     */
    void setReadOnly(boolean readOnly);

    /**
     * True if this database is a read-only database
     * @return true if read-only, false otherwise
     */
    boolean isReadOnly();
}
