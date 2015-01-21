/**
 * Copyright 2014 Ricky Tobing
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

import com.bingzer.android.dbv.internal.Database;

import java.util.ArrayList;
import java.util.Collection;

/**
 * {@linkplain com.bingzer.android.dbv.DbQuery} is your gateway to having
 * a fluent way of querying SQL in Android.
 * <p>
 * DbQuery provides access to {@link IDatabase}. To use <code>DbQuery</code>,
 * you must follow special conventions when it comes to "Id" as primary key
 * in every table in your database.
 * </p>
 *
 * <p>
 *     <b>Warning:</b><br/>
 *     <code>DbQuery</code> will <code>assume</code>
 *     that every table will follow a naming convention for
 *     their identifier scheme. By default, "Id" is assigned
 *     automatically. For more information see {@link IConfig}
 * </p>
 *
 * <p>
 * Sample Code:
 * <pre><code>
 * IDatabase db = DbQuery.getDatabase("<database-name>");
 * ...
 * </code></pre>
 * </p>
 *
 * <p>
 * DbQuery is totally open!
 * <ul>
 *   <li>
 *     Complete documentation and Wiki:
 *     <a href="http://github.com/bingzer/DbQuery/wiki">http://github.com/bingzer/DbQuery/wiki</a>
 *   </li>
 *   <li>
 *     Get involved! Pull Requests are welcome
 *     <a href="http://github.com/bingzer/DbQuery">http://github.com/bingzer/DbQuery</a>
 *   </li>
 * </ul>
 * </p>
 *
 * <p>
 *     <b>Important:</b> This entire javadoc is not read-proof. Some may be up to date, others may not
 * </p>
 *
 * @version 2.0
 * @see IConfig
 * @see IDatabase
 * @author Ricky Tobing
 */
public final class DbQuery {

    private static Collection<IDatabase> databaseList = new ArrayList<IDatabase>();

    public static final String Version = "2.0";

    /**
     * Returns the {@link IDatabase} object with the specified name.
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
