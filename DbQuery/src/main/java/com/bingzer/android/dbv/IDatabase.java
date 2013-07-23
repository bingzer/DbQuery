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

package com.bingzer.android.dbv;

import com.bingzer.android.dbv.queries.RawQueryable;
import com.bingzer.android.dbv.queries.SqlExecutable;

import java.util.List;

/**
 * Represents the database
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
     * @param tableName
     * @return
     */
    ITable get(String tableName);

    /**
     * Create the database
     * @param version
     * @param builder
     */
    void create(int version, Builder builder);

    /**
     * Close the database. Free any resources
     */
    void close();

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////

    /**
     * Interface that 'builds' database
     */
    public static interface Builder {

        /**
         * Returns the mode.
         * @return
         */
        MigrationMode getMigrationMode();

        /**
         * Called when database is about to create.
         * You should define all the table models here
         * @param modeling
         */
        void onCreate(Modeling modeling);

    }


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

}
