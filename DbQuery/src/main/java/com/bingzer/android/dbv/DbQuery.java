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

import com.bingzer.android.dbv.sqlite.Database;

import java.util.LinkedList;
import java.util.List;

/**
 * Db Engine
 */
public class DbQuery {

    private static List<IDatabase> databaseList = new LinkedList<IDatabase>();

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

}
