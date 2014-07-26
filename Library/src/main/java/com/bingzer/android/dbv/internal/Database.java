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

package com.bingzer.android.dbv.internal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.ITable;
import com.bingzer.android.dbv.IView;
import com.bingzer.android.dbv.SQLiteBuilder;
import com.bingzer.android.dbv.internal.queries.TransactionImpl;
import com.bingzer.android.dbv.queries.IQuery;
import com.bingzer.android.dbv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
public class Database implements IDatabase {
    public static final String SPACE = " ";
    private static final String TAG = "DBV.SQLite.Database";

    private final String name;
    private final DbModel dbModel = new DbModel();
    private final List<ITable> tables = new ArrayList<ITable>();
    private final List<IView> views = new ArrayList<IView>();
    private final IConfig config;

    private int version;
    private DbOpenHelper dbHelper;
    protected SQLiteDatabase sqLiteDb;

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
    public Builder getBuilder() {
        ensureDbHelperIsReady();
        return dbHelper.builder;
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
            ITable table = new Table(this, tableName);
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
    public IView getView(String viewName) {
        String alias = null;
        if(viewName.contains(SPACE)){
            // split and get alias..
            int index = viewName.indexOf(SPACE);
            alias = viewName.substring(index).trim();
            viewName = viewName.substring(0, index).trim();
        }

        for (IView view : views) {
            if (view.getName().equalsIgnoreCase(viewName)) {
                view.setAlias(alias);
                return view;
            }
        }

        // not found
        // okay maybe it's just been created..
        try{
            IView view = new View(this, viewName);
            view.setAlias(alias);
            views.add(view);
            return view;
        }
        catch (IllegalArgumentException e){
            // okay not found anywhere
            return null;
        }
    }

    @Override
    public void open(int version, Builder builder) {
        open(version, null, builder);
    }

    @Override
    public void open(int version, String dbPath, Builder builder) {
        if(!(builder instanceof SQLiteBuilder)) throw new RuntimeException("Use SQLiteBuilder interface");

        // assign builder
        if(dbHelper == null || this.version != version){
            close();
            // update version
            this.version = version;
            this.dbHelper = new DbOpenHelper(this, dbPath, (SQLiteBuilder) builder);
            this.sqLiteDb = dbHelper.getSQLiteDatabase();
            Cursor cursor = raw("SELECT name FROM sqlite_master WHERE type='table'").query();
            try{
                tables.clear();
                while(cursor.moveToNext()){
                    String tableName = cursor.getString(0);

                    Table table = new Table(Database.this, tableName);
                    tables.add(table);
                }
            }
            finally {
                cursor.close();
                // check for foreign key support
                setForeignKeySupport(config.getForeignKeySupport());
                // finally called on ready
                builder.onReady(this);
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
        // clear models
        dbModel.tableModels.clear();
        dbModel.viewModels.clear();
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
    public String getPath() {
        return getSQLiteDatabase().getPath();
    }

    @Override
    public IQuery<Cursor> raw(String sql) {
        return raw(sql, (Object)null);
    }

    @Override
    public IQuery<Cursor> raw(final String sql, final Object... args) {
        return new IQuery<Cursor>() {
            @Override
            public Cursor query() {
                return getSQLiteDatabase().rawQuery(sql, Utils.toStringArray(args));
            }
        };
    }

    @Override
    public void execSql(String sql) {
        enforceReadOnly();
        getSQLiteDatabase().execSQL(sql);
    }

    @Override
    public void execSql(String sql, Object... args) {
        enforceReadOnly();

        if(args == null) execSql(sql);
        else{
            getSQLiteDatabase().execSQL(Utils.bindArgs(sql, args));
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

    /**
     * Convenient method to get SQLiteDatabase object
     * {@link IDatabase} must be opened first by calling
     * {@link IDatabase#open(int, com.bingzer.android.dbv.IDatabase.Builder)}
     *
     * @see SQLiteDatabase
     * @see IDatabase#open(int, com.bingzer.android.dbv.IDatabase.Builder)
     * @return SQLiteDatabase object
     */
    public SQLiteDatabase getSQLiteDatabase(){
        ensureDbHelperIsReady();
        return sqLiteDb;
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

    boolean removeTable(ITable table){
        if(table instanceof IView)
            return views.remove(table);
        return tables.remove(table);
    }

    void ensureDbHelperIsReady(){
        if(dbHelper == null || sqLiteDb == null)
            throw new IllegalArgumentException("You must call IDatabase.open() first");
    }

    public void begin(){
        getSQLiteDatabase().beginTransaction();
    }

    public void commit(){
        getSQLiteDatabase().setTransactionSuccessful();
    }

    public void rollback(){
        ensureDbHelperIsReady();
        // absolutely don't do anything
    }

    public void end(){
        ensureDbHelperIsReady();
        getSQLiteDatabase().endTransaction();
    }

    void setForeignKeySupport(boolean on){
        if(on)
            getSQLiteDatabase().execSQL("PRAGMA FOREIGN_KEYS = ON");
        else
            getSQLiteDatabase().execSQL("PRAGMA FOREIGN_KEYS = OFF");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    static class TableModel implements ITable.Model {
        private final String tableName;
        private final List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
        private final List<String> columnIndexNames = new ArrayList<String>();
        private final List<String> foreignKeyModelList = new ArrayList<String>();
        private boolean ifNotExists;

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
        public ITable.Model index(String... columnNames) {
            if(columnNames != null){
                for(String columnName : columnNames){
                    if(!columnIndexNames.contains(columnName))
                        columnIndexNames.add(columnName);
                }
            }
            return this;
        }

        @Override
        public ITable.Model ifNotExists() {
            ifNotExists = true;
            return this;
        }

        @Override
        public ITable.Model foreignKey(String columnName, String targetColumn) {
            return foreignKey(columnName, targetColumn, null);
        }

        @Override
        public ITable.Model foreignKey(String columnName, String targetColumn, String actionClause) {
            if(!targetColumn.contains("."))
                throw new IllegalArgumentException(targetColumn + " is not an properly formatted. Must use format: [TABLE].[COLUMN] for targetColumn");

            String targetTable = targetColumn.substring(0, targetColumn.indexOf("."));
            targetColumn = targetColumn.substring(targetColumn.indexOf(".") + 1, targetColumn.length());
            return foreignKey(columnName, targetTable, targetColumn, actionClause);
        }

        @Override
        public ITable.Model foreignKey(String columnName, String targetTable, String targetColumn, String actionClause) {
            String sql = "FOREIGN KEY (" + columnName + ") " + "REFERENCES " + targetTable + "(" + targetColumn + ")";

            if (actionClause != null){
                sql += " " + actionClause;
            }

            if (!foreignKeyModelList.contains(sql)){
                foreignKeyModelList.add(sql);
            }

            return this;
        }

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
            builder.append("CREATE TABLE").append(ifNotExists ? " IF NOT EXISTS " : Database.SPACE);
            builder.append(tableName).append("(");
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

    static class ViewModel implements IView.Model, IView.Statement {

        private final String name;
        private final StringBuilder selectBuilder;
        private boolean ifNotExists = false;

        ViewModel(String viewName){
            this.name = viewName;
            this.selectBuilder = new StringBuilder();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public IView.Statement as(String selectSql) {
            selectBuilder.append(selectSql);
            return this;
        }

        @Override
        public IView.Model ifNotExists() {
            ifNotExists = true;
            return this;
        }

        @Override
        public IView.Statement append(String sql) {
            selectBuilder.append(SPACE).append(sql);
            return this;
        }

        @Override
        public String toString(){
            return "CREATE VIEW" + (ifNotExists ? " IF NOT EXISTS " : Database.SPACE) + name + " AS " + selectBuilder;
        }
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    static class ColumnModel {
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
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    static class DbModel implements Modeling {

        final List<Database.TableModel> tableModels = new ArrayList<TableModel>();
        final List<Database.ViewModel> viewModels = new ArrayList<ViewModel>();

        @Override
        public ITable.Model add(String tableName) {
            TableModel model = new TableModel(tableName);

            if(!containsModel(model)){
                tableModels.add(model);
            }
            return model;
        }

        @Override
        public IView.Model addView(String viewName) {
            ViewModel model = new ViewModel(viewName);

            if(!containsViewModel(model)){
                viewModels.add(model);
            }
            return model;
        }

        private boolean containsModel(TableModel tableModel){
            for(TableModel model : tableModels){
                if(model.tableName.equalsIgnoreCase(tableModel.tableName))
                    return true;
            }

            return false;
        }

        private boolean containsViewModel(ViewModel viewModel){
            for(ViewModel model : viewModels){
                if(model.name.equalsIgnoreCase(viewModel.name))
                    return true;
            }

            return false;
        }
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    static class DbOpenHelper extends SQLiteOpenHelper {
        SQLiteBuilder builder;
        Database database;
        String dbPath;
        SQLiteDatabase preloadedSQLiteDb;

        DbOpenHelper(Database database, String dbPath, SQLiteBuilder builder){
            super(builder.getContext(), database.getName(), null, database.getVersion());
            this.database = database;
            this.dbPath = dbPath;
            this.builder = builder;

            if(dbPath != null){
                try{
                    preloadedSQLiteDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

                    onOpen(preloadedSQLiteDb);
                    // check version..
                    if(preloadedSQLiteDb.getVersion() == database.getVersion())
                        // do nothing
                        Log.i(TAG, "Same version. Do nothing");
                    // upgrade
                    else if(preloadedSQLiteDb.getVersion() < database.getVersion())
                        onUpgrade(preloadedSQLiteDb, preloadedSQLiteDb.getVersion(), database.getVersion());
                    // downgrade
                    else
                        onDowngrade(preloadedSQLiteDb, preloadedSQLiteDb.getVersion(), database.getVersion());
                }
                catch (Exception e){
                    builder.onError(e);
                }
            }
        }

        SQLiteDatabase getSQLiteDatabase() {
            // if using pre-loaded db
            if(dbPath != null) return preloadedSQLiteDb;

            // otherwise check weather it's readonly or not
            if(database.getConfig().isReadOnly()) return getReadableDatabase();
            // finally returns the readwrite database
            return getWritableDatabase();
        }

        @Override
        public void onOpen(SQLiteDatabase db){
            Log.i(TAG, "SQLiteOpenHelper.open()");
            super.onOpen(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.i(TAG, "Downgrading from " + oldVersion + " to " + newVersion);
            try{
                database.sqLiteDb = db;
                // if returned true continue to onModelCreate
                if(builder.onDowngrade(database, oldVersion, newVersion))
                    onCreate(db);
            }
            catch (Throwable e){
                builder.onError(e);
            }
            finally {
                database.sqLiteDb = null;
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Upgrading from " + oldVersion + " to " + newVersion);
            try {
                database.sqLiteDb = db;
                // if returned true continue to onModelCreate
                if(builder.onUpgrade(database, oldVersion, newVersion))
                    onCreate(db);
            }
            catch (Throwable e){
                builder.onError(e);
            }
            finally {
                database.sqLiteDb = null;
            }
        }

        @Override
        public void onCreate(final SQLiteDatabase sqlDb) {
            Log.i(TAG, "Creating database");

            final Transaction transaction = database.begin(new Batch() {
                @Override
                public void exec(IDatabase d) {
                    // execute sql ** TABLES
                    for(TableModel model : database.dbModel.tableModels)
                        sqlDb.execSQL(model.toString());
                    // execute sql ** VIEWS
                    for(ViewModel model : database.dbModel.viewModels)
                        sqlDb.execSQL(model.toString());
                }
            });

            try{
                database.sqLiteDb = sqlDb;
                // open the model and execute it
                builder.onModelCreate(database, database.dbModel);
                // exec transaction
                transaction.commit();
            }
            catch (Throwable e){
                transaction.rollback();
                builder.onError(e);
            }
            finally {
                transaction.end();
                database.sqLiteDb = null;
            }
        }

    }

    protected void enforceReadOnly(){
        if(config.isReadOnly())
            throw new IllegalAccessError("Read-Only. Only SELECT statements are allowed.");
    }
}
