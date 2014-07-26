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

import com.bingzer.android.dbv.contracts.RawQueryable;
import com.bingzer.android.dbv.contracts.SqlExecutable;

import java.util.List;

/**
 * Represents a database. {@linkplain IDatabase} provides access
 * to {@link ITable} to achieve common <code>CRUD</code> tasks.
 * <p>
 *     Find a complete <code>Wiki</code> and documentation here:<br/>
 *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
 * </p>
 * <p>
 * To properly gain access to {@link IDatabase} object, you must
 * call {@link #open(int, com.bingzer.android.dbv.IDatabase.Builder)} and
 * provides the database modeling with your own term.
 * <code>
 * <pre>
 * IDatabase db = DbQuery.getDatabase("<database-name>");
 * db.open(dbVersion, new SQLiteBuilder() {
 *       ...
 * }
 * </pre>
 * </code>
 * </p>
 * <p>
 *     To upgrade or downgrade. The <code>Builder</code> should provide the following:
 *     <code>
 *     <pre>
 * public boolean onUpgrade(IDatabase db, int oldVersion, int newVersion);
 * public boolean onDowngrade(IDatabase db, int oldVersion, int newVersion);
 *     </pre>
 *     </code>
 *     <code>newVersion</code> is the current <code>dbVersion</code>.
 * </p>
 * <p>
 * {@link IDatabase} has one ore more tables. To gain access to {@link ITable},
 * use this code snippet:
 * <code>
 * <pre>
 * ITable table = db.get("<table-name>");
 * </pre>
 * </code>
 * </p>
 * <p>
 *     <b>Warning:</b><br/>
 *     <code>DbQuery</code> will <code>assume</code>
 *     that every table will follow a naming convention for
 *     their identifier scheme. By default, "Id" is assigned
 *     automatically. For more information see {@link IConfig}
 * </p>
 *
 * @version 2.0
 * @see ITable
 * @see Modeling
 * @see Builder
 * @see SQLiteBuilder
 * @author Ricky Tobing
 */
public interface IDatabase extends RawQueryable, SqlExecutable {

    /**
     * Returns the name of this database
     * @return the name of this database
     */
    String getName();

    /**
     * Returns the version of this database
     * @return the current version of this database
     */
    int getVersion();

    /**
     * Returns all tables exists in this database.
     * Note: you must first <code>open</code> the database
     *
     * @see #open(int, com.bingzer.android.dbv.IDatabase.Builder)
     * @return all tables exists in the database
     */
    List<ITable> getTables();

    /**
     * Returns the {@code Builder} that was used to build this database.
     * This method requires {@link #open(int, com.bingzer.android.dbv.IDatabase.Builder)}
     * or {@link #open(int, String, com.bingzer.android.dbv.IDatabase.Builder)} to be
     * called first.
     * This will throw an exception if {@code open()} hasn't been called yet.
     *
     * @return the builder (type of {@link Builder})
     */
    Builder getBuilder();

    /**
     * Returns table by its <code>tableName</code>. If the table
     * does not exists, this will returns null.
     * The {@link com.bingzer.android.dbv.ITable} provides access to
     * common CRUD operations.
     * Note: you must first <code>open</code> the database.
     * <p>
     * Example Code
     * <pre><code>
     * IDatabase db = ...
     * ITable table = db.get("table-name");
     * </code></pre>
     * </p>
     *
     * @see com.bingzer.android.dbv.ITable
     * @see #open(int, com.bingzer.android.dbv.IDatabase.Builder)
     * @see #getView(String)
     * @param tableName the table
     * @return null if table does not exists
     */
    ITable get(String tableName);

    /**
     * Returns view by its <code>viewName</code>.
     * If the view does not exists, this will returns null.
     * Note: you must first <code>open</code> the database
     * <p>
     * Example Code
     * <pre><code>
     * IDatabase db = ...
     * IView view = db.getView("view-name");
     * </code></pre>
     * </p>
     *
     * @see com.bingzer.android.dbv.IView
     * @see #open(int, com.bingzer.android.dbv.IDatabase.Builder)
     * @see #get(String)
     * @param viewName the view name
     * @return null if view does nto exists
     */
    IView getView(String viewName);

    /**
     * Open the connection the database. This is one of the main method
     * in order for you to fully access the database and use all API provides
     * by the <code>DbQuery</code> library.
     * <p>
     *     You should only call this once in the entire of you application
     *     lifecycle. After that, to access the database, other code in
     *     your application can simply call {@link DbQuery#getDatabase(String)}
     *     to get the same object as this one
     * </p>
     *
     * @see #close()
     * @param version the version of the database
     * @param builder the builder (You should always use SQLiteBuilder as your builder)
     */
    void open(int version, Builder builder);

    /**
     * Open the connection the database using pre-loaded database.
     * Most of the time, you probably have a .db file in "assets" folder,
     * use this method to open that .db file
     * @param version the version of the database
     * @param dbPath the path where the .db file is
     * @param builder the builder (You should always use SQLiteBuilder as your builder)
     */
    void open(int version, String dbPath, Builder builder);

    /**
     * Close the database. Free any resources.
     * <p>
     *     You should only call this when the whole application is terminated.
     *     Most of the time you don't need to call this at all. After calling
     *     this method you must call {@link #open(int, com.bingzer.android.dbv.IDatabase.Builder)}
     * </p>
     *
     * @see #open(int, com.bingzer.android.dbv.IDatabase.Builder)
     */
    void close();

    /**
     * Returns current configuration object.
     *
     * @see IConfig
     * @return IConfig
     */
    IConfig getConfig();

