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

import com.bingzer.android.dbv.queries.Droppable;
import com.bingzer.android.dbv.queries.RawQueryable;
import com.bingzer.android.dbv.queries.SqlExecutable;

import java.util.List;

/**
 * Provides access to {@link ITable} to achive common <code>CRUD</code>
 * tasks.
 *
 * To properly gain access to {@link IDatabase} object, you must
 * call {@link #open(int, com.bingzer.android.dbv.IDatabase.Builder)} and
 * provides the database modeling with your own term.
 * <pre>
 *     <code>
 *         ...
 *         IDatabase db = DbQuery.getDatabase("<database-name>");
 *         db.open(dbVersion, new SQLiteBuilder() {
 *             ...
 *         }
 *         ...
 *     </code>
 * </pre>
 *
 * {@link IDatabase} has one ore more tables. To gain access to {@link ITable},
 * use this code snippet:
 * <pre>
 *     <code>
 *         ...
 *         ITable table = db.get("<table-name>");
 *         ...
 *     </code>
 * </pre>
 *
 * @see ITable
 * @see Modeling
 *
 * Created by Ricky Tobing on 7/16/13.
 */
public interface IDatabase extends RawQueryable, SqlExecutable {

    /**
     * Returns the name of this database
     * @return
     */
    String getName();

    /**
     * Returns the version of this database
     * @return
     */
    int getVersion();

    /**
     * Returns all tables in this database
     * @return
     */
    List<ITable> getTables();

    /**
     * Returns the table
     * @param tableName the table
     * @return null if table does not exists
     */
    ITable get(String tableName);

    /**
     * Create the database
     * @param version the version of the database
     * @param builder the builder
     */
    void open(int version, Builder builder);

    /**
     * Close the database. Free any resources
     */
    void close();

    /**
     * Sets the config
     * @param config
     */
    void setConfig(IConfig config);

    /**
     * Returns config
     * @return
     */
    IConfig getConfig();

    /**
     * Begin a transaction. From this point on,
     * all the transaction should be set to
     * <code>autoCommit = false</code>
     * @param batch block batch to be executed
     * @return transaction
     *
     * @see Transaction
     * @see Batch
     */
    Transaction begin(Batch batch);

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Interface that 'builds' database
     */
    public static interface Builder {

        /**
         * Called when upgrade from oldVersion to newVersion
         *
         * @param database the database
         * @param oldVersion the old version
         * @param newVersion the new version
         * @return If true is returned, the code will continue
         *  on {@link #onModelCreate(IDatabase, com.bingzer.android.dbv.IDatabase.Modeling)}
         */
        boolean onUpgrade(IDatabase database, int oldVersion, int newVersion);

        /**
         * Called when downgrading from oldVersion to newVersion
         *
         * @param database the database
         * @param oldVersion the old version
         * @param newVersion the new version
         *
         * @return If true is returned, the code will continue
         *  on {@link #onModelCreate(IDatabase, com.bingzer.android.dbv.IDatabase.Modeling)}
         */
        boolean onDowngrade(IDatabase database, int oldVersion, int newVersion);

        /**
         * Called when database is about to be created for the first time.
         * You should define all the table models here
         *
         * @param database the instance of the database
         * @param modeling the modeling object used to model the database
         */
        void onModelCreate(IDatabase database, Modeling modeling);

        /**
         * Called after everything gets called
         * @param database the instance of the database
         */
        void onReady(IDatabase database);

        /**
         * Called when any error is encountered.
         * @param error the error
         */
        void onError(Throwable error);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Modeling that's used to query tables/column definition
     */
    public static interface Modeling {

        /**
         * Adds a table model
         * @param tableName
         * @return
         */
        ITable.Model add(String tableName);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents a transaction
     */
    public static interface Transaction {

        /**
         * Commit the transactions
         */
        void commit();

        /**
         * Rollback any transactions
         */
        void rollback();

        /**
         * Ends the transaction.
         * From this point on, all transaction has been set to
         * <code>autoCommit = true</code>
         */
        void end();

        /**
         * This method will do the following
         * <code>
         *     <pre>
         *         try{
         *             transaction.commit();
         *             return true;
         *         }
         *         catch (Throwable any){
         *             transaction.rollback();
         *             return false;
         *         }
         *         finally{
         *             transaction.end();
         *         }
         *     </pre>
         * </code>
         *
         * @return true if the batch is successfully committed, false if otherwise
         */
        boolean execute();
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents a batch block.
     *
     */
    public static interface Batch {

        /**
         * Execute lines of batch.
         * @param database
         */
        void exec(IDatabase database);
    }

}
