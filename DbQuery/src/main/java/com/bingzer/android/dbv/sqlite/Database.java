/**
 * Copyright 2013 Ricky Tobing
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

package com.bingzer.android.dbv.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bingzer.android.dbv.Config;
import com.bingzer.android.dbv.IColumn;
import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.ITable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
public class Database implements IDatabase {

    static final String TAG = "DBV.SQLite.Database";

    private final String name;
    private final DbModel dbModel = new DbModel();
    private final List<ITable> tables = new LinkedList<ITable>();

    private int version;
    private IConfig config;
    private SQLiteOpenHelper dbHelper;

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    ////////////////////////////////////////////////

    public Database(String name){
        this.name = name;
        this.config = new Config();  // default config
    }

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    ////////////////////////////////////////////////

    /**
     * Returns the name of this database
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the version of this database
     *
     * @return
     */
    @Override
    public int getVersion() {
        return version;
    }

    /**
     * Returns all tables in this database
     *
     * @return
     */
    @Override
    public List<ITable> getTables() {
        return tables;
    }

    /**
     * Returns the table
     *
     * @param tableName
     * @return
     */
    @Override
    public ITable get(String tableName) {
        String alias = null;
        if(tableName.contains(" ")){
            // split and get alias..
            int index = tableName.indexOf(" ");
            alias = tableName.substring(index).trim();
            tableName = tableName.substring(0, index).trim();
        }

        for(int i = 0; i < tables.size(); i++){
            if(tables.get(i).getName().equalsIgnoreCase(tableName)){
                ITable table = tables.get(i);
                table.setAlias(alias);
                return table;
            }
        }

        // not found
        return null;
    }

    /**
     * Create the database
     *
     * @param version
     * @param builder
     */
    @Override
    public void create(int version, Builder builder) {
        if(!(builder instanceof SQLiteBuilder)) throw new RuntimeException("Use SQLiteBuilder interface");

        this.version = version;

        // tell the builder to create using the meta data
        builder.onCreate(dbModel);

        // assign builder
        if(dbHelper == null){
            dbHelper = createHelper((SQLiteBuilder) builder);
            // -- get all tables..
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            try{
                while(cursor.moveToNext()){
                    String tableName = cursor.getString(0);

                    Table table = new Table(this, db, tableName);
                    tables.add(table);
                }
            }
            finally {
                cursor.close();
            }
        }
    }

    /**
     * Close the database. Free any resources
     */
    @Override
    public void close() {
        if(dbHelper != null){
            dbHelper.close();
        }
    }

    /**
     * Sets the config
     *
     * @param config
     */
    @Override
    public void setConfig(IConfig config) {
        if(config == null)
            throw new IllegalArgumentException("Config cannot be null");
        this.config = config;
    }

    /**
     * Returns config
     *
     * @return
     */
    @Override
    public IConfig getConfig() {
        return config;
    }

    /**
     * DbHelper
     * @return
     */
    SQLiteOpenHelper getHelper(){
        ensureDbHelperIsReady();
        return dbHelper;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The db helper
     *
     * @author Ricky
     */
    private final SQLiteOpenHelper createHelper(final SQLiteBuilder builder){
        return new SQLiteOpenHelper(builder.getContext(), getName(), null, getVersion()) {

            @Override
            public void onOpen(SQLiteDatabase db){
                Log.i(TAG, "SQLiteOpenHelper.open()");

                super.onOpen(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.i(TAG, "Upgrading from " + oldVersion + " to " + newVersion);

                switch (builder.getMigrationMode()){
                    case DropIfExists:
                        dropAllTables(db);
                        onCreate(db);
                        break;
                    case ErrorIfExists:
                        throw new UnsupportedOperationException("ErrorIfExists is not yet implemented. use MigrationMode.DropIfExists");
                    case UpgradeIfExists:
                        throw new UnsupportedOperationException("UpgradeIfExists is not yet implemented. use MigrationMode.DropIfExists");
                }
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.i(TAG, "Creating database for the first time");

                for(TableModel model : dbModel.tableModles){
                    db.execSQL(model.toString());
                }
            }


            private void dropAllTables(SQLiteDatabase db){
                //db.execSQL("delete from sqlite_master where type in ('table', 'index', 'trigger')");
            }
        };
    }

    /**
     * Runs raw sql
     *
     * @param sql
     * @return
     */
    @Override
    public IQuery<Cursor> raw(String sql) {
        return raw(sql, null);
    }

    /**
     * Runs raw sql
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    @Override
    public IQuery<Cursor> raw(final String sql, final String... selectionArgs) {
        ensureDbHelperIsReady();
        IQuery<Cursor> query = new IQuery<Cursor>() {
            @Override
            public Cursor query() {
                return dbHelper.getWritableDatabase().rawQuery(sql, selectionArgs);
            }
        };

        return query;
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    private void ensureDbHelperIsReady(){
        if(dbHelper == null)
            throw new IllegalArgumentException("You must call create() first");
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    static class TableModel implements ITable.Model {

        private final String tableName;
        private final List<ColumnModel> columnModels = new LinkedList<ColumnModel>();

        TableModel(String tableName){
            this.tableName = tableName;
        }

        /**
         * Returns the name of this table
         *
         * @return
         */
        @Override
        public String getName() {
            return tableName;
        }

        /**
         * Adds a column
         *
         * @param columnName
         * @param dataType
         * @return
         */
        @Override
        public ITable.Model add(String columnName, String dataType) {
            return add(columnName, dataType, null);
        }

        /**
         * Adds a column
         *
         * @param columnName
         * @param dataType
         * @param columnDefinition
         * @return
         */
        @Override
        public ITable.Model add(String columnName, String dataType, String columnDefinition) {
            ColumnModel model = new ColumnModel(columnName, dataType, columnDefinition);
            addModel(model);

            return this;
        }

        /**
         * Convenient way to adding an Id column
         *
         * @param columnName
         * @return
         */
        @Override
        public ITable.Model addColumnId(String columnName) {
            return add(columnName, "INTEGER", "primary key autoincrement not null");
        }

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();

            builder.append("CREATE TABLE ").append(tableName).append("(");
            // ----- columns
            for (int i = 0; i < columnModels.size(); i++) {
                ColumnModel col = columnModels.get(i);
                builder.append(col);

                if (i < columnModels.size() - 1)
                    builder.append(",");
            }
            builder.append(")");

            return builder.toString();
        }

        private void addModel(ColumnModel model){
            if(!containsModel(model)){
                columnModels.add(model);
            }
        }

        private boolean containsModel(ColumnModel columnModel){
            for(ColumnModel model : columnModels){
                if(model.name.equalsIgnoreCase(columnModel.name))
                    return true;
            }

            return false;
        }
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    static class ColumnModel implements IColumn.Model{
        private String name;
        private String definition;
        private String dataType;

        ColumnModel(String name, String dataType, String definition){
            this.name = name;
            this.dataType = dataType;
            this.definition = definition;
        }

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();
            builder.append(name).append(" ").append(dataType);

            // adds column definition
            if(definition != null){
                builder.append(" ").append(definition);
            }

            return builder.toString();
        }

        /**
         * Returns the name of this column
         *
         * @return
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * Returns the data type
         *
         * @return
         */
        @Override
        public String getDataType() {
            return dataType;
        }

        /**
         * Returns the definition if any
         *
         * @return
         */
        @Override
        public String getDefinition() {
            return definition;
        }
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    static class DbModel implements Modeling {

        final List<Database.TableModel> tableModles = new LinkedList<Database.TableModel>();

        /**
         * Adds a table model
         *
         * @param tableName
         * @return
         */
        @Override
        public ITable.Model add(String tableName) {
            Database.TableModel model = new Database.TableModel(tableName);

            addModel(model);
            return model;
        }

        private void addModel(Database.TableModel model){
            if(!containsModel(model)){
                tableModles.add(model);
            }
        }

        private boolean containsModel(Database.TableModel tableModel){
            for(Database.TableModel model : tableModles){
                if(model.tableName.equalsIgnoreCase(tableModel.tableName))
                    return true;
            }

            return false;
        }
    }
}
