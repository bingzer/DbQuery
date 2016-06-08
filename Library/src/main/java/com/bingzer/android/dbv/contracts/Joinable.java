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

package com.bingzer.android.dbv.contracts;

import com.bingzer.android.dbv.queries.InnerJoin;
import com.bingzer.android.dbv.queries.LeftJoin;
import com.bingzer.android.dbv.queries.OuterJoin;

/**
 * Created by Ricky Tobing on 7/17/13.
 */
public interface Joinable {

    /**
     * Inner-Join
     */
    public static interface Inner extends Joinable{

        /**
         * Inner join a table
         * @param tableName table name to join
         * @param onClause the on clause
         * @return {@link com.bingzer.android.dbv.queries.InnerJoin}
         */
        InnerJoin join(String tableName, String onClause);

        /**
         * Inner join a table
         * @param tableName table name to join
         * @param column1 first column
         * @param column2 second column
         * @return {@link com.bingzer.android.dbv.queries.InnerJoin}
         */
        InnerJoin join(String tableName, String column1, String column2);
    }

    public static interface Left extends Joinable{

        /**
         * Left join a table
         * @param tableName table name to join
         * @param onClause the on clause
         * @return {@link com.bingzer.android.dbv.queries.LeftJoin}
         */
        LeftJoin leftJoin(String tableName, String onClause);

        /**
         * Left join a table
         * @param tableName table name to join
         * @param column1 first column
         * @param column2 second column
         * @return {@link com.bingzer.android.dbv.queries.LeftJoin}
         */
        LeftJoin leftJoin(String tableName, String column1, String column2);
    }

    /**
     * Outer-Join
     */
    public static interface Outer extends Joinable{

        /**
         * Outer join a table
         * @param tableName table name to join
         * @param onClause the on clause
         * @return {@link com.bingzer.android.dbv.queries.OuterJoin}
         */
        OuterJoin outerJoin(String tableName, String onClause);

        /**
         * Inner join a table
         * @param tableName table name to join
         * @param column1 first column
         * @param column2 second column
         * @return {@link com.bingzer.android.dbv.queries.OuterJoin}
         */
        OuterJoin outerJoin(String tableName, String column1, String column2);
    }
}