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

import com.bingzer.android.dbv.internal.Database;

import java.util.LinkedList;
import java.util.List;

/**
 * DbQuery provides access to {@link IDatabase}. To use <code>DbQuery</code>,
 * you must follow special conventions when it comes to "Id" as primary key
 * in every table in your database.
 * <p>
 *     <b>Warning:</b><br/>
 *     <code>DbQuery</code> will <code>assume</code>
 *     that every table will follow a naming convention for
 *     their identifier scheme. By default, "Id" is assigned
 *     automatically. For more information see {@link IConfig}
 * </p>
 * <p>
 * Sample Code:
 * <pre>
 *   <code>
 *       IDatabase db = DbQuery.getDatabase("<database-name>");
 *       ...
 *   </code>
 * </pre>
 * </p>
 * Complete documentation and Wiki:
 * <a href="http://github.com/bingzer/DbQuery/wiki">http://github.com/bingzer/DbQuery/wiki</a>
 *
 * @see IConfig
 * @see IDatabase
 * @author Ricky Tobing
 */
public final class DbQuery {

    private static List<IDatabase> databaseList = new LinkedList<IDatabase>();

    /**
     * Returns the {@link IDatabase} object with the specified name.
     * If <code>databaseName</code> is not found it will return
     * <code>null</code>
     *
     * @param databaseName the database
     * @return {@link IDatabase} object
     */
    public static IDatabase getDatabase(String databaseName){
        for(IDatabase db : databaseList){
            if(db.getName().equalsIgnoreCase(databaseName)){
                return db;
            }
        }

        // else...
        IDatabase db = new Database(databaseName);
        databaseList.add(db);

        return getDatabase(databaseName);
    }


    /////////////////////////////////////////////////////////////////////////////////
    private DbQuery(){
        // nothing
    }
}
