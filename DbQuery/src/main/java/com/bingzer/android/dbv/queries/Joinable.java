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

package com.bingzer.android.dbv.queries;

import com.bingzer.android.dbv.IQuery;

/**
 * Created by Ricky Tobing on 7/17/13.
 */
public interface Joinable {

    /**
     * Inner joinable
     */
    public static interface Inner {

        /**
         * Inner join a table
         * @param tableName
         * @param onClause
         * @return
         */
        IQuery.InnerJoin join(String tableName, String onClause);

        /**
         * Inner join a table
         * @param tableName
         * @param column1
         * @param column2
         * @return
         */
        IQuery.InnerJoin join(String tableName, String column1, String column2);
    }

    public static interface Outer {

        /**
         * Joins a table
         * @param tableName
         * @param onClause
         * @return
         */
        IQuery.OuterJoin outerJoin(String tableName, String onClause);

        /**
         * Joins a table
         * @param tableName
         * @param column1
         * @param column2
         * @return
         */
        IQuery.OuterJoin outerJoin(String tableName, String column1, String column2);
    }
}
