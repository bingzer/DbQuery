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

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * You must use this builder
 */
public abstract class OrmBuilder extends SQLiteBuilder implements IOrmBuilder{

    /**
     * The system tables
     */
    private final List<String> internalTables = new ArrayList<String>(Arrays.asList("android_metadata", "sqlite_sequence"));

    /**
     * Adds an internal table.
     * @param tableName table name
     */
    @Override
    public void addInternalTable(String tableName){
        if(!internalTables.contains(tableName)){
            internalTables.add(tableName);
        }
    }

    /**
     * Returns the list of internal tables
     */
    @Override
    public List<String> getInternalTables(){
        return internalTables;
    }

    /**
     * Called after everything gets called
     *
     * @param database the instance of the database
     */
    @Override
    public void onReady(IDatabase database) {
        Environment local = (Environment) Environment.getLocalEnvironment();
        local.setDatabase(database);
        local.setEntityFactory(this);

        // make sure we have every table mapped
        // other than system tables. If not, let's complain
        // this is the only check here. If user creates a table somewhere after onReady()
        // called then this check is useless.
        // So make sure that we check for null when calling
        // ((OrmBuilder)IDatabase.getBuilder()).createEntity(name)
        for(ITable table : database.getTables()){
            if(!getInternalTables().contains(table.getName()) && createEntity(table.getName()) == null)
                throw new RuntimeException("Unmapped user-defined table " + table.getName());
        }

        super.onReady(database);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Convenient class to use if you don't need to model your database at all.
     * This class is really convenient if you have a pre-loaded database
     * <code>
     * <pre>
     * Context context = ...
     * IDatabase db = DbQuery.getDatabase("Test");
     *
     * db.open(version, new SQLiteBuilder.WithoutModeling(context));
     * </pre>
     * </code>
     */
    public static abstract class WithoutModeling extends OrmBuilder {

        private Context context;

        /**
         * Supply the context here.
         * You should always use <code>ApplicationContext</code>
         * here whenever possible
         * @param context the context
         */
        public WithoutModeling(Context context){
            this.context = context;
        }

        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
            // do nothing
        }
    }

    public static final class FactoryWithoutModeling extends WithoutModeling {

        private final IEntityFactory factory;

        /**
         * Supply the context here.
         * You should always use <code>ApplicationContext</code>
         * here whenever possible
         *
         * @param context the context
         * @param factory the IEntityFactory
         */
        public FactoryWithoutModeling(Context context, IEntityFactory factory) {
            super(context);
            this.factory = factory;
        }

        /**
         * Maps each table to an entity
         *
         * @param tableName the table name
         */
        @Override
        public IBaseEntity createEntity(String tableName) {
            return factory.createEntity(tableName);
        }
    }
}