    /**
     * Begin a transaction. The code inside the
     * {@link Batch#exec(IDatabase)} will be executed when you call
     * {@link com.bingzer.android.dbv.IDatabase.Transaction#commit()}.
     * However, if you choose to do this route you must follow this code for safety.
     * The proper way of calling {@link com.bingzer.android.dbv.IDatabase.Transaction#commit()}
     * as follows:
     * <code>
     * <pre>
     * Transaction transaction = db.begin(new Batch(){
     *      public void exec(IDatabase db){
     *          // do intensive sql here
     *          ...
     *      }
     * });
     *
     * try{
     *      transaction.commit();
     * }
     * catch (Throwable any){
     *      transaction.rollback();
     * }
     * finally{
     *      // must call this
     *      transaction.end();
     * }
     * </pre>
     * </code>
     * <p>
     * Another way of doing transaction to simply call
     * {@link com.bingzer.android.dbv.IDatabase.Transaction#execute()} which
     * will automatically do the following
     * {@linkplain com.bingzer.android.dbv.IDatabase.Transaction#commit()},
     * {@linkplain com.bingzer.android.dbv.IDatabase.Transaction#rollback()} and
     * {@linkplain com.bingzer.android.dbv.IDatabase.Transaction#end()}
     * properly.
     * However, the setback of calling
     * {@link com.bingzer.android.dbv.IDatabase.Transaction#execute()}
     * will prevent you to <code>catch</code> the <code>Exception</code>
     * that may occur during the batch processing
     * </p>
     *
     * @param batch block of code to be executed
     * @return transaction
     *
     * @see Transaction
     * @see Batch
     */
    Transaction begin(Batch batch);

    /**
     * Returns the absolute path where this database file exists
     * Note: you must first <code>open</code> the database
     *
     * @see #open(int, com.bingzer.android.dbv.IDatabase.Builder)
     * @return the absolute path
     */
    String getPath();

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Interface that 'builds' database.
     * This is the basic interface of a <code>Builder</code> object.
     * This object is used in {@link IDatabase#open(int, com.bingzer.android.dbv.IDatabase.Builder)}
     *
     * @see IDatabase#open(int, com.bingzer.android.dbv.IDatabase.Builder)
     */
    @SuppressWarnings("unused")
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
         * <b>Note</b>: there's a delay between code execution when using {@link Modeling} object.
         * The code will be executed when this <code>onModelCreate</code> is returned.
         * You should define all the table models here.
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
     * Modeling that's used to query tables/column definition.
     * Note that there's a delay between code execution when using {@link Modeling} object.
     * The code will be executed when this <code>onModelCreate</code> is returned.
     * This object is used inside the
     * {@link Builder#onModelCreate(IDatabase, com.bingzer.android.dbv.IDatabase.Modeling)}
     * to create the initial model of your database.
     * Sample code:
     * <code>
     * <pre>
     *         ...
     *         public void onModelCreate(IDatabase db, Modeling modeling){
     *             modeling.add("Customers")
     *                      .addPrimaryKey("Id")
     *                      .add("Name", "TEXT", "not null")
     *                      .add("Age", "INTEGER")
     *                      .index("Id")
     *                      .index("Name");
     *             modeling.add("Orders")
     *                      .addPrimaryKey("Id")
     *                      .add("ProductsName", "TEXT", "not null")
     *                      .add("CustomerId", "INTEGER", "not null")
     *                      .index("ProductsName")
     *                      .index("CustomerId");
     *         }
     *         ...
     * </pre>
     * </code>
     *
     * @see Builder#onModelCreate(IDatabase, com.bingzer.android.dbv.IDatabase.Modeling)
     */
    public static interface Modeling {

        /**
         * Adds a table model
         * @param tableName the table name
         * @return ITable.Model (Table's model object)
         */
        ITable.Model add(String tableName);

        /**
         * Adds a view model
         * @param viewName the view name
         * @return IView.Model (View's model object)
         */
        IView.Model addView(String viewName);

    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents a transaction.
     * Transaction can be used for a synchronized call of multiple lines of batch code.
     *
     * @see IDatabase#begin(com.bingzer.android.dbv.IDatabase.Batch)
     * @see Batch
     */
    public static interface Transaction {

        /**
         * Executes and try to commit the transactions.
         * If some exception is thrown you should catch this.
         *
         * @see #execute()
         */
        void commit();

        /**
         * Rollback any transactions. This is useful
         * if any exception is thrown after calling {@link #commit()}.
         * You should place this inside the <code>catch</code> block
         * inside your <code>try-catch</code>
         *
         * @see #commit()
         * @see #execute()
         */
        void rollback();

        /**
         * Ends the transaction.
         * From this point on, all transaction has been set to
         * <code>autoCommit = true</code>
         * If {@link #commit()} was called and successful then,
         * the data has been submitted and stored in db. If rollback was called,
         * then nothing is saved.
         */
        void end();

        /**
         * This method will do the following
         * <code>
         * <pre>
         * try{
         *     transaction.commit();
         *     return true;
         * }
         * catch (Throwable any){
         *     transaction.rollback();
         *     return false;
         * }
         * finally{
         *     transaction.end();
         * }
         * </pre>
         * </code>
         *
         * @return true if the batch is successfully committed, false if rollback
         */
        boolean execute();
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents a block of code. The code inside
     * the {@link #exec(IDatabase)} will be called after calling,
     * {@link com.bingzer.android.dbv.IDatabase.Transaction#commit()} or
     * {@link com.bingzer.android.dbv.IDatabase.Transaction#execute()} only.
     *
     */
    public static interface Batch {

        /**
         * Execute lines of batch.
         * @param database the database that originates this call
         */
        void exec(IDatabase database);
    }

}
