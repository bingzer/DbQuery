package com.bingzer.android.dbv.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bingzer.android.dbv.IColumn;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.ITable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 11856 on 7/16/13.
 */
public class Database implements IDatabase {

    static final String TAG = "DBV.SQLite.Database";

    private final String name;
    private final DbModel dbModel = new DbModel();
    private final List<ITable> tables = new LinkedList<ITable>();

    private int version;
    private SQLiteOpenHelper dbHelper;

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    ////////////////////////////////////////////////

    public Database(String name){
        this.name = name;
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
        for(int i = 0; i < tables.size(); i++){
            if(tables.get(i).getName().equalsIgnoreCase(tableName))
                return tables.get(i);
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
        this.version = version;

        if(builder instanceof SQLiteBuilder)
            throw new RuntimeException("Use SQLiteBuilder interface");

        // tell the builder to create using the meta data
        builder.onCreate(dbModel);

        // assign builder
        dbHelper = createHelper((SQLiteBuilder) builder);
        // -- get all tables..
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        while(cursor.moveToNext()){
            int nameIndex = cursor.getColumnIndex("name");
            String tableName = cursor.getString(nameIndex);

            Table table = new Table(db, tableName);
            tables.add(table);
        }
    }

    /**
     * DbHelper
     * @return
     */
    public SQLiteOpenHelper getHelper(){
        if(dbHelper == null)
            throw new IllegalArgumentException("You must call create() first");
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
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.i(TAG, "Upgrading from " + oldVersion + " to " + newVersion);

                switch (builder.getMode()){
                    case DropIfExists:
                        onCreate(db);
                        break;
                    case ErrorIfExists:
                        break;
                    case UpgradeIfExists:
                        break;
                }

                onCreate(db);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.i(TAG, "Creating database for the first time");

                for(TableModel model : dbModel.tableModles){
                    db.execSQL(model.toCreateSql());
                }
            }

            public void open(){
                Object o = getWritableDatabase();
                o = null;
            }
        };
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

        String toCreateSql(){
            StringBuilder builder = new StringBuilder();

            builder.append("CREATE TABLE ").append(tableName).append("(");
            // ----- columns
            for (int i = 0; i < columnModels.size(); i++) {
                ColumnModel col = columnModels.get(i);
                builder.append(col.toCreateSql());

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

        String toCreateSql(){
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
