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

import java.util.List;

/**
 * Created by Ricky Tobing on 7/16/13.
 */

public interface ITable extends IQueryableTable {

    /**
     * Returns the name
     * @return
     */
    String getName();

    /**
     * Returns the column name
     * @return
     */
    List<String> getColumns();

    /**
     * Returns the column count
     * @return
     */
    int getColumnCount();

    /**
     * The model of this table
     */
    public static interface Model {

        /**
         * Returns the name of this table
         * @return
         */
        String getName();

        /**
         * Adds a column
         * @param columnName
         * @param dataType
         * @return
         */
        Model add(String columnName, String dataType);

        /**
         * Adds a column
         * @param columnName
         * @param dataType
         * @param columnDefinition
         * @return
         */
        Model add(String columnName, String dataType, String columnDefinition);

    }
}
