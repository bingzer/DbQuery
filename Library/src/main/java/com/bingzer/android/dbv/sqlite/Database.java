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

import com.bingzer.android.dbv.IColumn;
import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
public class Database implements IDatabase {

    static final String TAG = "DBV.SQLite.Database";
    static final String SPACE = " ";

    private final String name;
    private final DbModel dbModel = new DbModel();
    private final List<ITable> tables = new LinkedList<ITable>();
    private final IConfig config;

    private int version;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase sqLiteDb;

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public List<ITable> getTables() {
        return tables;
    }

    @Override
    public ITable get(String tableName) {
        String alias = null;
        if(tableName.contains(SPACE)){
            // split and get alias..
            int index = tableName.indexOf(SPACE);
            alias = tableName.substring(index).trim();
            tableName = tableName.substring(0, index).trim();
        }

        for (ITable table : tables) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                table.setAlias(alias);
                return table;
            }
        }

        // not found
        // okay maybe it's just been created..
        try{
            ITable table = new Table(this, sqLiteDb, tableName);
            table.setAlias(alias);
            tables.add(table);
            return table;
        }
        catch (IllegalArgumentException e){
            // okay not found anywhere
            return null;
        }
    }

    @Override
    public void open(int version, Builder builder) {
        if(!(builder instanceof SQLiteBuilder)) throw new RuntimeException("Use SQLiteBuilder interface");

        // assign builder
        if(dbHelper == null || this.version != version){
            close();
            // update version
            this.version = version;
            this.dbHelper = createHelper((SQLiteBuilder) builder);
            this.sqLiteDb = dbHelper.getWritableDatabase();
            Cursor cursor = raw("SELECT name FROM sqlite_master WHERE type='table'").query();
            try{
                tables.clear();
                while(cursor.moveToNext()){
                    String tableName = cursor.getString(0);

                    Table table = new Table(Database.this, sqLiteDb, tableName);
                    tables.add(table);
                }
            }
            finally {
                cursor.close();
                // finally called on ready
                builder.onReady(this);
                // check for foreign key support
                setForeignKeySupport(config.getForeignKeySupport());
            }
        }
    }

    @Override
    public void close() {
        if(dbHelper != null) dbHelper.close();
        if(sqLiteDb != null) sqLiteDb.close();
        // reset
        sqLiteDb = null;
        dbHelper = null;
        dbModel.tableModles.clear();
    }

    @Override
    public IConfig getConfig() {
        return config;
    }

    @Override
    public Transaction begin(Batch batch) {
        return new TransactionImpl(this, batch);
    }

    @Override
    public IQuery<Cursor> raw(String sql) {
        return raw(sql, (String[])null);
    }

    @Override
    public IQuery<Cursor> raw(final String sql, final Object... args) {
        ensureDbHelperIsReady();
        return new IQuery<Cursor>() {
            @Override
            public Cursor query() {
                return sqLiteDb.rawQuery(sql, Util.toStringArray(args));
            }
        };
    }

    @Override
    public void execSql(String sql) {
        ensureDbHelperIsReady();
        sqLiteDb.execSQL(sql);
    }

    @Override
    public void execSql(String sql, Object... args) {
        if(args == null) execSql(sql);
        else{
            ensureDbHelperIsReady();
            sqLiteDb.execSQL(Util.bindArgs(sql, args));
        }
    }

    /**
     * Convenient method to get SQLiteOpenHelper object.
     * {@link IDatabase} must be opened first by calling
     * {@link IDatabase#open(int, com.bingzer.android.dbv.IDatabase.Builder)}
     *
     * @see SQLiteOpenHelper
     * @see IDatabase#open(int, com.bingzer.android.dbv.IDatabase.Builder)
     * @return SQLiteOpenHelper object
     */
    public SQLiteOpenHelper getSQLiteOpenHelper(){
        ensureDbHelperIsReady();
        return dbHelper;
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Database database = (Database) o;

        return name.equals(database.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    final SQLiteOpenHelper createHelper(final SQLiteBuilder builder){
        return new SQLiteOpenHelper(builder.getContext(), getName(), null, getVersion()) {

            @Override
            public void onOpen(SQLiteDatabase db){
                Log.i(TAG, "SQLiteOpenHelper.open()");
                super.onOpen(db);
            }

            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
                Log.i(TAG, "Downgrading from " + oldVersion + " to " + newVersion);
                try{
                    sqLiteDb = db;
                    // if returned true continue to onModelCreate
                    if(builder.onDowngrade(Database.this, oldVersion, newVersion))
                        onCreate(db);
                }
                catch (Throwable e){
                    builder.onError(e);
                }
                finally {
                    sqLiteDb = null;
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.i(TAG, "Upgrading from " + oldVersion + " to " + newVersion);
                try {
                    sqLiteDb = db;
                    // if returned true continue to onModelCreate
                    if(builder.onUpgrade(Database.this, oldVersion, newVersion))
                        onCreate(db);
                }
                catch (Throwable e){
                    builder.onError(e);
                }
                finally {
                    sqLiteDb = null;
                }
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.i(TAG, "Creating database");

                try{
                    sqLiteDb = db;
                    // open the model and execute it
                    builder.onModelCreate(Database.this, dbModel);
                    // execute sql
                    for(TableModel model : dbModel.tableModles){
                        db.execSQL(model.toString());
                    }
                }
                catch (Throwable e){
                    builder.onError(e);
                }
                finally {
                    sqLiteDb = null;
                }
            }

        };
    }

    boolean removeTable(ITable table){
        return tables.remove(table);
    }

    void ensureDbHelperIsReady(){
        if(dbHelper == null || sqLiteDb == null)
            throw new IllegalArgumentException("You must call IDatabase.open() first");
    }

    void begin(){
        ensureDbHelperIsReady();
        sqLiteDb.beginTransaction();
    }

    void commit(){
        ensureDbHelperIsReady();
        sqLiteDb.setTransactionSuccessful();
    }

    void rollback(){
        ensureDbHelperIsReady();
        // absolutely don't do anything
    }

    void end(){
        ensureDbHelperIsReady();
        sqLiteDb.endTransaction();
    }

    void setForeignKeySupport(boolean on){
        execSql("PRAGMA FOREIGN_KEYS = ?", on ? "ON" : "OFF");
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    static class TableModel implements ITable.Model {
        private final String tableName;
        private final List<ColumnModel> columnModels = new LinkedList<ColumnModel>();
        private final List<String> columnIndexNames = new LinkedList<String>();
        private final List<String> foreignKeyModelList = new LinkedList<String>();

        TableModel(String tableName){
            this.tableName = tableName;
        }

        @Override
        public String getName() {
            return tableName;
        }

        @Override
        public ITable.Model add(String columnName, String dataType) {
            return add(columnName, dataType, null);
        }

        @Override
        public ITable.Model add(String columnName, String dataType, String columnDefinition) {
            ColumnModel model = new ColumnModel(columnName, dataType, columnDefinition);
            addModel(model);

            return this;
        }

        @Override
        public ITable.Model addPrimaryKey(String columnName) {
            return addInteger(columnName, "primary key autoincrement not null");
        }

        @Override
        public ITable.Model index(String columnName) {
            if(!columnIndexNames.contains(columnName))
                columnIndexNames.add(columnName);
            return this;
        }

        @Override
        public ITable.Model foreignKey(String columnName, String targetTable, String targetColumn) {
            String sql = "FOREIGN KEY (" + columnName + ") " + "REFERENCES " + targetTable + "(" + targetColumn + ")";
            if(!foreignKeyModelList.contains(sql)){
                foreignKeyModelList.add(sql);
            }

            return this;
        }

        //////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////

        @Override
        public ITable.Model addText(String columnName) {
            return addText(columnName, null);
        }

        @Override
        public ITable.Model addText(String columnName, String columnDefinition) {
            return add(columnName, "TEXT", columnDefinition);
        }

        @Override
        public ITable.Model addInteger(String columnName) {
            return addInteger(columnName, null);
        }

        @Override
        public ITable.Model addInteger(String columnName, String columnDefinition) {
            return add(columnName, "INTEGER", columnDefinition);
        }

        @Override
        public ITable.Model addReal(String columnName) {
            return addReal(columnName, null);
        }

        @Override
        public ITable.Model addReal(String columnName, String columnDefinition) {
            return add(columnName, "REAL", columnDefinition);
        }

        @Override
        public ITable.Model addNumeric(String columnName) {
            return addNumeric(columnName, null);
        }

        @Override
        public ITable.Model addNumeric(String columnName, String columnDefinition) {
            return add(columnName, "NUMERIC", columnDefinition);
        }

        @Override
        public ITable.Model addBlob(String columnName) {
            return addBlob(columnName, null);
        }

        @Override
        public ITable.Model addBlob(String columnName, String columnDefinition) {
            return add(columnName, "BLOB", columnDefinition);
        }

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();

            // -------------- create table
            builder.append("CREATE TABLE ").append(tableName).append("(");
            // ----- columns
            for (int i = 0; i < columnModels.size(); i++) {
                ColumnModel col = columnModels.get(i);
                builder.append(col);

                if (i < columnModels.size() - 1)
                    builder.append(",");
            }
            // ----- foreign keys..
            if(foreignKeyModelList.size() > 0){
                builder.append(",");  // add comma
                for(int i = 0; i < foreignKeyModelList.size(); i++){
                    String foreignKeyString = foreignKeyModelList.get(i);
                    builder.append(foreignKeyString);

                    if(i < foreignKeyModelList.size() - 1)
                        builder.append(",");
                }
            }
            builder.append(");");
            // -------------- create indices if any
            if(columnIndexNames.size() > 0){
                builder.append("\n");
                for(String columnIndexName : columnIndexNames){
                    builder.append("\nCREATE INDEX ")
                            .append(generateIndexName(columnIndexName))
                            .append(" ON ")
                            .append(getName()).append(" (").append(columnIndexName).append(") ");
                    builder.append(";");
                }
            }

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

        private String generateIndexName(String columnName){
            return getName() + "_" + columnName + "_idx";
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

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDataType() {
            return dataType;
        }

        @Override
        public String getDefinition() {
            return definition;
        }
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    static class DbModel implements Modeling {

        final List<Database.TableModel> tableModles = new LinkedList<Database.TableModel>();

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
